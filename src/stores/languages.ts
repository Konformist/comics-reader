import type LanguageModel from '@/core/entities/language/LanguageModel.ts';
import LanguageController from '@/core/entities/language/LanguageController.ts';

export const useLanguagesStore = defineStore('languagesStore', {
  state() {
    return {
      languages: [] as LanguageModel[],
      loaded: false,
    };
  },

  actions: {
    async loadLanguages() {
      if (this.loaded) {
        return;
      }
      this.languages = await LanguageController.loadAll();
      this.loaded = true;
    },

    async loadLanguagesForce() {
      this.languages = await LanguageController.loadAll();
    },
  },
});
