<template>
  <v-alert
    v-if="error"
    class="mb-4"
    color="error"
  >
    Ошибка загрузки изображения
  </v-alert>
  <div
    v-if="!url"
    class="pa-4"
  >
    <v-file-input
      label="Загрузить свою страницу"
      @update:model-value="$emit('upload', $event)"
    />
    <v-textarea
      v-model.trim="from"
      label="Ссылка на страницу"
      rows="2"
    />
    <v-btn
      class="w-100"
      :loading="loading"
      text="Загрузить"
      @click="$emit('download')"
    />
    <v-btn
      class="mt-2 w-100"
      :loading="loading"
      text="Сохранить"
      @click="$emit('save')"
    />
  </div>
  <v-img
    v-else
    :src="url"
    @error="error = true"
    @loadstart="error = false"
  >
    <div
      class="cursor-pointer w-50 position-absolute bottom-0 top-0 left-0"
      @click="$emit('prev')"
    />
    <div
      class="cursor-pointer w-50 position-absolute bottom-0 top-0 right-0"
      @click="$emit('next')"
    />
  </v-img>
</template>

<script setup lang="ts">
const from = defineModel<string>('from', { default: '' });

defineEmits<{
  (e: 'next', v: void): void;
  (e: 'prev', v: void): void;
  (e: 'save', v: void): void;
  (e: 'download', v: void): void;
  (e: 'upload', v: File|File[]): void;
}>()
defineProps<{
  url: string
  loading: boolean
}>()

const error = ref(false);
</script>
