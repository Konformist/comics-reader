<template>
  <v-app-bar title="Файловый менеджер" />
  <v-main>
    <v-container>
      <FilesTree
        :tree="files"
      />
    </v-container>
  </v-main>
</template>

<script lang="ts" setup>
import FilesTree from '@/components/FilesTree.vue';
import type { IDirectory } from '@/core/entities/file/FileTypes.ts';
import server from '@/core/middleware/server.ts';
import { BACKUPS_DIRECTORY, COMICS_FILES_DIRECTORY } from '@/core/middleware/variables.ts';

const files = ref<IDirectory[]>([]);

const loadComics = async () => {
  try {
    files.value.push(...await server.getTree(COMICS_FILES_DIRECTORY))
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) { /* empty */ }

  try {
    files.value.push(...await server.getTree(BACKUPS_DIRECTORY))
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) { /* empty */ }
}

loadComics();
</script>
