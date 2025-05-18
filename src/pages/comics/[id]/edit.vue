<template>
  <v-main scrollable>
    <v-progress-linear
      v-if="loading"
      indeterminate
    />
    <v-container class="pa-0">
      <div class="px-4 py-8">
        <v-select
          v-if="parsers.length"
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
          :autocomplete="false"
          inputmode="url"
          label="Ссылка на комикс"
          rows="2"
          @change="updateParser()"
        />
        <v-textarea
          v-model.trim="comic.name"
          auto-grow
          label="Название"
          rows="2"
        />
        <v-select
          v-model="comic.language"
          item-title="name"
          item-value="id"
          :items="languages"
          label="Язык"
        />
        <v-select
          v-model="comic.authors"
          chips
          item-title="name"
          item-value="id"
          :items="authors"
          label="Авторы"
          multiple
        />
        <v-select
          v-model="comic.tags"
          chips
          item-title="name"
          item-value="id"
          :items="tags"
          label="Теги"
          multiple
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="!comic.parser || !comic.url || loading"
          text="Загрузить"
          @click="onLoadInfo()"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <template v-if="cover.url">
          <v-card
            class="mb-4"
            :image="cover.url"
            :loading="loading"
            min-height="200"
            rounded
          />
          <p class="mb-8 text-body-1">
            Размер: {{ formatBytes(cover.size) }}
          </p>
        </template>
        <v-file-input
          v-model="image"
          accept="image/*"
          hide-details
          label="Загрузить картинку"
        />
        <p class="my-2">
          Или
        </p>
        <v-textarea
          v-model.trim="comic.image.url"
          auto-grow
          :autocapitalize="false"
          :autocomplete="false"
          inputmode="url"
          label="Ссылка на картинку"
          rows="2"
        />
        <v-btn
          class="w-100"
          :disabled="(!image && !comic.image.url) || loading"
          text="Загрузить"
          @click="onLoadCover()"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-btn
          v-if="comic.parser"
          class="mb-4 w-100"
          :disabled="loading"
          text="Расширенные настройки"
          :to="{
            name: '/comics/[id]/edit-external',
            params: { id: comic.id },
          }"
          @click="onSave()"
        />
        <v-btn
          class="w-100"
          :disabled="loading"
          text="Редактировать страницы"
          :to="{
            name: '/comics/[id]/edit-pages',
            params: { id: comic.id },
          }"
          @click="onSave()"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-btn
          class="w-100"
          color="error"
          :disabled="loading"
          text="Удалить"
          @click="deleteComic()"
        />
      </div>
    </v-container>
    <v-fab
      icon="$save"
      :loading="loading"
      @click="onSave()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import useLoading from '@/composables/useLoading.ts';
import { formatBytes } from '@/core/utils/format.ts';
import { fileToBase64 } from '@/core/utils/image.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import FileModel from '@/core/object-value/file/FileModel.ts';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import AuthorController from '@/core/object-value/author/AuthorController.ts';
import AuthorObject from '@/core/object-value/author/AuthorObject.ts';
import LanguageController from '@/core/object-value/language/LanguageController.ts';
import LanguageObject from '@/core/object-value/language/LanguageObject.ts';
import TagController from '@/core/object-value/tag/TagController.ts';
import TagObject from '@/core/object-value/tag/TagObject.ts';

definePage({
  meta: {
    title: 'Редактирование комикса',
    isBack: true,
  },
});

const route = useRoute('/comics/[id]/edit');
const router = useRouter();
const { loading, loadingStart,loadingEnd } = useLoading();

const languages = ref<LanguageObject[]>([]);
const loadLanguages = async () => {
  languages.value = await LanguageController.loadAll();
};

const authors = ref<AuthorObject[]>([]);
const loadAuthors = async () => {
  authors.value = await AuthorController.loadAll();
};

const tags = ref<TagObject[]>([]);
const loadTags = async () => {
  tags.value = await TagController.loadAll();
};

const comicId = +(route.params.id || 0);

const cover = ref(new FileModel());
const loadCover = async () => {
  cover.value = await ComicController.loadCover(comicId);
};

