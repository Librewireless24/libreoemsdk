package com.libreAlexa.alexa;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;
import com.libreAlexa.Ls9Sac.GcastUpdateAdapter;
import com.libreAlexa.util.LibreLogger;
import java.util.Arrays;

/**
 * Created by Amit T on 05-08-2017.
 */

public class AudioRecordUtil {
    String TAG = AudioRecordUtil.class.getSimpleName();
    private static final int RECORDER_SAMPLERATE = 16000/*44100*/;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final String CLASS = AudioRecordUtil.class.getSimpleName();
    private static AudioRecordUtil audioRecordUtil;
    private AudioRecord audioRecord = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    private int BytesPerElement = 2; // 2 bytes in 16bit format
    private int minAudioBufferSize;
    private int shortsRead;
    private AudioRecordCallback audioRecordCallback;
    private AudioTrack audioTrackToPlay;

    /*Singleton*/
    private AudioRecordUtil() {
    }

    public static AudioRecordUtil getAudioRecordUtil() {
        if (audioRecordUtil == null)
            audioRecordUtil = new AudioRecordUtil();

        return audioRecordUtil;
    }

    public void startRecording(final AudioRecordCallback audioRecordCallback) {
        this.audioRecordCallback = audioRecordCallback;

        try {
            minAudioBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
          /*  *//*for voice only*//*
            if (ActivityCompat.checkSelfPermission(, permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }*/
            audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                BufferElements2Rec * BytesPerElement);

            audioRecord.startRecording();
            int recordingState = audioRecord.getRecordingState();
            LibreLogger.d(TAG, "RecordingState() after startRecording() = " + recordingState);
            if (recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                LibreLogger.d(TAG, "Audio recorder not free");
                audioRecordCallback.recordError("Audio recorder not free");
            }

            isRecording = true;
            recordingThread = new Thread(new Runnable() {
                public void run() {
                    sendAudioBufferToDevice(audioRecordCallback);
                }
            }, "AudioRecorder Thread");

            recordingThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAudioBufferToDevice(AudioRecordCallback audioRecordCallback) {
        /*if (audioRecord == null) {
            audioRecordCallback.recordError("Recorder not initialised");
            return;
        }*/

        // Write the output audio in byte
        short[] audioBufferInShorts = new short[/*minAudioBufferSize*/BufferElements2Rec];
        while (isRecording) {
            // gets the voice output from microphone to byte format

            try{
                shortsRead = audioRecord.read(audioBufferInShorts, 0, /*minAudioBufferSize*/BufferElements2Rec);

                if (shortsRead == AudioRecord.ERROR_BAD_VALUE || shortsRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    LibreLogger.d(TAG,"sendAudioBufferToDevice Error reading from microphone.");
                    audioRecordCallback.recordError("Error reading from microphone.");
                    break;
                }

                byte[] bufferBytes = short2byte(audioBufferInShorts);
                LibreLogger.d(TAG, "bufferBytes[] = " + Arrays.toString(bufferBytes));
                audioRecordCallback.sendBufferAudio(bufferBytes);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /*private void writeAudioBufferToFile(AudioRecordCallback audioRecordCallback) {
        // Write the output audio in byte

        String filePath = Environment.getExternalStorageDirectory() + "/audioSample.pcm";
        short audioBufferInShorts[] = new short[*//*minAudioBufferSize*//*BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
            while (isRecording) {
                // gets the voice output from microphone to byte format

                if (audioRecord == null) {
                    audioRecordCallback.recordError("Recorder not initialised");
                    return;
                }
                shortsRead = audioRecord.read(audioBufferInShorts, 0, *//*minAudioBufferSize*//*BufferElements2Rec);

                if (shortsRead == AudioRecord.ERROR_BAD_VALUE || shortsRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    LibreLogger.d(TAG,"writeAudioBufferToFile", "Error reading from microphone.");
                    audioRecordCallback.recordError("Error reading from microphone.");
                    break;
                }

                try {
                    // writes the data to file from buffer
                    // stores the voice buffer
                    LibreLogger.d(TAG,CLASS, "short[] = " + Arrays.toString(audioBufferInShorts));
                    byte bufferBytes[] = short2byte(audioBufferInShorts);
                    LibreLogger.d(TAG,CLASS, "bufferBytes[] = " + Arrays.toString(bufferBytes));

                    //writing to file
                    os.write(bufferBytes, 0, *//*minAudioBufferSize*//*BufferElements2Rec * BytesPerElement);
                    audioRecordCallback.sendBufferAudio(bufferBytes);

                } catch (IOException e) {
                    e.printStackTrace();
                    audioRecordCallback.recordError(e.getMessage());
                }
            }

            try {
                os.close();
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
                audioRecordCallback.recordError(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            audioRecordCallback.recordError(e.getMessage());
        }
    }*/

    public void stopRecording() {
        // stops the recording activity
        if (audioRecord !=null) {
            try {
                isRecording = false;
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
                audioRecordCallback.recordStopped();
                recordingThread = null;
                MicTcpServer.getMicTcpServer().clearAudioBuffer();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }
}
