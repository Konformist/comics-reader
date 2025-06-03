import type {
  IAuthorDTO,
  IChapterDTO,
  IChapterPageDTO,
  IComicCoverDTO,
  IComicDTO,
  IComicOverrideDTO,
  ILanguageDTO,
  IParserDTO, ISettingsDTO,
  ITagDTO,
} from '@/plugins/WebApiPlugin.ts';
import { faker } from '@faker-js/faker';

export default class WebApiPluginFake {
  LANGUAGE_COUNT = 10;
  TAG_COUNT = 50;
  AUTHOR_COUNT = 10;
  PARSER_COUNT = 10;
  CHAPTER_COUNT = 10;
  CHAPTER_PAGE_COUNT = 10;
  COMIC_COUNT = 200;

  languageIds = faker.helpers.uniqueArray(this.uniqArrInt(1, this.LANGUAGE_COUNT), this.LANGUAGE_COUNT);
  tagIds = faker.helpers.uniqueArray(this.uniqArrInt(1, this.TAG_COUNT), this.TAG_COUNT);
  authorIds = faker.helpers.uniqueArray(this.uniqArrInt(1, this.AUTHOR_COUNT), this.AUTHOR_COUNT);
  parserIds = faker.helpers.uniqueArray(this.uniqArrInt(1, this.PARSER_COUNT), this.PARSER_COUNT);
  chapterIds = faker.helpers.uniqueArray(this.uniqArrInt(1, this.CHAPTER_COUNT), this.CHAPTER_COUNT);
  chapterPageIds = faker.helpers.uniqueArray(this.uniqArrInt(1, this.CHAPTER_PAGE_COUNT), this.CHAPTER_PAGE_COUNT);
  comicCoverIds = faker.helpers.uniqueArray(this.uniqArrInt(1, this.COMIC_COUNT), this.COMIC_COUNT);
  comicOverrideIds = faker.helpers.uniqueArray(this.uniqArrInt(1, this.COMIC_COUNT), this.COMIC_COUNT);
  comicIds = faker.helpers.uniqueArray(this.uniqArrInt(1, this.COMIC_COUNT), this.COMIC_COUNT);

  uniqArrInt(min: number, max: number) {
    return () => faker.number.int({
      min,
      max,
    });
  }

  getParserId() {
    return faker.helpers.arrayElement(this.parserIds);
  }

  getParser(override?: Partial<IParserDTO>): IParserDTO {
    return {
      id: override?.id || this.getLanguageId(),
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
    };
  }

  getParsers(): IParserDTO[] {
    return faker.helpers.arrayElements(this.parserIds, {
      min: 1,
      max: this.PARSER_COUNT,
    })
      .map((e) => this.getParser({ id: e }));
  }

  getLanguageId() {
    return faker.helpers.arrayElement(this.languageIds);
  }

  getLanguage(override?: Partial<ILanguageDTO>): ILanguageDTO {
    return {
      id: override?.id || this.getLanguageId(),
      cdate: '',
      mdate: '',
      name: faker.word.words(1),
    };
  }

  getLanguages = (): ILanguageDTO[] => (
    faker.helpers.arrayElements(this.languageIds, {
      min: 1,
      max: this.LANGUAGE_COUNT,
    })
      .map((e) => this.getLanguage({ id: e }))
  );

  getTagId() {
    return faker.helpers.arrayElement(this.tagIds);
  }

  getTag(override?: Partial<ITagDTO>): ITagDTO {
    return {
      id: override?.id || this.getTagId(),
      cdate: '',
      mdate: '',
      name: faker.word.words(1),
    };
  }

  getTags(): ITagDTO[] {
    return faker.helpers.arrayElements(this.tagIds, {
      min: 1,
      max: this.TAG_COUNT,
    })
      .map((e) => this.getTag({ id: e }));
  }

  getAuthorId() {
    return faker.helpers.arrayElement(this.authorIds);
  }

