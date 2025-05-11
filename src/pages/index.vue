<template>
  <v-app-bar title="Комиксы" />
  <v-main>
    <v-container>
      <v-row>
        <v-col
          v-for="item in comics"
          :key="item.id"
          cols="12"
        >
          <v-card
            height="200"
            :to="{
              name: '/comics/[id]/',
              params: { id: item.id }
            }"
          >
            <ComicImage
              class="h-100"
              cover
              :src="item.image"
            />
            <div class="position-absolute bottom-0 w-100">
              <div class="position-absolute w-100 h-100 bg-black opacity-40" />
              <v-card-title class="position-relative">
                {{ item.name }}
              </v-card-title>
              <div class="px-4 pb-4 ga-2 d-flex flex-wrap">
                <v-chip
                  v-for="tag in item.tags.slice(0, 5)"
                  :key="tag"
                  density="compact"
                  :text="tag"
                />
              </div>
            </div>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-main>
  <v-fab
    app
    class="mb-4"
    icon="$plus"
    :to="{ name: '/comics/create' }"
  />
</template>

<script lang="ts" setup>
import ComicController from '@/core/entities/comic/ComicController.ts';
import type ComicModel from '@/core/entities/comic/ComicModel.ts';

const comics = ref<ComicModel[]>([]);

const loadComics = async () => {
  comics.value = await ComicController.loadAll();
}

loadComics();
</script>
