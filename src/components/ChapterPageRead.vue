<template>
  <v-img
    v-if="item.file?.url"
    :max-height="maxHeight"
    :max-width="maxWidth"
    :src="item.file.url"
  />
  <v-skeleton-loader
    v-else-if="item.fromUrl"
    min-height="200"
    min-width="100%"
    type="image"
  />
</template>

<script lang="ts" setup>
import type ChapterPageModel from '@/core/entities-v2/chapter-page/ChapterPageModel.ts';

const emit = defineEmits<{
  (e: 'download', v: void): void
}>();
const { item } = defineProps<{
  item: ChapterPageModel
  maxWidth?: string
  maxHeight?: string
}>();

if (!item.file?.url && item.fromUrl) {
  emit('download');
}
</script>
