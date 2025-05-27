<template>
  <v-app-bar
    class="rounded-b-xl"
    flat
    :title="title"
  >
    <template #prepend>
      <v-btn
        icon="$arrow-left"
        rounded="pill"
        @click="$router.back()"
      />
    </template>
  </v-app-bar>
  <router-view />
</template>
<script lang="ts" setup>
const route = useRoute();

const title = computed(() => (route.meta?.title as string));
</script>
