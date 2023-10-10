package com.libreAlexa.app.dlna.dmc.processor.impl;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.libreAlexa.LibreApplication;
import com.libreAlexa.app.dlna.dmc.gui.abstractactivity.UpnpListenerActivity;
import com.libreAlexa.app.dlna.dmc.processor.interfaces.UpnpProcessor;
import com.libreAlexa.app.dlna.dmc.processor.upnp.CoreUpnpService;
import com.libreAlexa.app.dlna.dmc.server.MusicServer;
import com.libreAlexa.app.dlna.dmc.utility.PlaybackHelper;
import com.libreAlexa.app.dlna.dmc.utility.UpnpDeviceManager;
import com.libreAlexa.util.LibreLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.message.header.DeviceTypeHeader;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

public class UpnpProcessorImpl implements UpnpProcessor, RegistryListener {
    public final static String DMS_NAMESPACE = "schemas-upnp-org";
    public final static String DMS_TYPE = "MediaServer";
    public final static String DMR_NAMESPACE = "schemas-upnp-org";
    public final static String DMR_TYPE = "MediaRenderer";
    private Activity activity;
    private Context context;
    private static CoreUpnpService.Binder upnpServiceBinder;
    private ServiceConnection serviceConnection;
    private final List<UpnpProcessorListener> upnpProcessorListeners;
    private MusicServer musicServer;
    private final static String TAG = UpnpProcessorImpl.class.getSimpleName();

    public UpnpProcessorImpl(UpnpListenerActivity activity) {
        this.activity = activity;
        context = activity;
        upnpProcessorListeners = new ArrayList<>();
    }

    public UpnpProcessorImpl(Context context) {
        this.context = context;
        upnpProcessorListeners = new ArrayList<>();
    }

