import TagController from '@/core/entities/tag/TagController.ts';
import type TagModel from '@/core/entities/tag/TagModel.ts';

export const useTagsStore = defineStore('tagsStore', {
  state() {
    return {
      tags: [] as TagModel[],
      loaded: false,
    };
  },

  actions: {
    async loadTags() {
      if (this.loaded) return;
      this.tags = await TagController.loadAll();
      this.loaded = true;
    },

    async loadTagsForce() {
      this.tags = await TagController.loadAll();
    },
  },
});
