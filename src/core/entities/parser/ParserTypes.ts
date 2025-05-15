export interface IParserDTO {
  id: number
  name: string
  /** Ссылка на сайт */
  site: string
  title: string
  image: string
  images: string
  authors: string
  authorsText: string
  language: string
  tags: string
  tagsText: string
}

export interface IParsedComic {
  name: string
  image: string
  tags: string[]
  authors: string[]
  language: string
  images: string[]
}

export type TParserOverride = Partial<Omit<IParserDTO, 'id' | 'name'>>;
