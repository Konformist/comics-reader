import type { IParserDTO } from '@/core/entities/parser/ParserTypes.ts';
import { PARSERS_STORE, PARSERS_VERSION } from '@/core/middleware/variables.ts';
import { Preferences } from '@capacitor/preferences';

let parsersRaw: IParserDTO[] = [];

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const migrationParsers = (value: any) => {
  return value;
};

const setParsersData = async () => {
  await Preferences.set({
    key: PARSERS_STORE,
    value: JSON.stringify({
      version: PARSERS_VERSION,
      items: parsersRaw,
    }),
  });
};

const getParsersData = async () => {
  const store = await Preferences.get({ key: PARSERS_STORE });

  if (!store.value) return;

  let result = JSON.parse(store.value);

  if (result.version !== PARSERS_VERSION) {
    result = migrationParsers(result);
  }

  parsersRaw = result.items || [];

  if (result.version !== PARSERS_VERSION) {
    await setParsersData();
  }
};

const addParser = async (data: IParserDTO) => {
  await getParsersData();
  const parserId = Math.max(...parsersRaw.map((e) => e.id), 0) + 1;

  parsersRaw.push({ ...data, id: parserId });
  await setParsersData();

  return parserId;
};

const setParser = async (data: IParserDTO): Promise<void> => {
  const index = parsersRaw.findIndex((e) => e.id === data.id);
  parsersRaw.splice(index, 1, data);
  await setParsersData();
};

const delParser = async (id: number): Promise<void> => {
  parsersRaw = parsersRaw.filter((e) => e.id !== id);
  await setParsersData();
};

const getParser = async (id: number): Promise<IParserDTO | undefined> => {
  const ids = parsersRaw.map((e) => e.id);

  if (!ids.includes(id)) await getParsersData();

  return parsersRaw.find((e) => e.id === id);
};

const getParsersAll = async (): Promise<Array<IParserDTO>> => {
  await getParsersData();

  return [...parsersRaw];
};

export default {
  parsersRaw,
  setParsersData,
  getParsersData,
  addParser,
  setParser,
  delParser,
  getParser,
  getParsersAll,
};
