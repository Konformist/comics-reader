<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <v-alert color="info" rounded="0">
        Парсеры работают с DOM деревом. См. CSS.
      </v-alert>
      <v-list v-if="parsers.length">
        <v-list-item
          v-for="item in parsers"
          :key="item.id"
          :title="item.name"
          :to="{
            name: '/parsers/[id]/',
            params: { id: item.id }
          }"
        >
          <template #append>
            <v-list-item-action end>
              <v-btn
                color="error"
                density="comfortable"
                :disabled="loadingGlobal"
                icon="$delete"
                variant="tonal"
                @click.prevent="deleteParser(item.id)"
              />
            </v-list-item-action>
          </template>
        </v-list-item>
      </v-list>
    </v-container>
    <v-fab
      :disabled="loadingGlobal"
      icon="$plus"
      @click="createParser()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import useLoading from '@/composables/useLoading.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import ParserController from '@/core/entities-v2/parser/ParserController.ts';
import ParserModel from '@/core/entities-v2/parser/ParserModel.ts';

definePage({
  meta: {
    title: 'Парсеры',
  },
});

const router = useRouter();
const {
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
} = useLoading();

const parsers = ref<ParserModel[]>([]);

const loadParsers = async (): Promise<void> => {
  parsers.value = await ParserController.loadAll();
};

loadParsers();

const createParser = async () => {
  try {
    loadingGlobalStart();
    const parserId = await ParserController.save(new ParserModel());

    if (parserId) {
      Toast.show({ text: 'Парсер создан' });
      router.push({
        name: '/parsers/[id]/',
        params: { id: parserId },
      });
    }
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const deleteParser = async (id: number) => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить парсер?',
  });

  if (!value) return;

  try {
    loadingGlobalStart();
    await ParserController.remove(id);
    await loadParsers();
    Toast.show({ text: 'Парсер удалён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
