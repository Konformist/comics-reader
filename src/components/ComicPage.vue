<template>
  <v-alert
    v-if="error"
    color="error"
  >
    Ошибка загрузки изображения
  </v-alert>
  <v-alert
    v-if="!url && !from"
    color="warning"
  >
    Нет картинки или невозможно скачать
  </v-alert>
  <v-img
    v-if="url"
    :src="url"
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
const emit = defineEmits<{
  (e: 'loaded', v: void): void
  (e: 'next', v: void): void
  (e: 'prev', v: void): void
  (e: 'download', v: void): void
}>();
const { url, from } = defineProps<{
  loading: boolean
  from: string
  url: string
}>();

if (!url && from) {
  emit('download');
}

const error = ref(false);
</script>
