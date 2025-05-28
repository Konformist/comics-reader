<template>
  <v-main>
    <v-container class="pa-0">
      <div class="pa-4">
        <v-alert
          color="info"
          variant="tonal"
        >
          При создании или применении бекапа нужно перезапустить приложение
        </v-alert>
        <v-btn
          class="mt-4 w-100"
          :disabled="loadingGlobal || loading"
          text="Создать бекап"
          variant="tonal"
          @click="addBackup()"
        />
        <FilesTree
          v-if="backups.length"
          v-model="backupPath"
          class="mt-4"
          :loading="loading"
          :tree="backups"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="!backupPath || loadingGlobal || loading"
          text="Применить бекап"
          variant="tonal"
          @click="restoreBackup()"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="!backupPath || loadingGlobal || loading"
          text="Сохранить в Загрузки"
          variant="tonal"
          @click="saveBackupToGlobal()"
        />
        <v-btn
          class="mt-4 w-100"
          color="error"
          :disabled="!backupPath || loadingGlobal || loading"
          text="Удалить бекап"
          variant="tonal"
          @click="delBackup()"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-alert color="info" variant="tonal">
          Если есть бекап с таким же именем, он будет заменён загруженным
        </v-alert>
        <v-file-input
          v-model="backupFile"
          class="mt-4"
          label="Файл бекапа"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="!backupFile || loadingGlobal || loading"
          text="Сохранить бекап"
          variant="tonal"
          @click="loadBackup()"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-btn
          class="w-100"
          :disabled="loadingGlobal || loading"
          text="Изображения в Загрузки"
          variant="tonal"
          @click="saveImagesToGlobal()"
        />
        <v-btn
          class="mt-4 w-100"
          color="error"
          :disabled="loadingGlobal || loading"
          text="Изображения из Загрузок"
          variant="tonal"
          @click="loadImagesFromGlobal()"
        />
      </div>
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import { fileToBase64 } from '@/core/utils/image.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import Api from '@/core/api/Api.ts';
import type { ITreeDirectory, ITreeFile } from '@/plugins/WebApiPlugin.ts';
import useLoading from '@/composables/useLoading.ts';
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useLanguagesStore } from '@/stores/languages.ts';
import { useParsersStore } from '@/stores/parsers.ts';
import { useTagsStore } from '@/stores/tags.ts';

definePage({
  meta: {
    layout: 'entity',
    title: 'Бекапы',
  },
});

const comicsStore = useComicsStore();
const tagsStore = useTagsStore();
const authorsStore = useAuthorsStore();
const languagesStore = useLanguagesStore();
const parsersStore = useParsersStore();

const {
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
} = useLoading();

const backups = ref<ITreeDirectory[]>([]);

const loadBackupsTree = async () => {
  loadingStart();
  backups.value = [await Api.api('file/backups/tree')];
  loadingEnd();
};

loadBackupsTree();

const addBackup = async () => {
  try {
    loadingGlobalStart();
    await Api.api('backup/backup/add');
    await loadBackupsTree();
    Toast.show({ text: 'Бекап создан' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const backupPath = ref<ITreeFile>();

const restoreBackup = async () => {
  if (!backupPath.value) return;

  try {
    loadingGlobalStart();
    await Api.api('backup/backup/restore', { fileName: backupPath.value.name });
    await Promise.all([
      comicsStore.loadComicsForce(),
      tagsStore.loadTagsForce(),
      authorsStore.loadAuthorsForce(),
      languagesStore.loadLanguagesForce(),
      parsersStore.loadParsersForce(),
    ]);
    Toast.show({ text: 'Бекап применён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const delBackup = async () => {
  if (!backupPath.value) return;

  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить бекап?',
  });

  if (!value) return;

  try {
    await Api.api('backup/backup/del', { fileName: backupPath.value.name });
    await loadBackupsTree();
    Toast.show({ text: 'Бекап удалён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  }
};

const backupFile = ref<File>();

const loadBackup = async () => {
  if (!backupFile.value) return;

  try {
    loadingGlobalStart();
    const result = await fileToBase64(backupFile.value);
    await Api.api('file/backups/upload', {
      fileName: backupFile.value.name,
      file: result,
    });
    await loadBackupsTree();
    Toast.show({ text: 'Данные получены' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const saveBackupToGlobal = async (): Promise<void> => {
  if (!backupPath.value) return;

  try {
    await Api.api('file/backups/downloads', {
      fileName: backupPath.value.name,
    });
    Toast.show({ text: 'Бекап сохранен в Загрузки' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const saveImagesToGlobal = async () => {
  try {
    loadingGlobalStart();
    await Api.api('file/comics-images/downloads');
    Toast.show({ text: 'Картинки сохранены в Загрузки' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const loadImagesFromGlobal = async () => {
  try {
    loadingGlobalStart();
    await Api.api('file/comics-images/upload');
    Toast.show({ text: 'Картинки загружены из Загрузок' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
