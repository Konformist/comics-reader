package com.konformist.comicsreader

import com.konformist.comicsreader.utils.FileManager
import org.junit.Assert
import org.junit.Test

class FileManagerTest {
  @Test
  fun getExtensionTest() {
    var extension = FileManager.getExtension("file.txt")
    Assert.assertEquals("txt", extension)

    extension = FileManager.getExtensionFromMime("image/jpeg")
    Assert.assertEquals("jpg", extension)
  }

  @Test
  fun getMimeTypeTest() {
    var mime = FileManager.getMimeType("file.webp")
    Assert.assertEquals("image/webp", mime)

    mime = FileManager.getMimeType("webp")
    Assert.assertEquals("", mime)

    mime = FileManager.getMimeType("test/comic.cbz")
    Assert.assertEquals("application/vnd.comicbook+zip", mime)

    mime = FileManager.getMimeType("test/comic.zip")
    Assert.assertEquals("application/zip", mime)
  }
}