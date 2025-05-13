<template>
  <v-main>
    <v-container v-if="comic">
      <v-select
        v-model="comic.parser"
        item-title="name"
        item-value="id"
        :items="parsers"
        label="Парсер"
        @update:model-value="loadParser()"
      />
      <v-textarea
        v-model.trim="comic.url"
        auto-grow
        :autocapitalize="false"
        label="Ссылка на комикс"
        rows="2"
      />
      <v-textarea
        v-model.trim="comic.name"
        auto-grow
        label="Название"
        rows="2"
      />
      <v-combobox
        v-model="comic.language"
        :items="languages"
        label="Язык"
      />
      <v-textarea
        v-model.trim="keyLanguage"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        label="Парсинг языка"
        rows="2"
      />
      <v-combobox
        v-model="comic.authors"
        chips
        :items="authors"
        label="Авторы"
        multiple
      />
      <v-textarea
        v-model.trim="keyAuthors"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        label="Парсинг авторов"
        rows="2"
      />
      <v-combobox
        v-model="comic.tags"
        chips
        :items="tags"
        label="Теги"
        multiple
      />
      <v-textarea
        v-model.trim="keyTags"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        label="Парсинг тегов"
        rows="2"
      />
      <v-btn
        class="w-100"
        :loading="loading"
        text="Перезагрузить"
        @click="onReloadInfo()"
      />
      <v-divider class="my-8" />
      <v-img
        v-if="comic.image"
        rounded
        :src="comic.image"
      />
      <v-file-input
        class="mt-4"
        label="Загрузить свою картинку"
        @update:model-value="uploadCover($event)"
      />
      <v-textarea
        v-model.trim="comic.imageUrl"
        auto-grow
        label="Ссылка на картинку"
        rows="2"
      />
      <v-btn
        class="w-100"
        :loading="loading"
        text="Перезагрузить"
        @click="onReloadCover()"
      />
    </v-container>
    <v-fab
      icon="$save"
      :loading="loading"
      @click="onSave()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import { dedupe } from '@/core/utils/array.ts';
import { Toast } from '@capacitor/toast';

definePage({
  meta: {
    title: 'Редактирование комикса',
    isBack: true,
  },
});

const route = useRoute('/comics/[id]/edit');

const comics = ref<ComicModel[]>([]);

const languages = ref<string[]>([]);
const tags = ref<string[]>([]);
const authors = ref<string[]>([]);

const loadComics = async () => {
  comics.value = await ComicController.loadAll();
  languages.value = dedupe(comics.value.map(e => e.language)).sort();
  tags.value = dedupe(comics.value.map(e => e.tags).flat(1)).sort();
  authors.value = dedupe(comics.value.map(e => e.authors).flat(1)).sort();
}

const comicId = +(route.params.id || 0);
const comic = ref(new ComicModel());

const loadComic = async () => {
  if (!comicId) return;
  comic.value = await ComicController.load(comicId);
}

const parsers = ref<ParserModel[]>([]);

const loadParsers = async () => {
  parsers.value = await ParserController.loadAll();
}

const parser = ref(new ParserModel());

const loadParser = async () => {
  if (!comic.value.parser) return;
  parser.value = await ParserController.load(comic.value.parser);
}

onMounted(async () => {
  loadComics();
  loadParsers();
  await loadComic();
  await loadParser();
})

const keyLanguage = computed({
  get () {
    return comic.value?.override.language || parser.value?.language || '';
  },
  set (value) {
    if (comic.value) {
      comic.value.override.language = value;
    }
  },
})

const keyAuthors = computed({
  get () {
    return comic.value.override.authors || parser.value.authors || '';
  },
  set (value) {
    comic.value.override.authors = value;
  },
})

const keyTags = computed({
  get () {
    return comic.value.override.tags || parser.value.tags || '';
  },
  set (value) {
    comic.value.override.tags = value;
  },
})

const saveComic = async () => {
  await ComicController.save(comic.value);
}

const loading = ref(false);

const onReloadInfo = async () => {
  if (!comic.value || !parser.value) return;

  try {
    loading.value = true;
    const result = await ParserController.loadComic(comic.value.url);
    const comicDTO = parser.value.parse(result, comic.value.override);

    if (comic.value.imageUrl) delete comicDTO.imageUrl;
    if (comic.value.images.length) delete comicDTO.images;

    Object.assign(comic.value, comicDTO);
    await saveComic();
    await loadComic();
    Toast.show({ text: 'Комикс сохранён' })
  } catch (e) {
    Toast.show({ text: `Комикс не сохранён. Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
};

const uploadCover = async (event: File|File[]) => {
  if (!event || Array.isArray(event)) return;

  await ComicController.saveCover(comic.value.id, event);
  await loadComic();
  Toast.show({ text: 'Комикс сохранён' })
}

const onReloadCover = async () => {
  if (!comic.value.imageUrl) return;

  try {
    loading.value = true;
    const result = await ParserController.loadImageRaw(comic.value.imageUrl);
    await uploadCover(result);
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
};

const onSave = async () => {
  await saveComic();
  Toast.show({ text: 'Комикс сохранён' });
};
</script>
