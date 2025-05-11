<template>
  <v-list-item
    @click="$emit('click')"
  >
    /{{ item.path }}
  </v-list-item>
  <FilesTree
    v-if="item.type === 'directory' && item.children.length"
    is-child
    :tree="item.children"
  />
</template>

<script lang="ts" setup>
import FilesTree from '@/components/FilesTree.vue';
import type { IDirectory, IFile } from '@/core/utils/filemanager.ts';

defineEmits<{
  click: () => void;
}>()

defineProps<{
  item: IDirectory|IFile
}>()
</script>
