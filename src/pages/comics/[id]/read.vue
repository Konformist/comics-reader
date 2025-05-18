<template>
  <v-main
    v-if="mounted"
    scrollable
  >
    <v-container
      v-if="appStore.settings.direction === 'none'"
      class="pa-0"
      min-height="100%"
    >
      <v-img
        v-for="item in comic.images"
        :key="item.id"
        max-width="100%"
        :src="item.url"
      />
    </v-container>
    <v-container
      v-else
      class="pa-0"
      height="100%"
    >
      <swiper-container
        :autoplay="isCycle ? {
          delay: appStore.settings.autoReadingTimeout * 1000,
          stopOnLastSlide: true,
        } : false"
        class="h-100"
        :direction="appStore.settings.direction"
        :initial-slide="currentPage"
        lazy
        :modules="[Autoplay, Pagination]"
        :pagination="{ el: '.swiper-pagination' }"
      >
        <swiper-slide
          v-for="item in comic.images"
          :key="item.id"
          class="d-flex align-center justify-center"
        >
          <v-img
            max-height="100%"
            max-width="100%"
            :src="item.url"
          />
        </swiper-slide>
      </swiper-container>
      <div class="swiper-pagination" />
    </v-container>
  </v-main>
</template>

<script lang="ts" setup>
import { register, type SwiperContainer, type SwiperSlide } from 'swiper/element';
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import { useAppStore } from '@/stores/app.ts';
import { KeepAwake } from '@capacitor-community/keep-awake';
import { Capacitor } from '@capacitor/core';
import { StatusBar } from '@capacitor/status-bar';
import { Autoplay, Pagination } from 'swiper/modules';

definePage({
  meta: {
    title: 'Чтение',
    isBack: true,
  },
});

const route = useRoute('/comics/[id]/read');
const router = useRouter();
const appStore = useAppStore();

register();

const mounted = ref(false);

onMounted(async () => {
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

const comicId = +(route.params.id || 0);

const comic = ref(new ComicModel());

const currentPage = ref<number>(+(route.query.page ?? 0));

const isCycle = computed(() => (
  appStore.settings.autoReading
  && currentPage.value !== comic.value.images.length - 1
));

const loadComic = async () => {
  comic.value = await ComicController.load(comicId);
  comic.value.images.forEach((e, i) => {
    if (i % 2 === 0) {
      e.url = 'https://cs11.pikabu.ru/post_img/2020/08/13/11/159734810613621269.webp';
    } else {
      e.url = 'https://cdn.fishki.net/upload/post/2021/02/16/3613245/tn/bled-1899264-1280.jpg';
    }
  });
  if (!comic.value.id) router.replace({ name: '/' });
};

loadComic();
</script>

<style lang="scss">
.swiper-pagination {
  height: 8px;
  display: flex;
  position: absolute;
  inset: 0 0 auto 0;
  z-index: 1;
  background-color: rgba(255, 255, 255, 0.4);

  &-bullet {
    height: 100%;
    flex-grow: 1;
    background-color: rgba(255, 255, 255, 0.6);
    transition: 0.3s;
  }

  &-bullet-active ~ .swiper-pagination-bullet {
    background-color: transparent;
  }
}
</style>
