<template>
  <v-card
    height="250"
    :image="cover.url"
    :to="{
      name: '/comics/[id]/',
      params: { id: comic.id }
    }"
  >
    <div class="ma-2 position-absolute top-0 right-0 d-flex">
      <v-badge
        color="info"
        :content="`${comic.imagesFilled.length} / ${comic.images.length}`"
        inline
      />
      <v-badge
        v-if="comic.isFilled"
        color="success"
        icon="$check"
        inline
      />
    </div>
    <v-card-text
      class="position-absolute bottom-0 left-0 right-0"
      style="background-color: rgba(0, 0, 0, 0.4)"
    >
      <span class="text-ellipsis">
        {{ comic.name }}
      </span>
    </v-card-text>
  </v-card>
</template>

<script lang="ts" setup>
import ComicController from '@/core/entities/comic/ComicController.ts';
import type ComicModel from '@/core/entities/comic/ComicModel.ts';
import FileModel from '@/core/object-value/file/FileModel.ts';

const { comic } = defineProps<{
  comic: ComicModel
}>();

const cover = ref(new FileModel());
const loadCover = async () => {
  cover.value = await ComicController.loadCover(comic.id);
};

loadCover();
</script>
