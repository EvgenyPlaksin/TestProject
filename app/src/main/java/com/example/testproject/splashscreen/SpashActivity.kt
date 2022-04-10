package com.example.testproject.splashscreen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.viewModels
import androidx.ads.identifier.AdvertisingIdClient
import androidx.ads.identifier.AdvertisingIdInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.example.testproject.MainActivity
import com.example.testproject.R
import com.example.testproject.game.fragments.utils.NetworkResult
import com.example.testproject.game.fragments.utils.Variables.url_2
import com.example.testproject.splashscreen.viewmodel.SplashViewModel
import com.example.testproject.webview.BrowserActivity
import com.example.testproject.webview.chack.ConnectionCheck
import com.facebook.FacebookSdk.fullyInitialize
import com.facebook.FacebookSdk.setAutoInitEnabled
import com.facebook.applinks.AppLinkData
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.Executors

@AndroidEntryPoint
class SpashActivity : AppCompatActivity() {

    val APPSFLYER_KEY = "EAh5oHTCYVhMvTpXN88pWQ"
    var subAll: Array<String> = emptyArray()// using one time for facebook and appsflier
    var adId: String? = null
    var datalist: MutableMap<String, Any>? = null
    private val viewModel by viewModels<SplashViewModel>()
    var lucky_status = "0"
    var af_status = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spash)

        val cld = ConnectionCheck(application)

        cld.observe(this) { isConnected ->

            if (isConnected) {
                printHashKey(this)
                initFacebook()
                determineAdvertisingInfo()
                appsflyer()
            }}

        if(lucky_status == "0" || af_status == "Organic") {
            val intent = Intent(this@SpashActivity, MainActivity::class.java)
            startActivity(intent)
        } else if(lucky_status == "1" || af_status == "Organic"){
            val intent = Intent(this@SpashActivity, BrowserActivity::class.java)
            startActivity(intent)
        } else if(af_status == "Non-Organic"){
            val intent = Intent(this@SpashActivity, BrowserActivity::class.java)
            startActivity(intent)
        }
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
                        if(it.key == "af_status"){
                            Log.e("TAG", "value af_status -> ${it.value}")
                            af_status = it.value.toString()
                        }

                    }
                    dataSeted = true
                }
                addDummyUser(adId.toString(), afId, "6d184139102f8c4ec4a62f423252a6fa", APPSFLYER_KEY, "945189296175257", datalist)
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
                       af_userid: String,
                       fb_at: String,
                       dev_key: String,
                       app_id: String,
                       datalist: MutableMap<String, Any>?
                    ) {
        viewModel.init()
        val builder: Uri.Builder = Uri.Builder()
        // обязательно использовать лаунч вен стартид
        lifecycleScope.launchWhenStarted {
            viewModel.response.collect { response ->
                when(response){
                    is NetworkResult.Success -> {
                        lucky_status = response?.data?.luckyStatus.toString()
                        builder.scheme("http")
                            .authority(response?.data?.luckyLink)
                            .appendQueryParameter("google_adid", google_adid)
                            .appendQueryParameter("af_userid", af_userid)
                            .appendQueryParameter("fb_at", fb_at)
                            .appendQueryParameter("dev_key", dev_key)
                            .appendQueryParameter("app_id", app_id)
                    }
                is NetworkResult.Error -> Log.e("TAG", "GET request error")
                is NetworkResult.Loading -> Log.e("TAG", "GET request loading")
                }
                    }
                }

        for(attrName in datalist!!.keys){
            if(datalist.get(attrName) != null){
                builder.appendQueryParameter(attrName, datalist.get(attrName).toString())
            }
        }
        url_2 = builder.build().toString()
    }

}