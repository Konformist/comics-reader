/**
 * main.ts
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Plugins
import { registerPlugins } from '@/plugins'

// Components
import App from './App.vue'

// Composables
import { createApp } from 'vue'

// Styles
import 'unfonts.css'

import { useAppStore } from '@/stores/app.ts';

const init = async () => {
  const app = createApp(App)

  registerPlugins(app)

  const appStore = useAppStore();

  await appStore.loadData();
  app.mount('#app')
}

init();
