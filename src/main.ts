/**
 * main.ts
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Composables
import { createApp } from 'vue';
// Components
import App from '@/App.vue';
// Plugins
import { registerPlugins } from '@/plugins';
import { useAppStore } from '@/stores/app.ts';

// Styles
import 'swiper/css';
import 'swiper/css/zoom';

import 'unfonts.css';

import '@/styles/styles.scss';

const init = async () => {
  const app = createApp(App);

  registerPlugins(app);

  await useAppStore().initApp();

  app.mount('#app');
};

init();