    public void bindUpnpService() {
        LibreLogger.d(TAG, "bindUpnpService");
        serviceConnection = new ServiceConnection() {

            public void onServiceDisconnected(ComponentName name) {
                LibreLogger.d(TAG, "bindUpnpService onServiceDisconnected name " + name.flattenToShortString());
                upnpServiceBinder = null;
            }

            public void onServiceConnected(ComponentName name, IBinder service) {
                LibreLogger.d(TAG, "bindUpnpService onServiceConnected name " + name.flattenToShortString());
                upnpServiceBinder = (CoreUpnpService.Binder) service;
                upnpServiceBinder.getRegistry().addListener(UpnpProcessorImpl.this);
                fireOnStartCompleteEvent();
            }
        };

        Intent intent = new Intent(context, CoreUpnpService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopMusicServer() {
        if (musicServer != null) {
            musicServer.stopMediaServer();
        }
    }

    public void unbindUpnpService() {
        LibreLogger.d(TAG, "unbindUpnpService");
        if (upnpServiceBinder != null) {
            try {
                if (serviceConnection != null && context != null) /*We Got the Crash in the CrashAnalaytics*/
                    context.unbindService(serviceConnection);
            } catch (Exception ex) {
                ex.printStackTrace();
                LibreLogger.d(TAG, "unbindUpnpService Exception " + ex.getMessage());
            } finally {
                context = null;
            }
        }
    }

    public void searchAll() {
        LibreLogger.d(TAG, "searchAll");
        upnpServiceBinder.getRegistry().removeAllRemoteDevices();
        upnpServiceBinder.getControlPoint().search();
    }


    public void addListener(UpnpProcessorListener listener) {
        synchronized (upnpProcessorListeners) {
            if (!upnpProcessorListeners.contains(listener)) {
                upnpProcessorListeners.add(listener);
            }
        }
    }

    public void removeListener(UpnpProcessorListener listener) {
        synchronized (upnpProcessorListeners) {
            if (upnpProcessorListeners.contains(listener)) {
                LibreLogger.d(TAG, "removeListener " + listener.getClass().getSimpleName());
                upnpProcessorListeners.remove(listener);
                /*to prevent memory leaks*/
                activity = null;
            }
        }
    }

    public ControlPoint getControlPoint() {
        return upnpServiceBinder.getControlPoint();
    }

    @Override
    public RemoteDevice getRemoteDevice(String UDN) {
        for (RemoteDevice device : upnpServiceBinder.getRegistry().getRemoteDevices()) {
            if (device.getIdentity().getUdn().toString().equals(UDN))
                return device;
        }

        return null;
    }

    public Collection<LocalDevice> getLocalDevices() {
        if (upnpServiceBinder != null)
            return upnpServiceBinder.getRegistry().getLocalDevices();

        return null;
    }

    public Collection<RemoteDevice> getRemoteDevices() {
        if (upnpServiceBinder != null)
            return upnpServiceBinder.getRegistry().getRemoteDevices();
        return null;
    }

    @SuppressWarnings("rawtypes")
    public Collection<RemoteDevice> getRemoteDMS() {
        DeviceType dmstype = new DeviceType(DMS_NAMESPACE, DMS_TYPE, 1);
        if (upnpServiceBinder == null) return null;

        Collection<Device> devices = upnpServiceBinder.getRegistry().getDevices(dmstype);
        if (devices == null) return null;

        ArrayList<RemoteDevice> remoteDev = new ArrayList<RemoteDevice>();
        for (Device dev : devices) {
            if (dev instanceof RemoteDevice)
                remoteDev.add((RemoteDevice) dev);
        }

        return remoteDev;
    }

    @SuppressWarnings("rawtypes")
    public Collection<RemoteDevice> getRemoteDMR() {
        DeviceType dmrtype = new DeviceType(DMR_NAMESPACE, DMR_TYPE, 1);
        if (upnpServiceBinder == null) return null;

        Collection<Device> devices = upnpServiceBinder.getRegistry().getDevices(dmrtype);
        if (devices == null) return null;

        ArrayList<RemoteDevice> remoteDev = new ArrayList<RemoteDevice>();
        for (Device dev : devices) {
            if (dev instanceof RemoteDevice)
                remoteDev.add((RemoteDevice) dev);
        }

        return remoteDev;
    }

    public LocalDevice getLocalDeviceFromRegistry(String UDN) {

        for (LocalDevice device : upnpServiceBinder.getRegistry().getLocalDevices()) {
            LibreLogger.d(TAG, "getLocalDeviceFromRegistry :" + device.getDetails().getFriendlyName() + "," + device.getIdentity().getUdn().toString());
            if (device.getIdentity().getUdn().toString().equals(UDN))
                return device;
        }
        return null;
    }


    private void fireOnStartCompleteEvent() {
        synchronized (upnpProcessorListeners) {
            for (UpnpProcessorListener listener : upnpProcessorListeners) {
                listener.onStartComplete();
            }
        }
    }

    /* Added by Praveen to get the callback on service disconnection

     */
    private void fireOnServiceDisconnected() {
        synchronized (upnpProcessorListeners) {
            for (UpnpProcessorListener listener : upnpProcessorListeners) {
                //    listener.onServiceDisconnected();
            }
        }
    }

    @Override
    public void searchDMS() {
        LibreLogger.d(TAG, "SearchDMS invoke");
        DeviceType type = new DeviceType(DMS_NAMESPACE, DMS_TYPE, 1);
        if (upnpServiceBinder != null) {
            upnpServiceBinder.getControlPoint().search(new DeviceTypeHeader(type));
        } else {
            LibreLogger.d(TAG, "searchDMS UPnP Service is null");
        }
    }

    @Override
    public void searchDMR() {

        LibreLogger.d(TAG, "SearchDMR invoke");
        DeviceType type = new DeviceType(DMR_NAMESPACE, DMR_TYPE, 1);
        if (upnpServiceBinder != null) {
            //upnpServiceBinder.getRegistry().removeAllRemoteDevices();
            upnpServiceBinder.getControlPoint().search(new DeviceTypeHeader(type));
        } else {
            LibreLogger.d(TAG, "searchDMR UPnP Service is null");
        }

    }

    public void ClearAll() {
        LibreLogger.d(TAG, "ClearAll");
        if (upnpServiceBinder != null) {
            upnpServiceBinder.getRegistry().removeAllRemoteDevices();
        } else {
            LibreLogger.d(TAG, "UPnP Service is null");
        }

    }

    /* this function will check the renderer in the registry for the presence of the ip address*/

    public RemoteDevice getTheRendererFromRegistryIp(String ipAdd) {
        for (RemoteDevice device : upnpServiceBinder.getRegistry().getRemoteDevices()) {
            String ip = device.getIdentity().getDescriptorURL().getHost();
            if (ip.equals(ipAdd))
                return device;
        }
        return null;
    }

    @Override
    public void remoteDeviceDiscoveryStarted(Registry registry,
                                             RemoteDevice device) {

    }

    @Override
    public void remoteDeviceDiscoveryFailed(Registry registry,
                                            RemoteDevice device, Exception ex) {



    }

    @Override
    public synchronized void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        LibreLogger.d(TAG, "remoteDeviceAdded " + device.getDisplayString() + " registry " + registry.toString());
        fireRemoteDeviceAddedEvent(device);
    }

    @Override
    public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {

    }

    @Override
    public synchronized void remoteDeviceRemoved(Registry registry, RemoteDevice device) {

        LibreLogger.d(TAG, "remoteDeviceRemoved " + device.getDisplayString());
        fireRemoteDeviceRemovedEvent(device);
    }

    @Override
    public void localDeviceAdded(Registry registry, LocalDevice device) {
        fireLocalDeviceAddedEvent(device);
    }

    @Override
    public void localDeviceRemoved(Registry registry, LocalDevice device) {
        fireLocalDeviceRemovedEvent(device);
    }

    @Override
    public void beforeShutdown(Registry registry) {


    }

    @Override
    public void afterShutdown() {


    }

    private void fireRemoteDeviceAddedEvent(RemoteDevice remoteDevice) {
        synchronized (upnpProcessorListeners) {
            for (UpnpProcessorListener listener : upnpProcessorListeners) {

                listener.onRemoteDeviceAdded(remoteDevice);
            }
        }
    }

    private void fireRemoteDeviceRemovedEvent(RemoteDevice remoteDevice) {
        synchronized (upnpProcessorListeners) {
            for (UpnpProcessorListener listener : upnpProcessorListeners) {
                listener.onRemoteDeviceRemoved(remoteDevice);
            }

        }
    }

    private void fireLocalDeviceAddedEvent(LocalDevice localDevice) {
        synchronized (upnpProcessorListeners) {
            for (UpnpProcessorListener listener : upnpProcessorListeners) {
                listener.onLocalDeviceAdded(localDevice);
            }
        }
    }

    private void fireLocalDeviceRemovedEvent(LocalDevice localDevice) {
        synchronized (upnpProcessorListeners) {
            for (UpnpProcessorListener listener : upnpProcessorListeners) {
                listener.onLocalDeviceRemoved(localDevice);
            }
        }
    }


    public void renew() {
        upnpServiceBinder.renewUpnpBinder();
        musicServer = MusicServer.getMusicServer();
        musicServer.clearMediaServer();
        UpnpDeviceManager.getInstance().clearMaps();
        LibreApplication.PLAYBACK_HELPER_MAP = new HashMap<String, PlaybackHelper>();

        upnpServiceBinder.getRegistry().addDevice(musicServer.getMediaServer().getDevice());
        LibreApplication.LOCAL_UDN = musicServer.getMediaServer().getDevice().getIdentity().getUdn().toString();

        if (activity != null){
            unbindUpnpService();
        }
        bindUpnpService();

    }

    public CoreUpnpService.Binder getBinder() {
        return upnpServiceBinder;
    }
}