package com.example.testproject.splashscreen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import androidx.ads.identifier.AdvertisingIdClient
import androidx.ads.identifier.AdvertisingIdInfo
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.example.testproject.*
import com.example.testproject.game.fragments.utils.Constants
import com.facebook.applinks.AppLinkData
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.Executors

class SpashActivity : AppCompatActivity() {

    val APPSFLYER_KEY = "EAh5oHTCYVhMvTpXN88pWQ"
    var subAll: Array<String> = arrayOf("null","null","null","null")// using one time for facebook and appsflier
    var adId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spash)


        printHashKey(this)
        initFacebook()
        determineAdvertisingInfo()
        appsflyer()

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
        Log.e("TAG", "initfaebook is called")
        AppLinkData.fetchDeferredAppLinkData(this) {

                appLinkData: AppLinkData? ->
            Log.e("TAG", "Aplinkdata -> $appLinkData")
            if (appLinkData != null && appLinkData.targetUri != null) {

                val FullLinkaa = appLinkData.targetUri.toString()
                Log.e("TAG", "link -> $FullLinkaa")
                val part1 = FullLinkaa.split("//")
                val subsDEEP = part1[1].split("_")//sub1,sub2, sub3,sub4
                subAll[0] = subsDEEP[0]
                subAll[1] = subsDEEP[1]
                subAll[2] = subsDEEP[2]
                subAll[3] = subsDEEP[3]
            }
        }
        Log.e("TAG", "subAll -> ${subAll[0]}")
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
    var adgroup = "null"
    var afSteid = "null"
    var affStuta = "null"
    var origCost = "null"
    var campaignId = "null"
    var afId = "null"
    var adsetId = "null"
    var appCampaign = "null"
    var adset = "null"
    var source = "null"
    var dataSeted = false

    fun appsflyer(){
        Log.e("TAG", "appsflyer is called")
        AppsFlyerLib.getInstance().init(APPSFLYER_KEY,createData(),this)
        AppsFlyerLib.getInstance().start(this)
    }

    fun createData(): AppsFlyerConversionListener {
        afId = AppsFlyerLib.getInstance().getAppsFlyerUID(this).toString()
        Log.e("TAG", "id -> $adId")
        return object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                data?.let {
                    for (it in data) {
                        if (it.key == "af_status") {
                            affStuta = it.value.toString()
                        }
                        if (it.value == null) {
                            continue
                        }
                        if (it.key == "media_source") {
                            source = it.value.toString()
                        }
                        if (it.key == "adgroup_id") {
                            adId = it.value.toString()
                        }
                        if (it.key == "adset_id") {
                            adsetId = it.value.toString()
                        }
                        if (it.key == "campaign_id") {
                            campaignId = it.value.toString()
                        }
                        if (it.key == "campaign") {
                            appCampaign = it.value.toString()
                            val subsNaming=appCampaign.split("_")//sub1 , sub2 , sub3 , sub4
                            subAll[0]=subsNaming[0]
                            subAll[1]=subsNaming[1]
                            subAll[2]=subsNaming[2]
                            subAll[3]=subsNaming[3]
                        }
                        if (it.key == "adset") {
                            adset = it.value.toString()
                        }
                        if (it.key == "adgroup") {
                            adgroup = it.value.toString()
                        }
                        if (it.key == "orig_cost") {
                            origCost = it.value.toString()
                        }
                        if (it.key == "af_siteid") {
                            afSteid = it.value.toString()
                        }
                    }
                    Log.e("TAG", "appsflyer -> ${it["af_status"]}")
                    dataSeted = true
                }
            }
            override fun onConversionDataFail(error: String?) {
                if (!dataSeted) {
                    dataSeted = true
                }
            }
            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                data?.map {
                    if (!dataSeted) {
                        dataSeted = true
                    }
                }
            }
            override fun onAttributionFailure(error: String?) {
                if (!dataSeted) {
                    dataSeted = true
                }
            }
        }
    }

}