package com.libreAlexa.netty;

/*
 * Created by praveena on 7/10/15.
 */

import android.util.Base64;
import android.util.Log;

import com.libreAlexa.LibreApplication;
import com.libreAlexa.luci.LSSDPNodeDB;
import com.libreAlexa.luci.LUCIControl;
import com.libreAlexa.util.LibreLogger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class NettyAndroidClient {

    private static final String TAG = NettyAndroidClient.class.getSimpleName();
    NettyClientHandler handler;
    String remotehost;
    int port;
    long lastNotifiedTime;
    //isLuciSecure =true is SECURE DEVICE
    //isLuciSecure =false is NON SECURE DEVICE
    boolean isLuciSecure=true;
    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {

        this.creationTime = creationTime;
    }

    long creationTime;

    private static NettyAndroidClient dummyCLient = new NettyAndroidClient();
    /*need not create every time for all clients*/
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();


    private NettyAndroidClient() {

    }

    public static NettyAndroidClient getDummyInstance() {
        dummyCLient.setRemotehost("0.0.0.0");
        dummyCLient.creationTime = System.currentTimeMillis();
        return dummyCLient;
    }

    public NettyAndroidClient(InetAddress host, int port,Boolean secureValue) throws Exception {

        handler = new NettyClientHandler(host.getHostAddress());
        remotehost = host.getHostAddress();
        port = port;


        Bootstrap b = new Bootstrap(); // (1)

        b.group(workerGroup); // (2)
        b.channel(NioSocketChannel.class); // (3)

        b.option(ChannelOption.TCP_NODELAY, true);
        b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                LibreLogger.d(TAG,"suma in luci SECURE Value\n**"+secureValue);
/* SUMA : If Secure Luci is supported in respective device make isLUCISecure value to true else false*/

                if(secureValue) {
                    // /*SUMA */Set up key manager factory to use our key store and DERRIVED FROM .P12
                      LibreLogger.d(TAG,"suma in luci SECURE**");
                    KeyStore keyStore = null;
                    try {
                        keyStore = KeyStore.getInstance("PKCS12");
                        LibreLogger.d(TAG, "suma in getting cert from .p12 NETTY CLIENT on Init 1 ***\n" + LibreApplication.openRawandroidp12);

                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    }
                    try {
                        LibreLogger.d(TAG, "suma in getting cert from .p12 NETTY CLIENT on Init before try ***\n" + keyStore);

                        try {
                            LibreLogger.d(TAG, "suma in getting cert from .p12 NETTY CLIENT on Init before 2nd try\n" + keyStore);

                            if (keyStore != null) {
                                LibreLogger.d(TAG, "suma in getting cert from .p12 NETTY CLIENT on Init keystore not NULL\n" + keyStore);
                                keyStore.load(LibreApplication.openRawandroidp12, "12345678".toCharArray());
                                LibreLogger.d(TAG, "suma in getting cert from .p12 NETTY CLIENT on Init 2 ***\n" + keyStore);

                            }
                        } catch (NoSuchAlgorithmException | CertificateException e) {
                            e.printStackTrace();
                        }
                        Enumeration<String> aliases = null;
                        try {
                            if (keyStore != null) {
                                aliases = keyStore.aliases();
                            }
                            LibreLogger.d(TAG, "suma in getting cert from .p12 NETTY CLIENT on Init 3 ***\n" + aliases);

                        } catch (KeyStoreException e) {
                            e.printStackTrace();
                        }
                        if (aliases != null) {
                            while (aliases.hasMoreElements()) {
                                String alias = aliases.nextElement();
                                try {
                                    if (keyStore.getCertificate(alias).getType().equals("X.509")) {
                                        LibreLogger.d(TAG, "suma in getting cert from .p12 NETTY CLIENT on Init 4***\n" + aliases);

                                        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
                                        LibreLogger.d(TAG, "suma in getting cert .p12 NETTY CLIENT***\n" + cert + "ISSUER IP\n" + remotehost);
                                       // LUCIControl.luciSocketMap.put(remotehost, dummyCLient);
                                       LibreApplication.securecertExchangeSucessDevices.put("cert",remotehost);

                                       LUCIControl.secureCertDevices.put("certip",remotehost);

                                        if (new Date().after(cert.getNotAfter())) {
                                            return;
                                        }
                                    }
                                } catch (KeyStoreException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (IOException ioe) {
                        // This occurs when there is an incorrect password for the certificate
                        return;
                    }

                    // /*SUMA */Set up key manager factory to use our key store and DERRIVED FROM .P12
                    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                    kmf.init(keyStore, "12345678".toCharArray());
                    TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    KeyStore tmpKS = null;
                    tmFactory.init(tmpKS);
                    KeyManager[] km = kmf.getKeyManagers();
                    TrustManager[] tm = tmFactory.getTrustManagers();
                    LibreLogger.d(TAG, "suma in getting cert from .p12 NETTY CLIENT on Init 7***\n");

                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(km, trustAllCerts.get(), null);
                    SSLEngine sslEngine = sslContext.createSSLEngine();
                    sslEngine.setUseClientMode(true);
                    sslEngine.setNeedClientAuth(true);

                    LibreLogger.d(TAG, "suma in getting cert from .p12 NETTY CLIENT on Init 8***\n");
                    SslHandler sslHandler = new SslHandler(sslEngine);
                    sslHandler.engine().setEnabledProtocols(new String[]{"TLSv1.2"});
                    ch.pipeline().addFirst(sslHandler);
//                    LibreLogger.d(TAG, "suma in getting cert from .p12 NETTY CLIENT on Init 12***\n"

                    LibreLogger.d(TAG, "suma in getting cert from .p12 NETTY CLIENT on Init 12***\n"+LibreApplication.securecertExchangeSucessDevices.get("cert"));
                    ch.pipeline().addLast("IdleChecker", new IdleStateHandler(10, 10, 10));
                    ch.pipeline().addLast("IdleDisconnecter", new HeartbeatHandler(remotehost));
                    ch.pipeline().addLast(new NettyDecoder(), handler);

                }
                else {
                    LibreLogger.d(TAG, "suma in luci NonSecure**");
                    ch.pipeline().addLast("IdleChecker", new IdleStateHandler(10, 10, 10));
                    ch.pipeline().addLast("IdleDisconnecter", new HeartbeatHandler(remotehost));
                    ch.pipeline().addLast(new NettyDecoder(), handler);
                }
            }
        });

        // Start the client.
        ChannelFuture f = b.connect(host, port).sync();
        setCreationTime(System.currentTimeMillis());
        /*f.addListener(new ChannelFutureListener() {
                          @Override
                          public void operationComplete(ChannelFuture channelFuture) throws Exception {

                              if (channelFuture.isSuccess()) {
                                  LibreLogger.d(TAG, "Channel success for " + channelFuture.channel().remoteAddress());

                              } else {
                                  LibreLogger.d(this, "CHANNEL FAILED for " + channelFuture.channel().remoteAddress());

                              }
                          }
                      }
        );*/

        /*    ChannelFuture f = b.connect(host, port).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()){
                        LibreLogger.d(TAG,TAG,"Creation of Socket Successful"+ channelFuture.channel().id()+":"+
                                remotehost);
                        socketCreated = true;
                    }else{
                        LibreLogger.d(TAG,"Creation of Socket UnSuccessful");
                        socketCreated = false;
                    }
                }
            }).await();*/
        LibreLogger.d(TAG,"Scan_Netty Created a Netty socket to listen ");


    }

    public NettyClientHandler getHandler(){
        return handler;
    }
    public String getRemotehost() {
        return remotehost;
    }

    public void setRemotehost(String remotehost) {
        this.remotehost = remotehost;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void write(byte[] bytes) {
        if (handler != null) {
            handler.write(bytes);

        }
    }


    public long getLastNotifiedTime() {
        return lastNotifiedTime;
    }

    public void setLastNotifiedTime(long notifiedTime) {
        lastNotifiedTime = notifiedTime;
    }


    /**
     * LatestDiscoveryChanges
     * this method will close existing NettyAndroidClient
     */
    public void closeSocket() {
        if (handler != null && handler.mChannelContext != null)
            handler.mChannelContext.close();
    }

    private final ThreadLocal<TrustManager[]> trustAllCerts = new ThreadLocal<TrustManager[]>() {
        @Override
        protected TrustManager[] initialValue() {
            return new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }

                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                    LibreLogger.d(TAG, "suma in netty client trusted certi");
                    // convertToX509Cert(LibreApplication.getMYPEMstring);
                    LibreLogger.d(TAG,"suma in getting cert from .p12 NETTY CLIENT on Init 9***\n");

                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    LibreLogger.d(TAG, "suma in netty server trusted certi");
                    LibreLogger.d(TAG,"suma in getting cert from .p12 NETTY CLIENT on Init 10***\n");
                  //  convertToX509Cert(LibreApplication.getMYPEMstring);

                }
            }
            };
        }
    };

    public static void convertToX509Cert(String certificateString) throws CertificateException {
        X509Certificate certificate = null;
        CertificateFactory cf = null;

        try {
            if (certificateString != null); // NEED FOR PEM FORMAT CERT STRING
            byte[] certificateData= Base64.decode(certificateString, Base64.DEFAULT);

            cf = CertificateFactory.getInstance("X509");
            certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificateData));
          //  LibreLogger.d(TAG,"suma in generate "+certificate);
        } catch (CertificateException e) {
            throw new CertificateException(e);
        }
    }

}
