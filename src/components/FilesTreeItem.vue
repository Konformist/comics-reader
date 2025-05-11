<template>
  <v-list-item
    @click="onClick()"
  >
    <v-list-item-title>
      {{ item.type === 'directory' ? '/' : '' }}{{ item.path }}
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
    is-child
    :tree="item.children"
  />
</template>

<script lang="ts" setup>
import FilesTree from '@/components/FilesTree.vue';
import type { IDirectory, IFile } from '@/core/utils/filemanager.ts';
import { formatBytes } from '@/core/utils/format.ts';

const emit = defineEmits<{
  (e: 'click'): void
}>()

const { item } = defineProps<{
  item: IDirectory|IFile
}>()

const opened = ref(false);

const toggleOpened = () => {
  opened.value = !opened.value;
}

const onClick = () => {
  toggleOpened();
  emit('click')
};
</script>
