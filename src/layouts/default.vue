<template>
  <router-view v-slot="{ Component, route }">
    <keep-alive include="/">
      <component
        :is="Component"
        :key="route"
      />
    </keep-alive>
  </router-view>
  <BottomNavigation />
</template>

<script lang="ts" setup>
import BottomNavigation from '@/components/BottomNavigation.vue';
</script>
