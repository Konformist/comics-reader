import type ComicModel from '@/core/entities/comic/ComicModel.ts';
import { sortString } from '@/core/utils/array.ts';
import { filterArrays, filterString } from '@/core/utils/filters.ts';
import { useComicsStore } from '@/stores/comics.ts';

export enum ESORT_TYPE {
  NAME = 1,
  DATE,
}

export const useComicsPageStore = defineStore('comicsPage', {
  state() {
    return {
      scroll: 0,
      sort: ESORT_TYPE.NAME,
      invertSort: false,

      filters: {
        page: 1 as number,
        search: '',
        authors: [] as number[],
        languages: [] as number[],
        tags: [] as number[],
      },
    };
  },

  getters: {
    comicsFiltered(): ComicModel[] {
      return useComicsStore().comics.filter((item) => (
        filterString(item.name, this.filters.search)
        && filterArrays(item.tags, this.filters.tags)
        && filterArrays(item.authors, this.filters.authors)
        && filterArrays([item.languageId], this.filters.languages)
      ));
    },

    comicsSorted(): ComicModel[] {
      return [...this.comicsFiltered].sort((a, b) => {
        switch (this.sort) {
          case ESORT_TYPE.NAME: return sortString(a.name, b.name, this.invertSort);
          case ESORT_TYPE.DATE: return sortString(a.mdate, b.mdate, this.invertSort);
          default: return 0;
        }
      });
    },
  },
});
