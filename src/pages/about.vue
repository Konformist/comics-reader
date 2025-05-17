<template>
  <v-main>
    <v-container class="pa-0">
      <div class="px-4 py-8">
        <p class="d-flex justify-space-between">
          <span class="font-weight-medium">Версия FrontEnd</span>
          <span>v{{ appStore.frontVersion }}</span>
        </p>
      </div>
    </v-container>
  </v-main>
</template>

<script lang="ts" setup>
import { useAppStore } from '@/stores/app.ts';

definePage({
  meta: {
    title: 'О приложении',
  },
});

const appStore = useAppStore();
</script>
