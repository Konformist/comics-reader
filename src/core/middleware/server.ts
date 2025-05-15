import serverBackups from '@/core/middleware/serverBackups.ts';
import serverComics from '@/core/middleware/serverComics.ts';
import serverFiles from '@/core/middleware/serverFiles.ts';
import serverParsers from '@/core/middleware/serverParsers.ts';
import serverSettings from '@/core/middleware/serverSettings.ts';
// import serverTags from '@/core/middleware/serverTags.ts';

export default {
  getTree: serverFiles.getTree,
  setSettings: serverSettings.setSettings,
  getSettings: serverSettings.getSettings,
  addParser: serverParsers.addParser,
  setParser: serverParsers.setParser,
  delParser: serverParsers.delParser,
  getParser: serverParsers.getParser,
  getParsersAll: serverParsers.getParsersAll,
  // addTag: serverTags.addTag,
  // setTag: serverTags.setTag,
  // delTag: serverTags.delTag,
  // getTag: serverTags.getTag,
  // getTagsAll: serverTags.getTagsAll,
  addComic: serverComics.addComic,
  setComic: serverComics.setComic,
  delComic: serverComics.delComic,
  getComic: serverComics.getComic,
  getComicAll: serverComics.getComicAll,
  addComicCover: serverComics.addComicCover,
  delComicCover: serverComics.delComicCover,
  resizeComicCover: serverComics.resizeComicCover,
  addComicFile: serverComics.addComicFile,
  setComicFile: serverComics.setComicFile,
  delComicFile: serverComics.delComicFile,
  delComicFiles: serverComics.delComicFiles,
  resizeComicFile: serverComics.resizeComicFile,
  autoBackup: serverBackups.autoBackup,
  setBackup: serverBackups.setBackup,
  getBackup: serverBackups.getBackup,
};
