import type { IFileDTO } from '@/core/object-value/file/FileTypes.ts';
import ObjectValue from '@/core/object-value/ObjectValue.ts';
import { Capacitor } from '@capacitor/core';
import { Filesystem } from '@capacitor/filesystem';

export default class FileModel extends ObjectValue<IFileDTO> {
  public id: number = 0;
  public name: string = '';
  public mime: string = '';
  public size: number = 0;
  public mdate: number = 0;
  public cdate: number = 0;
  public path: string = '';

  constructor(dto?: Partial<IFileDTO>) {
    super();

    if (dto) {
      this.id = dto?.id ?? 0;
      this.name = dto?.name ?? '';
      this.mime = dto?.mime ?? '';
      this.size = dto?.size ?? 0;
      this.mdate = dto?.mdate ?? 0;
      this.cdate = dto?.cdate ?? 0;
      this.path = dto?.path ?? '';
    }
  }

  get url(): string {
    if (!this.path) return '';
    else return `${Capacitor.convertFileSrc(this.path)}?${this.mdate}`;
  }

  public getDTO(): IFileDTO {
    return {
      id: this.id,
      name: this.name,
      mime: this.mime,
      size: this.size,
      mdate: this.mdate,
      cdate: this.cdate,
      path: this.path,
    };
  }
}
