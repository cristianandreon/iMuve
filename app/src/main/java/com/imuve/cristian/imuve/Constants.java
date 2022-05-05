package com.imuve.cristian.imuve;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.concurrent.Semaphore;



final public class Constants {

    public static String APP_VERSION = "1.8";
    public static float CPP_VERSION = 0.0f;
    public static String TABLE_VERSION = "1.2";


    // Controllo Cifratura Invio / Recezione
    public static boolean ENCRYPT = false;
    public static boolean DECRYPT = false;

    // Variabili Debug applicazione
    public static boolean DEBUG = false;
    public static boolean BUTTONS_AS_DEBUG = false;
    public static boolean ADD_VANI_AS_DEBUG = false;
    public static boolean ADD_OGGETTI_AS_DEBUG = false;
    public static boolean ADD_CUSTOM_FIELD_AS_DEBUG = false;



    // Semaforo disegno OpenGL
    public static boolean DRAW_BUSY = false;
    public static Semaphore sDRAW_BUSY = new Semaphore(0);


    public static String KEY = "00000000000000000000000000000000";
    public static byte[] KEY_BYTES = null;
    public static byte[] IV_BYTES = null;

    public static int ScreenWX = 1200;
    public static int ScreenWY = 660;

    public static String DEVICE_ID;
    public static String DEVICE_NAME;
    public static String ANDROID_VERSION;

    public static String SERVER_PROTOCOL = null;
    public static String SERVER_URL = null;
    public static Integer SERVER_PORT = 0;



    public static int DEFAULT_LV_OPEN_ROW_HEIGHT = 240;
    public static int DEFAULT_LV_CLOSED_ROW_HEIGHT = 60;


    public static boolean FIRST_TIME_EXEC = false;

    public static Integer VersionOnSharedPref = null;


    public static void reset_server_addr( boolean bTest) {
        SERVER_PROTOCOL = "http://";
        if (bTest) {
            SERVER_URL = "pamap.geisoft.org/services/pamap";
            SERVER_PORT = 0;
        } else {
            SERVER_URL = "dkmap.888sp.com/services/pamap";
            SERVER_PORT = 0;
        }
    }


