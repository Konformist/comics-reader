import type { IChapterPageDTO } from '@/plugins/WebApiPlugin.ts';
import Entity from '@/core/entities/Entity.ts';
import FileModel from '@/core/entities/file/FileModel.ts';

export default class ChapterPageModel extends Entity<IChapterPageDTO> implements IChapterPageDTO {
  id: number;
  cdate: string;
  mdate: string;
  chapterId: number;
  fromUrl: string;
  isRead: boolean;
  file: FileModel | null;

  constructor(dto?: Partial<IChapterPageDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.cdate = dto?.cdate ?? '';
    this.mdate = dto?.mdate ?? '';
    this.chapterId = dto?.chapterId ?? 0;
    this.fromUrl = dto?.fromUrl ?? '';
    this.isRead = dto?.isRead ?? false;
    this.file = dto?.file ? new FileModel(dto.file) : null;
  }

  getDTO(): IChapterPageDTO {
    return {
      id: this.id,
      cdate: this.cdate,
      mdate: this.mdate,
      chapterId: this.chapterId,
      fromUrl: this.fromUrl,
      isRead: this.isRead,
      file: this.file?.getDTO() ?? null,
    };
  }
}
