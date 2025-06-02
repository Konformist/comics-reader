<template>
  <v-app>
    <router-view />
    <NavigationDrawer
      v-model="appStore.drawer"
    />
    <v-progress-linear
      v-if="loadingGlobal"
      class="position-fixed top-0 left-0 right-0"
      indeterminate
      style="z-index: 1010"
    />
  </v-app>
</template>

<script setup lang="ts">
import useLoading from '@/composables/useLoading.ts';
import { useAppStore } from '@/stores/app.ts';

const appStore = useAppStore();
const { loadingGlobal } = useLoading();
</script>
