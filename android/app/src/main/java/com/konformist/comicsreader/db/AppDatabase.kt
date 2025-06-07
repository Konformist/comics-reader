package com.konformist.comicsreader.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.konformist.comicsreader.db.appfile.AppFile
import com.konformist.comicsreader.db.appfile.AppFileDao
import com.konformist.comicsreader.db.author.Author
import com.konformist.comicsreader.db.author.AuthorDao
import com.konformist.comicsreader.db.chapter.Chapter
import com.konformist.comicsreader.db.chapter.ChapterDao
import com.konformist.comicsreader.db.chapterpage.ChapterPage
import com.konformist.comicsreader.db.chapterpage.ChapterPageDao
import com.konformist.comicsreader.db.comic.Comic
import com.konformist.comicsreader.db.comic.ComicDao
import com.konformist.comicsreader.db.comiccover.ComicCover
import com.konformist.comicsreader.db.comiccover.ComicCoverDao
import com.konformist.comicsreader.db.comicoverride.ComicOverride
import com.konformist.comicsreader.db.comicoverride.ComicOverrideDao
import com.konformist.comicsreader.db.language.Language
import com.konformist.comicsreader.db.language.LanguageDao
import com.konformist.comicsreader.db.parser.ParserConfig
import com.konformist.comicsreader.db.parser.ParserDao
import com.konformist.comicsreader.db.tag.Tag
import com.konformist.comicsreader.db.tag.TagDao

@Database(
  version = 2,
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
  autoMigrations = [AutoMigration(from = 1, to = 2)]
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
  abstract fun parserDao(): ParserDao
  abstract fun tagDao(): TagDao
  abstract fun authorDao(): AuthorDao
  abstract fun languageDao(): LanguageDao
  abstract fun appFileDao(): AppFileDao
}