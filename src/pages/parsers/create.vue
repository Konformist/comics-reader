<template>
  <v-app-bar>
    <v-btn
      icon="$arrow-left"
      slim
      :to="{ name: '/parsers/' }"
    />
    <v-app-bar-title>
      Создание парсера
    </v-app-bar-title>
  </v-app-bar>
  <v-main>
    <v-container>
      <v-text-field
        v-model="parser.name"
        label="Название парсера"
      />
      <v-text-field
        v-model="parser.title"
        label="Название комикса"
      />
      <v-text-field
        v-model="parser.image"
        label="Изображение"
      />
      <div class="d-flex">
        <v-text-field
          v-model="parser.authors"
          class="mr-4"
          label="Авторы"
        />
        <v-text-field
          v-model="parser.authorsText"
          label="Текст авторов"
        />
      </div>
      <v-text-field
        v-model="parser.language"
        label="Язык"
      />
      <div class="d-flex">
        <v-text-field
          v-model="parser.tags"
          class="mr-4"
          label="Теги"
        />
        <v-text-field
          v-model="parser.tagsText"
          label="Текст тега"
        />
      </div>
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

const router = useRouter();
const appStore = useAppStore();

const parser = ref(new ParserModel());

const loading = ref(false);

const onCreate = async () => {
  loading.value = true;
  await appStore.createParser(parser.value);
  await router.push({
    name: '/parsers/[id]',
    params: { id: parser.value.id },
  })
};
</script>
