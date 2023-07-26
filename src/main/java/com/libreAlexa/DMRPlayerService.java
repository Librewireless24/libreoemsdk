package com.libreAlexa;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import com.cumulations.libreV2.activity.CTHomeTabsActivity;
import com.cumulations.libreV2.model.SceneObject;
import com.libreAlexa.Scanning.ScanThread;
import com.libreAlexa.Scanning.ScanningHandler;
import com.libreAlexa.app.dlna.dmc.processor.impl.UpnpProcessorImpl;
import com.libreAlexa.app.dlna.dmc.server.ContentTree;
import com.libreAlexa.app.dlna.dmc.utility.UpnpDeviceManager;
import com.libreAlexa.constants.Constants;
import com.libreAlexa.constants.LSSDPCONST;
import com.libreAlexa.constants.LUCIMESSAGES;
import com.libreAlexa.constants.MIDCONST;
import com.libreAlexa.firmwareupgarde.FirmwareUploadActivity;
import com.libreAlexa.luci.LSSDPNodeDB;
import com.libreAlexa.luci.LUCIControl;
import com.libreAlexa.util.LibreLogger;
import java.util.concurrent.ConcurrentHashMap;
import org.fourthline.cling.model.meta.RemoteDevice;


/**
 * Created by khajan on 2/9/15.
 * <p/>
 * This service listens for DMR devices in the background. On finding the DMR device, it will create
 * the playbackhelper for the same so that we can operate actions like seek
 */
public class DMRPlayerService extends Service {

  public static boolean mServiceRunning = false;
  public boolean isTaskRemoved;
  private UpnpProcessorImpl m_upnpProcessor;
  int CurrentAPIVersion;
//  private final static String TAG = DMRPlayerService.class.getSimpleName();
private final static String TAG ="APP_FORGROUND";
  @Override
  public void onCreate() {
    LibreLogger.d(TAG, "onCreate");
    super.onCreate();
    m_upnpProcessor = new UpnpProcessorImpl(this);
    /*Searching the Renderer issue*/
    m_upnpProcessor.addListener(UpnpDeviceManager.getInstance());
    m_upnpProcessor.bindUpnpService();
    CurrentAPIVersion = Build.VERSION.SDK_INT;


  }

  private PendingIntent getNotificationPendingIntent() {
    // Creates an explicit intent for an Activity in your app
    Intent resultIntent = new Intent(this, CTHomeTabsActivity.class);
    resultIntent.setAction(Constants.ACTION.MAIN_ACTION);
    // The stack builder object will contain an artificial back stack for the
    // started Activity.
    // This ensures that navigating backward from the Activity leads out of
    // your application to the Home screen.
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    // Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(ControlActivity.class);
    // Adds the Intent that starts the Activity to the top of the stack
    stackBuilder.addNextIntent(resultIntent);
    if (CurrentAPIVersion >= 12) {
      // Do something for 12 and above versions
      return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
    } else {
      // do something for phones running an SDK before 12
      return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }


  }


  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    LibreLogger.d(TAG, "onStartCommand, action = " + intent.getAction());
    if (intent.getAction() != null) {
      if (intent.getAction().equals(Constants.ACTION.START_DMR_SERVICE)) {
        getNotificationPendingIntent().cancel();
        RemoteViews contentView = new RemoteViews(getApplicationContext().getPackageName(),
            R.layout.custom_notification_layout);

        //set the button listeners
        setStopAction(contentView);

        Intent homeTabs = new Intent(this, CTHomeTabsActivity.class);
        homeTabs.setAction(Constants.ACTION.MAIN_ACTION);
        Notification notification = getNotificationBuilder(this,
            getString(R.string.foreground_channel_id),
            NotificationManagerCompat.IMPORTANCE_LOW)
            .setSmallIcon(R.drawable.libre_icon)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_content_text))
//                                .setTicker(getString(R.string.app_name))
//                                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
            .setContent(contentView)
            .setContentIntent(getNotificationPendingIntent())
            .setOngoing(true)
            .build();

