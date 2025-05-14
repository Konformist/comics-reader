import { version } from '../../package.json';
import { defineStore } from 'pinia';

export const useAppStore = defineStore('app', {
  state: () => ({
    frontVersion: version,
  }),

  actions: {
  },
});
