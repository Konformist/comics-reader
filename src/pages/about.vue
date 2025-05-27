<template>
  <v-main>
    <v-container class="pa-0">
      <p class="pa-4 d-flex justify-space-between">
        <span class="font-weight-medium">Версия приложения</span>
        <span>v{{ appStore.info.androidVersion || '—' }}</span>
      </p>
      <v-divider />
      <p class="pa-4 d-flex justify-space-between">
        <span class="font-weight-medium">Версия FrontEnd</span>
        <span>v{{ appStore.info.frontVersion }}</span>
      </p>
    </v-container>
  </v-main>
</template>

<script lang="ts" setup>
import { useAppStore } from '@/stores/app.ts';

definePage({
  meta: {
    layout: 'full',
    title: 'О приложении',
  },
});

const appStore = useAppStore();
</script>
