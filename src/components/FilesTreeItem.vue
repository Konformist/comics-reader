<template>
  <v-list-item
    :active="item.type === 'file' && model === item.path"
    :prepend-icon="icon"
    :title="`${item.name}${item.type === 'directory' ? '/' : ''}`"
    @click="onClick()"
  >
    <template #append>
      <v-list-item-subtitle v-if="item.type === 'file'">
        {{ formatBytes(item.size) }}
      </v-list-item-subtitle>
      <v-list-item-subtitle v-else>
        Элементов: {{ item.count }}
      </v-list-item-subtitle>
    </template>
  </v-list-item>
  <template v-if="item.type === 'directory' && item.count && opened">
    <v-divider />
    <FilesTree
      v-model="model"
      is-child
      :tree="item.childes"
    />
  </template>
</template>

<script lang="ts" setup>
import FilesTree from '@/components/FilesTree.vue';
import type { ITreeDirectory, ITreeFile } from '@/plugins/WebApiPlugin.ts';
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

  if (item.extension === 'json') {
    return '$file-code';
  } else if (['webp', 'jpg', 'jpeg', 'png'].includes(item.extension)) {
    return '$file-image';
  } else {
    return '$file';
  }
});

const onClick = () => {
  if (item.type === 'directory') toggleOpened();
  else model.value = item.path;
};
</script>
