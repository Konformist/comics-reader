<template>
  <v-card>
    <v-card-item class="py-4">
      <v-img
        class="mx-auto bg-grey-darken-3"
        height="300px"
        rounded="xl"
        :src="item.file?.url || '/'"
        width="200px"
      >
        <template #error>
          <div class="w-100 h-100 d-flex align-center justify-center text-body-2 text-grey-darken-2">
            Нет изображения
          </div>
        </template>
      </v-img>
    </v-card-item>
    <v-card-text class="pt-4 text-body-1">
      Размер: {{ formatBytes(item.file?.size || 0) }}
    </v-card-text>
    <v-card-item>
      <v-file-input
        v-model="imageFile"
        accept="image/*"
        :disabled="disabled"
        flat
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
        flat
        inputmode="url"
        label="Ссылка на картинку"
        rows="2"
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
import { formatBytes } from '@/core/utils/format.ts';
import type ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';

const from = defineModel('from', { default: '' });

const emit = defineEmits<{
  (e: 'upload', v: File | File[]): void
  (e: 'download', v: void): void
  (e: 'delete', v: void): void
}>();

defineProps<{
  item: ChapterPageModel
  disabled: boolean
}>();

const imageFile = ref<File | null>(null);

const onLoad = () => {
  if (imageFile.value) emit('upload', imageFile.value);
  else if (from.value) emit('download');
};
</script>
