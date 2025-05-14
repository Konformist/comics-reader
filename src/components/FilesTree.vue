<template>
  <v-list
    :class="isChild ? 'pl-4' : ''"
    :rounded="rounded"
  >
    <template
      v-for="(item, index) in tree"
      :key="item.path"
    >
      <v-divider v-if="index || isChild" />
      <FilesTreeItem
        v-model="model"
        :item="item"
        :parent="parent || ''"
      />
    </template>
  </v-list>
</template>

<script lang="ts" setup>
import FilesTreeItem from '@/components/FilesTreeItem.vue';
import type { IDirectory, IFile } from '@/core/entities/file/FileTypes.ts';

const model = defineModel<string>({ default: '' });

defineProps<{
  tree: Array<IDirectory|IFile>
  parent?: string
  isChild?: boolean
  rounded?: boolean
}>()
</script>
