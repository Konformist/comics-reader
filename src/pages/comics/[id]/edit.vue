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
          class="w-100"
          :loading="loading"
          text="Перезагрузить"
          @click="onReloadInfo()"
        />
      </p>
      <v-divider class="my-8" />
      <v-card
        v-if="comic.image"
        height="150"
      >
        <ComicImage :src="comic.image" />
      </v-card>
      <v-file-input
        class="mt-4"
        label="Загрузить свою картинку"
        @update:model-value="uploadCover($event)"
      />
      <v-textarea
        v-model.trim="comic.imageUrl"
        label="Ссылка на картинку"
        rows="2"
      />
      <p>
        <v-btn
          class="w-100"
          :loading="loading"
          text="Перезагрузить"
          @click="onReloadCover()"
        />
      </p>
      <template
        v-for="item in comic.images"
        :key="item.id"
      >
        <v-divider class="my-8" />
        <v-card
          v-if="item.url"
          height="150"
        >
          <ComicImage :src="item.url" />
        </v-card>
        <v-file-input
          class="mt-4"
          label="Загрузить свою страницу"
          @update:model-value="uploadImage(item, $event)"
        />
        <v-textarea
          v-model.trim="item.from"
          label="Ссылка на страницу"
          rows="2"
        />
        <p class="d-flex">
          <v-btn
            :loading="loading"
            text="Перезагрузить"
            @click="onReloadImage(item)"
          />
          <v-btn
            class="ml-auto"
            color="red"
            :loading="loading"
            text="Удалить"
            @click="delPage(item)"
          />
        </p>
      </template>
      <p>
        <v-btn
          class="mt-4 w-100"
          text="Добавить страницу"
          @click="addPage()"
        />
      </p>
    </v-container>
    <v-fab
      app
      class="mb-4"
      icon="$save"
      :loading="loading"
      @click="onSave()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import ComicImage from '@/components/ComicImage.vue';
import type { IComicImageDTO } from '@/core/entities/comic/ComicTypes.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import { useAppStore } from '@/stores/app.ts';
import { Toast } from '@capacitor/toast';

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

const addPage = () => {
  if (!comic.value) return;

  const lastId = comic.value.images[comic.value.images.length - 1]?.id || 0;
  comic.value.images.push({
    id: lastId + 1,
    url: '',
    from: '',
  })
}

const delPage = async (item: IComicImageDTO) => {
  if (!comic.value) return;

  const index = comic.value.images.findIndex(e => e.id === item.id);

  if (index !== -1) {
    const image = comic.value.images.splice(index, 1)[0];
    if (image.url) await ParserController.deleteFS(image.url)
    await appStore.saveComics();
    Toast.show({ text: 'Комикс сохранён' })
  }
};

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
    await appStore.saveComics();
    Toast.show({ text: 'Комикс сохранён' })
  } catch (e) {
    Toast.show({ text: `Комикс не сохранён. Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
};

const uploadCover = async (event: File|File[]) => {
  if (!comic.value || Array.isArray(event)) return;

  comic.value.image = await ParserController.writeFSCover(
    comic.value.id,
    ParserController.getExtension(comic.value.imageUrl),
    event,
  );

  await appStore.saveComics();
  Toast.show({ text: 'Комикс сохранён' })
}

const uploadImage = async (item: IComicImageDTO, event: File|File[]) => {
  if (!comic.value || Array.isArray(event)) return;

  const savedUrl = item.url;
  item.url = await ParserController.writeFSPage(comic.value.id, item, event);
  if (item.url !== savedUrl) await ParserController.deleteFS(savedUrl);

  await appStore.saveComics();
  Toast.show({ text: 'Комикс сохранён' })
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
    Toast.show({ text: 'Комикс сохранён' })
  } catch (e) {
    Toast.show({ text: `Комикс не сохранён. Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
};

const onReloadImage = async (item: IComicImageDTO) => {
  if (!comic.value) return;

  try {
    loading.value = true;
    const result = await ParserController.loadImage(item.from);

    const savedUrl = item.url;
    item.url = await ParserController.writeFSPage(comic.value.id, item, result);
    if (item.url !== savedUrl) await ParserController.deleteFS(savedUrl);

    await appStore.saveComics();
    Toast.show({ text: 'Комикс сохранён' })
  } catch (e) {
    Toast.show({ text: `Комикс не сохранён. Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
}

const onSave = async () => {
  await appStore.saveComics();
  Toast.show({ text: 'Комикс сохранён' });
};
</script>
