<template>
  <v-app-bar title="Настройки" />
  <v-main>
    <v-container>
      <v-btn
        class="w-100"
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
        @click="server.getBackup(backupFile)"
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
import { Directory, Encoding, Filesystem } from '@capacitor/filesystem';
import { Toast } from '@capacitor/toast';

const backups = ref<IDirectory[]>([]);

const loadBackupsTree = async () => {
  backups.value = await server.getTree(BACKUPS_DIRECTORY);
};

loadBackupsTree();

const setBackup = async () => {
  await server.setBackup()
  loadBackupsTree();
}

const backupFile = ref('');

const saveBackupToGlobal = async (path: string): Promise<void> => {
  const result = await Filesystem.readFile({
    path,
    directory: Directory.Data,
    encoding: Encoding.UTF8,
  });

  const now = new Date();
  const year = now.getFullYear();
  const month = (now.getMonth() + 1).toString().padStart(2, '0');
  const day = (now.getDate() + 1).toString().padStart(2, '0');

  await Filesystem.writeFile({
    directory: Directory.Documents,
    path: `Comics Reader/${year}-${month}-${day}.json`,
    recursive: true,
    encoding: Encoding.UTF8,
    data: result.data,
  })

  Toast.show({ text: 'Бекап сохранён в документы' })
}
</script>
