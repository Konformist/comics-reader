import ObjectValue from '@/core/object-value/ObjectValue.ts';
import type { ITagDTO } from '@/core/object-value/tag/TagTypes.ts';

export default class TagObject extends ObjectValue<ITagDTO> {
  public id: number = 0;
  public name: string = '';

  constructor(dto?: Partial<ITagDTO>) {
    super();

    if (dto) {
      this.id = dto?.id ?? 0;
      this.name = dto?.name ?? '';
    }
  }

  public getDTO(): ITagDTO {
    return {
      id: this.id,
      name: this.name,
    };
  }
}
