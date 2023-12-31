package com.libreAlexa.luci;
/*
 Copyright (C) 2014 Libre Wireless Technology
 <p/>
 "Junk Yard Lab" Project
 <p/>
 Libre Sync Android App
 Author: Subhajeet Roy
 */

import android.util.Log;
import com.cumulations.libreV2.tcp_tunneling.TunnelingControl;
import com.cumulations.libreV2.tcp_tunneling.enums.ModelId;
import com.libreAlexa.Scanning.ScanningHandler;
import com.libreAlexa.util.LibreLogger;
import java.util.ArrayList;

public class LSSDPNodeDB {
    private static final String TAG = LSSDPNodeDB.class.getSimpleName();
    private static LSSDPNodeDB LSSDPDB = new LSSDPNodeDB();
    private static ArrayList<LSSDPNodes> nodelist = new ArrayList<>();

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private LSSDPNodeDB() {
    }

    /* Static 'instance' method */
    public static LSSDPNodeDB getInstance() {

        return LSSDPDB;

    }

    public ArrayList<LSSDPNodes> GetDB() {
        return nodelist;
    }

    public boolean checkDataIsAvailable(String mNodeUSN) {
        for (LSSDPNodes mNode : nodelist) {
            if (mNode.getUSN().equals(mNodeUSN)) {
                return true;
            }
        }
        return false;
    }

    public void addToNodeDb(LSSDPNodes node) {/* USN may be Empty When Device is transiting From SA State to Another State*/
        if(node.getUSN().isEmpty()){
            return;
        }

        if (!checkDataIsAvailable(node.getUSN())) {
            nodelist.add(node);
        }
    }

    public void removeFromDB(LSSDPNodes node) {
        nodelist.remove(node);
    }

    public void clearDB() {
        nodelist.clear();
        deviceNameList.clear();
        ScanningHandler mHandler = ScanningHandler.getInstance();
    }


    public ArrayList<String> deviceNameList = new ArrayList<>();

    public LSSDPNodes getLSSDPNodeFromPosition(int position) {
        return nodelist.get(position);
    }

    public ArrayList<String> getLSSDPDeviceNameList() {
        return deviceNameList;
    }

    public void renewLSSDPNodeDataWithNewNode(LSSDPNodes mToBeChange) {
        int size = GetDB().size();
        for (int i = 0; i < size; i++) {
            if (GetDB().get(i).getIP().equals(mToBeChange.getIP())) {

                if (!GetDB().get(i).getFriendlyname().equals(mToBeChange.getFriendlyname())) {
                    GetDB().get(i).setFriendlyname(mToBeChange.getFriendlyname());
                }

                if (!GetDB().get(i).getDeviceState().equals(mToBeChange.getDeviceState())) {
                    GetDB().get(i).setDeviceState(mToBeChange.getDeviceState());
                }

                if (!GetDB().get(i).getNetworkMode().equals(mToBeChange.getNetworkMode())) {
                    GetDB().get(i).setNetworkMode(mToBeChange.getNetworkMode());
                }

                if (!GetDB().get(i).getSpeakerType().equals(mToBeChange.getSpeakerType())) {
                    GetDB().get(i).setSpeakerType(mToBeChange.getSpeakerType());
                }

                if (!GetDB().get(i).getZoneID().equals(mToBeChange.getZoneID())) {
                    if (!mToBeChange.getZoneID().equals(""))
                        GetDB().get(i).setZoneID(mToBeChange.getZoneID());
                }
                if(GetDB().get(i).getcSSID()!=null) {
                    if (!GetDB().get(i).getcSSID().equals(mToBeChange.getcSSID())) {
                        if (!mToBeChange.getcSSID().equals(""))
                            GetDB().get(i).setcSSID(mToBeChange.getcSSID());
                    }
                }

                if (!GetDB().get(i).getNodeAddress().equals(mToBeChange.getNodeAddress())) {
                    GetDB().get(i).setNodeAddress(mToBeChange.getNodeAddress());
                }

                if(mToBeChange.getgCastVerision()==null && GetDB().get(i).getgCastVerision()!=null){
                    GetDB().get(i).setgCastVerision(null);
                }else if(GetDB().get(i).getgCastVerision()==null && mToBeChange.getgCastVerision()!=null){
                    GetDB().get(i).setgCastVerision(mToBeChange.getgCastVerision());
                }else if(mToBeChange.getgCastVerision()==null && GetDB().get(i).getgCastVerision() ==null ) {
                }else if(!GetDB().get(i).getgCastVerision().equals(mToBeChange.getgCastVerision())){
                    GetDB().get(i).setgCastVerision(mToBeChange.getgCastVerision());
                }
                if(mToBeChange.getmDeviceCap()!=null){
                    Log.d(TAG, "renewLSSDPNodeDataWithNewNode: "+mToBeChange.getmDeviceCap().getmSource().isAlexaAvsSource());
                    GetDB().get(i).setmDeviceCap(mToBeChange.getmDeviceCap());
                }

                /*Updating FwVersion in renewal*/
                if (mToBeChange.getVersion()!=null && !mToBeChange.getVersion().isEmpty()){
                    GetDB().get(i).setVersion(mToBeChange.getVersion());
                }

                /*Updating AlexaRefreshToken in renewal*/
                if (mToBeChange.getAlexaRefreshToken()!=null){
                    GetDB().get(i).setAlexaRefreshToken(mToBeChange.getAlexaRefreshToken());
                }

                LibreLogger.d(TAG, "renewLSSDPNodeDataWithNewNode "+GetDB().get(i).getFriendlyname() + ":" + GetDB().get(i).getDeviceState() + ":"
                        + GetDB().get(i).getZoneID() + " : " + GetDB().get(i).getcSSID());

            }
        }
    }

