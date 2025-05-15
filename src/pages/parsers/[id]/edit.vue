<template>
  <v-main scrollable>
    <v-container>
      <v-text-field
        v-model.trim="parser.name"
        label="Название парсера"
      />
      <v-text-field
        v-model.trim="parser.site"
        :autocapitalize="false"
        :autocomplete="false"
        inputmode="url"
        label="Ссылка на сайт"
      />
      <v-divider class="mb-8 mt-4" />
      <v-textarea
        v-model.trim="parser.title"
        :autocapitalize="false"
        :autocomplete="false"
        inputmode="url"
        label="CSS указатель на название"
        rows="2"
      />
      <v-textarea
        v-model.trim="parser.image"
        :autocapitalize="false"
        :autocomplete="false"
        inputmode="url"
        label="CSS указатель на изображение"
        rows="2"
      />
      <v-textarea
        v-model.trim="parser.authors"
        :autocapitalize="false"
        :autocomplete="false"
        inputmode="url"
        label="CSS указатель на авторов"
        rows="2"
      />
      <v-text-field
        v-model.trim="parser.authorsText"
        :autocapitalize="false"
        :autocomplete="false"
        inputmode="url"
        label="CSS указатель на текст авторов"
      />
      <v-textarea
        v-model.trim="parser.language"
        :autocapitalize="false"
        :autocomplete="false"
        inputmode="url"
        label="CSS указатель на текст язык"
        rows="2"
      />
      <v-textarea
        v-model.trim="parser.tags"
        :autocapitalize="false"
        :autocomplete="false"
        inputmode="url"
        label="CSS указатель на теги"
        rows="2"
      />
      <v-text-field
        v-model.trim="parser.tagsText"
        :autocapitalize="false"
        :autocomplete="false"
        inputmode="url"
        label="CSS указатель на текст тегов"
      />
      <v-textarea
        v-model.trim="parser.images"
        :autocapitalize="false"
        :autocomplete="false"
        inputmode="url"
        label="CSS указатель на страницы"
        rows="2"
      />
      <v-divider class="mb-8 mt-4" />
      <v-file-input
        v-model="file"
        accept="application/json"
        hide-details
        label="Добавить файл"
      />
      <v-btn
        class="mt-4 w-100"
        :disabled="!file"
        text="Загрузить из файла"
        @click="setParser()"
      />
    </v-container>
    <v-fab
      icon="$save"
      @click="saveParser()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import type { IParserDTO } from '@/core/entities/parser/ParserTypes.ts';
import { Toast } from '@capacitor/toast';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';

definePage({
  meta: {
    title: 'Редактирование парсера',
    isBack: true,
  },
});

const route = useRoute('/parsers/[id]/edit');

const parserId = +(route.params?.id);

const parser = ref(new ParserModel());

const loadParser = async () => {
  parser.value = await ParserController.load(parserId);
};

loadParser();

const saveParser = async () => {
  await ParserController.save(parser.value);
  Toast.show({ text: 'Парсер сохранён' });
};

const file = ref<File | null>(null);

const setParser = async () => {
  if (!file.value) return;

  try {
    const result = await file.value.text();
    const parsed = JSON.parse(result) as Partial<IParserDTO>;
    parsed.id = parser.value.id;
    parser.value = new ParserModel(parsed);
    Toast.show({ text: 'Данные получены' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  }
};
</script>
