<template>
  <v-main>
    <v-container class="pa-0 pb-16 mb-4">
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
          <template v-if="parser.siteUrl">
            <a :href="parser.siteUrl">
              {{ parser.siteUrl }}
            </a>
            <v-icon
              class="ml-2"
              icon="$copy"
              size="20"
              @click="onCopy(parser.siteUrl)"
            />
          </template>
          <template v-else>
            —
          </template>
        </p>
      </div>
      <v-divider />
      <div class="pa-4">
        <h3 class="text-h6">
          CSS указатель на название
        </h3>
        <p class="text-body-1">
          {{ parser.titleCSS || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на аннотацию
        </h3>
        <p class="text-body-1">
          {{ parser.annotationCSS || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на изображение
        </h3>
        <p class="text-body-1">
          {{ parser.coverCSS || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на авторов
        </h3>
        <p class="text-body-1">
          {{ parser.authorsCSS || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на текст авторов
        </h3>
        <p class="text-body-1">
          {{ parser.authorsTextCSS || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на язык
        </h3>
        <p class="text-body-1">
          {{ parser.languageCSS || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на теги
        </h3>
        <p class="text-body-1">
          {{ parser.tagsCSS || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на текст тегов
        </h3>
        <p class="text-body-1">
          {{ parser.tagsTextCSS || '—' }}
        </p>
      </div>
      <v-divider />
      <div class="pa-4">
        <h3 class="text-h6">
          CSS указатель на главы
        </h3>
        <p class="text-body-1">
          {{ parser.chaptersCSS || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на название главы
        </h3>
        <p class="text-body-1">
          {{ parser.chaptersTitleCSS || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          Шаблон ссылок для страниц
        </h3>
        <p class="text-body-1">
          {{ parser.pagesTemplateUrl || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на страницы
        </h3>
        <p class="text-body-1">
          {{ parser.pagesCSS || '—' }}
        </p>
        <h3 class="mt-4 text-h6">
          CSS указатель на изображение страницы
        </h3>
        <p class="text-body-1">
          {{ parser.pagesImageCSS || '—' }}
        </p>
      </div>
    </v-container>
    <v-fab
      app
      appear
      :disabled="loadingGlobal"
      icon="$edit"
      :to="{
        name: '/parsers/[id]/edit',
        params: { id: parser.id },
      }"
    />
  </v-main>
</template>

<script lang="ts" setup>
import useLoading from '@/composables/useLoading.ts';
import { Clipboard } from '@capacitor/clipboard';
import { Toast } from '@capacitor/toast';
import ParserController from '@/core/entities/parser/ParserController.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';

definePage({
  meta: {
    layout: 'entity',
    title: 'Парсер',
  },
});

const router = useRouter();
const route = useRoute('/parsers/[id]/');
const { loadingGlobal } = useLoading();

const parserId = +(route.params?.id);

const parser = ref(new ParserModel());

const loadParser = async () => {
  parser.value = await ParserController.load(parserId);
};

onMounted(async () => {
  await loadParser();
  if (!parser.value.id) router.replace({ name: '/parsers/' });
});

const onCopy = async (string: string) => {
  await Clipboard.write({ string });
  Toast.show({ text: 'Скопировано' });
};
</script>
