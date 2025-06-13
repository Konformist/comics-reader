package com.konformist.comicsreader.utils.comicarchive

import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.io.File
import java.io.FileInputStream

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

    FileInputStream(xml).use {
      val xmlParsed = Jsoup.parse(xml, "UTF-8", "", Parser.xmlParser())
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
}