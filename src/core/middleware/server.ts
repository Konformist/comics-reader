import type { IComicDTO } from '@/core/entities/comic/ComicTypes.ts';
import type { IParserDTO } from '@/core/entities/parser/ParserTypes.ts';
import { COMICS_STORE, COMICS_VERSION, PARSERS_STORE, PARSERS_VERSION } from '@/core/utils/variables.ts';
import { Preferences } from '@capacitor/preferences';

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
    parsersRaw = result.items
    await setParsersData();
  } else {
    parsersRaw = result.items
  }
}

const addParser = async (data: IParserDTO) => {
  await getParsersData();
  const lastId = parsersRaw[parsersRaw.length - 1]?.id || 0;

  data.id = lastId;
  parsersRaw.push(data);
  await setParsersData();

  return lastId
}

const setParser = async (data: IParserDTO): Promise<void> => {
  parsersRaw.push(data);
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

let comicsRaw: IComicDTO[] = [];

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const migrationComics = (value: any) => {
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
    result = migrationComics(result)
    comicsRaw = result.items;
    await setComicsData();
  } else {
    comicsRaw = result.items;
  }
};

const addComic = async (data: IComicDTO): Promise<number> => {
  await getComicsData();
  const lastId = comicsRaw[comicsRaw.length - 1]?.id || 0;

  data.id = lastId;
  comicsRaw.push(data);
  await setComicsData();

  return lastId
}

const setComic = async (data: IComicDTO): Promise<void> => {
  comicsRaw.push(data);
  await setComicsData();
}

const delComic = async (id: number): Promise<void> => {
  comicsRaw = comicsRaw.filter(e => e.id !== id);
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

export default {
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
}
