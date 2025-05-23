import type { IChapterDTO } from '@/plugins/WebApiPlugin.ts';
import Entity from '@/core/entities/Entity.ts';
import ChapterPageModel from '@/core/entities-v2/chapter-page/ChapterPageModel.ts';

export default class ChapterModel extends Entity<IChapterDTO> implements IChapterDTO {
  id: number;
  cdate: number;
  mdate: number;
  name: string;
  comicId: number;
  pages: ChapterPageModel[];

  constructor(dto?: Partial<IChapterDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.cdate = dto?.cdate ?? 0;
    this.mdate = dto?.mdate ?? 0;
    this.name = dto?.name ?? '';
    this.comicId = dto?.comicId ?? 0;
    this.pages = (dto?.pages ?? []).map((e) => new ChapterPageModel(e));
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
