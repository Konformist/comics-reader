<template>
  <v-main scrollable>
    <v-toolbar density="compact">
      <v-toolbar-title
        v-if="comics.length !== comicsFiltered.length"
        class="text-subtitle-1"
      >
        Найдено: {{ comicsFiltered.length }}
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
        v-model:page="comicsStore.filters.page"
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
            v-model="comicsStore.filters.page"
            class="mt-4"
            density="comfortable"
            :length="pageCount"
            @next="nextPage()"
            @prev="prevPage()"
          />
        </template>
      </v-data-iterator>
    </v-container>
    <v-fab
      :disabled="loading || loadingGlobal"
      icon="$plus"
      @click="createComic()"
    />
    <v-bottom-sheet v-model="filtersSheet">
      <v-card>
        <v-card-item>
          <v-select
            v-model="comicsStore.filters.authors"
            item-title="name"
            item-value="id"
            :items="authors"
            label="Авторы"
            multiple
            variant="solo-filled"
          />
          <v-select
            v-model="comicsStore.filters.languages"
            item-title="name"
            item-value="id"
            :items="languages"
            label="Языки"
            multiple
            variant="solo-filled"
          />
          <v-select
            v-model="comicsStore.filters.tags"
            item-title="name"
            item-value="id"
            :items="tags"
            label="Теги"
            multiple
            variant="solo-filled"
          />
          <v-btn-toggle
            v-model="comicsStore.filters.filling"
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
  </v-main>
</template>

<script lang="ts" setup>
import ComicGallery from '@/components/ComicGallery.vue';
import useLoading from '@/composables/useLoading.ts';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import AuthorController from '@/core/object-value/author/AuthorController.ts';
import type AuthorObject from '@/core/object-value/author/AuthorObject.ts';
import LanguageController from '@/core/object-value/language/LanguageController.ts';
import type LanguageObject from '@/core/object-value/language/LanguageObject.ts';
import TagController from '@/core/object-value/tag/TagController.ts';
import type TagObject from '@/core/object-value/tag/TagObject.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { Toast } from '@capacitor/toast';

definePage({
  meta: {
    title: 'Галерея',
  },
});

const router = useRouter();
const comicsStore = useComicsStore();
const {
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
} = useLoading();

const filtersSheet = ref(false);

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

const comics = ref<ComicModel[]>([]);

const filterArrays = <T>(f: T[], s: T[]): boolean => (
  !s.length
  || f.some((e) => s.includes(e))
);

const comicsFiltered = computed(() => (
  comics.value.filter((item) => {
    if ((comicsStore.filters.filling === 1 && !item.isFilled)
      || (comicsStore.filters.filling === 2 && item.isFilled)) {
      return false;
    }

    return filterArrays(item.tags, comicsStore.filters.tags)
      && filterArrays(item.authors, comicsStore.filters.authors)
      && filterArrays([item.language], comicsStore.filters.languages);
  })
));

const loadComics = async () => {
  comics.value = await ComicController.loadAll();
};

const createComic = async () => {
  const comicId = await ComicController.save(new ComicModel());

  if (comicId) {
    Toast.show({ text: 'Комикс создан' });
    await router.push({
      name: '/comics/[id]/',
      params: { id: comicId },
    });
  }
};

const init = async () => {
  loadingStart();
  await Promise.all([
    loadLanguages(),
    loadAuthors(),
    loadTags(),
    loadComics(),
  ]);
  loadingEnd();
};

init();
</script>
