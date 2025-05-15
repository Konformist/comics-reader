import type { IComicDTO } from '@/core/entities/comic/ComicTypes.ts';
import ServerAbstract from '@/core/middleware/ServerAbstract.ts';
import serverFiles from '@/core/middleware/serverFiles.ts';
import { COMICS_FILES_DIRECTORY, COMICS_STORE } from '@/core/middleware/variables.ts';
import { optimizeImage } from '@/core/utils/image.ts';
import { Directory, Filesystem } from '@capacitor/filesystem';

export interface IResizeOptions {
  maxWidth: number,
  maxHeight: number,
}

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
    await this.setDatabase();

    return newId;
  }

  public async setItem(value: IComicDTO): Promise<void> {
    const index = this.dataRaw.findIndex((e) => e.id === value.id);
    this.dataRaw.splice(index, 1, value);
    await this.setDatabase();
  }

  public async delItem(id: number): Promise<void> {
    const comic = await this.getItem(id);

    if (!comic) return;

    if (comic.image) await this.delCover(id);
    await this.delImages(id);
    this.dataRaw = this.dataRaw.filter((e) => e.id !== id);
    await this.setDatabase();
  }

  #getCoverPath(comicId: number): string {
    return `${COMICS_FILES_DIRECTORY}/${comicId}/cover.webp`;
  }

  public async addCover(comicId: number, file: File): Promise<void> {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    const optimizedFile = await optimizeImage(file);
    comic.image = await serverFiles.addFile(this.#getCoverPath(comicId), optimizedFile, 'binary');
    await this.setDatabase();
  }

  public async delCover(comicId: number): Promise<void> {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    await serverFiles.delFile(this.#getCoverPath(comicId));
    comic.image = '';
    await this.setDatabase();
  }

  public async resizeCover(comicId: number, options: Partial<IResizeOptions>) {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    const result = await Filesystem.getUri({
      path: this.#getCoverPath(comicId),
      directory: Directory.Data,
    });

    comic.image = await serverFiles.resizeImage(result.uri, options);
    await this.setDatabase();
  }

  #getImagePath(comicId: number, fileId: number): string {
    return `${COMICS_FILES_DIRECTORY}/${comicId}/${fileId}.webp`;
  }

  public async addImage(comicId: number, file: File): Promise<void> {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    const fileId = Math.max(...comic.images.map((e) => e.id), 0) + 1;
    const optimizedFile = await optimizeImage(file);
    const uri = await serverFiles.addFile(this.#getImagePath(comicId, fileId), optimizedFile, 'binary');

    comic.images.push({
      id: fileId,
      url: uri,
      from: '',
    });

    await this.setDatabase();
  }

  public async setImage(comicId: number, fileId: number, file: File) {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    const image = comic.images.find((e) => e.id === fileId);

    if (!image) return;

    if (image.url) {
      try {
        await serverFiles.delFile(this.#getImagePath(comicId, fileId));
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
      } catch (_) { /* empty */ }
    }

    const optimizedFile = await optimizeImage(file);
    image.url = await serverFiles.addFile(this.#getImagePath(comicId, fileId), optimizedFile, 'binary');
    await this.setDatabase();
  }

  public async delImage(comicId: number, fileId: number): Promise<void> {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    const file = comic.images.find((e) => e.id === fileId);
    const fileIndex = comic.images.findIndex((e) => e.id === fileId);

    if (!file) return;

    if (file.url) await serverFiles.delFile(this.#getImagePath(comicId, fileId));
    comic.images.splice(fileIndex, 1);
    await this.setDatabase();
  }

  public async resizeImage(comicId: number, fileId: number, options: Partial<IResizeOptions>) {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    const file = comic.images.find((e) => e.id === fileId);

    if (!file) return;

    const result = await Filesystem.getUri({
      path: this.#getImagePath(comicId, fileId),
      directory: Directory.Data,
    });

    file.url = await serverFiles.resizeImage(result.uri, options);
    await this.setDatabase();
  }

  public async delImages(comicId: number): Promise<void> {
    const comic = await this.getItem(comicId);

    if (!comic) return;

    for (const item of comic.images) {
      if (item.url) await serverFiles.delFile(this.#getImagePath(comicId, item.id));
    }

    comic.images = [];
    await this.setDatabase();
  }
}

export default new ComicsServer();