        LibreLogger.d(TAG, "onStartCommand startForeground");
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        mServiceRunning = true;

      } else if (intent.getAction().equals(Constants.ACTION.STOP_DMR_SERVICE)) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
        stopForeground(true);
        stopSelf();
        LibreLogger.d(TAG, "onStartCommand stopForeground");
      }
    }

    return START_NOT_STICKY;
  }


  public void setStopAction(RemoteViews view) {
    Intent stopIntent = new Intent(this, DMRPlayerService.class);
    stopIntent.setAction(Constants.ACTION.STOP_DMR_SERVICE);
    PendingIntent stopPendingIntent=null;
    if (CurrentAPIVersion >= 12) {
      // Do something for 12 and above versions
      stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);
    }
    else{
      // do something for phones running an SDK before 12
      stopPendingIntent = PendingIntent.getService(this, 0, stopIntent,
          PendingIntent.FLAG_UPDATE_CURRENT);
    }
    view.setOnClickPendingIntent(R.id.iv_stop_foreground, stopPendingIntent);
  }


  @Override
  public void onDestroy() {
    LibreLogger.d(TAG, "onDestroy");
    stopDMRPlayback();
    mServiceRunning = false;
    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    // Used only in case of bound services.
    return null;
  }

  private void stopDMRPlayback() {

    ScanningHandler mScanningHandler = ScanningHandler.getInstance();
    ConcurrentHashMap<String, SceneObject> centralSceneObjectRepo = mScanningHandler.getSceneObjectMapFromRepo();

    /*which means no master present hence all devices are free so need to do anything*/
    if (centralSceneObjectRepo == null || centralSceneObjectRepo.size() == 0) {
      LibreLogger.d(TAG, "No master present");
      return;
    }

    for (String masterIPAddress : centralSceneObjectRepo.keySet()) {
      RemoteDevice renderingDevice = UpnpDeviceManager.getInstance()
          .getRemoteDMRDeviceByIp(masterIPAddress);
      SceneObject currentSceneObject = mScanningHandler.getSceneObjectFromCentralRepo(
          masterIPAddress);

      /* Added if its Playing DMR Source then only we have to Stop the Playback */
      try {
        if (renderingDevice != null
            && currentSceneObject != null
            && currentSceneObject.getPlayUrl() != null
            && !currentSceneObject.getPlayUrl().isEmpty()
            && currentSceneObject.getPlayUrl().contains(LibreApplication.LOCAL_IP)
            && currentSceneObject.getPlayUrl().contains(ContentTree.AUDIO_PREFIX)
            && currentSceneObject.getCurrentSource() == Constants.DMR_SOURCE
        ) {

          LUCIControl control = new LUCIControl(currentSceneObject.getIpAddress());
          control.SendCommand(MIDCONST.MID_PLAYCONTROL, LUCIMESSAGES.STOP, LSSDPCONST.LUCI_SET);

                    /*String renderingUDN = renderingDevice.getIdentity().getUdn().toString();
                    PlaybackHelper playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP.get(renderingUDN);
                    if (playbackHelper!=null){
                        playbackHelper.stopPlayback();
                    }*/
        }
      } catch (Exception e) {
        LibreLogger.d(TAG, "stopDMRPlayback exception : " + e.getMessage());
      }
    }
  }


  @Override
  public void onTaskRemoved(Intent rootIntent) {
    LibreLogger.d(TAG, "onTaskRemoved");
    stopDMRPlayback();
    ScanThread.getInstance().close();
    LSSDPNodeDB.getInstance().clearDB();
    ScanningHandler.getInstance().clearSceneObjectsFromCentralRepo();

    stopForeground(true);
    stopSelf();

    int pid = android.os.Process.myPid();
    android.os.Process.killProcess(pid);

    isTaskRemoved = true;
    super.onTaskRemoved(rootIntent);

  }

  public NotificationCompat.Builder getNotificationBuilder(Context context, String channelId,
      int importance) {
    NotificationCompat.Builder builder;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      LibreLogger.d(TAG, "getNotificationBuilder if");
      createChannel(context, channelId, importance);
      builder = new NotificationCompat.Builder(context, channelId);
    } else {
      LibreLogger.d(TAG, "getNotificationBuilder else");
      builder = new NotificationCompat.Builder(context);
    }
    return builder;
  }


  private void createChannel(Context context, String id, int importance) {
    LibreLogger.d(TAG, "getNotificationBuilder createChannel ");
    final String appName = context.getString(R.string.app_name);
    String description = context.getString(R.string.notifications_channel_description);
    final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);



    if (notificationManager != null) {
      NotificationChannel notificationChannel;
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        notificationChannel = notificationManager.getNotificationChannel(id);
        if (notificationChannel == null) {
          notificationChannel = new NotificationChannel(id, appName, importance);
          notificationChannel.setDescription(description);
          notificationChannel.setShowBadge(false);
          notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
          notificationManager.createNotificationChannel(notificationChannel);
        }
      }
    }
  }

}
