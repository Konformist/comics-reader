<template>
  <swiper-container
    ref="swiperRef"
    v-bind="swiperMode"
  >
    <swiper-slide
      v-for="item in items"
      :key="item.id"
      v-bind="swiperSlideMode"
      @click="$emit('open-pages')"
    >
      <ChapterPageRead
        :item="item"
        :max-height="appStore.settings.readingMode === 'webtoon' ? '' : '100%'"
        max-width="100%"
        @error="updateSwiper()"
        @load="updateSwiper()"
        @read="$emit('read', item)"
      />
    </swiper-slide>
  </swiper-container>
</template>

<script setup lang="ts">
import type { SwiperOptions } from 'swiper/types';
import { register } from 'swiper/element';
import type { SwiperContainer, SwiperSlide } from 'swiper/element';
import { Autoplay, FreeMode, Zoom } from 'swiper/modules';
import type ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';
import { useAppStore } from '@/stores/app.ts';
import debounce from '@/core/utils/debounce.ts';

register();

const appStore = useAppStore();

const model = defineModel<number>({ default: 0 });

defineEmits<{
  (e: 'open-pages', v: void): void
  (e: 'read', v: ChapterPageModel): void
}>();

const { items } = defineProps<{ items: ChapterPageModel[] }>();

const swiperRef = ref<SwiperContainer>();

const swiperStop = () => {
  swiperRef.value?.swiper.autoplay?.stop();
};

const swiperStart = () => {
  if (!appStore.settings.autoReading) return;
  swiperRef.value?.swiper.autoplay?.start();
};

const moveSlide = (index: number): void => {
  swiperRef.value?.swiper.slideTo(index);
};

const modeWebtoon = computed(() => ({
  direction: 'vertical',
  resistanceRatio: 0.5,
  freeMode: true,
  zoom: true,
  shortSwipes: false,
  longSwipes: false,
  slidesPerView: 'auto',
  modules: [FreeMode, Zoom],
} as SwiperOptions));

const modeVertical = computed(() => ({
  direction: 'vertical',
  zoom: true,
  autoplay: {
    enabled: appStore.settings.autoReading,
    delay: appStore.settings.autoReadingTimeout,
    stopOnLastSlide: true,
  },
  modules: [Autoplay, Zoom],
} as SwiperOptions));

const modeHorizontal = computed(() => ({
  direction: 'horizontal',
  zoom: true,
  autoplay: {
    enabled: appStore.settings.autoReading,
    delay: appStore.settings.autoReadingTimeout,
    stopOnLastSlide: true,
  },
  modules: [Autoplay, Zoom],
} as SwiperOptions));

const swiperMode = computed(() => {
  switch (appStore.settings.readingMode) {
  case 'webtoon': return modeWebtoon.value;
  case 'horizontal': return modeHorizontal.value;
  default: return modeVertical.value;
  }
});

const modeSlideWebtoon = computed(() => ({
  style: 'width: 100%; height: auto',
  class: 'd-flex align-center justify-center',
}));
const modeSlideVertical = computed(() => ({ class: 'd-flex align-center justify-center' }));
const modeSlideHorizontal = computed(() => ({ class: 'd-flex align-center justify-center' }));

const swiperSlideMode = computed(() => {
  switch (appStore.settings.readingMode) {
  case 'webtoon': return modeSlideWebtoon.value;
  case 'horizontal': return modeSlideHorizontal.value;
  default: return modeSlideVertical.value;
  }
});

const updateSwiper = debounce(() => (
  nextTick(() => {
    swiperRef.value?.swiper.update();
  })
), 200);

watch(
  model,
  (value) => {
    if (swiperRef.value?.swiper.activeIndex !== value) {
      moveSlide(value);
    }
  },
);

defineExpose({
  stop: swiperStop,
  start: swiperStart,
});

onMounted(() => {
  nextTick(() => {
    moveSlide(model.value);

    swiperRef.value?.swiper.on('activeIndexChange', (swiper) => {
      model.value = swiper.activeIndex;
    });
    swiperRef.value?.swiper.on('progress', (_, value) => {
      if (value > 0.9) model.value = items.length - 1;
    });
  });
});
</script>
