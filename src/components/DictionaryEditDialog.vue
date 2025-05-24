<template>
  <v-dialog v-model="opened">
    <v-card>
      <v-card-title>
        {{ isCreated ? 'Создание' : 'Редактирование' }}
      </v-card-title>
      <v-card-item class="pb-0">
        <v-text-field
          v-model.trim="model"
          variant="solo-filled"
        />
      </v-card-item>
      <v-card-actions>
        <v-btn
          v-if="!isCreated"
          class="mr-auto"
          color="error"
          density="comfortable"
          :disabled="disabled"
          text="Удалить"
          @click="$emit('remove')"
        />
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
