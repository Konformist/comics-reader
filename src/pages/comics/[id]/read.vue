<template>
  <v-main scrollable>
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
              :loading="loading"
              rounded
              variant="tonal"
              @click="prevPage"
            />
            <span class="d-inline-block px-4">{{ page }} / {{ pageCount }}</span>
            <v-btn
              density="comfortable"
              :disabled="page === pageCount"
              icon="$arrow-right"
              :loading="loading"
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
            :comic-id="comic.id"
            :item="item.raw"
            :loading="loading"
            @download="onLoadImage(item.raw)"
            @loaded="startTimer(nextPage)"
            @next="nextPage()"
            @prev="prevPage()"
          />
        </template>
        <template #footer="{ page, pageCount, prevPage, nextPage }">
          <v-footer>
            <v-spacer />
            <v-btn
              density="comfortable"
              :disabled="page === 1"
              icon="$arrow-left"
              :loading="loading"
              rounded
              variant="tonal"
              @click="prevPage"
            />
            <span class="d-inline-block px-4">{{ page }} / {{ pageCount }}</span>
            <v-btn
              density="comfortable"
              :disabled="page === pageCount"
              icon="$arrow-right"
              :loading="loading"
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
import type { IComicImageUrl } from '@/core/entities/comic/ComicTypes.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import FileModel from '@/core/object-value/file/FileModel.ts';
import { useAppStore } from '@/stores/app.ts';
import { KeepAwake } from '@capacitor-community/keep-awake';
import { Capacitor } from '@capacitor/core';
import { StatusBar } from '@capacitor/status-bar';
import { Toast } from '@capacitor/toast';

definePage({
  meta: {
    title: 'Чтение',
    isBack: true,
  },
});

const route = useRoute('/comics/[id]/read');
const appStore = useAppStore();

onMounted(async () => {
  if ((await KeepAwake.isSupported()).isSupported) {
    await KeepAwake.keepAwake();
  }

  if (Capacitor.isNativePlatform()) {
    await StatusBar.hide();
  }
});

onBeforeUnmount(async () => {
  if ((await KeepAwake.isSupported()).isSupported) {
    await KeepAwake.allowSleep();
  }

  if (Capacitor.isNativePlatform()) {
    await StatusBar.show();
  }
});

const comicId = +(route.params.id || 0);

const images = ref<FileModel[]>([]);
const loadImages = async () => {
  images.value = await ComicController.loadFiles(comicId);
};

loadImages();

const comic = ref(new ComicModel());

const currentPage = ref<number>(+(route.query.page ?? 1));

const loadComic = async () => {
  if (!comicId) return;
  comic.value = await ComicController.load(comicId);
};

loadComic();

let readTimer = 0;

const startTimer = (nextPage: () => void) => {
  if (!appStore.settings.autoReading) return;

  readTimer = setTimeout(() => {
    nextPage();
    readTimer = 0;
  }, appStore.settings.autoReadingTimeout * 1000);
};

onBeforeUnmount(() => {
  if (readTimer) {
    clearTimeout(readTimer);
    readTimer = 0;
  }
});

watch(
  currentPage,
  () => {
    if (readTimer) {
      clearTimeout(readTimer);
      readTimer = 0;
    }
  },
);

const loading = ref(false);

const onLoadImage = async (item: IComicImageUrl) => {
  if (!item.url) return;

  try {
    loading.value = true;
    const result = await ParserController.loadImageRaw(item.url);
    await ComicController.saveFile(comic.value.id, item, result);
    await loadComic();
    await loadImages();
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loading.value = false;
  }
};
</script>
