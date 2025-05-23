/**
 * main.ts
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Styles
import 'swiper/css';
import 'unfonts.css';
import '@/styles/styles.scss';

import { Preferences } from '@capacitor/preferences';

// Plugins
import { registerPlugins } from '@/plugins';
import { useAppStore } from '@/stores/app.ts';

// Components
import App from '@/App.vue';

// Composables
import { createApp } from 'vue';

// @todo delete after install
Preferences.remove({ key: 'isMigrate' });
Preferences.remove({ key: 'version' });
Preferences.remove({ key: 'parsers' });
Preferences.remove({ key: 'tags' });
Preferences.remove({ key: 'authors' });
Preferences.remove({ key: 'languages' });
Preferences.remove({ key: 'comics' });
Preferences.remove({ key: 'files' });

const init = async () => {
  const app = createApp(App);

  registerPlugins(app);

  await useAppStore().initApp();

  app.mount('#app');
};

init();
