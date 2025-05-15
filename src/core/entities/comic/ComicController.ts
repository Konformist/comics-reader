import ComicModel from '@/core/entities/comic/ComicModel.ts';
import server from '@/core/middleware/server.ts';

export default class ComicController {
  static async loadAll (): Promise<ComicModel[]> {
    const result = await server.getComicAll();

    return result.map((e) => new ComicModel(e));
  }

  static async load (id: number): Promise<ComicModel> {
    const result = await server.getComic(id);

    return new ComicModel(result);
  }

  static async save (model: ComicModel): Promise<number | void> {
    return model.id
      ? server.setComic(model.getDTO())
      : server.addComic(model.getDTO());
  }

  static async delete (id: number): Promise<void> {
    return server.delComic(id);
  }

  static saveCover (comicId: number, file: File): Promise<void> {
    return server.addComicCover(comicId, file);
  }

  static deleteCover (comicId: number): Promise<void> {
    return server.delComicCover(comicId);
  }

  static resizeComicCover (
    comicId: number,
    options: {
      maxWidth?: number,
      maxHeight?: number,
    },
  ) {
    return server.resizeComicCover(comicId, options);
  }

  static saveFile (comicId: number, fileId = 0, file: File): Promise<void> {
    return fileId
      ? server.setComicFile(comicId, fileId, file)
      : server.addComicFile(comicId, file);
  }

  static deleteFile (comicId: number, fileId: number): Promise<void> {
    return server.delComicFile(comicId, fileId);
  }

  static deleteFiles (comicId: number): Promise<void> {
    return server.delComicFiles(comicId);
  }

  static resizeComicFile (
    comicId: number,
    fileId: number,
    options: {
      maxWidth?: number,
      maxHeight?: number,
    },
  ) {
    return server.resizeComicFile(comicId, fileId, options);
  }
}
