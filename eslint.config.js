import { defineConfig } from 'eslint/config';
import stylistic from '@stylistic/eslint-plugin';
import vuetifyPlugin from 'eslint-config-vuetify/index.ts.mjs';

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
      '@stylistic/comma-style': ['error', 'last'],
      '@stylistic/dot-location': ['error', 'property'],
      '@stylistic/arrow-spacing': 'error',
      '@stylistic/arrow-parens': ['error', 'always'],
      '@stylistic/no-confusing-arrow': 'error',
      '@stylistic/semi': 'error',
      '@stylistic/space-before-function-paren': ['error', {
        'anonymous': 'always',
        'named': 'never',
        'asyncArrow': 'always',
      }],
      '@stylistic/space-infix-ops': 'error',
      '@stylistic/template-curly-spacing': 'error',
      '@stylistic/type-annotation-spacing': 'error',
      '@stylistic/multiline-ternary': ['error', 'always-multiline'],
      '@stylistic/no-mixed-operators': 'error',

      'vue/script-indent': ['error', 2],
    },
  },
]);
