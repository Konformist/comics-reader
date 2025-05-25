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
  mdiArrowRight,
  mdiBookOpenPageVariant,
  mdiCheckBold,
  mdiClose,
  mdiCog,
  mdiContentCopy,
  mdiContentSave,
  mdiDelete,
  mdiDevTo,
  mdiDownload,
  mdiFile,
  mdiFileCode,
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
      'folder': mdiFolder,
      'folder-open': mdiFolderOpen,
      'file-image': mdiFileImage,
      'file-code': mdiFileCode,
      'file': mdiFile,
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
          background: '#121212',
          primary: '#00bb00',
          'on-primary': '#000',
          secondary: '#770000',
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
    VBtnToggle: {
      color: 'primary',
    },
    VAlert: {
      rounded: 'xl',
    },
    VPagination: {
      rounded: true,
    },
    VBottomNavigation: {
      elevation: '2',
    },
    VCard: {
      rounded: 'xl',
    },
    VChip: {
      rounded: true,
    },
    VFileInput: {
      rounded: true,
      prependIcon: '',
      prependInnerIcon: '$download',
      color: 'primary',
      hideDetails: true,
      variant: 'solo',
    },
    VTextField: {
      rounded: true,
      clearable: true,
      persistentClear: true,
      color: 'primary',
      variant: 'solo',
      hideDetails: true,
    },
    VTextarea: {
      rounded: true,
      clearable: true,
      persistentClear: true,
      color: 'primary',
      variant: 'solo',
      hideDetails: true,
    },
    VList: {
      class: ['px-2'],
      rounded: 'xl',
      itemTitle: 'name',
      itemValue: 'id',
    },
    VListItem: {
      rounded: 'xl',
      itemTitle: 'name',
      itemValue: 'id',
    },
    VSelect: {
      variant: 'solo',
      chips: true,
      itemTitle: 'name',
      itemValue: 'id',
      closableChips: true,
      hideDetails: true,
      menuProps: {
        scrim: true,
        locationStrategy: 'static',
        height: '100vh',
        width: '100vw',
        contentProps: {
          style: 'margin: 16px; width: calc(100vw - 32px); height: calc(100vh - 32px - 64px); max-height: inherit',
        },
      },
    },
    VNumberInput: {
      variant: 'solo',
      hideDetails: true,
      controlVariant: 'hidden',
    },
    VSwitch: {
      inset: true,
      hideDetails: true,
      color: 'primary',
    },
  },
});
