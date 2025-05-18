<template>
  <v-main scrollable>
    <v-progress-linear
      v-if="loading"
      indeterminate
    />
    <v-container class="pa-0">
      <router-link
        v-if="cover.url"
        :to="{
          name: '/comics/[id]/read',
          params: { id: comic.id },
        }"
      >
        <v-img :src="cover.url" />
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
          v-if="comic.url"
          class="mt-2"
        >
          <a :href="comic.url">Ссылка на комикс</a>
          <v-icon
            class="ml-2"
            icon="$copy"
            size="20"
            @click="onCopy(comic.url)"
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
        <p class="mt-2 d-flex flex-wrap ga-1 align-center">
          <b class="font-weight-medium">Страниц:</b>
          <v-chip :text="comic.images.length" />
        </p>
        <p class="mt-2 d-flex flex-wrap ga-1 align-center">
          <b class="font-weight-medium">Загружено:</b>
          <v-chip :text="`${comic.imagesFilled.length} / ${comic.images.length}`" />
        </p>
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-btn
          class="w-100"
          :disabled="!comic.images.length || loading"
          text="Читать"
          :to="{
            name: '/comics/[id]/read',
            params: { id: comic.id },
          }"
        />
      </div>
      <template v-if="comic.images.length && !loading">
        <v-divider />
        <div class="px-4 py-8">
          <v-row>
            <v-col
              v-for="(image, index) in comic.images"
              :key="image.id"
              class="pa-2"
              cols="6"
            >
              <v-card
                height="250"
                :image="getImage(image.fileId)?.url"
                :to="{
                  name: '/comics/[id]/read',
                  params: { id: comic.id },
                  query: { page: index + 1 },
                }"
              />
            </v-col>
          </v-row>
        </div>
      </template>
    </v-container>
    <v-fab
      :disabled="loading"
      icon="$edit"
      :to="{
        name: '/comics/[id]/edit',
        params: { id: comic.id },
      }"
    />
  </v-main>
</template>

<script lang="ts" setup>
import useLoading from '@/composables/useLoading.ts';
import { Clipboard } from '@capacitor/clipboard';
import { Toast } from '@capacitor/toast';
import FileModel from '@/core/object-value/file/FileModel.ts';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import AuthorController from '@/core/object-value/author/AuthorController.ts';
import type AuthorObject from '@/core/object-value/author/AuthorObject.ts';
import LanguageController from '@/core/object-value/language/LanguageController.ts';
import type LanguageObject from '@/core/object-value/language/LanguageObject.ts';
import TagController from '@/core/object-value/tag/TagController.ts';
import type TagObject from '@/core/object-value/tag/TagObject.ts';

definePage({
  meta: {
    title: 'Комикс',
    isBack: true,
  },
});

const route = useRoute('/comics/[id]/');
const router = useRouter();
const { loading, loadingStart,loadingEnd } = useLoading();

const comicId = +route.params.id;

const cover = ref(new FileModel());
const loadCover = async () => {
  cover.value = await ComicController.loadCover(comicId);
};

const images = ref<FileModel[]>([]);

const getImage = (id: number) => (images.value.find((e) => e.id === id));

const loadImages = async () => {
  images.value = await ComicController.loadFiles(comicId);
};

const comic = ref(new ComicModel());

const loadComic = async () => {
  comic.value = await ComicController.load(comicId);
};

const languages = ref<LanguageObject[]>([]);
const languagesChips = computed(() => (
  languages.value.filter((e) => comic.value.language === e.id)
));

const loadLanguages = async () => {
  languages.value = await LanguageController.loadAll();
};

const authors = ref<AuthorObject[]>([]);
const authorsChips = computed(() => (
  authors.value.filter((e) => comic.value.authors.includes(e.id))
));

const loadAuthors = async () => {
  authors.value = await AuthorController.loadAll();
};

const tags = ref<TagObject[]>([]);
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
      loadCover(),
      loadImages(),
    ]);
  }

  loadingEnd();
};
init();
</script>
