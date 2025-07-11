import type { IComicOverrideDTO } from '@/plugins/WebApiPlugin.ts';
import Entity from '@/core/entities/Entity.ts';

export default class ComicOverrideModel extends Entity<IComicOverrideDTO> implements IComicOverrideDTO {
  id: number;
  cdate: string;
  mdate: string;
  titleCSS: string;
  annotationCSS: string;
  coverCSS: string;
  authorsCSS: string;
  authorsTextCSS: string;
  languageCSS: string;
  tagsCSS: string;
  tagsTextCSS: string;
  chaptersCSS: string;
  chaptersTitleCSS: string;
  pagesTemplateUrl: string;
  pagesCSS: string;
  pagesImageCSS: string;

  // eslint-disable-next-line complexity
  constructor(dto?: Partial<IComicOverrideDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.cdate = dto?.cdate ?? '';
    this.mdate = dto?.mdate ?? '';
    this.titleCSS = dto?.titleCSS ?? '';
    this.annotationCSS = dto?.annotationCSS ?? '';
    this.coverCSS = dto?.coverCSS ?? '';
    this.languageCSS = dto?.languageCSS ?? '';
    this.tagsCSS = dto?.tagsCSS ?? '';
    this.tagsTextCSS = dto?.tagsTextCSS ?? '';
    this.authorsCSS = dto?.authorsCSS ?? '';
    this.authorsTextCSS = dto?.authorsTextCSS ?? '';
    this.chaptersCSS = dto?.chaptersCSS ?? '';
    this.chaptersTitleCSS = dto?.chaptersTitleCSS ?? '';
    this.pagesTemplateUrl = dto?.pagesTemplateUrl ?? '';
    this.pagesCSS = dto?.pagesCSS ?? '';
    this.pagesImageCSS = dto?.pagesImageCSS ?? '';
  }

  getDTO(): IComicOverrideDTO {
    return {
      id: this.id,
      cdate: this.cdate,
      mdate: this.mdate,
      titleCSS: this.titleCSS,
      annotationCSS: this.annotationCSS,
      coverCSS: this.coverCSS,
      languageCSS: this.languageCSS,
      tagsCSS: this.tagsCSS,
      tagsTextCSS: this.tagsTextCSS,
      authorsCSS: this.authorsCSS,
      authorsTextCSS: this.authorsTextCSS,
      chaptersCSS: this.chaptersCSS,
      chaptersTitleCSS: this.chaptersTitleCSS,
      pagesTemplateUrl: this.pagesTemplateUrl,
      pagesCSS: this.pagesCSS,
      pagesImageCSS: this.pagesImageCSS,
    };
  }
}
