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
          :disabled="!languages.length"
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
        v-else-if="languages.length"
        activatable
      >
        <v-list-item
          v-for="item in sortedLanguages"
          :key="item.id"
          :active="item.id === selectedLanguage.id"
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
                @click="clickLanguage(item.id)"
              />
              <v-btn
                color="error"
                density="comfortable"
                icon="$delete"
                variant="tonal"
                @click="deleteLanguage(item.id)"
              />
            </v-list-item-action>
          </template>
        </v-list-item>
      </v-list>
    </v-container>
    <v-dialog v-model="dialog">
      <v-card>
        <v-card-title>
          {{ selectedLanguage.id ? 'Редактирование' : 'Создание' }}
        </v-card-title>
        <v-card-item class="pb-0">
          <v-text-field
            v-model.trim="selectedLanguage.name"
            variant="solo-filled"
          />
        </v-card-item>
        <v-card-actions>
          <v-btn
            :loading="loading"
            text="Сохранить"
            @click="saveLanguage()"
          />
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-fab
      :disabled="loading"
      icon="$plus"
      @click="clickLanguage(0)"
    />
  </v-main>
</template>

<script setup lang="ts">
import useLoading from '@/composables/useLoading.ts';
import { sortString } from '@/core/utils/array.ts';
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import LanguageController from '@/core/object-value/language/LanguageController.ts';
import LanguageObject from '@/core/object-value/language/LanguageObject.ts';

definePage({
  meta: {
    title: 'Языки',
  },
});

const { loading,loadingStart,loadingEnd } = useLoading();

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

const languages = ref<LanguageObject[]>([]);

const loadLanguages = async () => {
  languages.value = await LanguageController.loadAll();
};

loadLanguages();

const selectedLanguage = ref(new LanguageObject());

const clickLanguage = (value: number) => {
  selectedLanguage.value = languages.value.find((e) => e.id === value) || new LanguageObject();
  dialog.value = true;
};

const languagesCount = computed(() => (
  languages.value.reduce((acc, languale) => {
    acc[languale.id] = 0;

    comics.value.forEach((item) => {
      if (item.language === languale.id) acc[languale.id]++;
    });

    return acc;
  }, {} as Record<number, number>)
));

const fullLanguages = computed(() => (
  languages.value.map((item) => ({
    ...item.getDTO(),
    count: languagesCount.value[item.id] || 0,
  }))
));

const sortedLanguages = computed(() => (
  [...fullLanguages.value].sort((a, b) => {
    if (sortValue.value === 1) return sortString(b.name, a.name);
    else if (sortValue.value === 2) return a.count - b.count;
    else if (sortValue.value === 3) return b.count - a.count;
    else return sortString(a.name, b.name);
  })
));

const saveLanguage = async () => {
  try {
    loadingStart();
    await LanguageController.save(selectedLanguage.value);
    await loadLanguages();
    dialog.value = false;
    Toast.show({ text: 'Язык сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingEnd();
  }
};

const deleteLanguage = async (id: number) => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить язык?',
  });

  if (!value) return;

  try {
    loadingStart();
    await LanguageController.delete(id);
    await loadLanguages();
    Toast.show({ text: 'Язык удалён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingEnd();
  }
};
</script>
