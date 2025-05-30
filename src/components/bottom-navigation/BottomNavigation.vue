<template>
  <v-bottom-navigation
    class="pa-2 rounded-b-0"
    color="primary"
    elevation="4"
    grow
    height="64"
    rounded="xl"
    tag="footer"
  >
    <v-btn
      icon="$home"
      min-width="0"
      rounded="xl"
      :to="{ name: '/' }"
    />
    <v-btn
      icon="$tag"
      min-width="0"
      rounded="xl"
      :to="{ name: '/tags' }"
    />
    <v-btn
      icon="$author"
      min-width="0"
      rounded="xl"
      :to="{ name: '/authors' }"
    />
    <v-btn
      icon="$language"
      min-width="0"
      rounded="xl"
      :to="{ name: '/languages' }"
    />
    <v-btn
      icon="$settings"
      min-width="0"
      rounded="xl"
      :to="{ name: '/settings' }"
    />
  </v-bottom-navigation>
</template>
