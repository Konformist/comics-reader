<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <FilesTree
        :loading="loading || true"
        :tree="treeFiles"
      />
    </v-container>
  </v-main>
</template>

<script lang="ts" setup>
import FilesTree from '@/components/FilesTree.vue';
import useLoading from '@/composables/useLoading.ts';
import server from '@/core/middleware/server.ts';
import { BACKUPS_DIRECTORY, COMICS_FILES_DIRECTORY } from '@/core/middleware/variables.ts';
import type { ITreeDirectory } from '@/core/object-value/file/FileTypes.ts';

definePage({
  meta: {
    title: 'Файловый менеджер',
  },
});

const {
  loading,
  loadingStart,
  loadingEnd,
} = useLoading();

const treeFiles = ref<ITreeDirectory[]>([]);

const loadTreeFiles = async () => {
  loadingStart();
  treeFiles.value.push(...await server.getTree(COMICS_FILES_DIRECTORY));
  treeFiles.value.push(...await server.getTree(BACKUPS_DIRECTORY));
  loadingEnd();
};

loadTreeFiles();
</script>
