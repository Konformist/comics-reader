<template>
  <v-main>
    <v-container class="pa-0 pb-16">
      <div class="pa-4">
        <v-alert
          class="mb-4"
          color="info"
          variant="tonal"
        >
          Переопределение настроек парсера.<br>
          Работают с DOM деревом. См. CSS.
        </v-alert>
        <CustomSelect
          v-if="parsersStore.parsers.length > 0"
          v-model="comic.parserId"
          class="mb-4"
          :items="parsersStore.parsers"
          label="Парсер"
          @update:model-value="loadParser()"
        />
        <v-textarea
          v-model.trim="comic.fromUrl"
          auto-grow
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="Ссылка на комикс"
          rows="2"
          @change="updateParser()"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-textarea
          v-model.trim="overrideData.titleCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на название"
          rows="2"
        />
        <v-textarea
          v-model.trim="overrideData.annotationCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на аннотацию"
          rows="2"
        />
        <v-textarea
          v-model.trim="overrideData.coverCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на изображение"
          rows="2"
        />
        <v-textarea
          v-model.trim="overrideData.authorsCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на авторов"
          rows="2"
        />
        <v-text-field
          v-model.trim="overrideData.authorsTextCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на текст авторов"
        />
        <v-textarea
          v-model.trim="overrideData.languageCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на текст язык"
          rows="2"
        />
        <v-textarea
          v-model.trim="overrideData.tagsCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на теги"
          rows="2"
        />
        <v-text-field
          v-model.trim="overrideData.tagsTextCSS"
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="CSS указатель на текст тегов"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-textarea
          v-model.trim="overrideData.chaptersCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на главы"
          rows="2"
        />
        <v-textarea
          v-model.trim="overrideData.chaptersTitleCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на название главы"
          rows="2"
        />
        <v-textarea
          v-model.trim="overrideData.pagesTemplateUrl"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="Шаблон ссылок для страниц"
          rows="2"
        />
        <v-textarea
          v-model.trim="overrideData.pagesCSS"
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на страницы"
          rows="2"
        />
        <v-textarea
          v-model.trim="overrideData.pagesImageCSS"
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="CSS указатель на изображение страницы"
          rows="2"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-textarea
          v-model="cookie"
          auto-grow
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="Cookie"
          rows="2"
        />
        <v-btn
          class="w-100"
          :disabled="!comic.parserId || !comic.fromUrl || loading || loadingGlobal"
          text="Загрузить"
          variant="tonal"
          @click="parseComic()"
        />
      </div>
    </v-container>
    <v-fab
      :disabled="loading || loadingGlobal"
      icon="$save"
      @click="onSave()"
    />
  </v-main>
</template>

<script setup lang="ts">
import type { IParseData } from '@/plugins/WebApiPlugin.ts';
import useLoading from '@/composables/useLoading.ts';
import ComicOverrideController from '@/core/entities/comic-override/ComicOverrideController.ts';
import ComicOverrideModel from '@/core/entities/comic-override/ComicOverrideModel.ts';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import UI from '@/plugins/UIPlugin.ts';
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useLanguagesStore } from '@/stores/languages.ts';
import { useParsersStore } from '@/stores/parsers.ts';
import { useTagsStore } from '@/stores/tags.ts';

definePage({
  meta: {
    layout: 'entity',
    title: 'Расширенные настройки',
  },
});

const route = useRoute('/comics/[id]/override-edit');
const router = useRouter();
const comicsStore = useComicsStore();
const parsersStore = useParsersStore();
const tagsStore = useTagsStore();
const authorsStore = useAuthorsStore();
const languagesStore = useLanguagesStore();

const {
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
} = useLoading();

const comicId = +(route.params.id || 0);
const cookie = ref('');

const comic = ref(new ComicModel());
const saveComic = async () => {
  await ComicController.save(comic.value);
  await comicsStore.loadComicsForce();
  comic.value = new ComicModel(comicsStore.comic.getDTO());
};

const parser = ref(new ParserModel());
const loadParser = async () => {
  if (!comic.value.parserId) return;
  parser.value = await ParserController.load(comic.value.parserId);
};

const comicOverride = ref(new ComicOverrideModel());
const loadComicOverride = async () => {
  comicOverride.value = await ComicOverrideController.load(comicId);
};

