<template>
  <v-main>
    <v-container class="pa-0 pb-16 mb-4">
      <p class="pt-4">
        <v-skeleton-loader
          v-if="loading"
          class="mx-auto"
          height="300"
          type="image"
          width="200"
        />
        <v-img
          v-else
          class="mx-auto bg-grey-darken-4"
          cover
          height="300"
          rounded="xl"
          :src="comicsStore.comic.cover.file?.url || '/'"
          width="200"
        >
          <template #error>
            <div class="w-100 h-100 d-flex align-center justify-center text-body-2 text-grey-darken-2">
              Нет изображения
            </div>
          </template>
        </v-img>
      </p>
      <v-divider class="my-4" />
      <h3 class="px-4 font-weight-medium">
        {{ comicsStore.comic.name || '—' }} <v-icon
          v-if="comicsStore.comic.name"
          icon="$copy"
          size="20"
          @click="onCopy(comicsStore.comic.name)"
        />
      </h3>
      <template v-if="comicsStore.comic.fromUrl">
        <v-divider class="my-4" />
        <p class="px-4">
          <a :href="comicsStore.comic.fromUrl">Ссылка на комикс</a> <v-icon
            icon="$copy"
            size="20"
            @click="onCopy(comicsStore.comic.fromUrl)"
          />
        </p>
      </template>
      <v-divider class="my-4" />
      <p class="px-4">
        <b class="font-weight-medium">Авторы:</b> {{ authorsChips.length ? authorsChips.map((e) => e.name).join(", ") : '—' }}
      </p>
      <v-divider class="my-4" />
      <p class="px-4">
        <b class="font-weight-medium">Язык:</b> {{ languagesChips.length ? languagesChips[0].name : '—' }}
      </p>
      <template v-if="tagsChips.length">
        <v-divider class="my-4" />
        <p class="px-4 d-flex flex-wrap ga-1 align-center">
          <v-chip
            v-for="item in tagsChips"
            :key="item.id"
            color="primary"
            :text="item.name"
            variant="tonal"
          />
        </p>
      </template>
      <template v-if="chaptersList.length">
        <v-divider class="my-4" />
        <v-list
          class="ma-4"
          :items="chaptersList"
          :loading="loading"
          selectable
          @click:select="$router.push({
            name: '/chapters/[id]/read',
            params: { id: $event.id as number },
          })"
        >
          <template #append="{ item }">
            <v-icon
              :color="item.isRead ? 'success' : ''"
              icon="$read"
            />
          </template>
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
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useLanguagesStore } from '@/stores/languages.ts';
import { useTagsStore } from '@/stores/tags.ts';
import { Clipboard } from '@capacitor/clipboard';
import { Toast } from '@capacitor/toast';
import useLoading from '@/composables/useLoading.ts';
import ChapterController from '@/core/entities/chapter/ChapterController.ts';
import ChapterModel from '@/core/entities/chapter/ChapterModel.ts';

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
  chapters.value.map((e, i) => ({
    id: e.id,
    name: e.name || `Глава ${i + 1}`,
    isRead: e.pages.length && e.pages.every((e) => e.isRead),
  }))
));

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
  Toast.show({ text: 'Скопировано' });
};

const init = async () => {
  loadingStart();
  await loadComic();

  if (!comicsStore.comic.id) {
    router.replace({ name: '/' });
  } else {
    await Promise.all([
      languagesStore.loadLanguages(),
      authorsStore.loadAuthors(),
      tagsStore.loadTags(),
      loadChapters(),
    ]);
  }

  loadingEnd();
};

init();
</script>
