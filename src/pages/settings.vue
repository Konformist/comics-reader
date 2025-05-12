<template>
  <v-app-bar title="Настройки" />
  <v-main>
    <v-container>
      <v-btn
        class="w-100"
        text="Забекапить данные"
        @click="server.setBackup()"
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
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import type { IDirectory } from '@/core/entities/file/FileTypes.ts';
import server from '@/core/middleware/server.ts';
import { BACKUPS_DIRECTORY } from '@/core/middleware/variables.ts';

const backups = ref<IDirectory[]>([]);

const loadBackupsTree = async () => {
  try {
    backups.value = await server.getTree(BACKUPS_DIRECTORY);
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) { /* empty */ }
};

loadBackupsTree();

const backupFile = ref('');
</script>
