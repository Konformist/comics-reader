<template>
  <v-app-bar>
    <v-btn
      icon="$arrow-left"
      slim
      @click="$router.back()"
    />
    <v-app-bar-title text="Загрузка комикса" />
  </v-app-bar>
  <v-main>
    <v-container>
      <v-select
        v-model="parseOptions.parser"
        item-title="name"
        item-value="id"
        :items="parserItems"
        label="Парсер"
      />
      <v-textarea
        v-model.trim="parseOptions.url"
        label="Ссылка на комикс"
        rows="2"
      />
      <v-btn
        class="w-100"
        :disabled="!canParse"
        :loading="loading"
        text="Загрузить комикс"
        @click="loadComic()"
      />
      <v-btn
        class="mt-4 w-100"
        :disabled="!comic.imageUrl"
        :loading="loading"
        text="Загрузить картинку"
        @click="loadComicCover()"
      />
      <v-btn
        class="mt-4 w-100"
        :disabled="!comic.images.length"
        :loading="loading"
        text="Загрузить страницы"
        @click="loadComicImages()"
      />
      <v-img
        v-if="comicCoverUrl"
        class="mt-4"
        rounded
        :src="comicCoverUrl"
      />
      <h2
        v-if="comic.name"
        class="mt-4 font-weight-medium"
      >
        {{ comic.name }}
      </h2>
      <p
        v-if="comic.authors.length"
        class="mt-2 d-flex flex-wrap ga-1 align-center"
      >
        <b class="font-weight-medium">Авторы:</b>
        <v-chip
          v-for="author in comic.authors"
          :key="author"
          :text="author"
        />
      </p>
      <p
        v-if="comic.language"
        class="mt-2 d-flex flex-wrap ga-1 align-center"
      >
        <b class="font-weight-medium">Язык:</b>
        <v-chip
          :text="comic.language"
        />
      </p>
      <p
        v-if="comic.tags.length"
        class="mt-2 d-flex flex-wrap ga-1 align-center"
      >
        <b class="font-weight-medium">Теги:</b>
        <v-chip
          v-for="tag in comic.tags"
          :key="tag"
          :text="tag"
        />
      </p>
      <v-img
        v-for="image in comicImagesUrl"
        :key="image"
        :src="image"
      />
    </v-container>
    <v-fab
      app
      class="mb-4"
      icon="$save"
      :loading="loading"
      @click="saveComic()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import { Toast } from '@capacitor/toast';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import type ParserModel from '@/core/entities/parser/ParserModel.ts';

const router = useRouter();

const parseOptions = ref({
  parser: 0,
  url: '',
});

const parsers = ref<ParserModel[]>([]);

const loadParsers = async () => {
  parsers.value = await ParserController.loadAll();
  parseOptions.value.parser = parsers.value[0]?.id ?? 0;
}

loadParsers();

const parserItems = computed(() => (
  parsers.value.map(e => ({
    id: e.id,
    name: e.name,
  }))
));

const canParse = computed(() => (
  !!parseOptions.value.parser
  && !!parseOptions.value.url
))

const comic = ref(new ComicModel());
const comicCover = ref<File|null>(null);
const comicCoverUrl = computed(() => (
  comicCover.value
    ? URL.createObjectURL(comicCover.value)
    : ''
));

const comicImages = ref<File[]>([]);
const comicImagesUrl = computed(() => (
  comicImages.value.map(e => (URL.createObjectURL(e)))
));

const parser = computed(() => (parsers.value.find(e => e.id === parseOptions.value.parser)))

const loading = ref(false);

const loadComic = async () => {
  if (!parser.value) return;

  try {
    loading.value = true;
    const result = await ParserController.loadComicRaw(parseOptions.value.url);
    const comicDTO = parser.value.parse(result);

    comic.value = new ComicModel(comicDTO);
    comic.value.parser = parseOptions.value.parser;
    comic.value.url = parseOptions.value.url;
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
};

const loadComicCover = async () => {
  try {
    loading.value = true;
    comicCover.value = await ParserController.loadImageRaw(comic.value.imageUrl);
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
};

const loadComicImages = async () => {
  try {
    loading.value = true;
    const images = comic.value.images
      .filter(e => e.from)
      .map(e => e.from);
    comicImages.value = await Promise.all(images.map(image => (
      ParserController.loadImageRaw(image)
    )))
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
};

const saveComic = async () => {
  let comicId: number|void = 0;

  try {
    loading.value = true;
    comicId = await ComicController.save(comic.value);

    if (!comicId) return;

    if (comicCover.value) {
      await ComicController.saveCover(comicId, comicCover.value);
    }

    for (const image of comicImages.value) {
      await ComicController.saveFile(comicId, 0, image);
    }

    Toast.show({ text: 'Комикс сохранён' })
    await router.replace({
      name: '/comics/[id]/',
      params: { id: comicId },
    })
  } catch (e) {
    Toast.show({ text: `Комикс не сохранён. Ошибка: ${e}` })
    if (!comicId) return;
    if (comic.value.image) await ComicController.deleteCover(comicId);
    await ComicController.deleteFiles(comicId);
    ComicController.delete(comicId);
  } finally {
    loading.value = false;
  }
}
</script>
