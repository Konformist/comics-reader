package com.konformist.comicsreader.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.konformist.comicsreader.db.author.Author
import com.konformist.comicsreader.db.author.AuthorDao
import com.konformist.comicsreader.db.comic.Chapter
import com.konformist.comicsreader.db.comic.ChapterDao
import com.konformist.comicsreader.db.comic.ChapterPage
import com.konformist.comicsreader.db.comic.ChapterPageDao
import com.konformist.comicsreader.db.comic.Comic
import com.konformist.comicsreader.db.comic.ComicCover
import com.konformist.comicsreader.db.comic.ComicCoverDao
import com.konformist.comicsreader.db.comic.ComicDao
import com.konformist.comicsreader.db.comic.ComicOverride
import com.konformist.comicsreader.db.comic.ComicOverrideDao
import com.konformist.comicsreader.db.file.File
import com.konformist.comicsreader.db.file.FileDao
import com.konformist.comicsreader.db.language.Language
import com.konformist.comicsreader.db.language.LanguageDao
import com.konformist.comicsreader.db.parser.Parser
import com.konformist.comicsreader.db.parser.ParserDao
import com.konformist.comicsreader.db.tag.Tag
import com.konformist.comicsreader.db.tag.TagDao

@Database(
  version = 1,
  entities = [
    Comic::class,
    ComicCover::class,
    ComicOverride::class,
    Chapter::class,
    ChapterPage::class,
    Parser::class,
    Language::class,
    Author::class,
    Tag::class,
    File::class
  ],
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun comicDao(): ComicDao
  abstract fun comicCoverDao(): ComicCoverDao
  abstract fun comicOverrideDao(): ComicOverrideDao
  abstract fun chapterDao(): ChapterDao
  abstract fun chapterPageDao(): ChapterPageDao
  abstract fun parserDao(): ParserDao
  abstract fun tagDao(): TagDao
  abstract fun authorDao(): AuthorDao
  abstract fun languageDao(): LanguageDao
  abstract fun fileDao(): FileDao
}