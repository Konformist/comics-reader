<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <v-toolbar
        class="px-2 pt-2"
        density="comfortable"
      >
        <v-select
          v-model="sortValue"
          :chips="false"
          class="mt-3"
          :disabled="!authorsStore.authors.length"
          item-title="name"
          item-value="id"
          :items="sortItems"
          label="Сортировать"
        />
      </v-toolbar>
      <DictionaryList
        :items="sortedAuthors"
        :loading="loading"
        @click-item="clickAuthor($event)"
      />
    </v-container>
    <DictionaryEditDialog
      v-model="selectedAuthor.name"
      v-model:opened="dialog"
      :disabled="loadingGlobal"
      :is-created="!selectedAuthor.id"
      @remove="deleteAuthor()"
      @save="saveAuthor()"
    />
    <v-fab
      class="mb-14"
      :disabled="loading"
      icon="$plus"
      @click="clickAuthor(0)"
    />
  </v-main>
</template>

<script setup lang="ts">
import DictionaryEditDialog from '@/components/DictionaryEditDialog.vue';
import DictionaryList from '@/components/DictionaryList.vue';
import useLoading from '@/composables/useLoading.ts';
import { sortString } from '@/core/utils/array.ts';
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import AuthorController from '@/core/entities/author/AuthorController.ts';
import AuthorModel from '@/core/entities/author/AuthorModel.ts';

definePage({
  meta: {
    title: 'Авторы',
    isBottomNavigation: true,
  },
});

const comicsStore = useComicsStore();
const authorsStore = useAuthorsStore();

const {
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
} = useLoading();

const sortValue = ref(0);
const sortItems = [
  { id: 0, name: 'По имени' },
  { id: 1, name: 'По имени (обратная)' },
  { id: 2, name: 'По использованию' },
  { id: 3, name: 'По использованию (обратная)' },
];

const dialog = ref(false);

onMounted(async () => {
  loadingStart();
  await Promise.all([
    comicsStore.loadComics(),
    authorsStore.loadAuthors(),
  ]);
  loadingEnd();
});

const selectedAuthor = ref(new AuthorModel());

const clickAuthor = (value: number) => {
  selectedAuthor.value = authorsStore.authors.find((e) => e.id === value) || new AuthorModel();
  dialog.value = true;
};

const authorsCount = computed(() => (
  authorsStore.authors.reduce((acc, author) => {
    acc[author.id] = 0;

    comicsStore.comics.forEach((item) => {
      if (item.authors.includes(author.id)) acc[author.id]++;
    });

    return acc;
  }, {} as Record<number, number>)
));

const fullAuthors = computed(() => (
  authorsStore.authors.map((item) => ({
    ...item.getDTO(),
    count: authorsCount.value[item.id] || 0,
  }))
));

const sortedAuthors = computed(() => (
  [...fullAuthors.value].sort((a, b) => {
    if (sortValue.value === 1) return sortString(b.name, a.name);
    else if (sortValue.value === 2) return a.count - b.count;
    else if (sortValue.value === 3) return b.count - a.count;
    else return sortString(a.name, b.name);
  })
));

const saveAuthor = async () => {
  try {
    loadingGlobalStart();
    await AuthorController.save(selectedAuthor.value);
    await authorsStore.loadAuthorsForce();
    dialog.value = false;
    Toast.show({ text: 'Автор сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const deleteAuthor = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить автора?',
  });

  if (!value) return;

  try {
    loadingGlobalStart();
    await AuthorController.remove(selectedAuthor.value.id);
    await authorsStore.loadAuthorsForce();
    Toast.show({ text: 'Автор удалён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
