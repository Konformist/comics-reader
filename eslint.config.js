import { defineConfig } from 'eslint/config';
import vuetifyPlugin from 'eslint-config-vuetify/index.ts.mjs'

export default defineConfig([
  {
    ignores: ['**/android/**'],
  },
  ...vuetifyPlugin,
  {
    rules: {
      'vue/script-indent': ['error', 2],
    },
  },
])
