<template>
  <v-bottom-navigation
    grow
    tag="footer"
  >
    <v-btn :to="{ name: '/' }">
      <v-icon icon="$home" />
      Галерея
    </v-btn>
    <v-btn :to="{ name: '/filemanager' }">
      <v-icon icon="$files" />
      Файлы
    </v-btn>
    <v-btn :to="{ name: '/settings' }">
      <v-icon icon="$settings" />
      Настройки
    </v-btn>
  </v-bottom-navigation>
</template>
