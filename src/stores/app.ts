import type { IComicDTO } from '@/core/entities/comic/ComicTypes.ts';
import type { IParserDTO } from '@/core/entities/parser/ParserTypes.ts';
import { dedupe } from '@/core/utils/array.ts';
import { Preferences } from '@capacitor/preferences';
import { defineStore } from 'pinia'
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';

const PARSERS_STORE = 'parsers';
const PARSERS_VERSION = 1;
const COMICS_STORE = 'comics';
const COMICS_VERSION = 2;

export const useAppStore = defineStore('app', {
  state: () => ({
    parsers: [] as ParserModel[],
    comics: [] as ComicModel[],

    languages: [] as string[],
    tags: [] as string[],
    authors: [] as string[],
  }),

  actions: {
    async createParser (value: ParserModel) {
      const lastId = this.parsers[this.parsers.length - 1]?.id || 0;

      value.id = 1 + lastId;

      this.parsers.push(value);
      await this.saveParsers();
    },

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    migrateParsersStore (value: any) {
      return value;
    },

    async saveParsers () {
      await Preferences.set({
        key: PARSERS_STORE,
        value: JSON.stringify({
          version: PARSERS_VERSION,
          items: this.parsers.map(e => e.getDTO()),
        }),
      })
    },

    async loadParsers () {
      const store = await Preferences.get({ key: PARSERS_STORE });

      if (!store.value) return;

      let result = JSON.parse(store.value);

      if (result.version !== PARSERS_VERSION) {
        result = this.migrateParsersStore(result)
        this.parsers = result.items
          .map((e: IParserDTO) => new ParserModel(e));
        await this.saveParsers();
      } else {
        this.parsers = result.items
          .map((e: IParserDTO) => new ParserModel(e));
      }
    },

    async saveComics () {
      await Preferences.set({
        key: COMICS_STORE,
        value: JSON.stringify({
          version: COMICS_VERSION,
          items: this.comics.map(e => e.getDTO()),
        }),
      })

      this.updateItems();
    },

    updateItems () {
      this.languages = dedupe(this.comics.map(e => e.language));
      this.tags = dedupe(this.comics.map(e => e.tags).flat(1));
      this.authors = dedupe(this.comics.map(e => e.authors).flat(1));
    },

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    migrateComicsStore (value: any) {
      if (value.version < COMICS_VERSION) {
        console.log(value);
        value.items.forEach((item: IComicDTO) => {
          // @ts-expect-error parsing
          item.images = item.images.map((e: string, i) => ({
            id: i + 1,
            url: e,
            // @ts-expect-error parsing
            from: item.imagesUrl[i],
          }))
        })
      }

      return value;
    },

    async loadComics () {
      const store = await Preferences.get({ key: COMICS_STORE });

      if (!store.value) return

      let result = JSON.parse(store.value);

      if (result.version !== COMICS_VERSION) {
        result = this.migrateComicsStore(result)
        this.comics = result.items
          .map((e: IComicDTO) => new ComicModel(e));
        await this.saveComics();
      } else {
        this.comics = result.items
          .map((e: IComicDTO) => new ComicModel(e));
      }

      this.updateItems();
    },

    async loadData () {
      await this.loadParsers();
      await this.loadComics();
    },
  },
});
