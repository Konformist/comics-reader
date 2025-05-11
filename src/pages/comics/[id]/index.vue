<template>
  <v-app-bar>
    <v-btn
      icon="$arrow-left"
      slim
      :to="{ name: '/' }"
    />
    <v-app-bar-title>
      {{ comic?.name }}
    </v-app-bar-title>
  </v-app-bar>
  <v-main>
    <v-container>
      <template v-if="comic">
        <v-img
          rounded
          :src="coverUrl"
        />
        <p
          v-if="comic.authors.length"
          class="mt-2 d-flex flex-wrap ga-1 align-center"
        >
          <b class="font-weight-medium">Авторы:</b>
          <v-chip
            v-for="author in comic.authors"
            :key="author"
            :text="author"
          />
        </p>
        <p
          v-if="comic.language"
          class="mt-2 d-flex flex-wrap ga-1 align-center"
        >
          <b class="font-weight-medium">Язык:</b>
          <v-chip
            :text="comic.language"
          />
        </p>
        <p
          v-if="comic.tags.length"
          class="mt-2 d-flex flex-wrap ga-1 align-center"
        >
          <b class="font-weight-medium">Теги:</b>
          <v-chip
            v-for="tag in comic.tags"
            :key="tag"
            :text="tag"
          />
        </p>
        <p class="mt-4">
          <v-btn
            class="w-100"
            text="Читать"
            :to="{ name: '/comics/[id]/read' }"
          />
        </p>
      </template>
    </v-container>
    <v-fab
      app
      class="mb-4"
      icon="$edit"
      :to="{ name: '/comics/[id]/edit' }"
    />
  </v-main>
</template>

<script lang="ts" setup>
import { useAppStore } from '@/stores/app.ts';
import { Capacitor } from '@capacitor/core';

const route = useRoute('/comics/[id]/');
const appStore = useAppStore();

const comic = computed(() => (appStore.comics.find(e => e.id === +route.params.id)))

const coverUrl = computed(() => (
  comic.value?.image
    ? Capacitor.convertFileSrc(comic.value.image)
    : ''
))
</script>
