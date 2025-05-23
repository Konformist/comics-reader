import type { IAuthorDTO } from '@/plugins/WebApiPlugin.ts';
import Entity from '@/core/entities/Entity.ts';

export default class AuthorModel extends Entity<IAuthorDTO> implements IAuthorDTO {
  id: number;
  cdate: number;
  mdate: number;
  name: string;

  constructor(dto?: Partial<IAuthorDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.cdate = dto?.cdate ?? 0;
    this.mdate = dto?.mdate ?? 0;
    this.name = dto?.name ?? '';
  }

  getDTO(): IAuthorDTO {
    return {
      id: this.id,
      cdate: this.cdate,
      mdate: this.mdate,
      name: this.name,
    };
  }
}
