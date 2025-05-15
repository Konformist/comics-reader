<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <router-link
        :to="{
          name: '/comics/[id]/read',
          params: { id: comic.id },
        }"
      >
        <v-img :src="comic.image" />
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
      <div class="pa-4">
        <v-btn
          class="w-100"
          :disabled="!comic.images.length"
          :loading="loading"
          text="Читать"
          :to="{
            name: '/comics/[id]/read',
            params: { id: comic.id },
          }"
        />
        <v-btn
          class="mt-4 w-100"
          :loading="loading"
          text="Редактировать страницы"
          :to="{
            name: '/comics/[id]/edit-pages',
            params: { id: comic.id },
          }"
        />
        <v-btn
          class="mt-4 w-100"
          color="error"
          :loading="loading"
          text="Удалить"
          @click="deleteComic()"
        />
      </div>
    </v-container>
    <v-fab
      icon="$edit"
      :loading="loading"
      :to="{
        name: '/comics/[id]/edit',
        params: { id: comic.id },
      }"
    />
  </v-main>
</template>

<script lang="ts" setup>
import { Clipboard } from '@capacitor/clipboard';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
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

const comic = ref(new ComicModel());

const loadComic = async () => {
  const comicId = +route.params.id;

  if (!comicId) return;

  comic.value = await ComicController.load(comicId);
};

loadComic();

const languages = ref<LanguageObject[]>([]);
const languagesChips = computed(() => (
  languages.value.filter((e) => comic.value.language === e.id)
));

const loadLanguages = async () => {
  languages.value = await LanguageController.loadAll();
};

loadLanguages();

const authors = ref<AuthorObject[]>([]);
const authorsChips = computed(() => (
  authors.value.filter((e) => comic.value.authors.includes(e.id))
));

const loadAuthors = async () => {
  authors.value = await AuthorController.loadAll();
};

loadAuthors();

const tags = ref<TagObject[]>([]);
const tagsChips = computed(() => (
  tags.value.filter((e) => comic.value.tags.includes(e.id))
));

const loadTags = async () => {
  tags.value = await TagController.loadAll();
};

loadTags();

const loading = ref(false);

const deleteComic = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить комикс?',
  });

  if (!value) return;

  try {
    loading.value = true;
    await ComicController.delete(comic.value.id);
    Toast.show({ text: 'Комикс удалён' });
    router.replace({ name: '/' });
  } catch (e) {
    alert(e);
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loading.value = false;
  }
};

const onCopy = async (string: string) => {
  await Clipboard.write({ string });
  Toast.show({ text: 'Скопировано' });
};
</script>
