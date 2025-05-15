<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <v-toolbar density="comfortable">
        <v-select
          v-model="sortValue"
          :chips="false"
          class="mt-3"
          item-title="name"
          item-value="id"
          :items="sortItems"
          label="Сортировать"
        />
      </v-toolbar>
      <v-list activatable>
        <v-list-item
          v-for="item in sortedTags"
          :key="item.id"
          :active="item.id === selectedTag.id"
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
                icon="$edit"
                variant="tonal"
                @click="clickTag(item.id)"
              />
              <v-btn
                color="error"
                density="comfortable"
                icon="$delete"
                variant="tonal"
                @click="deleteTag(item.id)"
              />
            </v-list-item-action>
          </template>
        </v-list-item>
      </v-list>
    </v-container>
    <v-dialog v-model="dialog">
      <v-card>
        <v-card-title>
          {{ selectedTag.id ? 'Редактирование' : 'Создание' }}
        </v-card-title>
        <v-card-item class="pb-0">
          <v-text-field
            v-model.trim="selectedTag.name"
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
    <v-fab
      icon="$plus"
      @click="clickTag(0)"
    />
  </v-main>
</template>

<script setup lang="ts">
import { sortString } from '@/core/utils/array.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import TagController from '@/core/object-value/tag/TagController.ts';
import TagObject from '@/core/object-value/tag/TagObject.ts';

definePage({
  meta: {
    title: 'Теги',
  },
});

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

const tags = ref<TagObject[]>([]);

const loadTags = async () => {
  tags.value = await TagController.loadAll();
};

loadTags();

const selectedTag = ref(new TagObject());

const clickTag = (value: number) => {
  selectedTag.value = tags.value.find((e) => e.id === value) || new TagObject();
  dialog.value = true;
};

const tagsCount = computed(() => (
  tags.value.reduce((acc, tag) => {
    acc[tag.id] = 0;

    comics.value.forEach((item) => {
      if (item.tags.includes(tag.id)) acc[tag.id]++;
    });

    return acc;
  }, {} as Record<number, number>)
));

const fullTags = computed(() => (
  tags.value.map((item) => ({
    ...item.getDTO(),
    count: tagsCount.value[item.id] || 0,
  }))
));

const sortedTags = computed(() => (
  [...fullTags.value].sort((a, b) => {
    if (sortValue.value === 1) return sortString(b.name, a.name);
    else if (sortValue.value === 2) return a.count - b.count;
    else if (sortValue.value === 3) return b.count - a.count;
    else return sortString(a.name, b.name);
  })
));

const loading = ref(false);

const saveTag = async () => {
  try {
    loading.value = true;
    await TagController.save(selectedTag.value);
    await loadTags();
    dialog.value = false;
    Toast.show({ text: 'Тег сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loading.value = false;
  }
};

const deleteTag = async (id: number) => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить тег?',
  });

  if (!value) return;

  try {
    loading.value = true;
    await TagController.delete(id);
    await loadTags();
    Toast.show({ text: 'Тег удалён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loading.value = false;
  }
};
</script>
