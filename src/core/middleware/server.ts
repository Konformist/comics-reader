import type { IComicDTO } from '@/core/entities/comic/ComicTypes.ts';
import type { IDirectory, IFile } from '@/core/entities/file/FileTypes.ts';
import type { IParserDTO } from '@/core/entities/parser/ParserTypes.ts';
import {
  BACKUPS_DIRECTORY,
  COMICS_FILES_DIRECTORY,
  COMICS_STORE,
  COMICS_VERSION,
  PARSERS_STORE,
  PARSERS_VERSION,
} from '@/core/middleware/variables.ts';
import { fileToBase64, getFileUrl, optimizeImage } from '@/core/utils/image.ts';
import { ImageManipulator } from '@capacitor-community/image-manipulator';
import { Directory, Encoding, Filesystem } from '@capacitor/filesystem';
import { Preferences } from '@capacitor/preferences';

/// Parsers

let parsersRaw: IParserDTO[] = [];

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const migrationParsers = (value: any) => {
  return value;
}

const setParsersData = async () => {
  await Preferences.set({
    key: PARSERS_STORE,
    value: JSON.stringify({
      version: PARSERS_VERSION,
      items: parsersRaw,
    }),
  })
}

const getParsersData = async () => {
  const store = await Preferences.get({ key: PARSERS_STORE });

  if (!store.value) return;

  let result = JSON.parse(store.value);

  if (result.version !== PARSERS_VERSION) {
    result = migrationParsers(result)
  }

  parsersRaw = result.items

  if (result.version !== PARSERS_VERSION) {
    await setParsersData();
  }
}

const addParser = async (data: IParserDTO) => {
  await getParsersData();
  const parserId = Math.max(...parsersRaw.map(e => e.id), 0) + 1;

  parsersRaw.push({ ...data, id: parserId });
  await setParsersData();

  return parserId;
}

const setParser = async (data: IParserDTO): Promise<void> => {
  const index = parsersRaw.findIndex(e => e.id === data.id);
  parsersRaw.splice(index, 1, data);
  await setParsersData();
}

const delParser = async (id: number): Promise<void> => {
  parsersRaw = parsersRaw.filter(e => e.id !== id);
  await setParsersData();
}

const getParser = async (id: number): Promise<IParserDTO|undefined> => {
  const ids = parsersRaw.map(e => e.id)

  if (!ids.includes(id)) await getParsersData();

  return parsersRaw.find(e => e.id === id);
}

const getParsersAll = async (): Promise<Array<IParserDTO>> => {
  await getParsersData();

  return [...parsersRaw];
}

/// Files

const addFile = async (path: string, file: File): Promise<string> => {
  const ret = await Filesystem.writeFile({
    path,
    directory: Directory.Data,
    data: await fileToBase64(file),
    recursive: true,
  });

  return getFileUrl(ret.uri);
}

const delFile = (path: string): Promise<void> => {
  return Filesystem.deleteFile({
    path,
    directory: Directory.Data,
  });
}

const resizeImage = async (
  path: string,
  options: {
    maxWidth?: number,
    maxHeight?: number,
  },
): Promise<string> => {
  const result = await ImageManipulator.resize({
    imagePath: path,
    maxWidth: options.maxWidth,
    maxHeight: options.maxHeight,
  });

  await Filesystem.rename({
    from: result.imagePath,
    to: path,
  })

  return getFileUrl(path);
}

