import { registerPlugin, WebPlugin } from '@capacitor/core';

interface IApiError { error: string }

export type TReaderDirection = 'vertical' | 'horizontal' | 'webtoon';

export interface ISettingsDTO {
  /** Авто перелистывание */
  autoReading: boolean
  /** Таймер для авто перелистывания */
  autoReadingTimeout: number
  /** Сжимать картинки при загрузке */
  isCompress: boolean
  /** Направление прокрутки */
  readingMode: TReaderDirection
}

interface DBDates {
  id: number
  cdate: string
  mdate: string
}

export interface ITreeFile {
  name: string
  path: string
  isDirectory: false
  size: number
  lastModified: number
  mimeType: string | null
  children: null
}

export interface ITreeDirectory {
  name: string
  path: string
  isDirectory: true
  size: number
  lastModified: number
  mimeType: null
  children: Array<ITreeDirectory | ITreeFile>
}

export interface ITagDTO extends DBDates { name: string }

export interface IAuthorDTO extends DBDates { name: string }

export interface ILanguageDTO extends DBDates { name: string }

export interface IParseData {
  titleCSS: string
  annotationCSS: string
  coverCSS: string
  authorsCSS: string
  authorsTextCSS: string
  languageCSS: string
  tagsCSS: string
  tagsTextCSS: string
  chaptersCSS: string
  chaptersTitleCSS: string
  pagesTemplateUrl: string
  pagesCSS: string
  pagesImageCSS: string
}

export interface IParserDTO extends DBDates, IParseData {
  name: string
  siteUrl: string
}

export interface IFileDTO extends DBDates {
  name: string
  size: number
  path: string
  mime: string
}

export interface IComicCoverDTO extends DBDates {
  fromUrl: string
  file: IFileDTO | null
}

export interface IComicOverrideDTO extends DBDates, IParseData {}

export interface IComicDTO extends DBDates {
  name: string
  parserId: number
  fromUrl: string
  annotation: string
  languageId: number
  authors: number[]
  tags: number[]
  cover: IComicCoverDTO | null
}

export interface IChapterPageDTO extends DBDates {
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

export interface IApi {
  'tag/tag/list': {
    request: object
    response: ITagDTO[]
  }
  'tag/tag/get': {
    request: { id: number }
    response: ITagDTO
  }
  'tag/tag/add': {
    request: ITagDTO
    response: number
  }
  'tag/tag/set': {
    request: ITagDTO
    response: boolean
  }
  'tag/tag/del': {
    request: { id: number }
    response: boolean
  }
  'author/author/list': {
    request: object
    response: IAuthorDTO[]
  }
  'author/author/get': {
    request: { id: number }
    response: IAuthorDTO
  }
  'author/author/add': {
    request: IAuthorDTO
    response: number
  }
  'author/author/set': {
    request: IAuthorDTO
    response: boolean
  }
  'author/author/del': {
    request: { id: number }
    response: boolean
  }
  'language/language/list': {
    request: object
    response: ILanguageDTO[]
  }
  'language/language/get': {
    request: { id: number }
    response: ILanguageDTO
  }
  'language/language/add': {
    request: ILanguageDTO
    response: number
  }
  'language/language/set': {
    request: ILanguageDTO
    response: boolean
  }
  'language/language/del': {
    request: { id: number }
    response: boolean
  }
  'parser/parser/list': {
    request: object
    response: IParserDTO[]
  }
  'parser/parser/get': {
    request: { id: number }
    response: IParserDTO
  }
  'parser/parser/add': {
    request: IParserDTO
    response: number
  }
  'parser/parser/set': {
    request: IParserDTO
    response: boolean
  }
  'parser/parser/del': {
    request: { id: number }
    response: boolean
  }
  'comic/comic/list': {
    request: object
    response: IComicDTO[]
  }
  'comic/comic/get': {
    request: { id: number }
    response: IComicDTO
  }
  'comic/comic/add': {
    request: IComicDTO
    response: number
  }
  'comic/comic/set': {
    request: IComicDTO
    response: boolean
  }
  'comic/comic/del': {
    request: { id: number }
    response: boolean
  }
  'comic/comic/parse': {
    request: {
      id: number
      cookie?: string
    }
    response: boolean
  }
  'comic/comic/upload': {
    request: { id: number }
    response: boolean
  }
  'comic/archive/add': {
    request: object
    response: boolean
  }
  'comic/override/get': {
    request: { comicId: number }
    response: IComicOverrideDTO
  }
  'comic/override/set': {
    request: IComicOverrideDTO
    response: boolean
  }
  'chapter/chapter/list': {
    request: { comicId: number }
    response: IChapterDTO[]
  }
  'chapter/chapter/get': {
    request: { id: number }
    response: IChapterDTO
  }
  'chapter/chapter/add': {
    request: IChapterDTO
    response: number
  }
  'chapter/chapter/set': {
    request: IChapterDTO
    response: boolean
  }
  'chapter/chapter/del': {
    request: { id: number }
    response: boolean
  }
  'chapter/page/list': {
    request: { chapterId: number }
    response: IChapterPageDTO[]
  }
  'chapter/page/get': {
    request: { id: number }
    response: IChapterPageDTO
  }
  'chapter/page/add': {
    request: IChapterPageDTO
    response: number
  }
  'chapter/page/set': {
    request: IChapterPageDTO
    response: boolean
  }
  'chapter/page/del': {
    request: { id: number }
    response: boolean
  }
  'file/comic-cover/download': {
    request: {
      comicId: number
      link: string
    }
    response: number
  }
  'file/comic-cover/add': {
    request: { comicId: number }
    response: number
  }
  'file/comic-cover/del': {
    request: { comicId: number }
    response: boolean
  }
  'file/chapter-page/download': {
    request: {
      chapterPageId: number
      link: string
    }
    response: number
  }
  'file/chapter-page/add': {
    request: { chapterPageId: number }
    response: number
  }
  'file/chapter-page/del': {
    request: { chapterPageId: number }
    response: boolean
  }
  'file/files/tree': {
    request: object
    response: ITreeDirectory[]
  }
  'file/file/downloads': {
    request: {
      fileName: string
      file: string
    }
    response: boolean
  }
  'settings/settings/get': {
    request: object
    response: ISettingsDTO
  }
  'settings/settings/set': {
    request: ISettingsDTO
    response: boolean
  }
  'backup/backup/add': {
    request: object
    response: boolean
  }
  'backup/backup/restore': {
    request: object
    response: boolean
  }
  'data/data/migrate': {
    request: object
    response: boolean
  }
}

interface IWebApiPlugin {
  api<K extends keyof IApi>(options: {
    path: K
    body?: IApi[K]['request']
  }): Promise<{ result: IApi[K]['response'] } | IApiError>
}

class WebApiPlugin extends WebPlugin implements IWebApiPlugin {
  async api<K extends keyof IApi>(options: {
    path: K
    body: IApi[K]['request']
  }): Promise<{ result: IApi[K]['response'] } | IApiError> {
    const result = await fetch('/api', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(options),
    });
    return result.json();
  }
}

const WebApi = import.meta.env.DEV
  ? registerPlugin<IWebApiPlugin>('WebApi', { web: () => new WebApiPlugin() })
  : registerPlugin<IWebApiPlugin>('WebApi');

export default WebApi;
