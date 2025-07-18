<template>
  <v-main>
    <v-container class="pa-0 pb-16">
      <div class="pa-4">
        <v-textarea
          v-model.trim="chapter.name"
          auto-grow
          class="mb-4"
          label="Название"
          rows="2"
        />
        <v-btn
          class="w-100"
          color="error"
          :disabled="loadingGlobal"
          :loading="loading"
          text="Удалить главу"
          variant="tonal"
          @click="delChapter()"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-number-input
          v-model="pages"
          class="mb-4"
          label="Количество страниц"
          :min="minPages"
          @change="setPages()"
        />
        <v-textarea
          v-model.trim="imagesTemplate"
          auto-grow
          :autocapitalize="false"
          :autocomplete="false"
          class="mb-4"
          hint="Пример: https://domain.com/12/23/<ID>.jpeg"
          inputmode="url"
          label="Шаблон для автозаполнения"
          rows="2"
        />
        <v-number-input
          v-model="imagesTemplateStart"
          class="mb-4"
          control-variant="split"
          label="Начальный ID"
          :min="0"
          type="number"
        />
        <v-btn
          class="w-100"
          :disabled="!imagesTemplate || loadingGlobal"
          :loading="loading"
          text="Заполнить ссылки"
          variant="tonal"
          @click="setTemplate()"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-btn
          class="w-100"
          :disabled="!canLoadImages || loadingGlobal"
          :loading="loading"
          text="Загрузить только пустые"
          variant="tonal"
          @click="onLoadImages()"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="!canLoadImages || loadingGlobal"
          :loading="loading"
          text="Загрузить все изображения"
          variant="tonal"
          @click="onLoadImages(true)"
        />
        <v-btn
          class="mt-4 w-100"
          color="error"
          :disabled="chapter.pages.length === 0 || loadingGlobal"
          :loading="loading"
          text="Удалить все страницы"
          variant="tonal"
          @click="delPages()"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-data-iterator
          v-model:page="currentPage"
          :items="chapter.pages"
          items-per-page="20"
        >
          <template #header="{ pageCount, prevPage, nextPage }">
            <v-pagination
              v-model="currentPage"
              class="mb-4"
              :length="pageCount"
              @next="nextPage()"
              @prev="prevPage()"
            />
          </template>
          <template #default="{ items }">
            <v-row>
              <v-col
                v-for="item in items"
                :key="item.raw.id"
                cols="12"
              >
                <ChapterPageEdit
                  v-model:from-url="item.raw.fromUrl"
                  can-delete
                  :disabled="loading || loadingGlobal || !item.raw.id"
                  :size="item.raw.file?.size"
                  :src="item.raw.file?.url"
                  @delete="delPage(item.raw)"
                  @download="onLoadImage(item.raw)"
                  @pick="uploadImage(item.raw)"
                />
              </v-col>
            </v-row>
          </template>
          <template #footer="{ pageCount, prevPage, nextPage }">
            <v-pagination
              v-model="currentPage"
              class="mt-4"
              :length="pageCount"
              @next="nextPage()"
              @prev="prevPage()"
            />
          </template>
        </v-data-iterator>
      </div>
    </v-container>
    <v-fab
      :disabled="loading || loadingGlobal"
      icon="$save"
      @click="onSave()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import type ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';
import { Dialog } from '@capacitor/dialog';
import useLoading from '@/composables/useLoading.ts';
import ChapterPageController from '@/core/entities/chapter-page/ChapterPageController.ts';
import ChapterController from '@/core/entities/chapter/ChapterController.ts';
import ChapterModel from '@/core/entities/chapter/ChapterModel.ts';
import UI from '@/plugins/UIPlugin.ts';

definePage({
  meta: {
    layout: 'entity',
    title: 'Редактирование главы',
  },
});

const route = useRoute('/chapters/[id]/edit');
const router = useRouter();

const {
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
} = useLoading();

const currentPage = ref(1);

const pages = ref(0);

const chapterId = +(route.params.id || 0);

const chapter = ref(new ChapterModel());
const chapterReserve = ref(new ChapterModel());
const loadChapter = async () => {
  chapter.value = await ChapterController.load(chapterId);
  chapterReserve.value = new ChapterModel(chapter.value.getDTO());
  pages.value = chapter.value.pages.length;
};

