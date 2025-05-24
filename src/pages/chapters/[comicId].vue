<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <v-list>
        <v-list-item
          v-for="(chapter, index) in chapters"
          :key="chapter.id"
          append-icon="$edit"
          :title="chapter.name || `Глава ${index + 1}`"
          :to="{
            name: '/chapters/[id]/edit',
            params: { id: chapter.id },
          }"
        />
      </v-list>
    </v-container>
    <v-fab
      :disabled="loadingGlobal"
      icon="$plus"
      @click="createChapter()"
    />
  </v-main>
</template>

<script setup lang="ts">
import useLoading from '@/composables/useLoading.ts';
import ChapterController from '@/core/entities/chapter/ChapterController.ts';
import ChapterModel from '@/core/entities/chapter/ChapterModel.ts';
import { Toast } from '@capacitor/toast';

definePage({
  meta: {
    title: 'Редактирование глав',
    isBack: true,
  },
});

const router = useRouter();
const route = useRoute('/chapters/[comicId]');
const comicId = +route.params.comicId;

const {
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
} = useLoading();

const chapters = ref<ChapterModel[]>([]);

const loadChapters = async () => {
  chapters.value = await ChapterController.loadAll(comicId);
};

loadChapters();

const createChapter = async () => {
  try {
    loadingGlobalStart();
    const chapterId = await ChapterController.save(
      new ChapterModel({ comicId }),
    );

    if (!chapterId) return;

    Toast.show({ text: 'Глава создана' });
    await router.push({
      name: '/chapters/[id]/edit',
      params: { id: chapterId.toString() },
    });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>
