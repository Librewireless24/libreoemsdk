package com.libreAlexa.alexa;

import com.libreAlexa.constants.LSSDPCONST;
import com.libreAlexa.constants.LUCIMESSAGES;
import com.libreAlexa.constants.MIDCONST;
import com.libreAlexa.luci.LUCIControl;

/**
 * Created by bhargav on 25/10/17.
 */

public class AlexaUtils {

    public static void sendAlexaMetaDataRequest(String ipAddress) {
        new LUCIControl(ipAddress).SendCommand(MIDCONST.ALEXA_COMMAND, LUCIMESSAGES.DEVICE_METADATA_REQUEST, LSSDPCONST.LUCI_SET);
    }

    public static void sendAlexaRefreshTokenRequest(String ipAddress) {
        new LUCIControl(ipAddress).SendCommand(MIDCONST.MID_ENV_READ, LUCIMESSAGES.READ_ALEXA_REFRESH_TOKEN_MSG, LSSDPCONST.LUCI_GET);
    }

    public static void getDeviceUpdateStatus(String ipAddress) {
        new LUCIControl(ipAddress).SendCommand(MIDCONST.FW_UPGRADE_INTERNET_LS9, null, LSSDPCONST.LUCI_GET);
        new LUCIControl(ipAddress).SendCommand(MIDCONST.FW_UPGRADE_PROGRESS, null, LSSDPCONST.LUCI_GET);
    }
}
