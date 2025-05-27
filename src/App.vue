<template>
  <v-app>
    <router-view v-slot="{ Component, route }">
      <keep-alive :include="['/']">
        <component
          :is="Component"
          :key="route"
        />
      </keep-alive>
    </router-view>
    <NavigationDrawer
      v-model="appStore.drawer"
    />
    <v-progress-linear
      v-if="loadingGlobal"
      class="position-absolute top-0 left-0 right-0"
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
