package com.libreAlexa.app.dlna.dmc.utility;

import android.util.Log;
import com.libreAlexa.LibreApplication;
import com.libreAlexa.app.dlna.dmc.processor.impl.UpnpProcessorImpl;
import com.libreAlexa.app.dlna.dmc.processor.interfaces.UpnpProcessor;
import com.libreAlexa.util.LibreLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;

public class UpnpDeviceManager implements UpnpProcessor.UpnpProcessorListener {
    private final static String TAG = UpnpDeviceManager.class.getSimpleName();
    private HashSet<RemoteDevice> remoteDms = new HashSet<RemoteDevice>();
    private HashSet<RemoteDevice> remoteDmr = new HashSet<RemoteDevice>();
    private HashMap<String, RemoteDevice> remoteDmsMap = new HashMap<String, RemoteDevice>();
    private HashMap<String, RemoteDevice> remoteDmrMap = new HashMap<String, RemoteDevice>();
    private HashMap<String, LocalDevice> localDmsMap = new HashMap<String, LocalDevice>();
    private HashMap<String, RemoteDevice> remoteDmrIPMap = new HashMap<String, RemoteDevice>();

    private ArrayList<String> remoteRemoved = new ArrayList<String>();
    private static UpnpDeviceManager manager = new UpnpDeviceManager();


    public static UpnpDeviceManager getInstance() {
        return manager;
    }

    @Override
    public void onRemoteDeviceAdded(RemoteDevice device) {
//        LibreLogger.d(TAG,"onRemoteDeviceAdded, "+device.getIdentity().getDescriptorURL());
        LibreLogger.d(TAG,"onRemoteDeviceAdded, namespace:"+device.getType().getNamespace()+
                "type:"+device.getType().getType());

        String udn = device.getIdentity().getUdn().toString();
        String ip = device.getIdentity().getDescriptorURL().getHost();
        LibreLogger.d(TAG,"onRemoteDeviceAdded, ip "+ip+", udn:"+udn);

        if (device.getType().getNamespace().equals(UpnpProcessorImpl.DMS_NAMESPACE) &&
                device.getType().getType().equals(UpnpProcessorImpl.DMS_TYPE)) {
            if (remoteDmsMap.containsKey(udn)) {
                remoteDms.remove(device);
                remoteDmsMap.remove(udn);
            }
            remoteDms.add(device);
            remoteDmsMap.put(udn, device);
        } else if (device.getType().getNamespace().equals(UpnpProcessorImpl.DMR_NAMESPACE) &&
                device.getType().getType().equals(UpnpProcessorImpl.DMR_TYPE)) {
            if (remoteDmrMap.containsKey(udn)) {
                remoteDmr.remove(device);
                remoteDmrMap.remove(udn);
                remoteDmrIPMap.remove(ip);
            }
            remoteDmr.add(device);
            remoteDmrMap.put(udn, device);
            remoteDmrIPMap.put(ip, device);
        }
    }

    @Override
    public void onRemoteDeviceRemoved(RemoteDevice device) {
        if (device == null || device.getDetails() == null || device.getDetails().getFriendlyName() == null)
            return;
        LibreLogger.d(TAG,"onRemoteDeviceRemoved, "+device.getIdentity().getDescriptorURL());
        LibreLogger.d(TAG,"onRemoteDeviceRemoved, namespace:"+device.getType().getNamespace()+
                "type:"+device.getType().getType());

        LibreLogger.d(TAG, "onRemoteDeviceRemoved : " + device.getDetails().getFriendlyName());
        String udn = device.getIdentity().getUdn().toString();

        if (device.getType().getNamespace().equals(UpnpProcessorImpl.DMS_NAMESPACE) &&
                device.getType().getType().equals(UpnpProcessorImpl.DMS_TYPE)) {
            if (remoteDmsMap.containsKey(udn)) {
                remoteDms.remove(device);
                remoteDmsMap.remove(udn);
            }
        } else if (device.getType().getNamespace().equals(UpnpProcessorImpl.DMR_NAMESPACE) &&
                device.getType().getType().equals(UpnpProcessorImpl.DMR_TYPE)) {
            if (remoteDmrMap.containsKey(udn)) {
                remoteDmr.remove(device);
                remoteDmrMap.remove(udn);
                if (device.getIdentity().getDescriptorURL().getHost()!=null) {
                    remoteDmrIPMap.remove(device.getIdentity().getDescriptorURL().getHost());
                }
            }
        }

        try {
            if (device.getDetails().getBaseURL().getHost() != null) {
                remoteRemoved.remove(device.getDetails().getBaseURL().getHost());
            }
        } catch (Exception e) {
            e.printStackTrace();
            LibreLogger.d(TAG,"onRemoteDeviceRemoved, Exception "+e.getMessage());
        }
    }

    @Override
    public void onLocalDeviceAdded(LocalDevice device) {

        LibreLogger.d(TAG, "local dev added:" + device.getDetails().getFriendlyName());
        String udn = device.getIdentity().getUdn().toString();
        if (device.getType().getNamespace().equals(UpnpProcessorImpl.DMS_NAMESPACE) &&
                device.getType().getType().equals(UpnpProcessorImpl.DMS_TYPE)) {
            localDmsMap.remove(udn);
            localDmsMap.put(udn, device);
        }
    }

    @Override
    public void onLocalDeviceRemoved(LocalDevice device) {

        LibreLogger.d(TAG, "local dev removed:" + device.getDetails().getFriendlyName());
        String udn = device.getIdentity().getUdn().toString();
        if (localDmsMap.containsKey(udn)) {
            localDmsMap.remove(udn);
            LibreApplication.LOCAL_UDN = "";
        }
    }

    @Override
    public void onStartComplete() {

    }


    /* These to function doent have much of importance as they just indicate the connection to service - END*/

    public HashSet<RemoteDevice> getRemoteDms() {
        return remoteDms;
    }

    public HashSet<RemoteDevice> getRemoteDmr() {
        return remoteDmr;
    }

    HashMap<String, RemoteDevice> getRemoteDmsMap() {
        return remoteDmsMap;
    }

    HashMap<String, RemoteDevice> getRemoteDmrMap() {
        return remoteDmrMap;
    }

    HashMap<String, LocalDevice> getLocalDmsMap() {
        return localDmsMap;
    }

    public void clearMaps() {
        remoteDmr = new HashSet<>();
        remoteDmrMap = new HashMap<>();
        remoteDms = new HashSet<>();
        remoteDmsMap = new HashMap<>();
        /*	localDmsMap = new HashMap<String, LocalDevice>();*/
    }

    /* Added by Praveen to get the device from the remote map*/
    public RemoteDevice getRemoteDMSDeviceByUDN(String udn) {
        return remoteDmsMap.get(udn);
    }

    public RemoteDevice getRemoteDMRDeviceByUDN(String udn) {
        return remoteDmrMap.get(udn);
    }

    public LocalDevice getLocalDMSDevicByUDN(String udn) {
        return localDmsMap.get(udn);
    }

    public RemoteDevice getRemoteDMRDeviceByIp(String ip) {
        return remoteDmrIPMap.get(ip);
    }

    public boolean getRemoteRemoved(String ipAddress) {
        return remoteRemoved.contains(ipAddress);
    }
}
