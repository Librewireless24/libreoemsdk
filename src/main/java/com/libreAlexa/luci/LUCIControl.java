package com.libreAlexa.luci;

import static com.libreAlexa.LibreApplication.GLOBAL_TAG;
import static com.libreAlexa.constants.MIDCONST.MID_REGISTER;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.libreAlexa.LibreApplication;
import com.libreAlexa.LibreEntryPoint;
import com.libreAlexa.constants.Constants;
import com.libreAlexa.constants.LSSDPCONST;
import com.libreAlexa.constants.MIDCONST;
import com.libreAlexa.netty.NettyAndroidClient;
import com.libreAlexa.util.GoogleTOSTimeZone;
import com.libreAlexa.util.LibreLogger;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.io.LineReader;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * Created by Khajan on 1/8/15.
 */
public class LUCIControl {
    private static final String TAG = LUCIControl.class.getSimpleName();
    private static final int LUCI_CONTROL_PORT = 7777;
    private String SERVER_IP;
    public static final int LUCI_RESP_PORT = 3333;
    public static ConcurrentHashMap<String, NettyAndroidClient> luciSocketMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, ChannelHandlerContext> channelHandlerContextMap = new ConcurrentHashMap<>();
    private boolean isDataSent;

    public LUCIControl(String serverIP) {
        SERVER_IP = serverIP;
    }

    public static boolean TcpSendData(NettyAndroidClient tcpSocket, byte[] message) {
        if (tcpSocket != null) {
            tcpSocket.write(message);
        }
        return true;
    }

    public void deRegister() {
        SendCommand(4, null, LSSDPCONST.LUCI_SET);
    }

    public void sendAsynchronousCommandSpecificPlaces() {
        /* Crash Fixed Reported For Multiple Times SAC and rebooting Fix*/
        /*        if ((mNetIf != null)  && (new Utils().getIPAddress(true)!=null))*/
        {
            String registerData;
            //New MB3 source code changes
            if (LibreEntryPoint.Companion.getInstance().getRegisterMB3Data() != null
                && LibreEntryPoint.Companion.getInstance().getRegisterMB3Data().trim().length() > 0) {
                registerData = LibreEntryPoint.Companion.getInstance().getRegisterMB3Data();
            } else {
                //Old Source code
                registerData = new Utils().getIPAddress(true) + "," + LUCI_RESP_PORT;
            }
            SendCommand(MID_REGISTER, registerData, LSSDPCONST.LUCI_SET);
            LibreLogger.d(GLOBAL_TAG, "MB3 Data Sent " + registerData);
        }
    }

