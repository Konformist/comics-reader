import type { IParserDTO } from '@/core/entities/parser/ParserTypes.ts';

export interface IComicImageDTO {
  id: number
  url: string
  from: string
}

export interface IComicDTO {
  id: number
  url: string
  parser: number
  name: string
  image: string
  imageUrl: string
  tags: number[]
  authors: number[]
  language: number
  images: IComicImageDTO[]
  override: Partial<Omit<IParserDTO, 'id' | 'name'>>
}
