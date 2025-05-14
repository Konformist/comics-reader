<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <v-list activatable>
        <v-list-item
          v-for="item in tags"
          :key="item"
          :active="item === reserveTag"
          :title="item"
        >
          <template #append>
            <v-list-item-subtitle class="mr-1">
              {{ tagsCount[item] }}
            </v-list-item-subtitle>
            <v-list-item-action end>
              <v-btn
                class="mr-4"
                density="comfortable"
                icon="$edit"
                variant="tonal"
                @click="clickTag(item)"
              />
              <v-btn
                color="error"
                density="comfortable"
                icon="$delete"
                variant="tonal"
                @click="deleteTag(item)"
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
            :loading="loading"
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
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import { dedupe, sortString } from '@/core/utils/array.ts';
import { Dialog } from '@capacitor/dialog';
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
const tags = ref<string[]>([]);
const tagsCount = computed(() => (
  tags.value.reduce((acc, tag) => {
    acc[tag] = 0;

    comics.value.forEach(item => {
      if (item.tags.includes(tag)) acc[tag]++;
    })

    return acc;
  }, {} as Record<string, number>)
))

const loadComics = async () => {
  comics.value = await ComicController.loadAll();
  tags.value = dedupe(comics.value.map(e => e.tags).flat(1))
    .sort((a, b) => sortString(a, b));
}

loadComics();

const saveComics = async (value: ComicModel[]) => {
  for (const comic of value) {
    await ComicController.save(comic);
  }
}

const loading = ref(false);

const saveTag = async () => {
  try {
    const changed = comics.value
      .filter(e => (e.tags.includes(reserveTag.value)))
      .map(e => new ComicModel(e.getDTO()));

    changed.forEach(comic => {
      comic.tags.push(currentTag.value);
      comic.tags = comic.tags.filter(e => e !== reserveTag.value);
    })
    await saveComics(changed);
    await loadComics();
    dialog.value = false;
    Toast.show({ text: 'Тег сохранён' })
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
};

const deleteTag = async (item: string) => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить тег?',
  });

  if (!value) return;

  try {
    const changed = comics.value
      .filter(e => (e.tags.includes(item)))
      .map(e => new ComicModel(e.getDTO()));

    changed.forEach(comic => {
      comic.tags = comic.tags.filter(e => e !== item);
    });

    await saveComics(changed);
    await loadComics();
    Toast.show({ text: 'Тег удалён' })
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` })
  } finally {
    loading.value = false;
  }
}
</script>
