<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <FilesTree
        v-if="files.length"
        :tree="files"
      />
    </v-container>
  </v-main>
</template>

<script lang="ts" setup>
import FilesTree from '@/components/FilesTree.vue';
import server from '@/core/middleware/server.ts';
import { BACKUPS_DIRECTORY, COMICS_FILES_DIRECTORY } from '@/core/middleware/variables.ts';
import type { ITreeDirectory } from '@/core/object-value/file/FileTypes.ts';

definePage({
  meta: {
    title: 'Файловый менеджер',
  },
});

const files = ref<ITreeDirectory[]>([]);

const loadComics = async () => {
  files.value.push(...await server.getTree(COMICS_FILES_DIRECTORY));
  files.value.push(...await server.getTree(BACKUPS_DIRECTORY));
};

loadComics();
</script>
