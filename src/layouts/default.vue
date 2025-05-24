<template>
  <v-app-bar density="comfortable">
    <v-btn
      v-if="$route.meta?.isBack"
      icon="$arrow-left"
      slim
      @click="$router.back()"
    />
    <v-btn
      v-else
      :active="drawer"
      icon="$menu"
      slim
      @click="drawer = !drawer"
    />
    <v-app-bar-title :text="($route.meta?.title as string) || ''" />
  </v-app-bar>
  <NavigationDriwer
    v-model="drawer"
  />
  <v-progress-linear
    v-if="loadingGlobal"
    absolute
    indeterminate
    style="top: 56px;"
  />
  <router-view v-slot="{ Component, route }">
    <v-slide-x-transition leave-absolute>
      <component
        :is="Component"
        :key="route"
      />
    </v-slide-x-transition>
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
