import { registerPlugin } from '@capacitor/core';

interface DBDates {
  id: number
  cdate: string
  mdate: string
}

export interface ITagDTO extends DBDates {
  name: string
}

export interface IAuthorDTO extends DBDates {
  name: string
}

export interface ILanguageDTO extends DBDates {
  name: string
}

export interface IParserDTO extends DBDates {
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

export interface IFileDTO extends DBDates {
  name: string
  size: number
  path: string
  mime: string
}

export interface IComicCoverDTO {
  id: number
  fromUrl: string
  file: IFileDTO | null
}

export interface IComicOverrideDTO {
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

export interface IComicDTO extends DBDates {
  name: string
  parserId: number
  fromUrl: string
  languageId: number
  authors: number[]
  tags: number[]
  cover: IComicCoverDTO | null
}

export interface IChapterPageDTO {
  id: number
  chapterId: number
  fromUrl: string
  isRead: boolean
  file: IFileDTO | null
}

export interface IChapterDTO extends DBDates {
  name: string
  comicId: number
  pages: IChapterPageDTO[]
}

interface IWebApiPlugin {
  getTagsAll(): Promise<{ result: ITagDTO[] }>
  getTag(data: { id: number }): Promise<{ result: ITagDTO }>
  addTag(data: ITagDTO): Promise<{ result: number }>
  setTag(data: ITagDTO): Promise<void>
  delTag(data: { id: number }): Promise<void>

  getAuthorsAll(): Promise<{ result: IAuthorDTO[] }>
  getAuthor(data: { id: number }): Promise<{ result: IAuthorDTO }>
  addAuthor(data: IAuthorDTO): Promise<{ result: number }>
  setAuthor(data: IAuthorDTO): Promise<void>
  delAuthor(data: { id: number }): Promise<void>

  getLanguagesAll(): Promise<{ result: ILanguageDTO[] }>
  getLanguage(data: { id: number }): Promise<ILanguageDTO>
  addLanguage(data: ILanguageDTO): Promise<{ result: number }>
  setLanguage(data: ILanguageDTO): Promise<void>
  delLanguage(data: { id: number }): Promise<void>

  getParsersAll(): Promise<{ result: IParserDTO[] }>
  getParser(data: { id: number }): Promise<{ result: IParserDTO }>
  addParser(data: IParserDTO): Promise<{ result: number }>
  setParser(data: IParserDTO): Promise<void>
  delParser(data: { id: number }): Promise<void>

  getComicsAll(): Promise<{ result: IComicDTO[] }>
  getComic(data: { id: number }): Promise<{ result: IComicDTO }>
  addComic(data: IComicDTO): Promise<{ result: number }>
  setComic(data: IComicDTO): Promise<void>
  delComic(data: { id: number }): Promise<void>

  getComicOverride(data: { comicId: number }): Promise<{ result: IComicOverrideDTO }>
  setComicOverride(data: IComicOverrideDTO): Promise<void>

  addCoverFile(data: { comicId: number, file: string }): Promise<{ result: number }>
  delCoverFile(data: { comicId: number }): Promise<void>

  getChaptersAll(data: { comicId: number }): Promise<{ result: IChapterDTO[] }>
  getChapter(data: { id: number }): Promise<{ result: IChapterDTO }>
  addChapter(data: IChapterDTO): Promise<{ result: number }>
  setChapter(data: IChapterDTO): Promise<void>
  delChapter(data: { id: number }): Promise<void>

  getChapterPagesAll(data: { chapterId: number }): Promise<{ result: IChapterPageDTO[] }>
  getChapterPage(data: { id: number }): Promise<{ result: IChapterPageDTO[] }>
  addChapterPage(data: IChapterPageDTO): Promise<{ result: number }>
  setChapterPage(data: IChapterPageDTO): Promise<void>
  delChapterPage(data: { id: number }): Promise<void>

  addChapterPageFile(data: { chapterPageId: number, file: string }): Promise<{ result: number }>
  delChapterPageFile(data: { chapterPageId: number }): Promise<void>

  addBackup(): Promise<void>
  restoreBackup(data: { path: string }): Promise<void>
}

const WebApi = registerPlugin<IWebApiPlugin>('WebApi');

export default WebApi;
