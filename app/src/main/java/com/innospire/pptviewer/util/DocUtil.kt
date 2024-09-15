package com.innospire.pptviewer.util

import android.content.Context
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
        val ppt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt")
        val pptx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx")
        //Table
        //val table = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val table = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        //Column
        val column = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.ALBUM
        )
        //Where
        val selection = (MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?")
        //args
        val selectionArgs = arrayOf(ppt, pptx)
        val fileCursor = context.contentResolver.query(table, column, selection, selectionArgs, null)

        val displayNameKey = fileCursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        val dataKey = fileCursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        val lastModifiedKey = fileCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
        val albumKey = fileCursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)

        val docGroupList = ArrayList<DocGroupInfo>()
        val pptList = ArrayList<DocInfo>()

        while (fileCursor.moveToNext()) {
            val fileName = fileCursor.getString(displayNameKey)
            val path = fileCursor.getString(dataKey)
            val album = fileCursor.getString(albumKey)
            val lastModified = fileCursor.getLong(lastModifiedKey)
            Log.v(TAG,"fileName = $fileName")
            Log.v(TAG,"path = $path")
            Log.v(TAG,"lastModified = ${stampToDate(lastModified * 1000)}")

            var item = DocInfo()
            item.album = album
            item.fileName = fileName
            item.path = path
            item.lastModified = stampToDate(lastModified * 1000)

            var fileType = FileUtils.getFileTypeForUrl(path)
            if (fileType == FileType.PPT || fileType == FileType.PPTX) {
                pptList.add(item)
            }

        }

        docGroupList.add(DocGroupInfo("PPT & PPTX",pptList))

        return docGroupList
    }

    /*
     * 将时间戳转换为时间
     */
    fun stampToDate(s: Long): String? {
        var res: String? = null
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
            val date = Date(s)
            res = simpleDateFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return res
    }

}