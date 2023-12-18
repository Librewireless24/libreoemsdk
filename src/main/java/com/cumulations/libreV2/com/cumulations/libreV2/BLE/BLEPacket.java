package com.cumulations.libreV2.com.cumulations.libreV2.BLE;

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
        BLEDataPacket mDataPacket = new BLEDataPacket((byte)0,(short)0,null);
        mDataPacket.setmCompleteMessage(message);
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

    /**
     * Device End Implementation - https://jira-librewireless.atlassian.net/browse/LCB2-885 (Hex) -
     * Device Message - App Message 0x00->Connection Fail -> Something went wrong 0x01->Wrong Password
     * -> Incorrect Password 0x02->Network Not Found -> AP not in range
     */


    public static int decodeBleData(String bleData) {
        int extractData = -1;

        try {
            String header = "ab";
            String footer = "cd";
            if (bleData.startsWith(header) && bleData.endsWith(footer)) {
                // Extract the last two characters before the footer
                String dataHex = bleData.substring(bleData.length() - 4, bleData.length() - 2);
                int dataDecimal = Integer.parseInt(dataHex, 16);

                switch (dataDecimal) {
                    case 0x00:
                        extractData = 0;
                        break;
                    case 0x01:
                        extractData = 1;
                        break;
                    case 0x02:
                        extractData = 2;
                        break;
                    default:
                        extractData = 3;
                        break;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return extractData;
    }
}


