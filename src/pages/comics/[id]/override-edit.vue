<template>
  <v-main>
    <v-container class="pb-16 mb-4">
      <v-alert
        class="mb-4"
        color="info"
        variant="tonal"
      >
        Переопределение настроек парсера.<br>
        Работают с DOM деревом. См. CSS.
      </v-alert>
      <CustomSelect
        v-if="parsersStore.parsers.length"
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
        class="mb-4"
        inputmode="url"
        label="Ссылка на комикс"
        rows="2"
        @change="updateParser()"
      />
      <v-textarea
        v-model.trim="keyTitle"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        class="mb-4"
        :disabled="!comic.parserId"
        inputmode="url"
        label="CSS указатель на название"
        rows="2"
      />
      <v-textarea
        v-model.trim="keyCover"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        class="mb-4"
        :disabled="!comic.parserId"
        inputmode="url"
        label="CSS указатель на картинку"
        rows="2"
      />
      <v-textarea
        v-model.trim="keyLanguage"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        class="mb-4"
        :disabled="!comic.parserId"
        inputmode="url"
        label="CSS указатель на язык"
        rows="2"
      />
      <v-textarea
        v-model.trim="keyAuthors"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        class="mb-4"
        :disabled="!comic.parserId"
        inputmode="url"
        label="CSS указатель на авторов"
        rows="2"
      />
      <v-textarea
        v-model.trim="keyAuthorsText"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        class="mb-4"
        :disabled="!comic.parserId"
        inputmode="url"
        label="CSS указатель на текст авторов"
        rows="2"
      />
      <v-textarea
        v-model.trim="keyTags"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        class="mb-4"
        :disabled="!comic.parserId"
        inputmode="url"
        label="CSS указатель на теги"
        rows="2"
      />
      <v-textarea
        v-model.trim="keyTagsText"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        class="mb-4"
        :disabled="!comic.parserId"
        inputmode="url"
        label="CSS указатель на текст тегов"
        rows="2"
      />
      <v-btn
        class="w-100"
        :disabled="!comic.parserId || !comic.fromUrl || loading || loadingGlobal"
        text="Загрузить"
        variant="tonal"
        @click="onLoadInfo()"
      />
    </v-container>
    <v-fab
      :disabled="loading || loadingGlobal"
      icon="$save"
      @click="onSave()"
    />
  </v-main>
</template>

<script setup lang="ts">
import useLoading from '@/composables/useLoading.ts';
import AuthorController from '@/core/entities/author/AuthorController.ts';
import AuthorModel from '@/core/entities/author/AuthorModel.ts';
import ComicOverrideController from '@/core/entities/comic-override/ComicOverrideController.ts';
import ComicOverrideModel from '@/core/entities/comic-override/ComicOverrideModel.ts';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import LanguageController from '@/core/entities/language/LanguageController.ts';
import LanguageModel from '@/core/entities/language/LanguageModel.ts';
import Parser from '@/core/entities/parser/Parser.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import TagController from '@/core/entities/tag/TagController.ts';
import TagModel from '@/core/entities/tag/TagModel.ts';
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useLanguagesStore } from '@/stores/languages.ts';
import { useParsersStore } from '@/stores/parsers.ts';
import { useTagsStore } from '@/stores/tags.ts';
import { Toast } from '@capacitor/toast';

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

const comic = ref(new ComicModel());

const parser = ref(new ParserModel());
const loadParser = async () => {
  if (!comic.value.parserId) return;
  parser.value = await ParserController.load(comic.value.parserId);
};

const comicOverride = ref(new ComicOverrideModel());
const loadComicOverride = async () => {
  comicOverride.value = await ComicOverrideController.load(comicId);
};

const saveComic = async () => {
  await ComicController.save(comic.value);
  await comicsStore.loadComicsForce();
  comic.value = new ComicModel(comicsStore.comic.getDTO());
};

const saveComicOverride = async () => {
  await ComicOverrideController.save(comicOverride.value);
};

