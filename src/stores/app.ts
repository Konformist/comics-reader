import SettingsModel from '@/core/entities/settings/SettingsModel.ts';
import server from '@/core/middleware/server.ts';
import { App } from '@capacitor/app';
import { Capacitor } from '@capacitor/core';
import { version } from '../../package.json';
import { defineStore } from 'pinia';

export const useAppStore = defineStore('app', {
  state: () => ({
    frontVersion: version,
    settings: new SettingsModel(),
    info: {
      frontVersion: version,
      androidVersion: '',
    },
  }),

  actions: {
    async saveSettings() {
      await server.setSettings(this.settings.getDTO());
    },

    async loadSettings() {
      const result = await server.getSettings();

      this.settings = new SettingsModel(result);
    },

    async initApp() {
      await this.loadSettings();

      if (Capacitor.isNativePlatform()) {
        const appInfo = await App.getInfo();
        this.info.androidVersion = appInfo.version;
      }
    },
  },
});
