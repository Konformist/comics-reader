<template>
  <v-main scrollable>
    <v-container>
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
      <v-divider class="my-8" />
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
      <v-btn
        class="mt-4 w-100"
        :disabled="!canLoadImages"
        :loading="loading"
        text="Загрузить картинки"
        @click="onLoadImages()"
      />
      <v-divider class="my-8" />
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
            >
              <ComicPageEdit
                v-model:from="item.raw.from"
                :loading="loading"
                :url="item.raw.url"
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
    </v-container>
    <v-fab
      icon="$save"
      :loading="loading"
      @click="onSave()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import type { IComicImageDTO } from '@/core/entities/comic/ComicTypes.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';

definePage({
  meta: {
    title: 'Редактирование страниц',
    isBack: true,
  },
});

const route = useRoute('/comics/[id]/edit-pages');

const currentPage = ref(1);

const pages = ref(0);

const comicId = +(route.params.id || 0);
const comic = ref(new ComicModel());

const loadComic = async () => {
  if (!comicId) return;
  comic.value = await ComicController.load(comicId);
  pages.value = comic.value.images.length;
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
    if (cur.url) {
      acc = index + 1;
    }

    return acc;
  }, 0)
));

const imagesTemplate = ref('');
const imagesTemplateStart = ref(1);

const setTemplate = () => {
  comic.value?.images.forEach((image, index) => {
    image.from = imagesTemplate.value.replace('<ID>', (imagesTemplateStart.value + index).toString());
  });
};

const loading = ref(false);

const saveComic = async () => {
  await ComicController.save(comic.value);
};

const onSave = async () => {
  await saveComic();
  await loadComic();
  Toast.show({ text: 'Комикс сохранён' });
};

const uploadImage = async (item: IComicImageDTO, event: File | File[]) => {
  if (!event || Array.isArray(event)) return;

  await saveComic();
  await ComicController.saveFile(comic.value.id, item.id, event);
  await loadComic();
  Toast.show({ text: 'Комикс сохранён' });
};

const onLoadImage = async (item: IComicImageDTO) => {
  if (!item.from) return;

  try {
    loading.value = true;
    const result = await ParserController.loadImageRaw(item.from);
    await uploadImage(item, result);
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loading.value = false;
  }
};

const canLoadImages = computed(() => (
  comic.value.images.some((e) => e.from)
));

const onLoadImages = async () => {
  try {
    loading.value = true;
    await saveComic();

    for (const item of comic.value.images) {
      if (item.from && !item.url) {
        const result = await ParserController.loadImageRaw(item.from);
        await ComicController.saveFile(comic.value.id, item.id, result);
      }
    }

    await loadComic();
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loading.value = false;
  }
};

const delPage = async (item: IComicImageDTO) => {
  const index = comic.value.images.findIndex((e) => e.id === item.id);

  if (index === -1) return;

  const image = comic.value.images[index];

  try {
    loading.value = true;
    if (image.url) {
      const { value } = await Dialog.confirm({
        title: 'Подтверждение удаления',
        message: 'Удалить страницу?',
      });

      if (!value) return;

      await ComicController.deleteFile(comic.value.id, image.id);
    }

    comic.value.images.splice(index, 1);
    await saveComic();
    await loadComic();
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loading.value = false;
  }
};

const delPages = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить все страницы?',
  });

  if (!value) return;

  try {
    loading.value = true;
    await ComicController.deleteFiles(comic.value.id);
    await loadComic();
    Toast.show({ text: 'Комикс сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loading.value = false;
  }
};
</script>
