package com.libreAlexa.app.dlna.dmc.utility;

import static com.cumulations.libreV2.activity.CTNowPlayingActivity.REPEAT_ALL;
import static com.cumulations.libreV2.activity.CTNowPlayingActivity.REPEAT_ONE;

import android.util.Log;
import com.libreAlexa.app.dlna.dmc.processor.interfaces.DMRProcessor;
import com.libreAlexa.util.LibreLogger;
import java.util.List;
import java.util.Random;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.item.Item;


public class PlaybackHelper implements DMRProcessor.DMRProcessorListener {
    private final static String TAG = PlaybackHelper.class.getSimpleName();
    private DMRControlHelper dmrControlHelper;
    private DMSBrowseHelper dmsBrowseHelper;
    private int durationSeconds = 0; //seconds
    private boolean isPlaying;
    private int volume;

    /*this is made for shuffle feature while local content*/
    public void setIsShuffleOn(boolean isShuffleOn) {
        this.isShuffleOn = isShuffleOn;
    }

    public boolean isShuffleOn() {
        return isShuffleOn;
    }

    private boolean isShuffleOn;

    public int getRepeatState() {
        return repeatState;
    }

    public void setRepeatState(int repeatState) {
        this.repeatState = repeatState;
    }

    private int repeatState;

    public DMRControlHelper getDmrControlHelper() {
        return dmrControlHelper;
    }


    public DMSBrowseHelper getDmsBrowseHelper() {
        return dmsBrowseHelper;
    }

    public void setDmsBrowseHelper(DMSBrowseHelper dmsBrowseHelper) {
        this.dmsBrowseHelper = dmsBrowseHelper;
    }

    public void addDmrProcessorListener() {
        if (mStopPlayBackCalled) {
            mStopPlayBackCalled = false;
            dmrControlHelper.getDmrProcessor().addListener(this);
        }
    }

    public PlaybackHelper(DMRControlHelper dmr) {
        dmrControlHelper = dmr;
        dmrControlHelper.getDmrProcessor().addListener(this);
    }

    private boolean mStopPlayBackCalled = false;

    public boolean getPlaybackStopped() {
        return mStopPlayBackCalled;
    }

    @Override
    public String toString() {
        return dmrControlHelper.getDeviceUdn();
    }

    public boolean stopPlayback() {
        dmrControlHelper.getDmrProcessor().stop();
        dmrControlHelper.getDmrProcessor().removeListener(this);
        mStopPlayBackCalled = true;
        return true;
    }


    public void playSong() {
        LibreLogger.d(TAG, "PlaySong");
        if (dmsBrowseHelper == null){
            LibreLogger.d(TAG, "PlaySong dmsBrowseHelper null");
            return;
        }
        DIDLObject didlObject = dmsBrowseHelper.getDIDLObject();
        if (!(didlObject instanceof Item)) return;

//        DIDLObject didlObject = dmsBrowseHelper.getDIDLObject();
//        if (didlObject == null || !(didlObject instanceof Item)) {
//            LibreLogger.d(TAG, "PlaySong didlObject null or not Item");
//            return;
//        }

        String url = null;
        if (didlObject.getResources() != null && didlObject.getResources().get(0) != null) {
            url = didlObject.getResources().get(0).getValue();
        }

        if (url == null) {
            LibreLogger.d(TAG, "PlaySong url null");
            return;
        }
        String title = didlObject.getTitle();
        String creator = didlObject.getCreator();
//		String cls = didlObj.getClass();
        String album = didlObject.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM.class);
        java.net.URI uri = didlObject.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM_ART_URI.class);
        String artist = didlObject.getCreator();
        String contentFormat = didlObject.getFirstResource().getProtocolInfo().getContentFormat();
        String size;
        /*this is added to avoid NPE reported by Crashlytics //// AFAIK waste code*/
        if (didlObject.getFirstResource().getSize() != null) {
            size = didlObject.getFirstResource().getSize().toString();
        } else {
            size = "1024";
        }
        String duration = didlObject.getFirstResource().getDuration();

        String urlMeta = null;
        if (duration != null) {
            duration = !duration.contains("\\.") ? duration : duration.substring(0, duration.indexOf("\\."));
            urlMeta = "<DIDL-Lite xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\">"
                    + "<item id=\"audio-item-293\" parentID=\"2\" restricted=\"0\">"
                    + "<dc:title>"
                    + "<![CDATA[" + title + "]]>"
                    + "</dc:title>"
                    + "<dc:creator>"
                    + "<![CDATA[" + creator + "]]>"
                    + "</dc:creator>"
                    + "<upnp:class>object.item.audioItem.musicTrack</upnp:class>"
                    + "<upnp:album>"
                    + "<![CDATA[" + album + "]]>"
                    + "</upnp:album>"
                    + "<upnp:albumArtURI>"
                    + uri
                    + "</upnp:albumArtURI>"
                    + "<upnp:artist role=\"Performer\">"
                    + "<![CDATA[" + artist + "]]>"
                    + "</upnp:artist>"
                    + "<res protocolInfo=\"http-get:*:"
                    + contentFormat
                    + ":DLNA.ORG_PN=MP3;DLNA.ORG_OP=01;DLNA.ORG_FLAGS=01500000000000000000000000000000\" size=\""
                    + size
                    + "\" duration=\""
                    + duration
                    + "\">"
                    + url
                    + "</res>" + "</item>" + "</DIDL-Lite>";
        }

        LibreLogger.d(TAG, "PlaySong url "+url+" urlMeta "+urlMeta);

        if (urlMeta == null) {
            dmrControlHelper.getDmrProcessor().setURI(url, null);
            LibreLogger.d(TAG, "PlaySong urlMeta null return");
            return;
        }

        durationSeconds = (int) ModelUtil.fromTimeString(duration);
        dmrControlHelper.getDmrProcessor().setURI(url, urlMeta);
    }

    /*this function will give random pos for shuffle btw given range*/
    private int randomPosition(int maximum) {
        Random rn = new Random();
        int range = maximum + 1;
        return rn.nextInt(range);
    }


