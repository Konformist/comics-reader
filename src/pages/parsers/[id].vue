<template>
  <v-main scrollable>
    <v-container>
      <v-text-field
        v-model.trim="parser.name"
        label="Название парсера"
      />
      <v-text-field
        v-model.trim="parser.site"
        label="Ссылка на сайт"
      />
      <v-textarea
        v-model.trim="parser.title"
        :autocapitalize="false"
        :autocomplete="false"
        label="Название комикса"
        rows="2"
      />
      <v-textarea
        v-model.trim="parser.image"
        :autocapitalize="false"
        :autocomplete="false"
        label="Изображение"
        rows="2"
      />
      <v-textarea
        v-model.trim="parser.authors"
        :autocapitalize="false"
        :autocomplete="false"
        label="Авторы"
        rows="2"
      />
      <v-text-field
        v-model.trim="parser.authorsText"
        :autocapitalize="false"
        :autocomplete="false"
        label="Текст авторов"
      />
      <v-textarea
        v-model.trim="parser.language"
        :autocapitalize="false"
        :autocomplete="false"
        label="Язык"
        rows="2"
      />
      <v-textarea
        v-model.trim="parser.tags"
        :autocapitalize="false"
        :autocomplete="false"
        label="Теги"
        rows="2"
      />
      <v-text-field
        v-model.trim="parser.tagsText"
        :autocapitalize="false"
        :autocomplete="false"
        label="Текст тега"
      />
      <v-textarea
        v-model.trim="parser.images"
        :autocapitalize="false"
        :autocomplete="false"
        label="Страницы"
        rows="2"
      />
      <v-btn
        class="w-100"
        color="error"
        text="Удалить"
        @click="deleteParser()"
      />
    </v-container>
    <v-fab
      icon="$save"
      @click="saveParser()"
    />
  </v-main>
</template>

<script lang="ts" setup>
import { Dialog } from '@capacitor/dialog';
import { Toast } from '@capacitor/toast';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';

definePage({
  meta: {
    title: 'Редактирование парсера',
    isBack: true,
  },
});

const router = useRouter();
const route = useRoute('/parsers/[id]');

const parserId = +(route.params?.id);

const parser = ref(new ParserModel());

const loadParser = async () => {
  parser.value = await ParserController.load(parserId);
};

loadParser();

const saveParser = async () => {
  await ParserController.save(parser.value);
  Toast.show({ text: 'Парсер сохранён' });
};

const deleteParser = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить парсер?',
  });

  if (!value) return;

  await ParserController.delete(parser.value.id);
  Toast.show({ text: 'Парсер удалён' });
  router.replace({ name: '/parsers/' });
};
</script>
