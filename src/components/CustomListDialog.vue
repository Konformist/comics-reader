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
        <v-card-title class="pa-4">
          {{ label }}
        </v-card-title>
        <template v-if="items && items.length > 10">
          <v-divider />
          <v-card-item class="pa-4">
            <v-text-field
              v-model="search"
              flat
              label="Поиск"
              variant="solo-filled"
            />
          </v-card-item>
        </template>
        <v-divider />
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
const model = defineModel<number | number[]>({ default: 0 });

const {
  items = [],
  multiple,
} = defineProps<{
  items?: Array<{ id: number; name: string }>
  multiple?: boolean
  label?: string
}>();

const search = ref('');

const filteredItems = computed(() => (
  items.filter((e) => e.name.toLowerCase().includes(search.value.toLowerCase()))
));

const modelArray = computed<number[]>(() => (
  Array.isArray(model.value) ? model.value : [model.value]
));

const toggleModel = (id: number) => {
  if (!multiple) {
    model.value = id;
  } else {
    model.value = modelArray.value.includes(id)
      ? modelArray.value.filter((e) => e !== id)
      : [...modelArray.value, id];
  }
};
</script>
