<template>
  <v-main>
    <v-container class="pa-0">
      <div class="pa-4">
        <v-alert color="error" variant="tonal">
          Осторожно! Опасный функционал!<br>
          При неправильном использовании можно потерять данные
        </v-alert>
        <v-btn
          class="mt-4 w-100"
          color="error"
          text="Бекапы"
          :to="{ name: '/backups' }"
          variant="tonal"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <FilesTree
          :loading="loading"
          :tree="treeFiles"
        />
      </div>
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import useLoading from '@/composables/useLoading.ts';
import Api from '@/core/api/Api.ts';
import type { ITreeDirectory } from '@/plugins/WebApiPlugin.ts';

definePage({
  meta: {
    layout: 'full',
    title: 'Дебаггинг',
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
  treeFiles.value.push(await Api.api('file/comics-images/tree'));
  treeFiles.value.push(await Api.api('file/backups/tree'));
  loadingEnd();
};

loadTreeFiles();
</script>
