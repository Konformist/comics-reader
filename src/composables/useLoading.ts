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
    loading,
    loadingStart,
    loadingEnd,
  };
};
