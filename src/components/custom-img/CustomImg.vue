<template>
  <v-skeleton-loader
    v-if="loading"
    class="rounded-xl"
    :height="height"
    :width="width"
  />
  <v-img
    v-else
    :cover="cover"
    :height="height"
    rounded="xl"
    :src="src"
    :width="width"
  >
    <template #placeholder>
      <div
        class="w-100 h-100 d-flex align-center justify-center
          border-md rounded-xl
          text-body-2 text-grey-darken-2"
      >
        Нет изображения
      </div>
    </template>
    <template #error>
      <div
        class="w-100 h-100 d-flex align-center justify-center
          border-md rounded-xl
          text-body-2 text-grey-darken-2"
      >
        Нет изображения
      </div>
    </template>
  </v-img>
</template>

<script setup lang="ts">
defineProps<{
  loading?: boolean
  cover?: boolean
  height?: string | number
  width?: string | number
  src?: string
}>();
</script>
