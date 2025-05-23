import type { IComicDTO } from '@/plugins/WebApiPlugin.ts';
import ComicCoverModel from '@/core/entities-v2/comic-cover/ComicCoverModel.ts';
import Entity from '@/core/entities/Entity.ts';

export default class ComicModel extends Entity<IComicDTO> implements IComicDTO {
  id: number;
  cdate: number;
  mdate: number;
  name: string;
  parserId: number;
  fromUrl: string;
  languageId: number;
  authors: number[];
  tags: number[];
  cover: ComicCoverModel;

  constructor(dto?: Partial<IComicDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.cdate = dto?.cdate ?? 0;
    this.mdate = dto?.mdate ?? 0;
    this.name = dto?.name ?? '';
    this.parserId = dto?.parserId ?? 0;
    this.fromUrl = dto?.fromUrl ?? '';
    this.languageId = dto?.languageId ?? 0;
    this.authors = dto?.authors ?? [];
    this.tags = dto?.tags ?? [];
    this.cover = new ComicCoverModel(dto?.cover || undefined);
  }

  getDTO(): IComicDTO {
    return {
      id: this.id,
      cdate: this.cdate,
      mdate: this.mdate,
      name: this.name,
      parserId: this.parserId,
      fromUrl: this.fromUrl,
      languageId: this.languageId,
      authors: [...this.authors],
      tags: [...this.tags],
      cover: this.cover.getDTO(),
    };
  }
}
