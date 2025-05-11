<template>
  <v-app-bar>
    <v-btn
      icon="$arrow-left"
      slim
      :to="{ name: '/comics/[id]/' }"
    />
    <v-app-bar-title>
      {{ comic?.name }}
    </v-app-bar-title>
  </v-app-bar>
  <v-main>
    <v-container v-if="comic">
      <v-select
        v-model="comic.parser"
        item-title="name"
        item-value="id"
        :items="appStore.parsers"
        label="Парсер"
      />
      <v-textarea
        v-model.trim="comic.url"
        label="Ссылка на комикс"
        rows="2"
      />
      <v-textarea
        v-model.trim="comic.name"
        label="Название"
        rows="2"
      />
      <v-combobox
        v-model="comic.language"
        :items="appStore.languages"
        label="Язык"
      />
      <v-textarea
        v-model.trim="keyLanguage"
        label="Парсинг языка"
        rows="2"
      />
      <v-combobox
        v-model="comic.authors"
        chips
        :items="appStore.authors"
        label="Авторы"
        multiple
      />
      <v-textarea
        v-model.trim="keyAuthors"
        label="Парсинг авторов"
        rows="2"
      />
      <v-combobox
        v-model="comic.tags"
        chips
        :items="appStore.tags"
        label="Теги"
        multiple
      />
      <v-textarea
        v-model.trim="keyTags"
        label="Парсинг тегов"
        rows="2"
      />
      <p>
        <v-btn
          :loading="loading"
          text="Перезагрузить"
          @click="onReloadInfo()"
        />
      </p>
      <v-divider class="my-8" />
      <v-img
        v-if="comic.image"
        height="150"
        :src="Capacitor.convertFileSrc(comic.image)"
      />
      <v-file-input
        label="Загрузить свою картинку"
        @update:model-value="uploadCover($event)"
      />
      <v-textarea
        v-model="comic.imageUrl"
        label="Ссылка на картинку"
        rows="2"
      />
      <p>
        <v-btn
          :loading="loading"
          text="Перезагрузить"
          @click="onReloadCover()"
        />
      </p>
      <template
        v-for="(item, index) in comic.imagesUrl"
        :key="index"
      >
        <v-divider class="my-8" />
        <v-img
          v-if="comic.image"
          height="150"
          :src="Capacitor.convertFileSrc(comic.image)"
        />
        <v-textarea
          label="Ссылка на страницу"
          :model-value="item"
          rows="2"
          @change="setImageUrl(index, $event)"
        />
        <p>
          <v-btn
            :loading="loading"
            text="Перезагрузить"
            @click="onReloadImage(index)"
          />
        </p>
      </template>
    </v-container>
    <v-fab
      app
      class="mb-4"
      icon="$save"
      :loading="loading"
      @click="appStore.saveComics()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import ParserController from '@/core/entities/parser/ParserController.ts';
import { useAppStore } from '@/stores/app.ts';
import { Capacitor } from '@capacitor/core';

const route = useRoute('/comics/[id]/');
const appStore = useAppStore();

const comic = computed(() => (appStore.comics.find(e => e.id === +route.params.id)))
const parser = computed(() => (appStore.parsers.find(e => e.id === comic.value?.parser)))

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
    return comic.value?.override.authors || parser.value?.authors || '';
  },
  set (value) {
    if (comic.value) {
      comic.value.override.authors = value;
    }
  },
})

const keyTags = computed({
  get () {
    return comic.value?.override.tags || parser.value?.tags || '';
  },
  set (value) {
    if (comic.value) {
      comic.value.override.tags = value;
    }
  },
})

const setImageUrl = (index: number, event: Event) => {
  if (!comic.value) return;

  comic.value.imagesUrl[index] = (event.target as HTMLInputElement).value.trim();
}

const loading = ref(false);

const onReloadInfo = async () => {
  if (!comic.value || !parser.value) return;

  try {
    loading.value = true;
    const result = await ParserController.loadComic(comic.value.url);
    const comicDTO = parser.value.parseV2(result, comic.value.override);

    if (comic.value.imageUrl) delete comicDTO.imageUrl;
    if (comic.value.imagesUrl.length) delete comicDTO.imagesUrl;

    Object.assign(comic.value, comicDTO);
    await appStore.saveComics();
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

const uploadCover = async (event: File) => {
  if (!comic.value) return;

  comic.value.image = await ParserController.writeFSCover(
    comic.value.id,
    ParserController.getExtension(comic.value.imageUrl),
    event,
  );
  await appStore.saveComics();
}

const onReloadCover = async () => {
  if (!comic.value) return;

  try {
    loading.value = true;
    const result = await ParserController.loadImage(comic.value.imageUrl);
    comic.value.image = await ParserController.writeFSCover(
      comic.value.id,
      ParserController.getExtension(comic.value.imageUrl),
      result,
    );
    await appStore.saveComics();
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

const onReloadImage = async (index: number) => {
  if (!comic.value) return;

  try {
    loading.value = true;
    const imageUrl = comic.value.imagesUrl[index];
    const result = await ParserController.loadImage(imageUrl);
    comic.value.images[index] = await ParserController.writeFSPage(
      comic.value.id,
      index,
      ParserController.getExtension(imageUrl),
      result,
    );
    await appStore.saveComics();
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}
</script>
