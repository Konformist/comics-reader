<template>
  <v-list
    :class="isChild ? 'pl-4' : ''"
    :rounded="rounded"
  >
    <v-skeleton-loader
      v-if="loading"
      type="list-item"
    />
    <template v-else>
      <template
        v-for="(item, index) in tree"
        :key="item.path"
      >
        <v-divider v-if="index || isChild" />
        <FilesTreeItem
          v-model="model"
          :item="item"
        />
      </template>
    </template>
  </v-list>
</template>

<script lang="ts" setup>
import FilesTreeItem from '@/components/FilesTreeItem.vue';
import type { ITreeDirectory, ITreeFile } from '@/core/object-value/file/FileTypes.ts';

const model = defineModel<string>({ default: '' });

defineProps<{
  tree: Array<ITreeDirectory | ITreeFile>
  loading?: boolean
  isChild?: boolean
  rounded?: boolean
}>();
</script>
