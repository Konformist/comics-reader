package com.konformist.comicsreader

import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.konformist.comicsreader.webapi.WebApi

@CapacitorPlugin(name = "WebApi")
class WebApiPlugin : Plugin() {
  private lateinit var webApi: WebApi

  override fun load() {
    super.load()
    // TODO for testing
//    context.deleteDatabase("app-database")
    webApi = WebApi(context)
  }

  @PluginMethod
  fun getTagsAll(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getTagsAll()))
  }

  @PluginMethod
  fun getTag(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getTag(call.data)))
  }

  @PluginMethod
  fun addTag(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.addTag(call.data)))
  }

  @PluginMethod
  fun setTag(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.setTag(call.data)))
  }

  @PluginMethod
  fun delTag(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.delTag(call.data)))
  }

  @PluginMethod
  fun getAuthorsAll(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getAuthorsAll()))
  }

  @PluginMethod
  fun getAuthor(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getAuthor(call.data)))
  }

  @PluginMethod
  fun addAuthor(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.addAuthor(call.data)))
  }

  @PluginMethod
  fun setAuthor(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.setAuthor(call.data)))
  }

  @PluginMethod
  fun delAuthor(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.delAuthor(call.data)))
  }

  @PluginMethod
  fun getLanguagesAll(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getLanguagesAll()))
  }

  @PluginMethod
  fun getLanguage(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getLanguage(call.data)))
  }

  @PluginMethod
  fun addLanguage(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.addLanguage(call.data)))
  }

  @PluginMethod
  fun setLanguage(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.setLanguage(call.data)))
  }

  @PluginMethod
  fun delLanguage(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.delLanguage(call.data)))
  }

  @PluginMethod
  fun getParsersAll(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getParsersAll()))
  }

  @PluginMethod
  fun getParser(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getParser(call.data)))
  }

  @PluginMethod
  fun addParser(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.addParser(call.data)))
  }

  @PluginMethod
  fun setParser(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.setParser(call.data)))
  }

  @PluginMethod
  fun delParser(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.delParser(call.data)))
  }

  @PluginMethod
  fun getComicsAll(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getComicsAll()))
  }

  @PluginMethod
  fun getComic(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getComic(call.data)))
  }

  @PluginMethod
  fun addComic(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.addComic(call.data)))
  }

  @PluginMethod
  fun setComic(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.setComic(call.data)))
  }

  @PluginMethod
  fun delComic(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.delComic(call.data)))
  }

  @PluginMethod
  fun getComicOverride(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getComicOverride(call.data)))
  }

  @PluginMethod
  fun setComicOverride(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.setComicOverride(call.data)))
  }

  @PluginMethod
  fun addCoverFile(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.addCoverFile(call.data)))
  }

  @PluginMethod
  fun delCoverFile(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.delCoverFile(call.data)))
  }

  @PluginMethod
  fun getChaptersAll(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getChaptersAll(call.data)))
  }

  @PluginMethod
  fun getChapter(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getChapter(call.data)))
  }

  @PluginMethod
  fun addChapter(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.addChapter(call.data)))
  }

  @PluginMethod
  fun setChapter(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.setChapter(call.data)))
  }

  @PluginMethod
  fun delChapter(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.delChapter(call.data)))
  }

  @PluginMethod
  fun getChapterPagesAll(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getChapterPagesAll(call.data)))
  }

  @PluginMethod
  fun getChapterPage(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.getChapterPage(call.data)))
  }

  @PluginMethod
  fun addChapterPage(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.addChapterPage(call.data)))
  }

  @PluginMethod
  fun setChapterPage(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.setChapterPage(call.data)))
  }

  @PluginMethod
  fun delChapterPage(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.delChapterPage(call.data)))
  }

  @PluginMethod
  fun addChapterPageFile(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.addChapterPageFile(call.data)))
  }

  @PluginMethod
  fun delChapterPageFile(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.delChapterPageFile(call.data)))
  }

  @PluginMethod
  fun addBackup(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.addBackup()))
  }

  @PluginMethod
  fun restoreBackup(call: PluginCall) {
    call.resolve(JSObject.fromJSONObject(webApi.restoreBackup(call.data)))
  }

  @PluginMethod
  fun migrate(call: PluginCall) {
    webApi.migrate(call.data)
    call.resolve()
  }
}