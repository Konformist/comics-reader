<template>
  <v-img
    :cover="cover"
    :rounded="rounded"
    :src="link"
  />
</template>

<script lang="ts" setup>
import { getFileUrl } from '@/core/utils/image.ts';

const { src } = defineProps<{
  src?: string
  rounded?: boolean
  cover?: boolean
}>();

const link = ref('');

watch(
  () => src,
  async value => {
    if (value) link.value = await getFileUrl(value);
    else link.value = '';
  },
  { immediate: true }
)
</script>
