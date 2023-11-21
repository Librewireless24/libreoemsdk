package com.libreAlexa.app.dlna.dmc.processor.upnp;



import static com.libreAlexa.constants.Constants.DMR_SOURCE;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.cumulations.libreV2.AppUtils;
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity;
import com.cumulations.libreV2.model.SceneObject;
import com.cumulations.libreV2.tcp_tunneling.TunnelingClientRunnable;
import com.cumulations.libreV2.tcp_tunneling.TunnelingControl;
import com.libreAlexa.DMRPlayerService;
import com.libreAlexa.LibreApplication;
import com.libreAlexa.Ls9Sac.FwUpgradeData;
import com.libreAlexa.Scanning.ScanningHandler;
import com.libreAlexa.alexa.AudioRecordUtil;
import com.libreAlexa.app.dlna.dmc.server.MusicServer;
import com.libreAlexa.app.dlna.dmc.utility.PlaybackHelper;
import com.libreAlexa.app.dlna.dmc.utility.UpnpDeviceManager;
import com.libreAlexa.constants.Constants;
import com.libreAlexa.constants.LSSDPCONST;
import com.libreAlexa.constants.LUCIMESSAGES;
import com.libreAlexa.constants.MIDCONST;
import com.libreAlexa.luci.LSSDPNodeDB;
import com.libreAlexa.luci.LSSDPNodes;
import com.libreAlexa.luci.LUCIControl;
import com.libreAlexa.luci.LUCIPacket;
import com.libreAlexa.netty.BusProvider;
import com.libreAlexa.netty.LibreDeviceInteractionListner;
import com.libreAlexa.netty.NettyData;
import com.libreAlexa.netty.RemovedLibreDevice;
import com.libreAlexa.util.LibreLogger;
import com.squareup.otto.Subscribe;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.android.AndroidRouter;
import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import org.json.JSONObject;

public class CoreUpnpService extends AndroidUpnpServiceImpl implements LibreDeviceInteractionListner {
    private UpnpService upnpService;
    private Binder binder = new Binder();
    private   LibreDeviceInteractionListner libreDeviceListener = null;

