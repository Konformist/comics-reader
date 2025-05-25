<template>
  <v-app-bar :title="($route.meta?.title as string) || ''">
    <template #prepend>
      <v-btn
        v-if="$route.meta?.isBack"
        icon="$arrow-left"
        @click="$router.back()"
      />
      <v-btn
        v-else
        :active="drawer"
        icon="$menu"
        @click="drawer = !drawer"
      />
    </template>
    <template #append>
      <v-progress-linear
        v-if="loadingGlobal"
        absolute
        indeterminate
        style="bottom: 0; top: auto"
      />
    </template>
  </v-app-bar>
  <NavigationDriwer
    v-model="drawer"
  />
  <router-view v-slot="{ Component, route }">
    <v-fade-transition leave-absolute>
      <component
        :is="Component"
        :key="route"
      />
    </v-fade-transition>
  </router-view>
  <BottomNavigation
    v-if="$route.meta?.isBottomNavigation"
  />
</template>

<script lang="ts" setup>
import BottomNavigation from '@/components/BottomNavigation.vue';
import NavigationDriwer from '@/components/NavigationDriwer.vue';
import useLoading from '@/composables/useLoading.ts';

const drawer = ref(false);

const { loadingGlobal } = useLoading();
</script>
