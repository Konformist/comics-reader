<template>
  <v-app-bar title="Файловый менеджер" />
  <v-main>
    <v-container class="pa-0">
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
  files.value.push(...await server.getTree(COMICS_FILES_DIRECTORY))
  files.value.push(...await server.getTree(BACKUPS_DIRECTORY))
}

loadComics();
</script>
