<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <router-link
        v-if="comic.cover?.file?.url"
        :to="{
          name: '/comics/[id]/chapter-read',
          params: { id: comic.id },
        }"
      >
        <v-img :src="comic.cover.file.url" />
      </router-link>
      <div class="pa-4">
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
      <v-list>
        <v-list-item
          v-for="(chapter, index) in chapters"
          :key="chapter.id"
          :title="chapter.name || `Глава ${index + 1}`"
          :to="{
            name: '/comics/[id]/chapter-read',
            params: { id: chapter.id },
          }"
        >
          <template #append>
            <v-list-item-action end>
              <v-btn
                density="comfortable"
                :disabled="loadingGlobal"
                icon="$edit"
                :to="{
                  name: '/comics/[id]/chapter-edit',
                  params: { id: chapter.id, chapterId: chapter.id },
                }"
                variant="tonal"
                @click.prevent
              />
            </v-list-item-action>
          </template>
        </v-list-item>
      </v-list>
      <v-divider />
      <div class="px-4 py-8">
        <v-btn
          class="w-100"
          text="Добавить главу"
          @click="createChapter()"
        />
      </div>
    </v-container>
    <v-fab
      :disabled="loading || loadingGlobal"
      icon="$edit"
      :to="{
        name: '/comics/[id]/edit',
        params: { id: comic.id },
      }"
    />
  </v-main>
</template>

<script lang="ts" setup>
import { Clipboard } from '@capacitor/clipboard';
import { Toast } from '@capacitor/toast';
import useLoading from '@/composables/useLoading.ts';
import ComicController from '@/core/entities-v2/comic/ComicController.ts';
import ComicModel from '@/core/entities-v2/comic/ComicModel.ts';
import ChapterController from '@/core/entities-v2/chapter/ChapterController.ts';
import ChapterModel from '@/core/entities-v2/chapter/ChapterModel.ts';
import AuthorController from '@/core/entities-v2/author/AuthorController.ts';
import type AuthorModel from '@/core/entities-v2/author/AuthorModel.ts';
import LanguageController from '@/core/entities-v2/language/LanguageController.ts';
import type LanguageModel from '@/core/entities-v2/language/LanguageModel.ts';
import TagController from '@/core/entities-v2/tag/TagController.ts';
import type TagModel from '@/core/entities-v2/tag/TagModel.ts';

definePage({
  meta: {
    title: 'Комикс',
    isBack: true,
  },
});

const route = useRoute('/comics/[id]/');
const router = useRouter();
const {
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
} = useLoading();

const comicId = +route.params.id;

const chapters = ref<ChapterModel[]>([]);

const loadChapters = async () => {
  chapters.value = await ChapterController.loadAll(comicId);
};

const comic = ref(new ComicModel());

const loadComic = async () => {
  comic.value = await ComicController.load(comicId);
};

const languages = ref<LanguageModel[]>([]);
const languagesChips = computed(() => (
  languages.value.filter((e) => comic.value.languageId === e.id)
));

const loadLanguages = async () => {
  languages.value = await LanguageController.loadAll();
};

const authors = ref<AuthorModel[]>([]);
const authorsChips = computed(() => (
  authors.value.filter((e) => comic.value.authors.includes(e.id))
));

const loadAuthors = async () => {
  authors.value = await AuthorController.loadAll();
};

const tags = ref<TagModel[]>([]);
const tagsChips = computed(() => (
  tags.value.filter((e) => comic.value.tags.includes(e.id))
));

const loadTags = async () => {
  tags.value = await TagController.loadAll();
};

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
      loadLanguages(),
      loadAuthors(),
      loadTags(),
      loadChapters(),
    ]);
  }

  loadingEnd();
};

init();

const createChapter = async () => {
  const chapterId = await ChapterController.save(new ChapterModel({
    comicId,
  }));

  if (chapterId) {
    Toast.show({ text: 'Глава создана' });
    await router.push({
      name: '/comics/[id]/chapter-edit',
      params: { id: chapterId.toString() },
    });
  }
};
</script>
