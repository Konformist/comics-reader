<template>
  <v-main>
    <v-container class="pa-0">
      <div class="pa-4">
        <CustomBtnGroup
          v-model="appStore.settings.readingMode"
          :items="settingsDirectionItems"
          label="Направление прокрутки"
        />
        <v-label
          class="mt-4 w-100"
          text="Авто перелистывание"
        >
          <v-switch
            v-model="appStore.settings.autoReading"
            class="ml-auto"
            :disabled="appStore.settings.readingMode === 'webtoon'"
            :false-value="false"
            :true-value="true"
          />
        </v-label>
        <v-number-input
          v-model.number="appStore.settings.readingTimeoutSec"
          class="mt-4"
          :disabled="!appStore.settings.autoReading || appStore.settings.readingMode === 'webtoon'"
          label="До перелистывания"
          :min="0"
          suffix="с"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-label
          class="w-100"
          text="Сжатие изображений при загрузке"
        >
          <v-switch
            v-model="appStore.settings.isCompress"
            class="ml-auto"
            :false-value="false"
            :true-value="true"
          />
        </v-label>
        <v-btn
          class="mt-4 w-100"
          :disabled="loadingGlobal"
          text="Загрузить комикс"
          @click="getComicArchive()"
        />
      </div>
    </v-container>
    <v-fab
      app
      appear
      class="mb-16"
      :disabled="loadingGlobal"
      icon="$save"
      @click="appStore.saveSettings()"
    />
  </v-main>
</template>

<script setup lang="ts">
import useLoading from '@/composables/useLoading.ts';
import Api from '@/core/api/Api.ts';
import { settingsDirectionItems } from '@/core/entities/settings/settingsUtils.ts';
import UI from '@/plugins/UIPlugin.ts';
import { useAppStore } from '@/stores/app.ts';
import { useComicsStore } from '@/stores/comics.ts';

const appStore = useAppStore();
const comicsStore = useComicsStore();

definePage({
  meta: {
    layout: 'full',
    title: 'Настройки',
  },
});

onBeforeUnmount(() => {
  appStore.loadSettings();
});

const {
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
} = useLoading();

const getComicArchive = async () => {
  try {
    loadingGlobalStart();
    await Api.api('comic/archive/add');
    await comicsStore.loadComicsForce();
    UI.toast({ text: 'Комикс успешно загружен' });
  } catch (e) {
    UI.toast({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