    //private constructor to prevent instantiation/inheritance
    public static void InitConstants( Context context ) {

        Constants.KEY = "e8ffc7e56311679f12b6fc91aa77a5eb";

        try {
            Constants.KEY_BYTES = Constants.KEY.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Constants.IV_BYTES = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        /*
        Display display = WindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        ScreenWX = size.x;
        ScreenWY = size.y;
        */


        ////////////////////////////////////////////////
        // Metodo TelephonyManager.getDeviceId()
        //
        TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            DEVICE_ID = tManager!=null?tManager.getDeviceId():null;
        } catch (Exception e) {
            DEVICE_ID = null;
        }


        ////////////////////////////////////////////////
        // Metodo android.os.Build.SERIAL
        //
        try {
            if (DEVICE_ID == null || DEVICE_ID.equalsIgnoreCase("unknown") || DEVICE_ID.isEmpty()) {
                DEVICE_ID = android.os.Build.SERIAL;
            }
        } catch (Exception e) {
        }


        ////////////////////////////////////////////////
        // Metodo Settings.Secure.ANDROID_ID
        //
        try {
            if (DEVICE_ID == null || DEVICE_ID.equalsIgnoreCase("unknown") || DEVICE_ID.isEmpty()) {
                DEVICE_ID = Settings.Secure.ANDROID_ID;
                if (DEVICE_ID != null) {
                    if (DEVICE_ID.equalsIgnoreCase("android_id")) {
                        DEVICE_ID = null;
                    }
                }
            }
        } catch (Exception e) {
        }




        ////////////////////////////////////////////////
        // Metodo ro.serialno
        //
        if (DEVICE_ID == null || DEVICE_ID.equalsIgnoreCase("unknown") || DEVICE_ID.isEmpty()) {
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class, String.class);
                DEVICE_ID = (String)(get.invoke(c, "ro.serialno", "unknown"));
            } catch (Exception e) {
            }
        }



        // DEBUG
        // DEVICE_ID = null;

        try {
            if (DEVICE_ID == null || DEVICE_ID.equalsIgnoreCase("unknown") || DEVICE_ID.isEmpty()) {
                // if (DialogBox.DialogBox("ATTENZIONE", "Impossibile identificare il dispositovo.\r\nRivolgersi al servizio tecnico", 0 + 0, context)) {}
            }
        } catch (Exception e) {
        }





        DEVICE_NAME = android.os.Build.MODEL + "." + Build.ID + "." + Build.PRODUCT;


        ANDROID_VERSION = "" + Build.VERSION.RELEASE + "-" +Build.VERSION.CODENAME + " SDK " + Build.VERSION.SDK_INT;




        // Coordinate di default (poi lette sa sqlite)
        // reset_server_addr(false);






        ///////////////////////////////////////
        // Caricamento preferenze Persistenti
        //

        APPPRefs.load_app_prefs(context);

        APPPRefs.load_shared_app_prefs(context);



        if (Constants.VersionOnSharedPref == 0) {
            Constants.FIRST_TIME_EXEC = true;
            Constants.VersionOnSharedPref = 1;
        }

        APPPRefs.save_shared_app_prefs(context);
    }



    public static boolean getDrawAccess(int msec) {
        int cmsec = 0;
        while (Constants.DRAW_BUSY) {
            try {
                Thread.sleep(10);
                cmsec += 10;
                if (cmsec >= msec)
                    return false;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    return true;
    }

    public static String getVersion() {
        return APP_VERSION + "-" + String.valueOf(CPP_VERSION) + "-" + TABLE_VERSION + "-"+(FIRST_TIME_EXEC ? "0" : "1") + "";
    }

}


final class APPPRefs {

    static Context mContext = null;

    public static void load_app_prefs(Context context) {
        try {
            mContext = context;
            SharedPreferences prefs = context.getSharedPreferences("iMuve", Context.MODE_PRIVATE);
            // Constants.SERVER_PROTOCOL = prefs.getString("SERVER_PROTOCOL", Constants.SERVER_PROTOCOL);
            // Constants.SERVER_URL = prefs.getString("SERVER_URL", Constants.SERVER_URL);
            // Constants.SERVER_PORT = prefs.getInt("SERVER_PORT", Constants.SERVER_PORT);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void save_app_prefs(Context context) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences("iMuve", Context.MODE_PRIVATE).edit();
            // editor.putString("SERVER_PROTOCOL", Constants.SERVER_PROTOCOL);
            // editor.putString("SERVER_URL", Constants.SERVER_URL);
            // editor.putInt("SERVER_PORT", Constants.SERVER_PORT);
            // editor.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    // N.B.: NON Persiste sopra la disintallazione
    public static void load_shared_app_prefs(Context context) {
        try {
            if (context == null) context = mContext;
            context.createPackageContext("com.imuve.cristian.imuve", Context.MODE_WORLD_WRITEABLE);
            SharedPreferences sharedPreference = mContext.getSharedPreferences("Version", Context.MODE_WORLD_READABLE);
            Constants.VersionOnSharedPref = sharedPreference.getInt("VersionValue", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void save_shared_app_prefs(Context context) {
        try {
            if (context == null) context = mContext;
            context.createPackageContext("com.imuve.cristian.imuve", Context.MODE_WORLD_WRITEABLE);
            SharedPreferences sharedPreference = mContext.getSharedPreferences("Version", Context.MODE_WORLD_READABLE);
            SharedPreferences.Editor editor = sharedPreference.edit();
            editor.putInt("VersionValue", Constants.VersionOnSharedPref);
            editor.commit();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}