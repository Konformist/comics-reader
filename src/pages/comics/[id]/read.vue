<template>
  <v-app-bar>
    <v-btn
      icon="$arrow-left"
      slim
      :to="{ name: '/comics/[id]/' }"
    />
    <v-app-bar-title>
      {{ comic?.name }}
    </v-app-bar-title>
  </v-app-bar>
  <v-main>
    <v-container v-if="comic">
      <v-img
        v-for="(image, index) in comic.images"
        :key="image"
        :class="index ? 'mt-4' : ''"
        :src="Capacitor.convertFileSrc(image)"
      />
    </v-container>
  </v-main>
</template>
<script lang="ts" setup>
import { useAppStore } from '@/stores/app.ts';
import { Capacitor } from '@capacitor/core';

const route = useRoute('/comics/[id]/');
const appStore = useAppStore();

const comic = computed(() => (appStore.comics.find(e => e.id === +route.params.id)))
</script>
