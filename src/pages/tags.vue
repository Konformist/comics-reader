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
          :disabled="!tags.length"
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
        v-else-if="tags.length"
        activatable
      >
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
                :disabled="loadingGlobal"
                icon="$edit"
                variant="tonal"
                @click="clickTag(item.id)"
              />
              <v-btn
                color="error"
                density="comfortable"
                :disabled="loadingGlobal"
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
            :disabled="loadingGlobal"
            text="Сохранить"
            @click="saveTag()"
          />
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-fab
      :disabled="loading"
      icon="$plus"
      @click="clickTag(0)"
    />
  </v-main>
</template>

<script setup lang="ts">
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import { sortString } from '@/core/utils/array.ts';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import TagController from '@/core/entities/tag/TagController.ts';
import TagModel from '@/core/entities/tag/TagModel.ts';
import useLoading from '@/composables/useLoading.ts';

definePage({
  meta: {
    title: 'Теги',
  },
});

const {
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
  loading,
  loadingStart,
  loadingEnd,
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

const tags = ref<TagModel[]>([]);

const loadTags = async () => {
  tags.value = await TagController.loadAll();
};

onMounted(async () => {
  loadingStart();
  await Promise.all([
    loadComics(),
    loadTags(),
  ]);
  loadingEnd();
});

const selectedTag = ref(new TagModel());

const clickTag = (value: number) => {
  selectedTag.value = tags.value.find((e) => e.id === value) || new TagModel();
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

const saveTag = async () => {
  try {
    loadingGlobalStart();
    await TagController.save(selectedTag.value);
    await loadTags();
    dialog.value = false;
    Toast.show({ text: 'Тег сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const deleteTag = async (id: number) => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить тег?',
  });

  if (!value) return;

  try {
    loadingGlobalStart();
    await TagController.remove(id);
    await loadTags();
    Toast.show({ text: 'Тег удалён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
