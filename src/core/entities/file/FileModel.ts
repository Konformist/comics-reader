import Entity from '@/core/entities/Entity.ts';
import type { IFileDTO } from '@/plugins/WebApiPlugin.ts';
import { Capacitor } from '@capacitor/core';

export default class FileModel extends Entity<IFileDTO> implements IFileDTO {
  id: number;
  cdate: string;
  mdate: string;
  name: string;
  size: number;
  path: string;
  mime: string;

  constructor(dto?: Partial<IFileDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.cdate = dto?.cdate ?? '';
    this.mdate = dto?.mdate ?? '';
    this.name = dto?.name ?? '';
    this.size = dto?.size ?? 0;
    this.path = dto?.path ?? '';
    this.mime = dto?.mime ?? '';
  }

  get url(): string {
    return this.path ? `${Capacitor.convertFileSrc(this.path)}?${this.mdate}` : '';
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
