<template>
  <v-app-bar>
    <v-btn
      :disabled="loading"
      icon="$arrow-left"
      slim
      @click="$router.back()"
    />
    <v-app-bar-title text="Комикс" />
  </v-app-bar>
  <v-main>
    <v-container>
      <v-img
        rounded
        :src="comic.image"
      />
      <h3
        v-if="comic.name"
        class="mt-4 font-weight-medium"
      >
        {{ comic.name }}
      </h3>
      <p
        v-if="comic.authors.length"
        class="mt-2 d-flex flex-wrap ga-1 align-center"
      >
        <b class="font-weight-medium">Авторы:</b>
        <v-chip
          v-for="author in comic.authors"
          :key="author"
          :text="author"
        />
      </p>
      <p
        v-if="comic.language"
        class="mt-2 d-flex flex-wrap ga-1 align-center"
      >
        <b class="font-weight-medium">Язык:</b>
        <v-chip
          :text="comic.language"
        />
      </p>
      <p
        v-if="comic.tags.length"
        class="mt-2 d-flex flex-wrap ga-1 align-center"
      >
        <b class="font-weight-medium">Теги:</b>
        <v-chip
          v-for="tag in comic.tags"
          :key="tag"
          :text="tag"
        />
      </p>
      <p class="mt-2 d-flex flex-wrap ga-1 align-center">
        <b class="font-weight-medium">Страниц:</b>
        <v-chip :text="comic.images.length" />
      </p>
      <v-btn
        class="mt-4 w-100"
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
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';

const route = useRoute('/comics/[id]/');
const router = useRouter();

const comic = ref(new ComicModel());

const loadComic = async () => {
  const comicId = +route.params.id;

  if (!comicId) return;

  comic.value = await ComicController.load(comicId);
}

loadComic();

const loading = ref(false);

const deleteComic = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить комикс?',
  });

  if (!value) return;

  loading.value = true;

  try {
    await ComicController.delete(comic.value.id);
    Toast.show({ text: 'Комикс удалён' })
    router.replace({ name: '/' })
  } catch (e) {
    alert(e)
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loading.value = false;
  }
}
</script>
