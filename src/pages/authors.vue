<template>
  <v-main>
    <v-toolbar
      class="px-4"
      color="background"
      density="comfortable"
    >
      <v-spacer />
      <DropdownButton
        v-model="sortValue"
        :disabled="!authorsStore.authors.length"
        :items="sortItems"
        prepend-icon="$sort"
        text="Сортировать"
      />
    </v-toolbar>
    <v-container class="pb-16 mb-4">
      <DictionaryList
        v-if="sortedAuthors.length"
        :items="sortedAuthors"
        :loading="loading"
        @click-item="clickAuthor($event)"
      />
    </v-container>
  </v-main>
  <DictionaryEditDialog
    v-model="selectedAuthor.name"
    v-model:opened="dialog"
    :disabled="loadingGlobal"
    :is-created="!selectedAuthor.id"
    @remove="deleteAuthor()"
    @save="saveAuthor()"
  />
  <v-fab
    app
    appear
    class="mb-16"
    :disabled="loading"
    icon="$plus"
    @click="clickAuthor(0)"
  />
</template>

<script setup lang="ts">
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import useLoading from '@/composables/useLoading.ts';
import { sortString } from '@/core/utils/array.ts';
import { useAuthorsStore } from '@/stores/authors.ts';
import { useComicsStore } from '@/stores/comics.ts';
import AuthorController from '@/core/entities/author/AuthorController.ts';
import AuthorModel from '@/core/entities/author/AuthorModel.ts';

definePage({
  meta: {
    layout: 'full',
    title: 'Авторы',
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
  {
    id: 0,
    name: 'По имени',
  },
  {
    id: 1,
    name: 'По имени (обратная)',
  },
  {
    id: 2,
    name: 'По использованию',
  },
  {
    id: 3,
    name: 'По использованию (обратная)',
  },
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
