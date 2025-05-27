<template>
  <CustomListDialog
    v-model="model"
    :items="items"
    :label="text"
    :multiple="multiple"
  >
    <template #default="{ isActive, props }">
      <v-btn
        v-bind="{ ...props, ...$attrs }"
        color="primary"
        :prepend-icon="prependIcon"
        rounded="md"
        size="small"
        :text="btnText"
        variant="outlined"
      >
        <template #append>
          <v-icon
            class="DropdownButton--icon"
            :class="isActive ? 'DropdownButton--rotate' : ''"
            icon="$triangleDown"
            size="10"
          />
        </template>
      </v-btn>
    </template>
  </CustomListDialog>
</template>

<script lang="ts" setup>
const model = defineModel<number | number[]>({ default: 0 });

const {
  text,
  items = [],
  multiple,
} = defineProps<{
  prependIcon?: string
  items?: Array<{ id: number; name: string }>
  text?: string
  multiple?: boolean
}>();

const btnText = computed(() => {
  const item = items.filter((item) => (
    Array.isArray(model.value)
      ? model.value.includes(item.id)
      : model.value === item.id
  ));

  if (item.length > 1) {
    return `${item[0].name} +${item.slice(1).length}`;
  }

  return item[0]?.name || text;
});
</script>

<style lang="scss" scoped>
.DropdownButton {
  &--icon {
    transition: 0.3s transform;
    transform: rotate(0);
  }

  &--rotate {
    transform: rotate(180deg);
  }
}
</style>
