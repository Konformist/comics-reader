<template>
  <v-main>
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
      <v-data-iterator
        v-model:page="currentPage"
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
            v-model="currentPage"
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
      icon="$plus"
      @click="createComic()"
    />
    <v-bottom-sheet v-model="filtersSheet">
      <v-card>
        <v-card-item>
          <v-select
            v-model="filters.authors"
            :items="authors"
            label="Авторы"
            multiple
            variant="solo-filled"
          />
          <v-select
            v-model="filters.languages"
            :items="languages"
            label="Языки"
            multiple
            variant="solo-filled"
          />
          <v-select
            v-model="filters.tags"
            :items="tags"
            label="Теги"
            multiple
            variant="solo-filled"
          />
          <v-btn-toggle
            v-model="filters.filling"
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
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import { dedupe } from '@/core/utils/array.ts';
import { Toast } from '@capacitor/toast';

definePage({
  meta: {
    title: 'Комиксы',
  },
})

const router = useRouter();

const currentPage = ref(1);

const filtersSheet = ref(false)

const filters = ref({
  authors: [] as string[],
  languages: [] as string[],
  tags: [] as string[],
  filling: 0 as 0|1|2,
})

const languages = ref<string[]>([]);
const tags = ref<string[]>([]);
const authors = ref<string[]>([]);

const comics = ref<ComicModel[]>([]);

const filterArrays = (f: string[], s: string[]) => (
  !s.length
  || f.some(e => s.includes(e))
)

const filterSingle = (f: string, s: string[]) => (
  !s.length
  || s.includes(f)
)

const comicsFiltered = computed(() => (
  comics.value.filter(item => {
    if ((filters.value.filling === 1 && !item.isFilled)
      || (filters.value.filling === 2 && item.isFilled)) {
      return false;
    }

    return filterArrays(item.tags, filters.value.tags)
      && filterArrays(item.authors, filters.value.authors)
      && filterSingle(item.language, filters.value.languages)
  })
))

const loadComics = async () => {
  comics.value = await ComicController.loadAll();
  languages.value = dedupe(comics.value.map(e => e.language)).sort();
  tags.value = dedupe(comics.value.map(e => e.tags).flat(1)).sort();
  authors.value = dedupe(comics.value.map(e => e.authors).flat(1)).sort();
}

loadComics();

const createComic = async () => {
  const comicId = await ComicController.save(new ComicModel());

  if (comicId) {
    Toast.show({ text: 'Комикс создан' })
    await router.push({
      name: '/comics/[id]/',
      params: { id: comicId },
    })
  }
}
</script>
