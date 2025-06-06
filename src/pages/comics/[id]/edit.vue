<template>
  <v-main>
    <v-tabs
      v-model="frame"
      grow
      :items="tabs"
      @update:model-value="onMove()"
    />
    <v-container
      v-if="frame === 1"
      class="pa-0 pb-16"
    >
      <div class="pa-4">
        <v-textarea
          v-model.trim="comic.name"
          auto-grow
          class="mb-4"
          label="Название"
          rows="2"
        />
        <v-textarea
          v-model.trim="comic.annotation"
          auto-grow
          class="mb-4"
          label="Аннотация"
          rows="2"
        />
        <CustomSelect
          v-model="comic.languageId"
          class="mb-4"
          :items="languagesStore.languages"
          label="Язык"
        />
        <CustomSelect
          v-model="comic.authors"
          class="mb-4"
          :items="authorsStore.authors"
          label="Авторы"
          multiple
        />
        <CustomSelect
          v-model="comic.tags"
          :items="tagsStore.tags"
          label="Теги"
          multiple
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <ChapterPageEdit
          v-model:from-url="comic.cover.fromUrl"
          :disabled="loading || loadingGlobal"
          :size="comic.cover.file?.size"
          :src="comic.cover.file?.url"
          @download="loadByLink()"
          @pick="uploadCover()"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-btn
          class="mb-4 w-100"
          :disabled="loading || loadingGlobal"
          text="Сохранить в Загрузки"
          variant="tonal"
          @click="uploadComic()"
        />
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
    <v-container
      v-else-if="frame===2"
      class="pb-16 mb-4"
    >
      <DictionaryList
        v-if="chaptersList.length"
        :items="chaptersList"
        :loading="loading"
        @click-item="router.push({
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
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useLanguagesStore } from '@/stores/languages.ts';
import { useTagsStore } from '@/stores/tags.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import useLoading from '@/composables/useLoading.ts';
import ComicCoverController from '@/core/entities/comic-cover/ComicCoverController.ts';
import ComicController from '@/core/entities/comic/ComicController.ts';

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

const comicId = +(route.params.id || 0);

const tabs = [
  {
    value: 1,
    text: 'Комикс',
  },
  {
    value: 2,
    text: 'Главы',
  },
];

const frame = ref(+(route.query.frame || 1));
const onMove = () => {
  router.replace({
    name: '/comics/[id]/edit',
    params: { id: comicId },
    query: { frame: frame.value },
  });
};

const comic = ref(new ComicModel());

const saveComic = async () => {
  await ComicController.save(comic.value);
};

const uploadComic = async () => {
  try {
    loadingGlobalStart();
    await saveComic();
    await ComicController.upload(comic.value.id);
    Toast.show({ text: 'Комикс сохранён в Загрузки' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const uploadCover = async () => {
  try {
    loadingGlobalStart();
    await saveComic();
    await ComicCoverController.saveFile(comic.value.id);
    await comicsStore.loadComicsForce();
    comic.value = new ComicModel(comicsStore.comic.getDTO());
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
    await ComicCoverController.downloadFile(comic.value.id, comic.value.cover.fromUrl);
    await comicsStore.loadComicsForce();
    comic.value = new ComicModel(comicsStore.comic.getDTO());
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const onSave = async () => {
  try {
    loadingGlobalStart();
    await saveComic();
    await comicsStore.loadComicsForce();
    comic.value = new ComicModel(comicsStore.comic.getDTO());
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

const chapters = ref<ChapterModel[]>([]);
const chaptersList = computed(() => (
  chapters.value.map((e, i) => ({
    id: e.id,
    name: e.name || `Глава ${i + 1}`,
    count: `${e.pages.reduce((a,c) => a + (c.file ? 1 : 0), 0)} / ${e.pages.length}`,
  }))
));

const loadChapters = async () => {
  chapters.value = await ChapterController.loadAll(comicId);
};

const createChapter = async () => {
  try {
    loadingGlobalStart();
    const chapterId = await ChapterController.save(new ChapterModel({ comicId }));

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

  await comicsStore.loadComic(comicId);
  comic.value = new ComicModel(comicsStore.comic.getDTO());
  if (!comic.value.id) {
    router.replace({ name: '/' });
  } else {
    await Promise.all([
      tagsStore.loadTags(),
      authorsStore.loadAuthors(),
      languagesStore.loadLanguages(),
      loadChapters(),
    ]);
  }

  loadingEnd();
});
</script>
