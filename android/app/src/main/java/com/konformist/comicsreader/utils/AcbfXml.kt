package com.konformist.comicsreader.utils

import android.util.Xml
import com.konformist.comicsreader.db.author.Author
import com.konformist.comicsreader.db.chapter.ChapterWithPages
import com.konformist.comicsreader.db.comic.Comic
import com.konformist.comicsreader.db.comiccover.ComicCover
import com.konformist.comicsreader.db.language.Language
import com.konformist.comicsreader.webapi.ArchiveComic
import org.xmlpull.v1.XmlSerializer
import java.io.File
import java.io.FileOutputStream

class AcbfXml {
  private var comic: Comic? = null
  private var comicCover: ComicCover? = null
  private var chapters: List<ChapterWithPages>? = null
  private var authors: List<Author>? = null
  private var language: Language? = null

  fun addComic(value: Comic): AcbfXml {
    comic = value
    return this
  }

  fun addCover(value: ComicCover): AcbfXml {
    comicCover = value
    return this
  }

  fun addChapters(value: List<ChapterWithPages>): AcbfXml {
    chapters = value
    return this
  }

  fun addAuthors(value: List<Author>): AcbfXml {
    authors = value
    return this
  }

  fun addLanguage(value: Language): AcbfXml {
    language = value
    return this
  }

  private fun createImage(serializer: XmlSerializer, href: String) {
    if (href.isNotBlank()) {
      serializer.startTag("", "image")
      serializer.attribute("", "href", href)
      serializer.endTag("", "image")
    }
  }

  private fun createCoverpage(serializer: XmlSerializer) {
    if (comicCover == null) return

    serializer.startTag("", "coverpage")
    createImage(serializer, "cover.jpg")
    serializer.endTag("", "coverpage")
  }

  private fun createAuthor(serializer: XmlSerializer, author: Author) {
    if (author.name.isBlank()) return

    serializer.startTag("", "author")
    serializer.text(author.name)
    serializer.endTag("", "author")
  }

  private fun createTitle(serializer: XmlSerializer, title: String) {
    if (title.isNotBlank()) {
      serializer.startTag("", "title")
//    serializer.attribute("", "lang", "en")
      serializer.text(title)
      serializer.endTag("", "title")
    }
  }

  private fun createBookAnnotation(serializer: XmlSerializer) {
    if (comic?.annotation.isNullOrBlank()) return

    serializer.startTag("", "annotation")
//    serializer.attribute("", "lang", "en")
    serializer.text(comic?.annotation)
    serializer.endTag("", "annotation")
  }

  private fun createMetaData(serializer: XmlSerializer) {
    serializer.startTag("", "meta-data")
    serializer.startTag("", "book-info")
    authors?.forEach { author -> createAuthor(serializer, author) }
    comic?.let { createTitle(serializer, it.name) }
    createBookAnnotation(serializer)
    createCoverpage(serializer)
    serializer.endTag("", "book-info")
    serializer.endTag("", "meta-data")
  }

  private fun createBody(serializer: XmlSerializer) {
    if (chapters.isNullOrEmpty()) return

    serializer.startTag("", "body")
    val chaptersLength = if (chapters == null) 0
    else ArchiveComic.getPadCount(chapters!!.size)

    chapters!!.forEachIndexed { iChapter, chapter ->
      val length = ArchiveComic.getPadCount(chapter.pages.size)
      val dirName = ArchiveComic.createPadName(chaptersLength, iChapter)

      chapter.pages.forEachIndexed { index, page ->
        page.file?.let { file ->
          serializer.startTag("", "page")
          if (index == 0) chapter.chapter.name?.let { createTitle(serializer, it) }
          val ext = File(file.path).extension
          val fileName = "${ArchiveComic.createPadName(length, index)}.$ext"
          createImage(serializer, "$dirName/$fileName")
          serializer.endTag("", "page")
        }
      }
    }
    serializer.endTag("", "body")
  }

  fun create(file: File) {
    val serializer: XmlSerializer = Xml.newSerializer()
    val outputStream = FileOutputStream(file)

    serializer.setOutput(outputStream, "UTF-8")
    serializer.startDocument("UTF-8", true)

    // Начало элемента <ACBF>
    serializer.startTag("", "ACBF")
    serializer.attribute("", "xmlns", "http://www.acbf.info/xml/acbf/1.1")

    createMetaData(serializer)
    createBody(serializer)

    // Закрытие элемента <ACBF>
    serializer.endTag("", "ACBF")

    // Завершение документа
    serializer.endDocument()
    outputStream.close()
  }
}