/**
 * plugins/index.ts
 *
 * Automatically included in `./src/main.ts`
 */
import { App as AppPlugin } from '@capacitor/app';

// Types
import type { App } from 'vue';
import router from '../router';
import pinia from '../stores';

// Plugins
import vuetify from './vuetify';

AppPlugin.addListener('backButton', (event) => {
  if (event.canGoBack) {
    router.back();
  }
});

export function registerPlugins (app: App) {
  app
    .use(vuetify)
    .use(router)
    .use(pinia);
}
