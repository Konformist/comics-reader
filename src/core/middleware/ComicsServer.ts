import type { IComicDTO } from '@/core/entities/comic/ComicTypes.ts';
import type { IFileDTO } from '@/core/object-value/file/FileTypes.ts';
import { optimizeImage } from '@/core/utils/image.ts';
import { COMICS_FILES_DIRECTORY, COMICS_STORE } from '@/core/middleware/variables.ts';
import ServerAbstract from '@/core/middleware/ServerAbstract.ts';
import FilesServer from '@/core/middleware/FilesServer.ts';

class ComicsServer extends ServerAbstract<IComicDTO> {
  constructor() {
    super(COMICS_STORE);
  }

  public async getItems(): Promise<IComicDTO[]> {
    await this.getDatabase();

    return [...this.dataRaw];
  }

  public async getItem(id: number): Promise<IComicDTO | undefined> {
    const ids = this.dataRaw.map((e) => e.id);

    if (!ids.includes(id)) await this.getDatabase();

    return this.dataRaw.find((e) => e.id === id);
  }

  public async addItem(value: IComicDTO): Promise<number> {
    await this.getDatabase();

    const newId = this.getNewId();
    this.dataRaw.push({ ...value, id: newId });
    await FilesServer.addDirectory(`${COMICS_FILES_DIRECTORY}/${newId}`);
    await this.setDatabase();

    return newId;
  }

  public async setItem(value: IComicDTO): Promise<void> {
    const comic = await this.getItem(value.id);

    if (!comic) return;

    const existsFiles = value.images.map((e) => e.id);
    const filesDelete = comic.images.filter((e) => !existsFiles.includes(e.id));

    for (const file of filesDelete) {
      if (file.fileId) await FilesServer.delItem(file.fileId);
    }

    const index = this.dataRaw.findIndex((e) => e.id === value.id);
    this.dataRaw.splice(index, 1, value);
    await this.setDatabase();
  }

  public async delItem(id: number): Promise<void> {
    const comic = await this.getItem(id);

    if (!comic) return;

    if (comic.image) await this.delCover(id);
    await this.delImages(id);
    await FilesServer.delDirectory(`${COMICS_FILES_DIRECTORY}/${id}`);
    this.dataRaw = this.dataRaw.filter((e) => e.id !== id);
    await this.setDatabase();
  }

  async #setFile(path: string, file: File): Promise<void> {
    const optimizedFile = await optimizeImage(file);
    await FilesServer.setFile(path, optimizedFile);
  }

  #getCoverPath(comicId: number): string {
    return `${COMICS_FILES_DIRECTORY}/${comicId}/cover.webp`;
  }

  public async getCover(comicId: number): Promise<IFileDTO | undefined> {
    const comic = await this.getItem(comicId);

    if (!comic || !comic.image.fileId) return;

    return await FilesServer.getItem(comic.image.fileId);
  }

  public async addCover(comicId: number, file: File): Promise<void> {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    const fileId = comic.image.fileId
      ? comic.image.fileId
      : await FilesServer.addItem({
        id: 0,
        name: 'cover.webp',
        mime: 'image/webp',
        size: 0,
        mdate: 0,
        cdate: 0,
        path: this.#getCoverPath(comicId),
      });
    const cover = await FilesServer.getItem(fileId);

    if (!cover) return;

    let isFileSet = false;

    try {
      await this.#setFile(cover.path, file);
      isFileSet = true;
      const fileStat = await FilesServer.getFileStat(cover.path);
      await FilesServer.setItem({
        ...cover,
        size: fileStat.size,
        cdate: fileStat.cdate,
        mdate: fileStat.mdate,
      });
      comic.image.fileId = fileId;
    } catch (e) {
      if (isFileSet) await FilesServer.delFile(cover.path);
      throw e;
    }

    await this.setDatabase();
  }

  public async delCover(comicId: number): Promise<void> {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    await FilesServer.delItem(comic.image.fileId);
    comic.image.fileId = 0;
    await this.setDatabase();
  }

  #getImagePath(comicId: number, fileId: number): string {
    return `${COMICS_FILES_DIRECTORY}/${comicId}/${fileId}.webp`;
  }

  public async getImage(comicId: number, imageId: number): Promise<IFileDTO | undefined> {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    const image = comic.images.find((e) => e.id === imageId);

    if (!image?.fileId) return;

    return await FilesServer.getItem(image.fileId);
  }

  public async getImages(comicId: number): Promise<IFileDTO[]> {
    const comic = await this.getItem(comicId);

    if (!comic) return [];

    const ret: IFileDTO[] = [];

    for (const image of comic.images) {
      if (image.fileId) {
        const result = await FilesServer.getItem(image.fileId);
        if (result) ret.push(result);
      }
    }

    return ret;
  }

  public async addImage(comicId: number, imageId: number, file: File): Promise<void> {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    const image = comic.images.find((e) => e.id === imageId);

    if (!image) return;

    const fileId = await FilesServer.addItem({
      id: 0,
      name: '',
      mime: '',
      size: 0,
      mdate: 0,
      cdate: 0,
      path: '',
    });

    const pathFile = this.#getImagePath(comicId, image.id);
    let isFileSet = false;

    try {
      await this.#setFile(pathFile, file);
      isFileSet = true;
      const fileStat = await FilesServer.getFileStat(pathFile);
      await FilesServer.setItem({
        id: fileId,
        name: `${image.id}.webp`,
        mime: 'image/webp',
        size: fileStat.size,
        cdate: fileStat.cdate,
        mdate: fileStat.mdate,
        path: pathFile,
      });
      image.fileId = fileId;
    } catch (e) {
      await FilesServer.delItem(fileId);
      if (isFileSet) await FilesServer.delFile(pathFile);
      throw e;
    }

    await this.setDatabase();
  }

  public async setImage(comicId: number, imageId: number, file: File) {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    const image = comic.images.find((e) => e.id === imageId);

    if (!image) return;

    const fileItem = await FilesServer.getItem(image.fileId);

    if (!fileItem) return;

    await this.#setFile(fileItem.path, file);
    const fileStat = await FilesServer.getFileStat(fileItem.path);
    await FilesServer.setItem({
      ...fileItem,
      size: fileStat.size,
      cdate: fileStat.cdate,
      mdate: fileStat.mdate,
    });

    await this.setDatabase();
  }

  public async delImage(comicId: number, imageId: number): Promise<void> {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    const image = comic.images.find((e) => e.id === imageId);

    if (!image) return;

    await FilesServer.delItem(image.fileId);
    comic.images = comic.images.filter((e) => e.fileId !== image.fileId);
    await this.setDatabase();
  }

  public async delImages(comicId: number): Promise<void> {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    for (const item of comic.images) {
      await FilesServer.delItem(item.fileId);
    }

    comic.images = [];
    await this.setDatabase();
  }
}

export default new ComicsServer();
