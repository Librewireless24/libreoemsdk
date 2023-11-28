package com.libreAlexa.netty;

import com.libreAlexa.LibreApplication;
import com.libreAlexa.Scanning.EchoServerHandler;
import com.libreAlexa.luci.LSSDPNodeDB;
import com.libreAlexa.luci.LSSDPNodes;
import com.libreAlexa.luci.LUCIControl;
import com.libreAlexa.util.LibreLogger;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * This file checks if the particular socket connection is still alive.
 * This gets called internally for every NettyAndroid client object
 * Created by praveena on 7/18/15.
 */
public class HeartbeatHandler extends ChannelDuplexHandler {
    public static String TAG= HeartbeatHandler.class.getSimpleName();
    private String ipadddress;

    public HeartbeatHandler(String ipadddress) {
        super();
        this.ipadddress = ipadddress;
    }

    /**
     * Ping a host and return an int value of 0 or 1 or 2 0=success, 1=fail,
     * 2=error
     * <p>
     * Does not work in Android emulator and also delay by '1' second if host
     * not pingable In the Android emulator only ping to 127.0.0.1 works
     *
     * @param host host in dotted IP address format
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public int pingHost(String host, int timeout) throws IOException,
            InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        timeout /= 1000;
        String cmd = "ping -c 5 -W " + timeout + " " + host;
        Process proc = runtime.exec(cmd);
        LibreLogger.d(TAG, "Ping Result : " + cmd);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(proc.getInputStream()));
        int read;
        char[] buffer = new char[4096];
        StringBuffer output = new StringBuffer();
        while ((read = reader.read(buffer)) > 0) {
            output.append(buffer, 0, read);
        }
        reader.close();
        LibreLogger.d(TAG, "Ping result" + output.toString() + "For URL" + host);
        proc.waitFor();
        int exit = proc.exitValue();
        return exit;
    }

    public static boolean hasService(InetAddress host, int port) throws IOException {
        boolean status = false;
        int TIMEOUT = 3000;
        Socket sock = new Socket();

        try {
            sock.connect(new InetSocketAddress(host, port), TIMEOUT);
            if (sock.isConnected()) {
                sock.close();
                status = true;
            }
        } catch (ConnectException | NoRouteToHostException | SocketTimeoutException ex) {
            ex.printStackTrace();
        }
        return status;
    }

    public boolean ping(String url) {
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(url);
        } catch (UnknownHostException e) {

            e.printStackTrace();
            return false;

        }
      /*  try {

            // Executes the command.

            Process process = Runtime.getRuntime().exec("ping -c 1 "+ url);

            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();

            LibreLogger.d(TAG,"Ping result" + output.toString()+ "For URL" + url);
        } catch (IOException e) {

            throw new RuntimeException(e);

        } catch (InterruptedException e) {

            throw new RuntimeException(e);
        }*/
        try {
            int Result = pingHost(url, 5500); //For RELEASE
            LibreLogger.d(TAG, "First Ping Host" + "\n" + url + "- Result " + Result);
            if (Result != 0) {
                int mResult = pingHost(url, 5500);
                LibreLogger.d(TAG, "Second Ping Host" + "\n" + url + "- Result " + Result);
                return mResult == 0;
            } else {
                return true;
            }

            /*if (addr.isReachable(5000)) {
                LibreLogger.d(TAG,"InetAddress" + "\n"+ url + "- Respond OK");
                return true;
            } else {
                LibreLogger.d(TAG, "InetAddress" + "\n" + url + "- Respond NOT OK For First Ping");
                if(addr.isReachable(5000)){
                    LibreLogger.d(TAG, "InetAddress" + "\n" + url + "- Respond OK For Second Ping");
                    return true;
                }else {
                    LibreLogger.d(TAG, "InetAddress" + "\n" + url + "- Respond Not OK For Second Ping So Returning FALSE");
                    return false;
                }
            }*/
        } catch (IOException e) {

            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object evt) throws Exception {
        //   System.out.println("Event Fired"); // Doesn't trigger
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                NettyAndroidClient nettyAndroidClient = LUCIControl.luciSocketMap.get(ipadddress);
                LSSDPNodeDB mNodeDB1 = LSSDPNodeDB.getInstance();
                LSSDPNodes mNode = mNodeDB1.getTheNodeBasedOnTheIpAddress(ipadddress);
                //LibreLogger.d(TAG,"Scan_Netty", "Triggered Heartbeeat check") ;
                if (nettyAndroidClient == null) {
                    return;
                }

                if (mNode != null) {
                    LibreLogger.d(TAG, "Last Notified Time " + nettyAndroidClient.getLastNotifiedTime() +
                            " For the Ip " + nettyAndroidClient.getRemotehost() + " Device Name " + mNode.getFriendlyname());
                } else {
                    LibreLogger.d(TAG, "Last Notified Time " + nettyAndroidClient.getLastNotifiedTime() +
                            " For the Ip " + nettyAndroidClient.getRemotehost());
                }

                /* If we are Missing 6 Alive notification */
                if ((System.currentTimeMillis() - (nettyAndroidClient.getLastNotifiedTime())) > 20000) {
//                    LibreLogger.d(HeartbeatHandler.class.getSimpleName(), ipadddress + "Socket removed because we did not get notification since last 11 second");
                    LibreLogger.d(HeartbeatHandler.class.getSimpleName(), ipadddress + "Missed 6 Alive Notifications ( >60 sec )");
                    BusProvider.getInstance().post(new RemovedLibreDevice(ipadddress));

                    if (LUCIControl.luciSocketMap.containsKey(ipadddress)) {
                        if (isSocketToBeRemovedFromTheTCPMap(channelHandlerContext)) {
                            LUCIControl.luciSocketMap.remove(ipadddress);
                            LibreApplication.securecertExchangeSucessDevices.clear();
                           /// LUCIControl.handshake.clear();

                            BusProvider.getInstance().post(new RemovedLibreDevice(ipadddress));
                        }
                    }
                }
            }
        }
    }

    private boolean isSocketToBeRemovedFromTheTCPMap(ChannelHandlerContext ctx) {
        NettyAndroidClient mAndroidClient = LUCIControl.luciSocketMap.get(ipadddress);
        /* If Hashmap is returning Null that means Hashmap is Empty for the that particular AP , So we can return it as False */
        if (mAndroidClient == null || mAndroidClient.handler == null || mAndroidClient.handler.mChannelContext == null)
            return false;

        if (ctx != null && ctx.channel().id() == mAndroidClient.handler.mChannelContext.channel().id()) {
            return true;
        } else {
            LibreLogger.d(TAG, "BROKEN" + ipadddress + "id DID NOT MATCH " + ctx.channel().id() + " "
                    + LUCIControl.luciSocketMap.get(ipadddress).handler.mChannelContext.channel().id());
            return false;
        }
    }
}
