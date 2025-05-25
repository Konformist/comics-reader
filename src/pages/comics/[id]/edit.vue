<template>
  <v-main scrollable>
    <v-container class="pa-0 pb-12">
      <div class="px-4 py-8">
        <v-select
          v-if="parsersStore.parsers.length"
          v-model="comic.parserId"
          class="mb-4"
          :items="parsersStore.parsers"
          label="Парсер"
          @update:model-value="loadParser()"
        />
        <v-textarea
          v-model.trim="comic.fromUrl"
          auto-grow
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="Ссылка на комикс"
          rows="2"
          @change="updateParser()"
        />
        <v-textarea
          v-model.trim="comic.name"
          auto-grow
          class="mb-4"
          label="Название"
          rows="2"
        />
        <v-select
          v-model="comic.languageId"
          class="mb-4"
          :items="languagesStore.languages"
          label="Язык"
        />
        <v-select
          v-model="comic.authors"
          class="mb-4"
          :items="authorsStore.authors"
          label="Авторы"
          multiple
        />
        <v-select
          v-model="comic.tags"
          class="mb-4"
          :items="tagsStore.tags"
          label="Теги"
          multiple
        />
        <v-btn
          class="w-100"
          :disabled="!comic.parserId || !comic.fromUrl || loading || loadingGlobal"
          text="Загрузить"
          variant="tonal"
          @click="onLoadInfo()"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-card
          v-if="comic.cover.file?.url"
          class="mb-8"
          rounded
        >
          <v-img
            min-height="200"
            :src="comic.cover.file.url"
          />
          <v-card-text>
            Размер: {{ formatBytes(comic.cover.file.size) }}
          </v-card-text>
        </v-card>
        <v-file-input
          v-model="image"
          accept="image/*"
          label="Загрузить картинку"
        />
        <p class="my-2">
          Или
        </p>
        <v-textarea
          v-model.trim="comic.cover.fromUrl"
          auto-grow
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          inputmode="url"
          label="Ссылка на картинку"
          rows="2"
        />
        <v-btn
          class="w-100"
          :disabled="(!image && !comic.cover.fromUrl) || loading || loadingGlobal"
          text="Загрузить"
          variant="tonal"
          @click="onLoadCover()"
        />
      </div>
      <v-divider />
      <div
        v-if="comic.parserId"
        class="px-4 py-8"
      >
        <v-btn
          class="mb-4 w-100"
          :disabled="loading || loadingGlobal"
          text="Расширенные настройки"
          :to="{
            name: '/comics/[id]/override-edit',
            params: { id: comic.id },
          }"
          variant="tonal"
        />
        <v-btn
          class="w-100"
          color="error"
          :disabled="loading || loadingGlobal"
          text="Удалить"
          variant="tonal"
          @click="deleteComic()"
        />
      </div>
    </v-container>
    <v-fab
      :disabled="loading || loadingGlobal"
      icon="$save"
      @click="onSave()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import { parseComic } from '@/core/entities/parser/parseUtils.ts';
import { useAppStore } from '@/stores/app.ts';
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useLanguagesStore } from '@/stores/languages.ts';
import { useParsersStore } from '@/stores/parsers.ts';
import { useTagsStore } from '@/stores/tags.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import useLoading from '@/composables/useLoading.ts';
import ComicCoverController from '@/core/entities/comic-cover/ComicCoverController.ts';
import ComicOverrideController from '@/core/entities/comic-override/ComicOverrideController.ts';
import ComicOverrideModel from '@/core/entities/comic-override/ComicOverrideModel.ts';
import { formatBytes } from '@/core/utils/format.ts';
import { fileToBase64 } from '@/core/utils/image.ts';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import AuthorController from '@/core/entities/author/AuthorController.ts';
import AuthorModel from '@/core/entities/author/AuthorModel.ts';
import LanguageController from '@/core/entities/language/LanguageController.ts';
import LanguageModel from '@/core/entities/language/LanguageModel.ts';
import TagController from '@/core/entities/tag/TagController.ts';
import TagModel from '@/core/entities/tag/TagModel.ts';

definePage({
  meta: {
    title: 'Редактирование комикса',
    isBack: true,
  },
});

const route = useRoute('/comics/[id]/edit');
const router = useRouter();

const appStore = useAppStore();
const comicsStore = useComicsStore();
const tagsStore = useTagsStore();
const authorsStore = useAuthorsStore();
const languagesStore = useLanguagesStore();
const parsersStore = useParsersStore();

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

