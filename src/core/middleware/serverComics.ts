import { Directory, Filesystem } from '@capacitor/filesystem';
import { Preferences } from '@capacitor/preferences';
import { optimizeImage } from '@/core/utils/image.ts';
import type { IComicDTO } from '@/core/entities/comic/ComicTypes.ts';
import serverFiles from '@/core/middleware/serverFiles.ts';
import { COMICS_FILES_DIRECTORY, COMICS_STORE, COMICS_VERSION } from '@/core/middleware/variables.ts';

let comicsRaw: IComicDTO[] = [];

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const migrationComics = async (value: any) => {
  return value;
};

const setComicsData = async () => {
  await Preferences.set({
    key: COMICS_STORE,
    value: JSON.stringify({
      version: COMICS_VERSION,
      items: comicsRaw,
    }),
  });
};

const getComicsData = async () => {
  const store = await Preferences.get({ key: COMICS_STORE });

  if (!store.value) return;

  let result = JSON.parse(store.value);

  if (result.version !== COMICS_VERSION) {
    result = await migrationComics(result);
  }

  comicsRaw = result.items || [];

  if (result.version !== COMICS_VERSION) {
    await setComicsData();
  }
};

const addComic = async (data: IComicDTO): Promise<number> => {
  await getComicsData();
  const comicId = Math.max(...comicsRaw.map((e) => e.id), 0) + 1;

  comicsRaw.push({ ...data, id: comicId });
  await setComicsData();

  return comicId;
};

const setComic = async (data: IComicDTO): Promise<void> => {
  const index = comicsRaw.findIndex((e) => e.id === data.id);
  comicsRaw.splice(index, 1, data);
  await setComicsData();
};

const getComic = async (id: number): Promise<IComicDTO | undefined> => {
  const ids = comicsRaw.map((e) => e.id);

  if (!ids.includes(id)) await getComicsData();

  return comicsRaw.find((e) => e.id === id);
};

const getComicAll = async (): Promise<Array<IComicDTO>> => {
  await getComicsData();

  return [...comicsRaw];
};

const addComicCover = async (comicId: number, file: File): Promise<void> => {
  const comic = await getComic(comicId);

  if (!comic) return;

  const optimizedFile = await optimizeImage(file);
  comic.image = await serverFiles.addFile(`${COMICS_FILES_DIRECTORY}/${comicId}/cover.webp`, optimizedFile);
  await setComicsData();
};

const delComicCover = async (comicId: number): Promise<void> => {
  const comic = await getComic(comicId);

  if (!comic) return;

  await serverFiles.delFile(`${COMICS_FILES_DIRECTORY}/${comicId}/cover.webp`);
  comic.image = '';
  await setComicsData();
};

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

  comic.image = await serverFiles.resizeImage(result.uri, options);
  await setComicsData();
};

const addComicFile = async (comicId: number, file: File): Promise<void> => {
  const comic = await getComic(comicId);

  if (!comic) return undefined;

  const fileId = Math.max(...comic.images.map((e) => e.id), 0) + 1;
  const optimizedFile = await optimizeImage(file);
  const uri = await serverFiles.addFile(`${COMICS_FILES_DIRECTORY}/${comicId}/${fileId}.webp`, optimizedFile);

  comic.images.push({
    id: fileId,
    url: uri,
    from: '',
  });

  await setComicsData();
};

const setComicFile = async (comicId: number, fileId: number, file: File) => {
  const comic = await getComic(comicId);

  if (!comic) return undefined;

  const image = comic.images.find((e) => e.id === fileId);

  if (!image) return undefined;

  if (image.url) {
    try {
      await serverFiles.delFile(`${COMICS_FILES_DIRECTORY}/${comicId}/${fileId}.webp`);
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (_) { /* empty */ }
  }

  const optimizedFile = await optimizeImage(file);
  image.url = await serverFiles.addFile(`${COMICS_FILES_DIRECTORY}/${comicId}/${fileId}.webp`, optimizedFile);
  await setComicsData();
};

const delComicFile = async (comicId: number, fileId: number): Promise<void> => {
  const comic = await getComic(comicId);

  if (!comic) return;

  const file = comic.images.find((e) => e.id === fileId);
  const fileIndex = comic.images.findIndex((e) => e.id === fileId);

  if (!file) return;

  if (file.url) await serverFiles.delFile(`${COMICS_FILES_DIRECTORY}/${comicId}/${fileId}.webp`);
  comic.images.splice(fileIndex, 1);
  await setComicsData();
};

const delComicFiles = async (comicId: number): Promise<void> => {
  const comic = await getComic(comicId);

  if (!comic) return undefined;

  for (const item of comic.images) {
    if (item.url) await serverFiles.delFile(`${COMICS_FILES_DIRECTORY}/${comicId}/${item.id}.webp`);
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

  const file = comic.images.find((e) => e.id === fileId);

  if (!file) return;

  const result = await Filesystem.getUri({
    path: `${COMICS_FILES_DIRECTORY}/${comicId}/${fileId}.webp`,
    directory: Directory.Data,
  });

  file.url = await serverFiles.resizeImage(result.uri, options);
  await setComicsData();
};

const delComic = async (id: number): Promise<void> => {
  const comic = await getComic(id);

  if (!comic) return;

  if (comic.image) await delComicCover(id);
  await delComicFiles(id);
  comicsRaw = comicsRaw.filter((e) => e.id !== id);
  await setComicsData();
};

export default {
  comicsRaw,
  setComicsData,
  getComicsData,
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
};
