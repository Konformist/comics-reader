import Entity from '@/core/entities/Entity.ts';
import type { IParsedComic, IParserDTO, TParserOverride } from '@/core/entities/parser/ParserTypes.ts';

export default class ParserModel extends Entity<IParserDTO> {
  public id: number = 0;
  public name: string = '';
  public title: string = '';
  public image: string = '';
  public images: string = '';
  public authors: string = '';
  public authorsText: string = '';
  public language: string = '';
  public tags: string = '';
  public tagsText: string = '';

  constructor(dto?: Partial<IParserDTO>) {
    super();

    if (dto) {
      this.id = dto.id ?? 0;
      this.name = dto.name ?? '';
      this.title = dto.title ?? '';
      this.image = dto.image ?? '';
      this.images = dto.images ?? '';
      this.authors = dto.authors ?? '';
      this.authorsText = dto.authorsText ?? '';
      this.language = dto.language ?? '';
      this.tags = dto.tags ?? '';
      this.tagsText = dto.tagsText ?? '';
    }
  }

  cleanHTML(value: string): string {
    return value
      .replaceAll(/<head>(?:.|\n|\t)*?<\/head>/gm, '')
      .replaceAll(/<style.*?>(?:.|\n|\t)*?<\/style>/gm, '')
      .replaceAll(/<script.*?>(?:.|\n|\t)*?<\/script>/gm, '');
  }

  cleanStr(str: string): string {
    return str
      .trim()
      .replaceAll(/\s+/g, ' ');
  }

  getParsedString(data: HTMLElement, item: string) {
    return this.cleanStr(data.querySelector(item)?.textContent || '');
  }

  getParsedArray(data: HTMLElement, item: string, text: string) {
    return [...data.querySelectorAll<HTMLElement>(item)].map((e) => {
      if (text)
        return this.getParsedString(e, text);

      return this.cleanStr(e.textContent || '');
    });
  }

  parseTitle(data: HTMLElement, item?: string) {
    return item || this.title
      ? this.getParsedString(data, item || this.title)
      : '';
  }

  parseImage(data: HTMLElement, item?: string) {
    return item || this.image
      ? data.querySelector<HTMLImageElement>(item || this.image)?.src || ''
      : '';
  }

  parseImages(data: HTMLElement, item?: string) {
    return item || this.images
      ? [...data.querySelectorAll(item || this.images)].map((e) => {
        return e.querySelector<HTMLImageElement>('img')?.src || '';
      })
      : [];
  }

  parseLanguage(data: HTMLElement, item?: string) {
    return item || this.language
      ? this.getParsedString(data, item || this.language)
      : '';
  }

  parseAuthors(data: HTMLElement, item?: string) {
    return item || this.authors
      ? this.getParsedArray(data, item || this.authors, this.authorsText)
      : [];
  }

  parseTags(data: HTMLElement, item?: string) {
    return item || this.tags
      ? this.getParsedArray(data, item || this.tags, this.tagsText)
      : [];
  }

  parse(value: string, override?: TParserOverride): Partial<IParsedComic> {
    const cleaned = this.cleanHTML(value);
    const parser = new DOMParser();
    const result = parser
      .parseFromString(cleaned, 'text/html')
      .body;

    const parsedComic: Partial<IParsedComic> = {};

    const name = this.parseTitle(result, override?.title);
    if (name) parsedComic.name = name;

    const image = this.parseImage(result, override?.image);
    if (image) parsedComic.image = image;

    const images = this.parseImages(result, override?.images);
    if (images) {
      parsedComic.images = images;
    }

    const authors = this.parseAuthors(result, override?.authors);
    if (authors) parsedComic.authors = authors;

    const language = this.parseLanguage(result, override?.language);
    if (language) parsedComic.language = language;

    const tags = this.parseTags(result, override?.tags);
    if (tags) parsedComic.tags = tags;

    return parsedComic;
  }

  getDTO(): IParserDTO {
    return {
      id: this.id,
      name: this.name,
      title: this.title,
      image: this.image,
      images: this.images,
      authors: this.authors,
      authorsText: this.authorsText,
      language: this.language,
      tags: this.tags,
      tagsText: this.tagsText,
    };
  }
}
