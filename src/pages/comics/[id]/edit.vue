<template>
  <v-app-bar>
    <v-btn
      icon="$arrow-left"
      slim
      @click="$router.back()"
    />
    <v-app-bar-title text="Редактирование комикса" />
  </v-app-bar>
  <v-main>
    <v-container v-if="comic">
      <v-select
        v-model="comic.parser"
        item-title="name"
        item-value="id"
        :items="parsers"
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
        :items="languages"
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
        :items="authors"
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
        :items="tags"
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
      <v-divider class="my-8" />
      <v-textarea
        v-model.trim="imagesTemplate"
        hint="Пример: https://domain.com/12/23/<ID>.jpeg"
        label="Шаблон для автозаполнения"
        rows="2"
      />
      <v-text-field
        v-model.number="imagesTemplateStart"
        class="mt-4"
        label="Начальный ID"
        type="number"
      />
      <v-btn
        class="w-100"
        text="Заполнить"
        @click="setTemplate()"
      />
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
            color="error"
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
          @click="comic.addImage()"
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
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import type { IComicImageDTO } from '@/core/entities/comic/ComicTypes.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import { dedupe } from '@/core/utils/array.ts';
import { Toast } from '@capacitor/toast';

const route = useRoute('/comics/[id]/edit');

const comics = ref<ComicModel[]>([]);

const languages = ref<string[]>([]);
const tags = ref<string[]>([]);
const authors = ref<string[]>([]);

const loadComics = async () => {
  comics.value = await ComicController.loadAll();
  languages.value = dedupe(comics.value.map(e => e.language));
  tags.value = dedupe(comics.value.map(e => e.tags).flat(1));
  authors.value = dedupe(comics.value.map(e => e.authors).flat(1));
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

const imagesTemplate = ref('');
const imagesTemplateStart = ref(1);

const setTemplate = () => {
  comic.value?.images.forEach((image, index) => {
    image.from = imagesTemplate.value.replace('<ID>', (imagesTemplateStart.value + index).toString());
  })
};

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

const delPage = async (item: IComicImageDTO) => {
  if (!comic.value) return;

  const index = comic.value.images.findIndex(e => e.id === item.id);

  if (index !== -1) {
    const image = comic.value.images.splice(index, 1)[0];
    if (image.url) await ComicController.deleteFile(comic.value.id, image.id)
    await saveComic();
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

const uploadImage = async (item: IComicImageDTO, event: File|File[]) => {
  if (!event || Array.isArray(event)) return;

  await ComicController.saveFile(comic.value.id, item.id, event);
  await loadComic();
  Toast.show({ text: 'Комикс сохранён' })
}

const onReloadImage = async (item: IComicImageDTO) => {
  if (!item.from) return;

  try {
    loading.value = true;
    const result = await ParserController.loadImageRaw(item.from);
    await uploadImage(item, result);
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
}

const onSave = async () => {
  await saveComic();
  Toast.show({ text: 'Комикс сохранён' });
};
</script>
