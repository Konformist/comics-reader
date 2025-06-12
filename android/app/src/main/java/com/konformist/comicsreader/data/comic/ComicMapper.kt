package com.konformist.comicsreader.data.comic

import com.konformist.comicsreader.utils.DatesUtils

fun ComicUpdate.merge(value: Comic): ComicUpdate {
  return this.copy(
    mdate = DatesUtils.nowFormatted(),
    name = this.name ?: value.name,
    parserId = this.parserId ?: value.parserId,
    fromUrl = this.fromUrl ?: value.fromUrl,
    annotation = this.annotation ?: value.annotation,
    languageId = this.languageId ?: value.languageId,
    tags = this.tags ?: value.tags,
    authors = this.authors ?: value.authors,
  )
}
