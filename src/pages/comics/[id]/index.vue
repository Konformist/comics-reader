<template>
  <v-app-bar>
    <v-btn
      icon="$arrow-left"
      slim
      @click="$router.back()"
    />
    <v-app-bar-title text="Комикс" />
  </v-app-bar>
  <v-main>
    <v-container>
      <ComicImage
        rounded
        :src="comic.image"
      />
      <h2
        v-if="comic.authors.length"
        class="mt-4 font-weight-medium"
      >
        {{ comic.name }}
      </h2>
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
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';

const route = useRoute('/comics/[id]/');

const comic = ref(new ComicModel());

const loadComic = async () => {
  const comicId = +route.params.id;

  if (!comicId) return;

  comic.value = await ComicController.load(comicId);
}

loadComic();
</script>
