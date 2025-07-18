<template>
  <v-main>
    <v-container class="pa-0 pb-16">
      <div class="pa-4">
        <v-text-field
          v-model.trim="parser.name"
          class="mb-4"
          label="Название парсера"
        />
        <v-text-field
          v-model.trim="parser.siteUrl"
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="Ссылка на сайт"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-textarea
          v-model.trim="parser.titleCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на название"
          rows="2"
        />
        <v-textarea
          v-model.trim="parser.annotationCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на аннотацию"
          rows="2"
        />
        <v-textarea
          v-model.trim="parser.coverCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на изображение"
          rows="2"
        />
        <v-textarea
          v-model.trim="parser.authorsCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на авторов"
          rows="2"
        />
        <v-text-field
          v-model.trim="parser.authorsTextCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на текст авторов"
        />
        <v-textarea
          v-model.trim="parser.languageCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на текст язык"
          rows="2"
        />
        <v-textarea
          v-model.trim="parser.tagsCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
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
      </div>
      <v-divider />
      <div class="pa-4">
        <v-textarea
          v-model.trim="parser.chaptersCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на главы"
          rows="2"
        />
        <v-textarea
          v-model.trim="parser.chaptersTitleCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на название главы"
          rows="2"
        />
        <v-textarea
          v-model.trim="parser.pagesTemplateUrl"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="Шаблон ссылок для страниц"
          rows="2"
        />
        <v-textarea
          v-model.trim="parser.pagesCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на страницы"
          rows="2"
        />
        <v-textarea
          v-model.trim="parser.pagesImageCSS"
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="CSS указатель на изображение страницы"
          rows="2"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-file-input
          accept="application/json"
          label="Загрузить конфигурацию"
          @update:model-value="setParser($event)"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="loadingGlobal"
          text="Сохранить в Загрузки"
          variant="tonal"
          @click="toDownloads()"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-btn
          class="w-100"
          color="error"
          :disabled="loadingGlobal"
          text="Удалить"
          variant="tonal"
          @click="deleteParser()"
        />
      </div>
    </v-container>
  </v-main>
  <v-fab
    app
    appear
    :disabled="loadingGlobal"
    icon="$save"
    @click="saveParser()"
  />
</template>

<script lang="ts" setup>
import { Dialog } from '@capacitor/dialog';
import useLoading from '@/composables/useLoading.ts';
import Api from '@/core/api/Api.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import UI from '@/plugins/UIPlugin.ts';
import { useParsersStore } from '@/stores/parsers.ts';

definePage({
  meta: {
    layout: 'entity',
    title: 'Редактирование парсера',
  },
});

const route = useRoute('/parsers/[id]/edit');
const router = useRouter();
const parsersStore = useParsersStore();

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

onMounted(async () => {
  await loadParser();
  if (!parser.value.id) router.replace({ name: '/parsers/' });
});

const saveParser = async () => {
  try {
    loadingGlobalStart();
    await ParserController.save(parser.value);
    await parsersStore.loadParsersForce();
    loadParser();
    UI.toast({ text: 'Парсер сохранён' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};

const setParser = async (value: File | File[]) => {
  const file = Array.isArray(value) ? value[0] : value;

  if (!file) return;

  try {
    loadingGlobalStart();
    const result = await file.text();
    const parsed = JSON.parse(result);
    parsed.id = parser.value.id;
    parser.value = new ParserModel(parsed);
    UI.toast({ text: 'Парсен обновлён' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};

const toDownloads = async () => {
  try {
    loadingGlobalStart();
    const parserDTO = parser.value.getDTO();
    parserDTO.id = 0;
    await Api.api('file/file/downloads', {
      file: JSON.stringify(parserDTO),
      fileName: `${parser.value.name}.json`,
    });
    UI.toast({ text: 'Парсер сохранён в Загрузки' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
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
    await parsersStore.loadParsersForce();
    UI.toast({ text: 'Парсер удалён' });
    router.replace({ name: '/parsers/' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
