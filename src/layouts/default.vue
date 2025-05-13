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
    <v-app-bar-title :text="$route.meta?.title" />
  </v-app-bar>
  <v-navigation-drawer v-model="drawer">
    <v-list nav>
      <v-list-item
        prepend-icon="$home"
        title="Галерея"
        :to="{ name: '/' }"
      />
      <v-list-item
        prepend-icon="$list"
        title="Парсеры"
        :to="{ name: '/parsers/' }"
      />
      <v-list-item
        prepend-icon="$author"
        title="Авторы"
        :to="{ name: '/authors' }"
      />
      <v-list-item
        prepend-icon="$language"
        title="Языки"
        :to="{ name: '/languages' }"
      />
      <v-list-item
        prepend-icon="$tag"
        title="Теги"
        :to="{ name: '/tags' }"
      />
      <v-list-item
        prepend-icon="$files"
        title="Файлы"
        :to="{ name: '/filemanager' }"
      />
      <v-list-item
        prepend-icon="$settings"
        title="Настройки"
        :to="{ name: '/settings' }"
      />
    </v-list>
  </v-navigation-drawer>
  <router-view v-slot="{ Component, route }">
    <v-slide-x-transition leave-absolute>
      <keep-alive include="/">
        <component
          :is="Component"
          :key="route"
        />
      </keep-alive>
    </v-slide-x-transition>
  </router-view>
  <BottomNavigation />
</template>

<script lang="ts" setup>
import BottomNavigation from '@/components/BottomNavigation.vue';

const drawer = ref(false)
</script>
