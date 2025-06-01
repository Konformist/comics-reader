import type ComicOverrideModel from '@/core/entities/comic-override/ComicOverrideModel.ts';
import type ParserModel from '@/core/entities/parser/ParserModel.ts';
import type { IParsedComic } from '@/core/entities/parser/ParserTypes.ts';

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

export const parseComic = (
  value: string,
  parser: ParserModel,
  override: ComicOverrideModel,
): IParsedComic => {
  const cleaned = cleanHTML(value);
  const domParser = new DOMParser();
  const result = domParser
    .parseFromString(cleaned, 'text/html')
    .body;

  const parsedComic: IParsedComic = {};

  const titleCSS = parser.titleCSS || override.titleCSS;
  const name = parseString(result, titleCSS);
  if (name) parsedComic.name = name;

  const coverCSS = parser.coverCSS || override.coverCSS;
  const cover = parseImage(result, coverCSS);
  if (cover) parsedComic.cover = cover;

  const pagesCSS = parser.pagesCSS || override.pagesCSS;
  const pages = parseArray(result, pagesCSS);
  if (pages) parsedComic.pages = pages.length;

  const authorsCSS = parser.authorsCSS || override.authorsCSS;
  const authorsTextCSS = parser.authorsTextCSS || override.authorsTextCSS;
  const authors = parseArray(result, authorsCSS, authorsTextCSS);
  if (authors) parsedComic.authors = authors;

  const languageCSS = parser.languageCSS || override.languageCSS;
  const language = parseString(result, languageCSS);
  if (language) parsedComic.language = language;

  const tagsCSS = parser.tagsCSS || override.tagsCSS;
  const tagsTextCSS = parser.tagsTextCSS || override.tagsTextCSS;
  const tags = parseArray(result, tagsCSS, tagsTextCSS);
  if (tags) parsedComic.tags = tags;

  return parsedComic;
};
