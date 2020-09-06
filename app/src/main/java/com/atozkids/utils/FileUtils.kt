package com.atozkids.utils

import android.os.Environment
import com.atozkids.app.App
import com.atozkids.services.NewFileDownloaderService
import java.io.File

object FileUtils {
    private lateinit var cachedFile: File
    fun getPhotoFile(fileName: String): File {
        if (Environment.getExternalStorageState() == "mounted") {
            this.cachedFile = File(App.getAppContext().externalCacheDir.toString() + "/.data")
        } else {
            this.cachedFile = File(App.getAppContext().cacheDir.toString() + "/.data")
        }

        if (!this.cachedFile.exists()) {
            this.cachedFile.mkdir()
        }
        val file =
            File(this.cachedFile.absolutePath + "/" + fileName.replace(" ", "").trim() + ".jpg")
        file.parentFile.mkdirs()
        return file
    }

    fun getSoundFile(fileName: String): File {
        if (Environment.getExternalStorageState() == "mounted") {
            this.cachedFile = File(App.getAppContext().externalCacheDir.toString() + "/.data")
        } else {
            this.cachedFile = File(App.getAppContext().cacheDir.toString() + "/.data")
        }

        if (!this.cachedFile.exists()) {
            this.cachedFile.mkdir()
        }
        val file =
            File(this.cachedFile.absolutePath + "/" + fileName.replace(" ", "").trim() + ".mp3")
        file.parentFile.mkdirs()
        return file
    }

    fun getRealPhotoFile(fileName: String): File {
        return File(
            Environment.getExternalStorageDirectory().absolutePath + NewFileDownloaderService.APP_FOLDER_NAME,
            fileName.replace(" ", "").trim() + ".jpg"
        )
    }

    fun getRealSoundFile(fileName: String): File {
        return File(
            Environment.getExternalStorageDirectory().absolutePath + NewFileDownloaderService.APP_FOLDER_NAME,
            fileName.replace(" ", "").trim() + ".mp3"
        )
    }
}


