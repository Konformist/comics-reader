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
        >
          <template #append>
            <v-list-item-action end>
              <v-btn
                class="mr-4"
                density="comfortable"
                icon="$edit"
                :to="{
                  name: '/parsers/[id]/',
                  params: { id: item.id }
                }"
                variant="tonal"
              />
              <v-btn
                color="error"
                density="comfortable"
                icon="$delete"
                variant="tonal"
                @click="deleteParser(item.id)"
              />
            </v-list-item-action>
          </template>
        </v-list-item>
      </v-list>
    </v-container>
    <v-fab
      icon="$plus"
      @click="createParser()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';

definePage({
  meta: {
    title: 'Парсеры',
  },
});

const router = useRouter();

const parsers = ref<ParserModel[]>([]);

const loadParsers = async (): Promise<void> => {
  parsers.value = await ParserController.loadAll();
};

loadParsers();

const createParser = async () => {
  const parserId = await ParserController.save(new ParserModel());

  if (parserId) {
    Toast.show({ text: 'Парсер создан' });
    await router.push({
      name: '/parsers/[id]/',
      params: { id: parserId },
    });
  }
};

const deleteParser = async (id: number) => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить парсер?',
  });

  if (!value) return;

  try {
    await ParserController.delete(id);
    await loadParsers();
    Toast.show({ text: 'Парсер удалён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  }
};
</script>
