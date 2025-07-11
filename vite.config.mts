import { fileURLToPath, URL } from 'node:url';
import Vue from '@vitejs/plugin-vue';
/// <reference types="vitest" />
// Plugins
import AutoImport from 'unplugin-auto-import/vite';
import Fonts from 'unplugin-fonts/vite';
import Components from 'unplugin-vue-components/vite';
import { VueRouterAutoImports } from 'unplugin-vue-router';
import VueRouter from 'unplugin-vue-router/vite';
// Utilities
import { defineConfig, loadEnv } from 'vite';

import Layouts from 'vite-plugin-vue-layouts-next';
import Vuetify, { transformAssetUrls } from 'vite-plugin-vuetify';

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd());

  Object.assign(process.env, env);

  return {
    test: {
      globals: true,
      environment: 'jsdom',
      server: { deps: { inline: ['vuetify'] } },
    },
    build: {
      cssCodeSplit: false,
      rollupOptions: { output: { manualChunks: (id) => (id.includes('node_modules') ? 'vendor' : 'index') } },
    },
    esbuild: { legalComments: 'none' },
    plugins: [
      VueRouter({ dts: 'src/typed-router.d.ts' }),
      Layouts(),
      AutoImport({
        imports: [
          'vue',
          VueRouterAutoImports,
          { pinia: ['defineStore', 'storeToRefs'] },
        ],
        dts: 'src/auto-imports.d.ts',
        eslintrc: { enabled: true },
        vueTemplate: true,
      }),
      Components({ dts: 'src/components.d.ts' }),
      Vue({
        template: {
          transformAssetUrls,
          compilerOptions: { isCustomElement: (tag) => ['swiper-container', 'swiper-slide'].includes(tag) },
        },
      }),
      // https://github.com/vuetifyjs/vuetify-loader/tree/master/packages/vite-plugin#readme
      Vuetify({
        autoImport: true,
        styles: { configFile: 'src/styles/settings.scss' },
      }),
      Fonts({
        custom: {
          families: [
            {
              name: 'Roboto',
              local: 'Roboto',
              src: [
                './node_modules/@fontsource/roboto/files/roboto-latin-400-normal.woff2',
                './node_modules/@fontsource/roboto/files/roboto-latin-500-normal.woff2',
                './node_modules/@fontsource/roboto/files/roboto-cyrillic-400-normal.woff2',
                './node_modules/@fontsource/roboto/files/roboto-cyrillic-500-normal.woff2',
              ],
            },
          ],
          display: 'swap',
          preload: true,
        },
      }),
    ],
    optimizeDeps: {
      exclude: [
        'vuetify',
        'vue-router',
        'unplugin-vue-router/runtime',
        'unplugin-vue-router/data-loaders',
        'unplugin-vue-router/data-loaders/basic',
      ],
    },
    define: { 'process.env': env },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('src', import.meta.url)),
        'tests': fileURLToPath(new URL('tests', import.meta.url)),
      },
      extensions: [
        '.js',
        '.json',
        '.jsx',
        '.mjs',
        '.ts',
        '.tsx',
        '.vue',
      ],
    },
    server: {
      port: 8080,
      proxy: { '^/api': { target: 'http://localhost:3000' } },
    },
    css: {
      preprocessorOptions: {
        sass: { api: 'modern-compiler' },
        scss: { api: 'modern-compiler' },
      },
    },
  };
});
