import Api from '@/core/api/Api.ts';
import ComicOverrideModel from '@/core/entities/comic-override/ComicOverrideModel.ts';

export default class ComicOverrideController {
  static async load(comicId: number) {
    const result = await Api.api('comic/override/get', { comicId });
    return new ComicOverrideModel(result);
  }

  static save(value: ComicOverrideModel) {
    return Api.api('comic/override/set', value.getDTO());
  }
}
