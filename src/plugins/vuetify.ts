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
  mdiFilterVariant,
  mdiFolder,
  mdiFolderOpen,
  mdiHome,
  mdiInformation,
  mdiMenu,
  mdiPencil,
  mdiPlus, mdiSort,
  mdiTag,
  mdiTranslate, mdiTriangleDown,
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
      'filter': mdiFilterVariant,
      'sort': mdiSort,
      'check': mdiCheckBold,
      'delete': mdiDelete,
      'info': mdiInformation,
      'close': mdiClose,
      'dev': mdiDevTo,
      'read': mdiBookOpenPageVariant,
      'triangleDown': mdiTriangleDown,
    },
    sets: { mdi },
  },
  theme: {
    defaultTheme: 'dark',
    themes: {
      dark: {
        dark: true,
        colors: {
          'background': '#121212',
          'primary': '#00bb00',
          'on-primary': '#000',
          'secondary': '#770000',
        },
      },
    },
  },
  defaults: {
    VDialog: { scrim: '#121212' },
    VBottomSheet: { scrim: '#121212' },
    VAppBar: { elevation: '2' },
    VFab: {
      app: true,
      size: 'large',
    },
    VBtn: { size: 'large' },
    VBtnToggle: { color: 'primary' },
    VAlert: { rounded: 'xl' },
    VPagination: { rounded: true },
    VBottomNavigation: { elevation: '2' },
    VCard: { rounded: 'xl' },
    VChip: {
      color: 'primary',
      rounded: true,
    },
    VField: {
      color: 'primary',
      rounded: 'xl',
      variant: 'solo',
      hideDetails: 'auto',
    },
    VNumberInput: {
      variant: 'solo',
      controlVariant: 'hidden',
      hideDetails: 'auto',
    },
    VTextField: {
      clearable: true,
      persistentClear: true,
      variant: 'solo',
      hideDetails: 'auto',
    },
    VTextarea: {
      clearable: true,
      persistentClear: true,
      variant: 'solo',
      hideDetails: 'auto',
      persistentHint: true,
    },
    VFileInput: {
      prependIcon: '',
      prependInnerIcon: '$download',
      variant: 'solo',
      hideDetails: 'auto',
    },
    VList: {
      class: ['px-2', 'py-1'],
      color: 'primary',
      rounded: 'xl',
      itemTitle: 'name',
      itemValue: 'id',
    },
    VListItem: {
      slim: true,
      color: 'primary',
      rounded: 'xl',
      class: ['my-1'],
      itemTitle: 'name',
      itemValue: 'id',
    },
    VSwitch: {
      inset: true,
      hideDetails: true,
      color: 'primary',
    },
  },
});
