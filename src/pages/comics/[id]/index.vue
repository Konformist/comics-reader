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
        <ComicImage
          rounded
          :src="comic.image"
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
        <p class="mt-2 d-flex flex-wrap ga-1 align-center">
          <b class="font-weight-medium">Страниц:</b>
          <v-chip :text="comic.images.length" />
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

const route = useRoute('/comics/[id]/');
const appStore = useAppStore();

const comic = computed(() => (appStore.comics.find(e => e.id === +route.params.id)))
</script>
