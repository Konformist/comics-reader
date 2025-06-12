package com.konformist.comicsreader.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.konformist.comicsreader.data.appfile.AppFile
import com.konformist.comicsreader.data.appfile.AppFileDao
import com.konformist.comicsreader.data.author.Author
import com.konformist.comicsreader.data.author.AuthorDao
import com.konformist.comicsreader.data.chapter.Chapter
import com.konformist.comicsreader.data.chapter.ChapterDao
import com.konformist.comicsreader.data.chapterpage.ChapterPage
import com.konformist.comicsreader.data.chapterpage.ChapterPageDao
import com.konformist.comicsreader.data.comic.Comic
import com.konformist.comicsreader.data.comic.ComicDao
import com.konformist.comicsreader.data.comiccover.ComicCover
import com.konformist.comicsreader.data.comiccover.ComicCoverDao
import com.konformist.comicsreader.data.comicoverride.ComicOverride
import com.konformist.comicsreader.data.comicoverride.ComicOverrideDao
import com.konformist.comicsreader.data.language.Language
import com.konformist.comicsreader.data.language.LanguageDao
import com.konformist.comicsreader.data.parserconfig.ParserConfig
import com.konformist.comicsreader.data.parserconfig.ParserConfigDao
import com.konformist.comicsreader.data.tag.Tag
import com.konformist.comicsreader.data.tag.TagDao

@Database(
  version = 3,
  entities = [
    Comic::class,
    ComicCover::class,
    ComicOverride::class,
    Chapter::class,
    ChapterPage::class,
    ParserConfig::class,
    Language::class,
    Author::class,
    Tag::class,
    AppFile::class
  ],
  autoMigrations = [AutoMigration(from = 2, to = 3)]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  companion object {
    const val DATABASE_NAME = "app-database"
  }

  abstract fun comicDao(): ComicDao
  abstract fun comicCoverDao(): ComicCoverDao
  abstract fun comicOverrideDao(): ComicOverrideDao
  abstract fun chapterDao(): ChapterDao
  abstract fun chapterPageDao(): ChapterPageDao
  abstract fun parserDao(): ParserConfigDao
  abstract fun tagDao(): TagDao
  abstract fun authorDao(): AuthorDao
  abstract fun languageDao(): LanguageDao
  abstract fun appFileDao(): AppFileDao
}