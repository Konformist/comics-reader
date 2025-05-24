<template>
  <v-list v-if="loading">
    <v-skeleton-loader
      v-for="i in 10"
      :key="i"
      type="list-item"
    />
  </v-list>
  <v-list
    v-else-if="items.length"
    v-model:selected="selected"
    class="pa-0"
    density="comfortable"
    item-title="name"
    item-value="id"
    :items="items"
    select-strategy="single-independent"
    selectable
    slim
    @click:select="$emit('click-item', $event.id)"
  >
    <template #append="{ item }">
      <v-list-item-subtitle
        v-if="'count' in item"
        class="mr-4"
      >
        {{ item.count }}
      </v-list-item-subtitle>
      <v-icon icon="$edit" />
    </template>
  </v-list>
</template>

<script setup lang="ts">
defineEmits<{
  (e: 'click-item', v: number): void
}>();
defineProps<{
  loading?: boolean
  items: Array<{
    id: number
    count?: number
    name: string
  }>
}>();

const model = ref(0);
const selected = computed({
  get() {
    return [model.value];
  },
  set(value) {
    if (!value.length) return;
    model.value = value[0];
  },
});
</script>
