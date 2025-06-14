<template>
  <v-btn
    v-bind="{ ...$attrs }"
    color="primary"
    :prepend-icon="prependIcon"
    rounded="md"
    size="small"
    :text="text"
    variant="outlined"
  >
    <template #append>
      <slot name="append" />
    </template>
  </v-btn>
</template>

<script setup lang="ts">
defineProps<{
  prependIcon?: string
  text?: string
}>();
</script>
