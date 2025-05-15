import Entity from '@/core/entities/Entity.ts';
import type { IComicDTO, IComicImageDTO } from '@/core/entities/comic/ComicTypes.ts';
import type { TParserOverride } from '@/core/entities/parser/ParserTypes.ts';

export default class ComicModel extends Entity<IComicDTO> {
  public id: number = 0;
  public url: string = '';
  public parser: number = 0;
  public name: string = '';
  public image: string = '';
  public imageUrl: string = '';
  public authors: string[] = [];
  public tags: string[] = [];
  public language: string = '';
  public images: IComicImageDTO[] = [];
  public override: TParserOverride = {};

  constructor (dto?: Partial<IComicDTO>) {
    super();

    if (dto) {
      this.id = dto.id ?? 0;
      this.url = dto.url ?? '';
      this.parser = dto.parser ?? 0;
      this.name = dto.name ?? '';
      this.image = dto.image ?? '';
      this.imageUrl = dto.imageUrl ?? '';
      this.tags = dto.tags ? [...dto.tags] : [];
      this.authors = dto.authors ? [...dto.authors] : [];
      this.language = dto.language ?? '';
      this.images = (dto.images ?? []).map((e) => ({ ...e }));
      this.override = dto.override ? { ...dto.override } : {};
    }
  }

  get isFilled (): boolean {
    return this.images.every((e) => e.url)
      && !!this.name
      && !!this.language
      && !!this.authors.length;
  }

  get imagesFilled () {
    return this.images.filter((e) => e.url);
  }

  get imagesEmpty () {
    return this.images.filter((e) => !e.url);
  }

  addImage (): void {
    this.images.push({
      id: Math.max(...this.images.map((e) => e.id), 0) + 1,
      url: '',
      from: '',
    });
  }

  getDTO (): IComicDTO {
    return {
      id: this.id,
      url: this.url,
      parser: this.parser,
      name: this.name,
      image: this.image,
      imageUrl: this.imageUrl,
      tags: [...this.tags],
      authors: [...this.authors],
      language: this.language,
      images: this.images.map((e) => ({ ...e })),
      override: { ...this.override },
    };
  }
}
