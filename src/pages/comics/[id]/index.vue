<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <template v-if="comic.cover?.file?.url">
        <router-link
          v-if="chapters[0].id"
          :to="{
            name: '/chapters/[id]/read',
            params: { id: chapters[0].id },
          }"
        >
          <v-img :src="comic.cover.file.url" />
        </router-link>
        <v-img
          v-else
          :src="comic.cover.file.url"
        />
      </template>
      <div class="px-4 py-8">
        <h3 class="font-weight-medium">
          {{ comic.name || '—' }}
          <v-icon
            v-if="comic.name"
            icon="$copy"
            size="20"
            @click="onCopy(comic.name)"
          />
        </h3>
        <p
          v-if="comic.fromUrl"
          class="mt-2"
        >
          <a :href="comic.fromUrl">Ссылка на комикс</a>
          <v-icon
            class="ml-2"
            icon="$copy"
            size="20"
            @click="onCopy(comic.fromUrl)"
          />
        </p>
        <p
          v-if="authorsChips.length"
          class="mt-2 d-flex flex-wrap ga-1 align-center"
        >
          <b class="font-weight-medium">Авторы:</b>
          <v-chip
            v-for="item in authorsChips"
            :key="item.id"
            :text="item.name"
          />
        </p>
        <p
          v-if="languagesChips.length"
          class="mt-2 d-flex flex-wrap ga-1 align-center"
        >
          <b class="font-weight-medium">Язык:</b>
          <v-chip
            v-for="item in languagesChips"
            :key="item.id"
            :text="item.name"
          />
        </p>
        <p
          v-if="tagsChips.length"
          class="mt-2 d-flex flex-wrap ga-1 align-center"
        >
          <b class="font-weight-medium">Теги:</b>
          <v-chip
            v-for="item in tagsChips"
            :key="item.id"
            :text="item.name"
          />
        </p>
      </div>
      <v-divider />
      <v-list
        class="pa-0"
        density="comfortable"
        item-title="name"
        item-value="id"
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
    </v-container>
    <v-fab
      :disabled="loading || loadingGlobal"
      icon
    >
      <v-icon :icon="editOpened ? '$close' : '$edit'" />
      <v-speed-dial
        v-model="editOpened"
        activator="parent"
        location="top right"
        transition="scale-transition"
      >
        <v-btn
          key="2"
          color="secondary"
          text="Главы"
          :to="{
            name: '/chapters/[comicId]',
            params: { comicId: comic.id },
          }"
        />
        <v-btn
          key="1"
          color="secondary"
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
