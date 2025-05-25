<template>
  <v-main>
    <v-toolbar class="px-2">
      <v-select
        v-model="sortValue"
        :chips="false"
        :disabled="!tagsStore.tags.length"
        :items="sortItems"
        label="Сортировать"
      />
    </v-toolbar>
    <v-container class="pb-16 mb-4">
      <DictionaryList
        :items="sortedTags"
        :loading="loading"
        @click-item="clickTag($event)"
      />
    </v-container>
    <DictionaryEditDialog
      v-model="selectedTag.name"
      v-model:opened="dialog"
      :disabled="loadingGlobal"
      :is-created="!selectedTag.id"
      @remove="deleteTag()"
      @save="saveTag()"
    />
    <v-fab
      class="mb-14"
      :disabled="loading"
      icon="$plus"
      @click="clickTag(0)"
    />
  </v-main>
</template>

<script setup lang="ts">
import DictionaryEditDialog from '@/components/DictionaryEditDialog.vue';
import DictionaryList from '@/components/DictionaryList.vue';
import { useComicsStore } from '@/stores/comics.ts';
import { useTagsStore } from '@/stores/tags.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import { sortString } from '@/core/utils/array.ts';
import TagController from '@/core/entities/tag/TagController.ts';
import TagModel from '@/core/entities/tag/TagModel.ts';
import useLoading from '@/composables/useLoading.ts';

definePage({
  meta: {
    title: 'Теги',
    isBottomNavigation: true,
  },
});

const comicsStore = useComicsStore();
const tagsStore = useTagsStore();

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

onMounted(async () => {
  loadingStart();
  await Promise.all([
    comicsStore.loadComics(),
    tagsStore.loadTags(),
  ]);
  loadingEnd();
});

const selectedTag = ref(new TagModel());

const clickTag = (value: number) => {
  selectedTag.value = tagsStore.tags.find((e) => e.id === value) || new TagModel();
  dialog.value = true;
};

const tagsCount = computed(() => (
  tagsStore.tags.reduce((acc, tag) => {
    acc[tag.id] = 0;

    comicsStore.comics.forEach((item) => {
      if (item.tags.includes(tag.id)) acc[tag.id]++;
    });

    return acc;
  }, {} as Record<number, number>)
));

const fullTags = computed(() => (
  tagsStore.tags.map((item) => ({
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
    await tagsStore.loadTagsForce();
    dialog.value = false;
    Toast.show({ text: 'Тег сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};

const deleteTag = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить тег?',
  });

  if (!value) return;

  try {
    loadingGlobalStart();
    await TagController.remove(selectedTag.value.id);
    await tagsStore.loadTagsForce();
    dialog.value = false;
    Toast.show({ text: 'Тег удалён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