//    public DMRControlHelper getDmrHelper() {
//        return dmrHelper;
//    }
//
//    public PlaybackHelper(DMRControlHelper dmr) {
//        dmrHelper = dmr;
//        dmrHelper.getDmrProcessor().addListener(this);
//    }
    public void playNextSong(int nextPos) {
        LibreLogger.d(TAG, "PlayNextSong");
        if (dmsBrowseHelper == null) {
            LibreLogger.d(TAG, "PlayNextSong dmsBrowseHelper null");
            return;
        }
        List<DIDLObject> didlObjectList = dmsBrowseHelper.getDidlList();
        if (didlObjectList == null ||didlObjectList.isEmpty()) {
            LibreLogger.d(TAG, "PlayNextSong didlObjectList null or empty");
            return;
        }

        int totalCount = didlObjectList.size();
        int currPosition = dmsBrowseHelper.getAdapterPosition();
        /*changed to support shuffle for local content as we we have to give random position*/
        int nextPosition;

        LibreLogger.d(TAG, "PlayNextSong isShuffleOn "+isShuffleOn+" repeatState "+repeatState+
                "totalCount "+totalCount+" currentPos "+currPosition);
        if (isShuffleOn) {
            if (repeatState == REPEAT_ONE) {
                /*assigning same position every time*/
                nextPosition = currPosition;
            } else {
                /*otherwise get random position*/
                nextPosition = randomPosition(totalCount);
            }
            LibreLogger.d(TAG, "playNextSong: shuffle on next pos = " + nextPosition);
        } else {
            /*which means shuffle is off*/
            if (repeatState == REPEAT_ONE) {
                /*assigning same position every time*/
                nextPosition = currPosition;
            } else if (repeatState == REPEAT_ALL) {
                /*if both counts are equal sending it to previous position*/
                if (totalCount == currPosition + 1) {
                    nextPosition = 0;
                } else {
                    /*else increase it by x*/
                    nextPosition = currPosition + nextPos;
                }
            } else {
                /*else increase it by x*/
                nextPosition = currPosition + nextPos;
            }
            LibreLogger.d(TAG, "playNextSong: shuffle off nextPos = " + nextPosition);
        }

        DIDLObject didlObject = null;
        for (int i = 0; i < totalCount; i++) {
            if (nextPosition < 0) {
                return;
            } else if (nextPosition >= totalCount) {
                return;
            }

            didlObject = dmsBrowseHelper.getDIDLObject(nextPosition);
            if (didlObject instanceof Item) {
                break;
            } else {
                didlObject = null;
            }
            nextPosition += nextPos;
        }

        if (didlObject == null) {
            LibreLogger.d(TAG, "didlObject Null");
            return;
        }

        dmsBrowseHelper.setAdapterPosition(nextPosition);
        try {
            playSong();
        } catch (Exception e) {
            e.printStackTrace();
            LibreLogger.d(TAG, "playNextSong exception = "+e.getMessage());
        }
    }

    @Override
    public void onUpdatePosition(long position, long duration) {

        if (durationSeconds == 0)
            durationSeconds = (int) duration;

    }

    @Override
    public void onUpdateVolume(int currentVolume) {
        volume = currentVolume;
    }

    public void setVolume(int currentVolume) {
        this.volume = currentVolume;
    }

    public int getVolume() {
        return volume;
    }

    @Override
    public void onPaused() {

//        isPlaying = false;
    }

    @Override
    public void onStoped() {

        isPlaying = false;
    }

    @Override
    public void onSetURI() {

    }

    @Override
    public void onPlayCompleted() {

        LibreLogger.d(TAG, "On PlayCompleted-Helper");
        playNextSong(1);
    }

    @Override
    public void onPlaying() {

        isPlaying = true;
    }

    @Override
    public void onActionSuccess(Action action) {

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onActionFail(String actionCallback, UpnpResponse response, String cause) {

        LibreLogger.d(TAG,"onActionFail, UpnpResponse "+response.getResponseDetails()+" cause "+cause);
    }

    @Override
    public void onExceptionHappend(Action actionCallback, String mTitle, String cause) {
        LibreLogger.d(TAG, "onExceptionHappend for Title " + mTitle + ", cause = " + cause);

    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isThisTheLastSong() {
        try {
            int currentPos = dmsBrowseHelper.getAdapterPosition();
            int size = dmsBrowseHelper.getDidlList().size();
            LibreLogger.d(TAG, "isThisTheLastSong size "+size+" currentPos "+currentPos);
            return size - 1 - currentPos <= 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isThisOnlySong() {
        try {
            int size = dmsBrowseHelper.getDidlList().size();
            return size == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isThisFirstSong() {
        try {
            int currentPos = dmsBrowseHelper.getAdapterPosition();
            int size = dmsBrowseHelper.getDidlList().size();
            LibreLogger.d(TAG, "isThisFirstSong size =" + size + ", currentpos " + currentPos);
            return currentPos == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
