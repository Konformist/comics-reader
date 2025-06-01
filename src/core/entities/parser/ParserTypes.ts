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

export interface IParsedChapterData {
  name: string
  fromUrl: string
  pages: string[]
}

export interface IParsedComicData {
  /** Название комикса */
  name: string
  annotation: string
  /** Ссылка на cover */
  cover: string
  tags: string[]
  authors: string[]
  language: string
  chapters: Array<IParsedChapterData>
}
