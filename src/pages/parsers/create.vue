<template>
  <v-app-bar>
    <v-btn
      icon="$arrow-left"
      slim
      @click="$router.back()"
    />
    <v-app-bar-title text="Создание парсера" />
  </v-app-bar>
  <v-main>
    <v-container>
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
      :loading="loading"
      @click="onCreate()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import { useAppStore } from '@/stores/app.ts';
import { Toast } from '@capacitor/toast';

const router = useRouter();
const appStore = useAppStore();

const parser = ref(new ParserModel());

const loading = ref(false);

const onCreate = async () => {
  loading.value = true;
  await appStore.createParser(parser.value);
  Toast.show({ text: 'Парсер создан' });
  await router.push({
    name: '/parsers/[id]',
    params: { id: parser.value.id },
  })
};
</script>
