package com.libreAlexa.app.dlna.dmc.processor.impl;

import com.libreAlexa.alexa.MicTcpServer;
import com.libreAlexa.util.LibreLogger;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.RemoteService;

/**
 * Created by karunakaran on 2/22/2016.
 * Can be used in future
 */
public class TransportSubscription extends SubscriptionCallback {
    private final static String TAG = TransportSubscription.class.getSimpleName();
    public TransportSubscription(RemoteService service) {
        super(service,600);
    }
    @Override
    protected void failed(GENASubscription genaSubscription, UpnpResponse upnpResponse, Exception e, String s) {
        LibreLogger.d(TAG,"Failed" + s);
    }

    @Override
    protected void established(GENASubscription genaSubscription) {
        LibreLogger.d(TAG, "established" + genaSubscription.toString());
    }

    @Override
    protected void ended(GENASubscription genaSubscription, CancelReason cancelReason, UpnpResponse upnpResponse) {

        LibreLogger.d(TAG,"ended" + genaSubscription.toString() );
    }

    @Override
    protected void eventReceived(GENASubscription genaSubscription) {
        LibreLogger.d(TAG,"eventReceived" + genaSubscription.toString());
    }

    @Override
    protected void eventsMissed(GENASubscription genaSubscription, int i) {

    }
}
