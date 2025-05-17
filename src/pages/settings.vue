<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <div class="pa-4 pb-8">
        <v-label class="w-100" text="Авто перелистывание">
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
          class="mt-2"
          control-variant="split"
          :disabled="!appStore.settings.autoReading"
          label="До перелистывания"
          :min="0"
          suffix="с"
        />
        <v-btn
          class="w-100"
          text="Сохранить"
          @click="appStore.saveSettings()"
        />
      </div>
      <v-divider />
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
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/app.ts';

const appStore = useAppStore();

definePage({
  meta: {
    title: 'Настройки',
  },
});
</script>
