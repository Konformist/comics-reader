<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <v-list activatable>
        <v-list-item
          v-for="item in authors"
          :key="item.id"
          :active="item.id === selectedAuthor.id"
          :title="item.name"
        >
          <template #append>
            <v-list-item-subtitle class="mr-1">
              {{ authorsCount[item.id] }}
            </v-list-item-subtitle>
            <v-list-item-action end>
              <v-btn
                class="mr-4"
                density="comfortable"
                icon="$edit"
                variant="tonal"
                @click="clickAuthor(item.id)"
              />
              <v-btn
                color="error"
                density="comfortable"
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
            :loading="loading"
            text="Сохранить"
            @click="saveAuthor()"
          />
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-fab
      icon="$plus"
      @click="clickAuthor(0)"
    />
  </v-main>
</template>

<script setup lang="ts">
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import AuthorController from '@/core/object-value/author/AuthorController.ts';
import AuthorObject from '@/core/object-value/author/AuthorObject.ts';

definePage({
  meta: {
    title: 'Авторы',
  },
});

const dialog = ref(false);

const comics = ref<ComicModel[]>([]);

const loadComics = async () => {
  comics.value = await ComicController.loadAll();
};

loadComics();

const authors = ref<AuthorObject[]>([]);

const loadAuthors = async () => {
  authors.value = await AuthorController.loadAll();
};

loadAuthors();

const selectedAuthor = ref(new AuthorObject());

const clickAuthor = (value: number) => {
  selectedAuthor.value = authors.value.find((e) => e.id === value) || new AuthorObject();
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

const loading = ref(false);

const saveAuthor = async () => {
  try {
    loading.value = true;
    await AuthorController.save(selectedAuthor.value);
    await loadAuthors();
    dialog.value = false;
    Toast.show({ text: 'Автор сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loading.value = false;
  }
};

const deleteAuthor = async (id: number) => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить автора?',
  });

  if (!value) return;

  try {
    loading.value = true;
    await AuthorController.delete(id);
    await loadAuthors();
    Toast.show({ text: 'Автор удалён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loading.value = false;
  }
};
</script>
