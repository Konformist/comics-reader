import type { ISettingsDTO, TReaderDirection } from '@/plugins/WebApiPlugin.ts';
import Entity from '@/core/entities/Entity.ts';

export default class SettingsModel extends Entity<ISettingsDTO> implements ISettingsDTO {
  public autoReading: boolean = false;
  public autoReadingTimeout: number = 20000;
  public isCompress: boolean = false;
  public readingMode: TReaderDirection = 'horizontal';

  constructor(dto?: Partial<ISettingsDTO>) {
    super();

    if (dto) {
      this.autoReading = dto.autoReading ?? false;
      this.autoReadingTimeout = dto.autoReadingTimeout ?? 20000;
      this.isCompress = dto.isCompress ?? false;
      this.readingMode = dto.readingMode ?? 'horizontal';
    }
  }

  getDTO(): ISettingsDTO {
    return {
      autoReading: this.autoReading,
      autoReadingTimeout: this.autoReadingTimeout,
      isCompress: this.isCompress,
      readingMode: this.readingMode,
    };
  }
}
