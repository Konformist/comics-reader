import { KeepAwake } from '@capacitor-community/keep-awake';

const countGlobal = ref(0);
const loadingGlobal = computed(() => !!countGlobal.value);

const loadingGlobalStart = () => {
  countGlobal.value++;
};

const loadingGlobalEnd = () => {
  countGlobal.value -= countGlobal.value > 0 ? 1 : 0;
};

watch(
  loadingGlobal,
  async (value, oldValue) => {
    if (oldValue === value || !(await KeepAwake.isSupported()).isSupported) return;

    if (value) await KeepAwake.keepAwake();
    else await KeepAwake.allowSleep();
  },
);

export default () => {
  const count = ref(0);
  const loading = computed(() => !!count.value);

  const loadingStart = () => {
    count.value++;
  };

  const loadingEnd = () => {
    count.value -= count.value > 0 ? 1 : 0;
  };

  return {
    loadingGlobal,
    loadingGlobalStart,
    loadingGlobalEnd,
    loading,
    loadingStart,
    loadingEnd,
  };
};
