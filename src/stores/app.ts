import SettingsController from '@/core/entities/settings/SettingsController.ts';
import SettingsModel from '@/core/entities/settings/SettingsModel.ts';
import { App } from '@capacitor/app';
import { Capacitor } from '@capacitor/core';
import { version } from '../../package.json';
import { defineStore } from 'pinia';

export const useAppStore = defineStore('app', {
  state: () => ({
    drawer: false,
    frontVersion: version,
    settings: new SettingsModel(),
    info: {
      frontVersion: version,
      androidVersion: '',
    },
  }),

  actions: {
    async saveSettings() {
      await SettingsController.save(this.settings);
    },

    async loadSettings() {
      this.settings = await SettingsController.load();
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
