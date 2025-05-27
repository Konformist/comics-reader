<template>
  <v-list
    :class="isChild ? 'pl-4 pr-0' : ''"
    slim
  >
    <v-skeleton-loader
      v-if="loading"
      type="list-item"
    />
    <template v-else>
      <template
        v-for="(item, index) in tree"
        :key="item.name"
      >
        <FilesTreeItem
          v-model="model"
          :item="item"
        />
        <v-divider
          v-if="index < tree.length - 1"
          class="my-2 mx-4"
        />
      </template>
    </template>
  </v-list>
</template>

<script lang="ts" setup>
import FilesTreeItem from '@/components/FilesTreeItem.vue';
import type { ITreeDirectory, ITreeFile } from '@/plugins/WebApiPlugin.ts';

const model = defineModel<ITreeFile>();

defineProps<{
  tree: Array<ITreeDirectory | ITreeFile>
  loading?: boolean
  isChild?: boolean
}>();
</script>
