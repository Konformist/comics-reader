package com.konformist.comicsreader.appfile

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.konformist.comicsreader.data.appfile.AppFileController
import com.konformist.comicsreader.data.appfile.AppFileCreate
import com.konformist.comicsreader.data.appfile.AppFileDao
import com.konformist.comicsreader.data.appfile.AppFileDelete
import com.konformist.comicsreader.data.appfile.AppFileUpdate
import com.konformist.comicsreader.db.AppDatabase
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AppFilesControllerTest {
  private lateinit var db: AppDatabase
  private lateinit var appFileDao: AppFileDao
  private lateinit var controller: AppFileController
//  private lateinit var mockFileManager: MockedStatic<FileManager>

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

//    mockFileManager = Mockito.mockStatic(FileManager::class.java)
    appFileDao = db.appFileDao()
    controller = AppFileController(appFileDao)
  }

  @Test
  fun testCreateAppFile() {
    // Создаем объект для вставки
    val appFileCreate = AppFileCreate(
      name = "TestFile",
      mime = "image/jpeg",
      size = 12345,
      path = "test/path"
    )

    // Вызываем метод контроллера для создания записи
    val rowId = controller.create(appFileCreate)

    // Получаем запись из базы данных по ID
    val savedFile = appFileDao.read(rowId)

    // Проверяем, что запись была сохранена корректно
    Assert.assertNotNull(savedFile)
    Assert.assertEquals("TestFile", savedFile?.name)
    Assert.assertEquals("image/jpeg", savedFile?.mime)
    Assert.assertEquals(12345L, savedFile?.size)
    Assert.assertEquals("test/path", savedFile?.path)
  }

  @Test
  fun testUpdateAppFile() {
    // Создаем объект для вставки
    val appFileCreate = AppFileCreate(
      name = "TestFile",
      mime = "image/jpeg",
      size = 12345,
      path = "test/path"
    )
    val rowId = controller.create(appFileCreate)
    val row = appFileDao.read(rowId)

    // Обновляем объект
    val appFileUpdate = AppFileUpdate(
      id = rowId,
      mdate = "",
      name = "UpdatedTestFile",
      mime = "image/png",
      size = 54321,
      path = "updated/path"
    )

    Thread.sleep(1000)
    // Вызываем метод для обновления
    controller.update(appFileUpdate)

    // Получаем обновленную запись
    val updatedFile = appFileDao.read(rowId)

    // Проверяем, что данные были обновлены корректно
    Assert.assertNotNull(updatedFile)
    Assert.assertEquals(row?.cdate, updatedFile?.cdate)
    Assert.assertNotEquals("", updatedFile?.mdate)
    Assert.assertNotEquals(row?.mdate, updatedFile?.mdate)
    Assert.assertEquals("UpdatedTestFile", updatedFile?.name)
    Assert.assertEquals("image/png", updatedFile?.mime)
    Assert.assertEquals(54321L, updatedFile?.size)
    Assert.assertEquals("updated/path", updatedFile?.path)
  }

  @Test
  fun testDeleteAppFile() {
    // Создаем объект для вставки
    val appFileCreate = AppFileCreate(
      name = "TestFile",
      mime = "image/jpeg",
      size = 12345,
      path = "test/path"
    )
    val rowId = controller.create(appFileCreate)

    // Удаляем объект
    val deleted = controller.delete(AppFileDelete(id = rowId))

    // Проверяем, что объект был удален
    Assert.assertTrue(deleted)
    val deletedFile = appFileDao.read(rowId)
    Assert.assertNull(deletedFile)
  }

//  @Test
//  fun testCreateImage() {
//    val mime = "image/png"
//    val fileContent = "dummy image content".toByteArray()
//    val inputStream: InputStream = ByteArrayInputStream(fileContent)
//    val mockComicsDir = File("mock/comics")
//    val mockFile = File(mockComicsDir, "1.png")
//
//    mockFileManager.use { staticMock ->
//      Mockito.`when`(FileManager.comicsImagesDir).thenReturn(mockComicsDir)
//      Mockito.`when`(FileManager.getExtensionFromMime(mime)).thenReturn("png")
//      Mockito.`when`(mockComicsDir.exists()).thenReturn(true)
//      Mockito.doNothing().`when`(FileManager).writeStream(Mockito.eq(mockFile), Mockito.any(InputStream::class.java))
//
//      val rowId = controller.createImage(mime, inputStream)
//
//      staticMock.verify { FileManager.writeStream(Mockito.eq(mockFile), Mockito.any(InputStream::class.java)) }
//
//      val savedFile = appFileDao.read(rowId)
//      assertNotNull(savedFile)
//      assertEquals("$rowId.png", savedFile?.name)
//      assertEquals(mime, savedFile?.mime)
//    }
//  }


  @After
  fun tearDown() {
    db.close()
  }
}