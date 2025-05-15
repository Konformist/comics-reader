import { defineConfig } from 'eslint/config';
import stylistic from '@stylistic/eslint-plugin';
import vuetifyPlugin from 'eslint-config-vuetify/index.ts.mjs'

export default defineConfig([
  {
    ignores: ['**/android/**'],
  },
  ...vuetifyPlugin,
  {
    plugins: {
      '@stylistic': stylistic,
    },
    languageOptions: {
      globals: {
        definePage: 'readonly',
      },
    },
    rules: {
      '@stylistic/comma-dangle': ['error', 'always-multiline'],

      'vue/script-indent': ['error', 2],
    },
  },
])
