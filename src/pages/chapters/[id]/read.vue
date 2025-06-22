<template>
  <v-app-bar
    class="rounded-b-xl"
    :elevation="!showInterface ? 0 : undefined"
    :model-value="showInterface"
    :title="chapter.name || `Глава ${chapterIndex + 1}`"
  >
    <template #prepend>
      <v-btn
        icon="$arrow-left"
        rounded="pill"
        @click="$router.replace({
          name: '/comics/[id]/',
          params: { id: comicId },
        })"
      />
    </template>
    <template #extension>
      <div class="d-flex px-4 w-100">
        <v-spacer />
        <SmallBtn
          :active="showChapters"
          prepend-icon="$menu"
          text="Главы"
          @click="showChapters = true"
        />
      </div>
    </template>
  </v-app-bar>
  <v-main
    class="pt-0 pb-0"
    style="height: 100vh"
  >
    <v-container class="pa-0 h-100">
      <ReadContent
        :key="chapterId"
        ref="readContentRef"
        v-model="currentPage"
        class="h-100 w-100"
        :items="chapter.pages"
        @click="toggleInterface()"
        @read="onRead($event)"
      />
    </v-container>
  </v-main>
  <PagesSheet
    :active="currentPage"
    :items="chapter.pages"
    :opened="showInterface"
    @move="currentPage = $event"
  />
  <ChaptersSheet
    v-model="showChapters"
    :active="chapterId"
    :comic-id="comicId"
    :items="chapters"
    @move-chapter="moveChapter($event)"
  />
  <v-progress-linear
    class="position-fixed"
    location="bottom"
    :max="chapter.pages.length"
    :model-value="currentPage + 1"
    style="z-index: 1"
  />
  <v-fab
    v-if="nextChapterId && currentPage >= chapter.pages.length - 1"
    app
    appear
    append-icon="$arrow-right"
    text="Продолжить"
    @click="moveChapter(nextChapterId)"
  />
</template>

<script lang="ts" setup>
import type ReadContent from '@/components/ReadContent.vue';
import SmallBtn from '@/components/SmallBtn.vue';
import ChapterPageController from '@/core/entities/chapter-page/ChapterPageController.ts';
import type ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';
import UI from '@/plugins/UIPlugin.ts';
import ChapterController from '@/core/entities/chapter/ChapterController.ts';
import ChapterModel from '@/core/entities/chapter/ChapterModel.ts';

definePage({
  meta: {
    layout: 'empty',
    title: 'Чтение',
  },
});

const route = useRoute('/chapters/[id]/read');
const router = useRouter();

const comicId = +(route.query?.comic || 0);
const chapterId = ref(+(route.params.id || 0));

const chapters = ref<ChapterModel[]>([]);

const loadChapters = async () => {
  chapters.value = await ChapterController.loadAll(comicId);
};

const readContentRef = ref<typeof ReadContent>();

const showInterface = ref(false);
const currentPage = ref(+(route.query?.page || 0));

router.beforeResolve(() => {
  currentPage.value = 0;
});

const toggleInterface = (value?: boolean) => {
  showInterface.value = value ?? !showInterface.value;
  if (showInterface.value) readContentRef.value?.stop();
  else readContentRef.value?.start();
};

const showChapters = ref(false);

const moveChapter = (id: number) => {
  toggleInterface(false);
  showChapters.value = false;
  router.replace({
    name: '/chapters/[id]/read',
    params: { id },
    query: { comic: comicId },
  });
};

const chapter = ref<ChapterModel>(new ChapterModel());
const chapterIndex = computed(() => (
  chapters.value.findIndex((e) => e.id === chapterId.value)
));

const nextChapterId = computed(() => (chapters.value[chapterIndex.value + 1]?.id));

const loadChapter = async () => {
  chapter.value = await ChapterController.load(chapterId.value);
};

watch(
  () => route.params.id,
  (val) => {
    chapterId.value = +val;
    loadChapter();
    loadChapters();
    currentPage.value = 0;
  },
);

onMounted(async () => {
  await loadChapter();

  if (!chapter.value.id) {
    router.replace({ name: '/' });
  } else {
    loadChapters();
    UI.reading({ mode: 'start' });
  }
});

onBeforeUnmount(async () => {
  UI.reading({ mode: 'end' });
});

const onRead = async (item: ChapterPageModel) => {
  if (item.isRead) return;

  try {
    item.isRead = true;
    await ChapterPageController.save(item);
  } catch (e) {
    UI.toast({ text: `Ошибка: ${e}` });
  }
};
</script>
