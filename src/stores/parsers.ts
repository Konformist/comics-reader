import type ParserModel from '@/core/entities/parser/ParserModel.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';

export const useParsersStore = defineStore('parsersStore', {
  state() {
    return {
      parsers: [] as ParserModel[],
      loaded: false,
    };
  },

  actions: {
    async loadParsers() {
      if (this.loaded) {
        return;
      }
      this.parsers = await ParserController.loadAll();
      this.loaded = true;
    },

    async loadParsersForce() {
      this.parsers = await ParserController.loadAll();
    },
  },
});
