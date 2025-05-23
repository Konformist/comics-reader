import type { IParserDTO } from '@/plugins/WebApiPlugin.ts';
import Entity from '@/core/entities/Entity.ts';

export default class ParserModel extends Entity<IParserDTO> implements IParserDTO {
  id: number;
  cdate: number;
  mdate: number;
  name: string;
  siteUrl: string;
  titleCSS: string;
  coverCSS: string;
  pagesCSS: string;
  authorsCSS: string;
  authorsTextCSS: string;
  languageCSS: string;
  tagsCSS: string;
  tagsTextCSS: string;

  constructor(dto?: Partial<IParserDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.cdate = dto?.cdate ?? 0;
    this.mdate = dto?.mdate ?? 0;
    this.name = dto?.name ?? '';
    this.siteUrl = dto?.siteUrl ?? '';
    this.titleCSS = dto?.titleCSS ?? '';
    this.coverCSS = dto?.coverCSS ?? '';
    this.pagesCSS = dto?.pagesCSS ?? '';
    this.authorsCSS = dto?.authorsCSS ?? '';
    this.authorsTextCSS = dto?.authorsTextCSS ?? '';
    this.languageCSS = dto?.languageCSS ?? '';
    this.tagsCSS = dto?.tagsCSS ?? '';
    this.tagsTextCSS = dto?.tagsTextCSS ?? '';
  }

  getDTO(): IParserDTO {
    return {
      id: this.id,
      cdate: this.cdate,
      mdate: this.mdate,
      name: this.name,
      siteUrl: this.siteUrl,
      titleCSS: this.titleCSS,
      coverCSS: this.coverCSS,
      pagesCSS: this.pagesCSS,
      authorsCSS: this.authorsCSS,
      authorsTextCSS: this.authorsTextCSS,
      languageCSS: this.languageCSS,
      tagsCSS: this.tagsCSS,
      tagsTextCSS: this.tagsTextCSS,
    };
  }
}
