package com.libreAlexa.app.dlna.dmc.processor.interfaces;

import java.util.Collection;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;


public interface  UpnpProcessor {
    void addListener(UpnpProcessorListener listener);

    void removeListener(UpnpProcessorListener listener);

    void bindUpnpService();

    void unbindUpnpService();

    void searchAll();

    void searchDMS();

    void searchDMR();

    ControlPoint getControlPoint();

    RemoteDevice getRemoteDevice(String UDN);

    Collection<LocalDevice> getLocalDevices();

    Collection<RemoteDevice> getRemoteDevices();

    Collection<RemoteDevice> getRemoteDMS();

    Collection<RemoteDevice> getRemoteDMR();

    LocalDevice getLocalDeviceFromRegistry(String uDN);

    void stopMusicServer();

    void renew();

    interface UpnpProcessorListener {

        void onRemoteDeviceAdded(RemoteDevice device);

        void onRemoteDeviceRemoved(RemoteDevice device);

        void onLocalDeviceAdded(LocalDevice device);

        void onLocalDeviceRemoved(LocalDevice device);


        void onStartComplete();

    }

}
