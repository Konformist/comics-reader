import ComicController from '@/core/entities/comic/ComicController.ts';
import type ComicModel from '@/core/entities/comic/ComicModel.ts';

export const useComicsStore = defineStore('comicsStore', {
  state() {
    return {
      comics: [] as ComicModel[],
      loaded: false,
    };
  },

  actions: {
    async loadComics() {
      if (this.loaded) return;
      this.comics = await ComicController.loadAll();
      this.loaded = true;
    },

    async loadComicsForce() {
      this.comics = await ComicController.loadAll();
    },
  },
});
