package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.comic.ComicCover
import com.konformist.comicsreader.db.comic.ComicCoverWithFile
import com.konformist.comicsreader.db.file.File
import org.json.JSONArray
import org.json.JSONObject

class ComicCoverSerializer : Serializer<ComicCoverWithFile>() {
  private val fileSerializer = FileSerializer()

  override fun toJSON(item: ComicCoverWithFile): JSONObject {
    val data = JSONObject()
    data.put("id", item.cover.id)
    data.put("fromUrl", item.cover.fromUrl)

    if (item.file != null)
      data.put("file", fileSerializer.toJSON(item.file))
    else
      data.put("file", null)

    return data
  }

  override fun toJSONArray(items: List<ComicCoverWithFile>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }

  override fun fromJSON(item: JSONObject): ComicCoverWithFile {
    val fileItem = item.optJSONObject("file")

    var file: File? = null
    var fileId: Long? = null

    if (fileItem != null) {
      file = fileSerializer.fromJSON(fileItem)
      fileId = file.id
    }

    return ComicCoverWithFile(
      cover = ComicCover(
        id = getId(item.optLong("id")),
        comicId = 0,
        cdate = null,
        mdate = null,
        fromUrl = item.optString("fromUrl", ""),
        fileId = fileId,
      ),
      file = file
    )
  }
}