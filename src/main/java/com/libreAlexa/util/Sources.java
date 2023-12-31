package com.libreAlexa.util;

import java.util.HashMap;

/**
 * Created by karunakaran on 9/2/2016.
 */
public class Sources {
    boolean Airplay;
    boolean Dmr;
    boolean Dmp;
    boolean Spotify;
    boolean Usb;
    boolean SDcard;
    boolean Melon;
    boolean vTuner;
    boolean TuneIn;
    boolean Miracast;
    boolean Playlist;
    boolean DDMS_Slave;
    boolean AuxIn;
    boolean AppleDevice;
    boolean Direct_URL;
    boolean QPlay;
    boolean Bluetooth;
    boolean Deezer;
    boolean Tidal;
    boolean Favourites;
    boolean GoogleCast;
    boolean ExternalSource;
    boolean RTSP;
    boolean Roon;
    boolean AlexaAvsSource;
    boolean Airable;
    HashMap<String, Boolean> capitalCities = new HashMap<String, Boolean>();

    public HashMap<String, Boolean> getCapitalCities() {
        return capitalCities;
    }

    public void setCapitalCities(HashMap<String, Boolean> capitalCities) {
        this.capitalCities = capitalCities;
    }

    public String toPrintString() {

        return "Airplay :" + Airplay + "\t " +
                "Dmr :" + Dmr + "\t " +
                "Dmp :" + Dmp + "\t " +
                "Spotify :" + Spotify + "\t " +
                "USB :" + Usb + "\t " +
                "SDCARD: " + SDcard + "\t " +
                "Melon :" + Melon + "\t " +
                "VTuner :" + vTuner + "\t " +
                "TuneIn :" + TuneIn + "\t " +
                "Miracast :" + Miracast + "\t " +
                "Playlist :" + Playlist + "\t " +
                "DDMS_SLAVE :" + DDMS_Slave + "\t " +
                "AuxIn :" + AuxIn + "\t " +
                "AppleDevice :" + AppleDevice + "\t " +
                "Direct_URL :" + Direct_URL + "\t " +
                "QPlay :" + QPlay + "\t " +
                "BT :" + Bluetooth + "\t " +
                "Deezer :" + Deezer + "\t " +
                "Tial :" + Tidal + "\t " +
                "Fav :" + Favourites + "\t " +
                "Gcast :" + GoogleCast + "\t " +
                "ExternalSource :" + ExternalSource + "\t " +
                "RTSP :" + RTSP + "\t " +
                "Roon :" + Roon + "\t " +
                "AlexaAvsSource :" + AlexaAvsSource + "\t "+
                "Airable :" + Airable + "\t ";

    }

    public boolean isAirplay() {
        return Airplay;
    }

    public void setAirplay(boolean airplay) {
        Airplay = airplay;
    }

    public boolean isDmr() {
        return Dmr;
    }

    public void setDmr(boolean dmr) {
        Dmr = dmr;
    }

    public boolean isDmp() {
        return Dmp;
    }

    public void setDmp(boolean dmp) {
        Dmp = dmp;
    }

    public boolean isSpotify() {
        return Spotify;
    }

    public void setSpotify(boolean spotify) {
        Spotify = spotify;
    }

    public boolean isUsb() {
        return Usb;
    }

    public void setUsb(boolean usb) {
        Usb = usb;
    }

    public boolean isSDcard() {
        return SDcard;
    }

    public void setSDcard(boolean SDcard) {
        this.SDcard = SDcard;
    }

    public boolean isMelon() {
        return Melon;
    }

    public void setMelon(boolean melon) {
        Melon = melon;
    }

    public boolean isvTuner() {
        return vTuner;
    }

    public void setvTuner(boolean vTuner) {
        this.vTuner = vTuner;
    }

    public boolean isTuneIn() {
        return TuneIn;
    }

    public void setTuneIn(boolean tuneIn) {
        TuneIn = tuneIn;
    }

