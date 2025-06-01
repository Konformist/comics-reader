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
      </div>
    </v-container>
    <v-fab
      app
      appear
      class="mb-16"
      icon="$save"
      @click="appStore.saveSettings()"
    />
  </v-main>
</template>

<script setup lang="ts">
import { settingsDirectionItems } from '@/core/entities/settings/settingsUtils.ts';
import { useAppStore } from '@/stores/app.ts';

const appStore = useAppStore();

definePage({
  meta: {
    layout: 'full',
    title: 'Настройки',
  },
});

onBeforeUnmount(() => {
  appStore.loadSettings();
});
</script>
