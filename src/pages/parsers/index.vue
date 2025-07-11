<template>
  <v-main>
    <v-container>
      <v-alert
        color="info"
        variant="tonal"
      >
        Парсеры работают с DOM деревом. См. CSS.
      </v-alert>
      <DictionaryList
        v-if="parsersStore.parsers.length > 0"
        class="mt-4"
        :items="parsersStore.parsers"
        :loading="loading"
        @click-item="$router.push({
          name: '/parsers/[id]/',
          params: { id: $event },
        })"
      />
    </v-container>
  </v-main>
  <v-fab
    app
    appear
    class="mb-16"
    :disabled="loadingGlobal"
    icon="$plus"
    @click="createParser()"
  />
</template>

<script lang="ts" setup>
import useLoading from '@/composables/useLoading.ts';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import UI from '@/plugins/UIPlugin.ts';
import { useParsersStore } from '@/stores/parsers.ts';

definePage({
  meta: {
    layout: 'full',
    title: 'Парсеры',
  },
});

const router = useRouter();
const parsersStore = useParsersStore();

const {
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
} = useLoading();

onMounted(async () => {
  loadingStart();
  await parsersStore.loadParsers();
  loadingEnd();
});

const createParser = async () => {
  try {
    loadingGlobalStart();
    const parserId = await ParserController.save(new ParserModel());

    if (typeof parserId !== 'number') return;

    await parsersStore.loadParsersForce();
    UI.toast({ text: 'Парсер создан' });
    router.push({
      name: '/parsers/[id]/',
      params: { id: parserId },
    });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
