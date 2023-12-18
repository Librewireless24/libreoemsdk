package com.libreAlexa.netty;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.cumulations.libreV2.model.SceneObject;
import com.libreAlexa.LibreApplication;
import com.libreAlexa.LibreEntryPoint;
import com.libreAlexa.Scanning.ScanningHandler;
import com.libreAlexa.app.dlna.dmc.server.ContentTree;
import com.libreAlexa.app.dlna.dmc.utility.PlaybackHelper;
import com.libreAlexa.app.dlna.dmc.utility.UpnpDeviceManager;
import com.libreAlexa.constants.Constants;
import com.libreAlexa.constants.LSSDPCONST;
import com.libreAlexa.constants.LUCIMESSAGES;
import com.libreAlexa.constants.MIDCONST;
import com.libreAlexa.luci.LSSDPNodeDB;
import com.libreAlexa.luci.LSSDPNodes;
import com.libreAlexa.luci.LUCIControl;
import com.libreAlexa.luci.LUCIPacket;
import com.libreAlexa.util.LibreLogger;
import com.squareup.otto.Bus;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.fourthline.cling.model.meta.RemoteDevice;


/**
 * Created by praveena on 7/10/15.
 */
public class NettyClientHandler extends ChannelHandlerAdapter {

    private static final String TAG = "NettyClientHandler";
    public ChannelHandlerContext mChannelContext;
    private ScanningHandler m_ScanningHandler = ScanningHandler.getInstance();

    private final String remotedevice;
    private Handler handler;


