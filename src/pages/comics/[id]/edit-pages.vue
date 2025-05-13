<template>
  <v-app-bar>
    <v-btn
      icon="$arrow-left"
      slim
      @click="$router.back()"
    />
    <v-app-bar-title text="Редактирование страниц" />
  </v-app-bar>
  <v-main>
    <v-container>
      <v-number-input
        v-model="pages"
        label="Количество страниц"
        :min="minPages"
      />
      <v-btn />
      <v-divider class="mt-4 mb-8" />
      <v-textarea
        v-model.trim="imagesTemplate"
        auto-grow
        clearable
        hint="Пример: https://domain.com/12/23/<ID>.jpeg"
        label="Шаблон для автозаполнения"
        persistent-hint
        rows="2"
      />
      <v-text-field
        v-model.number="imagesTemplateStart"
        class="mt-4"
        clearable
        label="Начальный ID"
        type="number"
      />
      <v-btn
        class="w-100"
        :disabled="!imagesTemplate"
        text="Заполнить"
        @click="setTemplate()"
      />
      <v-divider class="my-8" />
      <v-data-iterator
        v-model:page="currentPage"
        :items="comic.images"
        items-per-page="20"
      >
        <template #default="{ items }">
          <v-row>
            <v-col
              v-for="item in items"
              :key="item.raw.id"
            >
              <v-card>
                <v-img
                  class="mb-4"
                  :src="item.raw.url"
                />
                <v-card-item>
                  <v-file-input
                    label="Загрузить свою страницу"
                    variant="solo-filled"
                    @update:model-value="uploadImage(item.raw, $event)"
                  />
                  <v-textarea
                    v-model.trim="item.raw.from"
                    auto-grow
                    label="Ссылка на страницу"
                    rows="2"
                    variant="solo-filled"
                  />
                </v-card-item>
                <v-card-actions>
                  <v-btn
                    :loading="loading"
                    text="Перезагрузить"
                    @click="onReloadImage(item.raw)"
                  />
                  <v-btn
                    class="ml-auto"
                    color="error"
                    :loading="loading"
                    text="Удалить"
                    @click="delPage(item.raw)"
                  />
                </v-card-actions>
              </v-card>
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
      @click="saveComic()"
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

const route = useRoute('/comics/[id]/edit-pages');

const currentPage = ref(1);

const comicId = +(route.params.id || 0);
const comic = ref(new ComicModel());

const loadComic = async () => {
  if (!comicId) return;
  comic.value = await ComicController.load(comicId);
}

loadComic();

const pages = computed({
  get () {
    return comic.value.images.length;
  },
  set (value) {
    const diff = value - comic.value.images.length;

    if (diff > 0) {
      for (let i = 0; i < diff; i++) {
        comic.value.addImage();
      }
    } else if (diff < 0) {
      comic.value.images.splice(-1, Math.abs(diff));
    }
  },
});

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
  })
};

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

const delPage = async (item: IComicImageDTO) => {
  const index = comic.value.images.findIndex(e => e.id === item.id);

  if (index === -1) return;

  const image = comic.value.images.splice(index, 1)[0];

  if (image.url) {
    const { value } = await Dialog.confirm({
      title: 'Подтверждение удаления',
      message: 'Удалить страницу?',
    });

    if (!value) return;

    await ComicController.deleteFile(comic.value.id, image.id)
  }

  await saveComic();
  await loadComic();
  Toast.show({ text: 'Комикс сохранён' })
};
</script>
