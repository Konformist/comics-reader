/**
 * main.ts
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

import AuthorsServer from '@/core/middleware/AuthorsServer.ts';
import ComicsServer from '@/core/middleware/ComicsServer.ts';
import FilesServer from '@/core/middleware/FilesServer.ts';
import LanguagesServer from '@/core/middleware/LanguagesServer.ts';
import ParsersServer from '@/core/middleware/ParsersServer.ts';
import serverSettings from '@/core/middleware/serverSettings.ts';
import TagsServer from '@/core/middleware/TagsServer.ts';
// Plugins
import { registerPlugins } from '@/plugins';
import WebApi from '@/plugins/WebApiPlugin.ts';
import { useAppStore } from '@/stores/app.ts';
import { Preferences } from '@capacitor/preferences';

// Components
import App from './App.vue';

// Composables
import { createApp } from 'vue';

// Styles
import 'swiper/css';
import 'unfonts.css';
import '@/styles/styles.scss';

const init = async () => {
  const app = createApp(App);

  registerPlugins(app);

  const isMigrate = await Preferences.get({ key: 'isMigrate' });

  if (isMigrate.value !== 'true') {
    await Promise.all([
      serverSettings.getSettingsData(),
      AuthorsServer.getDatabase(),
      LanguagesServer.getDatabase(),
      TagsServer.getDatabase(),
      ComicsServer.getDatabase(),
      ParsersServer.getDatabase(),
      FilesServer.getDatabase(),
    ]);

    await WebApi.migrate({
      settings: serverSettings.dataRaw.item,
      parsers: ParsersServer.dataRaw,
      authors: AuthorsServer.dataRaw,
      languages: LanguagesServer.dataRaw,
      tags: TagsServer.dataRaw,
      comics: ComicsServer.dataRaw,
      files: FilesServer.dataRaw,
    });

    await Preferences.set({ key: 'isMigrate', value: 'true' });
  }

  await useAppStore().initApp();

  app.mount('#app');
};

init();
