<template>
  <v-main>
    <v-container class="pa-0 pb-16">
      <p class="pa-4">
        <CustomImg
          class="mx-auto bg-grey-darken-4"
          cover
          height="300"
          :loading="loading"
          :src="comicsStore.comic.cover.file?.url"
          width="200"
        />
      </p>
      <v-divider />
      <h3 class="pa-4 font-weight-medium">
        {{ comicsStore.comic.name || '—' }} <v-icon
          v-if="comicsStore.comic.name"
          icon="$copy"
          size="20"
          @click="onCopy(comicsStore.comic.name)"
        />
      </h3>
      <template v-if="comicsStore.comic.fromUrl">
        <v-divider />
        <p class="pa-4">
          <a :href="comicsStore.comic.fromUrl">Ссылка на комикс</a> <v-icon
            icon="$copy"
            size="20"
            @click="onCopy(comicsStore.comic.fromUrl)"
          />
        </p>
      </template>
      <template v-if="comicsStore.comic.annotation">
        <v-divider />
        <p class="pa-4">
          {{ comicsStore.comic.annotation }}
        </p>
      </template>
      <v-divider />
      <p class="pa-4">
        <b class="font-weight-medium">Авторы:</b> {{ authorsChips.length > 0 ? authorsChips.map((e) => e.name).join(", ") : '—' }}
      </p>
      <v-divider />
      <p class="pa-4">
        <b class="font-weight-medium">Язык:</b> {{ languagesChips.length > 0 ? languagesChips[0].name : '—' }}
      </p>
      <template v-if="tagsChips.length > 0">
        <v-divider />
        <p class="pa-4 d-flex flex-wrap ga-1 align-center">
          <v-chip
            v-for="item in tagsChips"
            :key="item.id"
            color="primary"
            :text="item.name"
            variant="tonal"
          />
        </p>
      </template>
      <template v-if="chaptersList.length > 0">
        <v-divider />
        <v-list
          activable
          class="ma-4"
        >
          <v-list-item
            v-for="chapter in chaptersList"
            :key="chapter.id"
            :title="chapter.name"
            :to="chapter.total ? {
              name: '/chapters/[id]/read',
              params: { id: chapter.id },
              query: {
                comic: comicId,
                page: chapter.readLast,
              },
            } : undefined"
          >
            <template #append>
              <v-icon
                :color="getReadProcessColor(chapter.status)"
                icon="$read"
              />
            </template>
          </v-list-item>
        </v-list>
      </template>
    </v-container>
  </v-main>
  <v-fab
    app
    appear
    :disabled="loading || loadingGlobal"
    icon="$edit"
    :to="{
      name: '/comics/[id]/edit',
      params: { id: comicsStore.comic.id },
    }"
  />
</template>

<script lang="ts" setup>
import type ChapterModel from '@/core/entities/chapter/ChapterModel.ts';
import { CHAPTER_READ_STATUS } from '@/core/entities/chapter/ChapterTypes.ts';
import { Clipboard } from '@capacitor/clipboard';
import useLoading from '@/composables/useLoading.ts';
import ChapterController from '@/core/entities/chapter/ChapterController.ts';
import UI from '@/plugins/UIPlugin.ts';
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useLanguagesStore } from '@/stores/languages.ts';
import { useTagsStore } from '@/stores/tags.ts';

definePage({
  meta: {
    layout: 'entity',
    title: 'Комикс',
  },
});

const route = useRoute('/comics/[id]/');
const router = useRouter();
const comicId = +route.params.id;

const comicsStore = useComicsStore();
const tagsStore = useTagsStore();
const authorsStore = useAuthorsStore();
const languagesStore = useLanguagesStore();

const {
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
} = useLoading();

const chapters = ref<ChapterModel[]>([]);
const chaptersList = computed(() => (
  chapters.value
    .map((e, i) => ({
      id: e.id,
      name: e.name || `Глава ${i + 1}`,
      status: e.status,
      readLast: e.lastPageUnread < 1 ? 0 : e.lastPageUnread - 1,
      total: e.pages.length,
    }))
    .reverse()
));

const getReadProcessColor = (status: CHAPTER_READ_STATUS) => {
  switch (status) {
  case CHAPTER_READ_STATUS.PROCESS: return 'warning';
  case CHAPTER_READ_STATUS.FULL: return 'success';
  default: return '';
  }
};

const loadChapters = async () => {
  chapters.value = await ChapterController.loadAll(comicId);
};

const loadComic = () => comicsStore.loadComic(comicId);

const languagesChips = computed(() => (
  languagesStore.languages.filter((e) => comicsStore.comic.languageId === e.id)
));
const authorsChips = computed(() => (
  authorsStore.authors.filter((e) => comicsStore.comic.authors.includes(e.id))
));
const tagsChips = computed(() => (
  tagsStore.tags.filter((e) => comicsStore.comic.tags.includes(e.id))
));

const onCopy = async (string: string) => {
  await Clipboard.write({ string });
  UI.toast({ text: 'Скопировано' });
};

const init = async () => {
  loadingStart();
  await loadComic();

  if (comicsStore.comic.id) {
    await Promise.all([
      languagesStore.loadLanguages(),
      authorsStore.loadAuthors(),
      tagsStore.loadTags(),
      loadChapters(),
    ]);
  } else {
    router.replace({ name: '/' });
  }

  loadingEnd();
};

init();
</script>
