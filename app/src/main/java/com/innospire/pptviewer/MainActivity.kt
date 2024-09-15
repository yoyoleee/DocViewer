package com.innospire.pptviewer

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.cherry.lib.doc.DocViewerActivity
import com.cherry.lib.doc.bean.DocSourceType
import com.cherry.lib.doc.bean.FileType
import com.cherry.lib.doc.util.FileUtils
import com.cherry.permissions.lib.EasyPermissions
import com.cherry.permissions.lib.EasyPermissions.hasPermissions
import com.cherry.permissions.lib.annotations.AfterPermissionGranted
import com.cherry.permissions.lib.dialogs.SettingsDialog
import com.innospire.pptviewer.util.BasicSet
import com.innospire.pptviewer.util.DocUtil
import com.innospire.pptviewer.util.WordUtils
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.content_main.mRvDoc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),OnClickListener,OnItemClickListener,
    EasyPermissions.PermissionCallbacks {
    companion object {
        const val REQUEST_CODE_STORAGE_PERMISSION = 124
        const val REQUEST_CODE_STORAGE_PERMISSION11 = 125
        const val REQUEST_CODE_SELECT_DOCUMENT = 0x100
        const val TAG = "MainActivity"
    }

    var mDocAdapter: DocAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initData()

        val plusButton: ImageButton = findViewById(R.id.plusButton)
        plusButton.setOnClickListener {
            // 使用Intent打开文件管理器并选择文档
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("*/*") // 设置要选择的文件类型，此处为任意文件类型

            startActivityForResult(intent, REQUEST_CODE_SELECT_DOCUMENT) // 启动Activity并设置请求码
        }
    }

    private fun hasRwPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val isExternalStorageManager = Environment.isExternalStorageManager()
            return isExternalStorageManager
        }
        val read = hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val write = hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return read && write
    }

    @AfterPermissionGranted(REQUEST_CODE_STORAGE_PERMISSION)
    private fun requestStoragePermission() {
        if (hasRwPermission()) {
            // Have permission, do things!
            CoroutineScope(Dispatchers.IO).launch {
                var datas = DocUtil.getDocFile(this@MainActivity)
                CoroutineScope(Dispatchers.Main).launch {
                    mDocAdapter?.showDatas(datas)
                }
            }
        } else {
            // Ask for one permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                get11Permission()
                return
            }
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your storage to load local doc",
                REQUEST_CODE_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    fun get11Permission() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(java.lang.String.format("package:%s", packageName))
            startActivityForResult(intent, REQUEST_CODE_STORAGE_PERMISSION11)
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            startActivityForResult(intent, REQUEST_CODE_STORAGE_PERMISSION11)
        }
    }

    fun initView() {
        setSupportActionBar(toolbar)

        mDocAdapter = DocAdapter(this,this)
        mRvDoc.adapter = mDocAdapter
    }

    fun initData() {
        requestStoragePermission()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }

    fun checkSupport(path: String): Boolean {
        var fileType = FileUtils.getFileTypeForUrl(path)
        Log.e(javaClass.simpleName,"fileType = $fileType")
        if (fileType == FileType.NOT_SUPPORT) {
            return false
        }
        return true
    }

    fun openDoc(path: String,docSourceType: Int,type: Int? = null) {
        DocViewerActivity.launchDocViewer(this,docSourceType,path,type)
    }

    override fun onItemClick(p0: AdapterView<*>?, v: View?, position: Int, id: Long) {
        when (v?.id) {
            R.id.mCvDocCell -> {
                val groupInfo = mDocAdapter?.datas?.get(id.toInt())
                val docInfo = groupInfo?.docList?.get(position)
                var path = docInfo?.path ?: ""
                if (checkSupport(path)) {
                    openDoc(path,DocSourceType.PATH)
                }
            }
        }
    }

    fun word2Html(sourceFilePath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val htmlFilePath = cacheDir.absolutePath + "/html"
            val htmlFileName = "word_pdf"

            var bs = BasicSet(this@MainActivity,sourceFilePath,htmlFilePath, htmlFileName)
            bs.picturePath = htmlFilePath

            WordUtils.getInstance(bs).word2html()

            CoroutineScope(Dispatchers.Main).launch {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION11) {
            if (hasRwPermission()) {
                requestStoragePermission()
            }
        } else if (requestCode == REQUEST_CODE_SELECT_DOCUMENT && resultCode == RESULT_OK) {
            val documentUri = data?.data
            Log.d(TAG, "documentUri = $documentUri")
            documentUri?.let {
                openDoc(it.toString(), DocSourceType.URI, null)
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    // ============================================================================================
    //  Implementation Permission Callbacks
    // ============================================================================================

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // 会回调 AfterPermissionGranted注解对应方法
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {

            val settingsDialogBuilder = SettingsDialog.Builder(this)

            when(requestCode) {
                REQUEST_CODE_STORAGE_PERMISSION -> {
                    settingsDialogBuilder.title = getString(
                        com.cherry.permissions.lib.R.string.title_settings_dialog,
                        "Storage Permission")
                    settingsDialogBuilder.rationale = getString(
                        com.cherry.permissions.lib.R.string.rationale_ask_again,
                        "Storage Permission")
                }
            }

            settingsDialogBuilder.build().show()
        }

    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                // Handle the business logic for KEYCODE_ENTER
                // 使用Intent打开文件管理器并选择文档
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("*/*") // 设置要选择的文件类型，此处为任意文件类型

                startActivityForResult(intent, REQUEST_CODE_SELECT_DOCUMENT) // 启动Activity并设置请求码

                return true
            }
            // Add more cases here if needed for different key codes
            else -> return super.onKeyDown(keyCode, event)
        }
    }


}