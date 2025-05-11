<template>
  <v-list
    :class="isChild ? 'pl-4' : ''"
    style="list-style-type: none"
  >
    <template
      v-for="(item, index) in tree"
      :key="item.path"
    >
      <v-divider v-if="index || isChild" />
      <FilesTreeItem
        :item="item"
        @click="clickDirectory(item)"
      />
    </template>
  </v-list>
</template>

<script lang="ts" setup>
import FilesTreeItem from '@/components/FilesTreeItem.vue';
import type { IDirectory, IFile } from '@/core/utils/filemanager.ts';
import { Directory, Filesystem } from '@capacitor/filesystem';

defineProps<{
  tree: Array<IDirectory|IFile>
  isChild?: boolean
}>()

const clickDirectory = async (value: IDirectory|IFile) => {
  if (value.type === 'file') return;

  const result = await Filesystem.readdir({
    path: value.path,
    directory: Directory.Data,
  })

  value.children = result.files.map(file => {
    if (file.type === 'directory') {
      return {
        type: file.type,
        path: file.name,
        children: [],
      }
    } else {
      return {
        type: file.type,
        path: file.name,
      }
    }
  })
};
</script>