    public boolean isMiracast() {
        return Miracast;
    }

    public void setMiracast(boolean miracast) {
        Miracast = miracast;
    }

    public boolean isDDMS_Slave() {
        return DDMS_Slave;
    }

    public void setDDMS_Slave(boolean DDMS_Slave) {
        this.DDMS_Slave = DDMS_Slave;
    }

    public boolean isAuxIn() {
        return AuxIn;
    }

    public void setAuxIn(boolean auxIn) {
        AuxIn = auxIn;
    }

    public boolean isAppleDevice() {
        return AppleDevice;
    }

    public void setAppleDevice(boolean appleDevice) {
        AppleDevice = appleDevice;
    }

    public boolean isDirect_URL() {
        return Direct_URL;
    }

    public void setDirect_URL(boolean direct_URL) {
        Direct_URL = direct_URL;
    }

    public boolean isBluetooth() {
        return Bluetooth;
    }

    public void setBluetooth(boolean bluetooth) {
        Bluetooth = bluetooth;
    }

    public boolean isDeezer() {
        return Deezer;
    }

    public void setDeezer(boolean deezer) {
        Deezer = deezer;
    }

    public boolean isTidal() {
        return Tidal;
    }

    public void setTidal(boolean tidal) {
        Tidal = tidal;
    }

    public boolean isFavourites() {
        return Favourites;
    }

    public void setFavourites(boolean favourites) {
        Favourites = favourites;
    }

    public boolean isGoogleCast() {
        return GoogleCast;
    }

    public void setGoogleCast(boolean googleCast) {
        GoogleCast = googleCast;
    }

    public boolean isExternalSource() {
        return ExternalSource;
    }

    public void setExternalSource(boolean externalSource) {
        ExternalSource = externalSource;
    }

    public boolean isAlexaAvsSource() {
        return AlexaAvsSource;
    }

    public void setAlexaAvsSource(boolean alexaAvsSource) {
        AlexaAvsSource = alexaAvsSource;
    }

    public boolean isPlaylist() {
        return Playlist;
    }

    public void setPlaylist(boolean playlist) {
        Playlist = playlist;
    }

    public boolean isQPlay() {
        return QPlay;
    }

    public void setQPlay(boolean QPlay) {
        this.QPlay = QPlay;
    }

    public boolean isRTSP() {
        return RTSP;
    }

    public void setRTSP(boolean RTSP) {
        this.RTSP = RTSP;
    }

    public boolean isRoon() {
        return Roon;
    }

    public void setRoon(boolean roon) {
        Roon = roon;
    }

    public boolean isAirable() {
        return Airable;
    }

    public void setAirable(boolean airable) {
        Airable = airable;
    }

    public Sources() {

    }

    public Sources(boolean airplay, boolean dmr, boolean dmp, boolean spotify,
                   boolean usb, boolean SDcard, boolean melon, boolean vTuner,
                   boolean tuneIn, boolean miracast, boolean DDMS_Slave, boolean auxIn,
                   boolean appleDevice, boolean direct_URL, boolean bluetooth, boolean deezer,
                   boolean tidal, boolean favourites, boolean googleCast, boolean externalSource, boolean alexaAvsSource) {
        Airplay = airplay;
        Dmr = dmr;
        Dmp = dmp;
        Spotify = spotify;
        Usb = usb;
        this.SDcard = SDcard;
        Melon = melon;
        this.vTuner = vTuner;
        TuneIn = tuneIn;
        Miracast = miracast;
        this.DDMS_Slave = DDMS_Slave;
        AuxIn = auxIn;
        AppleDevice = appleDevice;
        Direct_URL = direct_URL;
        Bluetooth = bluetooth;
        Deezer = deezer;
        Tidal = tidal;
        Favourites = favourites;
        GoogleCast = googleCast;
        ExternalSource = externalSource;
        AlexaAvsSource = alexaAvsSource;
    }

}
