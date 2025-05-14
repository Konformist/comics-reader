<template>
  <v-main scrollable>
    <v-container class="pa-0">
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
    <v-fab
      icon="$plus"
      @click="createParser()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
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
}

loadParsers();

const createParser = async () => {
  const parserId = await ParserController.save(new ParserModel());

  if (parserId) {
    Toast.show({ text: 'Парсер создан' })
    await router.push({
      name: '/parsers/[id]',
      params: { id: parserId },
    })
  }
}
</script>
