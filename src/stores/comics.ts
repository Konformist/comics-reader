import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';

export const useComicsStore = defineStore('comicsStore', {
  state() {
    return {
      comicId: 0,
      comics: [] as ComicModel[],
      loaded: false,
    };
  },

  getters: {
    comic(): ComicModel {
      return this.comics.find((e) => e.id === this.comicId)
        || new ComicModel();
    },
  },

  actions: {
    async loadComic(id: number) {
      this.comicId = id;
      const ids = this.comics.map((comic) => comic.id);

      if (!ids.includes(id)) {
        this.loadComics();
      }
    },

    async loadComicForce(id: number) {
      const comic = await ComicController.load(id);
      const index = this.comics.findIndex((e) => e.id === comic.id);

      this.comics.splice(index, 1, comic);
    },

    async loadComics() {
      if (this.loaded) {
        return;
      }
      this.comics = await ComicController.loadAll();
      this.loaded = true;
    },

    async loadComicsForce() {
      this.comics = await ComicController.loadAll();
    },
  },
});
