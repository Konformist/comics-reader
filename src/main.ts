/**
 * main.ts
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

import server from '@/core/middleware/server.ts';
// Plugins
import { registerPlugins } from '@/plugins';
import { useAppStore } from '@/stores/app.ts';

// Components
import App from './App.vue';

// Composables
import { createApp } from 'vue';

// Styles
import 'unfonts.css';
import '@/styles/styles.scss';

const init = async () => {
  const app = createApp(App);

  registerPlugins(app);

  await server.autoBackup();
  await useAppStore().loadSettings();

  app.mount('#app');
};

init();
