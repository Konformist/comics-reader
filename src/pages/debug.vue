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
import type { ITreeDirectory } from '@/plugins/WebApiPlugin.ts';
import useLoading from '@/composables/useLoading.ts';
import Api from '@/core/api/Api.ts';
import UI from '@/plugins/UIPlugin.ts';
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useLanguagesStore } from '@/stores/languages.ts';
import { useParsersStore } from '@/stores/parsers.ts';
import { useTagsStore } from '@/stores/tags.ts';

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
    UI.toast({ text: 'Данные выгружены' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
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
    UI.toast({ text: 'Данные восстановлены' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};

const migrate = async () => {
  try {
    loadingGlobalStart();
    await Api.api('data/data/migrate');
    UI.toast({ text: 'Миграция прошла успешно' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
