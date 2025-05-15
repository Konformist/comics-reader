import { Preferences } from '@capacitor/preferences';
import { DATABASE_STORE, DATABASE_VERSION } from '@/core/middleware/variables.ts';

const dataRaw: { item: number } = {
  item: 0,
};

const setVersionData = () => (
  Preferences.set({
    key: DATABASE_STORE,
    value: dataRaw.item.toString(),
  })
);

const getVersionData = async () => {
  const result = await Preferences.get({ key: DATABASE_STORE });

  if (!result.value) await setVersionData();
  else dataRaw.item = +result.value;
};

const setVersion = async (value: number) => {
  dataRaw.item = value;
  await setVersionData();
};

const getVersion = async () => {
  if (!dataRaw.item) await getVersionData();

  return dataRaw.item;
};

const migrate = async () => {
  await getVersionData();

  if (dataRaw.item === DATABASE_VERSION) return;
};

export default {
  migrate,
  dataRaw,
  setVersionData,
  getVersionData,
  setVersion,
  getVersion,
};
