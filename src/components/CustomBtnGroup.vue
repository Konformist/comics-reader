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
      <v-divider v-if="index" />
      <v-btn
        :active="model === item.id"
        class="w-100 rounded-0"
        :class="{
          'rounded-t-xl': index === 0,
          'rounded-b-xl': index === (items.length - 1),
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

const { items = [], label = '' } = defineProps<{
  label?: string
  items?: Array<{
    id: string
    name: string
  }>
}>();
</script>
