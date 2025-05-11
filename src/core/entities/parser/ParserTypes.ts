export interface IParserDTO {
  id: number
  name: string
  title: string
  image: string
  images: string
  authors: string
  authorsText: string
  language: string
  tags: string
  tagsText: string
}

export type TParserOverride = Partial<Omit<IParserDTO, 'id'|'name'>>;
