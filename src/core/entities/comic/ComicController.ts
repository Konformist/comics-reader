import type { IComicImageUrl } from '@/core/entities/comic/ComicTypes.ts';
import server from '@/core/middleware/server.ts';
import FileModel from '@/core/object-value/file/FileModel.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';

export default class ComicController {
  static async loadAll(): Promise<ComicModel[]> {
    const result = await server.getComicAll();

    return result.map((e) => new ComicModel(e));
  }

  static async load(id: number): Promise<ComicModel> {
    const result = await server.getComic(id);

    return new ComicModel(result);
  }

  static async save(model: ComicModel): Promise<number | void> {
    return model.id
      ? server.setComic(model.getDTO())
      : server.addComic(model.getDTO());
  }

  static async delete(id: number): Promise<void> {
    return server.delComic(id);
  }

  static async loadCover(comicId: number): Promise<FileModel> {
    const result = await server.getComicCover(comicId);

    return new FileModel(result);
  }

  static saveCover(comicId: number, file: File): Promise<void> {
    return server.addComicCover(comicId, file);
  }

  static deleteCover(comicId: number): Promise<void> {
    return server.delComicCover(comicId);
  }

  static async loadFiles(comicId: number): Promise<FileModel[]> {
    const result = await server.getComicFiles(comicId);

    return result.map((e) => new FileModel(e));
  }

  static async loadFile(comicId: number, imageId: number): Promise<FileModel> {
    const result = await server.getComicFile(comicId, imageId);

    return new FileModel(result);
  }

  static saveFile(comicId: number, image: IComicImageUrl, file: File): Promise<void> {
    return image.fileId
      ? server.setComicFile(comicId, image.id, file)
      : server.addComicFile(comicId, image.id, file);
  }

  static deleteFile(comicId: number, imageId: number): Promise<void> {
    return server.delComicFile(comicId, imageId);
  }

  static deleteFiles(comicId: number): Promise<void> {
    return server.delComicFiles(comicId);
  }
}
