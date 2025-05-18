export type TReaderDirection = 'vertical' | 'horizontal' | 'none';

export interface ISettingsDTO {
  /** Авто перелистывание */
  autoReading: boolean
  /** Таймер для авто перелистывания */
  autoReadingTimeout: number
  /** Сжимать картинки при загрузке */
  isCompress: boolean
  /** Направление прокрутки */
  direction: TReaderDirection
}
