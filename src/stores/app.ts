import SettingsModel from '@/core/entities/settings/SettingsModel.ts';
import server from '@/core/middleware/server.ts';
import { version } from '../../package.json';
import { defineStore } from 'pinia';

export const useAppStore = defineStore('app', {
  state: () => ({
    frontVersion: version,
    settings: new SettingsModel(),
  }),

  actions: {
    async saveSettings() {
      await server.setSettings(this.settings.getDTO());
    },

    async loadSettings() {
      const result = await server.getSettings();

      this.settings = new SettingsModel(result);
    },
  },
});
