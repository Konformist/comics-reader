<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <v-alert color="info" rounded="0">
        Парсеры работают с DOM деревом. См. CSS.
      </v-alert>
      <DictionaryList
        :items="parsersStore.parsers"
        :loading="loading"
        @click-item="$router.push({
          name: '/parsers/[id]/',
          params: { id: $event }
        })"
      />
    </v-container>
    <v-fab
      class="mb-14"
      :disabled="loadingGlobal"
      icon="$plus"
      @click="createParser()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import { useParsersStore } from '@/stores/parsers.ts';
import { Toast } from '@capacitor/toast';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import useLoading from '@/composables/useLoading.ts';

definePage({
  meta: {
    title: 'Парсеры',
    isBottomNavigation: true,
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
    Toast.show({ text: 'Парсер создан' });
    router.push({
      name: '/parsers/[id]/',
      params: { id: parserId },
    });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
