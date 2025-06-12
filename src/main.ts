/**
 * main.ts
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Styles
import 'swiper/css';
import 'swiper/css/zoom';
import 'unfonts.css';
import '@/styles/styles.scss';

// Plugins
import { registerPlugins } from '@/plugins';
import { useAppStore } from '@/stores/app.ts';

// Components
import App from '@/App.vue';

// Composables
import { createApp } from 'vue';

const init = async () => {
  const app = createApp(App);

  registerPlugins(app);

  await useAppStore().initApp();

  app.mount('#app');
};

init();
