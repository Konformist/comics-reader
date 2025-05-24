<template>
  <v-main scrollable>
    <v-toolbar density="compact">
      <v-toolbar-title class="text-subtitle-1">
        {{ comicsStore.comics.length !== comicsFiltered.length ? 'Найдено' : 'Всего' }}: {{ comicsFiltered.length }}
      </v-toolbar-title>
      <v-spacer />
      <v-btn
        :active="filtersSheet"
        prepend-icon="$filter"
        text="Фильтры"
        @click="filtersSheet = true"
      />
    </v-toolbar>
    <v-container>
      <v-row v-if="loading">
        <v-col class="pa-2">
          <v-skeleton-loader
            height="250"
            type="card"
          />
        </v-col>
        <v-col class="pa-2">
          <v-skeleton-loader
            height="250"
            type="card"
          />
        </v-col>
      </v-row>
      <v-data-iterator
        v-else
        v-model:page="comicsPageStore.filters.page"
        :items="comicsFiltered"
        items-per-page="20"
      >
        <template #default="{ items }">
          <v-row>
            <v-col
              v-for="item in items"
              :key="item.raw.id"
              class="pa-2"
              cols="6"
            >
              <ComicGallery
                :comic="item.raw"
              />
            </v-col>
          </v-row>
        </template>
        <template #footer="{ pageCount, prevPage, nextPage }">
          <v-pagination
            v-model="comicsPageStore.filters.page"
            class="mt-4"
            density="comfortable"
            :length="pageCount"
            @next="nextPage()"
            @prev="prevPage()"
          />
        </template>
      </v-data-iterator>
    </v-container>
    <v-bottom-sheet v-model="filtersSheet">
      <v-card>
        <v-card-item>
          <v-select
            v-model="comicsPageStore.filters.authors"
            item-title="name"
            item-value="id"
            :items="authorsStore.authors"
            label="Авторы"
            multiple
            variant="solo-filled"
          />
          <v-select
            v-model="comicsPageStore.filters.languages"
            item-title="name"
            item-value="id"
            :items="languagesStore.languages"
            label="Языки"
            multiple
            variant="solo-filled"
          />
          <v-select
            v-model="comicsPageStore.filters.tags"
            item-title="name"
            item-value="id"
            :items="tagsStore.tags"
            label="Теги"
            multiple
            variant="solo-filled"
          />
          <v-btn-toggle
            v-model="comicsPageStore.filters.filling"
            class="w-100"
            density="comfortable"
            divided
            variant="tonal"
          >
            <v-btn class="flex-1-0" text="Все" :value="0" />
            <v-btn class="flex-1-0" text="Заполненные" :value="1" />
            <v-btn class="flex-1-0" text="Пустые" :value="2" />
          </v-btn-toggle>
        </v-card-item>
      </v-card>
    </v-bottom-sheet>
    <v-fab
      class="mb-14"
      :disabled="loading || loadingGlobal"
      icon="$plus"
      @click="createComic()"
    />
  </v-main>
</template>

<script lang="ts" setup>
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

definePage({
  meta: {
    title: 'Галерея',
    isBottomNavigation: true,
  },
});

const router = useRouter();
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

const filtersSheet = ref(false);

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

      return filterArrays(item.tags, comicsPageStore.filters.tags)
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

init();
</script>
