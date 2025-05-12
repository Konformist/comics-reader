<template>
  <v-app-bar title="Парсеры" />
  <v-main>
    <v-container>
      <v-list v-if="parsers.length">
        <v-list-item
          v-for="item in parsers"
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
import ParserController from '@/core/entities/parser/ParserController.ts';
import type ParserModel from '@/core/entities/parser/ParserModel.ts';

const parsers = ref<ParserModel[]>([]);

const loadParsers = async (): Promise<void> => {
  parsers.value = await ParserController.loadAll();
}

loadParsers();
</script>
