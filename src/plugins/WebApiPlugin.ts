import { registerPlugin } from '@capacitor/core';

interface ITagDTO {
  id: number
  cdate: number
  mdate: number
  name: string
}

interface IAuthorDTO {
  id: number
  cdate: number
  mdate: number
  name: string
}

interface ILanguageDTO {
  id: number
  cdate: number
  mdate: number
  name: string
}

interface IParserDTO {
  id: number
  cdate: number
  mdate: number
  name: string
  siteUrl: string
  titleCSS: string
  coverCSS: string
  pagesCSS: string
  authorsCSS: string
  authorsTextCSS: string
  languageCSS: string
  tagsCSS: string
  tagsTextCSS: string
}

interface IFileDTO {
  id: number
  cdate: number
  mdate: number
  name: string
  size: number
  path: string
  mime: string
}

interface IComicCoverDTO {
  id: number
  fromUrl: string
  file: IFileDTO | null
}

interface IComicOverrideDTO {
  id: number
  titleCSS: string
  coverCSS: string
  pagesCSS: string
  languageCSS: string
  tagsCSS: string
  tagsTextCSS: string
  authorsCSS: string
  authorsTextCSS: string
}

interface IComicDTO {
  id: number
  cdate: number
  mdate: number
  name: string
  parser: number
  fromUrl: string
  language: number
  authors: number[]
  tags: number[]
  cover: IComicCoverDTO | null
}

interface IChapterPageDTO {
  id: number
  fromUrl: string
  file: IFileDTO | null
}

interface IChapterDTO {
  id: number
  cdate: number
  mdate: number
  name: string
  pages: IChapterPageDTO[]
}

interface IWebApiPlugin {
  getTagsAll(): Promise<ITagDTO[]>
  getTag(data: { id: number }): Promise<ITagDTO>
  addTag(data: ITagDTO): Promise<{ id: number }>
  setTag(data: ITagDTO): Promise<void>
  delTag(data: { id: number }): Promise<void>

  getAuthorsAll(): Promise<IAuthorDTO[]>
  getAuthor(data: { id: number }): Promise<IAuthorDTO>
  addAuthor(data: IAuthorDTO): Promise<{ id: number }>
  setAuthor(data: IAuthorDTO): Promise<void>
  delAuthor(data: { id: number }): Promise<void>

  getLanguagesAll(): Promise<ILanguageDTO[]>
  getLanguage(data: { id: number }): Promise<ILanguageDTO>
  addLanguage(data: ILanguageDTO): Promise<{ id: number }>
  setLanguage(data: ILanguageDTO): Promise<void>
  delLanguage(data: { id: number }): Promise<void>

  getParsersAll(): Promise<IParserDTO[]>
  getParser(data: { id: number }): Promise<IParserDTO>
  addParser(data: IParserDTO): Promise<{ id: number }>
  setParser(data: IParserDTO): Promise<void>
  delParser(data: { id: number }): Promise<void>

  getComicsAll(): Promise<IComicDTO[]>
  getComic(data: { id: number }): Promise<IComicDTO>
  addComic(data: IComicDTO): Promise<{ id: number }>
  setComic(data: IComicDTO): Promise<void>
  delComic(data: { id: number }): Promise<void>

  getComicOverride(data: { id: number }): Promise<IComicOverrideDTO>
  setComicOverride(data: IComicOverrideDTO): Promise<void>

  addCoverFile(data: { comicId: number, file: string }): Promise<{ id: number }>
  delCoverFile(data: { comicId: number }): Promise<void>

  getChaptersAll(data: { comicId: number }): Promise<IChapterDTO[]>
  getChapter(data: { id: number }): Promise<IChapterDTO>
  addChapter(data: IChapterDTO): Promise<{ id: number }>
  setChapter(data: IChapterDTO): Promise<void>
  delChapter(data: { id: number }): Promise<void>

  getChapterPagesAll(data: { chapterId: number }): Promise<IChapterPageDTO[]>
  getChapterPage(data: { id: number }): Promise<IChapterPageDTO[]>
  addChapterPage(data: IChapterPageDTO): Promise<{ id: number }>
  setChapterPage(data: IChapterPageDTO): Promise<void>
  delChapterPage(data: { id: number }): Promise<void>

  addChapterPageFile(data: { comicId: number, chapterPageId: number, file: string }): Promise<{ id: number }>
  delChapterPageFile(data: { chapterPageId: number }): Promise<void>

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  migrate(data: any): Promise<void>
}

const WebApi = registerPlugin<IWebApiPlugin>('WebApi');

export default WebApi;
