<template>
  <v-app-bar>
    <v-btn
      icon="$arrow-left"
      slim
      @click="$router.back()"
    />
    <v-app-bar-title
      class="mr-8"
      :text="comic.name"
    />
  </v-app-bar>
  <v-main>
    <v-container>
      <ComicImage
        v-if="comic.images[current]"
        :src="comic.images[current].url"
      />
      <div
        class="cursor-pointer w-50 position-absolute bottom-0 top-0 left-0"
        @click="prev()"
      />
      <div
        class="cursor-pointer w-50 position-absolute bottom-0 top-0 right-0"
        @click="next()"
      />
    </v-container>
  </v-main>
</template>
<script lang="ts" setup>
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';

const route = useRoute('/comics/[id]/read');

const comic = ref(new ComicModel());
const current = ref(0);

const prev = () => {
  const prevValue = current.value - 1;

  if (prevValue >= 0) {
    current.value = prevValue;
  }
};

const next = () => {
  const nextValue = current.value + 1;

  if (nextValue <= comic.value.images.length - 1) {
    current.value = nextValue;
  }
};

const loadComic = async () => {
  const comicId = +route.params.id;

  if (!comicId) return;

  comic.value = await ComicController.load(comicId);
}

loadComic();
</script>
