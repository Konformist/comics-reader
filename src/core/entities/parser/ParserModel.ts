import type { IComicDTO } from '@/core/entities/comic/ComicTypes.ts';
import Entity from '@/core/entities/Entity.ts';
import type { IParserDTO, TParserOverride } from '@/core/entities/parser/ParserTypes.ts';

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

  constructor (dto?: Partial<IParserDTO>) {
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

  cleanHTML (value: string): string {
    return value
      .replaceAll(/<head>(?:.|\n|\t)*?<\/head>/gm, '')
      .replaceAll(/<style.*?>(?:.|\n|\t)*?<\/style>/gm, '')
      .replaceAll(/<script.*?>(?:.|\n|\t)*?<\/script>/gm, '')
  }

  getParsedString (data: HTMLElement, item: string) {
    return data.querySelector(item)?.textContent || '';
  }

  getParsedArray (data: HTMLElement, item: string, text: string) {
    return [...data.querySelectorAll<HTMLElement>(item)].map(e => {
      if (text)
        return this.getParsedString(e, text);

      return e.textContent || '';
    });
  }

  parseTitle (data: HTMLElement, item?: string) {
    return item || this.title
      ? this.getParsedString(data, item || this.title)
      : '';
  }

  parseImage (data: HTMLElement, item?: string) {
    return item || this.image
      ? data.querySelector<HTMLImageElement>(item || this.image)?.src || ''
      : '';
  }

  parseImages (data: HTMLElement, item?: string) {
    return item || this.images
      ? [...data.querySelectorAll(item || this.images)].map(e => {
        return e.querySelector<HTMLImageElement>('img')?.src || '';
      })
      : [];
  }

  parseLanguage (data: HTMLElement, item?: string) {
    return item || this.language
      ? this.getParsedString(data, item || this.language)
      : '';
  }

  parseAuthors (data: HTMLElement, item?: string) {
    return item || this.authors
      ? this.getParsedArray(data, item || this.authors, this.authorsText)
      : [];
  }

  parseTags (data: HTMLElement, item?: string) {
    return item || this.tags
      ? this.getParsedArray(data, item || this.tags, this.tagsText)
      : [];
  }

  parse (value: string, override?: TParserOverride) {
    const cleaned = this.cleanHTML(value);
    const parser = new DOMParser();
    const result = parser
      .parseFromString(cleaned, 'text/html')
      .body;

    const comicDTO: Partial<Omit<IComicDTO, 'id'|'image'>> = {};

    const name = this.parseTitle(result, override?.title);
    if (name) comicDTO.name = name;

    const image = this.parseImage(result, override?.image);
    if (image) comicDTO.imageUrl = image;

    const images = this.parseImages(result, override?.images);
    if (images) {
      comicDTO.images = images.map((from, index) => ({
        id: index + 1,
        url: '',
        from,
      }));
    }

    const authors = this.parseAuthors(result, override?.authors);
    if (authors) comicDTO.authors = authors;

    const language = this.parseLanguage(result, override?.language);
    if (language) comicDTO.language = language;

    const tags = this.parseTags(result, override?.tags);
    if (tags) comicDTO.tags = tags;

    return comicDTO;
  }

  getDTO (): IParserDTO {
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
    }
  }
}
