package com.konformist.comicsreader.webapi

class Query {
  companion object {
    const val TAG_TAG_LIST = "tag/tag/list"
    const val TAG_TAG_GET = "tag/tag/get"
    const val TAG_TAG_ADD = "tag/tag/add"
    const val TAG_TAG_SET = "tag/tag/set"
    const val TAG_TAG_DEL = "tag/tag/del"

    const val AUTHOR_AUTHOR_LIST = "author/author/list"
    const val AUTHOR_AUTHOR_GET = "author/author/get"
    const val AUTHOR_AUTHOR_ADD = "author/author/add"
    const val AUTHOR_AUTHOR_SET = "author/author/set"
    const val AUTHOR_AUTHOR_DEL = "author/author/del"

    const val LANGUAGE_LANGUAGE_LIST = "language/language/list"
    const val LANGUAGE_LANGUAGE_GET = "language/language/get"
    const val LANGUAGE_LANGUAGE_ADD = "language/language/add"
    const val LANGUAGE_LANGUAGE_SET = "language/language/set"
    const val LANGUAGE_LANGUAGE_DEL = "language/language/del"

    const val PARSER_PARSER_LIST = "parser/parser/list"
    const val PARSER_PARSER_GET = "parser/parser/get"
    const val PARSER_PARSER_ADD = "parser/parser/add"
    const val PARSER_PARSER_SET = "parser/parser/set"
    const val PARSER_PARSER_DEL = "parser/parser/del"

    const val COMIC_COMIC_LIST = "comic/comic/list"
    const val COMIC_COMIC_GET = "comic/comic/get"
    const val COMIC_COMIC_ADD = "comic/comic/add"
    const val COMIC_COMIC_SET = "comic/comic/set"
    const val COMIC_COMIC_DEL = "comic/comic/del"
    const val COMIC_COMIC_UPLOAD = "comic/comic/upload"
    const val COMIC_ARCHIVE_ADD = "comic/archive/add"

    const val COMIC_OVERRIDE_GET = "comic/override/get"
    const val COMIC_OVERRIDE_SET = "comic/override/set"

    const val CHAPTER_CHAPTER_LIST = "chapter/chapter/list"
    const val CHAPTER_CHAPTER_GET = "chapter/chapter/get"
    const val CHAPTER_CHAPTER_ADD = "chapter/chapter/add"
    const val CHAPTER_CHAPTER_SET = "chapter/chapter/set"
    const val CHAPTER_CHAPTER_DEL = "chapter/chapter/del"

    const val CHAPTER_PAGE_LIST = "chapter/page/list"
    const val CHAPTER_PAGE_GET = "chapter/page/get"
    const val CHAPTER_PAGE_ADD = "chapter/page/add"
    const val CHAPTER_PAGE_SET = "chapter/page/set"
    const val CHAPTER_PAGE_DEL = "chapter/page/del"

    const val FILE_COMIC_COVER_DOWNLOAD = "file/comic-cover/download"
    const val FILE_COMIC_COVER_ADD = "file/comic-cover/add"
    const val FILE_COMIC_COVER_DEL = "file/comic-cover/del"
    const val FILE_CHAPTER_PAGE_DOWNLOAD = "file/chapter-page/download"
    const val FILE_CHAPTER_PAGE_ADD = "file/chapter-page/add"
    const val FILE_CHAPTER_PAGE_DEL = "file/chapter-page/del"

    const val FILE_FILES_TREE = "file/files/tree"
    const val FILE_FILE_DOWNLOADS = "file/file/downloads"

    const val SETTINGS_SETTINGS_GET = "settings/settings/get"
    const val SETTINGS_SETTINGS_SET = "settings/settings/set"

    const val BACKUP_BACKUP_ADD = "backup/backup/add"
    const val BACKUP_BACKUP_RESTORE = "backup/backup/restore"

    const val PARSER_HTML_DOWNLOAD = "parser/html/download"
    const val DATA_DATA_MIGRATE = "data/data/migrate"
  }
}