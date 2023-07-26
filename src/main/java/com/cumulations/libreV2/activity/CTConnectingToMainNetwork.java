package com.cumulations.libreV2.activity;

import static android.media.ExifInterface.TAG_MODEL;
import static com.libreAlexa.constants.Constants.FW_FAILED;
import static com.libreAlexa.constants.Constants.GCAST_COMPLETE;
import static com.libreAlexa.constants.Constants.NO_UPDATE;
import static com.libreAlexa.constants.Constants.UPDATE_DOWNLOAD;
import static com.libreAlexa.constants.Constants.UPDATE_IMAGE_AVAILABLE;
import static com.libreAlexa.constants.Constants.UPDATE_STARTED;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import com.cumulations.libreV2.WifiUtil;
import com.cumulations.libreV2.activity.oem.CastToSActivity;
import com.cumulations.libreV2.activity.oem.SetUpDeviceActivity;
import com.cumulations.libreV2.model.WifiConnection;
import com.libreAlexa.LErrorHandeling.LibreError;
import com.libreAlexa.LibreApplication;
import com.libreAlexa.Ls9Sac.GoogleCastUpdateAfterSac;
import com.libreAlexa.R;
import com.libreAlexa.Scanning.ScanThread;
import com.libreAlexa.Scanning.ScanningHandler;
import com.libreAlexa.alexa.AlexaUtils;
import com.libreAlexa.alexa.DeviceProvisioningInfo;
import com.libreAlexa.constants.AppConstants;
import com.libreAlexa.constants.Constants;
import com.libreAlexa.constants.LUCIMESSAGES;
import com.libreAlexa.constants.MIDCONST;
import com.libreAlexa.luci.LSSDPNodeDB;
import com.libreAlexa.luci.LSSDPNodes;
import com.libreAlexa.luci.LUCIPacket;
import com.libreAlexa.netty.BusProvider;
import com.libreAlexa.netty.LibreDeviceInteractionListner;
import com.libreAlexa.netty.NettyData;
import com.libreAlexa.util.LibreLogger;
import java.text.DateFormat;
import org.json.JSONException;
import org.json.JSONObject;


