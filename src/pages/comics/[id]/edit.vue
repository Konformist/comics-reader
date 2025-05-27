<template>
  <v-main>
    <v-tabs
      v-model="frame"
      grow
      :items="tabs"
    />
    <v-container
      v-if="frame == 1"
      class="pa-0 pb-12"
    >
      <div class="px-4 py-8">
        <v-textarea
          v-model.trim="comicsStore.comic.name"
          auto-grow
          class="mb-4"
          label="Название"
          rows="2"
        />
        <CustomSelect
          v-model="comicsStore.comic.languageId"
          class="mb-4"
          :items="languagesStore.languages"
          label="Язык"
        />
        <CustomSelect
          v-model="comicsStore.comic.authors"
          class="mb-4"
          :items="authorsStore.authors"
          label="Авторы"
          multiple
        />
        <CustomSelect
          v-model="comicsStore.comic.tags"
          :items="tagsStore.tags"
          label="Теги"
          multiple
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-card class="mb-8">
          <v-img
            class="mx-auto"
            cover
            height="300"
            rounded="xl"
            :src="comicsStore.comic.cover.file?.url || '/'"
            width="200"
          >
            <template #error>
              <div
                class="w-100 h-100 d-flex align-center justify-center
                  border-md rounded-xl
                  text-body-2 text-grey-darken-2"
              >
                Нет изображения
              </div>
            </template>
          </v-img>
          <v-card-text>
            Размер: {{ formatBytes(comicsStore.comic.cover.file?.size || 0) }}
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
          v-model.trim="comicsStore.comic.cover.fromUrl"
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
          :disabled="(!image && !comicsStore.comic.cover.fromUrl) || loading || loadingGlobal"
          text="Загрузить"
          variant="tonal"
          @click="onLoadCover()"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-btn
          class="mb-4 w-100"
          :disabled="loading || loadingGlobal"
          text="Расширенные настройки"
          :to="{
            name: '/comics/[id]/override-edit',
            params: { id: comicsStore.comic.id },
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
    <v-container
      v-else-if="frame==2"
      class="pb-16 mb-4"
    >
      <DictionaryList
        v-if="chaptersList.length"
        :items="chaptersList"
        :loading="loading"
        @click-item="$router.push({
          name: '/chapters/[id]/edit',
          params: { id: $event }
        })"
      />
    </v-container>
  </v-main>
  <v-fab
    v-if="frame === 1"
    app
    appear
    :disabled="loading || loadingGlobal"
    icon="$save"
    @click="onSave()"
  />
  <v-fab
    v-else-if="frame === 2"
    app
    appear
    :disabled="loading || loadingGlobal"
    icon="$plus"
    @click="createChapter()"
  />
</template>

<script lang="ts" setup>
import ChapterController from '@/core/entities/chapter/ChapterController.ts';
import ChapterModel from '@/core/entities/chapter/ChapterModel.ts';
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useLanguagesStore } from '@/stores/languages.ts';
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
import ParserController from '@/core/entities/parser/ParserController.ts';

definePage({
  meta: {
    layout: 'entity',
    title: 'Редактирование комикса',
  },
});

const route = useRoute('/comics/[id]/edit');
const router = useRouter();

const comicsStore = useComicsStore();
const tagsStore = useTagsStore();
const authorsStore = useAuthorsStore();
const languagesStore = useLanguagesStore();

const {
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
} = useLoading();

const tabs = [
  { value: 1, text: 'Комикс' },
  { value: 2, text: 'Главы' },
];
const frame = ref(1);

const comicId = +(route.params.id || 0);

const loadComic = () => comicsStore.loadComic(comicId);

const comicOverride = ref(new ComicOverrideModel());
const loadComicOverride = async () => {
  comicOverride.value = await ComicOverrideController.load(comicId);
};

const saveComic = async () => {
  await ComicController.save(comicsStore.comic);
};

const image = ref<File | null>(null);

const uploadCover = async () => {
  if (!image.value) return;

  try {
    loadingGlobalStart();
    await saveComic();
    const base64 = await fileToBase64(image.value);
    await ComicCoverController.saveFile(comicsStore.comic.id, base64);
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
  if (!comicsStore.comic.cover.fromUrl) return;

  try {
    loadingGlobalStart();
    await saveComic();
    const result = await ParserController.loadImageRaw(comicsStore.comic.cover.fromUrl);
    await ComicCoverController.saveFile(comicsStore.comic.id, result);
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
  else if (comicsStore.comic.cover.fromUrl) loadByLink();
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
    await ComicController.remove(comicsStore.comic.id);
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

const chapters = ref<ChapterModel[]>([]);
const chaptersList = computed(() => (
  chapters.value.map((e, i) => ({
    id: e.id,
    name: e.name || `Глава ${i + 1}`,
  }))
));

const loadChapters = async () => {
  chapters.value = await ChapterController.loadAll(comicId);
};

const createChapter = async () => {
  try {
    loadingGlobalStart();
    const chapterId = await ChapterController.save(
      new ChapterModel({ comicId }),
    );

    if (!chapterId) return;

    Toast.show({ text: 'Глава создана' });
    await router.push({
      name: '/chapters/[id]/edit',
      params: { id: chapterId.toString() },
    });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

onMounted(async () => {
  loadingStart();

  await loadComic();
  if (!comicsStore.comic.id) {
    router.replace({ name: '/' });
  } else {
    await Promise.all([
      tagsStore.loadTags(),
      authorsStore.loadAuthors(),
      languagesStore.loadLanguages(),
      loadChapters(),
      loadComicOverride(),
    ]);
  }

  loadingEnd();
});
</script>
