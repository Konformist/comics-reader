import type { IComicDTO } from '@/core/entities/comic/ComicTypes.ts';
import type { IAuthorDTO } from '@/core/object-value/author/AuthorTypes.ts';
import type { ILanguageDTO } from '@/core/object-value/language/LanguageTypes.ts';
import type { ITagDTO } from '@/core/object-value/tag/TagTypes.ts';
import { dedupe, sortString } from '@/core/utils/array.ts';
import { Preferences } from '@capacitor/preferences';
import {
  AUTHORS_STORE,
  COMICS_STORE,
  DATABASE_STORE,
  DATABASE_VERSION, LANGUAGES_STORE,
  PARSERS_STORE,
  SETTINGS_STORE,
  TAGS_STORE,
} from '@/core/middleware/variables.ts';

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

  dataRaw.item = DATABASE_VERSION;
  await setVersionData();

  const settingsResult = await Preferences.get({ key: SETTINGS_STORE });

  if (settingsResult.value) {
    await Preferences.set({
      key: SETTINGS_STORE,
      value: JSON.stringify(JSON.parse(settingsResult.value)?.item || {
        autoReading: false,
        autoReadingTimeout: 10,
      }),
    });
  }

  const parsersResult = await Preferences.get({ key: PARSERS_STORE });

  if (parsersResult.value) {
    await Preferences.set({
      key: PARSERS_STORE,
      value: JSON.stringify(JSON.parse(parsersResult.value).items),
    });
  }

  const comicsResult = await Preferences.get({ key: COMICS_STORE });

  if (comicsResult.value) {
    const comics = JSON.parse(comicsResult.value).items;
    const tags: ITagDTO[] = dedupe(comics.map((e: IComicDTO) => e.tags)
      .flat(1))
      .sort((a, b) => sortString(a, b))
      .map((e, i) => ({ id: i + 1, name: e }));
    const authors: IAuthorDTO[] = dedupe(comics.map((e: IComicDTO) => e.authors)
      .flat(1))
      .sort((a, b) => sortString(a, b))
      .map((e, i) => ({ id: i + 1, name: e }));
    const languages: ILanguageDTO[] = dedupe(comics.map((e: IComicDTO) => e.language))
      .sort((a, b) => sortString(a, b))
      .map((e, i) => ({ id: i + 1, name: e }));

    comics.forEach((comic: IComicDTO) => {
      comic.tags = comic.tags.map((tag) => {
        // @ts-expect-error move string to number
        const tagObj = tags.find(((e) => e.name === tag)) as ITagDTO;

        return tagObj.id;
      });
      comic.authors = comic.authors.map((author) => {
        // @ts-expect-error move string to number
        const authorObj = authors.find(((e) => e.name === author)) as IAuthorDTO;

        return authorObj.id;
      });

      // @ts-expect-error move string to number
      comic.language = (languages.find(((e) => e.name === comic.language)) as ILanguageDTO).id;
    });

    await Preferences.set({
      key: COMICS_STORE,
      value: JSON.stringify(comics),
    });
    await Preferences.set({
      key: TAGS_STORE,
      value: JSON.stringify(tags),
    });
    await Preferences.set({
      key: AUTHORS_STORE,
      value: JSON.stringify(authors),
    });
    await Preferences.set({
      key: LANGUAGES_STORE,
      value: JSON.stringify(languages),
    });
  }
};

export default {
  migrate,
  dataRaw,
  setVersionData,
  getVersionData,
  setVersion,
  getVersion,
};
