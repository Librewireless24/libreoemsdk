package com.libreAlexa.util;


import android.util.Log;

public class LibreLogger {

  public static Boolean isLogEnable = true; //Make it false while releasing it to customer
  //  Change done by Praveen for accoumdadting logs which are exceeding the length
  public static void d(String TAG, String sb) {
    if (isLogEnable) {
      /*
       * Using the object we can't see class name and we can see TAG as String
       *  //String TAG = object.getClass().getSimpleName();
       */
      /* Change done by Praveen for accoumdadting logs which are exceeding the length */
      if (sb.length() > 4000) {
        Log.d(TAG, "Logger Message.length = " + sb.length());
        int chunkCount = sb.length() / 4000;     // integer division
        for (int i = 0; i <= chunkCount; i++) {
          int max = 4000 * (i + 1);
          //Log.d(TAG, "--Logger Logger chunkCount = " + chunkCount);
          if (max >= sb.length()) {
            // Log.d(TAG, "--Logger if");
            Log.e(TAG, "chunk " + i + " of " + chunkCount + ":" + sb.substring(4000 * i));
          } else {
           // Log.d(TAG, "--Logger ELSE");
            Log.e(TAG, "chunk " + i + " of " + chunkCount + ":" + sb.substring(4000 * i, max));
          }
        }
      } else {
        Log.e(TAG, sb);
      }
    }
  }
/*  public static void d (String TAG, String sb){
         if(!isLogEnable) {
             Log.d(TAG, sb);
         }
 }*/
}