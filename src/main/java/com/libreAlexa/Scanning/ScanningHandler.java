/*
 * ********************************************************************************************
 *  *
 *  * Copyright (C) 2014 Libre Wireless Technology
 *  *
 *  * "Libre Sync" Project
 *  *
 *  * Libre Sync Android App
 *  * Author: Android App Team
 *  *
 *  **********************************************************************************************
 */

package com.libreAlexa.Scanning;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import com.cumulations.libreV2.model.SceneObject;
import com.libreAlexa.LibreApplication;
import com.libreAlexa.app.dlna.dmc.server.ContentTree;
import com.libreAlexa.app.dlna.dmc.utility.PlaybackHelper;
import com.libreAlexa.app.dlna.dmc.utility.UpnpDeviceManager;
import com.libreAlexa.constants.Constants;
import com.libreAlexa.luci.LSSDPNodeDB;
import com.libreAlexa.luci.LSSDPNodes;
import com.libreAlexa.util.LibreLogger;
import com.libreAlexa.util.Sources;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import org.fourthline.cling.model.meta.RemoteDevice;

/**
 * Created by karunakaran on 8/1/2015.
 */
public class ScanningHandler {


    private static final String TAG = ScanningHandler.class.getSimpleName();
    private static ScanningHandler instance = new ScanningHandler();
    private ConcurrentHashMap<String, SceneObject> centralSceneObjectRepo = new ConcurrentHashMap<>();
    public Handler mSACHandler = null;
    public String secureIP ;
    public LSSDPNodeDB lssdpNodeDB = LSSDPNodeDB.getInstance();
    public static final int HN_MODE = 0;
    public static final int SA_MODE = 1;

    protected ScanningHandler() {
        // Exists only to defeat instantiation.
    }

    public void clearRemoveShuffleRepeatState(String mIpaddress) {
        RemoteDevice renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(mIpaddress);
        if (renderingDevice != null) {
            String renderingUDN = renderingDevice.getIdentity().getUdn().toString();
            PlaybackHelper playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP.get(renderingUDN);
            if (playbackHelper != null) {
                playbackHelper.setIsShuffleOn(false);
                playbackHelper.setRepeatState(0);
            }
        }
    }

    public static ScanningHandler getInstance() {
        if (instance == null) {
            instance = new ScanningHandler();
        }
        return instance;
    }

    public ConcurrentHashMap<String, SceneObject> getSceneObjectMapFromRepo() {
        return centralSceneObjectRepo;
    }


    public LSSDPNodes getLSSDPNodeFromCentralDB(String ip) {
        return lssdpNodeDB.getTheNodeBasedOnTheIpAddress(ip);
    }


    public void addHandler(Handler mHandler) {
        mSACHandler = mHandler;
    }

    public void removeHandler(Handler mHandler) {
        mSACHandler = null;
    }

    public Handler getHandler() {
        return mSACHandler;
    }


