<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <div class="pa-4">
        <p class="d-flex justify-space-between">
          <span class="font-weight-medium">Версия FrontEnd</span>
          <span>v{{ appStore.frontVersion }}</span>
        </p>
      </div>
      <v-divider />
      <div class="pa-4 pb-8">
        <v-label class="w-100" text="Авто перелистывание">
          <v-switch
            v-model="appStore.settings.autoReading"
            class="ml-auto"
            color="primary"
            :false-value="false"
            hide-details
            inset
            :true-value="true"
          />
        </v-label>
        <v-number-input
          v-model.number="appStore.settings.autoReadingTimeout"
          class="mt-2"
          control-variant="split"
          :disabled="!appStore.settings.autoReading"
          label="До перелистывания"
          :min="0"
          suffix="с"
        />
        <v-btn
          class="w-100"
          text="Сохранить"
          @click="appStore.saveSettings()"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-btn
          class="w-100"
          text="Создать бекап"
          @click="addBackup()"
        />
        <FilesTree
          v-if="backups.length"
          v-model="backupPath"
          class="mt-4"
          rounded
          :tree="backups"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="!backupPath"
          text="Применить бекап"
          @click="getBackup()"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="!backupPath"
          text="Сохранить в Документы"
          @click="saveBackupToGlobal()"
        />
        <v-btn
          class="mt-4 w-100"
          color="error"
          :disabled="!backupPath"
          text="Удалить бекап"
          @click="delBackup()"
        />
        <v-file-input
          v-model="backupFile"
          class="mt-4"
          label="Файл бекапа"
        />
        <v-btn
          class="w-100"
          :disabled="!backupFile"
          text="Сохранить бекап"
          @click="loadBackup()"
        />
      </div>
      <v-divider />
      <div class="px-4 py-8">
        <v-btn
          class="w-100"
          :loading="loading"
          text="Сохранить файлы в Документы"
          @click="saveImagesToGlobal()"
        />
        <v-btn
          class="mt-4 w-100"
          :loading="loading"
          text="Загрузить файлы из Документов"
          @click="loadImagesFromGlobal()"
        />
      </div>
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import server from '@/core/middleware/server.ts';
import { APP_NAME, BACKUPS_DIRECTORY, COMICS_FILES_DIRECTORY } from '@/core/middleware/variables.ts';
import type { ITreeDirectory } from '@/core/object-value/file/FileTypes.ts';
import { useAppStore } from '@/stores/app.ts';
import { Dialog } from '@capacitor/dialog';
import { Directory, Filesystem } from '@capacitor/filesystem';
import { Toast } from '@capacitor/toast';

const appStore = useAppStore();

definePage({
  meta: {
    title: 'Настройки',
  },
});

const backups = ref<ITreeDirectory[]>([]);

const loadBackupsTree = async () => {
  backups.value = await server.getTree(BACKUPS_DIRECTORY);
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
    const result = await backupFile.value.text();
    await server.setBackup(result, backupFile.value.name);
    await loadBackupsTree();
    Toast.show({ text: 'Данные получены' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
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
    await Filesystem.copy({
      from: backupPath.value,
      directory: Directory.Data,
      to: `${APP_NAME}/${backupPath.value}`,
      toDirectory: Directory.Documents,
    });

    Toast.show({ text: 'Бекап сохранён в документы' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  }
};

const loading = ref(false);

const saveImagesToGlobal = async () => {
  try {
    await Filesystem.rmdir({
      path: `${APP_NAME}/${COMICS_FILES_DIRECTORY}`,
      directory: Directory.Documents,
      recursive: true,
    });
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) { /* empty */ }

  try {
    loading.value = true;
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
    loading.value = false;
  }
};

const loadImagesFromGlobal = async () => {
  try {
    loading.value = true;

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
    loading.value = false;
  }
};
</script>
