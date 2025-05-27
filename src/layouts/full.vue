<template>
  <v-app-bar
    class="rounded-b-xl"
    flat
    scroll-behavior="hide"
    :title="($route.meta?.title as string) || ''"
  >
    <template #prepend>
      <v-btn
        :active="appStore.drawer"
        icon="$menu"
        rounded="pill"
        @click="appStore.drawer = !appStore.drawer"
      />
    </template>
  </v-app-bar>
  <router-view />
  <BottomNavigation />
</template>

<script lang="ts" setup>
import { useAppStore } from '@/stores/app.ts';

const appStore = useAppStore();
</script>
