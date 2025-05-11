import ComicModel from '@/core/entities/comic/ComicModel.ts';
import server from '@/core/middleware/server.ts';

export default class ComicController {
  static async loadAll () {
    const result = await server.getComicAll();

    return result.map(e => new ComicModel(e));
  }

  static async load (id: number) {
    const result = await server.getComic(id);

    return new ComicModel(result);
  }
}
