import type { ILanguageDTO } from '@/plugins/WebApiPlugin.ts';
import Entity from '@/core/entities/Entity.ts';

export default class LanguageModel extends Entity<ILanguageDTO> implements ILanguageDTO {
  id: number;
  cdate: string;
  mdate: string;
  name: string;

  constructor(dto?: Partial<ILanguageDTO>) {
    super();

    this.id = dto?.id ?? 0;
    this.cdate = dto?.cdate ?? '';
    this.mdate = dto?.mdate ?? '';
    this.name = dto?.name ?? '';
  }

  getDTO(): ILanguageDTO {
    return {
      id: this.id,
      cdate: this.cdate,
      mdate: this.mdate,
      name: this.name,
    };
  }
}
