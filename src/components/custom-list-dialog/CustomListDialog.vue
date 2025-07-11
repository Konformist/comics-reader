<template>
  <v-dialog>
    <template #activator="{ props, isActive }">
      <slot
        :is-active="isActive"
        :props="props"
      />
    </template>
    <template #default="{ isActive }">
      <v-card>
        <template v-if="label">
          <v-card-title class="pa-4">
            {{ label }}
          </v-card-title>
          <v-divider />
        </template>
        <template v-if="items && items.length >= 10">
          <v-card-item class="pa-4">
            <v-text-field
              v-model="search"
              flat
              label="Поиск"
              variant="solo-filled"
              @click:clear="search = ''"
            />
          </v-card-item>
          <v-divider />
        </template>
        <v-list activatable>
          <v-list-item
            v-for="item in filteredItems"
            :key="item.id"
            :active="modelArray.includes(item.id)"
            :title="item.name"
            @click="toggleModel(item.id)"
          >
            <template
              v-if="multiple"
              #prepend="{ isActive: itemActive }"
            >
              <v-checkbox
                color="primary"
                hide-details
                :model-value="itemActive"
                style="margin: -8px 0 -8px -8px"
              />
            </template>
          </v-list-item>
        </v-list>
        <v-divider />
        <v-card-actions>
          <v-btn
            :active="selected"
            text="Выбранные"
            @click="selected = !selected"
          />
          <v-spacer />
          <v-btn
            text="Закрыть"
            @click="isActive.value = false"
          />
        </v-card-actions>
      </v-card>
    </template>
  </v-dialog>
</template>

<script setup lang="ts">
import { sortString } from '@/core/utils/array.ts';

const model = defineModel<number | number[]>({ default: 0 });

const {
  items = [],
  multiple,
  label = '',
} = defineProps<{
  items?: Array<{
    id: number
    name: string
  }>
  multiple?: boolean
  label?: string
}>();

const search = ref('');
const selected = ref(false);

const modelArray = computed<number[]>(() => (
  Array.isArray(model.value) ? model.value : [model.value]
));

const filteredItems = computed(() => (
  items
    .filter((e) => !selected.value || modelArray.value.includes(e.id))
    .filter((e) => e.name.toLowerCase().includes(search.value.toLowerCase()))
    .sort((a, b) => sortString(a.name, b.name))
));

const toggleModel = (id: number) => {
  if (multiple) {
    model.value = modelArray.value.includes(id)
      ? modelArray.value.filter((e) => e !== id)
      : [...modelArray.value, id];
  } else {
    model.value = id;
  }
};
</script>
