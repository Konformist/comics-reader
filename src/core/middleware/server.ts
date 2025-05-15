import type { IComicDTO } from '@/core/entities/comic/ComicTypes.ts';
import type { IParserDTO } from '@/core/entities/parser/ParserTypes.ts';
import AuthorsServer from '@/core/middleware/AuthorsServer.ts';
import LanguagesServer from '@/core/middleware/LanguagesServer.ts';
import migrator from '@/core/middleware/migrator.ts';
import serverBackups from '@/core/middleware/serverBackups.ts';
import serverFiles from '@/core/middleware/serverFiles.ts';
import serverSettings from '@/core/middleware/serverSettings.ts';
import ComicsServer, { type IResizeOptions } from '@/core/middleware/ComicsServer.ts';
import ParsersServer from '@/core/middleware/ParsersServer.ts';
import TagsServer from '@/core/middleware/TagsServer.ts';
import type { IAuthorDTO } from '@/core/object-value/author/AuthorTypes.ts';
import type { ILanguageDTO } from '@/core/object-value/language/LanguageTypes.ts';
import type { ITagDTO } from '@/core/object-value/tag/TagTypes.ts';

export default {
  migrate: migrator.migrate,
  getTree: serverFiles.getTree,
  setSettings: serverSettings.setSettings,
  getSettings: serverSettings.getSettings,
  addParser: (v: IParserDTO) => ParsersServer.addItem(v),
  setParser: (v: IParserDTO) => ParsersServer.setItem(v),
  delParser: (id: number) => ParsersServer.delItem(id),
  getParser: (id: number) => ParsersServer.getItem(id),
  getParsersAll: () => ParsersServer.getItems(),
  addTag: (v: ITagDTO) => TagsServer.addItem(v),
  setTag: (v: ITagDTO) => TagsServer.setItem(v),
  delTag: (id: number) => TagsServer.delItem(id),
  getTag: (id: number) => TagsServer.getItem(id),
  getTagsAll: () => TagsServer.getItems(),
  addAuthor: (v: IAuthorDTO) => AuthorsServer.addItem(v),
  setAuthor: (v: IAuthorDTO) => AuthorsServer.setItem(v),
  delAuthor: (id: number) => AuthorsServer.delItem(id),
  getAuthor: (id: number) => AuthorsServer.getItem(id),
  getAuthorsAll: () => AuthorsServer.getItems(),
  addLanguage: (v: ILanguageDTO) => LanguagesServer.addItem(v),
  setLanguage: (v: ILanguageDTO) => LanguagesServer.setItem(v),
  delLanguage: (id: number) => LanguagesServer.delItem(id),
  getLanguage: (id: number) => LanguagesServer.getItem(id),
  getLanguagesAll: () => LanguagesServer.getItems(),
  addComic: (v: IComicDTO) => ComicsServer.addItem(v),
  setComic: (v: IComicDTO) => ComicsServer.setItem(v),
  delComic: (id: number) => ComicsServer.delItem(id),
  getComic: (id: number) => ComicsServer.getItem(id),
  getComicAll: () => ComicsServer.getItems(),
  addComicCover: (id: number, f: File) => ComicsServer.addCover(id, f),
  delComicCover: (id: number) => ComicsServer.delCover(id),
  resizeComicCover: (id: number, o: Partial<IResizeOptions>) => ComicsServer.resizeCover(id, o),
  addComicFile: (id: number, f: File) => ComicsServer.addImage(id, f),
  setComicFile: (id: number, fId: number, f: File) => ComicsServer.setImage(id, fId, f),
  delComicFile: (id: number, f: number) => ComicsServer.delImage(id, f),
  delComicFiles: (id: number) => ComicsServer.delImages(id),
  resizeComicFile: (id: number, f: number, o: Partial<IResizeOptions>) => ComicsServer.resizeImage(id, f, o),
  autoBackup: serverBackups.autoBackup,
  delBackup: serverBackups.delBackup,
  addBackup: serverBackups.addBackup,
  getBackup: serverBackups.getBackup,
};
