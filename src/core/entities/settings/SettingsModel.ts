import Entity from '@/core/entities/Entity.ts';
import type { ISettingsDTO, TReaderDirection } from '@/core/entities/settings/SettingsTypes.ts';

export default class SettingsModel extends Entity<ISettingsDTO> {
  public autoReading: boolean = false;
  public autoReadingTimeout: number = 10;
  public isCompress: boolean = false;
  public direction: TReaderDirection = 'horizontal';

  constructor(dto?: Partial<ISettingsDTO>) {
    super();

    if (dto) {
      this.autoReading = dto.autoReading ?? false;
      this.autoReadingTimeout = dto.autoReadingTimeout ?? 10;
      this.isCompress = dto.isCompress ?? false;
      this.direction = dto.direction ?? 'horizontal';
    }
  }

  getDTO(): ISettingsDTO {
    return {
      autoReading: this.autoReading,
      autoReadingTimeout: this.autoReadingTimeout,
      isCompress: this.isCompress,
      direction: this.direction,
    };
  }
}
