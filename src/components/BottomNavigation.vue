<template>
  <v-bottom-navigation
    grow
    tag="footer"
  >
    <v-btn :to="{ name: '/' }">
      <v-icon icon="$home" />
    </v-btn>
    <v-btn :to="{ name: '/tags' }">
      <v-icon icon="$tag" />
    </v-btn>
    <v-btn :to="{ name: '/authors' }">
      <v-icon icon="$author" />
    </v-btn>
    <v-btn :to="{ name: '/languages' }">
      <v-icon icon="$language" />
    </v-btn>
    <v-btn :to="{ name: '/settings' }">
      <v-icon icon="$settings" />
    </v-btn>
  </v-bottom-navigation>
</template>
