<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <FilesTree
        v-if="treeFiles.length"
        :tree="treeFiles"
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

const treeFiles = ref<ITreeDirectory[]>([]);

const loadTreeFiles = async () => {
  treeFiles.value.push(...await server.getTree(COMICS_FILES_DIRECTORY));
  treeFiles.value.push(...await server.getTree(BACKUPS_DIRECTORY));
};

loadTreeFiles();
</script>
