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
      <div class="pa-4">
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
      <div class="pa-4">
        <v-btn
          class="w-100"
          text="Создать бекап"
          @click="addBackup()"
        />
        <FilesTree
          v-if="backups.length"
          v-model="backupFile"
          class="mt-4"
          rounded
          :tree="backups"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="!backupFile"
          text="Применить бекап"
          @click="getBackup()"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="!backupFile"
          text="Сохранить в Документы"
          @click="saveBackupToGlobal()"
        />
        <v-btn
          class="mt-4 w-100"
          color="error"
          :disabled="!backupFile"
          text="Удалить"
          @click="delBackup()"
        />
      </div>
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import server from '@/core/middleware/server.ts';
import { APP_NAME, BACKUPS_DIRECTORY } from '@/core/middleware/variables.ts';
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

const backupFile = ref('');

const getBackup = async () => {
  try {
    await server.getBackup(backupFile.value);
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
    await server.delBackup(backupFile.value);
    await loadBackupsTree();
    Toast.show({ text: 'Бекап удалён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  }
};

const saveBackupToGlobal = async (): Promise<void> => {
  try {
    await Filesystem.mkdir({
      path: `${APP_NAME}/backups`,
      directory: Directory.Documents,
      recursive: true,
    });
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) { /* empty */ }

  try {
    await Filesystem.copy({
      from: backupFile.value,
      directory: Directory.Data,
      to: `${APP_NAME}/${backupFile.value}`,
      toDirectory: Directory.Documents,
    });

    Toast.show({ text: 'Бекап сохранён в документы' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  }
};
</script>
