package com.cumulations.libreV2.model;

import com.cumulations.libreV2.tcp_tunneling.enums.BatteryType;
import com.libreAlexa.constants.ControlConstants;
import com.libreAlexa.luci.LSSDPNodeDB;
import com.libreAlexa.util.LibreLogger;

/**
 * Created by karunakaran on 7/22/2015.
 */
public class SceneObject {
    private static final String TAG = SceneObject.class.getSimpleName();
    private String SceneName;
    private String SceneSourceName;
    private int numberOfDevices;
    private int previous;
    private int playstatus;
    private int next;
    private int volumemute;
    private int volumeValueInPercentage;
    private String ipAddress;
    private float currentPlaybackSeekPosition;
    private String album_art;
    private String trackName;
    private long totalTimeOfTheTrack;
    private AlexaControls alexaControls;
    private boolean isFavourite;
    /*added for repeat and shuffle*/
    private int shuffleState=0;
    private int repeatState=0;
    private int volumeZoneInPercentage;
    private String genre;
    private String album_name;
    private String artist_name;
    private int currentSource;
    private int BT_CONTROLLER;
    /* This is a important variable as it indicates if the DMR playback is happening from the
     current device or not as it will contain the ip address in that case
     */
    private String playUrl;
    private PREPARING_STATE preparingState;
    private boolean nextControl,previousControl,isSeekenabled;
    /*For alexa btn long press in device list*/
    private boolean isAlexaBtnLongPressed;

    private boolean isBatteryPluggedIn;
    private boolean isAuxPluggedIn;
    private boolean isBatteryCharging;
    private BatteryType batteryType;
    private int tunnelingCurrentSource;

    private String mute_status;

    public SceneObject() {}

    public SceneObject(String name, String sourceName, float currentPlaybackSeekPosition, String Ip) {
        this.SceneName = name;
        this.SceneSourceName = sourceName;
        this.currentPlaybackSeekPosition = currentPlaybackSeekPosition;
        this.previous = 0;
        this.playstatus = CURRENTLY_NOTPLAYING;
        this.next = 0;
        this.volumemute = 0;
        this.volumeValueInPercentage = 40;
        this.ipAddress = Ip;
    }

    public enum PREPARING_STATE {
        PREPARING_INITIATED, PREPARING_SUCCESS, PREPARING_FAILED;

    }

    public PREPARING_STATE getPreparingState() {
        return preparingState;
    }

    public void setPreparingState(PREPARING_STATE preparingState) {
        this.preparingState = preparingState;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }


    public int getvolumeZoneInPercentage() {

        return volumeZoneInPercentage;
    }

    public void setvolumeZoneInPercentage(int inputVolume) {
        volumeZoneInPercentage = inputVolume;
    }

    public String getSceneName() {

        if (LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(getIpAddress())!= null )
        return LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(getIpAddress()).getFriendlyname();

        return "";
    }
    public void setNextControl(boolean nextControl){
        this.nextControl=nextControl;
    }
    public boolean getNextControl(){
        return nextControl;
    }

    public void setPreviousControl(boolean previousControl){
        this.previousControl=previousControl;
    }
    public boolean getPreviousControl(){
        return previousControl;
    }
    public void setSeekEnabled(boolean isSeekenabled){
        this.isSeekenabled=isSeekenabled;
    }
    public boolean getSeekEnabled(){
        return isSeekenabled;
    }
    public void setSceneName(String sceneName) {
        SceneName = sceneName;
    }

    private String getSceneSourceName() {
        return SceneSourceName;
    }

    public int getShuffleState() {
        return shuffleState;
    }

    public void setShuffleState(int shuffleState) {
        this.shuffleState = shuffleState;
    }

    public int getRepeatState() {
        return repeatState;
    }

    public void setRepeatState(int repeatState) {
        this.repeatState = repeatState;
    }


    private void setSceneSourceName(String sceneSourceName) {
        SceneSourceName = sceneSourceName;
    }

    public int getNumberOfDevices() {
        return numberOfDevices;
    }

    public void setNumberOfDevices(int numberOfDevices) {
        this.numberOfDevices = numberOfDevices;
    }

    public int getPrevious() {
        return previous;
    }

    public void setPrevious(int previous) {
        this.previous = previous;
    }

    public int getPlaystatus() {
        return playstatus;
    }

