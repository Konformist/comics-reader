<template>
  <v-main>
    <v-container class="pa-0">
      <v-list activatable>
        <v-list-item
          v-for="item in tags"
          :key="item"
          :active="item === currentTag"
          :title="item"
        >
          <template #append>
            <v-list-item-action end>
              <v-btn
                density="comfortable"
                icon="$edit"
                variant="tonal"
                @click="clickTag(item)"
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
            v-model.trim="currentTag"
            variant="solo-filled"
          />
        </v-card-item>
        <v-card-actions>
          <v-btn
            text="Сохранить"
            @click="saveTag()"
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

const reserveTag = ref('');
const currentTag = ref('');

const clickTag = (value: string) => {
  reserveTag.value = value;
  currentTag.value = value;
  dialog.value = true;
}

const comics = ref<ComicModel[]>([]);
const tags = ref<string[]>();

const loadComics = async () => {
  comics.value = await ComicController.loadAll();
  tags.value = dedupe(comics.value.map(e => e.tags).flat(1))
    .sort();
}

loadComics();

const saveComics = async () => {
  for (const comic of comics.value) {
    await ComicController.save(comic);
  }
}

const saveTag = async () => {
  comics.value.forEach(comic => {
    if (comic.tags.includes(reserveTag.value)) {
      comic.tags.push(currentTag.value);
      comic.tags = comic.tags.filter(e => e !== reserveTag.value);
    }
  })
  await saveComics();
  await loadComics();
  dialog.value = false;
  Toast.show({ text: 'Тег сохранён' })
};
</script>
