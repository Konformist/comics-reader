<template>
  <v-card>
    <template v-if="image?.url">
      <v-img :src="image.url" />
      <v-card-text class="text-body-1">
        Размер: {{ formatBytes(image.size) }}
      </v-card-text>
    </template>
    <v-card-item class="pb-0">
      <v-file-input
        v-model="imageFile"
        accept="image/*"
        :disabled="disabled"
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
        :disabled="disabled"
        inputmode="url"
        label="Ссылка на картинку"
        rows="2"
        variant="solo-filled"
      />
      <v-alert color="error">
        Только для исправления багов загрузки
      </v-alert>
      <v-select
        v-model="item.fileId"
        class="mt-2"
        item-title="path"
        item-value="id"
        :items="files || []"
        variant="solo-filled"
      />
    </v-card-item>
    <v-card-actions>
      <v-btn
        :disabled="(!imageFile && !from) || disabled"
        text="Загрузить"
        @click="onLoad()"
      />
      <v-btn
        class="ml-auto"
        color="error"
        :disabled="disabled"
        text="Удалить"
        @click="$emit('delete')"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { IComicImageUrl } from '@/core/entities/comic/ComicTypes.ts';
import type { IFileDTO } from '@/core/object-value/file/FileTypes.ts';
import { formatBytes } from '@/core/utils/format.ts';
import FileModel from '@/core/object-value/file/FileModel.ts';

const from = defineModel('from', { default: '' });

const emit = defineEmits<{
  (e: 'upload', v: File | File[]): void
  (e: 'download', v: void): void
  (e: 'delete', v: void): void
}>();

defineProps<{
  files?: IFileDTO[]
  item: IComicImageUrl
  image?: FileModel
  disabled: boolean
}>();

const imageFile = ref<File | null>(null);

const onLoad = () => {
  if (imageFile.value) emit('upload', imageFile.value);
  else if (from.value) emit('download');
};
</script>
