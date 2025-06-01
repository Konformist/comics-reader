<template>
  <v-app-bar
    class="rounded-b-xl"
    title="Чтение"
  >
    <template #prepend>
      <v-btn
        icon="$arrow-left"
        rounded="pill"
        @click="$router.back()"
      />
    </template>
    <template #append>
      <v-btn
        icon="$tune"
        rounded="pill"
        @click="openSettings()"
      />
    </template>
  </v-app-bar>
  <v-main
    v-if="mounted"
    scrollable
  >
    <v-container
      v-if="appStore.settings.readingMode === 'webtoon'"
      class="pa-0"
      min-height="100%"
    >
      <ChapterPageRead
        v-for="item in chapter.pages"
        :key="item.id"
        :item="item"
        max-width="100%"
        @read="onRead(item)"
      />
    </v-container>
    <v-container
      v-else
      class="pa-0"
      height="100%"
    >
      <swiper-container
        ref="swiperRef"
        :autoplay="{
          enabled: appStore.settings.autoReading,
          delay: appStore.settings.autoReadingTimeout,
          stopOnLastSlide: true,
        }"
        class="h-100"
        :direction="appStore.settings.readingMode"
        lazy
        :modules="[Autoplay]"
      >
        <swiper-slide
          v-for="item in chapter.pages"
          :key="item.id"
          class="d-flex align-center justify-center"
        >
          <ChapterPageRead
            :item="item"
            max-height="100%"
            max-width="100%"
            @click="onOpenPages()"
            @read="onRead(item)"
          />
        </swiper-slide>
      </swiper-container>
    </v-container>
  </v-main>
  <v-bottom-sheet
    :model-value="showPages"
    @update:model-value="onClosePages()"
  >
    <v-card class="py-2 rounded-b-0">
      <swiper-container
        centered-slides
        class="w-100"
        :initial-slide="currentPage"
        lazy
        :slides-offset-after="2"
        :slides-offset-before="2"
        slides-per-view="auto"
      >
        <swiper-slide
          v-for="(item, index) in chapter.pages"
          :key="item.id"
          class="px-1 d-flex align-center justify-center"
          style="height: 128px; width: 100px"
        >
          <CustomImg
            cover
            height="100%"
            :src="item.file?.url"
            width="100%"
            @click="onMovePage(index)"
          />
        </swiper-slide>
      </swiper-container>
    </v-card>
  </v-bottom-sheet>
  <v-bottom-sheet
    :model-value="showSettings"
    @update:model-value="closeSettings()"
  >
    <v-card class="rounded-b-0">
      <v-card-item>
        <CustomBtnGroup
          v-model="appStore.settings.readingMode"
          border
          :items="settingsDirectionItems"
          label="Направление прокрутки"
        />
        <v-label
          class="mt-4 w-100"
          text="Авто перелистывание"
        >
          <v-switch
            v-model="appStore.settings.autoReading"
            class="ml-auto"
            :disabled="appStore.settings.readingMode === 'webtoon'"
            :false-value="false"
            :true-value="true"
          />
        </v-label>
        <v-number-input
          v-model.number="appStore.settings.readingTimeoutSec"
          class="mt-4"
          :disabled="!appStore.settings.autoReading || appStore.settings.readingMode === 'webtoon'"
          flat
          label="До перелистывания"
          :min="0"
          suffix="с"
          variant="solo-filled"
        />
      </v-card-item>
      <template #actions>
        <v-card-actions>
          <v-btn
            text="Сохранить"
            @click="saveSettings()"
          />
        </v-card-actions>
      </template>
    </v-card>
  </v-bottom-sheet>
  <v-progress-linear
    v-if="appStore.settings.readingMode !== 'webtoon'"
    class="position-fixed"
    location="bottom"
    :max="chapter.pages.length - 1"
    :model-value="currentPage"
  />
</template>

<script lang="ts" setup>
import ChapterPageController from '@/core/entities/chapter-page/ChapterPageController.ts';
import type ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';
import { settingsDirectionItems } from '@/core/entities/settings/settingsUtils.ts';
import { KeepAwake } from '@capacitor-community/keep-awake';
import { Capacitor } from '@capacitor/core';
import { StatusBar } from '@capacitor/status-bar';
import { Toast } from '@capacitor/toast';
import { Autoplay } from 'swiper/modules';
import { register } from 'swiper/element';
import type { SwiperContainer, SwiperSlide } from 'swiper/element';
import { useAppStore } from '@/stores/app.ts';
import ChapterController from '@/core/entities/chapter/ChapterController.ts';
import ChapterModel from '@/core/entities/chapter/ChapterModel.ts';
import ChapterPageRead from '@/components/ChapterPageRead.vue';

definePage({
  meta: {
    layout: 'empty',
    title: 'Чтение',
  },
});

const route = useRoute('/chapters/[id]/read');
const router = useRouter();
const appStore = useAppStore();

const swiperRef = ref<SwiperContainer>();

const showPages = ref(false);
const currentPage = ref(0);

const swiperStop = () => {
  if (swiperRef.value) swiperRef.value.swiper.autoplay.stop();
};

const swiperStart = () => {
  if (!swiperRef.value || !appStore.settings.autoReading) return;
  swiperRef.value.swiper.autoplay.start();
};

const onOpenPages = () => {
  swiperStop();
  showPages.value = true;
};

const onClosePages = () => {
  swiperStart();
  showPages.value = false;
};

const onMovePage = (index: number) => {
  if (swiperRef.value) swiperRef.value.swiper.slideTo(index);
};

const showSettings = ref(false);

const openSettings = () => {
  swiperStop();
  showSettings.value = true;
};

const closeSettings = () => {
  swiperStart();
  showSettings.value = false;
};

const saveSettings = async () => {
  await appStore.saveSettings();
  showSettings.value = false;
  swiperStart();
};

register();

const chapterId = +(route.params.id || 0);

const chapter = ref<ChapterModel>(new ChapterModel());
const loadChapter = async () => {
  chapter.value = await ChapterController.load(chapterId);
};

const mounted = ref(false);

onMounted(async () => {
  await loadChapter();

  if (!chapter.value.id) {
    router.replace({ name: '/' });
    return;
  }

  if ((await KeepAwake.isSupported()).isSupported) {
    await KeepAwake.keepAwake();
  }

  if (Capacitor.isNativePlatform()) {
    await StatusBar.hide();
  }

  mounted.value = true;

  nextTick(() => {
    if (!swiperRef.value) return;

    swiperRef.value.swiper.on('activeIndexChange', (swiper) => {
      currentPage.value = swiper.activeIndex;
    });
  });
});

onBeforeUnmount(async () => {
  if ((await KeepAwake.isSupported()).isSupported) {
    await KeepAwake.allowSleep();
  }

  if (Capacitor.isNativePlatform()) {
    await StatusBar.show();
  }
});

const onRead = async (item: ChapterPageModel) => {
  if (item.isRead) return;

  try {
    item.isRead = true;
    await ChapterPageController.save(item);
  } catch (e) {
    Toast.show({ text: `Ошибка: ${e}` });
  }
};
</script>
