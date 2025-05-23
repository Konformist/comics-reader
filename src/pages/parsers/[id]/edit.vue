<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <div class="px-4 py-8">
        <v-text-field
          v-model.trim="parser.name"
          label="Название парсера"
        />
        <v-text-field
          v-model.trim="parser.siteUrl"
          :autocapitalize="false"
          :autocomplete="false"
          hide-details
          inputmode="url"
          label="Ссылка на сайт"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-textarea
          v-model.trim="parser.titleCSS"
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="CSS указатель на название"
          rows="2"
        />
        <v-textarea
          v-model.trim="parser.coverCSS"
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="CSS указатель на изображение"
          rows="2"
        />
        <v-textarea
          v-model.trim="parser.authorsCSS"
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="CSS указатель на авторов"
          rows="2"
        />
        <v-text-field
          v-model.trim="parser.authorsTextCSS"
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="CSS указатель на текст авторов"
        />
        <v-textarea
          v-model.trim="parser.languageCSS"
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="CSS указатель на текст язык"
          rows="2"
        />
        <v-textarea
          v-model.trim="parser.tagsCSS"
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="CSS указатель на теги"
          rows="2"
        />
        <v-text-field
          v-model.trim="parser.tagsTextCSS"
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="CSS указатель на текст тегов"
        />
        <v-textarea
          v-model.trim="parser.pagesCSS"
          :autocapitalize="false"
          :autocomplete="false"
          hide-details
          inputmode="url"
          label="CSS указатель на страницы"
          rows="2"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-file-input
          v-model="file"
          accept="application/json"
          hide-details
          label="Добавить файл"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="!file || loadingGlobal"
          text="Загрузить из файла"
          @click="setParser()"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="loadingGlobal"
          text="Сохранить в Документы"
          @click="toDownloads()"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-btn
          class="w-100"
          color="error"
          :disabled="loadingGlobal"
          text="Удалить"
          @click="deleteParser()"
        />
      </div>
    </v-container>
    <v-fab
      :disabled="loadingGlobal"
      icon="$save"
      @click="saveParser()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import useLoading from '@/composables/useLoading.ts';
import type { IParserDTO } from '@/plugins/WebApiPlugin.ts';
import { APP_NAME } from '@/core/utils/variables.ts';
import { Dialog } from '@capacitor/dialog';
import { Directory, Encoding, Filesystem } from '@capacitor/filesystem';
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
const router = useRouter();
const {
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
} = useLoading();

const parserId = +(route.params?.id);

const parser = ref(new ParserModel());

const loadParser = async () => {
  parser.value = await ParserController.load(parserId);
};

loadParser();

onMounted(async () => {
  await loadParser();
  if (!parser.value.id) router.replace({ name: '/parsers/' });
});

const saveParser = async () => {
  try {
    loadingGlobalStart();
    await ParserController.save(parser.value);
    Toast.show({ text: 'Парсер сохранён' });
    loadParser();
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const file = ref<File | null>(null);

const setParser = async () => {
  if (!file.value) return;

  try {
    loadingGlobalStart();
    const result = await file.value.text();
    const parsed = JSON.parse(result) as Partial<IParserDTO>;
    parsed.id = parser.value.id;
    parser.value = new ParserModel(parsed);
    Toast.show({ text: 'Данные получены' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const toDownloads = async () => {
  try {
    loadingGlobalStart();
    const parserDTO = parser.value.getDTO();
    parserDTO.id = 0;

    await Filesystem.writeFile({
      path: `${APP_NAME}/parsers/${parser.value.name}.json`,
      directory: Directory.Documents,
      data: JSON.stringify(parserDTO),
      encoding: Encoding.UTF8,
      recursive: true,
    });
    Toast.show({ text: 'Парсер сохранён в документы' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const deleteParser = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить парсер?',
  });

  if (!value) return;

  try {
    loadingGlobalStart();
    await ParserController.remove(parser.value.id);
    Toast.show({ text: 'Парсер удалён' });
    router.replace({ name: '/parsers/' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
