/**
 * plugins/vuetify.ts
 *
 * Framework documentation: https://vuetifyjs.com`
 */

// Styles
import 'vuetify/styles'

// Composables
import { createVuetify } from 'vuetify'
import { md3 } from 'vuetify/blueprints';
import { ru } from 'vuetify/locale';
import { aliases, mdi } from 'vuetify/iconsets/mdi-svg';
import { mdiArrowLeft, mdiContentSaveOutline, mdiPencilOutline, mdiPlus, mdiViewDashboardOutline, mdiViewListOutline } from '@mdi/js';

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
export default createVuetify({
  blueprint: md3,
  locale: {
    locale: 'ru',
    messages: { ru },
  },
  icons: {
    defaultSet: 'mdi',
    aliases: {
      ...aliases,
      'plus': mdiPlus,
      'arrow-left': mdiArrowLeft,
      'dashboard': mdiViewDashboardOutline,
      'list': mdiViewListOutline,
      'save': mdiContentSaveOutline,
      'edit': mdiPencilOutline,
    },
    sets: {
      mdi,
    },
  },
  theme: {
    defaultTheme: 'dark',
  },
})
