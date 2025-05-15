<template>
  <v-list-item
    :active="model === `${parent}/${item.path}`"
    @click="onClick()"
  >
    <v-list-item-title>
      {{ item.path }}{{ item.type === 'directory' ? '/' : '' }}
    </v-list-item-title>
    <template
      v-if="item.type === 'file'"
      #append
    >
      <v-list-item-subtitle>
        {{ formatBytes(item.size) }}
      </v-list-item-subtitle>
    </template>
  </v-list-item>
  <FilesTree
    v-if="item.type === 'directory' && opened"
    v-model="model"
    is-child
    :parent="parent ? `${parent}/${item.path}` : item.path"
    :tree="item.children"
  />
</template>

<script lang="ts" setup>
import FilesTree from '@/components/FilesTree.vue';
import type { IDirectory, IFile } from '@/core/entities/file/FileTypes.ts';
import { formatBytes } from '@/core/utils/format.ts';

const model = defineModel<string>({ default: '' });

const { item, parent } = defineProps<{
  parent: string
  item: IDirectory|IFile
}>();

const opened = ref(false);

const toggleOpened = () => {
  opened.value = !opened.value;
};

const onClick = () => {
  if (item.type === 'directory') toggleOpened();
  else model.value = `${parent}/${item.path}`;
};
</script>
