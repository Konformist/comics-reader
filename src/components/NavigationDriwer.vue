<template>
  <v-navigation-drawer v-model="model">
    <v-list nav>
      <v-list-item
        prepend-icon="$home"
        title="Галерея"
        :to="{ name: '/' }"
      />
      <v-list-subheader
        title="Справочники"
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
        prepend-icon="$list"
        title="Парсеры"
        :to="{ name: '/parsers/' }"
      />
      <v-list-subheader
        title="Прочее"
      />
      <v-list-item
        prepend-icon="$settings"
        title="Настройки"
        :to="{ name: '/settings' }"
      />
      <v-list-item
        prepend-icon="$info"
        title="О приложении"
        :to="{ name: '/about' }"
      />
      <v-list-subheader
        title="Dev"
      />
      <v-list-item
        prepend-icon="$dev"
        title="Дебаггинг"
        :to="{ name: '/debug' }"
      />
    </v-list>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
const model = defineModel<boolean>({ default: false });
</script>