public class CTConnectingToMainNetwork extends CTDeviceDiscoveryActivity implements LibreDeviceInteractionListner {
    private final WifiConnection wifiConnect = WifiConnection.getInstance();
    private WifiManager mWifiManager;
    private AlertDialog alertConnectingtoNetwork,alertDialogFWFailed;
    private ProgressBar progressBar;
    private TextView mainMsgText,subMsgText;
    private String locale = "";
    private DeviceProvisioningInfo mDeviceProvisioningInfo;
    private WifiUtil wifiUtil;
    public static final String SAC_CURRENT_IPADDRESS = "sac_current_ipaddress";
    private String mSACConfiguredIpAddress = "";
    private String fwInternetUpgradeMessage = "";
    private AppCompatImageView setupProgressImage;
    private boolean mb223TimerRunning;
    public static final long OOPS_TIMEOUT = 45*1000;
    public static final String TAG = CTConnectingToMainNetwork.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_activity_device_connecting);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        progressBar = findViewById(R.id.setup_progress_bar);
        mainMsgText = findViewById(R.id.tv_setup_info);
        subMsgText = findViewById(R.id.please_wait_label);
        setupProgressImage = findViewById(R.id.setup_progress_image);

        wifiUtil = new WifiUtil(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        showProgressDialog();
        LibreApplication.isSacFlowStarted = true;
        /*Get Connected Ssid Name */
        if (getConnectedSSIDName(this).equals(wifiConnect.getMainSSID())) {
            LSSDPNodes mNode = LSSDPNodeDB.getInstance().isSacDeviceFoundInTheWork(LibreApplication.sacDeviceNameSetFromTheApp);
            if( mNode != null){
                LibreLogger.d(TAG, "Configure Device is already found " + mNode.getFriendlyname());
                configuredDeviceFound(mNode);
            } else {
                mHandler.sendEmptyMessage(Constants.CONNECTED_TO_MAIN_SSID_SUCCESS);
            }
        } else {
            mHandler.sendEmptyMessage(Constants.HTTP_POST_DONE_SUCCESSFULLY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerForDeviceEvents(this);
    }

    private final
    Handler mHandler = new Handler(Looper.getMainLooper()){
        @RequiresApi(api = VERSION_CODES.Q)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LibreLogger.d(TAG, "Handler Message what " + msg.what);

            String ssid = getConnectedSSIDName(CTConnectingToMainNetwork.this);
            switch (msg.what){
                case Constants.HTTP_POST_DONE_SUCCESSFULLY:
                    LibreLogger.d(TAG,"mHandler HTTP_POST_DONE_SUCCESSFULLY, ssid = "+ssid);
                    setSetupInfoTexts(getString(R.string.mConnectingToMainNetwork) + " " + wifiConnect.getMainSSID(),
                            getString(R.string.pleaseWait));
                    if (ssid.equals(wifiConnect.mainSSID)) {
                        mHandler.removeMessages(Constants.CONNECTED_TO_MAIN_SSID_SUCCESS);
                        mHandler.sendEmptyMessage(Constants.CONNECTED_TO_MAIN_SSID_SUCCESS);
                        return;
                    }
                    if(getDeviceName()!=null && getDeviceName().trim().length() > 0) {
                        LibreLogger.d(TAG_MODEL, "getDeviceName "+getDeviceName());
                        wifiUtil.disconnectCurrentWifi(getDeviceName());
                    }
                    wifiUtil.connectWiFiToSSID(wifiConnect.getMainSSID(), wifiConnect.getMainSSIDPwd(),wifiConnect.mainSSIDSec);
                    mHandler.sendEmptyMessageDelayed(Constants.CONNECTED_TO_MAIN_SSID_FAIL, OOPS_TIMEOUT);
                    break;

                case Constants.CONNECTED_TO_MAIN_SSID_SUCCESS:
                    LibreLogger.d(TAG,"mHandler CONNECTED_TO_MAIN_SSID_SUCCESS, ssid = "+ssid);
                    /* Removing Failed Callback */
                    mHandler.removeMessages(Constants.CONNECTED_TO_MAIN_SSID_FAIL);
                    if (ssid.equals(wifiConnect.getMainSSID())) {
                        cleanUpCode(false);
                        boolean mRestartOfAllSockets = false;
                        LibreLogger.d(TAG, "Connected To Main SSID " + wifiConnect.getMainSSID() + mRestartOfAllSockets);
                        LibreApplication.activeSSID = wifiConnect.getMainSSID();
//                        LSSDPNodeDB.getInstance().clearDB();
                        LibreApplication.LOCAL_IP = "";
                        LibreApplication.mCleanUpIsDoneButNotRestarted = false;
                        restartAllSockets();
                        mHandler.sendEmptyMessageDelayed(Constants.SEARCHING_FOR_DEVICE, 500);
                    }
                    break;

                case Constants.SEARCHING_FOR_DEVICE:
                    LibreLogger.d(TAG,"mHandler SEARCHING_FOR_DEVICE, ssid = "+ssid);
                    sendMSearchInIntervalOfTime();
                    LibreLogger.d(APP_FORGROUND,"TIMEOUT_FOR_SEARCHING_DEVICE= "+ssid);
                    mHandler.sendEmptyMessageDelayed(Constants.TIMEOUT_FOR_SEARCHING_DEVICE, OOPS_TIMEOUT);
                    LibreLogger.d(TAG, "Searching For The Device " + LibreApplication.sacDeviceNameSetFromTheApp);
                    setSetupInfoTexts(getString(R.string.setting_up_speaker),getString(R.string.pleaseWait));
                    break;

                case Constants.TIMEOUT_FOR_SEARCHING_DEVICE:
                case Constants.CONNECTED_TO_MAIN_SSID_FAIL:
                case Constants.FW_UPGRADE_REBOOT_TIMER:
                    LibreLogger.d(TAG,"mHandler TIMEOUT_FOR_SEARCHING_DEVICE||FW_UPGRADE_REBOOT_TIMER");
                    closeProgressDialog();
                    openOOPSScreen();
                    break;

                case Constants.CONFIGURED_DEVICE_FOUND:
                    LibreLogger.d(TAG,"mHandler CONFIGURED_DEVICE_FOUND");
                    closeProgressDialog();
                    goToNextScreen();
                    break;

                case Constants.CONNECTED_TO_DIFFERENT_SSID:
                    LibreLogger.d(TAG,"mHandler CONNECTED_TO_DIFFERENT_SSID");
                    showAlertDialogForClickingWrongNetwork();
                    break;

                case Constants.ALEXA_CHECK_TIMEOUT:
                    LibreLogger.d(TAG,"mHandler ALEXA_CHECK_TIMEOUT");
                    /*taking user to the usual screen flow ie configure speakers*/
                    mHandler.sendEmptyMessage(Constants.CONFIGURED_DEVICE_FOUND);
                    break;

                case Constants.WAITING_FOR_223_MB:
                    LibreLogger.d(TAG,"mHandler WAITING_FOR_223_MB");
                    /*taking user to the usual screen flow ie configure speakers*/
                    mb223TimerRunning = false;
                    readAlexaToken(mSACConfiguredIpAddress);
                    break;
                case FW_FAILED:
                    LibreLogger.d(TAG_FW_UPDATE, "FW_UPGRADE_INTERNET FW_FAILED ");
                    goToNextScreen();
            }
        }
    };

    private void openOOPSScreen() {
        startActivity(new Intent(CTConnectingToMainNetwork.this, CTSetupFailedActivity.class));
        finish();
    }

    private final int MSEARCH_TIMEOUT_SEARCH = 2000;
    private final Handler mTaskHandlerForSendingMSearch = new Handler();
    public boolean mBackgroundMSearchStoppedDeviceFound = false;
    private final Runnable mMyTaskRunnableForMSearch = new Runnable() {
        @Override
        public void run() {
            LibreLogger.d(TAG, "My task is Sending 1 Minute Once M-Search");
            /* do what you need to do */
            if (mBackgroundMSearchStoppedDeviceFound)
                return;

           /* final LibreApplication application = (LibreApplication) getApplication();
            application.getScanThread().UpdateNodes();*/
            //SDK CHANGE
            // val application = application as LibreApplication?
            ScanThread.getInstance().UpdateNodes();
            /* and here comes the "trick" */
            mTaskHandlerForSendingMSearch.postDelayed(this, MSEARCH_TIMEOUT_SEARCH);
        }
    };

    private void sendMSearchInIntervalOfTime() {
        mTaskHandlerForSendingMSearch.postDelayed(mMyTaskRunnableForMSearch, MSEARCH_TIMEOUT_SEARCH);
    }

    @Override
    public void onBackPressed() {
        /*disable back press*/
    }

    @Override
    public void deviceDiscoveryAfterClearingTheCacheStarted() {

    }

    public boolean compareSacDeviceNameWithFriendlyName(String friendlyName) {
        //Crash Happened Here
        String mSacDeviceName = LibreApplication.sacDeviceNameSetFromTheApp.substring(0,LibreApplication.sacDeviceNameSetFromTheApp.length()-1);
        LibreLogger.d(TAG, "SAC*** Device name stored in LibreApplication\n" + LibreApplication.sacDeviceNameSetFromTheApp+"friendlyName\n"+friendlyName );
        if(LibreApplication.sacDeviceNameSetFromTheApp.equalsIgnoreCase(friendlyName)){
            return true;
        } else if (friendlyName.contains(mSacDeviceName)) return true;
        return false;
    }
    /* Sometimes we are receiving NACK to solve it , we are resending the Data */
    private boolean mSent223ToSpeaker = false;
    public void configuredDeviceFound(LSSDPNodes node){
        String time = DateFormat.getInstance().format(System.currentTimeMillis());

        if (node != null) {
            LibreLogger.d(TAG, "SAC Configured Device Found = " + node.getFriendlyname() +" and Node Ip "+node.getIP()+" at   "+ LibreApplication.sacDeviceNameSetFromTheApp);
            if (compareSacDeviceNameWithFriendlyName(node.getFriendlyname())) {
                insertDeviceIntoDb(node,"CTE");
                LibreLogger.d(TAG, "SAC Configured Device Found = IF " + node.getFriendlyname() + " at " + time);
                LibreLogger.d(TAG_FW_UPDATE, "SAC newDeviceFound, fwInternetUpgradeMessage = " + fwInternetUpgradeMessage);
                mHandler.removeMessages(Constants.TIMEOUT_FOR_SEARCHING_DEVICE);
                LibreLogger.d(TAG, "SAC Configured Device Found = TIMEOUT_FOR_SEARCHING_DEVICE " + node.getIP());
                mSACConfiguredIpAddress = node.getIP();
                readAlexaToken(mSACConfiguredIpAddress);

                if (fwInternetUpgradeMessage == null || fwInternetUpgradeMessage.isEmpty()) {
                    LibreLogger.d(TAG_FW_UPDATE, "SAC Configured Device Found = Second IF " + node.getFriendlyname() + " at " + time);
                    mHandler.sendEmptyMessageDelayed(Constants.WAITING_FOR_223_MB,
                        Constants.INTERNET_PLAY_TIMEOUT);
                    mb223TimerRunning = true;
                    AlexaUtils.getDeviceUpdateStatus(mSACConfiguredIpAddress);
                    return;
                }
                LibreLogger.d(TAG_FW_UPDATE, "SAC*** Configured Device Found before third if  " + fwInternetUpgradeMessage);
              //NOTE FROM Suma :Enable this logic.., when checking with proper  OEM f/w
                 if (fwInternetUpgradeMessage.equals(NO_UPDATE)
                        || fwInternetUpgradeMessage.equals(GCAST_COMPLETE)
                        || fwInternetUpgradeMessage.equals(Constants.BATTERY_POWER)) {
                    if (fwInternetUpgradeMessage.equals(GCAST_COMPLETE)) {
                        LibreLogger.d(TAG_FW_UPDATE, "SAC*** Configured Device Found = Third IF " + fwInternetUpgradeMessage);
                        mHandler.removeMessages(Constants.FW_UPGRADE_REBOOT_TIMER);
                    }
                    readAlexaToken(mSACConfiguredIpAddress);
                }
                mSent223ToSpeaker = true;
            }

        } else {
            LibreLogger.d(TAG, "SAC*** Configured Device Found = ELSE " + node.getFriendlyname() +" at "+ time);
        }
    }

    @Override
    public void newDeviceFound(final LSSDPNodes node) {
      //  LibreLogger.d(TAG_SECUREROOM, "New Device Found CTConnect, friendlyName = " + node.getFriendlyname() +", gCastVersion = "+ node.getgCastVerision()+ " IPAddress: "+node.getIP());
      //  insertDeviceIntoDb(node,"CT");
        configuredDeviceFound(node);
    }

    @Override
    public void deviceGotRemoved(String ipaddress) {

    }

    private boolean mReceviedNACK = false;

    @Override
    public void messageRecieved(NettyData nettyData) {
        String nettyDataRemotedeviceIp = nettyData.getRemotedeviceIp();
        LUCIPacket packet = new LUCIPacket(nettyData.getMessage());
        String message = new String(packet.getpayload());

        LibreLogger.d(TAG, "messageRecieved, mb223TimerRunning = " + mb223TimerRunning + " SacConfigured Ipaddress " + mSACConfiguredIpAddress);
        LibreLogger.d(TAG, "Message recieved ipAddress " + nettyDataRemotedeviceIp
                + ", command " + packet.getCommand()
                + ", message " + message + ", at " + System.currentTimeMillis());
        LibreLogger.d(TAG_FW_UPDATE, "MB:- Firmware Update Progress Command: "+packet.getCommand());
        if (mSACConfiguredIpAddress.equalsIgnoreCase(nettyDataRemotedeviceIp)) {

            LSSDPNodes node = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(nettyDataRemotedeviceIp);
            if (node == null)
                return;
            LibreLogger.d(TAG, "messageRecieved, mb223TimerRunning = " + node.getFriendlyname() + " SacConfigured Ipaddress " + LibreApplication.sacDeviceNameSetFromTheApp);

            String time = DateFormat.getInstance().format(System.currentTimeMillis());

            switch (packet.Command) {
                case 0:
                    mReceviedNACK = true;
                    break;
                case MIDCONST.ALEXA_COMMAND:
                    LibreLogger.d(TAG, "Alexa Value From 230  " + message);
                    if (mb223TimerRunning) {
                        return;
                    }

                    if (!message.isEmpty()) {
                        /* Parse JSon */
                        try {
                            JSONObject jsonRootObject = new JSONObject(message);
                            // JSONArray jsonArray = jsonRootObject.optJSONArray("Window CONTENTS");
                            JSONObject jsonObject = jsonRootObject.getJSONObject(LUCIMESSAGES.ALEXA_KEY_WINDOW_CONTENT);
                            String productId = jsonObject.optString(LUCIMESSAGES.ALEXA_KEY_PRODUCT_ID);
                            String dsn = jsonObject.optString(LUCIMESSAGES.ALEXA_KEY_DSN);
                            String sessionId = jsonObject.optString(LUCIMESSAGES.ALEXA_KEY_SESSION_ID);
                            String codeChallenge = jsonObject.optString(LUCIMESSAGES.ALEXA_KEY_CODE_CHALLENGE);
                            String codeChallengeMethod = jsonObject.optString(LUCIMESSAGES.ALEXA_KEY_CODE_CHALLENGE_METHOD);
                            if (jsonObject.has(LUCIMESSAGES.ALEXA_KEY_LOCALE))
                                locale = jsonObject.optString(LUCIMESSAGES.ALEXA_KEY_LOCALE);
                            mDeviceProvisioningInfo = new DeviceProvisioningInfo(productId, dsn, sessionId, codeChallenge, codeChallengeMethod, locale);

                            if (node != null) {
                                node.setMdeviceProvisioningInfo(mDeviceProvisioningInfo);
                            }

                            mHandler.removeMessages(Constants.ALEXA_CHECK_TIMEOUT);
                            if (fwInternetUpgradeMessage.equals(NO_UPDATE)
                                    || fwInternetUpgradeMessage.equals(Constants.BATTERY_POWER)) {
                                mHandler.sendEmptyMessage(Constants.CONFIGURED_DEVICE_FOUND);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case MIDCONST.MID_ENV_READ:

                    if (mb223TimerRunning) {
                        /* ReceviedNACK and and we missed sent the 223 to Speaker so we need to resend it */
                        if(mReceviedNACK || mSent223ToSpeaker) {
                            configuredDeviceFound(LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(nettyData.getRemotedeviceIp()));
                        }
                        LibreLogger.d(TAG, "messageRecieved, mb 208 return mb223TimerRunning = true");
                        return;
                    }

                    if (message.contains("AlexaRefreshToken")) {
                        mHandler.removeMessages(Constants.ALEXA_CHECK_TIMEOUT);
                        String token = message.substring(message.indexOf(":") + 1);
                        LSSDPNodes mNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(nettyData.getRemotedeviceIp());
                        if (mNode != null) {
                            mNode.setAlexaRefreshToken(token);
                            if (token != null && !token.isEmpty()) {
                                if (fwInternetUpgradeMessage.equals(NO_UPDATE)
                                        || fwInternetUpgradeMessage.equals(Constants.BATTERY_POWER)) {
                                    mHandler.sendEmptyMessage(Constants.CONFIGURED_DEVICE_FOUND);
                                }
                            } else {
                                /*request metadata for login*/
                                readAlexaMetaData(mSACConfiguredIpAddress);
                            }
                        }
                    }
                    break;

                case MIDCONST.FW_UPGRADE_INTERNET_LS9:
                    LibreLogger.d(TAG_FW_UPDATE, "MB:- 223 fwInternetUpgradeMessage "+fwInternetUpgradeMessage);
                    if (message.isEmpty())
                        return;
                    fwInternetUpgradeMessage = message;
                    if (fwInternetUpgradeMessage != null && !fwInternetUpgradeMessage.isEmpty()) {
                        mHandler.removeMessages(Constants.WAITING_FOR_223_MB);
                        mb223TimerRunning = false;
                       // LibreLogger.d(TAG,TAG_FW_UPDATE, "fwInternetUpgradeMessage "+fwInternetUpgradeMessage);
                        if(fwInternetUpgradeMessage!=null && fwInternetUpgradeMessage.matches("[0-9]+")){
                         //   LibreLogger.d(TAG,TAG_FW_UPDATE, "fwInternetUpgradeMessage PROGRES:- "+fwInternetUpgradeMessage);
                            progressBar.setProgress(Integer.parseInt(fwInternetUpgradeMessage));
                        }
                        switch (fwInternetUpgradeMessage) {
                            case NO_UPDATE:
                                readAlexaToken(mSACConfiguredIpAddress);
                                break;
                            case UPDATE_STARTED:
                                //LibreLogger.d(TAG,TAG_FW_UPDATE, "FW_UPGRADE_INTERNET UPDATE_STARTED "+fwInternetUpgradeMessage);
                                break;
                            case UPDATE_DOWNLOAD:
                                setSetupInfoTexts(getString(R.string.updating_your_speaker),getString(R.string.mb223_update_download));
                                break;
                            case UPDATE_IMAGE_AVAILABLE:
                                setSetupInfoTexts(getString(R.string.updating_in_progress), getString(R.string.your_device_is_currently));
                                break;
                            case Constants.CRC_CHECK_ERROR:
                                showAlertDialogForFWError(getString(R.string.failed),getString(R.string.somethingWentWrong));
                                break;
                            case Constants.DOWNLOAD_FAIL:
                                showAlertDialogForFWError(getString(R.string.failed),getString(R.string.mb223_download_fail));
                                break;
                        }
                    }
                    break;
                case MIDCONST.FW_UPGRADE_PROGRESS:
                    LibreLogger.d(TAG_FW_UPDATE, "MB:- 66 Firmware Update Progress Start and Message: "+message);
                    if (message.isEmpty())
                        return;

                    fwInternetUpgradeMessage = message;
                    /*if Gcast Failed State  back to PlayNEwScreen */
                    if (fwInternetUpgradeMessage.equals("255")) {
                        LibreLogger.d(TAG_FW_UPDATE, "Firmware Update Failed For " + node.getFriendlyname());
                        return;
                    }

                    if (!fwInternetUpgradeMessage.isEmpty()) {
                        mHandler.removeMessages(Constants.WAITING_FOR_223_MB);
                        mb223TimerRunning = false;
                        if (fwInternetUpgradeMessage.equals(GCAST_COMPLETE)) {
                            setSetupInfoTexts(getString(R.string.now_rebooting), getString(R.string.indicating_light));
                            Log.d(APP_FORGROUND, "messageRecieved: "+fwInternetUpgradeMessage);
                            mHandler.sendEmptyMessageDelayed(Constants.FW_UPGRADE_REBOOT_TIMER, 3 * 60 * 1000);
                        }
                    }
                    break;
            }
        }
    }

    public void showAlertDialogForFWError(String mainStr, String subStr) {
        LibreLogger.d(TAG_FW_UPDATE, "FW_UPGRADE_INTERNET showAlertDialogForFWError "+mainStr);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle(
            mainStr).setMessage(subStr);
        if (alertDialogFWFailed == null) {
            alertDialogFWFailed = dialog.create();
            alertDialogFWFailed.show();
            LibreLogger.d(TAG_FW_UPDATE, "FW_UPGRADE_INTERNET showAlertDialogForFWError IF ");
        }
        mHandler.sendEmptyMessageDelayed(FW_FAILED,5000);
    }

    private void setSetupInfoTexts(String mainMsg,String subMsg) {
        LibreLogger.d(TAG_FW_UPDATE, "setSetupInfoTexts: "+mainMsg+" and subMsg: "+subMsg);
        mainMsgText.setText(mainMsg);
        subMsgText.setText(subMsg);

        if (subMsg.equals(getString(R.string.pleaseWait))){
            setupProgressImage.setImageResource(R.drawable.setup_progress1);
        } else if (subMsg.equals(getString(R.string.you_will_see_flashing))){
            setupProgressImage.setImageResource(R.drawable.setup_progress2);
        } else if (subMsg.equals(getString(R.string.indicating_light))){
            setupProgressImage.setImageResource(R.drawable.setup_progress3);
        }
    }

    private void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void closeProgressDialog() {
        progressBar.setVisibility(View.GONE);
    }

    private void callGoogleCastUpdateScreen() {
        ActivityCompat.finishAffinity(this);
        if (LibreApplication.FW_UPDATE_AVAILABLE_LIST.containsKey(mSACConfiguredIpAddress)) {
            LibreApplication.FW_UPDATE_AVAILABLE_LIST.remove(mSACConfiguredIpAddress);
        }
        startActivity(new Intent(CTConnectingToMainNetwork.this, GoogleCastUpdateAfterSac.class)
                .putExtra(SAC_CURRENT_IPADDRESS, mSACConfiguredIpAddress));
        finish();

    }

    private void readAlexaToken(String configuredDeviceIp) {
        AlexaUtils.sendAlexaRefreshTokenRequest(configuredDeviceIp);
        mHandler.sendEmptyMessageDelayed(Constants.ALEXA_CHECK_TIMEOUT, Constants.INTERNET_PLAY_TIMEOUT);
    }

    private void readAlexaMetaData(String configuredDeviceIp) {
        AlexaUtils.sendAlexaMetaDataRequest(configuredDeviceIp);
        mHandler.sendEmptyMessageDelayed(Constants.ALEXA_CHECK_TIMEOUT, Constants.INTERNET_PLAY_TIMEOUT);
    }

    private void goToNextScreen() {
        LSSDPNodes mNode = ScanningHandler.getInstance().getLSSDPNodeFromCentralDB(mSACConfiguredIpAddress);
        if (mNode == null) {
            showToast("Device not available in central db");
            intentToHome(CTConnectingToMainNetwork.this);
            return;
        }
        LibreLogger.d(TAG, "goToNextScreen Checking the Both the source:- " + mNode.getAlexaRefreshToken()
            + "isAlexaSource:- " + mNode.getmDeviceCap().getmSource().isAlexaAvsSource()
            + "isCastSource:- " + mNode.getmDeviceCap().getmSource().isGoogleCast());

        if (mNode.getmDeviceCap().getmSource().isAlexaAvsSource()) {
            LibreLogger.d(TAG, "goToNextScreen AVSSource " + mNode.getmDeviceCap().getmSource().isGoogleCast());
            if (mNode.getAlexaRefreshToken() == null || mNode.getAlexaRefreshToken().isEmpty()
                || mNode.getAlexaRefreshToken().equals("0")) {
                Intent newIntent = new Intent(CTConnectingToMainNetwork.this, CTAmazonInfoActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                newIntent.putExtra(Constants.CURRENT_DEVICE_IP, mSACConfiguredIpAddress);
                newIntent.putExtra(AppConstants.DEVICE_PROVISIONING_INFO, mNode.getMdeviceProvisioningInfo());
                newIntent.putExtra(Constants.FROM_ACTIVITY, CTConnectingToMainNetwork.class.getSimpleName());
                startActivity(newIntent);
                finish();
            } else {
                Intent i = new Intent(CTConnectingToMainNetwork.this, CTAlexaThingsToTryActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(Constants.CURRENT_DEVICE_IP, mSACConfiguredIpAddress);
                i.putExtra(Constants.FROM_ACTIVITY, CTConnectingToMainNetwork.class.getSimpleName());
                startActivity(i);
                finish();
            }
        } else if (mNode.getmDeviceCap().getmSource().isGoogleCast()) {
            LibreLogger.d(TAG, "goToNextScreen CastSource " + mNode.getmDeviceCap().getmSource().isGoogleCast());
            Intent goToCastTOSActivity = new Intent(this, CastToSActivity.class);
            goToCastTOSActivity.putExtra(Constants.CURRENT_DEVICE_IP, mSACConfiguredIpAddress);
            goToCastTOSActivity.putExtra(Constants.DEVICE_NAME, mNode.getFriendlyname());
            goToCastTOSActivity.putExtra(Constants.FROM_ACTIVITY, CTConnectingToMainNetwork.class.getSimpleName());
            startActivity(goToCastTOSActivity);
            finish();
        } else if (mNode.getmDeviceCap().getmSource().isGoogleCast() && mNode.getmDeviceCap().getmSource().isAlexaAvsSource()) {
            LibreLogger.d(TAG, "goToNextScreen BothSource " + mNode.getmDeviceCap().getmSource().isAlexaAvsSource() + " castSource:- " + mNode.getmDeviceCap().getmSource().isAlexaAvsSource());
            Intent newIntent = new Intent(CTConnectingToMainNetwork.this, SetUpDeviceActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.putExtra(Constants.CURRENT_DEVICE_IP, mSACConfiguredIpAddress);
            newIntent.putExtra(Constants.DEVICE_NAME, mNode.getFriendlyname());
            newIntent.putExtra(AppConstants.DEVICE_PROVISIONING_INFO, mNode.getMdeviceProvisioningInfo());
            newIntent.putExtra(Constants.FROM_ACTIVITY, CTConnectingToMainNetwork.class.getSimpleName());
            startActivity(newIntent);
            finish();

        } else {
            LibreLogger.d(TAG, "goToNextScreen No source available ");
            showToast(getString(R.string.configuration_successful));
            intentToHome(CTConnectingToMainNetwork.this);
        }

    }
    private void showAlertDialogForClickingWrongNetwork() {
        String ssid = getConnectedSSIDName(this);
        if (ssid.isEmpty()
                || ssid.contains(Constants.SA_SSID_RIVAA_CONCERT)
                || ssid.contains(Constants.SA_SSID_RIVAA_STADIUM)
                || ssid.equals(wifiConnect.mainSSID)) {
            return;
        }

        if (!CTConnectingToMainNetwork.this.isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CTConnectingToMainNetwork.this);
            String Message = String.format(getString(R.string.newrestartApp),
                    ssid, wifiConnect.getMainSSID());
            /*String Message = getResources().getString(R.string.mConnectedToSsid) + "\n" + getConnectedSSIDName(ConnectingToMainNetwork.this) + "\n" +
                    getString(R.string.restartTitle);*/
            builder.setMessage(Message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.open_wifi_settings, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (alertConnectingtoNetwork!=null && alertConnectingtoNetwork.isShowing()) {
                                alertConnectingtoNetwork.dismiss();
                                alertConnectingtoNetwork = null;
                            }

                            mHandler.removeMessages(Constants.WAITING_FOR_223_MB);
                            mb223TimerRunning = false;

                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            customStartActivityForResult(AppConstants.WIFI_SETTINGS_REQUEST_CODE,intent);
                        }
                    });
            if (alertConnectingtoNetwork == null) {
                alertConnectingtoNetwork = builder.create();
            }

            closeProgressDialog();
            alertConnectingtoNetwork.show();
            TextView messageView = (TextView) alertConnectingtoNetwork.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.CENTER);

        }
    }

    @Override
    public void connectivityOnReceiveCalled(Context context, Intent intent) {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

        String connectedSSID = getConnectedSSIDName(this);
        LibreLogger.d(TAG, "wifiConnect.mainSSID " + wifiConnect.mainSSID+"connectSSID"+connectedSSID);

        if (connectedSSID.equals(wifiConnect.mainSSID)
                && wifiInfo.getSupplicantState() == SupplicantState.INACTIVE) {
            LibreError error = new LibreError("Not able to connect ", wifiConnect.getMainSSID() +
                    "Authentication Error  , App Will be closed ");
            BusProvider.getInstance().post(error);
            mHandler.removeMessages(Constants.CONNECTED_TO_MAIN_SSID_FAIL);
            LibreLogger.d(APP_FORGROUND, "CONNECTED_TO_MAIN_SSID_FAIL connectivityOnReceiveCalled");
            mHandler.sendEmptyMessageDelayed(Constants.CONNECTED_TO_MAIN_SSID_FAIL, OOPS_TIMEOUT);
        }

        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED && !connectedSSID.isEmpty()) {
            if (connectedSSID.equals(wifiConnect.mainSSID)) {
                if (!isFinishing() && alertConnectingtoNetwork!=null && alertConnectingtoNetwork.isShowing())
                    alertConnectingtoNetwork.dismiss();

                mHandler.removeMessages(Constants.CONNECTED_TO_MAIN_SSID_FAIL);
                mHandler.removeMessages(Constants.CONNECTED_TO_MAIN_SSID_SUCCESS);
                mHandler.sendEmptyMessageDelayed(Constants.CONNECTED_TO_MAIN_SSID_SUCCESS,500);
            } else if (!(connectedSSID.contains(Constants.SA_SSID_RIVAA_CONCERT)
                    || connectedSSID.contains(Constants.SA_SSID_RIVAA_STADIUM))) {

                mHandler.removeMessages(Constants.TIMEOUT_FOR_SEARCHING_DEVICE);
                mHandler.removeMessages(Constants.SEARCHING_FOR_DEVICE);
                mHandler.removeMessages(Constants.CONNECTED_TO_MAIN_SSID_FAIL);
                mHandler.sendEmptyMessage(Constants.CONNECTED_TO_DIFFERENT_SSID);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unRegisterForDeviceEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fwInternetUpgradeMessage = null;
        mHandler.removeCallbacksAndMessages(null);
        mTaskHandlerForSendingMSearch.removeCallbacksAndMessages(null);
    }
}