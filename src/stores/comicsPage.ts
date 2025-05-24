export const useComicsPageStore = defineStore('comicsPage', {
  state: () => ({
    filters: {
      page: 1 as number,
      authors: [] as number[],
      languages: [] as number[],
      tags: [] as number[],
      filling: 0 as 0 | 1 | 2,
    },
  }),
});
