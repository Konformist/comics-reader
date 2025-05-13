import { defineConfig } from 'eslint/config';
import vuetifyPlugin from 'eslint-config-vuetify/index.ts.mjs'

export default defineConfig([
  {
    ignores: ['**/android/**'],
  },
  ...vuetifyPlugin,
  {
    languageOptions: {
      globals: {
        definePage: 'readonly',
      },
    },
    rules: {
      'vue/script-indent': ['error', 2],
    },
  },
])
