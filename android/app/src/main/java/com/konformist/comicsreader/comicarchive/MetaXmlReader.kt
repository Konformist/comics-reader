package com.konformist.comicsreader.comicarchive

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.parser.Parser
import java.io.File

class MetaXmlReader {
  data class ResultChapter(
    var title: String = "",
    var pages: MutableList<String> = mutableListOf<String>(),
  )

  data class Result(
    var title: String = "",
    var annotation: String = "",
    var cover: String = "",
    var chapters: MutableList<ResultChapter> = mutableListOf<ResultChapter>(),
  )

  fun read(xml: File): Result {
    val result = Result()
    val xmlParsed =
      Ksoup.parse(html = xml.readText(charset = Charsets.UTF_8), parser = Parser.xmlParser())
    val bookInfo = xmlParsed.select("book-info")

    result.title = bookInfo.select("title").text()
    result.annotation = bookInfo.select("annotation").text()
    result.cover = bookInfo.select("coverpage > image").attr("href")

    var chapter = ResultChapter()

    xmlParsed.select("body > page").forEach { page ->
      val title = page.select("title").text()
      if (title.isNotBlank()) {
        chapter = ResultChapter(title = title)
        result.chapters.add(chapter)
      }

      chapter.pages.add(page.select("image").attr("href"))
    }

    return result
  }
}