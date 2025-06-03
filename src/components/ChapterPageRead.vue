<template>
  <v-img
    v-intersect.once="onRead"
    :max-height="maxHeight"
    :max-width="maxWidth"
    :min-height="!load || error || !item.file?.url ? '100vh' : undefined"
    :src="item.file?.url"
    @error="onError()"
    @load="onLoad()"
  >
    <template #placeholder>
      <div class="w-100 h-100 d-flex justify-center align-center">
        <v-progress-circular
          indeterminate
        />
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
  (e: 'load', v: void): void
  (e: 'error', v: void): void
}>();

const { item, maxWidth = undefined, maxHeight = undefined } = defineProps<{
  item: ChapterPageModel
  maxWidth?: string
  maxHeight?: string
}>();

const load = ref(false);
const error = ref(false);

const onError = (): void => {
  load.value = false;
  error.value = true;
  emit('error');
};

const onLoad = (): void => {
  load.value = true;
  error.value = false;
  emit('load');
};

const onRead = (isIntersect: boolean): void => {
  if (item.file?.url
    && !item.isRead
    && isIntersect) {
    emit('read');
  }
};
</script>
