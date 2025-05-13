<template>
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
import { Directory, Filesystem } from '@capacitor/filesystem';
import { Toast } from '@capacitor/toast';

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
  loadBackupsTree();
}

const backupFile = ref('');

const saveBackupToGlobal = async (path: string): Promise<void> => {
  await Filesystem.copy({
    from: (await Filesystem.getUri({ path, directory: Directory.Data })).uri,
    to: (await Filesystem.getUri({ path: `Comics Reader/${path}`, directory: Directory.Documents })).uri,
  })

  Toast.show({ text: 'Бекап сохранён в документы' })
}
</script>
