<template>
  <v-main scrollable>
    <v-container class="pa-0">
      <div class="pa-4">
        <h3 class="text-h6">
          Название парсера
        </h3>
        <p class="text-body-1">
          {{ parser.name || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          Ссылка на сайт
        </h3>
        <p class="text-body-1">
          <template v-if="parser.site">
            <a :href="parser.site">
              {{ parser.site }}
            </a>
            <v-icon
              class="ml-2"
              icon="$copy"
              size="20"
              @click="onCopy(parser.site)"
            />
          </template>
          <template v-else>—</template>
        </p>
      </div>
      <v-divider />
      <div class="pa-4">
        <h3 class="text-h6">
          CSS указатель на название
        </h3>
        <p class="text-body-1">
          {{ parser.title || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на изображение
        </h3>
        <p class="text-body-1">
          {{ parser.image || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на авторов
        </h3>
        <p class="text-body-1">
          {{ parser.authors || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на текст авторов
        </h3>
        <p class="text-body-1">
          {{ parser.authorsText || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на текст язык
        </h3>
        <p class="text-body-1">
          {{ parser.language || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на теги
        </h3>
        <p class="text-body-1">
          {{ parser.tags || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на текст тегов
        </h3>
        <p class="text-body-1">
          {{ parser.tagsText || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на страницы
        </h3>
        <p class="text-body-1">
          {{ parser.images || '—' }}
        </p>
      </div>
      <v-divider />
      <div class="pa-4">
        <v-btn
          class="w-100"
          text="Сохранить в Документы"
          @click="toDownloads()"
        />
        <v-btn
          class="mt-4 w-100"
          color="error"
          text="Удалить"
          @click="deleteParser()"
        />
      </div>
    </v-container>
    <v-fab
      icon="$edit"
      :to="{
        name: '/parsers/[id]/edit',
        params: { id: parser.id },
      }"
    />
  </v-main>
</template>

<script lang="ts" setup>
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import { APP_NAME } from '@/core/middleware/variables.ts';
import { Clipboard } from '@capacitor/clipboard';
import { Dialog } from '@capacitor/dialog';
import { Directory, Encoding, Filesystem } from '@capacitor/filesystem';
import { Toast } from '@capacitor/toast';

definePage({
  meta: {
    title: 'Парсер',
    isBack: true,
  },
});

const router = useRouter();
const route = useRoute('/parsers/[id]/');

const parserId = +(route.params?.id);

const parser = ref(new ParserModel());

const loadParser = async () => {
  parser.value = await ParserController.load(parserId);
};

loadParser();

const deleteParser = async () => {
  const { value } = await Dialog.confirm({
    title: 'Подтверждение удаления',
    message: 'Удалить парсер?',
  });

  if (!value) return;

  try {
    await ParserController.delete(parser.value.id);
    Toast.show({ text: 'Парсер удалён' });
    router.replace({ name: '/parsers/' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  }
};

const onCopy = async (string: string) => {
  await Clipboard.write({ string });
  Toast.show({ text: 'Скопировано' });
};

const toDownloads = async () => {
  try {
    const parserDTO = parser.value.getDTO();
    parserDTO.id = 0;

    await Filesystem.writeFile({
      path: `${APP_NAME}/parsers/${parser.value.name}.json`,
      directory: Directory.Documents,
      data: JSON.stringify(parserDTO),
      encoding: Encoding.UTF8,
      recursive: true,
    });
    Toast.show({ text: 'Парсер сохранён в документы' });
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  }
};
</script>
