<template>
  <router-view />
  <v-bottom-navigation>
    <v-btn :to="{ path: '/' }">
      <v-icon>$dashboard</v-icon>
      Галерея
    </v-btn>
    <v-btn :to="{ path: '/parsers/' }">
      <v-icon>$list</v-icon>
      Парсеры
    </v-btn>
  </v-bottom-navigation>
</template>

<script lang="ts" setup>
  //
</script>