    public boolean ping(String url) {
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(url);
        } catch (UnknownHostException e) {

            e.printStackTrace();
            return false;

        }
        try {
            if (addr.isReachable(2500)) {
                LibreLogger.d(TAG, "InetAddress" + "\n" + url + "- Respond OK");
                return true;
            } else {
                LibreLogger.d(TAG, "InetAddress" + "\n" + url + "- Respond NOT OK");
                return false;
            }
        } catch (IOException e) {

            return false;
        }
    }


    public void SendCommand(final int MID, final String data, final int cmd_type) {
        new Thread() {
            public void run() {
                String messageStr = null;
                LUCIPacket packet = null;
                messageStr = data;
                NettyAndroidClient tcpSocketSendCtrl = null;

                LibreLogger.d(TAG, "SendLUCICommand  " + MID + "  ip  " + SERVER_IP +" data "+data+ " CMD_Type "+cmd_type);

                if (data != null)
                    packet = new LUCIPacket(messageStr.getBytes(), (short) messageStr.getBytes().length, (short) MID, (byte) cmd_type);
                else
                    packet = new LUCIPacket(null, (short) 0, (short) MID, (byte) cmd_type);

                InetAddress local = null;
                try {
                    local = InetAddress.getByName(SERVER_IP);
                } catch (UnknownHostException e) {

                    e.printStackTrace();
                }

                int msg_length = packet.getlength();
                byte[] message = new byte[msg_length];
                packet.getPacket(message);

                LibreLogger.d(TAG,"Scan_Netty" + SERVER_IP);

                if (SERVER_IP == null)
                    return;

                if (!(luciSocketMap.containsKey(SERVER_IP))) {
                    // LibreLogger.d(TAG,"Scan_Netty","Socket is Not Available for the IP "+SERVER_IP);
                    LibreLogger.d(TAG,"SCAN_NETTY Socket not present for " + SERVER_IP + " " + packet.getCommand());
                } else {
                    LibreLogger.d(TAG,"Scan_Netty Socket is  Available for the IP " + SERVER_IP);
                    try {
                        tcpSocketSendCtrl = luciSocketMap.get(SERVER_IP);
                        LibreLogger.d(TAG, "SERVERIPADDRESS*****" + tcpSocketSendCtrl.getRemotehost() + "commad was =" + packet.getCommand());
                        isDataSent = TcpSendData(tcpSocketSendCtrl, message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LibreLogger.d(TAG, "SERVERIPADDRESS*****" + SERVER_IP + "Addition Exception Happend  in SendCommand ");

                    }

                }
                if (isDataSent) {
                    LibreLogger.d(TAG, "Tcp GetLUCICommand is Successfull ");
                } else {
                    LibreLogger.d(TAG, "Tcp GetLUCICommand is UnSuccessfull ");
                }


                Bundle bundle = new Bundle();
                bundle.putString("data", data);
                bundle.putInt("mid", MID);
                bundle.putInt("cmd_type", cmd_type);
                bundle.putString("server_ip", SERVER_IP);
                //Message msg=LibreApplication.commandQueuHandler.obtainMessage(1);
                // LibreApplication.commandQueuHandler.sendMessage(msg);

            }

        }.start();

    }

    public void SendCommand(final ArrayList<LUCIPacket> luciPackets) {
        new Thread() {
            public void run() {

                for (LUCIPacket luciPacket : luciPackets) {
                    // DatagramSocket s = null;
                    NettyAndroidClient tcpSocketSendCtrl = null;

                    InetAddress local = null;
                    try {
                        local = InetAddress.getByName(SERVER_IP);
                    } catch (UnknownHostException e) {

                        e.printStackTrace();
                    }

                    int msg_length = luciPacket.getlength();
                    byte[] message = new byte[msg_length];
                    luciPacket.getPacket(message);

                    LibreLogger.d(TAG, "SendCommand, MB = " + luciPacket.getCommand());
                    LibreLogger.d(TAG, "SendCommand, luci packets array, message = " + new String(message));

                    if (SERVER_IP == null)
                        return;

                    if (!(luciSocketMap.containsKey(SERVER_IP))) {

                        LibreLogger.d(TAG,"SCAN_NETTY Socket not present for " + SERVER_IP + " " + luciPacket.getCommand());

                        /*       try {
                            tcpSocketSendCtrl = new NettyAndroidClient(local, server_port);
                            if (ping(SERVER_IP))
                                luciSocketMap.put(SERVER_IP, tcpSocketSendCtrl);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        */
                    } else {
                        try {
                            tcpSocketSendCtrl = luciSocketMap.get(SERVER_IP);
                            LibreLogger.d(TAG, "SERVERIPADDRESS*****" + tcpSocketSendCtrl.getRemotehost() + "commad was =" + luciPacket.getCommand());
                            isDataSent = TcpSendData(tcpSocketSendCtrl, message);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LibreLogger.d(TAG, "SERVERIPADDRESS*****" + SERVER_IP + "Addition Exception Happend  in SendCommand (Listof Packets)");
                        }
                    }
                    if (isDataSent) {
                        LibreLogger.d(TAG, "Tcp SendCommand is Successfull ");
                    } else {
                        isDataSent = false;
                        LibreLogger.d(TAG, "Tcp SendCommand is UnSuccessfull ");
                    }

                    try {
                        /* Sleeping 100 mili seconds to make sure the message order is correct */
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }


    public static void SendCommandWithIp(final int cmd, final String data, final int cmd_type, final String ip) {
        new Thread() {
            public void run() {
                String messageStr = null;
                LUCIPacket packet = null;
                messageStr = data;
                NettyAndroidClient tcpSocketSendCtrl = null;

                if (data != null)
                    packet = new LUCIPacket(messageStr.getBytes(), (short) messageStr.getBytes().length, (short) cmd, (byte) cmd_type);
                else
                    packet = new LUCIPacket(null, (short) 0, (short) cmd, (byte) cmd_type);

                int server_port = LUCI_CONTROL_PORT;

                InetAddress local = null;
                try {
                    local = InetAddress.getByName(ip);
                } catch (UnknownHostException e) {

                    e.printStackTrace();
                }

                int msg_length = packet.getlength();

                byte[] message = new byte[msg_length];
                packet.getPacket(message);

                /* To handle the odd crash */
                if (ip == null)
                    return;
                ;

                if (!(luciSocketMap.containsKey(ip))) {
                    LibreLogger.d(TAG,"SCAN_NETTY Socket not present for " + ip + " " + cmd);
                } else {
                    try {
                        tcpSocketSendCtrl = luciSocketMap.get(ip);

                        LibreLogger.d(TAG, "SERVERIPADDRESS*****" + tcpSocketSendCtrl.getRemotehost() + "commad was =" + cmd);
                        TcpSendData(tcpSocketSendCtrl, message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LibreLogger.d(TAG, "SERVERIPADDRESS*****" + ip + "Addition Exception Happend  in SendCommandWithIp");
                    }
                }
                // TcpSendData(tcpSocketSendCtrl, message);

            }

        }.start();

    }


    public void sendGoogleTOSAcceptanceIfRequired(Context mContext, String isCastDevice) {

        /* Google cast settings*/
        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(Constants.SHOWN_GOOGLE_TOS, Context.MODE_PRIVATE);
        String shownGoogle = sharedPreferences.getString(Constants.SHOWN_GOOGLE_TOS, null);
        if (shownGoogle != null && isCastDevice != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LibreLogger.d(TAG, "Sending the Google TOS acceptance");
                    ArrayList<LSSDPNodes> lssdpNodes = LSSDPNodeDB.getInstance().GetDB();
                    for (int i = 0; i < lssdpNodes.size(); i++) {
                        LSSDPNodes node = lssdpNodes.get(i);

                        LUCIControl control = new LUCIControl(node.getIP());
                        GoogleTOSTimeZone googleTOSTimeZone = LibreApplication.GOOGLE_TIMEZONE_MAP.get(node.getIP());

                        if (googleTOSTimeZone != null && node.getgCastVerision() != null) {

                            if (googleTOSTimeZone.isShareAccepted() == false)
                                control.SendCommand(MIDCONST.GCAST_TOS_SHARE_COMMAND, "1:" + googleTOSTimeZone.getTimezone(), LSSDPCONST.LUCI_SET);

                            else
                                control.SendCommand(MIDCONST.GCAST_TOS_SHARE_COMMAND, "3:" + googleTOSTimeZone.getTimezone(), LSSDPCONST.LUCI_SET);

                        }
                    }

                }
            }).start();
        }

    }


}

