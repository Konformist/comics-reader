<template>
  <v-main>
    <v-container class="pa-0">
      <div class="px-4 py-8">
        <v-alert color="error">
          Осторожно! Опасный функционал! При неправильном использовании можно потерять данные
        </v-alert>
        <v-btn
          class="mt-4 w-100"
          text="Бекапы"
          :to="{ name: '/backups' }"
        />
      </div>
      <FilesTree
        :loading="loading"
        :tree="treeFiles"
      />
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import FilesTree from '@/components/FilesTree.vue';
import useLoading from '@/composables/useLoading.ts';
import Api from '@/core/api/Api.ts';
import type { ITreeDirectory } from '@/plugins/WebApiPlugin.ts';

definePage({
  meta: {
    title: 'Дебаггинг',
    isBottomNavigation: true,
  },
});

const {
  loading,
  loadingStart,
  loadingEnd,
} = useLoading();

const treeFiles = ref<ITreeDirectory[]>([]);

const loadTreeFiles = async () => {
  loadingStart();
  treeFiles.value.push(await Api.api('file/comic-images/tree'));
  treeFiles.value.push(await Api.api('file/backups/tree'));
  loadingEnd();
};

loadTreeFiles();
</script>
