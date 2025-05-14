<template>
  <v-main>
    <v-container>
      <p class="d-flex justify-space-between">
        <span class="font-weight-medium">Версия frontend</span>
        <span>v{{ appStore.frontVersion }}</span>
      </p>
      <v-btn
        class="mt-4 w-100"
        text="Забекапить данные"
        @click="setBackup()"
      />
      <FilesTree
        v-model="backupFile"
        class="mt-4"
        :tree="backups"
      />
      <v-btn
        class="mt-4 w-100"
        :disabled="!backupFile"
        text="Вытащить из бекапа"
        @click="getBackup()"
      />
      <v-btn
        class="mt-4 w-100"
        :disabled="!backupFile"
        text="Вытащить бекап наружу"
        @click="saveBackupToGlobal(backupFile)"
      />
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import type { IDirectory } from '@/core/entities/file/FileTypes.ts';
import server from '@/core/middleware/server.ts';
import { BACKUPS_DIRECTORY } from '@/core/middleware/variables.ts';
import { useAppStore } from '@/stores/app.ts';
import { Directory, Filesystem } from '@capacitor/filesystem';
import { Toast } from '@capacitor/toast';

const appStore = useAppStore();

definePage({
  meta: {
    title: 'Настройки',
  },
})

const backups = ref<IDirectory[]>([]);

const loadBackupsTree = async () => {
  backups.value = await server.getTree(BACKUPS_DIRECTORY);
};

loadBackupsTree();

const setBackup = async () => {
  await server.setBackup()
  Toast.show({ text: 'Бекап сохранён' })
  loadBackupsTree();
}

const backupFile = ref('');

const getBackup = async () => {
  await server.getBackup(backupFile.value)
  Toast.show({ text: 'Бекап применён' })
}

const saveBackupToGlobal = async (path: string): Promise<void> => {
  await Filesystem.mkdir({
    path: 'Comics Reader/backups',
    directory: Directory.Documents,
    recursive: true,
  })
  await Filesystem.copy({
    from: path,
    directory: Directory.Data,
    to: `Comics Reader/${path}`,
    toDirectory: Directory.Documents,
  })

  Toast.show({ text: 'Бекап сохранён в документы' })
}
</script>
