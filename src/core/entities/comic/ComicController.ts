import ComicModel from '@/core/entities/comic/ComicModel.ts';
import { makeLinkFromPath } from '@/core/utils/image.ts';
import WebApi from '@/plugins/WebApiPlugin.ts';

export default class ComicController {
  static async loadAll() {
    const { result } = await WebApi.getComicsAll();
    const models: ComicModel[] = [];

    for (const comic of result) {
      if (comic.cover?.file) {
        comic.cover.file.path = await makeLinkFromPath(comic.cover.file.path);
      }

      models.push(new ComicModel(comic));
    }

    return models;
  }

  static async load(id: number) {
    const { result } = await WebApi.getComic({ id });

    if (result.cover?.file) {
      result.cover.file.path = await makeLinkFromPath(result.cover.file.path);
    }

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
