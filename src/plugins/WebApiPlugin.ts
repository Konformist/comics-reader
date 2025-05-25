import WebApiPluginFake from '@/plugins/WebApiPluginFake.ts';
import { registerPlugin, WebPlugin } from '@capacitor/core';

interface IApiError {
  error: string
}

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
  type: 'file'
  name: string
  extension: string
  size: number
  lastModified: number
  path: string
}

export interface ITreeDirectory {
  type: 'directory'
  name: string
  count: number
  childes: Array<ITreeDirectory | ITreeFile>
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
  'file/comic-cover/add': {
    request: {
      comicId: number
      file: string
    }
    response: number
  }
  'file/comic-cover/del': {
    request: { comicId: number }
    response: boolean
  }
  'file/chapter-page/add': {
    request: {
      chapterPageId: number
      file: string
    }
    response: number
  }
  'file/chapter-page/del': {
    request: { chapterPageId: number }
    response: boolean
  }
  'file/comics-images/tree': {
    request: object
    response: ITreeDirectory
  }
  'file/backups/tree': {
    request: object
    response: ITreeDirectory
  }
  'file/file/downloads': {
    request: {
      fileName: string
      file: string
    }
    response: boolean
  }
  'file/comics-images/downloads': {
    request: object
    response: boolean
  }
  'file/comics-images/upload': {
    request: object
    response: boolean
  }
  'file/backups/downloads': {
    request: { fileName: string }
    response: boolean
  }
  'file/backups/upload': {
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
  'backup/backup/del': {
    request: { fileName: string }
    response: boolean
  }
  'backup/backup/restore': {
    request: { fileName: string }
    response: boolean
  }
}

interface IWebApiPlugin {
  api<K extends keyof IApi>(options: { path: K, body?: IApi[K]['request'] }): Promise<{ result: IApi[K]['response'] } | IApiError>
}

class WebApiPlugin extends WebPlugin implements IWebApiPlugin {
  fake: WebApiPluginFake = new WebApiPluginFake();

  // eslint-disable-next-line complexity
  async api<K extends keyof IApi>(options: { path: K, body: IApi[K]['request'] }): Promise<{ result: IApi[K]['response'] } | IApiError> {
    let result: IApi[K]['response'] = true;

    console.log(options.path, options.body);

    switch (options.path) {
      case 'tag/tag/list': result = this.fake.getTags(); break;
      // @ts-expect-error fuck
      case 'tag/tag/get': result = this.fake.getTag({ id: options.body.id }); break;
      case 'tag/tag/add': result = this.fake.getTagId(); break;
      case 'tag/tag/set': result = true; break;
      case 'tag/tag/del': result = true; break;
      case 'author/author/list': result = this.fake.getAuthors(); break;
      // @ts-expect-error fuck
      case 'author/author/get': result = this.fake.getAuthor({ id: options.body.id }); break;
      case 'author/author/add': result = this.fake.getAuthorId(); break;
      case 'author/author/set': result = true; break;
      case 'author/author/del': result = true; break;
      case 'language/language/list': result = this.fake.getLanguages(); break;
      // @ts-expect-error fuck
      case 'language/language/get': result = this.fake.getLanguage({ id: options.body.id }); break;
      case 'language/language/add': result = this.fake.getLanguageId(); break;
      case 'language/language/set': result = true; break;
      case 'language/language/del': result = true; break;
      case 'parser/parser/list': result = this.fake.getParsers(); break;
      // @ts-expect-error fuck
      case 'parser/parser/get': result = this.fake.getParser({ id: options.body.id }); break;
      case 'parser/parser/add': result = this.fake.getParserId(); break;
      case 'parser/parser/set': result = true; break;
      case 'parser/parser/del': result = true; break;
      case 'comic/comic/list': result = this.fake.getComics(); break;
      // @ts-expect-error fuck
      case 'comic/comic/get': result = this.fake.getComic({ id: options.body.id }); break;
      case 'comic/comic/add': result = this.fake.getComicId(); break;
      case 'comic/comic/set': result = true; break;
      case 'comic/comic/del': result = true; break;
      case 'comic/override/get': result = this.fake.getComicOverride(); break;
      case 'comic/override/set': result = true; break;
      // @ts-expect-error fuck
      case 'chapter/chapter/list': result = this.fake.getChapters(options.body.comicId); break;
      // @ts-expect-error fuck
      case 'chapter/chapter/get': result = this.fake.getChapter({ id: options.body.id }); break;
      case 'chapter/chapter/add': result = this.fake.getChapterId(); break;
      case 'chapter/chapter/set': result = true; break;
      case 'chapter/chapter/del': result = true; break;
      // @ts-expect-error fuck
      case 'chapter/page/list': result = this.fake.getChapterPages(options.body.chapterId); break;
      // @ts-expect-error fuck
      case 'chapter/page/get': result = this.fake.getChapterPage({ id: options.body.id }); break;
      case 'chapter/page/add': result = this.fake.getChapterPageId(); break;
      case 'chapter/page/set': result = true; break;
      case 'chapter/page/del': result = true; break;
      case 'file/comic-cover/add': result = 1; break;
      case 'file/comic-cover/del': result = true; break;
      case 'file/chapter-page/add': result = 1; break;
      case 'file/chapter-page/del': result = true; break;
      case 'file/comics-images/tree': result = {
        type: 'directory',
        name: 'comic-images',
        count: 0,
        childes: [],
      }; break;
      case 'file/backups/tree': result = {
        type: 'directory',
        name: 'backups',
        count: 0,
        childes: [],
      }; break;
      case 'file/file/downloads': result = true; break;
      case 'settings/settings/get': result = {
        autoReading: false,
        autoReadingTimeout: 20,
        isCompress: true,
        readingMode: 'vertical',
      }; break;
      case 'settings/settings/set': result = true; break;
      case 'backup/backup/add': result = true; break;
      case 'backup/backup/del': result = true; break;
      case 'backup/backup/restore': result = true; break;
      default: break;
    }

    return { result };
  }
}

const WebApi = import.meta.env.DEV
  ? registerPlugin<IWebApiPlugin>('WebApi', { web: () => new WebApiPlugin() })
  : registerPlugin<IWebApiPlugin>('WebApi');

export default WebApi;
