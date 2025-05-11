<template>
  <v-app-bar>
    <v-btn
      icon="$arrow-left"
      slim
      :to="{ name: '/parsers/' }"
    />
    <v-app-bar-title>
      Редактирование парсера
    </v-app-bar-title>
  </v-app-bar>
  <v-main>
    <v-container v-if="parser">
      <v-text-field
        v-model="parser.name"
        label="Название парсера"
      />
      <v-textarea
        v-model="parser.title"
        label="Название комикса"
        rows="3"
      />
      <v-textarea
        v-model="parser.image"
        label="Изображение"
        rows="3"
      />
      <v-textarea
        v-model="parser.authors"
        label="Авторы"
        rows="3"
      />
      <v-text-field
        v-model="parser.authorsText"
        label="Текст авторов"
      />
      <v-textarea
        v-model="parser.language"
        label="Язык"
        rows="3"
      />
      <v-textarea
        v-model="parser.tags"
        label="Теги"
        rows="3"
      />
      <v-text-field
        v-model="parser.tagsText"
        label="Текст тега"
      />
      <v-textarea
        v-model="parser.images"
        label="Страницы"
        rows="3"
      />
    </v-container>
    <v-fab
      app
      class="mb-4"
      icon="$save"
      @click="appStore.saveParsers()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import { useAppStore } from '@/stores/app.ts';

const route = useRoute('/parsers/[id]');

const appStore = useAppStore();
const parserId = +(route.params?.id);

const parser = computed(() => (appStore.parsers.find(e => e.id === parserId)))
</script>
