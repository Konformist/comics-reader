import Api from '@/core/api/Api.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';

export default class ComicController {
  static async loadAll() {
    const result = await Api.api('comic/comic/list');
    return result.map((e) => new ComicModel(e));
  }

  static async load(id: number) {
    const result = await Api.api('comic/comic/get', { id });
    return new ComicModel(result);
  }

  static save(value: ComicModel) {
    return value.id
      ? Api.api('comic/comic/set', value.getDTO())
      : Api.api('comic/comic/add', value.getDTO());
  }

  static remove(id: number) {
    return Api.api('comic/comic/del', { id });
  }
}
