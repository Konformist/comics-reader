<template>
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
</template>

<script setup lang="ts">
definePage({
  meta: {
    title: 'Дебаггинг',
    isBottomNavigation: true,
  },
});
</script>
