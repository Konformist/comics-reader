import Entity from '@/core/entities/Entity.ts';
import type { IComicOverrideDTO } from '@/plugins/WebApiPlugin.ts';

export default class ComicOverrideModel extends Entity<IComicOverrideDTO> implements IComicOverrideDTO {
  id: number;
  titleCSS: string;
  coverCSS: string;
  pagesCSS: string;
  languageCSS: string;
  tagsCSS: string;
  tagsTextCSS: string;
  authorsCSS: string;
  authorsTextCSS: string;

  constructor(dto?: Partial<IComicOverrideDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.titleCSS = dto?.titleCSS ?? '';
    this.coverCSS = dto?.coverCSS ?? '';
    this.pagesCSS = dto?.pagesCSS ?? '';
    this.languageCSS = dto?.languageCSS ?? '';
    this.tagsCSS = dto?.tagsCSS ?? '';
    this.tagsTextCSS = dto?.tagsTextCSS ?? '';
    this.authorsCSS = dto?.authorsCSS ?? '';
    this.authorsTextCSS = dto?.authorsTextCSS ?? '';
  }

  getDTO(): IComicOverrideDTO {
    return {
      id: this.id,
      titleCSS: this.titleCSS,
      coverCSS: this.coverCSS,
      pagesCSS: this.pagesCSS,
      languageCSS: this.languageCSS,
      tagsCSS: this.tagsCSS,
      tagsTextCSS: this.tagsTextCSS,
      authorsCSS: this.authorsCSS,
      authorsTextCSS: this.authorsTextCSS,
    };
  }
}
