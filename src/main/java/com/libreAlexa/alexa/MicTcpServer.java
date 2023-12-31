package com.libreAlexa.alexa;

import android.system.ErrnoException;
import android.util.Log;
import com.libreAlexa.LibreApplication;
import com.libreAlexa.Scanning.ScanThread;
import com.libreAlexa.util.LibreLogger;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by Amit T on 10-08-2017.
 */

public class MicTcpServer {
    private final static String TAG = MicTcpServer.class.getSimpleName();
    private static MicTcpServer micTcpServer;
    private ServerSocket micTcpServerSocket;
    public static int MIC_TCP_SERVER_PORT;
    private Socket currentDeviceMicClientSocket;
    private DataOutputStream dataOutputStream;
    private MicServerThread micServerThread;
    private boolean isMicServerRunning;
    private ByteArrayOutputStream totalBufferStream;
    private MicTcpServerExceptionListener micTcpServerExceptionListener;

    private MicTcpServer() {}

    public static MicTcpServer getMicTcpServer() {
        if (micTcpServer == null)
            micTcpServer = new MicTcpServer();
        return micTcpServer;
    }

    public void startTcpServer(MicTcpServerExceptionListener micListener) throws ErrnoException {
        try {
            if (isMicServerRunning && micServerThread != null && micServerThread.isAlive()) {
                LibreLogger.d(TAG,"startMicTcpServer micServerRunning");
                return;
            }

            this.micTcpServerExceptionListener = micListener;
            MIC_TCP_SERVER_PORT = ScanThread.getInstance().isLSSDPPortAvailableAndReturnAvailablePort();
//            LibreLogger.d(TAG, "ptt port got is " + MIC_TCP_SERVER_PORT);
            while (MIC_TCP_SERVER_PORT == LibreApplication.mTcpPortInUse) {
                MIC_TCP_SERVER_PORT = ScanThread.getInstance().isLSSDPPortAvailableAndReturnAvailablePort();
            }
            LibreLogger.d(TAG, "ptt port got is " + MIC_TCP_SERVER_PORT + " . " + LibreApplication.mTcpPortInUse);
            micServerThread = new MicServerThread();
            micServerThread.start();
        } catch (Exception e) {
            LibreLogger.d(TAG, "startTcpServer : " + e.getMessage());
        }

    }

    private class MicServerThread extends Thread {
        public void run() {
            try {
                micTcpServerSocket = new ServerSocket(MIC_TCP_SERVER_PORT);
                totalBufferStream = new ByteArrayOutputStream();
                LibreLogger.d(TAG, "tcp server started, ptt port = " + MIC_TCP_SERVER_PORT);

                isMicServerRunning = true;

                Socket socket = null;
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        if (micTcpServerSocket!=null) {
                            socket = micTcpServerSocket.accept();
                            HandleClientSocketThread handleClientSocketThread = new HandleClientSocketThread(socket);
                            handleClientSocketThread.start();
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                        LibreLogger.d(TAG, e.getMessage());
                        isMicServerRunning = false;
                        micTcpServerExceptionListener.micTcpServerException(e);
                        if (e.getMessage()!=null && e.getMessage().contains("Socket is closed")){

                        }
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
                LibreLogger.d(TAG, e.getMessage());
                micTcpServerExceptionListener.micTcpServerException(e);
                isMicServerRunning = false;
            }
        }
    }

    private class HandleClientSocketThread extends Thread {

        public HandleClientSocketThread(Socket clientSocket) {
            currentDeviceMicClientSocket = clientSocket;
        }

        @Override
        public void run() {
            super.run();
            LibreLogger.d(TAG, "client connected, ip = " + currentDeviceMicClientSocket.getInetAddress().getHostAddress()
                    + ", port = " + currentDeviceMicClientSocket.getPort());
            try {
                dataOutputStream = new DataOutputStream(currentDeviceMicClientSocket.getOutputStream());
            } catch (Exception e) {
//                e.printStackTrace();
                micTcpServerExceptionListener.micTcpServerException(e);
                LibreLogger.d(TAG, e.getMessage());
            }
            /*try {
                fileOutputStream = new FileOutputStream(filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                micTcpServerExceptionListener.micTcpServerException(e);
                LibreLogger.d(TAG,TAG,e.getMessage());
            }*/
        }
    }

    public void sendDataToClient(byte[] audioBuffer) throws OutOfMemoryError {
        if (currentDeviceMicClientSocket != null) {
            if (currentDeviceMicClientSocket.isConnected()) {
                try {
                    if (dataOutputStream != null) {
                        if (totalBufferStream != null) {
                            totalBufferStream.write(audioBuffer);
                            dataOutputStream.write(totalBufferStream.toByteArray(), 0, totalBufferStream.toByteArray().length);
                        /*if (fileOutputStream!=null) {
                            fileOutputStream.write(totalBufferStream.toByteArray());
                        }*/
                            LibreLogger.d(TAG, "write successful,totalBufferStream = " + Arrays.toString(totalBufferStream.toByteArray()));
                            totalBufferStream.reset();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LibreLogger.d(TAG, e.getMessage());
                    totalBufferStream.reset();
                    currentDeviceMicClientSocket = null;
                    dataOutputStream = null;
//                    fileOutputStream = null;
                    micTcpServerExceptionListener.micTcpServerException(e);
                }
            } else {
                /*Saving recorded buffer data to total byte array until client socket is accepted*/
                try {
                    if (totalBufferStream!=null)
                        totalBufferStream.write(audioBuffer);
                    LibreLogger.d(TAG, "device not connected,totalBufferStream = " + Arrays.toString(totalBufferStream.toByteArray()));
                } catch (Exception e) {
//                    e.printStackTrace();
                    micTcpServerExceptionListener.micTcpServerException(e);
                    LibreLogger.d(TAG, e.getMessage());
                }
            }
        } else {
            /*Saving recorded buffer data to total byte array until client socket is accepted*/
            try {
                if (totalBufferStream!=null)
                    totalBufferStream.write(audioBuffer);
            } catch (Exception e) {
//                e.printStackTrace();
                micTcpServerExceptionListener.micTcpServerException(e);
                LibreLogger.d(TAG, e.getMessage());
            }
        }
    }

    public void close() {
        isMicServerRunning = false;
        if (micServerThread != null)
            micServerThread.interrupt();
        if (micTcpServerSocket != null && !micTcpServerSocket.isClosed()) {
            try {
                micTcpServerSocket.close();
            } catch (IOException e) {
//                e.printStackTrace();
                micTcpServerExceptionListener.micTcpServerException(e);
            }
        }
        micTcpServerSocket = null;
        micTcpServer = null;
    }


    void createVerificationFile() {
        /*try {
            fileOutputStream.close();
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            micTcpServerExceptionListener.micTcpServerException(e);
        }*/
    }

    public interface MicTcpServerExceptionListener {
        void micTcpServerException(Exception e);
    }

    void clearAudioBuffer() {
        totalBufferStream.reset();
    }


}


