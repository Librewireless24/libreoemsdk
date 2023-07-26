package com.libreAlexa.app.dlna.dmc.processor.interfaces;

import java.util.List;
import java.util.Map;
import org.fourthline.cling.support.model.DIDLObject;


public interface DMSProcessor {
    void browse(String UpnpListenerActivityobjectID);

    void dispose();

    void addListener(DMSProcessorListener listener);

    void removeListener(DMSProcessorListener listener);

    interface DMSProcessorListener {
        void onBrowseComplete(String parentObjectId, Map<String, List<? extends DIDLObject>> result);

        void onBrowseFail(String message);
    }
}
