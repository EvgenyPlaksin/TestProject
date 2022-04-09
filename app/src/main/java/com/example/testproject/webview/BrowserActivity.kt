package com.example.testproject.webview

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.testproject.R
import com.example.testproject.game.fragments.utils.Variables.FILECHOOSER_RESULTCODE
import com.example.testproject.game.fragments.utils.Variables.REQUEST_SELECT_FILE
import com.example.testproject.game.fragments.utils.Variables.fileornot
import com.example.testproject.game.fragments.utils.Variables.mUploadMessage
import com.example.testproject.game.fragments.utils.Variables.uploadMessage
import com.example.testproject.game.fragments.utils.Variables.url_2
import com.example.testproject.webview.chack.ConnectionCheck
import com.example.testproject.webview.client.WebClient
import kotlinx.android.synthetic.main.activity_browser.*

class BrowserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        val cld = ConnectionCheck(application)

        cld.observe(this) { isConnected ->

            if (isConnected) {

                textView.visibility = View.INVISIBLE

                val pmmCookies: CookieManager = CookieManager.getInstance()
                CookieManager.setAcceptFileSchemeCookies(true)
                pmmCookies.setAcceptThirdPartyCookies(web_browser, true)
                web_browser.settings.apply {
                    useWideViewPort = true
                    javaScriptEnabled = true
                    mixedContentMode = 0
                    loadWithOverviewMode = true
                    allowFileAccess = true
                    domStorageEnabled = true
                    defaultTextEncodingName = "utf-8"
                    databaseEnabled = true
                    allowFileAccessFromFileURLs = true
                    setAppCacheEnabled(true)
                    javaScriptCanOpenWindowsAutomatically = true
                }
                if(!fileornot) {
                    if (url_2 != null) {
                        web_browser.loadUrl("$url_2")
                    } else {
                        web_browser?.loadUrl("https://parimatch.com/")
                    }
                }
                web_browser?.settings?.javaScriptEnabled = true // we need to enable javascript
                web_browser?.canGoBack()
                web_browser?.webViewClient = WebClient(this)
                web_browser?.setWebChromeClient(object : WebChromeClient() {

                    override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                        Log.d("alert", message)
                        val dialogBuilder = AlertDialog.Builder(this@BrowserActivity)

                        dialogBuilder.setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK") { _, _ ->
                                result.confirm()
                            }

                        val alert = dialogBuilder.create()
                        alert.show()

                        return true
                    }

                    // For 3.0+ Devices (Start)
                    // onActivityResult attached before constructor
                    fun openFileChooser(uploadMsg : ValueCallback<Uri>, acceptType:String) {
                        mUploadMessage = uploadMsg
                        val i = Intent(Intent.ACTION_GET_CONTENT)
                        i.addCategory(Intent.CATEGORY_OPENABLE)
                        i.type = "*/*"
                        startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE)
                    }

                    // For Lollipop 5.0+ Devices
                    override fun onShowFileChooser(mWebView:WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams:WebChromeClient.FileChooserParams):Boolean {
                        fileornot = true
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            if (uploadMessage != null) {
                                uploadMessage?.onReceiveValue(null)
                                uploadMessage = null
                            }
                            uploadMessage = filePathCallback
                            val intent = fileChooserParams.createIntent()
                            try {
                                startActivityForResult(intent, REQUEST_SELECT_FILE)
                            } catch (e: ActivityNotFoundException) {
                                uploadMessage = null
                                Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show()
                                return false
                            }
                            return true
                        }else{
                            return false
                        }
                    }

                    //For Android 4.1 only
                    fun openFileChooser(uploadMsg:ValueCallback<Uri>, acceptType:String, capture:String) {
                        mUploadMessage = uploadMsg
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        intent.type = "*/*"
                        startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE)
                    }

                    fun openFileChooser(uploadMsg:ValueCallback<Uri>) {
                        mUploadMessage = uploadMsg
                        val i = Intent(Intent.ACTION_GET_CONTENT)
                        i.addCategory(Intent.CATEGORY_OPENABLE)
                        i.type = "*/*"
                        startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE)
                    }

                })

            } else textView.visibility = View.VISIBLE
        }

    }

    override fun onBackPressed() {
        if (web_browser.canGoBack()) {
            web_browser.goBack()
        } else {
            Log.d("TAG", "can`t go back")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if(requestCode == REQUEST_SELECT_FILE){
                if(uploadMessage != null){
                    uploadMessage?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode,intent))
                    uploadMessage = null
                }
            }
        }else if(requestCode == FILECHOOSER_RESULTCODE){
            if(mUploadMessage!=null){
                var result = intent?.data
                mUploadMessage?.onReceiveValue(result)
                mUploadMessage = null
            }
        }else{
            Toast.makeText(this,"Failed to open file uploader, please check app permissions.",Toast.LENGTH_LONG).show()
            super.onActivityResult(requestCode, resultCode, intent)
        }
    }

}
