package com.innospire.pptviewer.util

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import com.innospire.pptviewer.data.DocGroupInfo
import com.innospire.pptviewer.data.DocInfo
import com.cherry.lib.doc.bean.FileType
import com.cherry.lib.doc.util.FileUtils
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DocUtil
 * Author: Victor
 * Date: 2023/10/26 10:16
 * Description: 
 * -----------------------------------------------------------------
 */

object DocUtil {
    private val TAG = "DocUtil"

    fun getDocFile(context: Context): ArrayList<DocGroupInfo> {
        val docGroupList = ArrayList<DocGroupInfo>()
        val pptList = ArrayList<DocInfo>()

        // Get Downloads directory
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val files = downloadsDir.listFiles() ?: return docGroupList

        // Iterate over files and filter by extension
        for (file in files) {
            if (file.isFile) {
                val fileName = file.name
                val path = file.absolutePath
                val lastModified = file.lastModified()

                // Log file information
                Log.v(TAG, "fileName = $fileName")
                Log.v(TAG, "path = $path")
                Log.v(TAG, "lastModified = ${stampToDate(lastModified)}")

                val item = DocInfo().apply {
                    this.fileName = fileName
                    this.path = path
                    this.lastModified = stampToDate(lastModified)
                }

                // Determine file type based on extension
                if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
                    pptList.add(item)
                }
            }
        }

        docGroupList.add(DocGroupInfo("PPT & PPTX", pptList))
        return docGroupList
    }
    private fun stampToDate(timestamp: Long): String {
        // Convert timestamp to date string
        return java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
    }
}