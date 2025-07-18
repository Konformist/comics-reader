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
      v-else-if="frame === 2"
      class="pb-16 mb-4"
    >
      <DictionaryList
        v-if="chaptersList.length > 0"
        :items="chaptersList"
        :loading="loading"
        @click-item="router.push({
          name: '/chapters/[id]/edit',
          params: { id: $event },
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
    @click="chapterModal = true"
  />
  <DictionaryEditDialog
    v-model="chapterName"
    v-model:opened="chapterModal"
    :disabled="loadingGlobal"
    is-created
    @save="createChapter()"
    @update:opened="chapterName = ''"
  />
</template>

<script lang="ts" setup>
import { Dialog } from '@capacitor/dialog';
import useLoading from '@/composables/useLoading.ts';
import ChapterController from '@/core/entities/chapter/ChapterController.ts';
import ChapterModel from '@/core/entities/chapter/ChapterModel.ts';
import ComicCoverController from '@/core/entities/comic-cover/ComicCoverController.ts';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import UI from '@/plugins/UIPlugin.ts';
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useLanguagesStore } from '@/stores/languages.ts';
import { useTagsStore } from '@/stores/tags.ts';

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
    UI.toast({ text: 'Комикс сохранён в Загрузки' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
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
    UI.toast({ text: 'Комикс сохранён' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
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
    UI.toast({ text: 'Комикс сохранён' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
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
    UI.toast({ text: 'Комикс сохранён' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
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
    UI.toast({ text: 'Комикс удалён' });
    router.replace({ name: '/' });
  } catch (error) {
    alert(error);
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};

const chapters = ref<ChapterModel[]>([]);
const chaptersList = computed(() => (
  chapters.value
    .map((e, i) => ({
      id: e.id,
      name: e.name || `Глава ${i + 1}`,
      count: `${e.pages.reduce((a, c) => a + (c.file ? 1 : 0), 0)} / ${e.pages.length}`,
    }))
    .reverse()
));

const loadChapters = async () => {
  chapters.value = await ChapterController.loadAll(comicId);
};

const chapterModal = ref(false);
const chapterName = ref('');

const createChapter = async () => {
  try {
    loadingGlobalStart();
    await ChapterController.save(new ChapterModel({
      comicId,
      name: chapterName.value,
    }));
    chapterModal.value = false;
    chapterName.value = '';
    await loadChapters();
    UI.toast({ text: 'Глава создана' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};

onMounted(async () => {
  loadingStart();

  await comicsStore.loadComic(comicId);
  comic.value = new ComicModel(comicsStore.comic.getDTO());
  if (comic.value.id) {
    await Promise.all([
      tagsStore.loadTags(),
      authorsStore.loadAuthors(),
      languagesStore.loadLanguages(),
      loadChapters(),
    ]);
  } else {
    router.replace({ name: '/' });
  }

  loadingEnd();
});
</script>
