<template>
  <v-list-item
    :active="model === item.path"
    @click="onClick()"
  >
    <v-list-item-title>
      {{ item.name }}{{ item.type === 'directory' ? '/' : '' }}
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
    :tree="item.children"
  />
</template>

<script lang="ts" setup>
import FilesTree from '@/components/FilesTree.vue';
import type { ITreeDirectory, ITreeFile } from '@/core/object-value/file/FileTypes.ts';
import { formatBytes } from '@/core/utils/format.ts';

const model = defineModel<string>({ default: '' });

const { item } = defineProps<{
  item: ITreeDirectory | ITreeFile
}>();

const opened = ref(false);

const toggleOpened = () => {
  opened.value = !opened.value;
};

const onClick = () => {
  if (item.type === 'directory') toggleOpened();
  else model.value = item.path;
};
</script>