    public boolean findDupicateNode(LSSDPNodes mNode) {
        boolean found = false;
        int size = lssdpNodeDB.GetDB().size();
        /*preventing array index out of bound*/
        try {
            for (int i = 0; i < size; i++) {
                if (lssdpNodeDB.GetDB().get(i).getIP().equals(mNode.getIP())) {
                    lssdpNodeDB.renewLSSDPNodeDataWithNewNode(mNode);
                    found = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LibreLogger.d(TAG, "Exception occurred while finding duplicates");
        }
        return found;
    }


    public String parseHeaderValue(String content, String headerName) {
        Scanner s = new Scanner(content);
        s.nextLine(); // Skip the start line

        while (s.hasNextLine()) {
            String line = s.nextLine();
            if (line.equals(""))
                return null;
            int index = line.indexOf(':');
            if (index == -1)
                return null;
            String header = line.substring(0, index);
            if (headerName.equalsIgnoreCase(header.trim())) {
                /*Have Commented the Trim For parsing the DeviceState & FriendlyName with Space*/
                return line.substring(index + 1);
            }
        }

        return null;
    }

    private String parseStartLine(String content) {
        Scanner s = new Scanner(content);
        return s.nextLine();
    }

    public InetAddress getInetAddressFromSocketAddress(SocketAddress mSocketAddress) {
        InetAddress address = null;
        if (mSocketAddress instanceof InetSocketAddress) {
            address = ((InetSocketAddress) mSocketAddress).getAddress();

        }
        return address;
    }

    public SceneObject getSceneObjectFromCentralRepo(String mMasterIp) {
        try {
            return centralSceneObjectRepo.get(mMasterIp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearSceneObjectsFromCentralRepo() {
        centralSceneObjectRepo.clear();
    }

    public boolean isIpAvailableInCentralSceneRepo(String mMasterIP) {
        return centralSceneObjectRepo.containsKey(mMasterIP);
    }


    public ArrayList<LSSDPNodes> getFreeDeviceList() {
        ArrayList<LSSDPNodes> mFreeDevice = new ArrayList<>();
        LSSDPNodeDB mNodeDB = LSSDPNodeDB.getInstance();
        try {
            for (LSSDPNodes mNode : mNodeDB.GetDB()) {
                if ((mNode.getDeviceState().equals("F"))
                ) {

                    {

                        if (!mFreeDevice.contains(mNode))
                            mFreeDevice.add(mNode);
                    }
                    LibreLogger.d(TAG, "Current Source of Node " + mNode.getFriendlyname() +
                            " is " + mNode.getCurrentSource());
                }
                LibreLogger.d(TAG, "Get Slave List For MasterIp :" +
                        mFreeDevice.size() + "DB Size" +
                        mNodeDB.GetDB().size());
            }
        } catch (Exception e) {
            LibreLogger.d(TAG, e.getMessage() + "Exception happened while interating in getFreeDeviceList");
        }
        return mFreeDevice;
    }


    public boolean removeSceneMapFromCentralRepo(String mMasterIp) {
        LibreLogger.d(TAG, "Clearing :" + mMasterIp);
        centralSceneObjectRepo.remove(mMasterIp);
        clearRemoveShuffleRepeatState(mMasterIp);
        return !isIpAvailableInCentralSceneRepo(mMasterIp);
    }

    public void putSceneObjectToCentralRepo(String nodeMasterIp, SceneObject mScene) {
        /*Filtering for only Concert and Stadium devices*/
        centralSceneObjectRepo.put(nodeMasterIp, mScene);
    }

    public int getconnectedSSIDname(Context mContext) {
        WifiManager wifiManager;
        wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        LibreLogger.d(TAG, "getConnectedSSIDName wifiInfo = " + wifiInfo.toString());
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }

        LibreLogger.d(TAG, "Connected SSID" + ssid);
        if (ssid.contains(Constants.DDMS_SSID)) {
            return SA_MODE;
        } else {
            if (lssdpNodeDB.GetDB().size() > 0) {
                LSSDPNodes mSampleNode = lssdpNodeDB.GetDB().get(0);
                if (mSampleNode.getNetworkMode().equalsIgnoreCase("WLAN") == false) {
                    return SA_MODE;
                } else {
                    return HN_MODE;
                }
            }
        }
        return HN_MODE;
    }



    public Sources getSources(String mInputString) {
       // mInputString="LS10::7FFFFFFF";
        String beforeHexValue="";
        String mToBeOutput = null;
        try {
            int indexOfSemiColon = mInputString.indexOf("::");
            mToBeOutput = mInputString.substring(indexOfSemiColon + 2, mInputString.length());
            beforeHexValue = mInputString.substring(0, indexOfSemiColon);
        }catch (IndexOutOfBoundsException ex){
            LibreLogger.d(TAG, "Source list parse IndexOutOfBoundsException:-  " + ex.getMessage());
            mToBeOutput=mInputString;
        } catch (Exception ex){
            LibreLogger.d(TAG, "Source list parse Exception:-  " + ex.getMessage());
        }

        int loopPosition;
        if (beforeHexValue != null && beforeHexValue.length() > 0 && beforeHexValue.equals("LS10")) {
            loopPosition = 1;
        } else {
            loopPosition = 0;
        }
        LibreLogger.d(TAG, "Source list parse hexString " + mToBeOutput +" and loopPosition "+loopPosition);//f7ffffff - Hex Decimal

        String mHexString = mToBeOutput;
        LibreLogger.d(TAG, "getSource hexSrtring to String " + mHexString); //f7ffffff
        Sources mNewSources = new Sources();
        HashMap<String, Boolean> sourceList= new HashMap<>();
        BigInteger value;
        if (mHexString.startsWith("0x")) {
            value = new BigInteger(mHexString.substring(2), 16);
            LibreLogger.d(TAG, "getSource BigInteger if " + value);
        } else {
            value = new BigInteger(mHexString, 16); //16 means Hexa Decimal form String
            LibreLogger.d(TAG, "getSource BigInteger ELSE " + value); //ELSE 4160749567 Decimal
        }
        LibreLogger.d(TAG, "getSource final value " + value); //4160749567
        String valueBin = value.toString(2);
        LibreLogger.d(TAG, "getSource valueBin " + valueBin); //11110111111111111111111111111111 binary
        LibreLogger.d(TAG, "getSource valueBin length " + valueBin.length()); //32
        StringBuilder input1 = new StringBuilder();
        //appending 0 incase the string lenght is not 32
        if (valueBin.length() < 32) {
            int sizeOfZero = 32 - valueBin.length();
            for (int i = 0; i < sizeOfZero; i++) {
                valueBin = "0" + valueBin;
            }
        }
        // append a string into StringBuilder input1
        LibreLogger.d(TAG, "getSource final valueBin " + valueBin); //11110111111111111111111111111111
        input1.append(valueBin);
        // reverse StringBuilder input1
        input1 = input1.reverse();
        LibreLogger.d(TAG, "getSource after reverse " + input1); //11111111111111111111111111101111
        LibreLogger.d(TAG, "getSources, hex:" + mHexString + ",for 0:  " + valueBin.charAt(0) + ",for 28:  " + valueBin.charAt(28));//hex:f7ffffff,for 0:  1,for 28:  1
        for (int position = loopPosition; position < input1.length(); position++) {
            boolean mResult = false;
            if (input1.charAt(position) == '1') {
                mResult = true;
            }
            try {
                switch (position) {
                    case Constants.AIRPLAY_SOURCE:
                        mNewSources.setAirplay(mResult);
                        sourceList.put("Airplay",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.DMR_SOURCE:
                        mNewSources.setDmr(mResult);
                        sourceList.put("Dmr",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.DMP_SOURCE:
                        mNewSources.setDmp(mResult);
                        sourceList.put("Dmp",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.SPOTIFY_SOURCE:
                        mNewSources.setSpotify(mResult);
                        sourceList.put("Spotify",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.USB_SOURCE:
                        mNewSources.setUsb(mResult);
                        sourceList.put("USB",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.SDCARD_SOURCE:
                        mNewSources.setSDcard(mResult);
                        sourceList.put("SDCard",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.MELON_SOURCE:
                        mNewSources.setMelon(mResult);
                        sourceList.put("Melon",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.VTUNER_SOURCE:
                        mNewSources.setvTuner(mResult);
                        sourceList.put("VTuner",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.TUNEIN_SOURCE:
                        mNewSources.setTuneIn(mResult);
                        sourceList.put("TuneIn",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.MIRACAST_SOURCE:
                        mNewSources.setMiracast(mResult);
                        sourceList.put("Miracast",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.PLAY_LIST:
                        mNewSources.setPlaylist(mResult);
                        sourceList.put("Playlist",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.DDMSSLAVE_SOURCE:
                        mNewSources.setDDMS_Slave(mResult);
                        sourceList.put("DDMS SLAVE",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.AUX_SOURCE:
                        mNewSources.setAuxIn(mResult);
                        sourceList.put("AuxIn",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.APPLEDEVICE_SOURCE:
                        mNewSources.setAppleDevice(mResult);
                        sourceList.put("AppleDevice",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.DIRECTURL_SOURCE:
                        mNewSources.setDirect_URL(mResult);
                        sourceList.put("Direct URL",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.QPLAY:
                        mNewSources.setQPlay(mResult);
                        sourceList.put("QPlay",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.BT_SOURCE:
                        mNewSources.setBluetooth(mResult);
                        sourceList.put("Bluetooth",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.DEEZER_SOURCE:
                        mNewSources.setDeezer(mResult);
                        sourceList.put("Deezer",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.TIDAL_SOURCE:
                        mNewSources.setTidal(mResult);
                        sourceList.put("Tidal",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.FAVOURITES_SOURCE:
                        mNewSources.setFavourites(mResult);
                        sourceList.put("Favourites",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.GCAST_SOURCE:
                        mNewSources.setGoogleCast(mResult);
                        sourceList.put("Google Cast",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.EXTERNAL_SOURCE:
                        mNewSources.setExternalSource(mResult);
                        sourceList.put("External ",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.RTSP:
                        mNewSources.setRTSP(mResult);
                        sourceList.put("RTSP",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.ROON:
                        mNewSources.setRoon(mResult);
                        sourceList.put("Roon",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.ALEXA_SOURCE:
                        LibreLogger.d(TAG, "getSource FOR:-Alexa " + position+ " and position+1 "+(position+1) +" result "+mResult);
                        mNewSources.setAlexaAvsSource(mResult);
                        sourceList.put("Alexa Source",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;
                    case Constants.AIRABLE:
                        mNewSources.setAirable(mResult);
                        sourceList.put("Airable",mResult);
                        mNewSources.setCapitalCities(sourceList);
                        break;

                }
            } catch (Exception e) {
                LibreLogger.d(TAG, "getSources, Exception: " + e);
            }
        }
        LibreLogger.d(TAG, "getSources : " + mNewSources.toPrintString());
        return mNewSources;
    }

    private String mParseHexFromDeviceCap(String mInputString) {
        int indexOfSemoColon = mInputString.indexOf("::");
        String mToBeOutput = mInputString.substring(indexOfSemoColon + 2, mInputString.length());
        return mToBeOutput;
    }

    public LSSDPNodes getLSSDPNodeFromMessage(SocketAddress socketAddress, String inputString) {

        LSSDPNodes lssdpNodes;

        String DEFAULT_ZONEID = "239.255.255.251:3000";
        String s1 = parseStartLine(inputString);
        String usn = parseHeaderValue(inputString, "USN"); //Using 5
        String port = parseHeaderValue(inputString, "PORT"); //Using 12
        String deviceName = parseHeaderValue(inputString, "DeviceName");  //Using 13
        String state = parseHeaderValue(inputString, "State"); //Using 14 For now we are all speaker are free state should we keep this check
        String netMODE = parseHeaderValue(inputString, "NetMODE"); //Using 16 doubt
        String speakertype = parseHeaderValue(inputString, "SPEAKERTYPE"); //Using 17
        String ddmsConcurrentSSID = parseHeaderValue(inputString, "DDMSConcurrentSSID");//Using 15
        String castFwversion = parseHeaderValue(inputString, "CAST_FWVERSION"); //Using 8
        String fwversion = parseHeaderValue(inputString, "FWVERSION"); //Using 7
        String sourceList = parseHeaderValue(inputString, "SOURCE_LIST"); //Using 20
        String castTimezone = parseHeaderValue(inputString, "CAST_TIMEZONE"); // only Setting not getting Need to discuss wit Sathish
        String wifiband = parseHeaderValue(inputString, "WIFIBAND"); //Compltlry Not using

        String firstNotification = parseHeaderValue(inputString, "FN"); //Using 6

        String tcpport = parseHeaderValue(inputString, "TCPPORT"); //Using 18
        String zoneID = parseHeaderValue(inputString, "ZoneID"); //NOT USING Need to discuss wit Sathish

        /*For filtering*/
        String castModel = parseHeaderValue(inputString, "CAST_MODEL"); //Using 10
        LibreLogger.d(TAG, "getLSSDPNodeFromMessage: "+castModel+" castTimezone "+castTimezone+" castFwversion "+castFwversion);

        if (speakertype == null)
            speakertype = "0";

        if (zoneID == null || zoneID.equals("")) {
            zoneID = Constants.DEFAULT_ZONEID;
        }

        InetAddress address = null;
        if (socketAddress instanceof InetSocketAddress) {
            address = ((InetSocketAddress) socketAddress).getAddress();
        }

        lssdpNodes = new LSSDPNodes(
                address,
                deviceName,
                port,
                tcpport,
                state,
                speakertype,
                "0",
                usn,
                zoneID,
                ddmsConcurrentSSID);
        LibreLogger.d(TAG,"LSSDP  Socket Address : " + socketAddress.toString() + "InetAddress" + address.toString() + "Host ADDress" + address.getHostAddress());

        if (sourceList != null) {
            String[] mSplitUpDeviceCap = sourceList.split("::");
            lssdpNodes.createDeviceCap(mSplitUpDeviceCap[0], (mSplitUpDeviceCap[1]), getSources(sourceList));
        }

        if (wifiband != null) {
            lssdpNodes.setmWifiBand(wifiband);
        }

        /*LatestDiscoveryChanges
             setting first notification to LSSDP node*/
        if (firstNotification != null)
            lssdpNodes.setFirstNotification(firstNotification);

        lssdpNodes.setNetworkMode(netMODE);

        if (castModel!=null && !castModel.isEmpty())
            lssdpNodes.setCastModel(castModel);

        if (s1.equals(ScanThread.SL_NOTIFY)) {

            if (fwversion != null)
                lssdpNodes.setVersion(fwversion);

            /*adding for the google cast version */
            if (castFwversion != null)
                lssdpNodes.setgCastVerision(castFwversion);

            if (castTimezone != null)
                lssdpNodes.setmTimeZone(castTimezone);
        } else if (s1.equals(ScanThread.SL_OK)) {

            if (sourceList != null) {
                String[] mSplitUpDeviceCap = sourceList.split("::");
                lssdpNodes.createDeviceCap(mSplitUpDeviceCap[0], (mSplitUpDeviceCap[1]), getSources(sourceList));
            }

            if (wifiband != null) {
                lssdpNodes.setmWifiBand(wifiband);
            }

            /*adding for the google cast version */
            if (castFwversion != null)
                lssdpNodes.setgCastVerision(castFwversion);

            if (fwversion != null)
                lssdpNodes.setVersion(fwversion);

            if (castFwversion != null)
                lssdpNodes.setgCastVerision(castFwversion);

            if (castTimezone != null)
                lssdpNodes.setmTimeZone(castTimezone);
        }

        return lssdpNodes;
    }


    public boolean ToBeNeededToLaunchSourceScreen(SceneObject currentSceneObject) {
        if (currentSceneObject != null) {
            try {
                String masterIPAddress = currentSceneObject.getIpAddress();
                RemoteDevice renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(masterIPAddress);
                String renderingUDN = renderingDevice.getIdentity().getUdn().toString();
                PlaybackHelper playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP.get(renderingUDN);

                try {
                    if (playbackHelper != null
                            && playbackHelper.getDmsBrowseHelper() != null
                            && renderingDevice != null
                            && currentSceneObject != null
                            && currentSceneObject.getCurrentSource() == Constants.DMR_SOURCE
                            && currentSceneObject.getPlayUrl() != null
                            && !currentSceneObject.getPlayUrl().equalsIgnoreCase("")
                            && currentSceneObject.getPlayUrl().contains(LibreApplication.LOCAL_IP)
                            && currentSceneObject.getPlayUrl().contains(ContentTree.AUDIO_PREFIX)
                            && (currentSceneObject.getPlaystatus() != SceneObject.CURRENTLY_STOPPED
                            && currentSceneObject.getPlaystatus() != SceneObject.CURRENTLY_NOTPLAYING)

                    ) {

                        return false;
                    }
                } catch (Exception e) {

                    LibreLogger.d(TAG, "Handling the exception while sending the stopMediaServer command ");

                }
            } catch (Exception e) {
                LibreLogger.d(TAG, " 1 Handling the exception while sending the stopMediaServer command ");
            }
        }
        return true;
    }

    public void  setSecureIpaddress(String s) {
        this.secureIP=s;
    }
    public String  getSecureIP() {
        return secureIP;
    }

}