  getAuthor(override?: Partial<IAuthorDTO>): IAuthorDTO {
    return {
      id: override?.id || this.getLanguageId(),
      cdate: '',
      mdate: '',
      name: faker.word.words(1),
    };
  }

  getAuthors(): IAuthorDTO[] {
    return faker.helpers.arrayElements(this.authorIds, {
      min: 1,
      max: this.AUTHOR_COUNT,
    })
      .map((e) => this.getAuthor({ id: e }));
  }

  getComicCoverId() {
    return faker.helpers.arrayElement(this.comicCoverIds);
  }

  getComicCover(): IComicCoverDTO {
    return {
      id: this.getComicCoverId(),
      fromUrl: faker.internet.url(),
      file: {
        id: 0,
        cdate: '',
        mdate: '',
        name: '',
        size: faker.number.int(),
        path: faker.image.url(),
        mime: '',
      },
    };
  }

  getComicOverrideId() {
    return faker.helpers.arrayElement(this.comicOverrideIds);
  }

  getComicOverride(): IComicOverrideDTO {
    return {
      id: this.getComicOverrideId(),
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
    };
  }

  getComicId() {
    return faker.helpers.arrayElement(this.comicIds);
  }

  getComic(override?: Partial<IComicDTO>): IComicDTO {
    return {
      id: override?.id || this.getComicId(),
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
      parserId: this.getParserId(),
      fromUrl: faker.internet.url(),
      languageId: this.getLanguageId(),
      authors: faker.helpers.arrayElements(this.authorIds, {
        min: 1,
        max: this.AUTHOR_COUNT,
      }),
      tags: faker.helpers.arrayElements(this.tagIds, {
        min: 1,
        max: this.TAG_COUNT,
      }),
      cover: this.getComicCover(),
    };
  }

  getComics(): IComicDTO[] {
    return faker.helpers.arrayElements(this.comicIds, {
      min: 1,
      max: this.COMIC_COUNT,
    })
      .map((e) => this.getComic({ id: e }));
  }

  getChapterPageId() {
    return faker.helpers.arrayElement(this.chapterPageIds);
  }

  getChapterPage(override?: Partial<IChapterPageDTO>): IChapterPageDTO {
    return {
      id: override?.id || this.getChapterPageId(),
      chapterId: override?.chapterId || this.getChapterId(),
      fromUrl: faker.internet.url(),
      isRead: faker.helpers.arrayElement([true, false]),
      file: {
        id: 0,
        cdate: '',
        mdate: '',
        name: '',
        size: faker.number.int(),
        path: faker.image.url(),
        mime: '',
      },
    };
  }

  getChapterPages(chapterId: number): IChapterPageDTO[] {
    return faker.helpers.arrayElements(this.chapterPageIds, {
      min: 5,
      max: this.CHAPTER_PAGE_COUNT,
    })
      .map((e) => this.getChapterPage({
        id: e,
        chapterId,
      }));
  }

  getChapterId() {
    return faker.helpers.arrayElement(this.chapterIds);
  }

  getChapter = (override?: Partial<IChapterDTO>): IChapterDTO => {
    const chapterId = override?.id || this.getChapterId();
    return {
      id: chapterId,
      cdate: '',
      mdate: '',
      name: faker.word.words({
        count: {
          min: 1,
          max: 3,
        },
      }),
      comicId: override?.comicId || this.getComicId(),
      pages: this.getChapterPages(chapterId),
    };
  };

  getChapters = (comicId: number): IChapterDTO[] => (
    faker.helpers.arrayElements(this.chapterIds, {
      min: 1,
      max: this.CHAPTER_COUNT,
    })
      .map((e) => this.getChapter({
        id: e,
        comicId,
      }))
  );

  getSettings(): ISettingsDTO {
    return {
      autoReading: true,
      autoReadingTimeout: 3000,
      isCompress: true,
      readingMode: 'webtoon',
    };
  }
}
