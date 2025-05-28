<template>
  <v-img
    :cover="cover"
    :height="height"
    rounded="xl"
    :src="src"
    :width="width"
  >
    <template
      v-if="!src"
      #placeholder
    >
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
  cover?: boolean
  height?: string | number
  width?: string | number
  src?: string
}>();
</script>