const saveChapter = async () => {
  await ChapterController.save(chapter.value);
};

const savePageChapters = async () => {
  const forSave = chapter.value.pages;
  const exists = new Set(chapter.value.pages.map((e) => e.id));
  const forDelete = chapterReserve.value.pages
    .filter((e) => !exists.has(e.id))
    .map((e) => e.id);

  await ChapterPageController.sequenceRemove(forDelete);
  await ChapterPageController.sequenceSave(forSave);
};

const init = async () => {
  loadingStart();
  await loadChapter();
  if (!chapter.value.id) router.replace({ name: '/' });
  loadingEnd();
};

init();

const setPages = () => {
  console.log(pages.value);
  const diff = pages.value - chapter.value.pages.length;

  if (diff > 0) {
    for (let i = 0; i < diff; i++) chapter.value.addPage();
  } else if (diff < 0) {
    chapter.value.pages.splice(diff, Math.abs(diff));
  }
};

const minPages = computed(() => (
  chapter.value.pages
    .reduce((acc, cur, index) => (cur.file ? index + 1 : acc), 0)
));

const imagesTemplate = ref('');
const imagesTemplateStart = ref(1);

const setTemplate = () => {
  for (const [index, image] of chapter.value.pages.entries()) {
    image.fromUrl = imagesTemplate.value
      .replace('<ID>', (imagesTemplateStart.value + index).toString());
  }
};

const onSave = async () => {
  try {
    loadingGlobalStart();
    await saveChapter();
    await savePageChapters();
    await loadChapter();
    UI.toast({ text: 'Глава сохранена' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};

const uploadImage = async (item: ChapterPageModel) => {
  try {
    loadingGlobalStart();
    await saveChapter();
    await savePageChapters();
    await ChapterPageController.saveFile(item.id);
    await loadChapter();
    UI.toast({ text: 'Глава сохранена' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};

const onLoadImage = async (item: ChapterPageModel) => {
  if (!item.fromUrl) return;

  try {
    loadingGlobalStart();
    await saveChapter();
    await savePageChapters();
    await ChapterPageController.downloadFile(item.id, item.fromUrl);
    await loadChapter();
    UI.toast({ text: 'Глава сохранена' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};

const canLoadImages = computed(() => (
  chapter.value.pages.some((e) => e.fromUrl)
));

const onLoadImages = async (force = false) => {
  loadingGlobalStart();
  await saveChapter();
  await savePageChapters();

  let isError = false;

  try {
    for (const item of chapter.value.pages) {
      if (item.id && item.fromUrl && (!item.file || force)) {
        await ChapterPageController.downloadFile(item.id, item.fromUrl);
      }
    }
  } catch (error) {
    isError = true;
    UI.toast({ text: `Ошибка: ${error}` });
  }

  if (isError) {
    UI.toast({ text: 'Некоторые изображения не загружены' });
  } else {
    UI.toast({ text: 'Изображения загружены' });
  }

  await loadChapter();
  loadingGlobalEnd();
};

const delPage = async (item: ChapterPageModel) => {
  if (item.file) {
    const { value } = await Dialog.confirm({
      title: 'Подтверждение удаления',
      message: 'Удалить страницу с файлом?',
    });

    if (!value) return;
  }

  try {
    loadingGlobalStart();
    await saveChapter();
    await savePageChapters();
    await ChapterPageController.remove(item.id);
    await loadChapter();
    UI.toast({ text: 'Глава сохранена' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};

const delPages = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить все страницы?',
  });

  if (!value) return;

  try {
    loadingGlobalStart();
    await saveChapter();
    await ChapterPageController.sequenceRemove(chapter.value.pages.map((e) => e.id));
    await loadChapter();
    UI.toast({ text: 'Глава сохранена' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};

const delChapter = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить главу?',
  });

  if (!value) return;

  try {
    loadingGlobalStart();
    await ChapterController.remove(chapterId);
    UI.toast({ text: 'Глава удалена' });
    router.replace({
      name: '/comics/[id]/',
      params: { id: chapter.value.comicId },
    });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
