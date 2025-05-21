package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.comic.ChapterPage
import com.konformist.comicsreader.db.comic.ChapterPageWithFile
import com.konformist.comicsreader.db.file.File
import org.json.JSONArray
import org.json.JSONObject

class ChapterPageWithFileSerializer : Serializer<ChapterPageWithFile>() {
  private val fileSerializer = FileSerializer()

  override fun toJSON(item: ChapterPageWithFile): JSONObject {
    val data = JSONObject()

    data.put("id", item.page.id)
    data.put("fromUrl", item.page.fromUrl)

    if (item.file != null)
      data.put("file", fileSerializer.toJSON(item.file))
    else
      data.put("file", null)

    return data
  }

  override fun toJSONArray(items: List<ChapterPageWithFile>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }

  override fun fromJSON(item: JSONObject): ChapterPageWithFile {
    val fileItem = item.optJSONObject("file")

    var file: File? = null
    var fileId: Long? = null

    if (fileItem != null) {
      file = fileSerializer.fromJSON(fileItem)
      fileId = file.id
    }

    return ChapterPageWithFile(
      page = ChapterPage(
        id = getId(item.optLong("id")),
        chapterId = 0,
        cdate = null,
        mdate = null,
        fromUrl = item.optString("fromUrl", ""),
        fileId = fileId,
      ),
      file = file
    )
  }

  fun fromJSONArray(value: JSONArray): List<ChapterPageWithFile> {
    val result = arrayListOf<ChapterPageWithFile>()

    for (index in 0 until value.length()) {
      result.add(fromJSON(value.getJSONObject(index)))
    }

    return result
  }
}