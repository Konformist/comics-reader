<template>
  <v-main>
    <v-toolbar
      class="px-4"
      color="background"
      density="comfortable"
    >
      <v-spacer />
      <DropdownButton
        v-model="sortValue"
        :disabled="tagsStore.tags.length === 0"
        :items="sortItems"
        prepend-icon="$sort"
        text="Сортировать"
      />
    </v-toolbar>
    <v-container class="pb-16 mb-4">
      <DictionaryList
        v-if="sortedTags.length > 0"
        :items="sortedTags"
        :loading="loading"
        @click-item="clickTag($event)"
      />
    </v-container>
  </v-main>
  <DictionaryEditDialog
    v-model="selectedTag.name"
    v-model:opened="dialog"
    :disabled="loadingGlobal"
    :is-created="!selectedTag.id"
    @remove="deleteTag()"
    @save="saveTag()"
  />
  <v-fab
    app
    appear
    class="mb-16"
    :disabled="loading"
    icon="$plus"
    @click="clickTag(0)"
  />
</template>

<script setup lang="ts">
import { Dialog } from '@capacitor/dialog';
import useLoading from '@/composables/useLoading.ts';
import TagController from '@/core/entities/tag/TagController.ts';
import TagModel from '@/core/entities/tag/TagModel.ts';
import { sortString } from '@/core/utils/array.ts';
import UI from '@/plugins/UIPlugin.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useTagsStore } from '@/stores/tags.ts';

definePage({
  meta: {
    layout: 'full',
    title: 'Теги',
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
  {
    id: 0,
    name: 'По имени',
  },
  {
    id: 1,
    name: 'По имени (обратная)',
  },
  {
    id: 2,
    name: 'По использованию',
  },
  {
    id: 3,
    name: 'По использованию (обратная)',
  },
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

    for (const item of comicsStore.comics) {
      if (item.tags.includes(tag.id)) acc[tag.id]++;
    }

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
    switch (sortValue.value) {
    case 1: { return sortString(b.name, a.name);
    }
    case 2: { return a.count - b.count;
    }
    case 3: { return b.count - a.count;
    }
    default: { return sortString(a.name, b.name);
    }
    }
  })
));

const saveTag = async () => {
  try {
    loadingGlobalStart();
    await TagController.save(selectedTag.value);
    await tagsStore.loadTagsForce();
    dialog.value = false;
    UI.toast({ text: 'Тег сохранён' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
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
    UI.toast({ text: 'Тег удалён' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
