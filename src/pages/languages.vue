<template>
  <v-main>
    <v-container class="pa-0">
      <v-list activatable>
        <v-list-item
          v-for="item in languages"
          :key="item"
          :active="item === currentLanguage"
          :title="item"
        >
          <template #append>
            <v-list-item-action end>
              <v-btn
                density="comfortable"
                icon="$edit"
                variant="tonal"
                @click="clickLanguage(item)"
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
            v-model="currentLanguage"
            variant="solo-filled"
          />
        </v-card-item>
        <v-card-actions>
          <v-btn
            text="Сохранить"
            @click="saveLanguage()"
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
    title: 'Теги',
  },
});

const dialog = ref(false);

const reserveLanguage = ref('');
const currentLanguage = ref('');

const clickLanguage = (value: string) => {
  reserveLanguage.value = value;
  currentLanguage.value = value;
  dialog.value = true;
}

const comics = ref<ComicModel[]>([]);
const languages = ref<string[]>();

const loadComics = async () => {
  comics.value = await ComicController.loadAll();
  languages.value = dedupe(comics.value.map(e => e.language)).sort();
}

loadComics();

const saveComics = async () => {
  for (const comic of comics.value) {
    await ComicController.save(comic);
  }
}

const saveLanguage = async () => {
  comics.value.forEach(comic => {
    if (comic.language === reserveLanguage.value) {
      comic.language = currentLanguage.value;
    }
  })
  await saveComics();
  await loadComics();
  dialog.value = false;
  Toast.show({ text: 'Язык сохранён' })
};
</script>
