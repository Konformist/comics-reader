<template>
  <div>
    <v-label
      v-if="label"
      class="w-100 mb-2"
      :text="label"
    />
    <template
      v-for="(item, index) in items"
      :key="item.id"
    >
      <v-divider v-if="index && !border" />
      <v-btn
        :active="model === item.id"
        :border="border"
        class="w-100 rounded-0"
        :class="{
          'rounded-t-xl': index === 0,
          'rounded-b-xl': index === (items?.length ? items?.length - 1 : 0),
        }"
        :color="model === item.id ? 'primary' : ''"
        flat
        :rounded="false"
        :text="item.name"
        @click="model = item.id"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
const model = defineModel<string>({ default: '' });

defineProps<{
  border?: boolean
  label?: string
  items?: Array<{
    id: string
    name: string
  }>
}>();
</script>
