<template>
  <v-dialog v-model="opened">
    <v-card>
      <v-card-title class="pa-4">
        {{ isCreated ? 'Создание' : 'Редактирование' }}
      </v-card-title>
      <v-divider />
      <v-card-item class="pa-4">
        <v-text-field
          v-model.trim="model"
          flat
          variant="solo-filled"
        />
      </v-card-item>
      <v-divider />
      <v-card-actions>
        <v-btn
          v-if="!isCreated"
          color="error"
          :disabled="disabled"
          text="Удалить"
          @click="$emit('remove')"
        />
        <v-spacer />
        <v-btn
          :disabled="disabled"
          text="Сохранить"
          @click="$emit('save')"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
const opened = defineModel<boolean>('opened', { default: false });
const model = defineModel<string>({ default: '' });

defineEmits<{
  (e: 'save', v: void): void
  (e: 'remove', v: void): void
}>();
defineProps<{
  isCreated?: boolean
  disabled?: boolean
}>();
</script>
