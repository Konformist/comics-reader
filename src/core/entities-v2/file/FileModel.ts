import Entity from '@/core/entities/Entity.ts';
import type { IFileDTO } from '@/plugins/WebApiPlugin.ts';

export default class FileModel extends Entity<IFileDTO> implements IFileDTO {
  id: number;
  cdate: number;
  mdate: number;
  name: string;
  size: number;
  path: string;
  mime: string;

  constructor(dto?: Partial<IFileDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.cdate = dto?.cdate ?? 0;
    this.mdate = dto?.mdate ?? 0;
    this.name = dto?.name ?? '';
    this.size = dto?.size ?? 0;
    this.path = dto?.path ?? '';
    this.mime = dto?.mime ?? '';
  }

  get url(): string {
    return this.path ? `${this.path}?${this.mdate}` : '';
  }

  getDTO(): IFileDTO {
    return {
      id: this.id,
      cdate: this.cdate,
      mdate: this.mdate,
      name: this.name,
      size: this.size,
      path: this.path,
      mime: this.mime,
    };
  }
}
