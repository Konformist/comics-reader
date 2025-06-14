<template>
  <div class="position-relative swiper-zoom-container">
    <img
      v-intersect.once="onIntersect"
      :src="item.file?.url"
      :style="{
        display: 'block',
        maxWidth: maxWidth,
        maxHeight: maxHeight,
        height: loading || error || !item.file?.url ? '100vh' : undefined,
      }"
      @error="onError()"
      @load="onLoad()"
    >
    <div
      class="position-absolute w-100 h-100 d-flex justify-center align-center"
      style="inset: 0"
    >
      <v-skeleton-loader
        v-if="loading"
        class="rounded-0"
        height="100%"
        type="ossein"
        width="100%"
      />
      <div
        v-else-if="error"
        class="text-body-2 text-grey-darken-2"
      >
        Ошибка загрузки
      </div>
      <div
        v-else-if="!item.file?.url"
        class="text-body-2 text-grey-darken-2"
      >
        Нет изображения
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';

const emit = defineEmits<{
  (e: 'read', v: void): void
  (e: 'load', v: void): void
  (e: 'error', v: void): void
}>();

const {
  item,
  maxWidth = undefined,
  maxHeight = undefined,
} = defineProps<{
  item: ChapterPageModel
  maxWidth?: string
  maxHeight?: string
}>();

const error = ref(false);
const loading = ref(!!item.file?.url);

const intersect = ref(false);

const onRead = (): void => {
  if (intersect.value
    && item.file?.url
    && !error.value
    && !loading.value
    && !item.isRead) {
    emit('read');
  }
};

const onIntersect = (isIntersect: boolean) => {
  if (isIntersect) {
    intersect.value = isIntersect;
    onRead();
  }
};

const onError = (): void => {
  loading.value = false;
  error.value = true;
  emit('error');
};

const onLoad = (): void => {
  loading.value = false;
  error.value = false;
  onRead();
  emit('load');
};
</script>
