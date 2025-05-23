export interface IParsedComic {
  /** Название комикса */
  name?: string
  /** Ссылка на cover */
  cover?: string
  tags?: string[]
  authors?: string[]
  language?: string
  pages?: number
}
