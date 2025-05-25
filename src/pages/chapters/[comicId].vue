<template>
  <v-main>
    <v-container class="pb-16 mb-4">
      <DictionaryList
        :items="chaptersList"
        :loading="loading"
        @click-item="$router.push({
          name: '/chapters/[id]/edit',
          params: { id: $event }
        })"
      />
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
  loading,
  loadingStart,
  loadingEnd,
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
} = useLoading();

const chapters = ref<ChapterModel[]>([]);
const chaptersList = computed(() => (
  chapters.value.map((e, i) => ({
    id: e.id,
    name: e.name || `Глава ${i + 1}`,
  }))
));

const loadChapters = async () => {
  chapters.value = await ChapterController.loadAll(comicId);
};

onMounted(async () => {
  loadingStart();
  await loadChapters();
  loadingEnd();
});

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
