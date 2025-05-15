import type { ILanguageDTO } from '@/core/object-value/language/LanguageTypes.ts';
import ObjectValue from '@/core/object-value/ObjectValue.ts';

export default class LanguageObject extends ObjectValue<ILanguageDTO> {
  public id: number = 0;
  public name: string = '';

  constructor(dto?: Partial<ILanguageDTO>) {
    super();

    if (dto) {
      this.id = dto?.id ?? 0;
      this.name = dto?.name ?? '';
    }
  }

  public getDTO(): ILanguageDTO {
    return {
      id: this.id,
      name: this.name,
    };
  }
}
