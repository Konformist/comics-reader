package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.comic.Chapter
import com.konformist.comicsreader.db.comic.ChapterPageWithFile
import com.konformist.comicsreader.db.comic.ChapterWithPages
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

class ChapterWithPagesSerializer : Serializer<ChapterWithPages>() {
  private val pageSerializer = ChapterPageWithFileSerializer()

  override fun toJSON(item: ChapterWithPages): JSONObject {
    val data = JSONObject()

    data.put("id", item.chapter.id)
    data.put("cdate", item.chapter.cdate?.time)
    data.put("mdate", item.chapter.mdate?.time)
    data.put("name", item.chapter.name)
    data.put("pages", pageSerializer.toJSONArray(item.pages))

    return data
  }

  override fun toJSONArray(items: List<ChapterWithPages>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }

  override fun fromJSON(item: JSONObject): ChapterWithPages {
    val pagesJson = item.optJSONArray("pages")
    val pages: List<ChapterPageWithFile> = if (pagesJson != null)
      pageSerializer.fromJSONArray(pagesJson)
    else
      listOf()

    return ChapterWithPages(
      chapter = Chapter(
        id = item.optLong("id", 0),
        name = item.optString("name", ""),
        cdate = Date(item.optLong("cdate")),
        mdate = Date(item.optLong("mdate")),
        comicId = 0
      ),
      pages = pages
    )
  }

  fun fromJSONArray(value: JSONArray): List<ChapterWithPages> {
    val result = arrayListOf<ChapterWithPages>()

    for (index in 0 until value.length()) {
      result.add(fromJSON(value.getJSONObject(index)))
    }

    return result
  }
}