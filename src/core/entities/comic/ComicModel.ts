import type { IComicDTO } from '@/plugins/WebApiPlugin.ts';
import Entity from '@/core/entities/Entity.ts';
import ChapterModel from '@/core/entities/chapter/ChapterModel.ts';
import { CHAPTER_READ_STATUS } from '@/core/entities/chapter/ChapterTypes.ts';
import { COMIC_READ_STATUS } from '@/core/entities/comic/ComicTypes.ts';
import ComicOverrideModel from '@/core/entities/comic-override/ComicOverrideModel.ts';
import ComicCoverModel from '@/core/entities/comic-cover/ComicCoverModel.ts';

export default class ComicModel extends Entity<IComicDTO> implements IComicDTO {
  id: number;
  cdate: string;
  mdate: string;
  name: string;
  parserId: number;
  fromUrl: string;
  annotation: string;
  languageId: number;
  authors: number[];
  tags: number[];
  cover: ComicCoverModel;
  override: ComicOverrideModel;
  chapters: Array<ChapterModel>;

  constructor(dto?: Partial<IComicDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.cdate = dto?.cdate ?? '';
    this.mdate = dto?.mdate ?? '';
    this.name = dto?.name ?? '';
    this.parserId = dto?.parserId ?? 0;
    this.fromUrl = dto?.fromUrl ?? '';
    this.annotation = dto?.annotation ?? '';
    this.languageId = dto?.languageId ?? 0;
    this.authors = dto?.authors ?? [];
    this.tags = dto?.tags ?? [];
    this.cover = new ComicCoverModel(dto?.cover || undefined);
    this.override = new ComicOverrideModel(dto?.override || undefined);
    this.chapters = (dto?.chapters || []).map((e) => new ChapterModel(e));
  }

  get status() {
    if (!this.countRead || !this.chapters.length) {
      return COMIC_READ_STATUS.NONE;
    } else if (this.countRead === this.chapters.length) {
      return COMIC_READ_STATUS.FULL;
    } else {
      return COMIC_READ_STATUS.PROCESS;
    }
  }

  get countRead() {
    return this.chapters
      .filter((e) => e.status === CHAPTER_READ_STATUS.FULL)
      .length;
  }

  getDTO(): IComicDTO {
    return {
      id: this.id,
      cdate: this.cdate,
      mdate: this.mdate,
      name: this.name,
      parserId: this.parserId,
      fromUrl: this.fromUrl,
      annotation: this.annotation,
      languageId: this.languageId,
      authors: [...this.authors],
      tags: [...this.tags],
      cover: this.cover.getDTO(),
      override: this.override.getDTO(),
      chapters: this.chapters.map((e) => e.getDTO()),
    };
  }
}
