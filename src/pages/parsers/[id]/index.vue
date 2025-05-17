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
import { Clipboard } from '@capacitor/clipboard';
import { Toast } from '@capacitor/toast';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';

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
  if (!parser.value.id) router.replace({ name: '/parsers/' });
};

loadParser();

const onCopy = async (string: string) => {
  await Clipboard.write({ string });
  Toast.show({ text: 'Скопировано' });
};
</script>