const getTreeRecursive = async (path: string): Promise<Array<IDirectory|IFile>> => {
  const ret: Array<IDirectory|IFile> = [];

  try {
    const result = await Filesystem.readdir({ path });

    for (const file of result.files) {
      if (file.type === 'directory') {
        ret.push({
          type: file.type,
          path: file.name,
          children: await getTreeRecursive(file.uri),
        })
      } else {
        ret.push({
          type: file.type,
          path: file.name,
          size: file.size,
        })
      }
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) { /* empty */ }

  return ret;
}

/**
 * @param path - путь папки
 */
const getTree = async (path: string): Promise<IDirectory[]> => {
  try {
    const rootUri = await Filesystem.getUri({ path, directory: Directory.Data })

    return [{
      type: 'directory',
      path,
      children: await getTreeRecursive(rootUri.uri),
    }];
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) {
    return [];
  }
}

/// Comics

let comicsRaw: IComicDTO[] = [];

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const migrationComics = async (value: any) => {
  return value;
}

const setComicsData = async () => {
  await Preferences.set({
    key: COMICS_STORE,
    value: JSON.stringify({
      version: COMICS_VERSION,
      items: comicsRaw,
    }),
  })
}

const getComicsData = async () => {
  const store = await Preferences.get({ key: COMICS_STORE });

  if (!store.value) return

  let result = JSON.parse(store.value);

  if (result.version !== COMICS_VERSION) {
    result = await migrationComics(result)
  }

  comicsRaw = result.items;

  if (result.version !== COMICS_VERSION) {
    await setComicsData();
  }
};

const addComic = async (data: IComicDTO): Promise<number> => {
  await getComicsData();
  const comicId = Math.max(...comicsRaw.map(e => e.id), 0) + 1;

  comicsRaw.push({ ...data, id: comicId });
  await setComicsData();

  return comicId
}

const setComic = async (data: IComicDTO): Promise<void> => {
  const index = comicsRaw.findIndex(e => e.id === data.id);
  comicsRaw.splice(index, 1, data);
  await setComicsData();
}

const getComic = async (id: number): Promise<IComicDTO|undefined> => {
  const ids = comicsRaw.map(e => e.id)

  if (!ids.includes(id)) await getComicsData();

  return comicsRaw.find(e => e.id === id);
}

const getComicAll = async (): Promise<Array<IComicDTO>> => {
  await getComicsData();

  return [...comicsRaw];
}

const addComicCover = async (comicId: number, file: File): Promise<void> => {
  const comic = await getComic(comicId);

  if (!comic) return;

  const optimizedFile = await optimizeImage(file);
  comic.image = await addFile(`${COMICS_FILES_DIRECTORY}/${comicId}/cover.webp`, optimizedFile);
  await setComicsData();
}

const delComicCover = async (comicId: number): Promise<void> => {
  const comic = await getComic(comicId);

  if (!comic) return;

  await delFile(`${COMICS_FILES_DIRECTORY}/${comicId}/cover.webp`);
  comic.image = '';
  await setComicsData();
}

const resizeComicCover = async (
  comicId: number,
  options: {
    maxWidth?: number,
    maxHeight?: number,
  },
) => {
  const comic = await getComic(comicId);

  if (!comic) return;

  const result = await Filesystem.getUri({
    path: `${COMICS_FILES_DIRECTORY}/${comicId}/cover.webp`,
    directory: Directory.Data,
  });

  comic.image = await resizeImage(result.uri, options);
  await setComicsData();
}

const addComicFile = async (comicId: number, file: File): Promise<void> => {
  const comic = await getComic(comicId);

  if (!comic) return undefined;

  const fileId = Math.max(...comic.images.map(e => e.id), 0) + 1;
  const optimizedFile = await optimizeImage(file);
  const uri = await addFile(`${COMICS_FILES_DIRECTORY}/${comicId}/${fileId}.webp`, optimizedFile);

  comic.images.push({
    id: fileId,
    url: uri,
    from: '',
  })

  await setComicsData();
};

const setComicFile = async (comicId: number, fileId: number, file: File) => {
  const comic = await getComic(comicId);

  if (!comic) return undefined;

  const image = comic.images.find(e => e.id === fileId);

  if (!image) return undefined;

  if (image.url) {
    try {
      await delFile(`${COMICS_FILES_DIRECTORY}/${comicId}/${fileId}.webp`);
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (_) { /* empty */ }
  }

  const optimizedFile = await optimizeImage(file);
  image.url = await addFile(`${COMICS_FILES_DIRECTORY}/${comicId}/${fileId}.webp`, optimizedFile);
  await setComicsData();
};

const delComicFile = async (comicId: number, fileId: number): Promise<void> => {
  const comic = await getComic(comicId);

  if (!comic) return;

  const file = comic.images.find(e => e.id === fileId);
  const fileIndex = comic.images.findIndex(e => e.id === fileId);

  if (!file) return;

  if (file.url) await delFile(`${COMICS_FILES_DIRECTORY}/${comicId}/${fileId}.webp`);
  comic.images.splice(fileIndex, 1);
  await setComicsData();
};

const delComicFiles = async (comicId: number): Promise<void> => {
  const comic = await getComic(comicId);

  if (!comic) return undefined;

  for (const item of comic.images) {
    if (item.url) await delFile(`${COMICS_FILES_DIRECTORY}/${comicId}/${item.id}.webp`);
  }
  comic.images = [];
  await setComicsData();
};

const resizeComicFile = async (
  comicId: number,
  fileId: number,
  options: {
    maxWidth?: number,
    maxHeight?: number,
  },
) => {
  const comic = await getComic(comicId);

  if (!comic) return;

  const file = comic.images.find(e => e.id === fileId);

  if (!file) return;

  const result = await Filesystem.getUri({
    path: `${COMICS_FILES_DIRECTORY}/${comicId}/${fileId}.webp`,
    directory: Directory.Data,
  });

  file.url = await resizeImage(result.uri, options);
  await setComicsData();
}

const delComic = async (id: number): Promise<void> => {
  const comic = await getComic(id);

  if (!comic) return;

  if (comic.image) await delComicCover(id);
  await delComicFiles(id);
  comicsRaw = comicsRaw.filter(e => e.id !== id);
  await setComicsData();
}

/// backups

const getBackupFileName = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = (now.getMonth() + 1).toString().padStart(2, '0');
  const day = (now.getDate() + 1).toString().padStart(2, '0');

  return `${year}-${month}-${day}.json`;
}

const setBackup = async (): Promise<void> => {
  await getComicsData();
  await getParsersData();

  await Filesystem.writeFile({
    path: `${BACKUPS_DIRECTORY}/${getBackupFileName()}`,
    directory: Directory.Data,
    encoding: Encoding.UTF8,
    recursive: true,
    data: JSON.stringify({
      parsers: {
        version: PARSERS_VERSION,
        items: parsersRaw,
      },
      comics: {
        version: COMICS_VERSION,
        items: comicsRaw,
      },
    }),
  })
}

const autoBackup = async () => {
  try {
    await Filesystem.readFile({
      path: `${BACKUPS_DIRECTORY}/${getBackupFileName()}`,
      directory: Directory.Data,
    });
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) {
    await setBackup();
  }
};

const getBackup = async (path: string): Promise<void> => {
  const result = await Filesystem.readFile({
    path,
    directory: Directory.Data,
    encoding: Encoding.UTF8,
  });

  if (!result) return;

  const parsedData = JSON.parse(result.data as string);

  parsersRaw = parsedData.parsers.items;
  await setParsersData();
  comicsRaw = parsedData.comics.items;
  await setComicsData();
}

export default {
  getTree,
  addParser,
  setParser,
  delParser,
  getParser,
  getParsersAll,
  addComic,
  setComic,
  delComic,
  getComic,
  getComicAll,
  addComicCover,
  delComicCover,
  resizeComicCover,
  addComicFile,
  setComicFile,
  delComicFile,
  delComicFiles,
  resizeComicFile,
  autoBackup,
  setBackup,
  getBackup,
}
