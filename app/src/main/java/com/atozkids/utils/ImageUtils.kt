package com.atozkids.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.atozkids.R
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


object ImageUtils {
    fun showImageFromPath(imagePath: String, imageView: ImageView) {
        Glide.with(imageView.context).load(imagePath).into(imageView)
    }

    fun showImageFromFile(imageFile: File, imageView: ImageView) {
        Glide.with(imageView.context).load(imageFile).into(imageView)
    }

    fun showLoaderImage(imageView: ImageView) {
        Glide.with(imageView.context).asGif().load(R.drawable.ic_loader).into(imageView)
    }

    fun showBitmap(imageView: ImageView, bitmap: Bitmap) {
        Glide.with(imageView.context).load(bitmap).into(imageView)
    }

    private fun decodeFile(f: File): Bitmap? {
        try {
            //decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)

            //Find the correct scale value. It should be the power of 2.
            val REQUIRED_SIZE = 100
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) break
                width_tmp /= 2
                height_tmp /= 2
                scale *= 2
            }

            //decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
        } catch (e: FileNotFoundException) {
        }
        return null
    }
}