const saveComicOverride = async () => {
  await ComicOverrideController.save(comicOverride.value);
};

const onSave = async () => {
  try {
    loadingGlobalStart();
    await saveComic();
    await saveComicOverride();
    UI.toast({ text: 'Комикс сохранён' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};

const overrideData = computed<IParseData>(() => ({
  get titleCSS() {
    return comicOverride.value.titleCSS || parser.value.titleCSS;
  },
  set titleCSS(value) {
    comicOverride.value.titleCSS = value;
  },
  get annotationCSS() {
    return comicOverride.value.annotationCSS || parser.value.annotationCSS;
  },
  set annotationCSS(value) {
    comicOverride.value.annotationCSS = value;
  },
  get coverCSS() {
    return comicOverride.value.coverCSS || parser.value.coverCSS;
  },
  set coverCSS(value) {
    comicOverride.value.coverCSS = value;
  },
  get authorsCSS() {
    return comicOverride.value.authorsCSS || parser.value.authorsCSS;
  },
  set authorsCSS(value) {
    comicOverride.value.authorsCSS = value;
  },
  get authorsTextCSS() {
    return comicOverride.value.authorsTextCSS || parser.value.authorsTextCSS;
  },
  set authorsTextCSS(value) {
    comicOverride.value.authorsTextCSS = value;
  },
  get languageCSS() {
    return comicOverride.value.languageCSS || parser.value.languageCSS;
  },
  set languageCSS(value) {
    comicOverride.value.languageCSS = value;
  },
  get tagsCSS() {
    return comicOverride.value.tagsCSS || parser.value.tagsCSS;
  },
  set tagsCSS(value) {
    comicOverride.value.tagsCSS = value;
  },
  get tagsTextCSS() {
    return comicOverride.value.tagsTextCSS || parser.value.tagsTextCSS;
  },
  set tagsTextCSS(value) {
    comicOverride.value.tagsTextCSS = value;
  },
  get chaptersCSS() {
    return comicOverride.value.chaptersCSS || parser.value.chaptersCSS;
  },
  set chaptersCSS(value) {
    comicOverride.value.chaptersCSS = value;
  },
  get chaptersTitleCSS() {
    return comicOverride.value.chaptersTitleCSS || parser.value.chaptersTitleCSS;
  },
  set chaptersTitleCSS(value) {
    comicOverride.value.chaptersTitleCSS = value;
  },
  get pagesTemplateUrl() {
    return comicOverride.value.pagesTemplateUrl || parser.value.pagesTemplateUrl;
  },
  set pagesTemplateUrl(value) {
    comicOverride.value.pagesTemplateUrl = value;
  },
  get pagesCSS() {
    return comicOverride.value.pagesCSS || parser.value.pagesCSS;
  },
  set pagesCSS(value) {
    comicOverride.value.pagesCSS = value;
  },
  get pagesImageCSS() {
    return comicOverride.value.pagesImageCSS || parser.value.pagesImageCSS;
  },
  set pagesImageCSS(value) {
    comicOverride.value.pagesImageCSS = value;
  },
}));

const init = async () => {
  loadingStart();
  await comicsStore.loadComic(comicId);
  comic.value = new ComicModel(comicsStore.comic.getDTO());

  if (comic.value.id) {
    await Promise.all([
      parsersStore.loadParsers(),
      loadComicOverride(),
      loadParser(),
    ]);
  } else {
    router.replace({ name: '/' });
  }

  loadingEnd();
};

init();

const updateParser = () => {
  if (!comic.value.fromUrl) return;

  const item = parsersStore.parsers.find((e) => e.siteUrl && comic.value.fromUrl.includes(e.siteUrl));

  if (!item) return;

  comic.value.parserId = item.id;
  loadParser();
};

const parseComic = async () => {
  try {
    loadingGlobalStart();
    await ComicController.parse(comicId, cookie.value);
    await Promise.all([
      tagsStore.loadTagsForce(),
      authorsStore.loadAuthorsForce(),
      languagesStore.loadLanguagesForce(),
      comicsStore.loadComicsForce(),
    ]);
    UI.toast({ text: 'Комикс сохранён' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
