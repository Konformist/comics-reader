package com.konformist.comicsreader.comicarchive

import android.util.Xml
import com.konformist.comicsreader.data.author.Author
import com.konformist.comicsreader.data.comic.Comic
import com.konformist.comicsreader.data.language.Language
import org.xmlpull.v1.XmlSerializer
import java.io.File
import java.io.FileOutputStream

class MetaXmlCreator {
  private var authors: List<Author>? = null
  private var language: Language? = null
  private var comic: Comic? = null
  private var cover: String = ""

  private data class Chapter(
    val name: String,
    val pages: List<String>,
  )

  private var chapters: MutableList<Chapter> = mutableListOf()

  fun addComic(value: Comic): MetaXmlCreator {
    comic = value
    return this
  }

  fun addCover(value: String): MetaXmlCreator {
    cover = value
    return this
  }

  fun addChapter(name: String, pages: List<String>): MetaXmlCreator {
    chapters.add(Chapter(name = name, pages = pages))
    return this
  }

  fun addAuthors(value: List<Author>): MetaXmlCreator {
    authors = value
    return this
  }

  fun addLanguage(value: Language): MetaXmlCreator {
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
    if (cover.isNotBlank()) {
      serializer.startTag("", "coverpage")
      createImage(serializer, cover)
      serializer.endTag("", "coverpage")
    }
  }

  private fun createAuthor(serializer: XmlSerializer, author: Author) {
    if (author.name.isNotBlank()) {
      serializer.startTag("", "author")
      serializer.text(author.name)
      serializer.endTag("", "author")
    }
  }

  private fun createTitle(serializer: XmlSerializer, title: String) {
    if (title.isNotBlank()) {
      serializer.startTag("", "title")
//    serializer.attribute("", "lang", "en")
      serializer.text(title)
      serializer.endTag("", "title")
    }
  }

  private fun createBookAnnotation(serializer: XmlSerializer, value: String) {
    if (value.isNotBlank()) {
      serializer.startTag("", "annotation")
//    serializer.attribute("", "lang", "en")
      serializer.text(value)
      serializer.endTag("", "annotation")
    }
  }

  private fun createMetaData(serializer: XmlSerializer) {
    serializer.startTag("", "meta-data")
    serializer.startTag("", "book-info")

    authors?.forEach { author -> createAuthor(serializer, author) }
    comic?.let {
      createTitle(serializer, it.name)
      createBookAnnotation(serializer, it.annotation)
    }
    createCoverpage(serializer)

    serializer.endTag("", "book-info")
    serializer.endTag("", "meta-data")
  }

  private fun createBody(serializer: XmlSerializer) {
    serializer.startTag("", "body")
    chapters.forEach { chapter ->
      chapter.pages.forEachIndexed { index, page ->
        serializer.startTag("", "page")
        if (index == 0) createTitle(serializer, chapter.name)
        createImage(serializer, page)
        serializer.endTag("", "page")
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