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

const canOpened = computed(() => (
  item.type === 'directory' && item.children.length
))
const toggleOpened = () => {
  opened.value = canOpened.value ? !opened.value : false;
}

const onClick = () => {
  toggleOpened();
  if (opened.value) emit('click')
};
</script>
