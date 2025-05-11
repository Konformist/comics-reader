<template>
  <v-app-bar>
    <v-btn
      icon="$arrow-left"
      slim
      @click="$router.back()"
    />
    <v-app-bar-title text="Редактирование парсера" />
  </v-app-bar>
  <v-main>
    <v-container v-if="parser">
      <v-text-field
        v-model.trim="parser.name"
        label="Название парсера"
      />
      <v-textarea
        v-model.trim="parser.title"
        label="Название комикса"
        rows="2"
      />
      <v-textarea
        v-model.trim="parser.image"
        label="Изображение"
        rows="2"
      />
      <v-textarea
        v-model.trim="parser.authors"
        label="Авторы"
        rows="2"
      />
      <v-text-field
        v-model.trim="parser.authorsText"
        label="Текст авторов"
      />
      <v-textarea
        v-model.trim="parser.language"
        label="Язык"
        rows="2"
      />
      <v-textarea
        v-model.trim="parser.tags"
        label="Теги"
        rows="2"
      />
      <v-text-field
        v-model.trim="parser.tagsText"
        label="Текст тега"
      />
      <v-textarea
        v-model.trim="parser.images"
        label="Страницы"
        rows="2"
      />
    </v-container>
    <v-fab
      app
      class="mb-4"
      icon="$save"
      @click="onSave()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import { useAppStore } from '@/stores/app.ts';
import { Toast } from '@capacitor/toast';

const route = useRoute('/parsers/[id]');

const appStore = useAppStore();
const parserId = +(route.params?.id);

const parser = computed(() => (appStore.parsers.find(e => e.id === parserId)))

const onSave = async () => {
  await appStore.saveParsers();
  Toast.show({ text: 'Парсер сохранён' });
}
</script>