const comic = ref(new ComicModel());
const loadComic = async () => {
  comic.value = await ComicController.load(comicId);
  if (!comic.value.id) router.replace({ name: '/' });
};

const parsers = ref<ParserModel[]>([]);
const loadParsers = async () => {
  parsers.value = await ParserController.loadAll();
};

const parser = ref(new ParserModel());
const loadParser = async () => {
  if (!comic.value.parser) return;
  parser.value = await ParserController.load(comic.value.parser);
};

const updateParser = () => {
  if (!comic.value.url) return;

  const item = parsers.value.find((e) => e.site && comic.value.url.includes(e.site));

  if (!item) return;

  comic.value.parser = item.id;
  loadParser();
};

onMounted(async () => {
  await Promise.all([
    loadParsers(),
    loadTags(),
    loadAuthors(),
    loadLanguages(),
    loadCover(),
    loadComic(),
  ]);
  await loadParser();
});

const saveComic = async () => {
  await ComicController.save(comic.value);
};

const onLoadInfo = async () => {
  if (!comic.value || !parser.value) return;

  try {
    loadingStart();
    const result = await ParserController.loadComicRaw(comic.value.url);
    const parsedComic = parser.value.parse(result, comic.value.override);

    if (!comic.value.image.url && parsedComic.image) {
      comic.value.image.url = parsedComic.image;
    }

    if (!comic.value.images.length && parsedComic.images) {
      comic.value.images = parsedComic.images.map((e, i) => ({
        id: i + 1,
        url: e,
        fileId: 0,
      }));
    }

    if (parsedComic.name) {
      comic.value.name = parsedComic.name;
    }

    if (parsedComic.language) {
      const language = languages.value.find((e) => e.name.toLowerCase() === parsedComic.language?.toLowerCase());

      if (!language) {
        const itemId = await LanguageController.save(new LanguageObject({ id: 0, name: parsedComic.language }));
        if (itemId) comic.value.language = itemId;
      } else {
        comic.value.language = language.id;
      }
    }

    for (const item of parsedComic.authors || []) {
      const newItem = authors.value.find((e) => e.name.toLowerCase() === item.toLowerCase());

      if (!newItem) {
        const itemId = await AuthorController.save(new AuthorObject({ id: 0, name: item }));
        if (itemId) comic.value.authors.push(itemId);
      } else {
        comic.value.authors.push(newItem.id);
      }
    }

    for (const item of parsedComic.tags || []) {
      const newItem = tags.value.find((e) => e.name.toLowerCase() === item.toLowerCase());

      if (!newItem) {
        const itemId = await TagController.save(new TagObject({ id: 0, name: item }));
        if (itemId) comic.value.tags.push(itemId);
      } else {
        comic.value.tags.push(newItem.id);
      }
    }

    await saveComic();
    await Promise.all([
      loadTags(),
      loadAuthors(),
      loadLanguages(),
      loadComic(),
    ]);
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Комикс не сохранён. Ошибка: ${e}` });
  } finally {
    loadingEnd();
  }
};

const image = ref<File | null>(null);

const uploadCover = async () => {
  if (!image.value) return;

  try {
    loadingStart();
    await saveComic();
    const base64 = await fileToBase64(image.value);
    await ComicController.saveCover(comic.value.id, base64);
    await loadComic();
    await loadCover();
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingEnd();
  }
};

const loadByLink = async () => {
  if (!comic.value.image.url) return;

  try {
    loadingStart();
    const result = await ParserController.loadImageRaw(comic.value.image.url);
    await saveComic();
    await ComicController.saveCover(comic.value.id, result);
    await loadComic();
    await loadCover();
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingEnd();
  }
};

const onLoadCover = () => {
  if (image.value) uploadCover();
  else if (comic.value.image.url) loadByLink();
};

const onSave = async () => {
  await saveComic();
  Toast.show({ text: 'Комикс сохранён' });
};

const deleteComic = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить комикс?',
  });

  if (!value) return;

  try {
    loadingStart();
    await ComicController.delete(comic.value.id);
    Toast.show({ text: 'Комикс удалён' });
    router.replace({ name: '/' });
  } catch (e) {
    alert(e);
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingEnd();
  }
};
</script>
