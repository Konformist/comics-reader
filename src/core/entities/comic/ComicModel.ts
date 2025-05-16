import Entity from '@/core/entities/Entity.ts';
import type { IComicDTO, IComicImageUrl } from '@/core/entities/comic/ComicTypes.ts';
import type { TParserOverride } from '@/core/entities/parser/ParserTypes.ts';

export default class ComicModel extends Entity<IComicDTO> {
  public id: number = 0;
  public url: string = '';
  public parser: number = 0;
  public name: string = '';
  public image: IComicImageUrl = {
    id: 0,
    fileId: 0,
    url: '',
  };
  public authors: number[] = [];
  public tags: number[] = [];
  public language: number = 0;
  public images: IComicImageUrl[] = [];
  public override: TParserOverride = {};

  constructor(dto?: Partial<IComicDTO>) {
    super();

    if (dto) {
      this.id = dto.id ?? 0;
      this.url = dto.url ?? '';
      this.parser = dto.parser ?? 0;
      this.name = dto.name ?? '';
      this.image = {
        id: 0,
        fileId: dto.image?.fileId ?? 0,
        url: dto.image?.url ?? '',
      };
      this.tags = dto.tags ? [...dto.tags] : [];
      this.authors = dto.authors ? [...dto.authors] : [];
      this.language = dto.language ?? 0;
      this.images = (dto.images ?? []).map((e) => ({ ...e }));
      this.override = dto.override ? { ...dto.override } : {};
    }
  }

  get isFilled(): boolean {
    return !!this.imagesFilled.length
      && !!this.name
      && !!this.language
      && !!this.authors.length;
  }

  get imagesFilled() {
    return this.images.filter((e) => e.fileId);
  }

  get imagesEmpty() {
    return this.images.filter((e) => !e.fileId);
  }

  addImage(): void {
    this.images.push({
      id: Math.max(...this.images.map((e) => e.id), 0) + 1,
      fileId: 0,
      url: '',
    });
  }

  getDTO(): IComicDTO {
    return {
      id: this.id,
      url: this.url,
      parser: this.parser,
      name: this.name,
      image: { ...this.image },
      tags: [...this.tags],
      authors: [...this.authors],
      language: this.language,
      images: this.images.map((e) => ({ ...e })),
      override: { ...this.override },
    };
  }
}
