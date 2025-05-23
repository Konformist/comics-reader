import type { ITagDTO } from '@/plugins/WebApiPlugin.ts';
import Entity from '@/core/entities/Entity.ts';

export default class TagModel extends Entity<ITagDTO> implements ITagDTO {
  id: number;
  cdate: string;
  mdate: string;
  name: string;

  constructor(dto?: Partial<ITagDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.cdate = dto?.cdate ?? '';
    this.mdate = dto?.mdate ?? '';
    this.name = dto?.name ?? '';
  }

  getDTO(): ITagDTO {
    return {
      id: this.id,
      cdate: this.cdate,
      mdate: this.mdate,
      name: this.name,
    };
  }
}
