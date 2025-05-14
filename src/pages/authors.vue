<template>
  <v-main scrollable>
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
            v-model.trim="currentAuthor"
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
  </v-main>
</template>

<script setup lang="ts">
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import { dedupe, sortString } from '@/core/utils/array.ts';
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
    .sort((a, b) => sortString(a, b));
}

loadComics();

const saveComics = async (value: ComicModel[]) => {
  for (const comic of value) {
    await ComicController.save(comic);
  }
}

const loading = ref(false);

const saveAuthor = async () => {
  try {
    loading.value = true;
    const changed = comics.value
      .filter(e => (e.authors.includes(reserveAuthor.value)))
      .map(e => new ComicModel(e.getDTO()));

    changed.forEach(comic => {
      comic.authors.push(currentAuthor.value);
      comic.authors = comic.authors.filter(e => e !== reserveAuthor.value);
    })
    await saveComics(changed);
    await loadComics();
    dialog.value = false;
    Toast.show({ text: 'Автор сохранён' })
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
};
</script>
