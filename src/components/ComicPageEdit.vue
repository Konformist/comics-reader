<template>
  <v-card>
    <v-img
      v-if="image.url"
      class="mb-4"
      :src="image.url"
    />
    <v-card-item class="pb-0">
      <v-file-input
        v-model="imageFile"
        accept="image/*"
        hide-details
        label="Загрузить картинку"
        variant="solo-filled"
      />
      <p class="my-4">
        Или
      </p>
      <v-textarea
        v-model.trim="from"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        inputmode="url"
        label="Ссылка на картинку"
        rows="2"
        variant="solo-filled"
      />
    </v-card-item>
    <v-card-actions>
      <v-btn
        :disabled="!imageFile && !from"
        :loading="loading"
        text="Загрузить"
        @click="onLoad()"
      />
      <v-btn
        class="ml-auto"
        color="error"
        :loading="loading"
        text="Удалить"
        @click="$emit('delete')"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import ComicController from '@/core/entities/comic/ComicController.ts';
import type { IComicImageUrl } from '@/core/entities/comic/ComicTypes.ts';
import FileModel from '@/core/object-value/file/FileModel.ts';

const from = defineModel('from', { default: '' });

const emit = defineEmits<{
  (e: 'upload', v: File | File[]): void
  (e: 'download', v: void): void
  (e: 'delete', v: void): void
}>();

const { item, comicId } = defineProps<{
  comicId: number
  item: IComicImageUrl
  loading: boolean
}>();

const image = ref(new FileModel());

const loadImage = async () => {
  if (item.fileId) {
    image.value = await ComicController.loadFile(comicId, item.id);
  }
};

watch(
  () => item.fileId,
  () => loadImage(),
  { immediate: true },
);

const imageFile = ref<File | null>(null);

const onLoad = () => {
  if (imageFile.value) emit('upload', imageFile.value);
  else if (from.value) emit('download');
};
</script>
