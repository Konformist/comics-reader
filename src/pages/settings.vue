<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <div class="px-4 pt-6 pb-8">
        <v-label class="w-100" text="Направление прокрутки" />
        <v-btn-toggle
          v-model="appStore.settings.direction"
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
        <v-label class="mt-4 w-100" text="Авто перелистывание">
          <v-switch
            v-model="appStore.settings.autoReading"
            class="ml-auto"
            color="primary"
            :false-value="false"
            hide-details
            inset
            :true-value="true"
          />
        </v-label>
        <v-number-input
          v-model.number="appStore.settings.autoReadingTimeout"
          control-variant="split"
          :disabled="!appStore.settings.autoReading"
          hide-details
          label="До перелистывания"
          :min="0"
          suffix="с"
        />
      </div>
      <v-divider />
      <div class="px-4 pt-4 pb-8">
        <v-label class="w-100" text="Сжатие изображений при загрузке">
          <v-switch
            v-model="appStore.settings.isCompress"
            class="ml-auto"
            color="primary"
            :false-value="false"
            hide-details
            inset
            :true-value="true"
          />
        </v-label>
      </div>
    </v-container>
    <v-fab
      class="mb-14"
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
    title: 'Настройки',
    isBottomNavigation: true,
  },
});

onBeforeUnmount(() => {
  appStore.loadSettings();
});

const onSave = async () => {
  await appStore.saveSettings();
  Toast.show({ text: 'Настройки сохранены' });
};
</script>