    String TAG = CoreUpnpService.class.getSimpleName();
    @Override
    protected AndroidRouter createRouter(UpnpServiceConfiguration configuration, ProtocolFactory protocolFactory, Context context) {
        return super.createRouter(configuration, protocolFactory, context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LibreLogger.d(TAG, "CoreUpnpService is getting created");
        //startDmrPlayerService();

        registerforDeviceEvents(this);
        upnpService = new UpnpServiceImpl(createConfiguration());
        try {
            BusProvider.getInstance().register(busEventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected AndroidUpnpServiceConfiguration createConfiguration(WifiManager wifiManager) {
        return new AndroidUpnpServiceConfiguration();
    }

	/*protected AndroidWifiSwitchableRouter createRouter(UpnpServiceConfiguration configuration, ProtocolFactory protocolFactory,
            WifiManager wifiManager, ConnectivityManager connectivityManager) {
		return new AndroidWifiSwitchableRouter(configuration, protocolFactory, wifiManager, connectivityManager);
	}*/

    @Override
    public void onDestroy() {
        //unregisterReceiver(((AndroidWifiSwitchableRouter) upnpService.getRouter()).getBroadcastReceiver());
        unregisterDeviceEvents();
        try {

            /*Gets called after screen lock and no activity is binding to service*/
            LibreLogger.d(TAG, "CoreUpnpService is  Trying shutting down");


            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        upnpService.shutdown();
                        LibreLogger.d(TAG, "CoreUpnpService is  shutting down is Completed");
                    } catch (Exception ex) {
                        LibreLogger.d(TAG, "CoreUpnpService is  Trying to WifiLock  Got Exception" + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }).start();*/

           // BusProvider.getInstance().unregister(busEventListener);

        } catch (Exception ex) {
            LibreLogger.d(TAG, "CoreUpnpService is  Trying to shutting down but Got Exception"
                    + ex.getMessage());
            ex.printStackTrace();
        }


        super.onDestroy();
        //m_httpThread.stopHttpThread();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    protected boolean isListeningForConnectivityChanges() {
        return true;
    }

    @Override
    public void deviceDiscoveryAfterClearingTheCacheStarted() {

    }

    @Override
    public void newDeviceFound(LSSDPNodes node) {

    }

    @Override
    public void deviceGotRemoved(String ipaddress) {

    }

    @Override
    public void messageRecieved(NettyData packet) {

    }

    public class Binder extends android.os.Binder implements AndroidUpnpService {

        public UpnpService get() {
            return upnpService;
        }

        public UpnpServiceConfiguration getConfiguration() {
            return upnpService.getConfiguration();
        }

        public Registry getRegistry() {
            return upnpService.getRegistry();
        }

        public ControlPoint getControlPoint() {
            return upnpService.getControlPoint();
        }

        public void renewUpnpBinder() {

            Collection<RegistryListener> listeners = upnpService.getRegistry().getListeners();
            try {
                upnpService.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
                LibreLogger.d(TAG,"CoreUpnpService Exception while switching networks");
            }

            upnpService = new UpnpServiceImpl(createConfiguration());
            upnpService.getRegistry().removeAllRemoteDevices();

            for (RegistryListener list : listeners) {
                upnpService.getRegistry().addListener(list);
            }
        }


    }

    public void removeDeviceSceneFromRepo(String ipadddress) {
        if (ipadddress != null) {
            LUCIControl.luciSocketMap.remove(ipadddress);

            LibreApplication.securecertExchangeSucessDevices.clear();

            BusProvider.getInstance().post(new RemovedLibreDevice(ipadddress));
            try {
                if (ScanningHandler.getInstance().isIpAvailableInCentralSceneRepo(ipadddress)) {
                    boolean status = ScanningHandler.getInstance().removeSceneMapFromCentralRepo(ipadddress);
                    LibreLogger.d(TAG, "Active Scene Adapter For the Master " + status);
                }
            } catch (Exception e) {
                LibreLogger.d(TAG, "Active Scene Adapter" + "Removal Exception ");
            }
            LSSDPNodeDB.getInstance().clearNode(ipadddress);
        }
    }


    Object busEventListener = new Object() {


        @Subscribe
        public void newDeviceFound(final LSSDPNodes nodes) {
            LibreLogger.d(TAG, "newDeviceFound, node = " + nodes.getFriendlyname());
            CTDeviceDiscoveryActivity ctDeviceDiscoveryActivity = new CTDeviceDiscoveryActivity();
            ctDeviceDiscoveryActivity.insertDeviceIntoDbFromCoreUPNP(nodes);

//            if (libreDeviceListener != null) {
//                libreDeviceListener.newDeviceFound(nodes);
//            }
            /* This below if loop is introduced to handle the case where Device state from the DUT could be Null sometimes
             * Ideally the device state should not be null but we are just handling it to make sure it will not result in any crash!
             *
             * */

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    checkForUPnPReinitialization();
                }
            });

            createOrUpdateTunnelingClients(nodes);

            if (nodes == null || nodes.getDeviceState() == null) {
                Toast.makeText(CoreUpnpService.this, "Alert! Device State is null " + nodes.getDeviceState(), Toast.LENGTH_SHORT).show();
            } else if (nodes.getDeviceState() != null) {
                FwUpgradeData mGCastData = LibreApplication.FW_UPDATE_AVAILABLE_LIST.get(nodes.getIP());
                if (mGCastData != null) {
                    LibreApplication.FW_UPDATE_AVAILABLE_LIST.remove(nodes.getIP());
                }
                ArrayList<LUCIPacket> luciPackets = new ArrayList<LUCIPacket>();
                LUCIControl control = new LUCIControl(nodes.getIP());
               /* NetworkInterface mNetIf = com.libreAlexa.luci.Utils.getActiveNetworkInterface();
                String messageStr=  com.libreAlexa.luci.Utils.getLocalV4Address(mNetIf).getHostAddress() + "," + LUCIControl.LUCI_RESP_PORT;
                LUCIPacket packet1 = new LUCIPacket(messageStr.getBytes(), (short) messageStr.length(), (short) 3, (byte) LSSDPCONST.LUCI_SET);
                luciPackets.add(packet1);*/

                String readBTController = "READ_BT_CONTROLLER";

                LUCIPacket packet = new LUCIPacket(readBTController.getBytes(), (short) readBTController.length(),
                        (short) MIDCONST.MID_ENV_READ, (byte) LSSDPCONST.LUCI_GET);
                luciPackets.add(packet);

                String readAlexaRefreshToken = "READ_AlexaRefreshToken";
                LUCIPacket alexaPacket = new LUCIPacket(readAlexaRefreshToken.getBytes(), (short) readAlexaRefreshToken.length(),
                        (short) MIDCONST.MID_ENV_READ, (byte) LSSDPCONST.LUCI_GET);
                luciPackets.add(alexaPacket);


                String readModelType = "READ_Model";
                /*sending first 208*/
                LUCIPacket packet3 = new LUCIPacket(readModelType.getBytes(),
                        (short) readModelType.length(), (short) MIDCONST.MID_ENV_READ, (byte) LSSDPCONST.LUCI_GET);
                luciPackets.add(packet3);

                LUCIPacket packet2 = new LUCIPacket(null, (short) 0, (short) MIDCONST.VOLUME_CONTROL, (byte) LSSDPCONST.LUCI_GET);
                luciPackets.add(packet2);

                /*LUCIPacket packet3 = new LUCIPacket(null, (short) 0, (short) MIDCONST.NEW_SOURCE, (byte) LSSDPCONST.LUCI_GET);
                luciPackets.add(packet3);*/

                if (nodes.getgCastVerision() != null) {
                    LibreLogger.d(TAG, "Sending GCAST 226 value read command");
                    LUCIPacket packet4 = new LUCIPacket(null, (short) 0, (short) MIDCONST.GCAST_TOS_SHARE_COMMAND, (byte) LSSDPCONST.LUCI_GET);
                    luciPackets.add(packet4);
                }

                LUCIPacket packet6 = new LUCIPacket(null, (short) 0, (short) MIDCONST.MID_CURRENT_SOURCE, (byte) LSSDPCONST.LUCI_GET);
                luciPackets.add(packet6);
                LUCIPacket packet7 = new LUCIPacket(null, (short) 0, (short) MIDCONST.MID_CURRENT_PLAY_STATE, (byte) LSSDPCONST.LUCI_GET);
                luciPackets.add(packet7);

//                if (nodes.getDeviceState().contains("M")) {
//                    LUCIPacket packet5 = new LUCIPacket(null, (short) 0, (short) MIDCONST.ZONE_VOLUME, (byte) LSSDPCONST.LUCI_GET);
//                    luciPackets.add(packet5);
//                }

                control.SendCommand(luciPackets);
                if (nodes.getDeviceState() != null) {
                    if (!ScanningHandler.getInstance().isIpAvailableInCentralSceneRepo(nodes.getIP())) {
                        SceneObject sceneObjec = new SceneObject(" ", nodes.getFriendlyname(), 0, nodes.getIP());
                        ScanningHandler.getInstance().putSceneObjectToCentralRepo(nodes.getIP(), sceneObjec);
                        LibreLogger.d(TAG, "Device is not available in Central Repo So Created SceneObject " + nodes.getIP());
                    }
                }


            }
        }

        public void changeDeviceStateStereoConcurrentZoneids(String mIpAddress, String mMessage) {
            ScanningHandler mScanHandler = ScanningHandler.getInstance();
            LSSDPNodes mToBeUpdateNode = mScanHandler.getLSSDPNodeFromCentralDB(mIpAddress);
            if (mToBeUpdateNode != null && mMessage != null) {
                String[] mMessageArray = mMessage.split(",");
                if (mMessageArray.length > 0) {
                    if (mMessageArray[0].equalsIgnoreCase("Master")) {
                        mToBeUpdateNode.setDeviceState("M");
                        /* Create Scene Object */

                        if (!ScanningHandler.getInstance().isIpAvailableInCentralSceneRepo(mIpAddress)) {
                            SceneObject sceneObjec = new SceneObject(" ", mToBeUpdateNode.getFriendlyname(), 0, mIpAddress);
                            ScanningHandler.getInstance().putSceneObjectToCentralRepo(mToBeUpdateNode.getIP(), sceneObjec);
                            LibreLogger.d(TAG, "Master is not available in Central Repo So Created SceneObject " + mToBeUpdateNode.getIP());
                        }
                    }
                    if (mMessageArray[0].equalsIgnoreCase("Slave")) {
                        mToBeUpdateNode.setDeviceState("S");
                        mToBeUpdateNode.setCurrentState(LSSDPNodes.STATE_CHANGE_STAGE.CHANGE_SUCCESS);
                        /* Remove the SceneObject if a Device transition is happened */
                        ScanningHandler.getInstance().removeSceneMapFromCentralRepo(mIpAddress);
                    }
                    if (mMessageArray[0].equalsIgnoreCase("Free")) {
                        mToBeUpdateNode.setDeviceState("F");
                        mToBeUpdateNode.setCurrentState(LSSDPNodes.STATE_CHANGE_STAGE.CHANGE_SUCCESS);
                        /* Remove the SceneObject if a Device transition is happened */
                        ScanningHandler.getInstance().removeSceneMapFromCentralRepo(mIpAddress);
                    }

                    if (mMessageArray[1].equalsIgnoreCase("STEREO")) {
                        mToBeUpdateNode.setSpeakerType("0");
                    }
                    if (mMessageArray[1].equalsIgnoreCase("LEFT")) {
                        mToBeUpdateNode.setSpeakerType("1");
                    }
                    if (mMessageArray[1].equalsIgnoreCase("RIGHT")) {
                        mToBeUpdateNode.setSpeakerType("2");
                    }
                    if (mToBeUpdateNode.getNetworkMode().equalsIgnoreCase("WLAN")) {
                        mToBeUpdateNode.setcSSID(mMessageArray[2]);
                    } else {
                        mToBeUpdateNode.setZoneID(mMessageArray[2]);
                    }
                    LSSDPNodeDB.getInstance().renewLSSDPNodeDataWithNewNode(mToBeUpdateNode);
                }
            }
        }

        @Subscribe
        public void newMessageRecieved(final NettyData nettyData) {
            LUCIPacket dummyPacket = new LUCIPacket(nettyData.getMessage());
            LibreLogger.d(TAG, "newMessageRecieved, ip:" + nettyData.getRemotedeviceIp()
                    +"\tMB:" + dummyPacket.getCommand()
                    + "\tmsg:" + new String(dummyPacket.getpayload()));

//            if (nettyData != null) {
//                libreDeviceListener.messageRecieved(nettyData);
//            }
            /*Updating the last notified Time for all the Device*/
            if (nettyData != null) {
                if (LUCIControl.luciSocketMap.containsKey(nettyData.getRemotedeviceIp()))
                    LUCIControl.luciSocketMap.get(nettyData.getRemotedeviceIp()).setLastNotifiedTime(System.currentTimeMillis());
            }


            LUCIPacket packet = new LUCIPacket(nettyData.getMessage());

            if (packet.getCommandStatus() == 1) {
                LibreLogger.d(TAG, "Command status 1 recieved for " + packet.getCommandStatus());

            }

            /*For RIVA speakers, during AUX we won't get volume in MB 64 for volume changes
             * done through speaker hardware buttons*/

            /*if (packet.getCommand() == MIDCONST.VOLUME_CONTROL) {
                String volumeMessage = new String(packet.getpayload());
                try {
                    int volume = Integer.parseInt(volumeMessage);
                    LibreApplication.INDIVIDUAL_VOLUME_MAP.put(nettyData.getRemotedeviceIp(), volume);
                    LibreLogger.d(TAG,"VolumeMapUpdating", "INDIVIDUAL_VOLUME_MAP " + volume);
                } catch (Exception e) {
                    LibreLogger.d(TAG, "Exception occurred in newMessageReceived");
                }

            }*/

            if (packet.getCommand() == MIDCONST.SET_UI) {

                try {
                    LibreLogger.d(TAG, "Recieved Playback url in 42 at coreup");
                    String message = new String(packet.payload);
                    LibreLogger.d(TAG, "Recieved Playback url in 42 at coreup message"+message);

                    JSONObject root = new JSONObject(message);
                    int cmd_id = root.getInt(LUCIMESSAGES.TAG_CMD_ID);

                    if (cmd_id == 3) {
                        JSONObject window = root.getJSONObject(LUCIMESSAGES.ALEXA_KEY_WINDOW_CONTENT);
                        ScanningHandler mScanHandler = ScanningHandler.getInstance();
                        SceneObject mSceneObj = mScanHandler.getSceneObjectFromCentralRepo(nettyData.getRemotedeviceIp());

                        mSceneObj.setPlayUrl(window.getString("PlayUrl").toLowerCase());
                        mSceneObj.setAlbum_art(window.getString("CoverArtUrl"));
                        mSceneObj.setArtist_name(window.getString("Artist"));
                        mSceneObj.setTotalTimeOfTheTrack(window.getLong("TotalTime"));
                        mSceneObj.setTrackName(window.getString("TrackName"));
                        mSceneObj.setAlbum_name(window.getString("Album"));
                        LibreLogger.d(TAG, "PLAY JSON ARTIST NAME"+mSceneObj.getArtist_name());

                        LibreApplication.currentAlbumnName =mSceneObj.getAlbum_name();
                        LibreApplication. currentArtistName=mSceneObj.getArtist_name();
                        LibreApplication.currentAlbumArtView=mSceneObj.getAlbum_art();
                        LibreApplication.currentTrackName=mSceneObj.getTrackName();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }



            /*if (packet.getCommand() == MIDCONST.ZONE_VOLUME) {
                String volumeMessage = new String(packet.getpayload());
                try {
                    int volume = Integer.parseInt(volumeMessage);
                    /*this map is having Masters volume/*
                    LibreApplication.ZONE_VOLUME_MAP.put(nettyData.getRemotedeviceIp(), volume);
                    LibreLogger.d(TAG,"VolumeMapUpdating", "ZONE_VOLUME " + volume);
                } catch (Exception e) {
                    LibreLogger.d(TAG, "Exception occurred in newMessageReceived");
                }
            }*/

            if (packet.getCommand() == MIDCONST.MID_SCENE_NAME) {
                String msg = new String(packet.getpayload());
                try {
                    LibreLogger.d(TAG, "Scene Name updation in CTDeviceDiscoveryActivity");
                         /* if command Type 1 , then Scene Name information will be come in the same packet
if command type 2 and command status is 1 , then data will be empty., at that time we should not update the value .*/
                    if (packet.getCommandStatus() == 1
                            && packet.getCommandType() == 2)
                        return;
                    ScanningHandler mScanHandler = ScanningHandler.getInstance();
                    SceneObject mSceneObj = mScanHandler.getSceneObjectFromCentralRepo(nettyData.getRemotedeviceIp());
                    if (mSceneObj != null)
                        mSceneObj.setSceneName(msg);
                } catch (Exception e) {
                    LibreLogger.d(TAG, "Exception occurred in newMessageReceived");
                }
            }

            /*this has been added for DMR hijacking issue*/
            if (packet.getCommand() == MIDCONST.NEW_SOURCE) {
                String msg = new String(packet.getpayload());
                try {
                    int duration = Integer.parseInt(msg);
                    LibreLogger.d(TAG, "Current source updation in CTDeviceDiscoveryActivity");
                    LibreLogger.d(TAG,"DMR_CURRENT_SOURCE_BASE" + duration);
                    ScanningHandler mScanHandler = ScanningHandler.getInstance();
                    SceneObject mSceneObj = mScanHandler.getSceneObjectFromCentralRepo(nettyData.getRemotedeviceIp());
                    LibreLogger.d(TAG,"NEW CURRENT SRC" + mSceneObj.getCurrentSource());

                    mSceneObj.setCurrentSource(duration);
                    LSSDPNodeDB mLssdpNodeDb = LSSDPNodeDB.getInstance();
                    final LSSDPNodes mNode = mLssdpNodeDb.getTheNodeBasedOnTheIpAddress(nettyData.getRemotedeviceIp());
                    if (mNode != null) {
                        mNode.setCurrentSource(duration);
                    }
                    mLssdpNodeDb.renewLSSDPNodeDataWithNewNode(mNode);
                } catch (Exception e) {
                    LibreLogger.d(TAG, "Exception occurred in newMessageReceived");
                }
            }
            /* Device State ACK Implementation */
            if (packet.getCommand() == MIDCONST.MID_DEVICE_STATE_ACK) {
                try {
                    changeDeviceStateStereoConcurrentZoneids(nettyData.getRemotedeviceIp(), new String(packet.getpayload()));
                } catch (Exception e) {
                    e.printStackTrace();
                    LibreLogger.d(TAG, "Exception occurred in newMessageReceived of 103");
                }
            }

            if (packet.getCommand() == MIDCONST.NETWORK_STATUS_CHANGE) {
                String msg = new String(packet.getpayload());
                try {

                    LibreLogger.d(TAG, "Current NetworkS Status updation in CTDeviceDiscoveryActivity" + nettyData.getRemotedeviceIp());
                    LibreLogger.d(TAG, "Current Net status" + msg);
                        /*ScanningHandler mScanHandler = ScanningHandler.getInstance();
                        SceneObject mSceneObj = mScanHandler.getSceneObjectFromCentralRepo(nettyData.getRemotedeviceIp());
                        if (mSceneObj != null)
                            mSceneObj.setCurrentSource(duration);*/
                } catch (Exception e) {
                    LibreLogger.d(TAG, "Exception occurred in newMessageReceived");
                }
            }
            if (packet.getCommand() == MIDCONST.MID_ENV_READ) {
                try {
                    // for getting env item
                    String messages = new String(packet.getpayload());
                    LibreLogger.d(TAG, "MID_ENV_READ value is " + messages);

                    if (messages.contains("speechvolume")) {
                        String speechvolume = String.valueOf(messages.substring(messages.indexOf(":") + 1)); /*40,1*/
                        LibreLogger.d(TAG, "got READ_speechvolume " + messages);
                        LSSDPNodeDB mNodeDB = LSSDPNodeDB.getInstance();
                        LSSDPNodes mNode = mNodeDB.getTheNodeBasedOnTheIpAddress(nettyData.getRemotedeviceIp());
                        if (mNode != null) {
                            mNode.setSpeechVolume(speechvolume);
                        }
                    }
                    if (messages.contains("AlexaRefreshToken")) {
                        String token = messages.substring(messages.indexOf(":") + 1);
                        LibreLogger.d(TAG, "AlexaRefreshToken value " + token);
                        LSSDPNodeDB mNodeDB = LSSDPNodeDB.getInstance();
                        LSSDPNodes mNode = mNodeDB.getTheNodeBasedOnTheIpAddress(nettyData.getRemotedeviceIp());
                        if (mNode != null) {
                            mNode.setAlexaRefreshToken(token);
                            mNodeDB.renewLSSDPNodeDataWithNewNode(mNode);
                        }
                    } else {
                        int value = Integer.parseInt(messages.substring(messages.indexOf(":") + 1));
                        LibreLogger.d(TAG, "BT_CONTROLLER value after parse is " + value);

                        ScanningHandler mScanHandler = ScanningHandler.getInstance();
                        SceneObject mSceneObj = mScanHandler.getSceneObjectFromCentralRepo(nettyData.getRemotedeviceIp());
                        mSceneObj.setBT_CONTROLLER(value);
                    }

                } catch (Exception e) {
                    LibreLogger.d(TAG, "error in 208 " + e.toString());
                }
            }


            if (packet.getCommand() == MIDCONST.MID_CURRENT_SOURCE) {
                String msg = new String(packet.getpayload());
                try {
                    int currentSource = Integer.parseInt(msg);
                    LibreLogger.d(TAG,"DMR_CURRENT_SOURCE "+ currentSource);
                    SceneObject mSceneObj = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(nettyData.getRemotedeviceIp());
                    if (mSceneObj != null && mSceneObj.getCurrentSource()!=currentSource) {
                        mSceneObj.setCurrentSource(currentSource);
                    }
                    final LSSDPNodes mNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(nettyData.getRemotedeviceIp());
                    if (mNode != null && mNode.getCurrentSource()!=currentSource) {
                        mNode.setCurrentSource(currentSource);
                        LSSDPNodeDB.getInstance().renewLSSDPNodeDataWithNewNode(mNode);
                    }
                } catch (Exception e) {
                    LibreLogger.d(TAG, "Exception occurred in newMessageReceived");
                }
            }

            if (packet.getCommand() == MIDCONST.MID_CURRENT_PLAY_STATE) {
                String msg = new String(packet.getpayload());
                try {
                    int playStatus = Integer.parseInt(msg);
                    LibreLogger.d(TAG,"MID_CURRENT_PLAY_STATE"+ playStatus);
                    SceneObject sceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(nettyData.getRemotedeviceIp());
                    if (sceneObject != null && sceneObject.getPlaystatus()!=playStatus) {
                        sceneObject.setPlaystatus(playStatus);
                        if (playStatus == SceneObject.CURRENTLY_PLAYING || playStatus == SceneObject.CURRENTLY_PAUSED) {
                            if (isAnyDeviceLocalDMRPlaying()) {
                            //  startDmrPlayerService();

                            }
                        }

                        if (playStatus == SceneObject.CURRENTLY_STOPPED || playStatus == SceneObject.CURRENTLY_NOTPLAYING){
                            if (!isAnyDeviceLocalDMRPlaying()){
                                stopDmrPlayerService();
                            }
                        }
                    }

                    final LSSDPNodes mNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(nettyData.getRemotedeviceIp());
                    if (mNode != null && mNode.getmPlayStatus()!=playStatus) {
                        mNode.setmPlayStatus(playStatus);
                        LSSDPNodeDB.getInstance().renewLSSDPNodeDataWithNewNode(mNode);
                    }
                } catch (Exception e) {
                    LibreLogger.d(TAG, "Exception occurred in newMessageReceived");
                }
            }
            /* Sending Whenver a command 3 When i got Zero From Device */
            if (packet.getCommand() == 0) {
                new LUCIControl(nettyData.getRemotedeviceIp()).sendAsynchronousCommandSpecificPlaces();
            }

        }

        @Subscribe
        public void deviceGotRemoved(String ipaddress) {
            LibreLogger.d(TAG, "deviceGotRemoved, ip " + ipaddress);
            removeDeviceSceneFromRepo(ipaddress);
        }
    };

    private void createOrUpdateTunnelingClients(final LSSDPNodes mInputNode) {
        /* It will not Wait for Socket to be created */
        try {
            if (TunnelingControl.isTunnelingClientPresent(mInputNode.getIP())) {
                Socket mExistingSocket = TunnelingControl.getTunnelingClient(mInputNode.getIP());
                LibreLogger.d(TAG, "createOrUpdateTunnelingClients, socket ip " + mInputNode.getIP() + " is connected " + mExistingSocket.isConnected());
                /*Socket is Already Exists*/
                if (!mExistingSocket.isConnected()) {
                    mExistingSocket.close();
                    TunnelingControl.removeTunnelingClient(mInputNode.getIP());
                    new Thread(new TunnelingClientRunnable(mInputNode.getIP())).start();
                }
            } else {
                new Thread(new TunnelingClientRunnable(mInputNode.getIP())).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
            LibreLogger.d(TAG, "createOrUpdateTunnelingClients, exception = " + e.getMessage());
        }
    }


    private synchronized void checkForUPnPReinitialization() {
        String localIpAddress = AppUtils.INSTANCE.getWifiIp(this);
        try {
            LibreLogger.d(TAG, "checkForUPnPReinitialization Phone ip " + localIpAddress + ", LOCAL_IP " + LibreApplication.LOCAL_IP);
            /*if its an Empty String, Contains will pass  , because contains checking >=0 ; if its Not Empty or Not equal we need ot reinitate it*/
            if (localIpAddress != null
                    && (LibreApplication.LOCAL_IP.isEmpty()
                    || !localIpAddress.equalsIgnoreCase(LibreApplication.LOCAL_IP))) {

                LibreLogger.d(TAG, "Phone ip and upnp ip is different");
                LibreApplication.LOCAL_IP = localIpAddress;
                binder.renewUpnpBinder();
                MusicServer ms = MusicServer.getMusicServer();
                ms.clearMediaServer();
                UpnpDeviceManager.getInstance().clearMaps();

                LibreApplication.PLAYBACK_HELPER_MAP = new HashMap<String, PlaybackHelper>();
                binder.getRegistry().addDevice(ms.getMediaServer().getDevice());
                LibreApplication.LOCAL_UDN = ms.getMediaServer().getDevice().getIdentity().getUdn().toString();

                startService(new Intent(CoreUpnpService.this, LoadLocalContentService.class));
//              ((LibreApplication) getApplication()).setControlPoint(binder.getControlPoint());
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            LibreLogger.d(TAG, "Exception " + ee.getMessage());
        }
    }

    private boolean isAnyDeviceLocalDMRPlaying() {
        boolean playing = false;
        for (Map.Entry<String, SceneObject> entry : ScanningHandler.getInstance().getSceneObjectMapFromRepo().entrySet()) {
            LibreLogger.d(TAG, "isAnyDeviceLocalDMRPlaying, ip " + entry.getKey());
            SceneObject sceneObject = entry.getValue();
            if (sceneObject != null
                    && sceneObject.getPlayUrl() != null
                    && !sceneObject.getPlayUrl().isEmpty()
                    && sceneObject.getPlayUrl().contains(LibreApplication.LOCAL_IP)
                    && sceneObject.getCurrentSource() == DMR_SOURCE
                    && (sceneObject.getPlaystatus() == SceneObject.CURRENTLY_PLAYING || sceneObject.getPlaystatus() == SceneObject.CURRENTLY_PAUSED)) {
                playing = true;
                break;
            }
        }

        return playing;
    }

    private void startDmrPlayerService() {
//        if (Build.VERSION.SDK_INT >= 26) {
//            getApplicationContext().startForegroundService(new Intent(this, DMRPlayerService.class)
//                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    .setAction(Constants.ACTION.START_DMR_SERVICE));
//        } else {
//            // Pre-O behavior.
//            Intent serviceIntent = new Intent(CoreUpnpService.this, DMRPlayerService.class);
//
//            getApplicationContext().startService(serviceIntent);
//        }

        try {
            ContextCompat.startForegroundService(this,
                    new Intent(this, DMRPlayerService.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .setAction(Constants.ACTION.START_DMR_SERVICE));
        } catch (Exception e){
            e.printStackTrace();
            LibreLogger.d(TAG,"startDmrPlayerService, exception "+e.getMessage());
        }
    }

      public void registerforDeviceEvents(LibreDeviceInteractionListner libreDeviceInteractionListner){
          this.libreDeviceListener = libreDeviceInteractionListner;

      }

      public void unregisterDeviceEvents(){
          libreDeviceListener=null;
      }
    private void stopDmrPlayerService(){
        stopService(new Intent(this, DMRPlayerService.class));
    }
}
