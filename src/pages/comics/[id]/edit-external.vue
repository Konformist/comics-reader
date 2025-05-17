<template>
  <v-main>
    <v-progress-linear
      v-if="loading"
      indeterminate
    />
    <v-container>
      <h2 class="text-h6">
        Переопределение настроек парсера
      </h2>
      <v-textarea
        v-model.trim="keyTitle"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        class="mt-4"
        inputmode="url"
        label="CSS указатель на название"
        rows="2"
        variant="solo-filled"
      />
      <v-textarea
        v-model.trim="keyLanguage"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        :disabled="!comic.parser"
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
        v-model.trim="keyAuthorsText"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        hide-details
        inputmode="url"
        label="CSS указатель на текст тегов"
        rows="2"
        variant="solo-filled"
      />
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import useLoading from '@/composables/useLoading.ts';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';

const route = useRoute('/comics/[id]/edit');
const router = useRouter();
const { loading, loadingStart,loadingEnd } = useLoading();

const comicId = +(route.params.id || 0);

const parser = ref(new ParserModel());
const loadParser = async () => {
  parser.value = await ParserController.load(comic.value.parser);
  if (!comic.value.id) router.replace({ name: '/' });
};

const comic = ref(new ComicModel());
const loadComic = async () => {
  if (!comicId) return;
  comic.value = await ComicController.load(comicId);
};

const keyTitle = computed({
  get() {
    return comic.value.override.title || parser.value.language;
  },
  set(value) {
    comic.value.override.language = value;
  },
});

const keyLanguage = computed({
  get() {
    return comic.value.override.language || parser.value.language;
  },
  set(value) {
    comic.value.override.language = value;
  },
});

const keyAuthors = computed({
  get() {
    return comic.value.override.authors || parser.value.authors;
  },
  set(value) {
    comic.value.override.authors = value;
  },
});

const keyAuthorsText = computed({
  get() {
    return comic.value.override.authorsText || parser.value.authorsText;
  },
  set(value) {
    comic.value.override.authorsText = value;
  },
});

const keyTags = computed({
  get() {
    return comic.value.override.tags || parser.value.tags;
  },
  set(value) {
    comic.value.override.tags = value;
  },
});

const init = async () => {
  loadingStart();
  await loadComic();
  await loadParser();
  loadingEnd();
};

init();
</script>
