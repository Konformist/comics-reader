<template>
  <v-main>
    <v-container class="pa-0">
      <v-list activatable>
        <v-list-item
          v-for="item in authors"
          :key="item"
          :active="item === currentAuthor"
          :title="item"
        >
          <template #append>
            <v-list-item-action end>
              <v-btn
                density="comfortable"
                icon="$edit"
                variant="tonal"
                @click="clickAuthor(item)"
              />
            </v-list-item-action>
          </template>
        </v-list-item>
      </v-list>
    </v-container>
    <v-dialog v-model="dialog">
      <v-card>
        <v-card-item class="pb-0">
          <v-text-field
            v-model="currentAuthor"
            variant="solo-filled"
          />
        </v-card-item>
        <v-card-actions>
          <v-btn
            text="Сохранить"
            @click="saveAuthor()"
          />
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-main>
</template>

<script setup lang="ts">
import ComicController from '@/core/entities/comic/ComicController.ts';
import type ComicModel from '@/core/entities/comic/ComicModel.ts';
import { dedupe } from '@/core/utils/array.ts';
import { Toast } from '@capacitor/toast';

definePage({
  meta: {
    title: 'Авторы',
  },
});

const dialog = ref(false);

const reserveAuthor = ref('');
const currentAuthor = ref('');

const clickAuthor = (value: string) => {
  reserveAuthor.value = value;
  currentAuthor.value = value;
  dialog.value = true;
}

const comics = ref<ComicModel[]>([]);
const authors = ref<string[]>();

const loadComics = async () => {
  comics.value = await ComicController.loadAll();
  authors.value = dedupe(comics.value.map(e => e.authors).flat(1))
    .sort();
}

loadComics();

const saveComics = async () => {
  for (const comic of comics.value) {
    await ComicController.save(comic);
  }
}

const saveAuthor = async () => {
  comics.value.forEach(comic => {
    comic.authors.push(currentAuthor.value);
    comic.authors = comic.authors.filter(e => e !== reserveAuthor.value);
  })
  await saveComics();
  await loadComics();
  dialog.value = false;
  Toast.show({ text: 'Автор сохранён' })
};
</script>
