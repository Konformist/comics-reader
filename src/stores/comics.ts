export const useComicsStore = defineStore('comics', {
  state: () => ({
    filters: {
      authors: [] as string[],
      languages: [] as string[],
      tags: [] as string[],
      filling: 0 as 0|1|2,
    },
  }),
});
