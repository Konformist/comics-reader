<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <v-list activatable>
        <v-list-item
          v-for="item in languages"
          :key="item.id"
          :active="item.id === selectedLanguage.id"
          :title="item.name"
        >
          <template #append>
            <v-list-item-subtitle class="mr-1">
              {{ languagesCount[item.id] }}
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
      icon="$plus"
      @click="clickLanguage(0)"
    />
  </v-main>
</template>

<script setup lang="ts">
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import LanguageController from '@/core/object-value/language/LanguageController.ts';
import LanguageObject from '@/core/object-value/language/LanguageObject.ts';

definePage({
  meta: {
    title: 'Теги',
  },
});

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

const loading = ref(false);

const saveLanguage = async () => {
  try {
    loading.value = true;
    await LanguageController.save(selectedLanguage.value);
    await loadLanguages();
    dialog.value = false;
    Toast.show({ text: 'Язык сохранён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loading.value = false;
  }
};

const deleteLanguage = async (id: number) => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить язык?',
  });

  if (!value) return;

  try {
    loading.value = true;
    await LanguageController.delete(id);
    await loadLanguages();
    Toast.show({ text: 'Язык удалён' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loading.value = false;
  }
};
</script>