    public void clearNode(String mNodeAddress) {
        LibreLogger.d(TAG,"Clearing the Node "+ mNodeAddress);
        for (int i = 0; i < nodelist.size(); i++) {
            LSSDPNodes mNode = nodelist.get(i);
            if (mNode.getIP().equals(mNodeAddress)) {
                removeFromDB(mNode);
            }
        }

        TunnelingControl.removeTunnelingClient(mNodeAddress);
    }


    public LSSDPNodes getTheNodeBasedOnTheIpAddress(String ipaddress) {

        try {
            for (int i = 0; i < nodelist.size(); i++) {
                LSSDPNodes mNode = nodelist.get(i);
                if (mNode.getIP().equals(ipaddress)) {
                    return mNode;
                }
            }
        } catch (Exception e) {
            LibreLogger.d(TAG,"EXCEPTION Error while returning the node from ip address");
        }

        return null;

    }

    public boolean isAnyMasterIsAvailableInNetwork(){

        for (int i = 0; i < nodelist.size(); i++) {
            LSSDPNodes mNode = nodelist.get(i);

            if(  mNode!= null && mNode.getDeviceState()!=null && mNode.getDeviceState().equalsIgnoreCase("M"))
                return true;
        }
        return false;
    }

    public boolean isDeviceInNodeRepo(){

        for (int i = 0; i < nodelist.size(); i++) {
            LSSDPNodes mNode = nodelist.get(i);
            if (mNode != null
                    && mNode.getDeviceState() != null
                    && (mNode.getDeviceState().equalsIgnoreCase("M")
                    || mNode.getDeviceState().equalsIgnoreCase("F")
                    /*|| mNode.getDeviceState().equalsIgnoreCase("S")*/))
                return true;
        }
        return false;
    }

    public static boolean isLS9ExistsInTheNetwork(){

        for (int i = 0; i < nodelist.size(); i++) {
            LSSDPNodes mNode = nodelist.get(i);

          if(  mNode.getgCastVerision()!=null)
              return true;
        }
        return false;

    }

    public ArrayList<LSSDPNodes> getAllMasterNodes(){
        ArrayList<LSSDPNodes> masterNodesList = new ArrayList<>();
        ArrayList<LSSDPNodes> nodesArrayList = GetDB();
        for (LSSDPNodes lssdpNodes : nodesArrayList){
            if (lssdpNodes.getDeviceState()!=null && !lssdpNodes.getDeviceState().isEmpty() &&
                    lssdpNodes.getDeviceState().equalsIgnoreCase("M")){
                masterNodesList.add(lssdpNodes);
            }
        }

        return masterNodesList;
    }

    public LSSDPNodes isSacDeviceFoundInTheWork(String deviceName) {
        ArrayList<LSSDPNodes> masterNodesList = new ArrayList<>();
        ArrayList<LSSDPNodes> nodesArrayList = GetDB();
        for (LSSDPNodes lssdpNodes : nodesArrayList){
            if (lssdpNodes.getDeviceState()!=null && !lssdpNodes.getDeviceState().isEmpty() &&
                    lssdpNodes.getFriendlyname().equalsIgnoreCase(deviceName)){
               return lssdpNodes;
            }
        }
        return null;
    }
    public boolean hasFilteredModels(LSSDPNodes nodes) {
        return (nodes.getModelId() != null
                && (nodes.getModelId() == ModelId.Concert || nodes.getModelId() == ModelId.Stadium))
                ||
                (nodes.getCastModel() != null && !nodes.getCastModel().isEmpty()
                        && (nodes.getCastModel().toUpperCase().contains("RIVA CONCERT")
                        || nodes.getCastModel().toUpperCase().contains("RIVAACONCERT")
                        || nodes.getCastModel().toUpperCase().contains("RIVA STADIUM")
                        || nodes.getCastModel().toUpperCase().contains("RIVAASTADIUM")));
    }
}
