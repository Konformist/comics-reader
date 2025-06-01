import ParserController from '@/core/entities/parser/ParserController.ts';
import type { IParsedChapterData, IParsedComicData } from '@/core/entities/parser/ParserTypes.ts';
import { cleanHTML, parseArray, parseImage, parseString } from '@/core/entities/parser/parseUtils.ts';
import sleep from '@/core/utils/sleep.ts';
import type { IParseData } from '@/plugins/WebApiPlugin';
import type ParserModel from '@/core/entities/parser/ParserModel.ts';
import type ComicOverrideModel from '@/core/entities/comic-override/ComicOverrideModel.ts';

export default class Parser {
  static PAGE_ID = '<PAGE_ID>';
  private domParser = new DOMParser();

  private parseInfo: IParseData = {
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

  setParseInfo(parser: ParserModel, override: ComicOverrideModel) {
    this.parseInfo.titleCSS = override.titleCSS || parser.titleCSS;
    this.parseInfo.annotationCSS = override.annotationCSS || parser.annotationCSS;
    this.parseInfo.coverCSS = override.coverCSS || parser.coverCSS;
    this.parseInfo.authorsCSS = override.authorsCSS || parser.authorsCSS;
    this.parseInfo.authorsTextCSS = override.authorsTextCSS || parser.authorsTextCSS;
    this.parseInfo.languageCSS = override.languageCSS || parser.languageCSS;
    this.parseInfo.tagsCSS = override.tagsCSS || parser.tagsCSS;
    this.parseInfo.tagsTextCSS = override.tagsTextCSS || parser.tagsTextCSS;
    this.parseInfo.chaptersCSS = override.chaptersCSS || parser.chaptersCSS;
    this.parseInfo.chaptersTitleCSS = override.chaptersTitleCSS || parser.chaptersTitleCSS;
    this.parseInfo.pagesTemplateUrl = override.pagesTemplateUrl || parser.pagesTemplateUrl;
    this.parseInfo.pagesCSS = override.pagesCSS || parser.pagesCSS;
    this.parseInfo.pagesImageCSS = override.pagesImageCSS || parser.pagesImageCSS;
  }

  private parseComic(comicRaw: string): IParsedComicData {
    const cleaned = cleanHTML(comicRaw);
    const comicDOM = this.domParser
      .parseFromString(cleaned, 'text/html')
      .body;
    const result: IParsedComicData = {
      name: '',
      annotation: '',
      cover: '',
      language: '',
      tags: [],
      authors: [],
      chapters: [],
    };

    result.name = parseString(comicDOM, this.parseInfo.titleCSS);
    result.annotation = parseString(comicDOM, this.parseInfo.annotationCSS);
    result.cover = parseImage(comicDOM, this.parseInfo.coverCSS);
    result.language = parseString(comicDOM, this.parseInfo.languageCSS);
    result.tags = parseArray(comicDOM, this.parseInfo.tagsCSS, this.parseInfo.tagsTextCSS);
    result.authors = parseArray(comicDOM, this.parseInfo.authorsCSS, this.parseInfo.authorsTextCSS);

    const chapters: IParsedChapterData[] = [];
    result.chapters = chapters;

    if (this.parseInfo.chaptersCSS) {
      comicDOM.querySelectorAll(this.parseInfo.chaptersCSS).forEach((e) => {
        const pagesCount = +(e.querySelector(this.parseInfo.pagesCSS)?.textContent || 0);

        chapters.push({
          name: e.querySelector(this.parseInfo.chaptersTitleCSS)?.textContent || '',
          fromUrl: e.querySelector<HTMLLinkElement>('a')?.href || '',
          pages: (new Array(pagesCount)).fill(''),
        });
      });
    } else if (this.parseInfo.pagesCSS) {
      const pagesCount = comicDOM.querySelectorAll(this.parseInfo.pagesCSS).length;
      chapters.push({
        name: '',
        fromUrl: '',
        pages: (new Array(pagesCount)).fill(''),
      });
    }

    return result;
  }

  private parseChapterPagesCount(chapterRaw: string): number {
    const cleaned = cleanHTML(chapterRaw);
    const chapterDOM = this.domParser
      .parseFromString(cleaned, 'text/html')
      .body;

    return +(chapterDOM.querySelector(this.parseInfo.pagesCSS)?.textContent || 0);
  }

  private parseChapterPageImage(pageRaw: string): string {
    const cleaned = cleanHTML(pageRaw);
    const pageDOM = this.domParser
      .parseFromString(cleaned, 'text/html')
      .body;

    return (pageDOM.querySelector<HTMLImageElement>(this.parseInfo.pagesImageCSS)?.src || '');
  }

  async parse(comicUrl: string, cookie: string = ''): Promise<IParsedComicData> {
    const comicRaw = await ParserController.loadHTMLRaw(comicUrl, cookie);
    const comicData = this.parseComic(comicRaw);

    if (this.parseInfo.pagesCSS) {
      for (const chapter of comicData.chapters) {
        const chapterRaw = chapter.fromUrl
          ? await ParserController.loadHTMLRaw(chapter.fromUrl, cookie)
          : comicRaw;
        const pagesCount = this.parseChapterPagesCount(chapterRaw);
        if (pagesCount <= chapter.pages.length) continue;

        for (let i = 0; i < pagesCount - chapter.pages.length; i++) {
          chapter.pages.push('');
        }
      }
    }

    if (this.parseInfo.pagesImageCSS && this.parseInfo.pagesTemplateUrl) {
      for (let i = 0; i < comicData.chapters.length; i++){
        const chapter = comicData.chapters[i];
        const parentUrlPattern = (chapter.fromUrl || comicUrl).endsWith('/')
          ? (chapter.fromUrl || comicUrl)
          : `${(chapter.fromUrl || comicUrl)}/`;
        const pageUrlPattern = this.parseInfo.pagesTemplateUrl.startsWith('/')
          ? this.parseInfo.pagesTemplateUrl.replace('/', '')
          : this.parseInfo.pagesTemplateUrl;
        const urlPattern = `${parentUrlPattern}${pageUrlPattern}`.replace(Parser.PAGE_ID, (i + 1).toString());

        const pages: string[] = [];
        for (let j = 0; j < chapter.pages.length; j++) {
          const pageRaw = await ParserController.loadHTMLRaw(urlPattern, cookie);
          const pageImage = this.parseChapterPageImage(pageRaw);
          pages.push(pageImage);
          await sleep(Math.random());
        }
        chapter.pages = pages;
      }
    }

    return comicData;
  }
}
