package com.libreAlexa

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.multidex.BuildConfig
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.AppUtils.providesSharedPreference
import com.cumulations.libreV2.tcp_tunneling.TunnelingControl
import com.libreAlexa.LibreApplication.CONNECTIVITY_SERVICE
import com.libreAlexa.LibreApplication.GLOBAL_TAG
import com.libreAlexa.LibreApplication.INDIVIDUAL_VOLUME_MAP
import com.libreAlexa.LibreApplication.LOCAL_IP
import com.libreAlexa.LibreApplication.PLAYBACK_HELPER_MAP
import com.libreAlexa.LibreApplication.ZONE_VOLUME_MAP
import com.libreAlexa.LibreApplication.getMYPEMstring
import com.libreAlexa.LibreApplication.isKeyAliasGenerated
import com.libreAlexa.LibreApplication.mLuciThreadInitiated
import com.libreAlexa.Scanning.ScanThread
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.alexa.MicExceptionListener
import com.libreAlexa.constants.LUCIMESSAGES.APP_INFO
import com.libreAlexa.constants.LUCIMESSAGES.ID
import com.libreAlexa.constants.LUCIMESSAGES.PHONE_MODEL
import com.libreAlexa.constants.LUCIMESSAGES.PHONE_OS_VERSION
import com.libreAlexa.constants.LUCIMESSAGES.VERSION
import com.libreAlexa.constants.LUCIMESSAGES.WIFI_IP_ADDRESS
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.luci.Utils
import com.libreAlexa.util.LibreLogger
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.Inet4Address
import java.security.NoSuchAlgorithmException
import java.util.Arrays
import java.util.Base64
import javax.crypto.KeyGenerator

class LibreEntryPoint() {
    private var appContext: Context? = null
    var scanthread: Thread? = null
    var mExecuted = false
    private var micExceptionActivityListener: MicExceptionListener? = null
    //MB 3 Changes
    private var registerData: String? = null

    companion object {
        var wt: ScanThread? = null
        private var key: String? = null
        private val TAG = LibreEntryPoint::class.java.simpleName
        private val TAG_IP = "TAG_IP"

        @Volatile
        private lateinit var instance: LibreEntryPoint
        fun getInstance(): LibreEntryPoint {
            // Log.d(GLOBAL_TAG, "LibreEntryPoint  called")
            synchronized(this) {
                if (!Companion::instance.isInitialized) {
                    instance = LibreEntryPoint()
                }
                return instance
            }
        }
    }