const comicOverride = ref(new ComicOverrideModel());
const loadComicOverride = async () => {
  comicOverride.value = await ComicOverrideController.load(comicId);
};

const parser = ref(new ParserModel());
const loadParser = async () => {
  if (!comic.value.parserId) return;
  parser.value = await ParserController.load(comic.value.parserId);
};

const updateParser = () => {
  if (!comic.value.fromUrl) return;

  const item = parsersStore.parsers.find((e) => e.siteUrl && comic.value.fromUrl.includes(e.siteUrl));

  if (!item) return;

  comic.value.parserId = item.id;
  loadParser();
};

onMounted(async () => {
  loadingStart();

  await loadComic();
  if (!comic.value.id) {
    router.replace({ name: '/' });
  } else {
    await Promise.all([
      parsersStore.loadParsers(),
      tagsStore.loadTags(),
      authorsStore.loadAuthors(),
      languagesStore.loadLanguages(),
      loadComicOverride(),
    ]);
    await loadParser();
  }

  loadingEnd();
});

const saveComic = async () => {
  await ComicController.save(comic.value);
};

const onLoadInfo = async () => {
  if (!comic.value || !parser.value) return;

  try {
    loadingGlobalStart();
    const result = await ParserController.loadComicRaw(comic.value.fromUrl);
    const parsedComic = parseComic(result, parser.value, comicOverride.value);

    if (!comic.value.cover.fromUrl && parsedComic.cover)
      comic.value.cover.fromUrl = parsedComic.cover;

    if (parsedComic.name) {
      comic.value.name = parsedComic.name;
    }

    if (parsedComic.language) {
      const language = languagesStore.languages.find((e) => e.name.toLowerCase() === parsedComic.language?.toLowerCase());

      if (!language) {
        const itemId = await LanguageController.save(new LanguageModel({ name: parsedComic.language }));
        if (typeof itemId === 'number') comic.value.languageId = itemId;
      } else {
        comic.value.languageId = language.id;
      }
    }

    for (const item of parsedComic.authors || []) {
      const newItem = authorsStore.authors.find((e) => e.name.toLowerCase() === item.toLowerCase());

      if (!newItem) {
        const itemId = await AuthorController.save(new AuthorModel({ name: item }));
        if (typeof itemId === 'number') comic.value.authors.push(itemId);
      } else {
        comic.value.authors.push(newItem.id);
      }
    }

    for (const item of parsedComic.tags || []) {
      const newItem = tagsStore.tags.find((e) => e.name.toLowerCase() === item.toLowerCase());

      if (!newItem) {
        const itemId = await TagController.save(new TagModel({ name: item }));
        if (typeof itemId === 'number') comic.value.tags.push(itemId);
      } else {
        comic.value.tags.push(newItem.id);
      }
    }

    await saveComic();
    await comicsStore.loadComicsForce();
    await Promise.all([
      tagsStore.loadTagsForce(),
      authorsStore.loadAuthorsForce(),
      languagesStore.loadLanguagesForce(),
      loadComic(),
    ]);
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Комикс не сохранён. Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const image = ref<File | null>(null);

const uploadCover = async () => {
  if (!image.value) return;

  try {
    loadingGlobalStart();
    await saveComic();
    const base64 = await fileToBase64(image.value);
    await ComicCoverController.saveFile(comic.value.id, base64, appStore.settings.isCompress);
    await comicsStore.loadComicsForce();
    await loadComic();
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const loadByLink = async () => {
  if (!comic.value.cover.fromUrl) return;

  try {
    loadingGlobalStart();
    await saveComic();
    const result = await ParserController.loadImageRaw(comic.value.cover.fromUrl);
    await ComicCoverController.saveFile(comic.value.id, result, appStore.settings.isCompress);
    await loadComic();
    await comicsStore.loadComicsForce();
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const onLoadCover = () => {
  if (image.value) uploadCover();
  else if (comic.value.cover.fromUrl) loadByLink();
};

const onSave = async () => {
  try {
    loadingGlobalStart();
    await saveComic();
    await comicsStore.loadComicsForce();
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const deleteComic = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить комикс?',
  });

  if (!value) return;

  try {
    loadingGlobalStart();
    await ComicController.remove(comic.value.id);
    await comicsStore.loadComicsForce();
    Toast.show({ text: 'Комикс удалён' });
    router.replace({ name: '/' });
  } catch (e) {
    alert(e);
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
