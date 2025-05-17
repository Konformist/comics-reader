<template>
  <v-main scrollable>
    <v-progress-linear
      v-if="loading"
      indeterminate
    />
    <v-container class="pa-0">
      <div class="px-4 py-8">
        <v-number-input
          v-model.number="pages"
          control-variant="hidden"
          label="Количество страниц"
          :min="minPages"
          @change="setPages()"
        />
        <v-btn
          class="w-100"
          color="error"
          :disabled="!comic.images.length"
          :loading="loading"
          text="Удалить все картинки"
          @click="delPages()"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-textarea
          v-model.trim="imagesTemplate"
          auto-grow
          :autocapitalize="false"
          :autocomplete="false"
          clearable
          hint="Пример: https://domain.com/12/23/<ID>.jpeg"
          inputmode="url"
          label="Шаблон для автозаполнения"
          persistent-hint
          rows="2"
        />
        <v-number-input
          v-model.number="imagesTemplateStart"
          class="mt-4"
          control-variant="split"
          label="Начальный ID"
          :min="0"
          type="number"
        />
        <v-btn
          class="w-100"
          :disabled="!imagesTemplate"
          :loading="loading"
          text="Заполнить ссылки"
          @click="setTemplate()"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-btn
          class="w-100"
          :disabled="!canLoadImages"
          :loading="loading"
          text="Загрузить только пустые"
          @click="onLoadImages()"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="!canLoadImages"
          :loading="loading"
          text="Загрузить все"
          @click="onLoadImages(true)"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-data-iterator
          v-model:page="currentPage"
          :items="comic.images"
          items-per-page="20"
        >
          <template #header="{ pageCount, prevPage, nextPage }">
            <v-pagination
              v-model="currentPage"
              class="mb-4"
              density="comfortable"
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
                <ComicPageEdit
                  v-model:from="item.raw.url"
                  :comic-id="comic.id"
                  :item="item.raw"
                  :loading-parent="loading"
                  @delete="delPage(item.raw)"
                  @download="onLoadImage(item.raw)"
                  @upload="uploadImage(item.raw, $event)"
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
      </div>
    </v-container>
    <v-fab
      :disabled="loading"
      icon="$save"
      @click="onSave()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import useLoading from '@/composables/useLoading.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import type { IComicImageUrl } from '@/core/entities/comic/ComicTypes.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';

definePage({
  meta: {
    title: 'Редактирование страниц',
    isBack: true,
  },
});

const route = useRoute('/comics/[id]/edit-pages');
const router = useRouter();
const { loading, loadingStart,loadingEnd } = useLoading();

const currentPage = ref(1);

const pages = ref(0);

const comicId = +(route.params.id || 0);

const comic = ref(new ComicModel());

const loadComic = async () => {
  comic.value = await ComicController.load(comicId);
  if (!comic.value.id) router.replace({ name: '/' });
  else pages.value = comic.value.images.length;
};

loadComic();

const setPages = () => {
  const diff = pages.value - comic.value.images.length;

  if (diff > 0) {
    for (let i = 0; i < diff; i++) {
      comic.value.addImage();
    }
  } else if (diff < 0) {
    comic.value.images.splice(diff, Math.abs(diff));
  }
};

const minPages = computed(() => (
  comic.value.images.reduce((acc, cur, index) => {
    if (cur.fileId) {
      acc = index + 1;
    }

    return acc;
  }, 0)
));

const imagesTemplate = ref('');
const imagesTemplateStart = ref(1);

const setTemplate = () => {
  comic.value?.images.forEach((image, index) => {
    image.url = imagesTemplate.value.replace('<ID>', (imagesTemplateStart.value + index).toString());
  });
};

const saveComic = async () => {
  await ComicController.save(comic.value);
};

const onSave = async () => {
  await saveComic();
  await loadComic();
  Toast.show({ text: 'Комикс сохранён' });
};

const uploadImage = async (item: IComicImageUrl, event: File | File[]) => {
  if (!event || Array.isArray(event)) return;

  try {
    loadingStart();
    await saveComic();
    await ComicController.saveFile(comic.value.id, item, event);
    await loadComic();
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingEnd();
  }
};

const onLoadImage = async (item: IComicImageUrl) => {
  if (!item.url) return;

  try {
    loadingStart();
    const result = await ParserController.loadImageRaw(item.url);
    await saveComic();
    await ComicController.saveFile(comic.value.id, item, result);
    await loadComic();
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingEnd();
  }
};

const canLoadImages = computed(() => (
  comic.value.images.some((e) => e.url)
));

const onLoadImages = async (force: boolean = false) => {
  loadingStart();
  await saveComic();

  try {
    for (const item of comic.value.images) {
      if (item.url && (!item.fileId || force)) {
        const result = await ParserController.loadImageRaw(item.url);
        await ComicController.saveFile(comic.value.id, item, result);
      }
    }
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  }

  await loadComic();
  Toast.show({ text: 'Комикс сохранён' });
  loadingEnd();
};

const delPage = async (item: IComicImageUrl) => {
  if (item.fileId) {
    const { value } = await Dialog.confirm({
      title: 'Подтверждение удаления',
      message: 'Удалить страницу с файлом?',
    });

    if (!value) return;
  }

  try {
    loadingStart();
    comic.value.images = comic.value.images.filter((e) => e.id !== item.id);
    await saveComic();
    await loadComic();
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingEnd();
  }
};

const delPages = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить все страницы?',
  });

  if (!value) return;

  try {
    loadingStart();
    comic.value.images = [];
    await saveComic();
    await loadComic();
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingEnd();
  }
};
</script>
