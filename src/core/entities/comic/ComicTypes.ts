import type { IParserDTO } from '@/core/entities/parser/ParserTypes.ts';

export interface IComicImageDTO {
  id: number
  url: string
  from: string
}

export interface IComicImageUrl {
  /** ID */
  id: number
  /** ID файла */
  fileId: number
  /** Ссылка на источник */
  url: string
}

export interface IComicDTO {
  /** ID комикса */
  id: number
  /** Ссылка на комикс */
  url: string
  /** Название комикса */
  name: string
  /** Файла картинки */
  image: IComicImageUrl
  /** ID тегов */
  tags: number[]
  /** ID авторов */
  authors: number[]
  /** ID языков */
  language: number
  /** Файлы картинок */
  images: Array<IComicImageUrl>
  /** ID парсера */
  parser: number
  /** Перезапись значений парсера */
  override: Partial<Omit<IParserDTO, 'id' | 'name'>>
}
