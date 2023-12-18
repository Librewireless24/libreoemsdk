package com.libreAlexa.netty;

/* Created by praveena on 7/16/15.*/

import android.util.Log;
import com.cumulations.libreV2.tcp_tunneling.TunnelingClientRunnable;
import com.libreAlexa.util.LibreLogger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class NettyDecoder extends ByteToMessageDecoder { // (1)


    private static final String TAG = NettyDecoder.class.getSimpleName();
    byte[] globalBytesRead =new byte[0];


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) { // (2)

        synchronized (out) {

            int numOfBytesReadInThisIteration = in.readableBytes();
            byte[] bytesReadInThisIteration = new byte[numOfBytesReadInThisIteration];
            in.readBytes(bytesReadInThisIteration);
            globalBytesRead = addToGlobalArray(globalBytesRead, bytesReadInThisIteration);


        /* First check ff the read bytes is atleast the size of header*/
            if (globalBytesRead.length < 10) {

                LibreLogger.d(TAG,"NettyDecoder Header is not present yet");
                return; // (3)
            }
            else if (getMessageBoxofTheCommand(globalBytesRead)<0||getMessageBoxofTheCommand(globalBytesRead)>1200 )
            {
                String tempExtrString=new String();
                for (int j=0; j<globalBytesRead.length; j++) {
                    tempExtrString+=""+ Integer.toHexString(globalBytesRead[j]);
                }
                LibreLogger.d(TAG,"NettyDecoder globalbytes are ignored as they constituted invalid stream " + tempExtrString);
                LibreLogger.d(TAG,"NettyDecoder globalbytes are ignored had messagebox as " + getMessageBoxofTheCommand(globalBytesRead));
                globalBytesRead=new byte[0];
                return;
            }


        /* Now you have the header completly, check the data length and contsize till you get*/
            else {

                int lengthOfThePacketWeAreLookingFor = getPacketLength(globalBytesRead);

                if (globalBytesRead.length < lengthOfThePacketWeAreLookingFor) {
                    LibreLogger.d(TAG,"NettyDecoder We were looking for packet of size " + lengthOfThePacketWeAreLookingFor + " what we have recieved till now is " + globalBytesRead.length);
                    return;
                } else {
                    LibreLogger.d(TAG,"NettyDecoder We were looking for packet of size " + lengthOfThePacketWeAreLookingFor + " what we have recieved till now is " + globalBytesRead.length);
                    if (lengthOfThePacketWeAreLookingFor == globalBytesRead.length) {
                    /* No extra bytes read and hence everything is fine */
                        LibreLogger.d(TAG,"NettyDecoder We wrote messagebox " + getMessageBoxofTheCommand(globalBytesRead));
                        String tempExtrString=new String();
                        for (int j=10; j<globalBytesRead.length; j++) {
                            tempExtrString+=""+ Integer.toHexString(globalBytesRead[j]);
                        }
                        LibreLogger.d(TAG,"NettyDecoder Extracted data was1" + new String(globalBytesRead));

                        out.add(globalBytesRead);
                        globalBytesRead = new byte[0];
                        LibreLogger.d(TAG,"NettyDecoder Reintialized the globalbytes"+ tempExtrString);

                    } else {
                        LibreLogger.d(TAG,"NettyDecoder clearly the Data we recieved is more than one Luci packet");

                    /* takeout the extra bytes separately */
                        byte[] extraBytes = new byte[globalBytesRead.length - lengthOfThePacketWeAreLookingFor];
                        for (int i = lengthOfThePacketWeAreLookingFor; i < globalBytesRead.length; i++) {
                            extraBytes[i - lengthOfThePacketWeAreLookingFor] = globalBytesRead[i];

                        }


                    /* write the global bytes till datalength */
                        byte[] newGlobalArry = new byte[lengthOfThePacketWeAreLookingFor];
                        for (int i = 0; i < lengthOfThePacketWeAreLookingFor; i++) {
                            newGlobalArry[i] = globalBytesRead[i];
                        }

                        out.add(newGlobalArry);

                        LibreLogger.d(TAG,"NettyDecoder Extracted only " + newGlobalArry.length + " as we were looking for packet size of  " + lengthOfThePacketWeAreLookingFor + " whose message box is" + getMessageBoxofTheCommand(newGlobalArry));
                        String globalString=new String();
                        for (int j=10; j<newGlobalArry.length; j++) {
                            globalString+=""+ Integer.toHexString(newGlobalArry[j]);
                        }
                        LibreLogger.d(TAG,"NettyDecoder Extracted data is" + new String(newGlobalArry));

                        globalBytesRead = extraBytes;
                        if (globalBytesRead.length >= 10) {

                            LibreLogger.d(TAG,"NettyDecoder Now we are processing extrabytes with total length" + extraBytes.length + " while packet length from header is " + getPacketLength(extraBytes));
                            processExtraBytes(out);

                        }


                    }


                }
            }

        }
    }

    private void processExtraBytes(List<Object> out) {

         /* First check ff the read bytes is atleast the size of header*/
        if (globalBytesRead.length < 10) {

            LibreLogger.d(TAG,"NettyDecoder_extra Header is not present yet");
            return; // (3)
        }
        else if (getMessageBoxofTheCommand(globalBytesRead)<0||getMessageBoxofTheCommand(globalBytesRead)>1200 )
        {
            String tempExtrString=new String();
            for (int j=0; j<globalBytesRead.length; j++) {
                tempExtrString+=""+ Integer.toHexString(globalBytesRead[j]);
            }
            LibreLogger.d(TAG,"NettyDecoder_extra globalbytes are ignored as they constituted invalid stream " + tempExtrString);
            LibreLogger.d(TAG,"NettyDecoder_extra globalbytes are ignored had messagebox as " + getMessageBoxofTheCommand(globalBytesRead));
            globalBytesRead=new byte[0];
            return;
        }


        /* Now you have the header completly, check the data length and contsize till you get*/
        else {

            int lengthOfThePacketWeAreLookingFor = getPacketLength(globalBytesRead);

            if (globalBytesRead.length < lengthOfThePacketWeAreLookingFor) {
                LibreLogger.d(TAG,"NettyDecoder_extra We were looking for packet of size " + lengthOfThePacketWeAreLookingFor + " what we have recieved till now is " + globalBytesRead.length);
                return;
            } else {
                LibreLogger.d(TAG,"NettyDecoder_extra We were looking for packet of size " + lengthOfThePacketWeAreLookingFor + " what we have recieved till now is " + globalBytesRead.length);
                if (lengthOfThePacketWeAreLookingFor == globalBytesRead.length) {
                    /* No extra bytes read and hence everything is fine */
                    LibreLogger.d(TAG,"NettyDecoder_extra We wrote messagebox " + getMessageBoxofTheCommand(globalBytesRead));
                    String tempExtrString=new String();
                    for (int j=10; j<globalBytesRead.length; j++) {
                        tempExtrString+=""+ Integer.toHexString(globalBytesRead[j]);
                    }
                    LibreLogger.d(TAG,"NettyDecoder_extra Extracted data was2" + new String(globalBytesRead));

                    out.add(globalBytesRead);
                    globalBytesRead = new byte[0];
                    LibreLogger.d(TAG,"NettyDecoder_extra Reintialized the globalbytes");

                } else {
                    LibreLogger.d(TAG,"NettyDecoder_extra clearly the Data we recieved is more than one Luci packet");

                    /* takeout the extra bytes separately */
                    byte[] extraBytes = new byte[globalBytesRead.length - lengthOfThePacketWeAreLookingFor];
                    for (int i = lengthOfThePacketWeAreLookingFor; i < globalBytesRead.length; i++) {
                        extraBytes[i - lengthOfThePacketWeAreLookingFor] = globalBytesRead[i];

                    }


                    /* write the global bytes till datalength */
                    byte[] newGlobalArry = new byte[lengthOfThePacketWeAreLookingFor];
                    for (int i = 0; i < lengthOfThePacketWeAreLookingFor; i++) {
                        newGlobalArry[i] = globalBytesRead[i];
                    }

                    out.add(newGlobalArry);


                    LibreLogger.d(TAG,"NettyDecoder_extra Extracted only " + newGlobalArry.length + " as we were looking for packet size of  " + lengthOfThePacketWeAreLookingFor + " whose message box is" + getMessageBoxofTheCommand(newGlobalArry));
                    String globalString=new String();
                    for (int j=10; j<newGlobalArry.length; j++) {
                        globalString+=""+ Integer.toHexString(newGlobalArry[j]);
                    }
                    LibreLogger.d(TAG,"NettyDecoder_extraExtracted data is" + new String(newGlobalArry));





                    globalBytesRead = extraBytes;
                    if (globalBytesRead.length >= 10) {

                        LibreLogger.d(TAG,"NettyDecoder_extra Now we are processing extrabytes with total length" + extraBytes.length + " while packet length from header is " + getPacketLength(extraBytes));
                        processExtraBytes(out);

                    }


                }


            }
        }

    }



    public int getPacketLength(byte[] packetbytes){

    int offset=0;
    int datalengthInPacket = (packetbytes[offset + 8] << 8) | (packetbytes[offset + 9] & 0xFF);
    int msgBox = packetbytes[3] * 16 + packetbytes[4];
    return  datalengthInPacket+10;

}

    public byte[] addToGlobalArray(byte[] one,byte[] two){
        byte[] combined = new byte[one.length + two.length];

        for (int i = 0; i < combined.length; ++i)
        {
            combined[i] = i < one.length ? one[i] : two[i - one.length];
        }

        return combined;
    }

    public int getDatalengthForExtra(byte[] packetbytes){


        int offset=0;
        int datalengthInPacket = (packetbytes[offset + 8] << 8) | (packetbytes[offset + 9] & 0xFF);

        int msgBox = packetbytes[3] * 16 + packetbytes[4];


        return  datalengthInPacket+10;

    }


    public int getMessageBoxofTheCommand(byte[] packetbytes) {
        return packetbytes[3] * 16 + (packetbytes[4] & 0xFF);
    }




    }