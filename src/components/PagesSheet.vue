<template>
  <v-bottom-sheet v-model="opened">
    <v-card
      class="py-2 rounded-b-0"
      title="Страницы"
    >
      <swiper-container
        class="w-100"
        v-bind="{
          centeredSlides: true,
          freeMode: true,
          initialSlide: active,
          modules: [FreeMode],
          slidesPerView: 'auto',
          shortSwipes: false,
          longSwipes: false,
        }"
      >
        <swiper-slide
          v-for="(item, index) in items"
          :key="item.id"
          class="pages-sheet--slide px-1 d-flex align-center justify-center"
        >
          <CustomImg
            cover
            height="100%"
            :src="item.file?.url"
            width="100%"
            @click="$emit('move',index)"
          />
        </swiper-slide>
      </swiper-container>
    </v-card>
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
