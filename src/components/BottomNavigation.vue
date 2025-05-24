<template>
  <v-bottom-navigation
    grow
    tag="footer"
  >
    <v-btn :to="{ name: '/' }">
      <v-icon icon="$home" />
      Галерея
    </v-btn>
    <v-btn :to="{ name: '/tags' }">
      <v-icon icon="$tag" />
      Теги
    </v-btn>
    <v-btn :to="{ name: '/authors' }">
      <v-icon icon="$author" />
      Авторы
    </v-btn>
    <v-btn :to="{ name: '/languages' }">
      <v-icon icon="$language" />
      Языки
    </v-btn>
    <v-btn :to="{ name: '/settings' }">
      <v-icon icon="$settings" />
      Настройки
    </v-btn>
  </v-bottom-navigation>
</template>
<script setup lang="ts">
</script>
