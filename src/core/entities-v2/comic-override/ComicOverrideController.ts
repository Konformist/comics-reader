import ComicOverrideModel from '@/core/entities-v2/comic-override/ComicOverrideModel.ts';
import WebApi from '@/plugins/WebApiPlugin.ts';

export default class ComicOverrideController {
  static async load(comicId: number) {
    const { result } = await WebApi.getComicOverride({ comicId });

    return new ComicOverrideModel(result);
  }

  static save(value: ComicOverrideModel) {
    return WebApi.setComicOverride(value.getDTO());
  }
}
