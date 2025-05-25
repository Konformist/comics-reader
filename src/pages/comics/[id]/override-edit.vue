<template>
  <v-main>
    <v-container class="pa-0 pb-12">
      <v-alert
        color="info"
        rounded="0"
        variant="tonal"
      >
        Переопределение настроек парсера.<br>
        Работают с DOM деревом. См. CSS.
      </v-alert>
      <div class="px-4 py-8">
        <v-textarea
          v-model.trim="keyTitle"
          auto-grow
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="CSS указатель на название"
          rows="2"
          variant="solo-filled"
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
          variant="solo-filled"
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
          variant="solo-filled"
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
          variant="solo-filled"
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
          variant="solo-filled"
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
          variant="solo-filled"
        />
        <v-textarea
          v-model.trim="keyTagsText"
          auto-grow
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="CSS указатель на текст тегов"
          rows="2"
          variant="solo-filled"
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
import ComicOverrideController from '@/core/entities/comic-override/ComicOverrideController.ts';
import ComicOverrideModel from '@/core/entities/comic-override/ComicOverrideModel.ts';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import { Toast } from '@capacitor/toast';

definePage({
  meta: {
    title: 'Расширенные настройки',
    isBack: true,
  },
});

const route = useRoute('/comics/[id]/override-edit');
const router = useRouter();
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
const loadComic = async () => {
  comic.value = await ComicController.load(comicId);
};

const parser = ref(new ParserModel());
const loadParser = async () => {
  parser.value = await ParserController.load(comic.value.parserId);
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

  if (!comic.value.id) {
    router.replace({ name: '/' });
  } else {
    await Promise.all([
      loadComicOverride(),
      loadParser(),
    ]);
  }

  loadingEnd();
};

init();
</script>
