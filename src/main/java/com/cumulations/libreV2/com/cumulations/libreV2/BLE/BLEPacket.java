package com.cumulations.libreV2.com.cumulations.libreV2.BLE;


import static com.cumulations.libreV2.activity.CTBluetoothPassCredentials.TAG_BLE_SHAIk;

import android.util.Log;
import com.libreAlexa.util.LibreLogger;

/**
 * This class represents the message being exchanged between device and the application
 *
 * 0xAB| ID | Len | Data | 0xCD |
 * 	 1B   1B    2B    ~~     1B
 *
 *    Byte[0]                   : 0xAB     -> Packet Start Delimeter
 *    Byte[1]                   : ID       -> Based on the above enumerations
 *    Byte[2] Byte[3]           : Len      -> Length of the DATA
 *    Byte[4] to Byte[len+3]    :          -> Data
 *    Byte[len+4]               : 0xCD     -> Packet End Delimeter
 *
 *
 */
public class BLEPacket {

    static int BLE_HEADER_SIZE = 4;
    public byte START_DELIMITER = (byte)0xAB;
    public byte END_DELIMITER = (byte) 0xCD;

    public byte Command = 0;
    public byte[] payload;

    public class BLEDataPacket {
        private byte command;
        private short dataLength;
        private byte[] message;
        private byte[] mCompleteMessage;

        public byte[] getcompleteMessage() {
            return  mCompleteMessage;
        }

        public int getCommand() {
            return command;
        }

        public void setCommand(byte command) {
            this.command = command;
        }

        public short getDataLength() {
            return dataLength;
        }

        public void setDataLength(short dataLength) {
            this.dataLength = dataLength;
        }

        public byte[] getMessage() {
            return message;
        }

        public void setMessage(byte[] message) {
            this.message = message;
        }

        public void setmCompleteMessage(byte[] message) {
            mCompleteMessage = message;
        }
        public BLEDataPacket(byte command, short dataLength, byte[] message){
            this.command = command;
            this.dataLength = dataLength;
            this.message = message;
        }
    }

    public BLEPacket(){

    }
    public BLEPacket(byte[] message,byte Command ,boolean lengthToAdd) {
        payload = new byte[BLE_HEADER_SIZE + message.length+1];
        payload[0] = START_DELIMITER;
        payload[1] = Command;
        if(lengthToAdd) {
            payload[2] = (byte) (message.length & 0x00FF);
            payload[3] = (byte) ((message.length & 0xFF00) >> 8);
        }
        if (message.length > 0) {
            for (int i = 0; i < message.length; i++) {
                payload[BLE_HEADER_SIZE + i] = message[i];
            }
        }
        payload[BLE_HEADER_SIZE + message.length] = END_DELIMITER;

    }
    public BLEPacket(byte[] message,byte Command) {
        payload = new byte[BLE_HEADER_SIZE + message.length+1];
        payload[0] = START_DELIMITER;
        payload[1] = Command;
        payload[2] = (byte) (message.length & 0x00FF);
        payload[3] = (byte) ((message.length & 0xFF00) >> 8);
        if (message.length > 0) {
            for (int i = 0; i < message.length; i++) {
                payload[BLE_HEADER_SIZE + i] = message[i];
            }
        }
        payload[BLE_HEADER_SIZE + message.length] = END_DELIMITER;

    }

    public byte[] getpayload() {
        return payload;
    }


    public int getCommand() {
        return Command;
    }

    public BLEDataPacket createBlePacketFromMessage(byte[] message){
        LibreLogger.d(TAG_BLE_SHAIk,"Received BLE data dataPacket data $"+message);
        BLEDataPacket mDataPacket = new BLEDataPacket((byte)0,(short)0,null);
        mDataPacket.setmCompleteMessage(message);
        LibreLogger.d(TAG_BLE_SHAIk,"Received BLE data dataPacket data $"+mDataPacket.message);
        if((byte)(message[0] & 0xFF )== START_DELIMITER) {
            mDataPacket.command = (byte)( message[1] & 0xFF);
            //Shaik Old code receiving the 23 bytes of data
           // mDataPacket.dataLength = (short)(message[3] * 16 + message[2]& 0xFF);
            //Shaik new Code receiving the large amount of data tested in with FW with Sampath
            mDataPacket.dataLength = (short)(((message[3]&0xFF) << 8) | message[2]& 0xFF);
            mDataPacket.message = new byte[mDataPacket.dataLength];
            for(int i =0 ;i < mDataPacket.dataLength ;i++) {
                if(message[i+4] == END_DELIMITER) {
                    break;
                }
               // LibreLogger.d("karunakaran" , ""+ message[i+4]);
                mDataPacket.message[i] = message[i+4];
            }
        }
        return mDataPacket;
    }

    private static final byte HEADER1 = (byte) 0xAB;
    private static final byte HEADER2 = (byte) 0xCD;
    private static final byte FOOTER1 = (byte) 0xCD;
    private static final byte FOOTER2 = (byte) 0xAB;

   /* public static String parseBleData(BLEPacket.BLEDataPacket dataPacket) {
        LibreLogger.d(TAG_BLE_SHAIk,"Received BLE data parseBleData dataPacket $"+dataPacket);

        // Assuming getmCompleteMessage returns a byte array
        byte[] data = dataPacket.mCompleteMessage;
        LibreLogger.d(TAG_BLE_SHAIk,"Received BLE datadata parseBleData Packet data $"+data);
        if (isHeaderFooterPresent(data)) {
            // Extract the event, length, and data bytes
            byte event = data[2];
            short length = (short) ((data[4] << 8) | (data[3] & 0xFF));
            byte[] packetData = new byte[length];

            // Check if there is enough data to contain the entire packet
            if (data.length >= length + 6) {
                // Copy the data bytes
                System.arraycopy(data, 5, packetData, 0, length);

                // Interpret the data byte
                return interpretDataByte(packetData[0]);
            }
        }

        // Header or footer not found
        return null;
    }


    private static boolean isHeaderFooterPresent(byte[] data) {
        return data.length >= 6 &&
            data[0] == HEADER1 && data[1] == HEADER2 &&
            data[data.length - 1] == FOOTER1 && data[data.length - 2] == FOOTER2;
    }*/

    private static String interpretDataByte(byte dataByte) {
        switch (dataByte) {
            case 0x00:
                // Connection Fail
                return "Connection Fail";
            case 0x01:
                // Wrong Password
                return "Wrong Password";
            case 0x02:
                // Network Not Found
                return "Network Not Found";
            // Add more cases as needed

            default:
                // Unknown data byte
                return "Unknown Data";
        }
    }




    public static String parseBleData(byte[] data) {
        // Convert the byte array to a hex string
        String hexString = byteArrayToHexString(data);

        // Check if the header and footer are present
        if (isHeaderFooterPresent(hexString)) {
            // Extract data
            String extractedData = extractData(hexString);

            // extractedData now contains the parsed information
            return extractedData;
        }

        // Header or footer not found
        return null;
    }

    private static boolean isHeaderFooterPresent(String hexString) {
        // Check if the hex string contains the header and footer
        return hexString.startsWith("ab") && hexString.endsWith("cd");
    }

    private static String extractData(String hexString) {
        // Extract the data portion from the hex string
        return hexString.substring(4, hexString.length() - 2);
    }

    private static String byteArrayToHexString(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder(data.length * 2);
        for (byte b : data) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }


}

