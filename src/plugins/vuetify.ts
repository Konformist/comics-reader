/**
 * plugins/vuetify.ts
 *
 * Framework documentation: https://vuetifyjs.com`
 */

// Styles
import 'vuetify/styles';

// Composables
import { createVuetify } from 'vuetify';
import { md3 } from 'vuetify/blueprints';
import { ru } from 'vuetify/locale';
import { aliases, mdi } from 'vuetify/iconsets/mdi-svg';
import {
  mdiAccount,
  mdiArrowLeft,
  mdiArrowRight, mdiBookOpenPageVariant,
  mdiCheckBold, mdiClose,
  mdiCog,
  mdiContentCopy,
  mdiContentSave,
  mdiDelete, mdiDevTo,
  mdiDownload,
  mdiFileCode,
  mdiFileEye,
  mdiFileImage,
  mdiFilter,
  mdiFolder,
  mdiFolderOpen,
  mdiHome,
  mdiInformation,
  mdiMenu,
  mdiPencil,
  mdiPlus,
  mdiTag,
  mdiTranslate,
  mdiViewDashboard,
  mdiViewList,
} from '@mdi/js';

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
      'arrow-right': mdiArrowRight,
      'dashboard': mdiViewDashboard,
      'list': mdiViewList,
      'save': mdiContentSave,
      'edit': mdiPencil,
      'files': mdiFileEye,
      'folder': mdiFolder,
      'folder-open': mdiFolderOpen,
      'file-image': mdiFileImage,
      'file-code': mdiFileCode,
      'settings': mdiCog,
      'download': mdiDownload,
      'copy': mdiContentCopy,
      'menu': mdiMenu,
      'home': mdiHome,
      'tag': mdiTag,
      'author': mdiAccount,
      'language': mdiTranslate,
      'filter': mdiFilter,
      'check': mdiCheckBold,
      'delete': mdiDelete,
      'info': mdiInformation,
      'close': mdiClose,
      'dev': mdiDevTo,
      'read': mdiBookOpenPageVariant,
    },
    sets: {
      mdi,
    },
  },
  theme: {
    defaultTheme: 'dark',
    themes: {
      dark: {
        dark: true,
        colors: {
          background: '#122212',
          surface: '#1D2D1D',
          'surface-bright': '#FFFFFF',
          'surface-light': '#122212',
          'surface-variant': '#424242',
          'on-surface-variant': '#EEEEEE',
          primary: '#00A300',
          'primary-darken-1': '#1F5592',
          secondary: '#5e1b20',
          'secondary-darken-1': '#870186',
          error: '#B00020',
          info: '#2196F3',
          success: '#4CAF50',
          warning: '#FB8C00',
        },
      },
    },
  },
  defaults: {
    VAppBar: {
      elevation: '2',
    },
    VFab: {
      app: true,
      size: 'large',
    },
    VBottomNavigation: {
      elevation: '2',
    },
    VFileInput: {
      density: 'comfortable',
      prependIcon: '',
      prependInnerIcon: '$download',
      variant: 'solo',
    },
    VTextField: {
      density: 'comfortable',
      variant: 'solo',
    },
    VTextarea: {
      density: 'comfortable',
      variant: 'solo',
    },
    VSelect: {
      density: 'comfortable',
      variant: 'solo',
      chips: true,
      closableChips: true,
      menuProps: {
        scrim: true,
        locationStrategy: 'static',
        height: '100vh',
        width: '100vw',
        contentProps: {
          style: 'margin: 16px; width: calc(100vw - 32px); height: calc(100vh - 32px); max-height: inherit',
        },
      },
    },
    VCombobox: {
      density: 'comfortable',
      variant: 'solo',
      closableChips: true,
    },
    VNumberInput: {
      density: 'comfortable',
      variant: 'solo',
    },
  },
});
