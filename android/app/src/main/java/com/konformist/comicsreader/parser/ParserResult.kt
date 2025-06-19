package com.konformist.comicsreader.parser

data class ParserChapterResult(
  val title: String = "",
  val link: String = "",
  val pages: MutableList<String> = mutableListOf<String>(),
)

data class ParserResult(
  var title: String = "",
  var annotation: String = "",
  var coverUrl: String = "",
  val tags: MutableList<String> = mutableListOf<String>(),
  val authors: MutableList<String> = mutableListOf<String>(),
  val languages: MutableList<String> = mutableListOf<String>(),
  val chapters: MutableList<ParserChapterResult> = mutableListOf<ParserChapterResult>(),
)
