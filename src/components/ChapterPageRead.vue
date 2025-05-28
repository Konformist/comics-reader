<template>
  <v-img
    v-intersect.once="onRead"
    :max-height="maxHeight"
    :max-width="maxWidth"
    :min-height="error || !item.file?.url ? 400 : undefined"
    :src="item.file?.url"
    @error="error = true"
  >
    <template #placeholder>
      <div class="w-100 h-100 d-flex justify-center align-center text-body-2 text-grey-darken-2">
        Нет изображения
      </div>
    </template>
    <template #error>
      <div class="w-100 h-100 d-flex justify-center align-center text-body-2 text-grey-darken-2">
        Нет изображения
      </div>
    </template>
  </v-img>
</template>

<script lang="ts" setup>
import type ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';

const emit = defineEmits<{
  (e: 'read', v: void): void
}>();

const { item } = defineProps<{
  item: ChapterPageModel
  maxWidth?: string
  maxHeight?: string
}>();

const error = ref(false);

const onRead = (isIntersect: boolean) => {
  if (!item.isRead && isIntersect) emit('read');
};
</script>
