<template>
  <v-app-bar>
    <v-btn
      icon="$arrow-left"
      slim
      @click="$router.back()"
    />
    <v-app-bar-title
      class="mr-8"
      :text="comic.name"
    />
  </v-app-bar>
  <v-main>
    <v-container class="pa-0">
      <v-data-iterator
        v-model:page="currentPage"
        :items="comic.images"
        items-per-page="1"
      >
        <template #header="{ page, pageCount, prevPage, nextPage }">
          <v-footer tag="header">
            <v-spacer />
            <v-btn
              density="comfortable"
              :disabled="page === 1"
              icon="$arrow-left"
              rounded
              variant="tonal"
              @click="prevPage"
            />
            <span class="d-inline-block px-4">{{ page }} / {{ pageCount }}</span>
            <v-btn
              density="comfortable"
              :disabled="page === pageCount"
              icon="$arrow-right"
              rounded
              variant="tonal"
              @click="nextPage"
            />
            <v-spacer />
          </v-footer>
        </template>
        <template #default="{ items, prevPage, nextPage }">
          <ComicPage
            v-for="item in items"
            :key="item.raw.id"
            v-model:from="item.raw.from"
            :url="item.raw.url"
            @download="onReloadImage(item.raw)"
            @next="nextPage()"
            @prev="prevPage()"
            @save="saveComic()"
            @upload="uploadImage(item.raw, $event)"
          />
        </template>
        <template #footer="{ page, pageCount, prevPage, nextPage }">
          <v-footer>
            <v-spacer />
            <v-btn
              density="comfortable"
              :disabled="page === 1"
              icon="$arrow-left"
              rounded
              variant="tonal"
              @click="prevPage"
            />
            <span class="d-inline-block px-4">{{ page }} / {{ pageCount }}</span>
            <v-btn
              density="comfortable"
              :disabled="page === pageCount"
              icon="$arrow-right"
              rounded
              variant="tonal"
              @click="nextPage"
            />
            <v-spacer />
          </v-footer>
        </template>
      </v-data-iterator>
    </v-container>
  </v-main>
</template>

<script lang="ts" setup>
import ComicPage from '@/components/ComicPage.vue';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import type { IComicImageDTO } from '@/core/entities/comic/ComicTypes.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import { Toast } from '@capacitor/toast';

const route = useRoute('/comics/[id]/read');

const comicId = +(route.params.id || 0);
const comic = ref(new ComicModel());

const currentPage = ref(1);

const loadComic = async () => {
  if (!comicId) return;
  comic.value = await ComicController.load(comicId);
}

loadComic();

const loading = ref(false);

const saveComic = async () => {
  await ComicController.save(comic.value);
}

const uploadImage = async (item: IComicImageDTO, event: File|File[]) => {
  if (!event || Array.isArray(event)) return;

  await saveComic();
  await ComicController.saveFile(comic.value.id, item.id, event);
  await loadComic();
  Toast.show({ text: 'Комикс сохранён' })
}

const onReloadImage = async (item: IComicImageDTO) => {
  if (!item.from) return;

  try {
    loading.value = true;
    const result = await ParserController.loadImageRaw(item.from);
    await uploadImage(item, result);
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
}
</script>
