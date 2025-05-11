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
      <p>
        <v-btn
          class="w-100"
          :disabled="!canParse"
          :loading="loading"
          text="Загрузить"
          @click="onCreate()"
        />
      </p>
    </v-container>
  </v-main>
</template>

<script lang="ts" setup>
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import type { IComicImageDTO } from '@/core/entities/comic/ComicTypes.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import type ParserModel from '@/core/entities/parser/ParserModel.ts';
import { useAppStore } from '@/stores/app.ts';
import { Toast } from '@capacitor/toast';

const router = useRouter();
const appStore = useAppStore();

const parserItems = computed(() => {
  const items = [{ id: 0, name: 'Не выбрано' }];
  const parsers = appStore.parsers.map(e => ({
    id: e.id,
    name: e.name,
  }))

  items.push(...parsers);

  return items;
});

const parseOptions = ref({
  parser: 0,
  url: '',
});

const canParse = computed(() => (
  parseOptions.value.parser
  && parseOptions.value.url
))

const parser = computed(() => (appStore.parsers.find(e => e.id === parseOptions.value.parser)))

const loading = ref(false);

const createComic = async (value: ParserModel, options: typeof parseOptions.value) => {
  const result = await ParserController.loadComic(options.url);
  const comicDTO = value.parse(result);
  const comic = new ComicModel(comicDTO);
  const lastId = appStore.comics[appStore.comics.length - 1]?.id || 0;

  comic.id = lastId + 1;
  comic.parser = options.parser;
  comic.url = options.url;

  return comic;
}

const createCover = async (comic: ComicModel) => {
  const result = await ParserController.loadImage(comic.imageUrl);

  return await ParserController.writeFSCover(
    comic.id,
    ParserController.getExtension(comic.imageUrl),
    result,
  );
}

const createImages = async (comic: ComicModel) => {
  const arr: IComicImageDTO[] = [];

  for (const image of comic.images) {
    if (!image) continue;

    const blob = await ParserController.loadImage(image.from);
    const path = await ParserController.writeFSPage(comic.id, image, blob);

    arr.push({ ...image, url: path });
  }

  return arr;
}

const onCreate = async () => {
  if (!parser.value) return;

  let comic = new ComicModel();

  try {
    loading.value = true;

    comic = await createComic(parser.value, parseOptions.value);

    if (comic.imageUrl) comic.image = await createCover(comic);
    if (comic.images.length) comic.images = await createImages(comic);

    appStore.comics.push(comic);
    await appStore.saveComics();
    Toast.show({ text: 'Комикс сохранён' })
    await router.push({
      name: '/comics/[id]/',
      params: { id: comic.id },
    })
  } catch (e) {
    if (comic.image) ParserController.deleteFS(comic.image);
    comic.images.forEach(e => {
      if (e) ParserController.deleteFS(e.url);
    })
    Toast.show({ text: `Комикс не сохранён. Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
};
</script>
