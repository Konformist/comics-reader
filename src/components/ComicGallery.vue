<template>
  <v-card
    @click="$emit('move-comic', comic.id)"
  >
    <div class="d-flex flex-no-wrap justify-space-between">
      <CustomImg
        class="flex-0-0"
        cover
        height="200"
        :src="comic.cover?.file?.url"
        width="140"
      />
      <div
        class="flex-1-1 py-4"
        style="min-width: 0"
      >
        <v-card-text class="py-1 text-body-1">
          <div
            class="text-ellipsis"
            style="line-clamp: 2"
          >
            {{ comic.name }}
          </div>
        </v-card-text>
        <v-card-text class="py-1 text-grey">
          {{ comicAuthors.join(', ') || '-' }}
        </v-card-text>
        <v-card-text class="py-1">
          <div class="d-flex flex-wrap ga-1">
            <v-chip
              v-for="item in comicTags"
              :key="item"
              color="primary"
              size="small"
              :text="item"
              variant="tonal"
            />
          </div>
        </v-card-text>
      </div>
    </div>
  </v-card>
</template>

<script lang="ts" setup>
import type AuthorModel from '@/core/entities/author/AuthorModel.ts';
import type ComicModel from '@/core/entities/comic/ComicModel.ts';
import type TagModel from '@/core/entities/tag/TagModel.ts';

defineEmits<{ (e: 'move-comic', v: number): void }>();

const {
  comic,
  authors = [],
  tags = [],
} = defineProps<{
  comic: ComicModel
  tags?: TagModel[]
  authors?: AuthorModel[]
}>();

const comicAuthors = computed(() => (
  authors
    .filter((e) => comic.authors.includes(e.id))
    .slice(0, 3)
    .map((e) => e.name)
));

const comicTags = computed(() => (
  tags
    .filter((e) => comic.tags.includes(e.id))
    .slice(0, 3)
    .map((e) => e.name)
));
</script>
