package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.chapterpage.ChapterPageCreate
import com.konformist.comicsreader.db.chapterpage.ChapterPageUpdate
import com.konformist.comicsreader.db.chapterpage.ChapterPageWithFile
import com.konformist.comicsreader.utils.ValidationException
import org.json.JSONArray
import org.json.JSONObject

class ChapterPageSerializer : Serializer<ChapterPageWithFile>() {
  private val fileSerializer = FileSerializer()

  @Throws(ValidationException::class)
  override fun createFromJSON(value: JSONObject): ChapterPageCreate {
    val chapterId = value.optLong("chapterId", 0)
    if (chapterId == 0.toLong()) throw ValidationException("chapterId")

    return ChapterPageCreate(
      chapterId = chapterId,
      fileId = value.optLong("fileId", 0),
      fromUrl = value.optString("fromUrl", ""),
    )
  }

  @Throws(ValidationException::class)
  override fun updateFromJSON(value: JSONObject): ChapterPageUpdate {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("id")

    return ChapterPageUpdate(
      id = id,
      mdate = getMDate(),
      fileId = value.optLong("fileId", 0),
      fromUrl = value.optString("fromUrl", ""),
      isRead = value.optBoolean("isRead", false),
    )
  }

  @Throws(ValidationException::class)
  override fun deleteFromJSON(value: JSONObject) {
  }

  override fun toJSON(item: ChapterPageWithFile): JSONObject {
    val data = JSONObject()

    data.put("id", item.page.id)
    data.put("chapterId", item.page.chapterId)
    data.put("fromUrl", item.page.fromUrl)
    data.put("isRead", item.page.isRead)

    if (item.file == null) data.put("file", null)
    else data.put("file", fileSerializer.toJSON(item.file))

    return data
  }

  override fun toJSONArray(items: List<ChapterPageWithFile>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }
}