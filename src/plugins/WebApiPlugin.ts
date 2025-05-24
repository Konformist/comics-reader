import WebApiPluginFake from '@/plugins/WebApiPluginFake.ts';
import { registerPlugin, WebPlugin } from '@capacitor/core';

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
  getLanguage(data: { id: number }): Promise<{ result: ILanguageDTO }>
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

  addCoverFile(data: { comicId: number, file: string, optimization?: boolean }): Promise<{ result: number }>
  delCoverFile(data: { comicId: number }): Promise<void>

  getChaptersAll(data: { comicId: number }): Promise<{ result: IChapterDTO[] }>
  getChapter(data: { id: number }): Promise<{ result: IChapterDTO }>
  addChapter(data: IChapterDTO): Promise<{ result: number }>
  setChapter(data: IChapterDTO): Promise<void>
  delChapter(data: { id: number }): Promise<void>

  getChapterPagesAll(data: { chapterId: number }): Promise<{ result: IChapterPageDTO[] }>
  getChapterPage(data: { id: number }): Promise<{ result: IChapterPageDTO }>
  addChapterPage(data: IChapterPageDTO): Promise<{ result: number }>
  setChapterPage(data: IChapterPageDTO): Promise<void>
  delChapterPage(data: { id: number }): Promise<void>

  addChapterPageFile(data: { chapterPageId: number, file: string, optimization?: boolean }): Promise<{ result: number }>
  delChapterPageFile(data: { chapterPageId: number }): Promise<void>

  addBackup(): Promise<void>
  restoreBackup(data: { path: string }): Promise<void>
}

class WebApiPlugin extends WebPlugin implements IWebApiPlugin {
  fake: WebApiPluginFake = new WebApiPluginFake();

  async getTagsAll(): Promise<{ result: ITagDTO[] }> {
    return { result: this.fake.getTags() };
  }
  async getTag(data: { id: number }): Promise<{ result: ITagDTO }> {
    console.log(data);
    return { result: this.fake.getTag() };
  }
  async addTag(data: ITagDTO): Promise<{ result: number }> {
    console.log(data);
    return { result: this.fake.getTagId() };
  }
  async setTag(data: ITagDTO): Promise<void> {
    console.log(data);
  }
  async delTag(data: { id: number }): Promise<void> {
    console.log(data);
  }
  async getAuthorsAll(): Promise<{ result: IAuthorDTO[] }> {
    return { result: this.fake.getAuthors() };
  }
  async getAuthor(data: { id: number }): Promise<{ result: IAuthorDTO }> {
    console.log(data);
    return { result: this.fake.getAuthor() };
  }
  async addAuthor(data: IAuthorDTO): Promise<{ result: number }> {
    console.log(data);
    return { result: this.fake.getAuthorId() };
  }
  async setAuthor(data: IAuthorDTO): Promise<void> {
    console.log(data);
  }
  async delAuthor(data: { id: number }): Promise<void> {
    console.log(data);
  }
  async getLanguagesAll(): Promise<{ result: ILanguageDTO[] }> {
    return { result: this.fake.getLanguages() };
  }
  async getLanguage(data: { id: number }): Promise<{ result: ILanguageDTO }> {
    console.log(data);
    return { result: this.fake.getLanguage() };
  }
  async addLanguage(data: ILanguageDTO): Promise<{ result: number }> {
    console.log(data);
    return { result: this.fake.getLanguageId() };
  }
  async setLanguage(data: ILanguageDTO): Promise<void> {
    console.log(data);
  }
  async delLanguage(data: { id: number }): Promise<void> {
    console.log(data);
  }
  async getParsersAll(): Promise<{ result: IParserDTO[] }> {
    return { result: this.fake.getParsers() };
  }
  async getParser(data: { id: number }): Promise<{ result: IParserDTO }> {
    console.log(data);
    return { result: this.fake.getParser() };
  }
  async addParser(data: IParserDTO): Promise<{ result: number }> {
    console.log(data);
    return { result: this.fake.getParserId() };
  }
  async setParser(data: IParserDTO): Promise<void> {
    console.log(data);
  }
  async delParser(data: { id: number }): Promise<void> {
    console.log(data);
  }
  async getComicsAll(): Promise<{ result: IComicDTO[] }> {
    return { result: this.fake.getComics() };
  }
  async getComic(data: { id: number }): Promise<{ result: IComicDTO }> {
    console.log(data);
    return { result: this.fake.getComic({ id: data.id }) };
  }
  async addComic(data: IComicDTO): Promise<{ result: number }> {
    console.log(data);
    return { result: this.fake.getComicId() };
  }
  async setComic(data: IComicDTO): Promise<void> {
    console.log(data);
  }
  async delComic(data: { id: number }): Promise<void> {
    console.log(data);
  }
  async getComicOverride(data: { comicId: number }): Promise<{ result: IComicOverrideDTO }> {
    console.log(data);
    return { result: this.fake.getComicOverride() };
  }
  async setComicOverride(data: IComicOverrideDTO): Promise<void> {
    console.log(data);
  }
  async addCoverFile(data: { comicId: number, file: string, optimization?: boolean }): Promise<{ result: number }> {
    console.log(data);
    return { result: 1 };
  }
  async delCoverFile(data: { comicId: number }): Promise<void> {
    console.log(data);
  }
  async getChaptersAll(data: { comicId: number }): Promise<{ result: IChapterDTO[] }> {
    console.log(data);
    return { result: this.fake.getChapters(data.comicId) };
  }
  async getChapter(data: { id: number }): Promise<{ result: IChapterDTO }> {
    console.log(data);
    return { result: this.fake.getChapter({ id: data.id }) };
  }
  async addChapter(data: IChapterDTO): Promise<{ result: number }> {
    console.log(data);
    return { result: this.fake.getChapterId() };
  }
  async setChapter(data: IChapterDTO): Promise<void> {
    console.log(data);
  }
  async delChapter(data: { id: number }): Promise<void> {
    console.log(data);
  }
  async getChapterPagesAll(data: { chapterId: number }): Promise<{ result: IChapterPageDTO[] }> {
    console.log(data);
    return { result: this.fake.getChapterPages(data.chapterId) };
  }
  async getChapterPage(data: { id: number }): Promise<{ result: IChapterPageDTO }> {
    console.log(data);
    return { result: this.fake.getChapterPage({ id: data.id }) };
  }
  async addChapterPage(data: IChapterPageDTO): Promise<{ result: number }> {
    console.log(data);
    return { result: this.fake.getChapterPageId() };
  }
  async setChapterPage(data: IChapterPageDTO): Promise<void> {
    console.log(data);
  }
  async delChapterPage(data: { id: number }): Promise<void> {
    console.log(data);
  }
  async addChapterPageFile(data: { chapterPageId: number, file: string, optimization?: boolean }): Promise<{ result: number }> {
    console.log(data);
    return { result: 1 };
  }
  async delChapterPageFile(data: { chapterPageId: number }): Promise<void> {
    console.log(data);
  }
  async addBackup(): Promise<void> {
  }
  async restoreBackup(data: { path: string }): Promise<void> {
    console.log(data);
  }
}

const WebApi = import.meta.env.DEV
  ? registerPlugin<IWebApiPlugin>('WebApi', { web: () => new WebApiPlugin() })
  : registerPlugin<IWebApiPlugin>('WebApi');

export default WebApi;
