<template>
  <v-main scrollable>
    <v-container v-if="comic">
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
      <v-expansion-panels>
        <v-expansion-panel static title="Расширенные настройки">
          <v-expansion-panel-text>
            <v-textarea
              v-model.trim="keyTitle"
              auto-grow
              :autocapitalize="false"
              :autocomplete="false"
              class="mt-4"
              :disabled="!comic.parser"
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
              :disabled="!comic.parser"
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
              :disabled="!comic.parser"
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
              :disabled="!comic.parser"
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
              :disabled="!comic.parser"
              hide-details
              inputmode="url"
              label="CSS указатель на текст тегов"
              rows="2"
              variant="solo-filled"
            />
          </v-expansion-panel-text>
        </v-expansion-panel>
      </v-expansion-panels>
      <v-btn
        class="mt-4 w-100"
        :disabled="!comic.parser || !comic.url"
        :loading="loading"
        text="Загрузить"
        @click="onLoadInfo()"
      />
      <v-divider class="mt-8 mb-4" />
      <v-img
        v-if="cover.url"
        rounded
        :src="cover.url"
      />
      <v-file-input
        v-model="image"
        accept="image/*"
        class="mt-4"
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
        :disabled="!image && !comic.image.url"
        :loading="loading"
        text="Загрузить"
        @click="onLoadCover()"
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
import useLoading from '@/composables/useLoading.ts';
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
  if (!comicId) return;
  comic.value = await ComicController.load(comicId);
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

const saveComic = async () => {
  await ComicController.save(comic.value);
};

const onLoadInfo = async () => {
  if (!comic.value || !parser.value) return;

  try {
    loadingStart();
    const result = await ParserController.loadComic(comic.value.url);
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
    await ComicController.saveCover(comic.value.id, image.value);
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
</script>
