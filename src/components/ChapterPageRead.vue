<template>
  <v-img
    v-if="item.file?.url"
    v-intersect.once="onRead"
    :max-height="maxHeight"
    :max-width="maxWidth"
    :src="item.file.url"
  />
  <div
    v-else
    class="w-100 d-flex justify-center align-center text-body-2 text-grey-darken-2"
    style="min-height: 400px"
  >
    Нет изображения
  </div>
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

const onRead = (isIntersect: boolean) => {
  if (!item.isRead && isIntersect) emit('read');
};
</script>
