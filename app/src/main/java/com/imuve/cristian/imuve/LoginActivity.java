package com.imuve.cristian.imuve;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class LoginActivity extends Activity {


    // Parametri di debug
    static final boolean BYPASS_LOGIN_AS_DEBUG = false;
    static final boolean CONTINUE_LOGIN_AS_DEBUG = false;
    static final boolean CONTINUE_LOGIN_IF_SERVER_FAIL = false;


    ProgressDialog progress = null;

    Object stick = new Object();

    JSONParser jsonParser = null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        this.setFinishOnTouchOutside(false);

        this.setDefaultKeyMode(DEFAULT_KEYS_DISABLE);


        try {

            ArrayList<String> Fields = new ArrayList<String>();
            Fields.add(APPData.USER_LOGIN);

            TextView tv = (TextView) findViewById(R.id.device_name);
            tv.setText(Constants.DEVICE_ID!=null?Constants.DEVICE_ID:"[Non Identificato]");

            tv = (TextView) findViewById(R.id.tvSN);
            tv.setText(Constants.DEVICE_NAME);

            tv = (TextView) findViewById(R.id.tvAndroidVersion);
            tv.setText(Constants.ANDROID_VERSION);

            tv = (TextView) findViewById(R.id.tvVersion);
            if (tv!=null) tv.setText("iMuve ver.: " + Constants.getVersion());


            ListView lv = (ListView) findViewById(R.id.lvUser);

            // SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, APPData.ObjectCustomFields, APPData.ObjectCustomLabels);
            SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, Fields, null, null, 0);

            lv.setAdapter(ListAdapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                    // String str=[position];
                    System.err.println("onItemClick:" + position);
                }
            });

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


        Button button = (Button) findViewById(R.id.btLogin);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onLogin();
            }
        });

        button = (Button) findViewById(R.id.btSettings);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSettings(v);
            }
        });


        View backgroundimage = findViewById(R.id.background);
        if(backgroundimage != null) {
            Drawable background = backgroundimage.getBackground();
            background.setAlpha(40);
        }


        // View lay = findViewById(R.id.glMain);;
        // lay.setElevation (5.0f);


        if (APPData.JUST_LOGGED_OFF) {
        } else {
            if (APPData.KEEP_LOGGED.compareToIgnoreCase("1") == 0) {
                onLogin();
            } else {
                if (    (APPData.LAST_ENV != null && !APPData.LAST_ENV.isEmpty()) ||
                        (APPData.LAST_ENV_ON_DWG != null && !APPData.LAST_ENV_ON_DWG.isEmpty()) ) {
                    this.getWindow().getDecorView().setAlpha(0.2f);
                    // Recovery from crash
                    onLogin();
                }
            }
        }
    }



    private void openSettings(View v) {
        try {
            Intent myIntent = new Intent(this, SettingsActivity.class);
            myIntent.putExtra("key", "value"); //Optional parameters
            startActivity(myIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void onLogin() {
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Autenticazione in corso...");
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        Thread mThread = new Thread() {
            @Override
            public void run() {
                jsonParser = new JSONParser(null);
                doLogin(jsonParser);
                progress.dismiss();
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onLoginDone(jsonParser);
                        onReadUserProfile(jsonParser);
                    }
                });
            }
        };

        mThread.start();
    }





    private boolean doLogin( JSONParser jsonParser ) {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        int res = 0;


        if (BYPASS_LOGIN_AS_DEBUG) {
            // Bypass the login
            APPData.LOGGED_IN = 1;
            APPData.COD_ESITO = "S";

            this.finish();

            return true;
        }


        try {


            retry_to_log:

            // int dialogBoxRes = new DialogBox().DialogBox("Title", "Message", 0, LoginActivity.this);


            ////////////////////////////
            // Login
            //
            APPData.LOGGED_IN = 0;
            APPData.COD_ESITO = "";





            // Nomi Utente :
            // // azienda ,  admin , muve
            /*
            # Servizio login a partire da un device
            # metodo : POST
            # path : /login-service/login-device
            # esempio chiamata
            http://pamap.geisoft.org/services/pamap/login-service/login-device?username=azienda&imei=AAAAAA-BB-CCCCCC-E&longitude=45.01&latitude=1234.66
            http://dkmap.888sp.com/services/pamap/login-service/login-device?username=azienda&imei=AAAAAA-BB-CCCCCC-E&longitude=45.01&latitude=1234.66
            # esempio risposta
            {"codEsito":"S","idsessione":"1a64f71a-28fb-4d72-9a66-69386c5c2f85"}

            es.:
            "http://dkmap.888sp.com/services/pamap/login-service/login-device"
                username=correr&imei=353316091790942
                ->
                "{"idsessione":null,"codEsito":"S"}"
            */

            String loginURL = APPData.getServiceURL("login-service/login-device", APPData.bEncript);
            String[] labelsParam = new String[]{"username", "imei", "longitude", "latitude"};
            String[] valuesParam = new String[]{APPData.USER_LOGIN, Constants.DEVICE_ID, "0.123", "0.123"};
            String[] resultTags = new String[]{"codEsito", "idsessione", "msgEsito"};


            boolean tryToConnect = true;




            ///////////////////////////
            // Recupero da crash
            //
            if (    (APPData.LAST_ENV != null && !APPData.LAST_ENV.isEmpty()) ||
                    (APPData.LAST_ENV_ON_DWG != null && !APPData.LAST_ENV_ON_DWG.isEmpty()) ) {
                // Login offline
                if (APPData.LAST_LOGIN_RESPONSE != null && !APPData.LAST_LOGIN_RESPONSE.isEmpty()) {
                    jsonParser.rawHttpStatus = 200;
                    res = jsonParser.ParseString(APPData.LAST_LOGIN_RESPONSE, resultTags, null, null);
                    if (res > 0) {
                        return true;
                    } else {
                        // ??????????
                        return false;
                    }
                }
            }


            // Controllo nome dispositivo
            if (Constants.DEVICE_ID == null || Constants.DEVICE_ID.equalsIgnoreCase("unknown") || Constants.DEVICE_ID.isEmpty()) {
                // Bypass the login
                APPData.LOGGED_IN = 0;
                APPData.COD_ESITO = "E";
                return false;
            }


            if (NetworkActivity.isOnline() > 0) {
                res = jsonParser.ParseURL(loginURL, labelsParam, valuesParam, resultTags, null, null, null, null, Constants.ENCRYPT, Constants.DECRYPT, true);
                if (res > 0) {
                    // Store last response ? : No may by inaccurate
                    if (Constants.DEBUG) {
                        DialogBox.DialogBox("DEBUG", "res:"+res+"\r\njsonParser.rawHttpContent:" + jsonParser.rawHttpContent, 0 + 0, this);
                    }
                } else {
                    if (jsonParser.rawHttpStatus != 200 && jsonParser.rawHttpStatus != 201 && jsonParser.rawHttpStatus != 202 && jsonParser.rawHttpStatus != 203) {
                        APPData.LOGGED_IN_BY_SERVER = true;
                        if (Constants.DEBUG) {
                            DialogBox.DialogBox("DEBUG", "jsonParser.rawHttpStatus:"+jsonParser.rawHttpStatus+"\r\njsonParser.rawHttpContent:" + jsonParser.rawHttpContent, 0 + 0, this);
                        }
                    }
                }

            } else {
                // Login offline
                res = jsonParser.ParseString(APPData.LAST_LOGIN_RESPONSE, resultTags, null, null);
                if (res > 0) {
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }






    // TEST
    // String result = "{\"codEsito\":\"S\",\"idSessione\":\"dcbf6c31-f583-462d-ae2d-55108c6416f9\"}";
    // jsonParser.ParseString(result, new String[]{"codEsito","idSessione"}, null, null);



    private boolean onLoginDone( JSONParser jsonParser ) {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        int res = 0;

        try {

            if (jsonParser.rawHttpStatus != 200 && jsonParser.rawHttpStatus != 201 && jsonParser.rawHttpStatus != 202 && jsonParser.rawHttpStatus != 203) {
                if (APPData.LAST_LOGIN_RESPONSE != null && !APPData.LAST_LOGIN_RESPONSE.isEmpty()) {
                    String dbg_message = "Risposta dal server non valida [HTTP status:" + jsonParser.rawHttpStatus + "]";
                    DialogBox.ShowMessage(dbg_message, LoginActivity.this, 1);

                    String message = "Server non disponibile!";
                    message += CONTINUE_LOGIN_IF_SERVER_FAIL ? "\r\n\r\n Continuare il login ?\r\n" : "";

                    // Controllo nome dispositivo
                    if (Constants.DEVICE_ID == null || Constants.DEVICE_ID.equalsIgnoreCase("unknown") || Constants.DEVICE_ID.isEmpty()) {
                        message += "\r\nImpossibile identificare il dispositovo\r\n";
                    }

                    if (Constants.DEBUG) {
                        message += "\r\n\r\n--- Dettaglio Risposta Server ---\r\n" + dbg_message;
                    }

                    if (    (APPData.LAST_ENV != null && !APPData.LAST_ENV.isEmpty()) ||
                            (APPData.LAST_ENV_ON_DWG != null && !APPData.LAST_ENV_ON_DWG.isEmpty()) ) {
                        /////////////////////////////////////////////
                        // Recovery from crash
                        //
                        String[] resultTags = new String[]{"codEsito", "idsessione"};
                        res = jsonParser.ParseString(APPData.LAST_LOGIN_RESPONSE, resultTags, null, null);
                        if (res > 0) {
                        }
                    } else {
                        /////////////////////////////////////////////
                        // Login from last response ?
                        //
                        boolean bLoginOffline = false;
                        if (NetworkActivity.isOnline() > 0) {
                            if (DialogBox.DialogBox("ATTENZIONE", message, CONTINUE_LOGIN_IF_SERVER_FAIL?(0 + 1 + 2):(0 + 0), LoginActivity.this)) {
                                // Login offline
                                bLoginOffline = true;
                            }
                        } else {
                            bLoginOffline = true;
                        }

                        if (bLoginOffline) {
                            // Login offline
                            String[] resultTags = new String[]{"codEsito", "idsessione"};
                            res = jsonParser.ParseString(APPData.LAST_LOGIN_RESPONSE, resultTags, null, null);
                            if (res > 0) {
                            }
                            APPData.LOGGED_IN_OFFLINE = true;
                        } else {
                            return false;
                        }
                    }
                }
            }


            APPData.COD_ESITO = jsonParser.Header != null ? jsonParser.Header.get(0) : "";

            if (jsonParser.Header != null) {
                if (jsonParser.Header.size() >= 3) {
                    APPData.MSG_ESITO = jsonParser.Header != null ? jsonParser.Header.get(2) : "";
                } else {
                    APPData.MSG_ESITO = "";
                }
            }

            if (APPData.COD_ESITO.equals("S")) {

                try {
                    APPData.ID_SESSIONE = jsonParser.Header.get(1);
                } catch (Exception e) {
                }

                if (APPData.ID_SESSIONE != null) {
                    if (APPData.ID_SESSIONE.equalsIgnoreCase("null")) {
                        APPData.ID_SESSIONE = null;
                    }
                }
                if (APPData.ID_SESSIONE == null || APPData.ID_SESSIONE.isEmpty()) {
                    APPData.LOGGED_IN = -2;
                    String message = "Sessione non valida";
                    DialogBox.ShowMessage(message, LoginActivity.this, 1);
                    message += CONTINUE_LOGIN_AS_DEBUG ? "\r\n\r\n Forzare il login ?\r\n" : "";
                    if (DialogBox.DialogBox("ATTENZIONE", message, 0 + (CONTINUE_LOGIN_AS_DEBUG ? 1 + 2 : 0), LoginActivity.this)) {
                        if (CONTINUE_LOGIN_AS_DEBUG) {
                            APPData.ID_SESSIONE = "";
                            APPData.LOGGED_IN = 1;
                        }
                    }

                } else {

                    APPData.USER_LOGIN_ID = ""; // jsonParser.Header.get(1);

                    APPData.LOGGED_IN = 1;
                    // Store last response
                    if (jsonParser.rawHttpContent != null) {
                        APPData.LAST_LOGIN_RESPONSE = jsonParser.rawHttpContent;
                        mainActivity.sqliteWrapper.update_setup_record("LastLoginResponse", APPData.LAST_LOGIN_RESPONSE);
                    }
                }
            } else {
                APPData.LOGGED_IN = -1;
                String message = "Risposta dal server non valida\r\n\r\n" + (APPData.MSG_ESITO!=null?APPData.MSG_ESITO:"") + "";
                DialogBox.ShowMessage(message, LoginActivity.this, 1);
                message += CONTINUE_LOGIN_AS_DEBUG ? "\r\n\r\n Forzare il login ?\r\n" : "";
                if (DialogBox.DialogBox("ATTENZIONE", message, 0 + (CONTINUE_LOGIN_AS_DEBUG ? 1 + 2 : 0), LoginActivity.this)) {
                    APPData.ID_SESSIONE = "";
                    APPData.LOGGED_IN = 1;
                }
            }


        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }


        return true;
    }




    public boolean onReadUserProfile( JSONParser jsonParser ) {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        int res = 0;

        try {

            if (APPData.LOGGED_IN == 1) {
                ////////////////////////////
                // Lettura profilo
                //
                String loginURL = APPData.getServiceURL("login-service/get-profile", APPData.bEncript);
                String[] labelsParam = new String[]{"username"};
                String[] valuesParam = new String[]{APPData.USER_LOGIN};
                String[] resultTags = new String[]{"codEsito", "profilo", "user_login", "user_descrizione", "device_pkpooll", "device_longitude", "device_latitude", "device_imei", "persona_id", "device_idcomplesso"};

                boolean tryToConnect = true;


                ///////////////////////////
                // Recupero da crash
                //
                if (    (APPData.LAST_ENV != null && !APPData.LAST_ENV.isEmpty()) ||
                        (APPData.LAST_ENV_ON_DWG != null && !APPData.LAST_ENV_ON_DWG.isEmpty()) ) {
                    // Lettura profilo offline
                    if (APPData.LAST_USER_PROFILE_RESPONSE != null && !APPData.LAST_USER_PROFILE_RESPONSE.isEmpty()) {
                        APPData.LOGGED_IN_OFFLINE = true;
                        res = jsonParser.ParseString(APPData.LAST_USER_PROFILE_RESPONSE, resultTags, null, null);
                        if (res > 0) {
                            tryToConnect = false;
                        }
                    }
                }


                if (tryToConnect) {
                    if (NetworkActivity.isOnline() > 0) {
                        res = jsonParser.ParseURL(loginURL, labelsParam, valuesParam, resultTags, null, null, null, null, Constants.ENCRYPT, Constants.DECRYPT, true);
                        if (res > 0) {
                            // Store last response ? No may be inaccurate
                        } else {
                            // Login offline
                            if (APPData.LAST_USER_PROFILE_RESPONSE != null && APPData.LAST_USER_PROFILE_RESPONSE.isEmpty()) {
                                res = jsonParser.ParseString(APPData.LAST_USER_PROFILE_RESPONSE, resultTags, null, null);
                                if (res > 0) {
                                }
                            } else {
                                String message = "Profilo non valido";
                                DialogBox.ShowMessage(message, LoginActivity.this, 1);
                                if (APPData.LAST_USER_PROFILE_RESPONSE != null && !APPData.LAST_USER_PROFILE_RESPONSE.isEmpty()) {
                                    jsonParser.ParseString(APPData.LAST_USER_PROFILE_RESPONSE, resultTags, null, null);
                                } else {
                                    DialogBox.DialogBox("ATTENZIONE", message, 0 + 0, LoginActivity.this);
                                }
                            }
                        }

                    } else {
                        // Lettura profilo offline
                        jsonParser.ParseString(APPData.LAST_USER_PROFILE_RESPONSE, resultTags, null, null);
                    }
                }



                // result :
                // {"device_idcomplesso":null,"user_login":"muve","user_descrizione":"Utente muve","device_pkpooll":"AH","persona_nome":"Luca","persona_id":"1","device_longitude":"45.01","device_latitude":"1234.66","user_id":5,"persona_cognome":"Gri","device_imei":"AAAAAA-BB-CCCCCC-X","persona_codice":"02"}

                // jsonParser.ParseString(result2, new String[]{"codEsito", "profilo", "user_login", "user_descrizione", "device_pkpooll", "device_longitude", "device_latitude", "device_imei"}, null, null);


                APPData.COD_ESITO = jsonParser.Header != null ? jsonParser.Header.get(0) : "";
                if (APPData.COD_ESITO.equals("S")) {
                    if (jsonParser.Header.size() >= 7) {
                        try {
                            APPData.USER_LOGIN = jsonParser.Header.get(1);
                            APPData.USER_DESCRIZIONE = jsonParser.Header.get(2);
                            APPData.DEVICE_PKPOOL = jsonParser.Header.get(3);
                            APPData.DEVICE_LONG = jsonParser.Header.get(4);
                            APPData.DEVICE_LAT = jsonParser.Header.get(5);
                            APPData.DEVICE_IMEI = jsonParser.Header.get(6);
                            APPData.USER_LOGIN_ID = jsonParser.Header.get(7);
                            if (jsonParser.Header.size() >= 8) {
                                APPData.DEVICE_ID_COMPLESSO = jsonParser.Header.get(8);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (APPData.USER_LOGIN_ID == null || APPData.USER_LOGIN_ID.isEmpty()) {
                            String message = "Errore sul servizio lettura profilo utente\r\nID Utente non valido";
                            DialogBox.ShowMessage(message, LoginActivity.this, 1);
                        }

                        if (APPData.DEVICE_PKPOOL == null || APPData.DEVICE_PKPOOL.isEmpty()) {
                        } else {
                            // Store last response
                            if (jsonParser.rawHttpContent != null) {
                                APPData.LAST_USER_PROFILE_RESPONSE = jsonParser.rawHttpContent;
                                mainActivity.sqliteWrapper.update_setup_record("LastUserProfileResponse", APPData.LAST_USER_PROFILE_RESPONSE);
                            }
                        }
                    }
                }





                /////////////////////////////////////
                // Lettura Etichette Oggetti
                //
                res = APPOggettiSQL.leggi_etichette_oggetti(mainActivity.context, LoginActivity.this);
                if (res < 0) {
                    String message = "Errore sul servizio lettura etichette oggetti";
                    DialogBox.ShowMessage(message, LoginActivity.this, 1);
                } else {
                }



                /////////////////////////////////////
                // Lettura Contatore nel server
                //
                if (APPPkPool.leggi_pk_counter() > 0) {
                    if (APPData.InterventiNIDonServer != 0) {
                        if (APPData.InterventiNIDonServer > APPData.InterventiNID) {
                            // Recupero contatore
                            // String message = "L'aplicazione è stata reinstallata!";
                            // DialogBox.ShowMessage(message, this.getBaseContext(), 1);
                            APPData.InterventiNID = APPData.InterventiNIDonServer;
                        } else {
                            // OK
                        }
                    } else {
                        //////////////////////////////
                        // Creazione pool di chiavi
                        //
                        APPPkPool.scrivi_pk_counter();
                    }
                } else {
                    if (APPData.LOGGED_IN_OFFLINE) {
                        // Pool di chiavi non verificabile
                    } else {
                        // Pool di chiavi non verificato : Non permessa la creazione
                        APPData.DEVICE_PKPOOL = null;
                    }
                }



                // Azzeramento just logged off
                APPData.JUST_LOGGED_OFF = false;




                // Chiusura Activity (Finestra login)
                this.finish();




            } else {
                // Login fallito
            }


        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }


        return true;
    }






    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                android.os.Handler mainHandler = new android.os.Handler(mainActivity.context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        exitApp();
                    }
                };
                mainHandler.post(myRunnable);
                // this.runOnUiThread ( new Runnable() { @Override public void run() { exitApp(null); } });
                return false;
            }
            return super.onKeyDown(keyCode, event);
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }


    public void exitApp() {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        if (DialogBox.DialogBox("ATTENZIONE", "Chiudere l'applicazione?", 0 + 1 + 2, LoginActivity.this)) {
            try {
                Toast.makeText(mainActivity.context, "iMuve v1.01 closing...", Toast.LENGTH_LONG).show();

                // Salvataggio dati cfg
                APPData.save();
                // APPPRefs.save_app_prefs(mainActivity.context);

                APPData.LOGGED_IN = -9;
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    static public void doLogOut () {

        APPData.LOGGED_IN = 0;
        APPData.ID_SESSIONE = null;
        APPData.USER_DESCRIZIONE = null;
        APPData.DEVICE_PKPOOL = null;
        APPData.DEVICE_LONG = null;
        APPData.DEVICE_LAT = null;
        APPData.DEVICE_IMEI = null;

        APPData.LAST_ENV = null;
        APPData.LAST_ENV_ON_DWG = null;

    /*
    #Servizio logout
    #metodo:
    POST
    #path:/login - service / logout
    #esempio chiamata
    http://pamap.geisoft.org/services/pamap/login-service/logout?idsessione=1a64f71a-28fb-4d72-9a66-69386c5c2f85
    #esempio risposta
    { "eseguito":true, "codEsito":"S" }
    */

        if (APPData.LOGGED_IN_BY_SERVER) {
            String loginURL = APPData.getServiceURL("login-service/logout", APPData.bEncript);
            String[] labelsParam = new String[]{"username"};
            String[] valuesParam = new String[]{APPData.USER_LOGIN};
            String[] resultTags = new String[]{"codEsito"};

            if (NetworkActivity.isOnline() > 0) {
                JSONParser jsonParser = new JSONParser(null);
                int res = jsonParser.ParseURL(loginURL, labelsParam, valuesParam, resultTags, null, null, null, null, Constants.ENCRYPT, Constants.DECRYPT, true);
                if (res > 0) {
                }
            }
        }

        APPData.LOGGED_IN_BY_SERVER = false;
    }


}



