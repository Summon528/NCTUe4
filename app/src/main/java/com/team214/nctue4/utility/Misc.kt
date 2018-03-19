package com.team214.nctue4.utility

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.team214.nctue4.R
import java.io.File

fun downloadFile(fileName: String, uri: String, context: Context, activity: Activity,
                 requestPermissions: (() -> Unit?)?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
        requestPermissions?.invoke()
    } else {
        val path = activity.getExternalFilesDir(null).toString()
        val dir = File(path + File.separator + "Download")
        if (!dir.exists()) dir.mkdirs()
        val file = File(path + File.separator + "Download" + File.separator + fileName)
        if (file.exists()) {
            AlertDialog.Builder(context)
                    .setMessage(context.getString(R.string.detect_same_file))
                    .setPositiveButton(R.string.download_again, { _, _ ->
                        file.delete()
                        downloadFile(fileName, uri, context, activity, requestPermissions)
                    })
                    .setNegativeButton(R.string.open_existed, { _, _ ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        val extension = MimeTypeMap.getFileExtensionFromUrl(fileName)
                        val type = if (extension != null) {
                            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                        } else null
                        val fileUri = FileProvider.getUriForFile(context,
                                context.applicationContext.packageName +
                                ".com.team214", file)
                        intent.setDataAndType(fileUri, type)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        activity.startActivity(intent)

                    })
                    .show()
        } else {
            val request = DownloadManager.Request(Uri.parse(uri))
            request.setTitle(fileName)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            request.setDestinationUri(Uri.fromFile(file))
            request.setVisibleInDownloadsUi(true)
            Toast.makeText(context, R.string.download_start, Toast.LENGTH_SHORT).show()
            manager.enqueue(request)
        }
    }
}