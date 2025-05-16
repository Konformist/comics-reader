<template>
  <v-alert
    v-if="error"
    color="error"
  >
    Ошибка загрузки изображения
  </v-alert>
  <v-alert
    v-if="!item.url && !item.fileId"
    color="warning"
  >
    Нет картинки или невозможно скачать
  </v-alert>
  <v-img
    v-if="image.url"
    :src="image.url"
    @error="error = true"
    @load="$emit('loaded')"
    @loadstart="error = false"
  >
    <div
      v-if="!loading"
      class="cursor-pointer w-50 position-absolute bottom-0 top-0 left-0"
      @click="$emit('prev')"
    />
    <div
      v-if="!loading"
      class="cursor-pointer w-50 position-absolute bottom-0 top-0 right-0"
      @click="$emit('next')"
    />
  </v-img>
</template>

<script setup lang="ts">
import ComicController from '@/core/entities/comic/ComicController.ts';
import type { IComicImageUrl } from '@/core/entities/comic/ComicTypes.ts';
import FileModel from '@/core/object-value/file/FileModel.ts';

const emit = defineEmits<{
  (e: 'loaded', v: void): void
  (e: 'next', v: void): void
  (e: 'prev', v: void): void
  (e: 'download', v: void): void
}>();
const { item, comicId } = defineProps<{
  loading: boolean
  comicId: number
  item: IComicImageUrl
}>();

const error = ref(false);

const image = ref(new FileModel());

const loadImage = async () => {
  if (item.fileId) {
    image.value = await ComicController.loadFile(comicId, item.id);
  } else if (item.url) {
    emit('download');
  }
};

watch(
  () => item.fileId,
  () => loadImage(),
  { immediate: true },
);
</script>
