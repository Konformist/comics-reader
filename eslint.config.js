import { defineConfig } from 'vite';
import vuetifyPlugin from 'eslint-config-vuetify/index.ts.mjs'

export default defineConfig([
  ...vuetifyPlugin,
  {
    rules: {
      'vue/script-indent': ['error', 2],
    }
  },
])
