package com.libreAlexa.alexa;

public interface AudioRecordCallback {
    void recordError(String error);

    void recordStopped();

    void recordProgress(byte[] byteBuffer);

    void sendBufferAudio(byte[] audioBufferBytes);
}