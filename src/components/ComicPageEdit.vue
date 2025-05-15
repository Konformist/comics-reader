<template>
  <v-card>
    <v-img
      class="mb-4"
      :src="url"
    />
    <v-card-item class="pb-0">
      <v-file-input
        v-model="image"
        accept="image/*"
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
        inputmode="url"
        label="Ссылка на картинку"
        rows="2"
        variant="solo-filled"
      />
    </v-card-item>
    <v-card-actions>
      <v-btn
        :disabled="!image && !from"
        :loading="loading"
        text="Загрузить"
        @click="onLoad()"
      />
      <v-btn
        class="ml-auto"
        color="error"
        :loading="loading"
        text="Удалить"
        @click="$emit('delete')"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
const from = defineModel('from', { default: '' });

const emit = defineEmits<{
  (e: 'upload', v: File | File[]): void
  (e: 'download', v: void): void
  (e: 'delete', v: void): void
}>();

defineProps<{
  url: string
  loading: boolean
}>();

const image = ref<File | null>(null);

const onLoad = () => {
  if (image.value) emit('upload', image.value);
  else if (from.value) emit('download');
};
</script>
