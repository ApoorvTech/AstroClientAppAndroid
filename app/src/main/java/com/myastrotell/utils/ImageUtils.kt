package com.myastrotell.utils

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.myastrotell.R
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream


object ImageUtils {

    const val REQUEST_CAMERA_CAPTURE = 210
    const val REQUEST_PICK_GALLERY_IMAGE = 211

    val CAMERA_STORAGE_PERMISSSION_ARR =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val STORAGE_PERMISSSION_ARR = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)


    /**
     * method to create Alert Dialog for Camera/Gallery selection
     */
    fun showCameraGalleryDialog(
        context: Context,
        listener: ((Int) -> Unit)
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.txt_select_option))
        val items = arrayOf<CharSequence>(
            context.getString(R.string.txt_camera),
            context.getString(R.string.txt_gallery)
        )
        builder.setItems(items) { dialog, position ->
            listener.invoke(position)
            dialog?.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }


    fun getImageFile(context: Context): File? {
        val picDirFile = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (picDirFile != null) {
            val folder =
                File(picDirFile.absolutePath.plus("/").plus(context.getString(R.string.app_name)))
            if (!folder.exists()) {
                folder.mkdir()
            }

            return File.createTempFile(
                "IMG_${System.currentTimeMillis()}", /* prefix */
                ".png", /* suffix */
                folder /* directory */
            )
        }
        return null
    }


    /**
     * Method to get ImageUri for Camera Intent
     */
    fun getOutputImageUri(context: Context): Uri? {
        var imageUri: Uri? = null
        val imageFile = getImageFile(context)
        imageFile?.also {
            imageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", it)
        }

//        val values = ContentValues()
//        values.put(MediaStore.Images.Media.TITLE, imageFile.absolutePath)
//        imageUri =
//            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        return imageUri
    }


    /**
     * Get intent for capturing image from device camera
     */
    private fun getCameraIntent(context: Context, imageUri: Uri? = null): Intent {
        var outputFileUri = imageUri
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (outputFileUri == null)
            outputFileUri = getOutputImageUri(context)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
        return intent
    }


    /**
     * method to capture image from device camera
     */
    fun openCameraToCaptureImage(context: Activity?, imageUri: Uri? = null) {
        if (context != null) {
            val intent = getCameraIntent(context, imageUri)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivityForResult(intent, REQUEST_CAMERA_CAPTURE)
            }
        }
    }


    /**
     * Get intent for getting image from gallery
     */
    fun getGalleryIntent(): Intent {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        return intent
    }


    /**
     * Get all Gallery intents for getting image from one of the apps of the device that handle images.
     */
    fun getGalleryIntentsList(context: Context): List<Intent> {
        val intents = ArrayList<Intent>()

        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"

        val listGallery = context.packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            intents.add(intent)
        }

        return intents
    }


    /**
     * method to capture image from device camera
     */
    fun openGalleryToPickImage(context: Activity?) {
        if (context != null) {
            val intent = getGalleryIntent()
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivityForResult(intent, REQUEST_PICK_GALLERY_IMAGE)
            }
        }
    }


    private fun copyFileToDownloads(context: Context, file: File, name: String? = null): Uri? {
        var fileName = name
        if (fileName.isNullOrBlank()) {
            fileName = "IMG_${System.currentTimeMillis()}.png"

        }
        val resolver = context.contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    fileName
                )
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.SIZE, file.length() / 1024)
            }
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            val DOWNLOAD_DIR =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val authority = "${context.packageName}.provider"
            val destinyFile = File(DOWNLOAD_DIR, fileName)
            FileProvider.getUriForFile(context, authority, destinyFile)
        }?.also { downloadedUri ->
            resolver.openOutputStream(downloadedUri).use { outputStream ->
                val brr = ByteArray(1024)
                var len: Int
                val bufferedInputStream =
                    BufferedInputStream(FileInputStream(file.absoluteFile))
                while ((bufferedInputStream.read(brr, 0, brr.size).also { len = it }) != -1) {
                    outputStream?.write(brr, 0, len)
                }
                outputStream?.flush()
                bufferedInputStream.close()
            }
        }
    }


    /**
     * method to get real path form URI
     */
    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        val cursor: Cursor?
        var filePath: String? = ""
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            if (cursor == null) return contentUri.path
            val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor.moveToFirst()) filePath = cursor.getString(column_index)
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
            filePath = contentUri.path
        }
        return filePath
    }

}
