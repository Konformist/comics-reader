import type { ISettingsDTO, TReaderDirection } from '@/plugins/WebApiPlugin.ts';
import Entity from '@/core/entities/Entity.ts';

export default class SettingsModel extends Entity<ISettingsDTO> implements ISettingsDTO {
  public autoReading = false;
  public autoReadingTimeout = 20_000;
  public isCompress = false;
  public readingMode: TReaderDirection = 'horizontal';

  constructor(dto?: Partial<ISettingsDTO>) {
    super();

    if (dto) {
      this.autoReading = dto.autoReading ?? false;
      this.autoReadingTimeout = dto.autoReadingTimeout ?? 20_000;
      this.isCompress = dto.isCompress ?? false;
      this.readingMode = dto.readingMode ?? 'horizontal';
    }
  }

  get readingTimeoutSec() {
    return this.autoReadingTimeout / 1000;
  }

  set readingTimeoutSec(val) {
    this.autoReadingTimeout = val * 1000;
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
