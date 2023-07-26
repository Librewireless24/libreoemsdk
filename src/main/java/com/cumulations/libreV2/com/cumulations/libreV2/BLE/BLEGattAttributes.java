/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cumulations.libreV2.com.cumulations.libreV2.BLE;

import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class BLEGattAttributes {

    public static final int MTU_SIZE = 180;
    public static UUID RIVA_BLE_SERVICE = convertFromInteger(0xAAAA); //UUID.fromString("0000aaaa-0000-1000-8000-00805f9b34fb");
    public static UUID RIVA_BLE_CHARACTERISTICS = convertFromInteger(0x1111); //UUID.fromString("00001111-0000-1000-8000-00805f9b34fb");
    public static UUID RIVA_BLE_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

}
