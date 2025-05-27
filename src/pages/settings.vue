<template>
  <v-main>
    <v-container class="pa-0">
      <div class="pa-4">
        <v-label
          class="w-100"
          text="Направление прокрутки"
        />
        <div>
          <v-btn
            :active="appStore.settings.readingMode === 'vertical'"
            class="w-100 rounded-t-xl"
            :color="appStore.settings.readingMode === 'vertical' ? 'primary' : ''"
            flat
            :rounded="false"
            text="Вертикaльно"
            @click="appStore.settings.readingMode = 'vertical'"
          />
          <v-divider />
          <v-btn
            :active="appStore.settings.readingMode === 'horizontal'"
            class="w-100 rounded-0"
            :color="appStore.settings.readingMode === 'horizontal' ? 'primary' : ''"
            flat
            :rounded="false"
            text="Горизонтально"
            @click="appStore.settings.readingMode = 'horizontal'"
          />
          <v-divider />
          <v-btn
            :active="appStore.settings.readingMode === 'webtoon'"
            class="w-100 rounded-b-xl"
            :color="appStore.settings.readingMode === 'webtoon' ? 'primary' : ''"
            flat
            :rounded="false"
            text="Webtoon"
            @click="appStore.settings.readingMode = 'webtoon'"
          />
        </div>
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
