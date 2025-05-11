<template>
  <v-app-bar title="Парсеры" />
  <v-main>
    <v-container>
      <v-list v-if="appStore.parsers.length">
        <v-list-item
          v-for="item in appStore.parsers"
          :key="item.id"
          :title="item.name"
          :to="{
            name: '/parsers/[id]',
            params: { id: item.id }
          }"
        />
      </v-list>
    </v-container>
  </v-main>
  <v-fab
    app
    class="mb-4"
    icon="$plus"
    :to="{ name: '/parsers/create' }"
  />
</template>

<script lang="ts" setup>
import { useAppStore } from '@/stores/app.ts';

const appStore = useAppStore();
</script>
