<template>
  <v-app-bar title="Комиксы" />
  <v-main>
    <v-container>
      <v-data-iterator
        v-model:page="currentPage"
        :items="comics"
        items-per-page="20"
      >
        <template #default="{ items }">
          <v-row>
            <v-col
              v-for="item in items"
              :key="item.raw.id"
              class="pa-2"
              cols="6"
            >
              <v-card
                height="250"
                :to="{
                  name: '/comics/[id]/',
                  params: { id: item.raw.id }
                }"
              >
                <v-img
                  class="h-100"
                  cover
                  :src="item.raw.image"
                />
                <div class="position-absolute bottom-0 w-100">
                  <div class="position-absolute w-100 h-100 bg-black opacity-40" />
                  <v-card-text class="position-relative">
                    <span class="text-ellipsis">
                      {{ item.raw.name }}
                    </span>
                  </v-card-text>
                </div>
              </v-card>
            </v-col>
          </v-row>
        </template>
        <template #footer="{ pageCount, prevPage, nextPage }">
          <v-pagination
            v-model="currentPage"
            class="mt-4"
            density="comfortable"
            :length="pageCount"
            @next="nextPage()"
            @prev="prevPage()"
          />
        </template>
      </v-data-iterator>
    </v-container>
    <v-fab
      icon="$plus"
      @click="createComic()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import ComicController from '@/core/entities/comic/ComicController.ts';
import ComicModel from '@/core/entities/comic/ComicModel.ts';
import { Toast } from '@capacitor/toast';

const router = useRouter();

const comics = ref<ComicModel[]>([]);

const currentPage = ref(1);

const loadComics = async () => {
  comics.value = await ComicController.loadAll();
}

loadComics();

const createComic = async () => {
  const comicId = await ComicController.save(new ComicModel());

  if (comicId) {
    Toast.show({ text: 'Комикс создан' })
    await router.push({
      name: '/comics/[id]/',
      params: { id: comicId },
    })
  }
}
</script>
