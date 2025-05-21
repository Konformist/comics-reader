package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.comic.Comic
import com.konformist.comicsreader.db.comic.ComicCoverWithFile
import com.konformist.comicsreader.db.comic.ComicLite
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

class ComicLiteSerializer : Serializer<ComicLite>() {
  private val coverSerializer = ComicCoverSerializer()

  override fun toJSON(item: ComicLite): JSONObject {
    val data = JSONObject()

    data.put("id", item.comic.id)
    data.put("cdate", item.comic.cdate?.time)
    data.put("mdate", item.comic.mdate?.time)
    data.put("name", item.comic.name)
    data.put("parser", item.comic.parserId)
    data.put("fromUrl", item.comic.fromUrl)
    data.put("language", item.comic.languageId)
    data.put("authors", toArrayString(item.comic.authors))
    data.put("tags", toArrayString(item.comic.tags))

    if (item.cover != null) {
      data.put("cover", coverSerializer.toJSON(item.cover))
    } else {
      data.put("cover", null)
    }

    return data
  }

  override fun toJSONArray(items: List<ComicLite>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }

  override fun fromJSON(item: JSONObject): ComicLite {
    val coverItem = item.optJSONObject("cover")
    val cover: ComicCoverWithFile? = if (coverItem != null) {
      coverSerializer.fromJSON(coverItem)
    } else {
      null
    }

    return ComicLite(
      comic = Comic(
        id = item.optLong("id", 0),
        cdate = Date(item.optLong("cdate", Date().time)),
        mdate = Date(item.optLong("mdate", Date().time)),
        name = item.optString("name", ""),
        parserId = item.optLong("parser", 0),
        fromUrl = item.optString("fromUrl", ""),
        languageId = item.optLong("language", 0),
        authors = fromArrayString(item.optString("authors")),
        tags = fromArrayString(item.optString("tags"))
      ),
      cover = cover,
    )
  }
}