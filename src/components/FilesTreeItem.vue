<template>
  <v-list-item
    :active="model === item.path"
    :prepend-icon="icon"
    slim
    :title="`${ item.name }${ item.type === 'directory' ? '/' : '' }`"
    @click="onClick()"
  >
    <template #append>
      <v-list-item-subtitle v-if="item.type === 'file'">
        {{ formatBytes(item.size) }}
      </v-list-item-subtitle>
      <v-list-item-subtitle v-else>
        Элементов: {{ item.children.length }}
      </v-list-item-subtitle>
    </template>
  </v-list-item>
  <template v-if="item.type === 'directory' && opened">
    <v-divider />
    <FilesTree
      v-model="model"
      is-child
      :tree="item.children"
    />
  </template>
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

const icon = computed(() => {
  if (item.type === 'directory') {
    return opened.value ? '$folder-open' : '$folder';
  }

  if (item.name.includes('.json')) return '$file-code';
  else return '$file-image';
});

const onClick = () => {
  if (item.type === 'directory') toggleOpened();
  else model.value = item.path;
};
</script>
