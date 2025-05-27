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
          v-if="parsersStore.parsers.length"
          v-model="comicsStore.comic.parserId"
          class="mb-4"
          :items="parsersStore.parsers"
          label="Парсер"
          @update:model-value="loadParser()"
        />
        <v-textarea
          v-model.trim="comicsStore.comic.fromUrl"
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
          :disabled="!comicsStore.comic.parserId"
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
          inputmode="url"
          label="CSS указатель на текст тегов"
          rows="2"
        />
        <v-btn
          class="w-100"
          :disabled="!comicsStore.comic.parserId || !comicsStore.comic.fromUrl || loading || loadingGlobal"
          text="Загрузить"
          variant="tonal"
          @click="onLoadInfo()"
        />
      </div>
    </v-container>
    <v-fab
      :disabled="loading || loadingGlobal"
      icon="$save"
      @click="saveComic()"
    />
  </v-main>
</template>

<script setup lang="ts">
import useLoading from '@/composables/useLoading.ts';
import AuthorController from '@/core/entities/author/AuthorController.ts';
import AuthorModel from '@/core/entities/author/AuthorModel.ts';
import ComicOverrideController from '@/core/entities/comic-override/ComicOverrideController.ts';
import ComicOverrideModel from '@/core/entities/comic-override/ComicOverrideModel.ts';
import LanguageController from '@/core/entities/language/LanguageController.ts';
import LanguageModel from '@/core/entities/language/LanguageModel.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import { parseComic } from '@/core/entities/parser/parseUtils.ts';
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

const loadComic = () => comicsStore.loadComic(comicId);

const parser = ref(new ParserModel());
const loadParser = async () => {
  parser.value = await ParserController.load(comicsStore.comic.parserId);
};

const comicOverride = ref(new ComicOverrideModel());
const loadComicOverride = async () => {
  comicOverride.value = await ComicOverrideController.load(comicId);
};

const saveComic = async () => {
  try {
    loadingGlobalStart();
    await ComicOverrideController.save(comicOverride.value);
    await loadComicOverride();
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
  await loadComic();

  if (!comicsStore.comic.id) {
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
  if (!comicsStore.comic.fromUrl) return;

  const item = parsersStore.parsers.find((e) => e.siteUrl && comicsStore.comic.fromUrl.includes(e.siteUrl));

  if (!item) return;

  comicsStore.comic.parserId = item.id;
  loadParser();
};

const onLoadInfo = async () => {
  if (!comicsStore.comic.fromUrl || !parser.value) return;

  try {
    loadingGlobalStart();
    const result = await ParserController.loadComicRaw(comicsStore.comic.fromUrl);
    const parsedComic = parseComic(result, parser.value, comicOverride.value);

    if (!comicsStore.comic.cover.fromUrl && parsedComic.cover)
      comicsStore.comic.cover.fromUrl = parsedComic.cover;

    if (parsedComic.name) {
      comicsStore.comic.name = parsedComic.name;
    }

    if (parsedComic.language) {
      const language = languagesStore.languages.find((e) => e.name.toLowerCase() === parsedComic.language?.toLowerCase());

      if (!language) {
        const itemId = await LanguageController.save(new LanguageModel({ name: parsedComic.language }));
        if (typeof itemId === 'number') comicsStore.comic.languageId = itemId;
      } else {
        comicsStore.comic.languageId = language.id;
      }
    }

    for (const item of parsedComic.authors || []) {
      const newItem = authorsStore.authors.find((e) => e.name.toLowerCase() === item.toLowerCase());

      if (!newItem) {
        const itemId = await AuthorController.save(new AuthorModel({ name: item }));
        if (typeof itemId === 'number') comicsStore.comic.authors.push(itemId);
      } else {
        comicsStore.comic.authors.push(newItem.id);
      }
    }

    for (const item of parsedComic.tags || []) {
      const newItem = tagsStore.tags.find((e) => e.name.toLowerCase() === item.toLowerCase());

      if (!newItem) {
        const itemId = await TagController.save(new TagModel({ name: item }));
        if (typeof itemId === 'number') comicsStore.comic.tags.push(itemId);
      } else {
        comicsStore.comic.tags.push(newItem.id);
      }
    }

    await saveComic();
    await comicsStore.loadComicsForce();
    await Promise.all([
      tagsStore.loadTagsForce(),
      authorsStore.loadAuthorsForce(),
      languagesStore.loadLanguagesForce(),
      loadComic(),
    ]);
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Комикс не сохранён. Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
