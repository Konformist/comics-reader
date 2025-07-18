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
        :disabled="languagesStore.languages.length === 0"
        :items="sortItems"
        prepend-icon="$sort"
        text="Сортировать"
      />
    </v-toolbar>
    <v-container class="pb-16 mb-4">
      <DictionaryList
        v-if="sortedLanguages.length > 0"
        :items="sortedLanguages"
        :loading="loading"
        @click-item="clickLanguage($event)"
      />
    </v-container>
  </v-main>
  <DictionaryEditDialog
    v-model="selectedLanguage.name"
    v-model:opened="dialog"
    :disabled="loadingGlobal"
    :is-created="!selectedLanguage.id"
    @remove="deleteLanguage()"
    @save="saveLanguage()"
  />
  <v-fab
    app
    appear
    class="mb-16"
    :disabled="loading"
    icon="$plus"
    @click="clickLanguage(0)"
  />
</template>

<script setup lang="ts">
import { Dialog } from '@capacitor/dialog';
import DictionaryEditDialog from '@/components/DictionaryEditDialog.vue';
import DictionaryList from '@/components/DictionaryList.vue';
import useLoading from '@/composables/useLoading.ts';
import LanguageController from '@/core/entities/language/LanguageController.ts';
import LanguageModel from '@/core/entities/language/LanguageModel.ts';
import { sortString } from '@/core/utils/array.ts';
import UI from '@/plugins/UIPlugin.ts';
import { useComicsStore } from '@/stores/comics.ts';
import { useLanguagesStore } from '@/stores/languages.ts';

definePage({
  meta: {
    layout: 'full',
    title: 'Языки',
  },
});

const comicsStore = useComicsStore();
const languagesStore = useLanguagesStore();

const {
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
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
    languagesStore.loadLanguages(),
  ]);
  loadingEnd();
});

const selectedLanguage = ref(new LanguageModel());

const clickLanguage = (value: number) => {
  selectedLanguage.value = languagesStore.languages.find((e) => e.id === value) || new LanguageModel();
  dialog.value = true;
};

const languagesCount = computed(() => (
  languagesStore.languages.reduce((acc, languale) => {
    acc[languale.id] = 0;

    for (const item of comicsStore.comics) {
      if (item.languageId === languale.id) acc[languale.id]++;
    }

    return acc;
  }, {} as Record<number, number>)
));

const fullLanguages = computed(() => (
  languagesStore.languages.map((item) => ({
    ...item.getDTO(),
    count: languagesCount.value[item.id] || 0,
  }))
));

const sortedLanguages = computed(() => (
  [...fullLanguages.value].sort((a, b) => {
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

const saveLanguage = async () => {
  try {
    loadingGlobalStart();
    await LanguageController.save(selectedLanguage.value);
    await languagesStore.loadLanguagesForce();
    dialog.value = false;
    UI.toast({ text: 'Язык сохранён' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};

const deleteLanguage = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить язык?',
  });

  if (!value) return;

  try {
    loadingGlobalStart();
    await LanguageController.remove(selectedLanguage.value.id);
    await languagesStore.loadLanguagesForce();
    dialog.value = false;
    UI.toast({ text: 'Язык удалён' });
  } catch (error) {
    UI.toast({ text: `Ошибка: ${error}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
