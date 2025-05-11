<template>
  <v-app>
    <router-view />
  </v-app>
</template>

<script lang="ts" setup>
import { useAppStore } from '@/stores/app.ts';

const appStore = useAppStore();

appStore.loadData();
</script>