    fun init(context: Context) {
        appContext = context
        Log.d(TAG, "LibreEntryPoint Init on App Create Method Called")
        val connection_manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var input: InputStream? = null
        try {
            input = context.assets.open("server.pem")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //symmentrickey_of_productid.txt can't be more than 2 gigs.
        var size = 0
        try {
            if (input != null) {
                size = input.available()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val buffer = ByteArray(size)
        try {
            input?.read(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            input!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // byte buffer into a string
        var text = String(buffer)
        text = text.replace("\n".toRegex(), "")
        //Log.d(GLOBAL_TAG, "msg digest symmentric  standard product ID mavidapplication$text")
        text = text.replace("-----BEGIN CERTIFICATE-----", "")
        text = text.replace("-----END CERTIFICATE-----", "")
         Log.d(TAG, "msg digest symmentric  standard product ID EntryPoint$text")
        getMYPEMstring = text
        //Libre Code
        val request: NetworkRequest.Builder?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !mExecuted) {
            request = NetworkRequest.Builder()
            request.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            connection_manager.registerNetworkCallback(request.build(), object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        (context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager).bindProcessToNetwork(network)
                    } else {
                        ConnectivityManager.setProcessDefaultNetwork(network)
                    }
                    //Cross Checked in Moto 8.1 and OnePlus 12 with multiple networks working
                    // fine Suma has to confirm
                    val ip: String? = getIpAddress(context)
                    LOCAL_IP = if (ip != null && ip.trim().isNotEmpty()) {
                        ip
                    } else {
                        //IF the above New API code failed for safer side
                        getIpAddressOldAPI(context)
                    }
                    initLUCIServices() // New Change
                }
            })
        } else {
            //Just for safer side below 5 versions
            LOCAL_IP = getIpAddressOldAPI(context)
            initLUCIServices() // New Change
        }
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork() // or .detectAll() for all detectable problems
                .penaltyLog().build())
        }
    }

    fun initLUCIServices() {
        Log.d(TAG, "initLUCIServices Running:- $mLuciThreadInitiated isKeyAliasGenerated:- $isKeyAliasGenerated ")
        try {
            wt = ScanThread.getInstance()
            wt!!.setmContext(appContext)
            if (!mLuciThreadInitiated) {
                scanthread = Thread(wt)
                scanthread!!.start()
                // micTcpStart()
                mLuciThreadInitiated = true
            }
            if (!isKeyAliasGenerated) {
                val isFirsTime = AppUtils.getIsFirstTimeLaunch(appContext)
              ///  Log.d(GLOBAL_TAG, "IsFirstTime called $isFirsTime")
                if (!isFirsTime) {
                    generateKeyForDataStore()
                }
            }
            generateRegisterDataForMB3()
            Log.d(GLOBAL_TAG, "initLUCIServices After Setting the value  $mLuciThreadInitiated")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(GLOBAL_TAG, "initLUCIServices Exception ${e.printStackTrace()}")
        }
    }

    /**
     * clearing all collections related to application
     */
    fun clearApplicationCollections() {
        try {
            Log.d(GLOBAL_TAG, "LibreEntryPoint clearApplicationCollections() called")
            PLAYBACK_HELPER_MAP.clear()
            INDIVIDUAL_VOLUME_MAP.clear()
            ZONE_VOLUME_MAP.clear()
            TunnelingControl.clearTunnelingClients()
            LUCIControl.luciSocketMap.clear()
            LibreApplication.securecertExchangeSucessDevices.clear()
            LSSDPNodeDB.getInstance().clearDB()
            ScanningHandler.getInstance().clearSceneObjectsFromCentralRepo()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getIpAddress(context: Context): String? {
        var ipAddress: String? = null
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (connectivityManager is ConnectivityManager) {
                val linkProperties = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
                linkProperties?.let { linkProp ->
                    for (linkAddress in linkProp.linkAddresses) {
                        val inetAddress = linkAddress.address
                        if (inetAddress is Inet4Address && !inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                            ipAddress = inetAddress.getHostAddress()
                        }
                    }
                }

            } else {
                //Just for safer side
                getIpAddressOldAPI(appContext!!)
            }
        }
        return ipAddress
    }

    private fun getIpAddressOldAPI(context: Context): String {
        val ip: String?
        val wifiMan = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInf = wifiMan.connectionInfo
        val ipAddress = wifiInf.ipAddress
        ip = String.format("%d.%d.%d.%d", ipAddress and 0xff, ipAddress shr 8 and 0xff, ipAddress shr 16 and 0xff, ipAddress shr 24 and 0xff)
        return ip
    }

    fun registerForMicException(listener: MicExceptionListener) {
        micExceptionActivityListener = listener
    }

    fun unregisterMicException() {
        micExceptionActivityListener = null
    }

    private fun generateKeyForDataStore() {
        Log.d(TAG, "generateKeyForDataStore called")
        val encodedKey: String = try {
            // "AES" is the key generation algorithm, you might want to use a different one.
            val keyGen = KeyGenerator.getInstance("AES")
            // 256-bit key, you may want more or fewer bits.
            keyGen.init(256)
            val symKey = keyGen.generateKey()
            val encodedSymmetricKey = symKey.encoded
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Encode to a String, e.g. base 64 encoded
                String(Base64.getEncoder().encode(encodedSymmetricKey))
            } else {
                Arrays.toString(android.util.Base64.encode(encodedSymmetricKey, android.util.Base64.DEFAULT))
            }
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
        Log.d(TAG, "Created Encoded key is $encodedKey")
        //Storing the key into Secure Shared pref
        providesSharedPreference(appContext!!).edit().putString("", encodedKey).apply()
        AppUtils.isFirstTimeLaunch(appContext!!, true)
        isKeyAliasGenerated = true
    }

    fun getKey(): String? {
        return key
    }

    fun setKey(key_alias: String?) {
        key = key_alias
    }
    private fun generateRegisterDataForMB3() {
        try {
            val packageName: String = appContext!!.packageName
            val appVersion: String = AppUtils.getVersion(context = appContext!!)
            val phoneModel: String = Build.MANUFACTURER + Build.MODEL
            val wifiIpAddress: String = Utils().getIPAddress(true)
            val phoneOsVersion: String = Build.VERSION.SDK_INT.toString()

            val createAppJSON = JSONObject()
            createAppJSON.put(ID, packageName)
            createAppJSON.put(VERSION, appVersion)
            createAppJSON.put(PHONE_MODEL, phoneModel)
            createAppJSON.put(WIFI_IP_ADDRESS, wifiIpAddress)
            createAppJSON.put(PHONE_OS_VERSION, phoneOsVersion)
            val postData = JSONObject()
            postData.put(APP_INFO, createAppJSON)
            registerData = postData.toString()
            LibreLogger.d(GLOBAL_TAG, "postData $postData")
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

    }

    fun getRegisterMB3Data(): String? {
        return registerData
    }
}
