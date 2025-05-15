<template>
  <v-card>
    <v-img
      class="mb-4"
      :src="url"
    />
    <v-card-item class="pb-0">
      <v-file-input
        label="Загрузить свою страницу"
        variant="solo-filled"
        @update:model-value="$emit('upload', $event)"
      />
      <v-textarea
        v-model.trim="from"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        inputmode="url"
        label="Ссылка на страницу"
        rows="2"
        variant="solo-filled"
      />
    </v-card-item>
    <v-card-actions>
      <v-btn
        :loading="loading || localLoading"
        text="Загрузить"
        @click="$emit('download')"
      />
      <v-btn
        class="ml-auto"
        color="error"
        :loading="loading || localLoading"
        text="Удалить"
        @click="$emit('delete')"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
const from = defineModel('from', { default: '' });

defineEmits<{
  (e: 'upload', v: File | File[]): void
  (e: 'download', v: void): void
  (e: 'resize', v: { maxWidth: number, maxHeight: number }): void
  (e: 'delete', v: void): void
}>();
defineProps<{
  comicId: number
  fileId: number
  url: string
  loading: boolean
}>();

const localLoading = ref(false);

const maxWidth = ref(0);
const maxHeight = ref(0);
</script>
