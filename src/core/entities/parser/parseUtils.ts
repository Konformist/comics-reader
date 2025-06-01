export const cleanHTML = (value: string): string => (
  value
    .replaceAll(/<head>(?:.|\n|\t)*?<\/head>/gm, '')
    .replaceAll(/<style.*?>(?:.|\n|\t)*?<\/style>/gm, '')
    .replaceAll(/<script.*?>(?:.|\n|\t)*?<\/script>/gm, '')
);

const cleanStr = (str: string): string => (
  str.trim().replaceAll(/\s+/g, ' ')
);

export const parseString = (data: HTMLElement, item?: string) => (
  item
    ? cleanStr(data.querySelector(item)?.textContent || '')
    : ''
);

export const parseArray = (data: HTMLElement, item?: string, itemText?: string): string[] => {
  if (!item) return [];

  return [...data.querySelectorAll<HTMLElement>(item)].map((e) => (
    itemText
      ? parseString(e, itemText)
      : cleanStr(e.textContent || '')
  ));
};

export const parseImage = (data: HTMLElement, item: string) => (
  item
    ? data.querySelector<HTMLImageElement>(item)?.src || ''
    : ''
);
