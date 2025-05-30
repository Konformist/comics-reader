<template>
  <CustomListDialog
    v-model="model"
    :items="items"
    :label="label"
    :multiple="multiple"
  >
    <template #default="{ props, isActive }">
      <v-text-field
        v-bind="{ ...props, ...$attrs }"
        :active="chips.length > 0"
        :label="label"
        readonly
      >
        <v-chip
          v-for="item in chipsShow"
          :key="item.id"
          append-icon="$delete"
          class="mr-1"
          size="small"
          :text="item.name"
          @click.stop="onDelete(item.id)"
        />
        <span
          v-if="chipsOther.length"
          class="text-grey text-caption align-self-center"
        >
          +{{ chipsOther.length }}
        </span>
        <template #append-inner>
          <v-icon
            class="mr-2 CustomSelect--icon"
            :class="isActive ? 'CustomSelect--rotate' : ''"
            icon="$triangleDown"
            size="10"
          />
        </template>
      </v-text-field>
    </template>
  </CustomListDialog>
</template>

<script lang="ts" setup>
const SHOW_COUNT = 2;

const model = defineModel<number | number[]>({ default: 0 });

const {
  items = [],
  multiple, label = '',
} = defineProps<{
  items?: Array<{
    id: number
    name: string
  }>
  multiple?: boolean
  label?: string
}>();

const modelArray = computed<number[]>(() => (
  Array.isArray(model.value)
    ? model.value
    : [model.value]
));

const onDelete = (id: number) => {
  if (multiple) model.value = modelArray.value.filter((item) => item !== id);
  else model.value = 0;
};

const chips = computed(() => (
  items.filter((e) => modelArray.value.includes(e.id))
));
const chipsShow = computed(() => chips.value.slice(0, SHOW_COUNT));
const chipsOther = computed(() => chips.value.slice(SHOW_COUNT));
</script>

<style lang="scss" scoped>
.CustomSelect {
  &--icon {
    transition: 0.3s transform;
    transform: rotate(0);
  }

  &--rotate {
    transform: rotate(180deg);
  }
}
</style>
