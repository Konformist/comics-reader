import ServerAbstract from '@/core/middleware/ServerAbstract.ts';
import { FILES_STORE } from '@/core/middleware/variables.ts';
import type { IFileDTO, ITreeDirectory, ITreeFile } from '@/core/object-value/file/FileTypes.ts';
import { Directory, Encoding, Filesystem } from '@capacitor/filesystem';

class FilesServer extends ServerAbstract<IFileDTO> {
  constructor() {
    super(FILES_STORE);
  }

  public async getItems(): Promise<IFileDTO[]> {
    await this.getDatabase();

    return [...this.dataRaw];
  }

  public async getItem(id: number): Promise<IFileDTO | undefined> {
    const ids = this.dataRaw.map((e) => e.id);

    if (!ids.includes(id)) await this.getDatabase();

    return this.dataRaw.find((e) => e.id === id);
  }

  async #availablePath(path: string): Promise<boolean> {
    if (!path) return true;
    const result = await this.getItems();
    return result.every((e) => e.path !== path);
  }

  public async addItem(value: IFileDTO): Promise<number> {
    await this.getDatabase();

    if (!(await this.#availablePath(value.path))) {
      throw new Error('Path is exist');
    }

    const newId = this.getNewId();
    this.dataRaw.push({ ...value, id: newId });
    await this.setDatabase();

    return newId;
  }

  public async setItem(value: IFileDTO): Promise<void> {
    const item = await this.getItem(value.id);

    if (!item) return;

    if (item.path !== value.path && !(await this.#availablePath(value.path))) {
      throw new Error('Path is exist');
    }

    const index = this.dataRaw.findIndex((e) => e.id === value.id);
    this.dataRaw.splice(index, 1, value);
    await this.setDatabase();
  }

  public async delItem(id: number): Promise<void> {
    const item = await this.getItem(id);

    if (!item) return;

    if (item.path) await this.delFile(item.path);
    this.dataRaw = this.dataRaw.filter((e) => e.id !== id);

    await this.setDatabase();
  }

  public async getFile(path: string, type: 'string' | 'binary' = 'binary'): Promise<string> {
    const result = await Filesystem.readFile({
      path,
      directory: Directory.Data,
      encoding: type === 'string' ? Encoding.UTF8 : undefined,
    });

    return result.data as string;
  }

  public async getFileStat(path: string): Promise<{ size: number, cdate: number, mdate: number }> {
    const result = await Filesystem.stat({
      path,
      directory: Directory.Data,
    });

    return {
      size: result.size,
      cdate: result.ctime || 0,
      mdate: result.mtime,
    };
  }

  public async setFile(path: string, data: string, type: 'binary' | 'string' = 'binary'): Promise<void> {
    await Filesystem.writeFile({
      path,
      directory: Directory.Data,
      data,
      encoding: type === 'string' ? Encoding.UTF8 : undefined,
      recursive: true,
    });
  }

  public async delFile(path: string): Promise<void> {
    await Filesystem.deleteFile({
      path,
      directory: Directory.Data,
    });
  }

  public async addDirectory(path: string): Promise<void> {
    try {
      await Filesystem.readdir({
        path,
        directory: Directory.Data,
      });
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (_) {
      await Filesystem.mkdir({
        path,
        directory: Directory.Data,
        recursive: true,
      });
    }
  }

  public async delDirectory(path: string): Promise<void> {
    await Filesystem.rmdir({
      path,
      directory: Directory.Data,
      recursive: true,
    });
  }

  private async getTreeChildren(path: string): Promise<Array<ITreeDirectory | ITreeFile>> {
    const ret: Array<ITreeDirectory | ITreeFile> = [];

    const result = await Filesystem.readdir({
      path,
      directory: Directory.Data,
    });

    for (const file of result.files) {
      const fullPath = path ? `${path}/${file.name}` : file.name;

      if (file.type === 'directory') {
        ret.push({
          type: file.type,
          path: fullPath,
          name: file.name,
          children: await this.getTreeChildren(fullPath),
        });
      } else {
        ret.push({
          type: file.type,
          path: fullPath,
          name: file.name,
          size: file.size,
        });
      }
    }

    return ret;
  }

  public async getTree(path: string): Promise<ITreeDirectory[]> {
    return [{
      type: 'directory',
      path,
      name: path,
      children: await this.getTreeChildren(path),
    }];
  }
}

export default new FilesServer();
