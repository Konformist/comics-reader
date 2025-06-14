import { faker } from '@faker-js/faker';
import express from 'express';

const LANGUAGE_COUNT = 10;
const TAG_COUNT = 50;
const AUTHOR_COUNT = 10;
const PARSER_COUNT = 10;
const COMIC_COUNT = 50;
const CHAPTER_COUNT = 10 * COMIC_COUNT;
const CHAPTER_PAGE_COUNT = 10 * CHAPTER_COUNT;

const uniqArrInt = (min, max) => {
  return () => faker.number.int({
    min,
    max,
  });
};

const languageIds = faker.helpers.uniqueArray(uniqArrInt(1, LANGUAGE_COUNT), LANGUAGE_COUNT);
const tagIds = faker.helpers.uniqueArray(uniqArrInt(1, TAG_COUNT), TAG_COUNT);
const authorIds = faker.helpers.uniqueArray(uniqArrInt(1, AUTHOR_COUNT), AUTHOR_COUNT);
const parserIds = faker.helpers.uniqueArray(uniqArrInt(1, PARSER_COUNT), PARSER_COUNT);
const chapterIds = faker.helpers.uniqueArray(uniqArrInt(1, CHAPTER_COUNT), CHAPTER_COUNT);
const chapterPageIds = faker.helpers.uniqueArray(uniqArrInt(1, CHAPTER_PAGE_COUNT), CHAPTER_PAGE_COUNT);
const comicIds = faker.helpers.uniqueArray(uniqArrInt(1, COMIC_COUNT), COMIC_COUNT);

const getFileSize = () => faker.number.int({
  min: 0,
  max: 1000000000,
});

const getTag = (override) => ({
  id: override?.id || 0,
  cdate: '',
  mdate: '',
  name: faker.word.words(1),
});

const tags = tagIds.map((id) => getTag({ id }));

const getAuthor = (override) => ({
  id: override?.id || 0,
  cdate: '',
  mdate: '',
  name: faker.word.words(1),
});
const authors = authorIds.map((id) => getAuthor({ id }));

const getLanguage = (override) => ({
  id: override?.id || 0,
  cdate: '',
  mdate: '',
  name: faker.word.words(1),
});
const languages = languageIds.map((id) => getLanguage({ id }));

const getParser = (override) => ({
  id: override?.id || 0,
  cdate: '',
  mdate: '',
  name: faker.word.words(1),
  siteUrl: faker.internet.url(),
  titleCSS: '',
  annotationCSS: '',
  coverCSS: '',
  authorsCSS: '',
  authorsTextCSS: '',
  languageCSS: '',
  tagsCSS: '',
  tagsTextCSS: '',
  chaptersCSS: '',
  chaptersTitleCSS: '',
  pagesTemplateUrl: '',
  pagesCSS: '',
  pagesImageCSS: '',
});
const parsers = parserIds.map((id) => getParser({ id }));
const parserSingle = (id) => parsers.find((e) => e.id === id);

const getComicCover = (override) => ({
  id: override?.id || 0,
  cdate: '',
  mdate: '',
  comicId: override?.id || 0,
  fromUrl: faker.internet.url(),
  file: {
    id: 0,
    cdate: '',
    mdate: '',
    name: '',
    size: getFileSize(),
    path: faker.image.url(),
    mime: '',
  },
});
const covers = comicIds.map((id) => getComicCover({ id }));

const getComicOverride = (override) => ({
  id: override?.id || 0,
  cdate: '',
  mdate: '',
  comicId: override?.id || 0,
  titleCSS: '',
  annotationCSS: '',
  coverCSS: '',
  authorsCSS: '',
  authorsTextCSS: '',
  languageCSS: '',
  tagsCSS: '',
  tagsTextCSS: '',
  chaptersCSS: '',
  chaptersTitleCSS: '',
  pagesTemplateUrl: '',
  pagesCSS: '',
  pagesImageCSS: '',
});
const overrides = comicIds.map((id) => getComicOverride({ id }));
const overrideSingle = (comicId) => overrides.find((e) => e.comicId === comicId);

const getComic = (override) => ({
  id: override?.id || 0,
  cdate: '',
  mdate: '',
  name: faker.word.words({
    count: {
      min: 1,
      max: 10,
    },
  }),
  annotation: faker.lorem.paragraphs({
    min: 1,
    max: 10,
  }),
  parserId: faker.helpers.arrayElement(parserIds),
  fromUrl: faker.internet.url(),
  languageId: faker.helpers.arrayElement(languageIds),
  authors: faker.helpers.arrayElements(authorIds, {
    min: 1,
    max: AUTHOR_COUNT,
  }),
  tags: faker.helpers.arrayElements(tagIds, {
    min: 1,
    max: TAG_COUNT,
  }),
  cover: covers.find((e) => e.id === override?.id) || null,
});
const comics = comicIds.map((id) => getComic({ id }));
const comicSingle = (id) => comics.find((e) => e.id === id);

const getChapterPage = (override) => ({
  id: override?.id || 0,
  cdate: '',
  mdate: '',
  chapterId: faker.helpers.arrayElement(chapterIds),
  fromUrl: faker.internet.url(),
  isRead: false,
  file: {
    id: 0,
    cdate: '',
    mdate: '',
    name: '',
    size: getFileSize(),
    path: faker.image.url(),
    mime: '',
  },
});
const chapterPages = chapterPageIds.map((id) => getChapterPage({ id }));
const chapterPagesByChapter = (chapterId) => chapterPages.filter((e) => e.chapterId === chapterId);
const updatePage = (override) => {
  const page = chapterPages.find((e) => e.id === override.id);
  Object.assign(page, override);
};
const deletePage = (id) => {
  const index = chapterPages.findIndex((e) => e.id === id);
  chapterPages.splice(index, 1);
};

const getChapter = (override) => ({
  id: override?.id || 0,
  cdate: '',
  mdate: '',
  name: faker.word.words({
    count: {
      min: 1,
      max: 3,
    },
  }),
  comicId: faker.helpers.arrayElement(comicIds),
  get pages() {
    return chapterPagesByChapter(override?.id || 0);
  },
});
const chapters = chapterIds.map((id) => getChapter({ id }));
const chaptersByComic = (comicId) => chapters.filter((e) => e.comicId === comicId);
const chapterSingle = (id) => chapters.find((e) => e.id === id);

const app = express();
const port = 3000;

app.use(express.json());

app.post('/api', (req, res) => {
  console.debug(req.body.path, req.body.body);
  const body = req.body.body;
  const result = { result: {} };

  switch (req.body.path) {
    case 'tag/tag/list': result.result = tags; break;
    case 'author/author/list': result.result = authors; break;
    case 'language/language/list': result.result = languages; break;
    case 'parser/parser/list': result.result = parsers; break;
    case 'parser/parser/get': result.result = parserSingle(body.id); break;
    case 'comic/comic/list': result.result = comics; break;
    case 'comic/comic/get': result.result = comicSingle(body.id); break;
    case 'comic/override/get': result.result = overrideSingle(body.comicId); break;
    case 'chapter/chapter/get': result.result = chapterSingle(body.id); break;
    case 'chapter/chapter/list': result.result = chaptersByComic(body.comicId); break;
    case 'chapter/page/set': result.result = true; updatePage(body); break;
    case 'chapter/page/del': result.result = true; deletePage(body.id); break;
  }

  res.send(JSON.stringify(result));
});

app.listen(port, () => {
  console.log(`Example app listening on port ${port}`);
});
