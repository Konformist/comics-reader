import Entity from '@/core/entities/Entity.ts';
import FileModel from '@/core/entities-v2/file/FileModel.ts';
import type { IComicCoverDTO } from '@/plugins/WebApiPlugin.ts';

export default class ComicCoverModel extends Entity<IComicCoverDTO> implements IComicCoverDTO {
  id: number;
  fromUrl: string;
  file: FileModel | null;

  constructor(dto?: Partial<IComicCoverDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.fromUrl = dto?.fromUrl ?? '';
    this.file = dto?.file ? new FileModel(dto?.file) : null;
  }

  getDTO(): IComicCoverDTO {
    return {
      id: this.id,
      fromUrl: this.fromUrl,
      file: this.file?.getDTO() ?? null,
    };
  }
}
