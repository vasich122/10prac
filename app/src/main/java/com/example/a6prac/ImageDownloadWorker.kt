package com.example.a6prac
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.work.Worker
import androidx.work.WorkerParameters
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream

class ImageDownloadWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val imageUrl = inputData.getString("IMAGE_URL") ?: return Result.failure()

        return runBlocking { // Запускаем корутину
            try {
                val bitmap = downloadImage(imageUrl)
                if (bitmap != null) {
                    saveImageToInternalStorage(bitmap)
                    Result.success()
                } else {
                    Result.failure()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }

    private suspend fun downloadImage(url: String): Bitmap? {
        val request = ImageRequest.Builder(applicationContext)
            .data(url)
            .size(Size.ORIGINAL)
            .build()

        val result = applicationContext.imageLoader.execute(request)
        return if (result.drawable is BitmapDrawable) {
            (result.drawable as BitmapDrawable).bitmap
        } else {
            null
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap) {
        val file = File(applicationContext.filesDir, "downloaded_image.png")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
    }
}
