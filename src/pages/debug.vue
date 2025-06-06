<template>
  <v-main>
    <v-container class="pa-0">
      <div class="pa-4">
        <FilesTree
          :loading="loading"
          :tree="treeFiles"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-alert
          color="info"
          variant="tonal"
        >
          При выгрузке или восстановлении данных лучше перезапустить приложение
        </v-alert>
        <v-btn
          class="mt-4 w-100"
          :disabled="loadingGlobal"
          text="Выгрузить данные"
          variant="tonal"
          @click="addBackup()"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="loadingGlobal"
          text="Восстановить данные"
          variant="tonal"
          @click="restoreBackup()"
        />
        <v-btn
          class="mt-4 w-100"
          :disabled="loadingGlobal"
          text="Миграция"
          variant="tonal"
          @click="migrate()"
        />
      </div>
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import useLoading from '@/composables/useLoading.ts';
import Api from '@/core/api/Api.ts';
import type { ITreeDirectory } from '@/plugins/WebApiPlugin.ts';
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useLanguagesStore } from '@/stores/languages.ts';
import { useParsersStore } from '@/stores/parsers.ts';
import { useTagsStore } from '@/stores/tags.ts';
import { Toast } from '@capacitor/toast';

definePage({
  meta: {
    layout: 'full',
    title: 'Debug',
  },
});

const comicsStore = useComicsStore();
const tagsStore = useTagsStore();
const authorsStore = useAuthorsStore();
const languagesStore = useLanguagesStore();
const parsersStore = useParsersStore();

const {
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
} = useLoading();

const treeFiles = ref<ITreeDirectory[]>([]);

const loadTreeFiles = async () => {
  loadingStart();
  treeFiles.value = await Api.api('file/files/tree');
  loadingEnd();
};

loadTreeFiles();

const addBackup = async () => {
  try {
    loadingGlobalStart();
    await Api.api('backup/backup/add');
    Toast.show({ text: 'Данные выгружены' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const restoreBackup = async () => {
  try {
    loadingGlobalStart();
    await Api.api('backup/backup/restore');
    await Promise.all([
      comicsStore.loadComicsForce(),
      tagsStore.loadTagsForce(),
      authorsStore.loadAuthorsForce(),
      languagesStore.loadLanguagesForce(),
      parsersStore.loadParsersForce(),
    ]);
    loadTreeFiles();
    Toast.show({ text: 'Данные восстановлены' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const migrate = async () => {
  try {
    loadingGlobalStart();
    await Api.api('data/data/migrate');
    Toast.show({ text: 'Миграция прошла успешно' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
