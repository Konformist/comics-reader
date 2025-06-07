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
import { Autoplay, FreeMode } from 'swiper/modules';
import type ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';
import { useAppStore } from '@/stores/app.ts';

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
  if (swiperRef.value) swiperRef.value.swiper.autoplay?.stop();
};

const swiperStart = () => {
  if (!swiperRef.value || !appStore.settings.autoReading) return;
  swiperRef.value.swiper.autoplay?.start();
};

const moveSlide = (index: number): void => {
  if (swiperRef.value) swiperRef.value.swiper.slideTo(index);
};

const modeWebtoon = computed<SwiperOptions>(() => ({
  direction: 'vertical',
  resistanceRatio: 0.5,
  freeMode: true,
  slidesPerView: 'auto',
  modules: [FreeMode],
}));

const modeVertical = computed<SwiperOptions>(() => ({
  direction: 'vertical',
  autoplay: {
    enabled: appStore.settings.autoReading,
    delay: appStore.settings.autoReadingTimeout,
    stopOnLastSlide: true,
  },
  modules: [Autoplay],
}));

const modeHorizontal = computed<SwiperOptions>(() => ({
  direction: 'horizontal',
  autoplay: {
    enabled: appStore.settings.autoReading,
    delay: appStore.settings.autoReadingTimeout,
    stopOnLastSlide: true,
  },
  modules: [Autoplay],
}));

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

const updateSwiper = () => (
  nextTick(() => {
    swiperRef.value?.swiper.update();
  })
);

watch(
  () => items,
  () => {
    swiperRef.value?.swiper.disable();
    swiperRef.value?.swiper.enable();
    nextTick(() => {
      swiperRef.value?.swiper.update();
    });
  },
  { deep: true },
);

watch(
  model,
  (value) => {
    if (swiperRef.value
      && swiperRef.value.swiper.activeIndex !== value) {
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
    if (!swiperRef.value) return;

    moveSlide(model.value);

    swiperRef.value.swiper.on('activeIndexChange', (swiper) => {
      model.value = swiper.activeIndex;
    });
    swiperRef.value.swiper.on('reachEnd', () => {
      model.value = items.length - 1;
    });
  });
});
</script>
