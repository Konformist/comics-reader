<template>
  <v-list-item
    :active="!item.isDirectory && model?.path === item.path"
    :prepend-icon="icon"
    :title="`${item.name}${item.isDirectory ? '/' : ''}`"
    @click="onClick()"
  >
    <template #append>
      <v-list-item-subtitle v-if="!item.isDirectory">
        {{ formatBytes(item.size) }}
      </v-list-item-subtitle>
      <v-list-item-subtitle v-else>
        Элем: {{ item.children.length }}
      </v-list-item-subtitle>
    </template>
  </v-list-item>
  <template v-if="item.isDirectory && item.children.length > 0 && opened">
    <v-divider class="mt-2 mb-1 mx-4" />
    <FilesTree
      v-model="model"
      is-child
      :tree="item.children"
    />
  </template>
</template>

<script lang="ts" setup>
import type { ITreeDirectory, ITreeFile } from '@/plugins/WebApiPlugin.ts';
import { formatBytes } from '@/core/utils/format.ts';

const model = defineModel<ITreeFile>();

const { item } = defineProps<{ item: ITreeDirectory | ITreeFile }>();

const opened = ref(false);

const toggleOpened = () => {
  opened.value = !opened.value;
};

const icon = computed(() => {
  if (item.isDirectory) {
    return opened.value ? '$folder-open' : '$folder';
  }

  if (item.mimeType === 'application/json') {
    return '$file-code';
  } else if (item.mimeType?.includes('image/')) {
    return '$file-image';
  } else {
    return '$file';
  }
});

const onClick = () => {
  if (item.isDirectory) toggleOpened();
  else model.value = item;
};
</script>
