<template>
  <v-card>
    <v-img
      class="mb-4"
      :src="url"
    />
    <v-card-item>
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
        label="Ссылка на страницу"
        rows="2"
        variant="solo-filled"
      />
      <div class="d-flex">
        <v-number-input
          v-model.number="maxWidth"
          class="mr-4"
          control-variant="hidden"
          label="Изменить ширину"
          :min="0"
          variant="solo-filled"
        />
        <v-number-input
          v-model.number="maxHeight"
          control-variant="hidden"
          label="Изменить высоту"
          :min="0"
          variant="solo-filled"
        />
      </div>
    </v-card-item>
    <v-card-actions>
      <v-btn
        :loading="loading || localLoading"
        text="Загрузить"
        @click="$emit('download')"
      />
      <v-btn
        :disabled="!maxWidth && !maxHeight"
        :loading="loading || localLoading"
        text="Сжать"
        @click="resizeImage()"
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
import ComicController from '@/core/entities/comic/ComicController.ts';
import { Toast } from '@capacitor/toast';

const from = defineModel('from', { default: '' })

const emit = defineEmits<{
  (e: 'reload', v: void): void
  (e: 'upload', v: File|File[]): void
  (e: 'download', v: void): void
  (e: 'delete', v: void): void
}>()
const { comicId, fileId } = defineProps<{
  comicId: number
  fileId: number
  url: string
  loading: boolean
}>()

const localLoading = ref(false);

const maxWidth = ref(0);
const maxHeight = ref(0);

const resizeImage = async () => {
  try {
    localLoading.value = true;
    await ComicController.resizeComicFile(comicId, fileId, {
      maxWidth: maxWidth.value || undefined,
      maxHeight: maxHeight.value || undefined,
    })
    Toast.show({ text: `Изображение сжато` })
    emit('reload')
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` })
  } finally {
    localLoading.value = false;
  }
};
</script>
