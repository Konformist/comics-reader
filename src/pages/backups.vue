<template>
  <v-main>
    <v-container class="pa-0">
      <div class="px-4 py-8">
        <v-btn
          class="w-100"
          :disabled="loadingGlobal || loading"
          text="Создать бекап"
          @click="addBackup()"
        />
        <FilesTree
          v-if="backups.length"
          v-model="backupPath"
          class="mt-4"
          :loading="loading"
          rounded
          :tree="backups"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="!backupPath || loadingGlobal || loading"
          text="Применить бекап"
          @click="getBackup()"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="!backupPath || loadingGlobal || loading"
          text="Сохранить в Документы"
          @click="saveBackupToGlobal()"
        />
        <v-btn
          class="mt-4 w-100"
          color="error"
          :disabled="!backupPath || loadingGlobal || loading"
          text="Удалить бекап"
          @click="delBackup()"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-alert color="info">
          Если есть бекап с таким же именем, он будет заменён загруженным
        </v-alert>
        <v-file-input
          v-model="backupFile"
          class="mt-4"
          label="Файл бекапа"
        />
        <v-btn
          class="w-100"
          :disabled="!backupFile || loadingGlobal || loading"
          text="Сохранить бекап"
          @click="loadBackup()"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-btn
          class="w-100"
          :disabled="loadingGlobal || loading"
          text="Сохранить файлы в Документы"
          @click="saveImagesToGlobal()"
        />
        <v-btn
          class="mt-4 w-100"
          color="error"
          :disabled="loadingGlobal || loading"
          text="Загрузить файлы из Документов"
          @click="loadImagesFromGlobal()"
        />
      </div>
    </v-container>
  </v-main>
</template>
<script setup lang="ts">
import useLoading from '@/composables/useLoading.ts';
import server from '@/core/middleware/server.ts';
import { APP_NAME, BACKUPS_DIRECTORY, COMICS_FILES_DIRECTORY } from '@/core/middleware/variables.ts';
import type { ITreeDirectory } from '@/core/object-value/file/FileTypes.ts';
import { Dialog } from '@capacitor/dialog';
import { Directory, Filesystem } from '@capacitor/filesystem';
import { Toast } from '@capacitor/toast';

definePage({
  meta: {
    title: 'Бекапы',
  },
});

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
  backups.value = await server.getTree(BACKUPS_DIRECTORY);
  loadingEnd();
};

loadBackupsTree();

const addBackup = async () => {
  try {
    await server.addBackup();
    await loadBackupsTree();
    Toast.show({ text: 'Бекап создан' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  }
};

const backupPath = ref('');

const getBackup = async () => {
  try {
    await server.getBackup(backupPath.value);
    Toast.show({ text: 'Бекап применён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  }
};

const delBackup = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить бекап?',
  });

  if (!value) return;

  try {
    await server.delBackup(backupPath.value);
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
    const result = await backupFile.value.text();
    await server.setBackup(result, backupFile.value.name);
    await loadBackupsTree();
    Toast.show({ text: 'Данные получены' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const saveBackupToGlobal = async (): Promise<void> => {
  try {
    await Filesystem.mkdir({
      path: `${APP_NAME}/${BACKUPS_DIRECTORY}`,
      directory: Directory.Documents,
      recursive: true,
    });
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) { /* empty */ }

  try {
    loadingGlobalStart();
    await Filesystem.copy({
      from: backupPath.value,
      directory: Directory.Data,
      to: `${APP_NAME}/${backupPath.value}`,
      toDirectory: Directory.Documents,
    });

    Toast.show({ text: 'Бекап сохранён в документы' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const saveImagesToGlobal = async () => {
  try {
    loadingGlobalStart();

    try {
      await Filesystem.rmdir({
        path: `${APP_NAME}/${COMICS_FILES_DIRECTORY}`,
        directory: Directory.Documents,
        recursive: true,
      });
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (_) { /* empty */ }

    await Filesystem.copy({
      from: `${COMICS_FILES_DIRECTORY}`,
      directory: Directory.Data,
      to: `${APP_NAME}/${COMICS_FILES_DIRECTORY}`,
      toDirectory: Directory.Documents,
    });

    Toast.show({ text: 'Картинки сохранены в Документы' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const loadImagesFromGlobal = async () => {
  try {
    loadingGlobalStart();

    try {
      await Filesystem.rmdir({
        path: `${COMICS_FILES_DIRECTORY}`,
        directory: Directory.Data,
        recursive: true,
      });
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (_) { /* empty */ }

    await Filesystem.copy({
      from: `${APP_NAME}/${COMICS_FILES_DIRECTORY}`,
      directory: Directory.Documents,
      to: `${COMICS_FILES_DIRECTORY}`,
      toDirectory: Directory.Data,
    });

    Toast.show({ text: 'Картинки загружены из Документов' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
