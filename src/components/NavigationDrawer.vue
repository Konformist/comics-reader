<template>
  <v-dialog
    v-model="model"
    :content-props="{
      style: 'margin: 0 64px 0 0',
    }"
    height="100vh"
    max-height="100vh"
    max-width="calc(100vw - 64px)"
    transition="slide-x-transition"
    width="calc(100vw - 64px)"
  >
    <v-card class="rounded-s-0">
      <v-card-title class="px-6 py-4">
        {{ APP_NAME }}
      </v-card-title>
      <v-divider />
      <v-list
        slim
        style="padding: 0 !important;"
      >
        <v-list-item
          class="ma-2"
          prepend-icon="$home"
          title="Галерея"
          :to="{ name: '/' }"
          @click="model = false"
        />
        <v-divider />
        <v-list-subheader
          class="mx-4 my-2"
          title="Справочники"
        />
        <v-list-item
          class="ma-2"
          prepend-icon="$author"
          title="Авторы"
          :to="{ name: '/authors' }"
          @click="model = false"
        />
        <v-list-item
          class="ma-2"
          prepend-icon="$language"
          title="Языки"
          :to="{ name: '/languages' }"
          @click="model = false"
        />
        <v-list-item
          class="ma-2"
          prepend-icon="$tag"
          title="Теги"
          :to="{ name: '/tags' }"
          @click="model = false"
        />
        <v-list-item
          class="ma-2"
          prepend-icon="$list"
          title="Парсеры"
          :to="{ name: '/parsers/' }"
          @click="model = false"
        />
        <v-divider />
        <v-list-subheader
          class="mx-4 my-2"
          title="Прочее"
        />
        <v-list-item
          class="ma-2"
          prepend-icon="$settings"
          title="Настройки"
          :to="{ name: '/settings' }"
          @click="model = false"
        />
        <v-list-item
          class="ma-2"
          prepend-icon="$info"
          title="О приложении"
          :to="{ name: '/about' }"
          @click="model = false"
        />
        <v-divider />
        <v-list-subheader
          class="mx-4 my-2"
          title="Dev"
        />
        <v-list-item
          class="ma-2"
          prepend-icon="$dev"
          title="Дебаггинг"
          :to="{ name: '/debug' }"
          @click="model = false"
        />
      </v-list>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { APP_NAME } from '../core/utils/variables.ts';

const model = defineModel<boolean>({ default: false });
</script>
