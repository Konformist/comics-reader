<template>
  <v-img
    v-if="item.file?.url"
    :max-height="maxHeight"
    :max-width="maxWidth"
    :src="item.file.url"
  />
  <div
    v-else
    class="w-100 d-flex justify-center align-center"
    style="min-height: 400px"
  >
    Нет изображения
  </div>
</template>

<script lang="ts" setup>
import type ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';

const { item } = defineProps<{
  item: ChapterPageModel
  maxWidth?: string
  maxHeight?: string
}>();
</script>
