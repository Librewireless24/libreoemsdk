package com.libreAlexa;
/*********************************************************************************************
 * Copyright (C) 2014 Libre Wireless Technology
 * <p/>
 * "Junk Yard Lab" Project
 * <p/>
 * Libre Sync Android App
 * Author: Subhajeet Roy
 ***********************************************************************************************/



import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.AppLaunchChecker;

import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDex;
import com.cumulations.libreV2.AppUtils;
import com.cumulations.libreV2.receiver.GpsStateReceiver;
import com.cumulations.libreV2.tcp_tunneling.TunnelingControl;
import com.libreAlexa.Ls9Sac.FwUpgradeData;
import com.libreAlexa.Scanning.ScanThread;
import com.libreAlexa.Scanning.ScanningHandler;
import com.libreAlexa.alexa.MicExceptionListener;
import com.libreAlexa.alexa.MicTcpServer;
import com.libreAlexa.app.dlna.dmc.utility.PlaybackHelper;
import com.libreAlexa.luci.LSSDPNodeDB;
import com.libreAlexa.luci.LUCIControl;
import com.libreAlexa.util.GoogleTOSTimeZone;
import com.libreAlexa.util.LibreLogger;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.fourthline.cling.controlpoint.ControlPoint;


public class LibreApplication extends Application implements
    MicTcpServer.MicTcpServerExceptionListener {

  public static boolean mCleanUpIsDoneButNotRestarted = false;
  public static boolean isWifiON = false;
  public static InputStream openRawandroidp12;
  public static boolean isUSBSrc=false;
  public static final HashMap<String, String> fnFlowPassed = new HashMap<>();
  public static final HashMap<String, String> nettyActiveWriteDevices = new HashMap<>();

  public static  HashMap<String, String> securecertExchangeSucessDevices = new HashMap<>();
  public static String getMYPEMstring = "";
  public static Activity ContextActivity;
  public static int disconnectedCount = 0, betweenDisconnectedCount = 0;
  public static boolean noReconnectionRuleToApply = false;
  public static boolean GOOGLE_TOS_ACCEPTED = false;
  public static String thisSACDeviceNeeds226 = "";
  public static String sacDeviceNameSetFromTheApp = "";
  public static boolean scanAlreadySent = false;

  public static boolean mLuciThreadInitiated = false;
  public static boolean isKeyAliasGenerated = false;
  public static boolean isPasswordEmpty = false;

  public static boolean isSacFlowStarted = false;
  public static boolean isBackPressed = false;

  public static boolean hideErrorMessage;
  private static final String TAG = LibreApplication.class.getSimpleName();

  public static String activeSSID;
  public static String mActiveSSIDBeforeWifiOff;
  public static String currentTrackName = "-1";
  public static String currentArtistName = "-1";
  public static String currentAlbumnName = "-1";
  public static String currentAlbumArtView = "-1";
  public static final String GLOBAL_TAG = "GLOBAL_TAG";
  /*this map is having all devices volume(64) based on IP address*/
  public static HashMap<String, Integer> INDIVIDUAL_VOLUME_MAP = new HashMap<>();

  public static HashMap<String, GoogleTOSTimeZone> GOOGLE_TIMEZONE_MAP = new HashMap<>();
  public static LinkedHashMap<String, FwUpgradeData> FW_UPDATE_AVAILABLE_LIST = new LinkedHashMap<>();

  /*this map is having zone volume(219) only for master to avoid flickering in active scene*/
  public static HashMap<String, Integer> ZONE_VOLUME_MAP = new HashMap<>();
  public static int mTcpPortInUse = -1;
  private MicTcpServer micTcpServer;
  /**
   * This hashmap stores the DMRplayback helper for a udn
   */
  public static HashMap<String, PlaybackHelper> PLAYBACK_HELPER_MAP = new HashMap<String, PlaybackHelper>();
  public static String LOCAL_UDN = "";
  public static String LOCAL_IP = "";
  public GpsStateReceiver gpsStateReceiver;

  public String getDeviceIpAddress() {
    return deviceIpAddress;
  }

  public void setDeviceIpAddress(String deviceIpAddress) {
    this.deviceIpAddress = deviceIpAddress;
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
  }

  private String deviceIpAddress;
  ScanThread wt = null;
  Thread scanThread = null;

  private MicExceptionListener micExceptionActivityListener;
  private static boolean isGpsReceiverRegistered;
  private static boolean scanListAlreadtSent = false;
  static BluetoothGattCharacteristic btCharacteristic;
  private static String key = null;
  public static boolean isSecure=false;
  public static boolean isForgetNetworkCalled=false;

  @Override
  public void onCreate() {
    super.onCreate();
    MultiDex.install(this);
   // LibreEntryPoint.Companion.getInstance().init(this);
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    LibreApplication.openRawandroidp12 = getApplicationContext().getResources().openRawResource(R.raw.android);

    if (BuildConfig.DEBUG) {
      LibreLogger.d(TAG, "DEBUG mode called");
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
          .detectDiskReads()
          .detectDiskWrites()
          .detectNetwork()   // or .detectAll() for all detectable problems
          .penaltyLog()
          .build());
    }

    //restrictNetworkToWifi();

        /*Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){

            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                myHandlingWhenAppForceStopped (paramThread, paramThrowable);
            }
        });*/

    registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
      @Override
      public void onActivityCreated(Activity activity, Bundle bundle) {
        //Have to put the logs and see the behaviour from here and have to add the clear data store logic
      }

      @Override
      public void onActivityStarted(Activity activity) {
      }

      @Override
      public void onActivityResumed(Activity activity) {
        LibreApplication.openRawandroidp12 = getApplicationContext().getResources()
            .openRawResource(R.raw.android);

      }

      @Override
      public void onActivityPaused(Activity activity) {
      }


      @Override
      public void onActivityStopped(Activity activity) {
      }

      @Override
      public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
      }

      @Override
      public void onActivityDestroyed(Activity activity) {
      }
    });

  }

  public void registerGpsStateReceiver() {
    if (isGpsReceiverRegistered) {
      return;
    }
    gpsStateReceiver = new GpsStateReceiver();
    /*Keeping receiver active for app level activity.*/
    registerReceiver(gpsStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    isGpsReceiverRegistered = true;
  }

  private void restrictNetworkToWifi() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      final ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(
          Context.CONNECTIVITY_SERVICE);
      NetworkRequest.Builder request = new NetworkRequest.Builder();
      request.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);

      if (connectivityManager == null) {
        return;
      }

      connectivityManager.registerNetworkCallback(request.build(),
          new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {

              if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                ConnectivityManager.setProcessDefaultNetwork(network);
              }

              LibreApplication.LOCAL_IP = AppUtils.INSTANCE.getWifiIp(LibreApplication.this);
              initLUCIServices();
            }
          });
    } else {
      LibreApplication.LOCAL_IP = AppUtils.INSTANCE.getWifiIp(LibreApplication.this);
      initLUCIServices();
    }
  }

  private void myHandlingWhenAppForceStopped(Thread paramThread, Throwable paramThrowable) {
    LibreLogger.d(TAG,
        "Alert Lets See if it Works !!! paramThread:::" + paramThread + "paramThrowable:::"
            + paramThrowable);
    /* Killing our Android App with The PID For the Safe Case */
    int pid = android.os.Process.myPid();
    android.os.Process.killProcess(pid);
    System.exit(0);

  }

  public boolean initLUCIServices() {
    try {
    /*  LibreLogger.d(TAG,
          "initLUCIServices Running:- " + mLuciThreadInitiated + " isKeyAliasGenerated:- "
              + isKeyAliasGenerated);*/
      if (!LibreApplication.mLuciThreadInitiated) {
        wt = ScanThread.getInstance();
        scanThread = new Thread(wt);
        scanThread.start();
        micTcpStart();
        if (!isKeyAliasGenerated) {
          boolean isFirsTime = AppLaunchChecker.hasStartedFromLauncher(this);
          if (!isFirsTime) {
            generateKeyForDataStore();
          }
        }
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;
  }

  public void generateKeyForDataStore() {
    String encodedKey;
    try {
      // "AES" is the key generation algorithm, you might want to use a different one.
      KeyGenerator keyGen = KeyGenerator.getInstance("AES");
      // 256-bit key, you may want more or fewer bits.
      keyGen.init(256);
      SecretKey symKey = keyGen.generateKey();
      byte[] encodedSymmetricKey = symKey.getEncoded();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Encode to a String, e.g. base 64 encoded
        encodedKey = new String(Base64.getEncoder().encode(encodedSymmetricKey));
      } else {
        encodedKey = Arrays.toString(
            android.util.Base64.encode(encodedSymmetricKey, android.util.Base64.DEFAULT));
      }
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    //Storing the key into Secure Shared pref
    AppUtils.INSTANCE.providesSharedPreference(this).edit().putString("", encodedKey).apply();
    isKeyAliasGenerated = true;
  }

  public ScanThread getScanThread() {
    // LibreLogger.d(TAG, "getScanThread called");
    return wt;
  }

  /**
   * clearing all collections related to application
   */
  public void clearApplicationCollections() {
    try {
      PLAYBACK_HELPER_MAP.clear();
      INDIVIDUAL_VOLUME_MAP.clear();
      ZONE_VOLUME_MAP.clear();
      LUCIControl.luciSocketMap.clear();
      TunnelingControl.clearTunnelingClients();
      LSSDPNodeDB.getInstance().clearDB();
      ScanningHandler.getInstance().clearSceneObjectsFromCentralRepo();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public synchronized void restart() {
    if (wt != null) {
      wt.close();
    }
    if (micTcpServer != null) {
      micTcpServer.close();
    }
    try {
      scanThread = new Thread(wt);
      scanThread.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public synchronized void micTcpStart() {
    LibreLogger.d(TAG, "micTcpStart started");
    try {
      ConnectivityManager connManager = (ConnectivityManager) getSystemService(
          Context.CONNECTIVITY_SERVICE);
      NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

      /*Can't get connectedSSID if location is off*/
            /*String connectedSSID = AppUtils.INSTANCE.getConnectedSSID(this);
            if (connectedSSID == null || connectedSSID.toLowerCase().contains(Constants.RIVAA_WAC_SSID.toLowerCase())) {
                return;
            }*/

      if (mWifi == null || !mWifi.isConnected()) {
        return;
      }

      micTcpServer = MicTcpServer.getMicTcpServer();
      micTcpServer.startTcpServer(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public synchronized void micTcpClose() {
    LibreLogger.d(TAG, " micTcpClose");
    try {
      if (micTcpServer != null) {
        micTcpServer.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void micTcpServerException(Exception e) {
    LibreLogger.d(TAG, "micTcpServerException = " + e.getMessage());
    if (micExceptionActivityListener != null) {
      LibreLogger.d(TAG,
          "micTcpServerEx listener " + micExceptionActivityListener.getClass().getSimpleName());
      micExceptionActivityListener.micExceptionCaught(e);
    }
  }

  public void registerForMicException(MicExceptionListener listener) {
    LibreLogger.d(TAG, "registerForMicException");
    micExceptionActivityListener = listener;
  }

  public void unregisterMicException() {
    LibreLogger.d(TAG, "unregisterMicException");
    micExceptionActivityListener = null;
  }

  /*private boolean isMobileDataEnabled() {
      boolean mobileDataEnabled = false;
      LibreLogger.d(TAG,"suma check if mobile data is enabled or not");
      ConnectivityManager cm1 = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo info1 = null;
      if (cm1 != null) {
          info1 = cm1.getActiveNetworkInfo();
      }
      if (info1 != null) {
          if (info1.getType() == ConnectivityManager.TYPE_MOBILE) {
              try {
                  Class cmClass = Class.forName(cm1.getClass().getName());
                  Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                  method.setAccessible(true);
                  mobileDataEnabled = (Boolean) method.invoke(cm1);
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }

      }
      return mobileDataEnabled;
  }*/
  private ControlPoint controlPoint;

  public void setControlPoint(ControlPoint controlPoint) {
    this.controlPoint = controlPoint;
  }

  public void setBTGattCharacteristic(BluetoothGattCharacteristic value) {
    btCharacteristic = value;
  }

  public BluetoothGattCharacteristic getBTGattCharacteristic() {
    return btCharacteristic;
  }

  /*public String getKey() {
    return key;
  }

  public void setKey(String key_alias) {
    key = key_alias;
  }*/
}
