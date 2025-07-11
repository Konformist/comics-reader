import type AuthorModel from '@/core/entities/author/AuthorModel.ts';
import AuthorController from '@/core/entities/author/AuthorController.ts';

export const useAuthorsStore = defineStore('authorsStore', {
  state() {
    return {
      authors: [] as AuthorModel[],
      loaded: false,
    };
  },

  actions: {
    async loadAuthors() {
      if (this.loaded) {
        return;
      }
      this.authors = await AuthorController.loadAll();
      this.loaded = true;
    },

    async loadAuthorsForce() {
      this.authors = await AuthorController.loadAll();
    },
  },
});