    public void setPlaystatus(int playstatus) {
        this.playstatus = playstatus;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getVolumemute() {
        return volumemute;
    }

    public void setVolumemute(int volumemute) {
        this.volumemute = volumemute;
    }

    public int getVolumeValueInPercentage() {
        return volumeValueInPercentage;
    }

    public void setVolumeValueInPercentage(int volumeValueInPercentage) {
        this.volumeValueInPercentage = volumeValueInPercentage;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public float getCurrentPlaybackSeekPosition() {
        return currentPlaybackSeekPosition;
    }

    public void setCurrentPlaybackSeekPosition(float currentPlaybackSeekPosition) {
        this.currentPlaybackSeekPosition = currentPlaybackSeekPosition;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public String getmPreviousTrackName() {
        if(mPreviousTrackName==null )
            return "-1";
        return mPreviousTrackName;

    }

    public void setmPreviousTrackName(String mPreviousTrackName) {
        this.mPreviousTrackName = mPreviousTrackName;
    }

    private String mPreviousTrackName="-1";
    public static final int CURRENTLY_PLAYING = 0;
    public static final int CURRENTLY_STOPPED = 1;
    public static final int CURRENTLY_PAUSED = 2;
    public static final int CURRENTLY_NOTPLAYING = 4;
    public static final int CURRENTLY_BUFFERING = 5;


    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public long getTotalTimeOfTheTrack() {
        return totalTimeOfTheTrack;
    }

    public void setTotalTimeOfTheTrack(long totalTimeOfTheTrack) {
        this.totalTimeOfTheTrack = totalTimeOfTheTrack;
    }


    public int getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(int currentSource) {
        this.currentSource = currentSource;
    }

    public String getPlayUrl() {
        LibreLogger.d(TAG,"getPlayUrl for "+ipAddress +" is "+playUrl);
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {

        if(playUrl==null){
            LibreLogger.d(TAG,"ERROR Playurl is Null");
        }
        LibreLogger.d(TAG,"Setting the playurl for "+ipAddress +" is "+playUrl);
        this.playUrl = playUrl;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackname) {
        this.trackName = trackname;
    }

    public void setBT_CONTROLLER(int value){
        this.BT_CONTROLLER = value;
    }
    public int getBT_CONTROLLER(){
      return BT_CONTROLLER;
    }
    public void setAlexaControls(boolean[] flags){
        setAlexaControls(flags[0],flags[1],flags[2]);
    }
    public void setAlexaControls(boolean playPause,boolean next,boolean prev){
        if (alexaControls==null){
            //create new
            alexaControls = new AlexaControls(playPause,next,prev);
        }else{
            alexaControls.setPlayPauseEnabled(playPause);
            alexaControls.setNextEnabled(next);
            alexaControls.setPrevEnabled(prev);
        }
    }
    public String getAlexaSourceImageURL(){
        String url = getPlayUrl();
        String[] arr = url.split(ControlConstants.ALEXA_SOURCE_IMG_DELIMITER);
        if (arr==null){
            return null;
        }
        return arr[(arr.length)-1];
    }
    public  boolean[] getControlsValue(){
        AlexaControls controls = getAlexaControls();
        if (controls == null){
            return null;
        }
        boolean[] arr = new boolean[3];
        arr[0] = controls.getPlayPauseEnabled();
        arr[1] = controls.getNextEnabled();
        arr[2] = controls.getPrevEnabled();
        return arr;
    }
    public AlexaControls getAlexaControls(){
        return this.alexaControls;
    }
    private class AlexaControls{
        boolean playPauseEnabled = false
                ,nextEnabled =false
                ,prevEnabled = false;
        public AlexaControls(boolean playPause,boolean next,boolean prev){
            this.setPlayPauseEnabled(playPause);
            this.setNextEnabled(next);
            this.setPrevEnabled(prev);
        }
        public void setPlayPauseEnabled(boolean value){
            this.playPauseEnabled = value;
        }
        public void setNextEnabled(boolean value){
            this.nextEnabled = value;
        }
        public void setPrevEnabled(boolean value){
            this.prevEnabled = value;
        }

        public boolean getPlayPauseEnabled(){
            return this.playPauseEnabled;
        }
        public boolean getNextEnabled(){
            return this.nextEnabled;
        }
        public boolean getPrevEnabled(){
            return this.prevEnabled;
        }

    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public boolean isAlexaBtnLongPressed() {
        return isAlexaBtnLongPressed;
    }

    public void setAlexaBtnLongPressed(boolean alexaBtnLongPressed) {
        isAlexaBtnLongPressed = alexaBtnLongPressed;
    }

    public boolean isBatteryPluggedIn() {
        return isBatteryPluggedIn;
    }

    public void setBatteryPluggedIn(boolean batteryPluggedIn) {
        isBatteryPluggedIn = batteryPluggedIn;
    }

    public boolean isAuxPluggedIn() {
        return isAuxPluggedIn;
    }

    public void setAuxPluggedIn(boolean auxPluggedIn) {
        isAuxPluggedIn = auxPluggedIn;
    }

    public boolean isBatteryCharging() {
        return isBatteryCharging;
    }

    public void setBatteryCharging(boolean batteryCharging) {
        isBatteryCharging = batteryCharging;
    }

    public BatteryType getBatteryType() {
        return batteryType;
    }

    public void setBatteryType(BatteryType batteryType) {
        this.batteryType = batteryType;
    }

    public void clearBatteryStats(){
        isAuxPluggedIn = false;
        isBatteryPluggedIn = false;
        isBatteryCharging = false;
    }

    public int getTunnelingCurrentSource() {
        return tunnelingCurrentSource;
    }

    public void setTunnelingCurrentSource(int tunnelingCurrentSource) {
        this.tunnelingCurrentSource = tunnelingCurrentSource;
    }

    public String getMute_status() {
        return mute_status;
    }

    public void setMute_status(String mute_status) {
        this.mute_status = mute_status;
    }
}
