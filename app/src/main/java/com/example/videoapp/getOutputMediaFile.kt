package com.example.videoapp

import android.annotation.SuppressLint
import android.os.Environment
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
private fun getOutputMediaFile(type: Int): File? {
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.
    val mediaStorageDir = File(
        Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ), "MyCameraApp"
    )
    // This location works best if you want the created images to be shared
    // between applications and persist after your app has been uninstalled.

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists()) {
        if (!mediaStorageDir.mkdirs()) {
            return null
        }
    }

    // Create a media file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val mediaFile: File
    if (type == MEDIA_TYPE_IMAGE) {
        mediaFile = File(
            mediaStorageDir.getPath() + File.separator.toString() +
                    "IMG_" + timeStamp + ".jpg"
        )
    } else if (type == MEDIA_TYPE_VIDEO) {
        mediaFile = File(
            mediaStorageDir.getPath() + File.separator.toString() +
                    "VID_" + timeStamp + ".mp4"
        )
    } else {
        return null
    }
    return mediaFile
}
