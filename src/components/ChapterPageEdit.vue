<template>
  <v-card>
    <v-card-item class="py-4">
      <CustomImg
        class="mx-auto"
        height="300"
        :src="src"
        width="200"
      />
    </v-card-item>
    <v-card-text class="pt-4 text-body-1">
      Размер: {{ formatBytes(size || 0) }}
    </v-card-text>
    <v-card-item>
      <v-file-input
        v-model="imageFile"
        accept="image/*"
        :disabled="disabled"
        flat
        label="Загрузить картинку"
        variant="solo-filled"
      />
      <p class="my-4">
        Или
      </p>
      <v-textarea
        v-model.trim="fromUrl"
        auto-grow
        :autocapitalize="false"
        :autocomplete="false"
        :disabled="disabled"
        flat
        inputmode="url"
        label="Ссылка на картинку"
        rows="2"
        variant="solo-filled"
      />
    </v-card-item>
    <v-card-actions>
      <v-btn
        v-if="canDelete"
        class="ml-auto"
        color="error"
        :disabled="disabled"
        text="Удалить"
        @click="$emit('delete')"
      />
      <v-spacer />
      <v-btn
        :disabled="(!imageFile && !fromUrl) || disabled"
        text="Загрузить"
        @click="onLoad()"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { formatBytes } from '@/core/utils/format.ts';

const fromUrl = defineModel<string>('fromUrl', { default: '' });

const emit = defineEmits<{
  (e: 'upload', v: File): void
  (e: 'download', v: void): void
  (e: 'delete', v: void): void
}>();

defineProps<{
  size?: number
  src?: string
  canDelete?: boolean
  disabled: boolean
}>();

const imageFile = ref<File | null>(null);

const onLoad = () => {
  if (imageFile.value) emit('upload', imageFile.value);
  else if (fromUrl.value) emit('download');
};
</script>
