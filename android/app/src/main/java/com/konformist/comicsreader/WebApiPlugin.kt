package com.konformist.comicsreader

import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.konformist.comicsreader.webapi.WebApi

@CapacitorPlugin(name = "WebApi")
class WebApiPlugin : Plugin() {
  private val webApi = WebApi(context)

  @PluginMethod
  fun getTagsAll(call: PluginCall) {
    call.resolve(JSObject(webApi.getTagsAll()))
  }

  @PluginMethod
  fun getTag(call: PluginCall) {
    try {
      val result = webApi.getTag(call.getLong("id", 0)!!)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun addTag(call: PluginCall) {
    try {
      val result = webApi.addTag(call.data)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun setTag(call: PluginCall) {
    try {
      webApi.setTag(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun delTag(call: PluginCall) {
    try {
      webApi.delTag(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun getAuthorsAll(call: PluginCall) {
    call.resolve(JSObject(webApi.getAuthorsAll()))
  }

  @PluginMethod
  fun getAuthor(call: PluginCall) {
    try {
      val result = webApi.getAuthor(call.getLong("id", 0)!!)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun addAuthor(call: PluginCall) {
    try {
      val result = webApi.addAuthor(call.data)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun setAuthor(call: PluginCall) {
    try {
      webApi.setAuthor(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun delAuthor(call: PluginCall) {
    try {
      webApi.delAuthor(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun getLanguagesAll(call: PluginCall) {
    call.resolve(JSObject(webApi.getLanguagesAll()))
  }

  @PluginMethod
  fun getLanguage(call: PluginCall) {
    try {
      val result = webApi.getLanguage(call.getLong("id", 0)!!)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun addLanguage(call: PluginCall) {
    try {
      val result = webApi.addLanguage(call.data)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun setLanguage(call: PluginCall) {
    try {
      webApi.setLanguage(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun delLanguage(call: PluginCall) {
    try {
      webApi.delLanguage(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun getParsersAll(call: PluginCall) {
    call.resolve(JSObject(webApi.getParsersAll()))
  }

  @PluginMethod
  fun getParser(call: PluginCall) {
    try {
      val result = webApi.getParser(call.getLong("id", 0)!!)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun addParser(call: PluginCall) {
    try {
      val result = webApi.addParser(call.data)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun setParser(call: PluginCall) {
    try {
      webApi.setParser(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun delParser(call: PluginCall) {
    try {
      webApi.delParser(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun getComicsAll(call: PluginCall) {
    call.resolve(JSObject(webApi.getComicsAll()))
  }

  @PluginMethod
  fun getComic(call: PluginCall) {
    try {
      val result = webApi.getComic(call.getLong("id", 0)!!)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun addComic(call: PluginCall) {
    try {
      val result = webApi.addComic(call.data)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun setComic(call: PluginCall) {
    try {
      webApi.setComic(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun delComic(call: PluginCall) {
    try {
      webApi.delComic(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun getComicOverride(call: PluginCall) {
    try {
      val result = webApi.getComicOverride(call.getLong("id", 0)!!)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun setComicOverride(call: PluginCall) {
    try {
      webApi.setComicOverride(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun addCoverFile(call: PluginCall) {
    try {
      val result = webApi.addCoverFile(call.data)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun delCoverFile(call: PluginCall) {
    try {
      webApi.delCoverFile(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun getChaptersAll(call: PluginCall) {
    try {
      val result = webApi.getChaptersAll(call.data)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun getChapter(call: PluginCall) {
    try {
      val result = webApi.getChapter(call.getLong("id", 0)!!)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun addChapter(call: PluginCall) {
    try {
      val result = webApi.addChapter(call.data)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun setChapter(call: PluginCall) {
    try {
      webApi.setChapter(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun delChapter(call: PluginCall) {
    try {
      webApi.delChapter(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun getChapterPagesAll(call: PluginCall) {
    try {
      val result = webApi.getChapterPagesAll(call.data)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun getChapterPage(call: PluginCall) {
    try {
      val result = webApi.getChapterPage(call.getLong("id", 0)!!)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun addChapterPage(call: PluginCall) {
    try {
      val result = webApi.addChapterPage(call.data)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun setChapterPage(call: PluginCall) {
    try {
      webApi.setChapterPage(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun delChapterPage(call: PluginCall) {
    try {
      webApi.delChapterPage(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun addChapterPageFile(call: PluginCall) {
    try {
      val result = webApi.addChapterPageFile(call.data)
      call.resolve(JSObject(result))
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun delChapterPageFile(call: PluginCall) {
    try {
      webApi.delChapterPageFile(call.data)
      call.resolve()
    } catch (e: Error) {
      call.reject(e.message)
    }
  }

  @PluginMethod
  fun migrate(call: PluginCall) {
    webApi.migrate(call.data)
    call.resolve()
  }
}