const onSave = async () => {
  try {
    loadingGlobalStart();
    await saveComic();
    await saveComicOverride();
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const keyTitle = computed({
  get() {
    return comicOverride.value.titleCSS || parser.value.titleCSS;
  },
  set(value) {
    comicOverride.value.titleCSS = value;
  },
});

const keyCover = computed({
  get() {
    return comicOverride.value.coverCSS || parser.value.coverCSS;
  },
  set(value) {
    comicOverride.value.coverCSS = value;
  },
});

const keyLanguage = computed({
  get() {
    return comicOverride.value.languageCSS || parser.value.languageCSS;
  },
  set(value) {
    comicOverride.value.languageCSS = value;
  },
});

const keyAuthors = computed({
  get() {
    return comicOverride.value.authorsCSS || parser.value.authorsCSS;
  },
  set(value) {
    comicOverride.value.authorsCSS = value;
  },
});

const keyAuthorsText = computed({
  get() {
    return comicOverride.value.authorsTextCSS || parser.value.authorsTextCSS;
  },
  set(value) {
    comicOverride.value.authorsTextCSS = value;
  },
});

const keyTags = computed({
  get() {
    return comicOverride.value.tagsCSS || parser.value.tagsCSS;
  },
  set(value) {
    comicOverride.value.tagsCSS = value;
  },
});

const keyTagsText = computed({
  get() {
    return comicOverride.value.tagsTextCSS || parser.value.tagsTextCSS;
  },
  set(value) {
    comicOverride.value.tagsTextCSS = value;
  },
});

const init = async () => {
  loadingStart();
  await comicsStore.loadComic(comicId);
  comic.value = new ComicModel(comicsStore.comic.getDTO());

  if (!comic.value.id) {
    router.replace({ name: '/' });
  } else {
    await Promise.all([
      parsersStore.loadParsers(),
      tagsStore.loadTags(),
      authorsStore.loadAuthors(),
      languagesStore.loadLanguages(),
      loadComicOverride(),
      loadParser(),
    ]);
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

const onLoadInfo = async () => {
  if (!comic.value.fromUrl || !parser.value) return;

  try {
    loadingGlobalStart();
    const newComic = new ComicModel();
    const parserUtil = new Parser();
    parserUtil.setParseInfo(parser.value, comicOverride.value);
    const result = await parserUtil.parse(comic.value.fromUrl);

    const oldTags = tagsStore.tags.map((e) => e.name.toLowerCase());
    const saveTags = result.tags.filter((e) => oldTags.includes(e.toLowerCase()));

    for (const item of saveTags) {
      await TagController.save(new TagModel({ name: item }));
    }

    const oldAuthors = authorsStore.authors.map((e) => e.name.toLowerCase());
    const saveAuthors = result.authors.filter((e) => oldAuthors.includes(e.toLowerCase()));

    for (const item of saveAuthors) {
      await AuthorController.save(new AuthorModel({ name: item }));
    }

    const oldLanguages = languagesStore.languages.map((e) => e.name.toLowerCase());
    const saveLanguages = [result.language].filter((e) => oldLanguages.includes(e.toLowerCase()));

    for (const item of saveLanguages) {
      await LanguageController.save(new LanguageModel({ name: item }));
    }

    await Promise.all([
      tagsStore.loadTagsForce(),
      authorsStore.loadAuthorsForce(),
      languagesStore.loadLanguagesForce(),
    ]);

    newComic.name = result.name || comic.value.name;
    newComic.annotation = result.annotation || comic.value.annotation;
    newComic.cover.fromUrl = result.cover || comic.value.cover.fromUrl;

    const language = languagesStore.languages.find((e) => e.name.toLowerCase() === result.language.toLowerCase());
    newComic.languageId = language?.id || comic.value.languageId;

    const newTags = result.tags.map((e) => e.toLowerCase());
    const tags = tagsStore.tags
      .filter((e) => newTags.includes(e.name.toLowerCase()))
      .map((e) => e.id);
    newComic.tags = tags.length ? tags : comic.value.tags;

    const newAuthors = result.authors.map((e) => e.toLowerCase());
    const authors = authorsStore.authors
      .filter((e) => newAuthors.includes(e.name.toLowerCase()))
      .map((e) => e.id);
    newComic.authors = authors.length ? authors : comic.value.authors;
    comic.value = new ComicModel(newComic.getDTO());

    await saveComic();
    await saveComicOverride();
    await Promise.all([
      comicsStore.loadComicsForce(),
      loadComicOverride(),
    ]);
    comic.value = new ComicModel(comicsStore.comic.getDTO());
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
