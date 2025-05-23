<template>
  <v-main
    v-if="mounted"
    scrollable
  >
    <v-container
      v-if="appStore.settings.direction === 'webtoon'"
      class="pa-0"
      min-height="100%"
    >
      <ChapterPageRead
        v-for="item in chapter.pages"
        :key="item.id"
        :item="item"
        max-width="100%"
        @download="onLoadImage(item)"
      />
    </v-container>
    <v-container
      v-else
      class="pa-0"
      height="100%"
    >
      <swiper-container
        :autoplay="autoPlay"
        class="h-100"
        :direction="appStore.settings.direction"
        :initial-slide="currentPage"
        lazy
        :modules="[Autoplay, Pagination]"
        :pagination="{ el: '.swiper-pagination' }"
      >
        <swiper-slide
          v-for="item in chapter.pages"
          :key="item.id"
          class="d-flex align-center justify-center"
        >
          <ChapterPageRead
            :item="item"
            max-height="100%"
            max-width="100%"
            @download="onLoadImage(item)"
          />
        </swiper-slide>
      </swiper-container>
      <div class="swiper-pagination" />
    </v-container>
  </v-main>
</template>

<script lang="ts" setup>
import ChapterPageRead from '@/components/ChapterPageRead.vue';
import useLoading from '@/composables/useLoading.ts';
import ChapterPageController from '@/core/entities-v2/chapter-page/ChapterPageController.ts';
import type ChapterPageModel from '@/core/entities-v2/chapter-page/ChapterPageModel.ts';
import ParserController from '@/core/entities-v2/parser/ParserController.ts';
import { KeepAwake } from '@capacitor-community/keep-awake';
import { Capacitor } from '@capacitor/core';
import { StatusBar } from '@capacitor/status-bar';
import { Toast } from '@capacitor/toast';
import { Autoplay, Pagination } from 'swiper/modules';
import { register, type SwiperContainer, type SwiperSlide } from 'swiper/element';
import { useAppStore } from '@/stores/app.ts';
import ChapterController from '@/core/entities-v2/chapter/ChapterController.ts';
import ChapterModel from '@/core/entities-v2/chapter/ChapterModel.ts';

definePage({
  meta: {
    title: 'Чтение',
    isBack: true,
  },
});
const {
  loadingGlobal,
  loadingGlobalStart,
  loadingGlobalEnd,
} = useLoading();

const route = useRoute('/comics/[id]/chapter-read');
const router = useRouter();
const appStore = useAppStore();

const autoPlay = computed(() => (
  appStore.settings.autoReading && !loadingGlobal.value
    ? { delay: appStore.settings.autoReadingTimeout * 1000, stopOnLastSlide: true }
    : false
));

register();

const currentPage = +(route.query.page ?? 0);

const chapterId = +(route.params.id || 0);

const chapter = ref<ChapterModel>(new ChapterModel());
const loadChapter = async () => {
  chapter.value = await ChapterController.load(chapterId);
};

const mounted = ref(false);

onMounted(async () => {
  await loadChapter();

  if (!chapter.value.id) {
    router.replace({ name: '/' });
    return;
  }

  if ((await KeepAwake.isSupported()).isSupported) {
    await KeepAwake.keepAwake();
  }

  if (Capacitor.isNativePlatform()) {
    await StatusBar.hide();
  }

  mounted.value = true;
});

onBeforeUnmount(async () => {
  if ((await KeepAwake.isSupported()).isSupported) {
    await KeepAwake.allowSleep();
  }

  if (Capacitor.isNativePlatform()) {
    await StatusBar.show();
  }
});

const onLoadImage = async (item: ChapterPageModel) => {
  if (!item.fromUrl) return;

  try {
    loadingGlobalStart();
    const result = await ParserController.loadImageRaw(item.fromUrl);
    await ChapterPageController.saveFile(item.id, result);
    await loadChapter();
    Toast.show({ text: 'Глава сохранена' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  } finally {
    loadingGlobalEnd();
  }
};
</script>

<style lang="scss">
.swiper-pagination {
  height: 4px;
  display: flex;
  position: absolute;
  inset: 0 0 auto 0;
  z-index: 1;
  background-color: rgba(255, 255, 255, 0.2);

  &-bullet {
    height: 100%;
    flex-grow: 1;
    background-color: rgb(255, 255, 255);
    transition: 0.3s;
  }

  &-bullet-active ~ .swiper-pagination-bullet {
    background-color: transparent;
  }
}
</style>
