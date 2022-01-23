package com.codewithdimi.app_template.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


const val ShareImageFolderName = "sharedimages" //check res/xml/filepaths. should be the same
const val ShareImageName = "shareimage.jpg"

fun saveImageToInternalStorage(context: Context, bitmap: Bitmap): Boolean {
    return try {
        val cachePath = File(context.cacheDir, ShareImageFolderName)
        cachePath.mkdirs() // don't forget to make the directory
        val stream =
            FileOutputStream("$cachePath/$ShareImageName") // overwrites this image every time
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.close()
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

fun shareImage(activity: Activity, bitmap: Bitmap) {
    if(!saveImageToInternalStorage(activity, bitmap))
        return

    val imagePath = File(activity.cacheDir, ShareImageFolderName)
    val newFile = File(imagePath, ShareImageName)
    val contentUri: Uri? =
        FileProvider.getUriForFile(activity, "com.codewithdimi.app_template .fileprovider", newFile)

    if (contentUri != null) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
        shareIntent.setDataAndType(contentUri, activity.contentResolver.getType(contentUri))
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        activity.startActivity(Intent.createChooser(shareIntent, null))
    }
}