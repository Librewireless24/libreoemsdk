package com.libreAlexa.app.dlna.dmc.processor.upnp;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import com.cumulations.libreV2.AppUtils;
import com.libreAlexa.app.dlna.dmc.processor.impl.UpnpProcessorImpl;
import com.libreAlexa.app.dlna.dmc.server.MusicServer;
import com.libreAlexa.util.LibreLogger;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 *
 * helper methods.
 */
public class LoadLocalContentService extends IntentService {
    String TAG = LoadLocalContentService.class.getSimpleName();
    public LoadLocalContentService() {
        super("LoadLocalContentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M
                && !AppUtils.INSTANCE.isPermissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            return;
        }

        UpnpProcessorImpl  upnpProcessor = new UpnpProcessorImpl(this);
        upnpProcessor.bindUpnpService();
        MusicServer.getMusicServer().prepareMediaServer(getApplicationContext(), upnpProcessor.getBinder());
        upnpProcessor.unbindUpnpService();
        LibreLogger.d(TAG,"LoadLocalContentService started");
    }
}
