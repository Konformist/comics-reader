<template>
  <v-app-bar title="Файловый менеджер" />
  <v-main>
    <v-container>
      <FilesTree
        :tree="tree"
      />
    </v-container>
  </v-main>
</template>

<script lang="ts" setup>
import FilesTree from '@/components/FilesTree.vue';
import type { IDirectory, IFile } from '@/core/utils/filemanager.ts';
import { useAppStore } from '@/stores/app.ts';

const appStore = useAppStore();

const tree = ref<Array<IDirectory|IFile>>(appStore.comics.map(e => ({
  type: 'directory',
  path: e.id.toString(),
  children: [],
})));
</script>
