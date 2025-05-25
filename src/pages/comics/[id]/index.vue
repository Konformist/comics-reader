<template>
  <v-main>
    <v-container class="pa-0 pb-16 mb-4">
      <!--      <template v-if="comic.cover?.file?.url">-->
      <p class="pt-4">
        <v-skeleton-loader
          v-if="loading"
          type="image"
        />
        <v-img
          v-else
          class="mx-auto bg-grey-darken-4"
          height="300px"
          rounded="xl"
          :src="comic.cover.file?.url || '/'"
          width="200px"
        >
          <template #error>
            <div class="w-100 h-100 d-flex align-center justify-center">
              Нет изображения
            </div>
          </template>
        </v-img>
      </p>
      <v-divider class="my-4" />
      <h3 class="px-4 pt-4 font-weight-medium">
        {{ comic.name || '—' }}
        <v-icon
          v-if="comic.name"
          icon="$copy"
          size="20"
          @click="onCopy(comic.name)"
        />
      </h3>
      <v-divider class="my-4" />
      <template v-if="comic.fromUrl">
        <p class="px-4">
          <a :href="comic.fromUrl">Ссылка на комикс</a>
          <v-icon
            class="ml-2"
            icon="$copy"
            size="20"
            @click="onCopy(comic.fromUrl)"
          />
        </p>
        <v-divider class="my-4" />
      </template>
      <p class="px-4">
        <b class="font-weight-medium">Авторы:</b> {{ authorsChips.length ? authorsChips.map((e) => e.name).join(", ") : '—' }}
      </p>
      <v-divider class="my-4" />
      <p class="px-4">
        <b class="font-weight-medium">Язык:</b> {{ languagesChips.length ? languagesChips[0].name : '—' }}
      </p>
      <v-divider class="my-4" />
      <template v-if="tagsChips.length">
        <p class="px-4 d-flex flex-wrap ga-1 align-center">
          <v-chip
            v-for="item in tagsChips"
            :key="item.id"
            color="primary"
            :text="item.name"
            variant="tonal"
          />
        </p>
        <v-divider class="my-4" />
      </template>
      <p class="px-4">
        <v-list
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
      </p>
    </v-container>
    <v-fab
      :disabled="loading || loadingGlobal"
      icon
    >
      <v-fab-transition>
        <v-icon v-if="editOpened" icon="$close" />
        <v-icon v-else icon="$edit" />
      </v-fab-transition>
      <v-speed-dial
        v-model="editOpened"
        activator="parent"
        location="top right"
        transition="scale-transition"
      >
        <v-btn
          key="2"
          color="secondary"
          height="32"
          text="Главы"
          :to="{
            name: '/chapters/[comicId]',
            params: { comicId: comic.id },
          }"
        />
        <v-btn
          key="1"
          color="secondary"
          height="32"
          text="Комикс"
          :to="{
            name: '/comics/[id]/edit',
            params: { id: comic.id },
          }"
        />
      </v-speed-dial>
    </v-fab>
  </v-main>
</template>

<script lang="ts" setup>
import { useAuthorsStore } from '@/stores/authors.ts';
import { useLanguagesStore } from '@/stores/languages.ts';
import { useTagsStore } from '@/stores/tags.ts';
import { Clipboard } from '@capacitor/clipboard';
import { Toast } from '@capacitor/toast';
import useLoading from '@/composables/useLoading.ts';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import ChapterController from '@/core/entities/chapter/ChapterController.ts';
import ChapterModel from '@/core/entities/chapter/ChapterModel.ts';

definePage({
  meta: {
    title: 'Комикс',
    isBack: true,
  },
});

const route = useRoute('/comics/[id]/');
const router = useRouter();
const comicId = +route.params.id;

const tagsStore = useTagsStore();
const authorsStore = useAuthorsStore();
const languagesStore = useLanguagesStore();

const {
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
} = useLoading();

const editOpened = ref(false);

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

const comic = ref(new ComicModel());
const loadComic = async () => {
  comic.value = await ComicController.load(comicId);
};

const languagesChips = computed(() => (
  languagesStore.languages.filter((e) => comic.value.languageId === e.id)
));
const authorsChips = computed(() => (
  authorsStore.authors.filter((e) => comic.value.authors.includes(e.id))
));
const tagsChips = computed(() => (
  tagsStore.tags.filter((e) => comic.value.tags.includes(e.id))
));

const onCopy = async (string: string) => {
  await Clipboard.write({ string });
  Toast.show({ text: 'Скопировано' });
};

const init = async () => {
  loadingStart();
  await loadComic();

  if (!comic.value.id) {
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
