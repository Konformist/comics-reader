<template>
  <v-main>
    <v-container class="pa-0">
      <div class="pa-4">
        <v-label
          class="w-100"
          text="Направление прокрутки"
        />
        <v-btn-toggle
          v-model="appStore.settings.readingMode"
          class="mt-2 w-100"
        >
          <v-btn
            class="flex-1-0"
            text="Горизонт."
            value="horizontal"
          />
          <v-btn
            class="flex-1-0"
            text="Вертик."
            value="vertical"
          />
          <v-btn
            class="flex-1-0"
            text="Webtoon"
            value="webtoon"
          />
        </v-btn-toggle>
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
          v-model.number="autoReadingTimeout"
          class="mt-4"
          :disabled="!appStore.settings.autoReading || appStore.settings.readingMode === 'webtoon'"
          label="До перелистывания"
          :min="0"
          suffix="с"
        />
      </div>
      <v-divider />
      <div class="pa-4">
        <v-label class="w-100" text="Сжатие изображений при загрузке">
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
      @click="onSave()"
    />
  </v-main>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/app.ts';
import { Toast } from '@capacitor/toast';

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

const autoReadingTimeout = computed({
  get: () => appStore.settings.autoReadingTimeout / 1000,
  set: (val) => appStore.settings.autoReadingTimeout = val * 1000,
});

const onSave = async () => {
  await appStore.saveSettings();
  Toast.show({ text: 'Настройки сохранены' });
};
</script>
