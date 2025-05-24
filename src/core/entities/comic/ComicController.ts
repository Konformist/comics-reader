import ComicModel from '@/core/entities/comic/ComicModel.ts';
import WebApi from '@/plugins/WebApiPlugin.ts';

export default class ComicController {
  static async loadAll() {
    const { result } = await WebApi.getComicsAll();

    return result.map((e) => new ComicModel(e));
  }

  static async load(id: number) {
    const { result } = await WebApi.getComic({ id });

    return new ComicModel(result);
  }

  static async save(value: ComicModel) {
    return value.id
      ? WebApi.setComic(value.getDTO())
      : (await WebApi.addComic(value.getDTO())).result;
  }

  static remove(id: number) {
    return WebApi.delComic({ id });
  }
}
