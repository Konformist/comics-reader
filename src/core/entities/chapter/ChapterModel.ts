import { CHAPTER_READ_STATUS } from '@/core/entities/chapter/ChapterTypes.ts';
import type { IChapterDTO } from '@/plugins/WebApiPlugin.ts';
import ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';
import Entity from '@/core/entities/Entity.ts';

export default class ChapterModel extends Entity<IChapterDTO> implements IChapterDTO {
  id: number;
  cdate: string;
  mdate: string;
  name: string;
  comicId: number;
  pages: ChapterPageModel[];

  constructor(dto?: Partial<IChapterDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.cdate = dto?.cdate ?? '';
    this.mdate = dto?.mdate ?? '';
    this.name = dto?.name ?? '';
    this.comicId = dto?.comicId ?? 0;
    this.pages = (dto?.pages ?? []).map((e) => new ChapterPageModel(e));
  }

  get status() {
    if (!this.countPagesRead || !this.pages.length) {
      return CHAPTER_READ_STATUS.NONE;
    } else if (this.countPagesRead === this.pages.length) {
      return CHAPTER_READ_STATUS.FULL;
    } else {
      return CHAPTER_READ_STATUS.PROCESS;
    }
  }

  get lastPageUnread() {
    return this.pages.findIndex((e) => !e.isRead);
  }

  get countPagesRead() {
    return this.pages
      .filter((e) => e.isRead)
      .length;
  }

  addPage() {
    this.pages.push(new ChapterPageModel({ chapterId: this.id }));
  }

  getDTO(): IChapterDTO {
    return {
      id: this.id,
      cdate: this.cdate,
      mdate: this.mdate,
      name: this.name,
      comicId: this.comicId,
      pages: this.pages.map((e) => e.getDTO()),
    };
  }
}
