<template>
  <v-app-bar
    class="rounded-b-xl"
    flat
  >
    <v-btn
      :active="appStore.drawer"
      icon="$menu"
      rounded="pill"
      @click="appStore.drawer = !appStore.drawer"
    />
    <v-text-field
      v-model.trim="comicsPageStore.filters.search"
      flat
      placeholder="Поиск..."
    />
  </v-app-bar>
  <v-main>
    <v-toolbar
      class="px-4"
      color="background"
      density="comfortable"
    >
      <div
        class="w-100 d-flex"
        style="overflow-x: auto;"
      >
        <DropdownButton
          v-model="comicsPageStore.filters.tags"
          class="mr-2"
          :disabled="!tagsStore.tags.length"
          :items="tagsStore.tags"
          multiple
          prepend-icon="$filter"
          text="Теги"
        />
        <DropdownButton
          v-model="comicsPageStore.filters.authors"
          class="mr-2"
          :disabled="!authorsStore.authors.length"
          :items="authorsStore.authors"
          multiple
          prepend-icon="$filter"
          text="Авторы"
        />
        <DropdownButton
          v-model="comicsPageStore.filters.languages"
          :disabled="!languagesStore.languages.length"
          :items="languagesStore.languages"
          multiple
          prepend-icon="$filter"
          text="Языки"
        />
      </div>
    </v-toolbar>
    <v-container class="pb-16 mb-4">
      <template v-if="loading">
        <v-skeleton-loader
          v-for="i in 2"
          :key="i"
          class="rounded-xl"
          :class="i - 1 ? 'mt-4' : ''"
          height="200"
          type="card"
        />
      </template>
      <v-data-iterator
        v-else
        v-model:page="comicsPageStore.filters.page"
        :items="comicsFiltered"
        items-per-page="20"
      >
        <template #default="{ items }">
          <ComicGallery
            v-for="(item, index) in items"
            :key="item.raw.id"
            :authors="authorsStore.authors"
            :class="index ? 'mt-4' : ''"
            :comic="item.raw"
            :tags="tagsStore.tags"
            @move-comic="moveComic($event)"
          />
        </template>
        <template #footer="{ pageCount, prevPage, nextPage }">
          <v-pagination
            v-model="comicsPageStore.filters.page"
            class="mt-4"
            :length="pageCount"
            @next="nextPage(); scrollY(0)"
            @prev="prevPage(); scrollY(0)"
            @update:model-value="scrollY(0)"
          />
        </template>
      </v-data-iterator>
    </v-container>
  </v-main>
  <v-fab
    app
    appear
    class="mb-16"
    :disabled="loading || loadingGlobal"
    icon="$plus"
    @click="createComic()"
  />
</template>

<script lang="ts" setup>
import { useAppStore } from '@/stores/app.ts';
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useLanguagesStore } from '@/stores/languages.ts';
import { useTagsStore } from '@/stores/tags.ts';
import { Toast } from '@capacitor/toast';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import { useComicsPageStore } from '@/stores/comicsPage.ts';
import useLoading from '@/composables/useLoading.ts';
import ComicGallery from '@/components/ComicGallery.vue';
import { onBeforeMount } from 'vue';

definePage({
  meta: {
    layout: 'list',
    title: 'Галерея',
  },
});

const router = useRouter();
const appStore = useAppStore();
const comicsStore = useComicsStore();
const comicsPageStore = useComicsPageStore();
const tagsStore = useTagsStore();
const authorsStore = useAuthorsStore();
const languagesStore = useLanguagesStore();

const {
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
} = useLoading();

const scrollY = (value: number) => {
  nextTick(() => document.scrollingElement?.scrollTo(0, value));
};

const filterString = (v: string, s: string) => (
  v.toLowerCase().includes(s.toLowerCase())
);
const filterArrays = <T>(f: T[], s: T[]): boolean => (
  !s.length
  || f.some((e) => s.includes(e))
);

const comicsFiltered = computed(() => (
  comicsStore.comics
    .filter((item) => {
      if ((comicsPageStore.filters.filling === 1 && !item.isFilled)
        || (comicsPageStore.filters.filling === 2 && item.isFilled)) {
        return false;
      }

      return filterString(item.name, comicsPageStore.filters.search)
        && filterArrays(item.tags, comicsPageStore.filters.tags)
        && filterArrays(item.authors, comicsPageStore.filters.authors)
        && filterArrays([item.languageId], comicsPageStore.filters.languages);
    })
    .reverse()
));

const createComic = async () => {
  const comicId = await ComicController.save(new ComicModel());

  if (typeof comicId !== 'number') return;

  await comicsStore.loadComicsForce();
  Toast.show({ text: 'Комикс создан' });
  await router.push({
    name: '/comics/[id]/',
    params: { id: comicId },
  });
};

const moveComic = (id: number) => {
  comicsPageStore.scroll = document.scrollingElement?.scrollTop ?? 0;

  router.push({
    name: '/comics/[id]/',
    params: { id },
  });
};

const init = async () => {
  loadingStart();
  await Promise.all([
    languagesStore.loadLanguages(),
    authorsStore.loadAuthors(),
    tagsStore.loadTags(),
    comicsStore.loadComics(),
  ]);
  loadingEnd();
};

onBeforeMount(async () => {
  await init();
  scrollY(comicsPageStore.scroll);
});
</script>
