import UI from '@/plugins/UIPlugin.ts';

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
    if (oldValue === value) {
      return;
    }

    if (value) {
      UI.loading({ show: true });
    } else {
      UI.loading({ show: false });
    }
  },
);

const useLoading = () => {
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

export default useLoading;
