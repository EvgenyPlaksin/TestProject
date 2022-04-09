package com.example.testproject.splashscreen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.ads.identifier.AdvertisingIdClient
import androidx.ads.identifier.AdvertisingIdInfo
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.example.testproject.MainActivity
import com.example.testproject.R
import com.example.testproject.game.fragments.utils.Variables.url_2
import com.example.testproject.webview.BrowserActivity
import com.facebook.FacebookSdk.fullyInitialize
import com.facebook.FacebookSdk.setAutoInitEnabled
import com.facebook.applinks.AppLinkData
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.Executors


class SpashActivity : AppCompatActivity() {

    val APPSFLYER_KEY = "EAh5oHTCYVhMvTpXN88pWQ"
    var subAll: Array<String> = emptyArray()// using one time for facebook and appsflier
    var adId: String? = null
    var datalist: MutableMap<String, Any>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spash)


        printHashKey(this)
        initFacebook()
        determineAdvertisingInfo()
        appsflyer()
        addDummyUser(adId.toString(), afId, "6d184139102f8c4ec4a62f423252a6fa", APPSFLYER_KEY, "945189296175257")

        val intent = Intent(this@SpashActivity, MainActivity::class.java)
        startActivity(intent)
    }

    fun printHashKey(context: Context){
        try {
            val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val heshkey = String(Base64.encode(md.digest(), 0))
                Log.e("TAG", heshkey)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("TAG", "${e.message}")
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "${e.message}")
        }
    }

    fun initFacebook() {
        Log.e("TAG", "initfacebook is called")
        setAutoInitEnabled(true)
        fullyInitialize()

        AppLinkData.fetchDeferredAppLinkData(this) {
                appLinkData: AppLinkData? ->
            Log.e("TAG", "Aplinkdata -> $appLinkData")
            if (appLinkData != null && appLinkData.targetUri != null) {

                val fullLink = appLinkData.targetUri.toString()
                Log.e("TAG", "link -> $fullLink")
                val part1 = fullLink.split("//")
                val subsDEEP = part1[1].split("_")//sub1,sub2, sub3,sub4
                subAll[0] = subsDEEP[0]
                subAll[1] = subsDEEP[1]
                subAll[2] = subsDEEP[2]
                subAll[3] = subsDEEP[3]
            }
            Log.e("TAG", "subAll -> ${subAll[0]}")
        }

    }

    private fun determineAdvertisingInfo() {
        Log.e("TAG", "determineAdvertisingInfo is called")
        if (AdvertisingIdClient.isAdvertisingIdProviderAvailable(this)) {
            val advertisingIdInfoListenableFuture =
                AdvertisingIdClient.getAdvertisingIdInfo(applicationContext)

            Futures.addCallback(advertisingIdInfoListenableFuture,
                object : FutureCallback<AdvertisingIdInfo> {
                    override fun onSuccess(adInfo: AdvertisingIdInfo?) {
                        val id: String = adInfo?.id.toString()
                        adId = id
                        Log.e("TAG", "id -> $id")
                    }

                    override fun onFailure(t: Throwable) {
                        Log.e(
                            "MY_APP_TAG",
                            "Failed to connect to Advertising ID provider."
                        )
                        // Try to connect to the Advertising ID provider again, or fall
                        // back to an ads solution that doesn't require using the
                        // Advertising ID library.
                    }
                }, Executors.newSingleThreadExecutor()
            )
        } else {
            // The Advertising ID client library is unavailable. Use a different
            // library to perform any required ads use cases.
        }
    }

    var appCampaign = "null"
    var dataSeted = false
    var afId = "null"
    fun appsflyer(){
        Log.e("TAG", "appsflyer is called")
        AppsFlyerLib.getInstance().init(APPSFLYER_KEY,createData(),this)
        AppsFlyerLib.getInstance().start(this)
    }

    fun createData(): AppsFlyerConversionListener {
        afId = AppsFlyerLib.getInstance().getAppsFlyerUID(this).toString()
        Log.e("TAG", "adId -> $adId")
        return object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {

               datalist = data

                data?.let {
                    for (it in data) {

                        if (it.key == "campaign") {
                            appCampaign = it.value.toString()
                            Log.e("TAG", "value campaign -> ${it.value}")
                            val subsNaming=appCampaign.split("_")//sub1 , sub2 , sub3 , sub4
                            subAll[0]=subsNaming[0]
                            subAll[1]=subsNaming[1]
                            subAll[2]=subsNaming[2]
                            subAll[3]=subsNaming[3]
                        }

                    }
                    dataSeted = true
                }
            }
            override fun onConversionDataFail(error: String?) {
                Log.e("TAG", "onConversionDataFail called")
                if (!dataSeted) dataSeted = true
            }
            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                Log.e("TAG", "onAppOpenAttribution called")
                data?.map {
                    if (!dataSeted) dataSeted = true
                }
            }
            override fun onAttributionFailure(error: String?) {
                Log.e("TAG", "onAttributionFailure called")
                if (!dataSeted) dataSeted = true
            }
        }
    }


    fun addDummyUser(  google_adid: String,
                       af_urerid: String,
                       fb_at: String,
                       dev_key: String,
                       app_id: String
//                       datalist: MutableMap<String, Any>?
                    ) {
        val builder: Uri.Builder = Uri.Builder()
        builder.scheme("http")
            .authority("185.198.165.10")
            .appendPath("dimas")
            .appendQueryParameter("google_adid", google_adid)
            .appendQueryParameter("af_userid", af_urerid)
            .appendQueryParameter("fb_at", fb_at)
            .appendQueryParameter("dev_key", dev_key)
            .appendQueryParameter("app_id", app_id)
//
//        for(attrName in datalist!!.keys){
//            if(datalist.get(attrName) != null){
//                builder.appendQueryParameter(attrName, datalist.get(attrName).toString())
//            }
//        }
        url_2 = builder.build().toString()
    }

}