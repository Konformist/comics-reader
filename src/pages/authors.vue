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
          :disabled="!authors.length"
          item-title="name"
          item-value="id"
          :items="sortItems"
          label="Сортировать"
        />
      </v-toolbar>
      <v-list v-if="loading">
        <v-skeleton-loader
          type="list-item"
        />
      </v-list>
      <v-list
        v-else-if="authors.length"
        activatable
      >
        <v-list-item
          v-for="item in sortedAuthors"
          :key="item.id"
          :active="item.id === selectedAuthor.id"
          :title="item.name"
        >
          <template #append>
            <v-list-item-subtitle class="mr-1">
              {{ item.count }}
            </v-list-item-subtitle>
            <v-list-item-action end>
              <v-btn
                class="mr-4"
                density="comfortable"
                :disabled="loadingGlobal"
                icon="$edit"
                variant="tonal"
                @click="clickAuthor(item.id)"
              />
              <v-btn
                color="error"
                density="comfortable"
                :disabled="loadingGlobal"
                icon="$delete"
                variant="tonal"
                @click="deleteAuthor(item.id)"
              />
            </v-list-item-action>
          </template>
        </v-list-item>
      </v-list>
    </v-container>
    <v-dialog v-model="dialog">
      <v-card>
        <v-card-title>
          {{ selectedAuthor.id ? 'Редактирование' : 'Создание' }}
        </v-card-title>
        <v-card-item class="pb-0">
          <v-text-field
            v-model.trim="selectedAuthor.name"
            variant="solo-filled"
          />
        </v-card-item>
        <v-card-actions>
          <v-btn
            :disabled="loadingGlobal"
            text="Сохранить"
            @click="saveAuthor()"
          />
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-fab
      class="mb-14"
      :disabled="loading"
      icon="$plus"
      @click="clickAuthor(0)"
    />
  </v-main>
</template>

<script setup lang="ts">
import useLoading from '@/composables/useLoading.ts';
import { sortString } from '@/core/utils/array.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import AuthorController from '@/core/entities/author/AuthorController.ts';
import AuthorModel from '@/core/entities/author/AuthorModel.ts';

definePage({
  meta: {
    title: 'Авторы',
    isBottomNavigation: true,
  },
});

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

const comics = ref<ComicModel[]>([]);

const loadComics = async () => {
  comics.value = await ComicController.loadAll();
};

loadComics();

const authors = ref<AuthorModel[]>([]);

const loadAuthors = async () => {
  authors.value = await AuthorController.loadAll();
};

loadAuthors();

onMounted(async () => {
  loadingStart();
  await Promise.all([
    loadComics(),
    loadAuthors(),
  ]);
  loadingEnd();
});

const selectedAuthor = ref(new AuthorModel());

const clickAuthor = (value: number) => {
  selectedAuthor.value = authors.value.find((e) => e.id === value) || new AuthorModel();
  dialog.value = true;
};

const authorsCount = computed(() => (
  authors.value.reduce((acc, author) => {
    acc[author.id] = 0;

    comics.value.forEach((item) => {
      if (item.authors.includes(author.id)) acc[author.id]++;
    });

    return acc;
  }, {} as Record<number, number>)
));

const fullAuthors = computed(() => (
  authors.value.map((item) => ({
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
    await loadAuthors();
    dialog.value = false;
    Toast.show({ text: 'Автор сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const deleteAuthor = async (id: number) => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить автора?',
  });

  if (!value) return;

  try {
    loadingGlobalStart();
    await AuthorController.remove(id);
    await loadAuthors();
    Toast.show({ text: 'Автор удалён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