    public NettyClientHandler(final String remotedevice) {
        this.remotedevice = remotedevice;

        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();

        handler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Constants.CHECK_ALIVE) {
                    if (LUCIControl.luciSocketMap.containsKey(remotedevice)) {
                        if ((System.currentTimeMillis() - LUCIControl.luciSocketMap.get(remotedevice).getLastNotifiedTime() > 10000)) {
                            try {
                                LibreLogger.d(TAG, "Trying to write bye bye");
                                if (mChannelContext != null && isSocketToBeRemovedFromTheTCPMap(mChannelContext, remotedevice)) {

                                    String messageStr = "" + LibreApplication.LOCAL_IP;
                                    LUCIPacket packet = new LUCIPacket(messageStr.getBytes(), (short) messageStr.length(), (short) MIDCONST.MID_DE_REGISTER, (byte) LSSDPCONST.LUCI_SET);

                                    int msg_length = packet.getlength();
                                    byte[] message = new byte[msg_length];
                                    packet.getPacket(message);

                                    write(message, true);

                                }
                            } catch (Exception e) {
                                LibreLogger.d(TAG, "Failure while sending the deregyster command");
                            }
                            String message = msg.getData().getString("Message");
                            if (message != null) {
                                LibreLogger.d(TAG, "Send the Message as " + message + " to the ip " + remotedevice);
                            } else {
                                LibreLogger.d(TAG, "Send the Message as NULL  to the ip " + remotedevice);
                            }

                            if (mChannelContext != null && isSocketToBeRemovedFromTheTCPMap(mChannelContext, remotedevice)) {
                                if (handler.hasMessages(Constants.CHECK_ALIVE))
                           // *suma  comented before
                            handler.removeMessages(Constants.CHECK_ALIVE);
                                BusProvider.getInstance().post(new RemovedLibreDevice(remotedevice));
                            }

                        }
                    }
                }

            }
        };

    }

    private void write(byte[] sbytes, boolean b) {

        if (mChannelContext != null) {
            final ByteBuf byteBufMsg = mChannelContext.channel().alloc().buffer(sbytes.length);
            byteBufMsg.writeBytes(sbytes);
            byte[] bytenew = new byte[sbytes.length];
            String str = "";
            bytenew = byteBufMsg.array();
            for (int i = 0; i < bytenew.length; i++) {
                str += bytenew[i] + ",";
            }
/*
            ChannelFuture channelFuture = mChannelContext.channel().writeAndFlush(byteBufMsg);
            boolean variable = channelFuture.isSuccess();
            LibreLogger.d(TAG,"Exact Data Sent : " +variable+": DataSent To Device :"+str);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    LibreLogger.d(TAG, "Application writing " + new String(sbytes) + " completed ");

                }
            });
*/

            LibreLogger.d(TAG, "Channel Is Connected or Not " + mChannelContext.channel().isActive()+"remote device\n"+remotedevice);
//            mChannelContext.pipeline().fireChannelRead("1");
            try {
                mChannelContext.channel().writeAndFlush(byteBufMsg).sync();
                handler.sendEmptyMessageDelayed(Constants.CHECK_ALIVE, Constants.CHECK_ALIVE_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
                LibreLogger.d(TAG, "InterruptedException Happend in Writing the Data, Dont Know What is the Issue ");
            } catch (Exception e) {
                e.printStackTrace();

                LibreLogger.d(TAG, "Exception Happend in Writing the Data, Pending Write Issues Have been Cancelled, Device Got Rebooted ");
            }

        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        byte[] databytes = (byte[]) msg;

        /*ByteBuf m = (ByteBuf) msg;

        int numReadBytes = ((ByteBuf) msg).readableBytes();
        byte[] bytes = new byte[numReadBytes];
        m.readBytes(bytes);

        LibreLogger.d(TAG, "Application received of length mumReadByyes =" +numReadBytes);

        byte[] databytes=bytes.clone();*/
        NettyData nData = new NettyData(remotedevice, databytes);

        LUCIPacket packet = new LUCIPacket(nData.getMessage());

        /* Special case to handle the 54 for DMR playback completed */
        if (packet.getCommand() == 54) {
            String message = new String(packet.getpayload());

            String mIpaddress = nData.getRemotedeviceIp();
            ScanningHandler mScanHandler = ScanningHandler.getInstance();
            SceneObject currentSceneObject = mScanHandler.getSceneObjectFromCentralRepo(mIpaddress);

            LibreLogger.d(TAG, "channelRead, MB 54 message "+message+" for "+currentSceneObject.getSceneName());
            LibreLogger.d(TAG, "channelRead, current source "+currentSceneObject.getCurrentSource()
                    +", play url = "+ currentSceneObject.getPlayUrl());
            if (message != null && (message.equalsIgnoreCase(Constants.DMR_PLAYBACK_COMPLETED)
                    || message.contains(Constants.FAIL)
                    || message.contains(Constants.DMR_SONG_UNSUPPORTED)
                    || message.equalsIgnoreCase(LUCIMESSAGES.NEXTMESSAGE))) {
                if (currentSceneObject.getPlayUrl() != null) {
                    LibreLogger.d(TAG, "DMR completed for URL " + currentSceneObject.getPlayUrl());
                }
                if (currentSceneObject != null && currentSceneObject.getPlayUrl() != null &&
                        currentSceneObject.getPlayUrl().contains(LibreApplication.LOCAL_IP)
                        && currentSceneObject.getPlayUrl().contains(ContentTree.AUDIO_PREFIX)
                        && currentSceneObject.getCurrentSource() == Constants.DMR_SOURCE) {

                    /* If the playback url is from our ip address
                     *  and the current source is DMR then only we need to send the URL. We get the source change in the message box 10.
                     *  mantis id:584
                     *  */
                    RemoteDevice renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(nData.getRemotedeviceIp());
                    if (renderingDevice != null) {
                        String renderingUDN = renderingDevice.getIdentity().getUdn().toString();
                        PlaybackHelper playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP.get(renderingUDN);
                        if (playbackHelper != null) {
                            playbackHelper.playNextSong(1);
                        } else {
                            LibreLogger.d(TAG, "channelRead, PlayBackHelper Null for "+nData.remotedeviceIp);
                        }
                    } else {
                        LibreLogger.d(TAG,"channelRead renderingDevice null for "+nData.remotedeviceIp);
                    }
                }
            } else if (message != null && message.equalsIgnoreCase(LUCIMESSAGES.PREVMESSAGE)) {
                if (currentSceneObject.getPlayUrl() != null)
                    LibreLogger.d(TAG, "DMR completed for URL " + currentSceneObject.getPlayUrl());
                if (currentSceneObject != null && currentSceneObject.getPlayUrl() != null &&
                        currentSceneObject.getPlayUrl().contains(LibreApplication.LOCAL_IP)
                        && currentSceneObject.getPlayUrl().contains(ContentTree.AUDIO_PREFIX)
                        && currentSceneObject.getCurrentSource() == Constants.DMR_SOURCE) {

                    /* If the playback url is from our ip address
                     *  and the current source is DMR then only we need to send the URL. We get the source change in the message box 10.
                     *  mantis id:584
                     *  */
                    RemoteDevice renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(nData.getRemotedeviceIp());
                    if (renderingDevice != null) {
                        String renderingUDN = renderingDevice.getIdentity().getUdn().toString();
                        PlaybackHelper playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP.get(renderingUDN);
                        if (playbackHelper != null) {
                            playbackHelper.playNextSong(-1);
                        } else {
                            LibreLogger.d(TAG, "channelRead, PlayBackHelper Null for "+nData.remotedeviceIp);
                        }
                    } else {
                        LibreLogger.d(TAG,"channelRead renderingDevice null for "+nData.remotedeviceIp);
                    }
                }
            }
        }


        Bus bus = BusProvider.getInstance();

        try {
            bus.post(nData);
        } catch (Exception e) {
            e.printStackTrace();
            LibreLogger.d(TAG, TAG + "Exception for throwing an event in otto");
            /*if (LUCIControl.luciSocketMap.containsKey(remotedevice)) {
                if (isSocketToBeRemovedFromTheTCPMap(ctx, remotedevice)) {
                    LUCIControl.luciSocketMap.remove(remotedevice);
                }
            }*/
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LibreLogger.d(TAG, TAG + "addToNodeDb inside  exception Exception caught " + cause.getMessage()+"check\n"+cause.getMessage().contains("SSL")+"for IP\n"+remotedevice);
        cause.printStackTrace();

        LibreLogger.d(TAG, "SSL HandShake added hashmap\n " + LUCIControl.handshake.toString());

        if(cause.getMessage().contains("SSLHandshakeException")){
                    LibreLogger.d(TAG, "Socket Creating for Ip get my value wts my IP SSL HandShake\n " + remotedevice);

       if(LUCIControl.handshake.get("handshake")!=null&&!LUCIControl.handshake.get("handshake").contains(remotedevice)) {

           LSSDPNodes mToBeUpdateNode = ScanningHandler.getInstance().getLSSDPNodeFromCentralDB(remotedevice);
           LibreLogger.d(TAG, "Socket Creating for Ip get my value SSL HandShake\n " + mToBeUpdateNode);
           LUCIControl.handshake.put("handshake",remotedevice);

           LUCIControl.luciSocketMap.put(remotedevice, NettyAndroidClient.getDummyInstance());
           LibreLogger.d(TAG, "android developer luci socket map THREE adding SSL HandShake \n" + LUCIControl.luciSocketMap.toString());


           if (mToBeUpdateNode != null) {
               NettyAndroidClient tcpSocketSendCtr = null;
               try {
                   tcpSocketSendCtr = new NettyAndroidClient(mToBeUpdateNode.getNodeAddress(), 7777, true);
                   LUCIControl.handshake.put("handshake", remotedevice);

               } catch (Exception e) {
                   throw new RuntimeException(e);
               }

               LibreLogger.d(TAG, "Socket Created for Ip " + mToBeUpdateNode.getIP() +
                       " Printing From NettyAndroidClient Socket  " + tcpSocketSendCtr.getRemotehost());

               tcpSocketSendCtr.setLastNotifiedTime(System.currentTimeMillis());

               LUCIControl.luciSocketMap.put(mToBeUpdateNode.getIP(), tcpSocketSendCtr);
               LibreLogger.d(TAG, "android developer luci socket map FOUR adding \n" + LUCIControl.luciSocketMap.toString());

               new LUCIControl(mToBeUpdateNode.getIP()).sendAsynchronousCommandSpecificPlaces();
               if (mToBeUpdateNode.getIP() != null && /*mToBeUpdateNode.getDeviceState() != null && */!m_ScanningHandler.findDupicateNode(mToBeUpdateNode)) {
                   LibreLogger.d(TAG, "New Node is Found For the ipAddress " + mToBeUpdateNode.getIP());

                   boolean isFilteredSpeaker = LSSDPNodeDB.getInstance().hasFilteredModels(mToBeUpdateNode);
                   LibreLogger.d(TAG, "addToNodeDb, " + mToBeUpdateNode.getFriendlyname() + " isFilteredSpeaker = " + isFilteredSpeaker);

                   if (!mToBeUpdateNode.getUSN().isEmpty() /*&& isFilteredSpeaker*/) {
                       BusProvider.getInstance().post(mToBeUpdateNode);
                       m_ScanningHandler.lssdpNodeDB.addToNodeDb(mToBeUpdateNode);
                       LibreLogger.d(TAG, "addToNodeDb inside clientHandler loop, " + mToBeUpdateNode.getFriendlyname() + " isFilteredSpeaker = " + isFilteredSpeaker);

                   }
                   else {
                       LibreLogger.d(TAG, "USN is Empty " + mToBeUpdateNode.getIP());
                   }
               }
           }
       }
        }
        else{
            if(cause.getMessage().contains("SSL/TLS")){
                            //ctx.close();
                LibreLogger.d(TAG, TAG + "addToNodeDb inside  exception Exception caught else else\n " + cause.getMessage()+"check\n"+cause.getMessage().contains("SSL")+"for IP\n"+remotedevice);
                LSSDPNodes mToBeUpdateNode = ScanningHandler.getInstance().getLSSDPNodeFromCentralDB(remotedevice);
                LibreLogger.d(TAG, "Socket Creating for Ip get my value\n " + mToBeUpdateNode);

                LUCIControl.luciSocketMap.put(remotedevice, NettyAndroidClient.getDummyInstance());
                LibreLogger.d(TAG,"android developer luci socket map THREE adding \n"+LUCIControl.luciSocketMap.toString());

                if(mToBeUpdateNode!=null) {
                    NettyAndroidClient tcpSocketSendCtr = null;
                    try {
                        tcpSocketSendCtr = new NettyAndroidClient(mToBeUpdateNode.getNodeAddress(), 7777, false);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    LibreLogger.d(TAG, "Socket Created for Ip " + mToBeUpdateNode.getIP() +
                            " Printing From NettyAndroidClient Socket  " + tcpSocketSendCtr.getRemotehost());

                    tcpSocketSendCtr.setLastNotifiedTime(System.currentTimeMillis());

                    LUCIControl.luciSocketMap.put(mToBeUpdateNode.getIP(), tcpSocketSendCtr);
                    LibreLogger.d(TAG, "android developer luci socket map FOUR adding \n" + LUCIControl.luciSocketMap.toString());

                    new LUCIControl(mToBeUpdateNode.getIP()).sendAsynchronousCommandSpecificPlaces();
                    if (mToBeUpdateNode.getIP() != null /*&& mToBeUpdateNode.getDeviceState() != null */&& !m_ScanningHandler.findDupicateNode(mToBeUpdateNode)) {
                        LibreLogger.d(TAG, "New Node is Found For the ipAddress " + mToBeUpdateNode.getIP());

                        boolean isFilteredSpeaker = LSSDPNodeDB.getInstance().hasFilteredModels(mToBeUpdateNode);
                        LibreLogger.d(TAG, "addToNodeDb, " + mToBeUpdateNode.getFriendlyname() + " isFilteredSpeaker = " + isFilteredSpeaker);

                        if (!mToBeUpdateNode.getUSN().isEmpty() /*&& isFilteredSpeaker*/) {
                            BusProvider.getInstance().post(mToBeUpdateNode);
                            m_ScanningHandler.lssdpNodeDB.addToNodeDb(mToBeUpdateNode);
                            LibreLogger.d(TAG, "addToNodeDb inside clientHandler loop, " + mToBeUpdateNode.getFriendlyname() + " isFilteredSpeaker = " + isFilteredSpeaker);

                        }
                        else {
                            LibreLogger.d(TAG, "USN is Empty " + mToBeUpdateNode.getIP());
                        }
                    }
                }
//
//            /**
//             * LatestDiscoveryChanges
//             */

            if (LUCIControl.luciSocketMap.containsKey(remotedevice)) {
                if (isSocketToBeRemovedFromTheTCPMap(ctx, remotedevice)) {
                    BusProvider.getInstance().post(new RemovedLibreDevice(remotedevice));
                    LUCIControl.luciSocketMap.remove(remotedevice);
                    LibreApplication.securecertExchangeSucessDevices.clear();

                }
            }
            }

//
//            /**
//             * LatestDiscoveryChanges
//             */
//            if (LUCIControl.luciSocketMap.containsKey(remotedevice)) {
//                if (isSocketToBeRemovedFromTheTCPMap(ctx, remotedevice)) {
//                    BusProvider.getInstance().post(new RemovedLibreDevice(remotedevice));
//                    LUCIControl.luciSocketMap.remove(remotedevice);
//                    LibreApplication.securecertExchangeSucessDevices.clear();
//                }
//            }

        }
//        ctx.close();
///*SUMA: for now not removing dis commments untill final qa verified on multidevice support
// */
//        /**
//         * LatestDiscoveryChanges
//         */
//        if (LUCIControl.luciSocketMap.containsKey(remotedevice)) {
//            if (isSocketToBeRemovedFromTheTCPMap(ctx, remotedevice)) {
//                BusProvider.getInstance().post(new RemovedLibreDevice(remotedevice));
//                LUCIControl.luciSocketMap.remove(remotedevice);
//            }
//        }


    }

    public void channelActive(ChannelHandlerContext ctx) {
        LibreLogger.d(TAG, TAG + "Channel active " + remotedevice + "id as " + ctx.channel().id());
        mChannelContext = ctx;

    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);

        LibreLogger.d(TAG, TAG + "connecting to  " + remoteAddress.toString() + "from my localdevice " + localAddress);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        LibreLogger.d(TAG, TAG + "remotedevice disconnected " + remotedevice);

        super.disconnect(ctx, promise);
        /**
         * LatestDiscoveryChanges
         */
        if (LUCIControl.luciSocketMap.containsKey(remotedevice)) {
            if (isSocketToBeRemovedFromTheTCPMap(ctx, remotedevice)) {
                LUCIControl.luciSocketMap.remove(remotedevice);
                LibreApplication.securecertExchangeSucessDevices.clear();
                LUCIControl.handshake.clear();

            }
        }
    }

    public void write(final byte[] sbytes) {

        if (mChannelContext != null) {
            final ByteBuf byteBufMsg = mChannelContext.channel().alloc().buffer(sbytes.length);
            byteBufMsg.writeBytes(sbytes);
            byte[] bytenew;
            StringBuilder str = new StringBuilder();
            bytenew = byteBufMsg.array();
            for (byte b1 : bytenew) {
                str.append(b1).append(",");
            }

            /*ChannelFuture channelFuture = mChannelContext.channel().writeAndFlush(byteBufMsg);
            boolean variable = channelFuture.isSuccess();
            LibreLogger.d(TAG,"Exact Data Sent : " +variable+": DataSent To Device :"+str);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    LibreLogger.d(TAG, "Application writing " + new String(sbytes) + " completed ");

                }
            });
*/

            LibreLogger.d(TAG, "Channel Is Connected or " + mChannelContext.channel().isActive()+"***remote devices"+remotedevice);
//            mChannelContext.pipeline().fireChannelRead("1");
            try {
                mChannelContext.channel().writeAndFlush(byteBufMsg).sync();
                /* Device will not Send CommandStatus=1 for Volume */
                int mMessageBox = getMessageBoxofTheCommand(sbytes);
                /* KK According to the EMail I have Sent on 16 March , Regarding the LUCI Messages ,Device is not sending command Status as
                 * i.e., no immeidate response from the device.Only they will repsonse for the below Messagebox if we are doing GET
                 * */
                /* We are not using the below msgbox in the for Setting the Values , thats ComandType as SET */
                /*LUCI_MESSAGEBOX_CURRENT_SOURCE = #MB50
                LUCI_MESSAGEBOX_PLAYSTATE = #MB51
                LUCI_MESSAGEBOX_RSSI = #MB151

                *//* We are using the below msgbox thats ComandType as SET & GET*//*
                LUCI_MESSAGEBOX_VOLUMECONTROL = #MB64
                LUCI_MESSAGEBOX_ZVOLUMECONTROL = #MB219
                LUCI_MESSAGEBOX_KEYCONTROL = #MB41
                LUCI_MESSAGEBOX_REMOTE_UI = #MB42
                LUCI_MESSAGEBOX_LEDCONTROL = #MB207
                LUCI_MESSAGEBOX_NV_READ = #MB208
                LUCI_MESSAGEBOX_SETUP_STEREO_PAIR = #MB108
                LUCI_MESSAGEBOX_DEREGISTER_ASYNC  =#MB4*/
                if (!((mMessageBox == MIDCONST.VOLUME_CONTROL
                        /*|| mMessageBox == MIDCONST.ZONE_VOLUME*/
                        || mMessageBox == MIDCONST.MID_ENV_READ
                        || mMessageBox == MIDCONST.SET_UI
                        || mMessageBox == MIDCONST.MID_REMOTE
                        || mMessageBox == MIDCONST.MID_IOT_CONTROL
                        || mMessageBox == MIDCONST.MID_DE_REGISTER)
                        && getCommandType(sbytes) == LSSDPCONST.LUCI_SET)) {
                    LibreLogger.d(TAG, "Amit + NettyClient handler Timer is Started " + mMessageBox + " :: Value :: " + getCommandType(sbytes));

                    Message msg = new Message();
                    Bundle b = new Bundle();
                    b.putString("Message", str.toString());
                    msg.what = Constants.CHECK_ALIVE;
                    msg.setData(b);
                    handler.sendMessageDelayed(msg, Constants.CHECK_ALIVE_TIMEOUT);
                    /*LibreApplication.nettyActiveWriteDevices have not removed ,in future may be usuable */
                    LibreApplication.nettyActiveWriteDevices.put("active",remotedevice);
                  LibreLogger.d(TAG,"addToNodeDb inside NettyCLientHandler writing to MAP\n"+LibreApplication.nettyActiveWriteDevices.get("active"));
//                    handler.sendEmptyMessageDelayed(Constants.CHECK_ALIVE, Constants.CHECK_ALIVE_TIMEOUT);
                } else {
                    LibreLogger.d(TAG, "Amit + NettyClient handler is  Not Started " + mMessageBox + " :: Value :: " + getCommandType(sbytes));
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                LibreLogger.d(TAG, "InterruptedException happened in Writing the Data, Dont Know What is the Issue ");
            }
            catch (Exception e) {
                e.printStackTrace();
                LibreLogger.d(TAG, "Exception happened in Writing the Data, Device Got Rebooted \n"+remotedevice+"netty active write devices\n"+LibreApplication.nettyActiveWriteDevices.toString());

                //friday suma changes
//                if (LUCIControl.luciSocketMap.containsKey(remotedevice)) {
//                        LUCIControl.luciSocketMap.remove(remotedevice);
//                        LibreApplication.securecertExchangeSucessDevices.clear();
//                    }
            }

        }

    }
    /* For Getting The Message Box From the command */
    /* KK  :: Commenting because Android is following the approach of Little indian . So if we are try to get command from
     * LuciPacket we are getting the wrong value , so we are initiating the timer for all the Commands  but still it will works because
     * NOTIFY packets will update the last Notifed Time. And Our App will not work for 16Bit thats MsgBox greater than 255
     * So I remodified the Code  to support 16 Bit
     *
     * NettyDecoder .java , We dont need to change the Function because , Devec always follow the apporach of Big Indian
     * And Speakers are having inbuilt support to auto conversion of Little Indian To Big Indian */
    /*public int getMessageBoxofTheCommand(byte[] packetbytes) {
        return packetbytes[3] * 16 + (packetbytes[4] & 0xFF);
    }*/

    /* KK:: Remodified Code For Getting The Message Box From the command */
    public int getMessageBoxofTheCommand(byte[] packetbytes) {
        return packetbytes[3] & 0xFF + ((packetbytes[4] & 0xFF) << 8);
    }


    /* For Getting The Message Box From the command */
    public int getCommandType(byte[] packetbytes) {
        return packetbytes[2];
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        LibreLogger.d(TAG, "Channel is broken " + remotedevice + "id as " + ctx.channel().id());

        if (handler.hasMessages(Constants.CHECK_ALIVE))
            handler.removeMessages(Constants.CHECK_ALIVE);
        /**
         * LatestDiscoveryChanges
         */
//                ctx.close();

        if (LUCIControl.luciSocketMap.containsKey(remotedevice)) {
            if (isSocketToBeRemovedFromTheTCPMap(ctx, remotedevice)) {
                //BusProvider.getInstance().post(new RemovedLibreDevice(remotedevice));
                LUCIControl.luciSocketMap.remove(remotedevice);
                LibreApplication.securecertExchangeSucessDevices.clear();

            }
        }

//    for now not removing this commment untill final qa test report on multidevice support submitted

//    if (LUCIControl.luciSocketMap.containsKey(remotedevice)) {
//            if (isSocketToBeRemovedFromTheTCPMap(ctx, remotedevice)) {
//                LUCIControl.luciSocketMap.remove(remotedevice);
//
//                LibreApplication.securecertExchangeSucessDevices.clear();
//            }
//        }

        try {
            ctx.close();
        } catch (Exception e) {
            e.printStackTrace();
            LibreLogger.d(TAG, " Channel Is Broken And Closing Exception Happend in Writing the Data, Dont Know What is the Issue " + remotedevice);
        }
    }

    private boolean isSocketToBeRemovedFromTheTCPMap(ChannelHandlerContext ctx, String mIpAddress) {
        NettyAndroidClient mAndroidClient = LUCIControl.luciSocketMap.get(remotedevice);
        /* If Hashmap is returning Null that means Hashmap is Empty for the that particular AP , So we can return it as False */
        if (mAndroidClient == null || mAndroidClient.handler == null || mAndroidClient.handler.mChannelContext == null)
            return false;

        if (ctx != null && ctx.channel().id() == mAndroidClient.handler.mChannelContext.channel().id()) {
            return true;
        } else {
            LibreLogger.d(TAG, "BROKEN" + remotedevice
                    + "id DID NOT MATCH " + ctx.channel().id() + " "
                    + LUCIControl.luciSocketMap.get(remotedevice).handler.mChannelContext.channel().id());
            return false;
        }
    }


}
