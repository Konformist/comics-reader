import Entity from '@/core/entities/Entity.ts';
import FileModel from '@/core/entities/file/FileModel.ts';
import type { IComicCoverDTO } from '@/plugins/WebApiPlugin.ts';

export default class ComicCoverModel extends Entity<IComicCoverDTO> implements IComicCoverDTO {
  id: number;
  cdate: string;
  mdate: string;
  fromUrl: string;
  file: FileModel | null;

  constructor(dto?: Partial<IComicCoverDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.cdate = dto?.cdate ?? '';
    this.mdate = dto?.mdate ?? '';
    this.fromUrl = dto?.fromUrl ?? '';
    this.file = dto?.file ? new FileModel(dto?.file) : null;
  }

  getDTO(): IComicCoverDTO {
    return {
      id: this.id,
      cdate: this.cdate,
      mdate: this.mdate,
      fromUrl: this.fromUrl,
      file: this.file?.getDTO() ?? null,
    };
  }
}
