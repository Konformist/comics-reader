<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <FilesTree
        :loading="loading"
        :tree="treeFiles"
      />
    </v-container>
  </v-main>
</template>

<script lang="ts" setup>
import FilesTree from '@/components/FilesTree.vue';
import useLoading from '@/composables/useLoading.ts';
import { BACKUPS_DIRECTORY, COMICS_FILES_DIRECTORY } from '@/core/utils/variables.ts';
import type { ITreeDirectory } from '@/core/entities/file/FileTypes.ts';
import { getTree } from '@/core/utils/files.ts';

definePage({
  meta: {
    title: 'Файловый менеджер',
    isBottomNavigation: true,
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
  treeFiles.value.push(...await getTree(COMICS_FILES_DIRECTORY));
  treeFiles.value.push(...await getTree(BACKUPS_DIRECTORY));
  loadingEnd();
};

loadTreeFiles();
</script>
