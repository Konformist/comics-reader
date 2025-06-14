<template>
  <v-bottom-sheet v-model="opened">
    <v-card
      class="rounded-b-0"
      title="Главы"
    >
      <v-list>
        <v-list-item
          v-for="(item, i) in items"
          :key="item.id"
          :active="active === item.id"
          :title="item.name || `Глава ${i + 1}`"
          @click="$emit('move-chapter', item.id)"
        />
      </v-list>
    </v-card>
  </v-bottom-sheet>
</template>
<script setup lang="ts">
import type ChapterModel from '@/core/entities/chapter/ChapterModel.ts';

const opened = defineModel<boolean>('opened', { default: false });

defineEmits<{ (e: 'move-chapter', v: number): void }>();
defineProps<{
  items: ChapterModel[]
  active: number
  comicId: number
}>();
</script>
