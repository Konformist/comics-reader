<template>
  <v-bottom-sheet
    :model-value="opened"
    :scrim="false"
    @update:model-value="opened = true"
  >
    <v-sheet class="py-2 rounded-b-0">
      <swiper-container
        class="w-100"
        v-bind="{
          centeredSlides: true,
          freeMode: true,
          initialSlide: active,
          modules: [FreeMode],
          slidesPerView: 'auto',
          shortSwipes: false,
          spaceBetween: 8,
          longSwipes: false,
        }"
      >
        <swiper-slide
          v-for="(item, index) in items"
          :key="item.id"
          class="pages-sheet--slide position-relative d-flex align-center justify-center"
        >
          <CustomImg
            cover
            height="100%"
            :src="item.file?.url"
            width="100%"
            @click="$emit('move',index)"
          />
          <div
            v-if="index === active"
            class="position-absolute top-0 bottom-0 right-0 left-0 bg-primary opacity-30 rounded-xl"
          />
        </swiper-slide>
      </swiper-container>
    </v-sheet>
  </v-bottom-sheet>
</template>

<script setup lang="ts">
import type ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';
import type { SwiperContainer, SwiperSlide } from 'swiper/element';
import { FreeMode } from 'swiper/modules';

const opened = defineModel<boolean>('opened', { default: false });

defineEmits<{ (e: 'move', v: number): void }>();

defineProps<{
  active: number
  items: ChapterPageModel[]
}>();
</script>

<style lang="scss" scoped>
.pages-sheet {
  &--slide {
    height: 128px;
    width: 100px;
  }
}
</style>
