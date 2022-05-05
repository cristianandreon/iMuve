package com.imuve.cristian.imuve;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;



class APPOggetti {


    public int cOggetto = -1;
    public int NumOggetti = 0;

    public ArrayList<Integer> IDOggetti = null;
    public ArrayList<String> CodOggetti = null;
    public ArrayList<String> DescOggetti = null;
    public ArrayList<String> ExtDescOggetti = null;
    public ArrayList<Integer> IDVanno = null;

    public APPOggetti() {
        IDOggetti=new ArrayList<Integer>();
        CodOggetti=new ArrayList<String>();
        DescOggetti=new ArrayList<String>();
        ExtDescOggetti=new ArrayList<String>();
        IDVanno=new ArrayList<Integer>();
    }


    public void setup ( ) {

        cOggetto=-1;
        NumOggetti=0;

        if(IDOggetti==null) IDOggetti=new ArrayList<Integer>();
        if(CodOggetti==null) CodOggetti=new ArrayList<String>();
        if(DescOggetti==null) DescOggetti=new ArrayList<String>();
        if(ExtDescOggetti==null) ExtDescOggetti=new ArrayList<String>();
        if (IDVanno == null) IDVanno=new ArrayList<Integer>();

        IDOggetti.clear();
        CodOggetti.clear();
        DescOggetti.clear();
        ExtDescOggetti.clear();
        IDVanno.clear();

        return;
    }

}


class APPInterventi {

    public int cIntervento = 0;
    public int NumInterventi = 0;

    public ArrayList<String> IDInterventi = null;
    public ArrayList<Integer> IDInterventiOnQueue = null;
    public ArrayList<String> CodInterventi = null;
    public ArrayList<String> DescInterventi = null;
    public ArrayList<String> DescInterventiAux = null;
    public ArrayList<String> ExtDescInterventi = null;
    public ArrayList<String> DataAperturaIntervento = null;
    public ArrayList<String> DataScadenzaIntervento = null;
    public ArrayList<String> DataChiusuraIntervento = null;
    public ArrayList<String> StatoIntervento = null;
    public ArrayList<String> CodOggettoInterventi = null;
    public ArrayList<String> DescOggettoInterventi = null;
    public ArrayList<String> IDOggettoInterventi = null;
    public ArrayList<String> IDVanoInterventi = null;
    public ArrayList<String> IDPainoInterventi = null;
    public ArrayList<String> IDEdificioInterventi = null;
    public ArrayList<String> IDComplessoInterventi = null;
    public ArrayList<String> EdificioInterventi = null;
    public ArrayList<String> PianoInterventi = null;
    public ArrayList<String> VanoInterventi = null;


    public void setup() {

        NumInterventi = 0;

        if (IDInterventi == null) IDInterventi = new ArrayList<String>();
        if (IDInterventiOnQueue == null) IDInterventiOnQueue = new ArrayList<Integer>();

        if (CodInterventi == null) CodInterventi = new ArrayList<String>();
        if (DescInterventi == null) DescInterventi = new ArrayList<String>();
        if (DescInterventiAux == null) DescInterventiAux = new ArrayList<String>();
        if (ExtDescInterventi == null) ExtDescInterventi = new ArrayList<String>();
        if (DataAperturaIntervento == null) DataAperturaIntervento = new ArrayList<String>();
        if (DataScadenzaIntervento == null) DataScadenzaIntervento = new ArrayList<String>();
        if (DataChiusuraIntervento == null) DataChiusuraIntervento = new ArrayList<String>();
        if (StatoIntervento == null) StatoIntervento = new ArrayList<String>();
        if (IDOggettoInterventi == null) IDOggettoInterventi = new ArrayList<String>();
        if (IDVanoInterventi == null) IDVanoInterventi = new ArrayList<String>();
        if (IDPainoInterventi == null) IDPainoInterventi = new ArrayList<String>();
        if (IDEdificioInterventi == null) IDEdificioInterventi = new ArrayList<String>();
        if (IDComplessoInterventi == null) IDComplessoInterventi = new ArrayList<String>();

        if (CodOggettoInterventi == null) CodOggettoInterventi = new ArrayList<String>();
        if (DescOggettoInterventi == null) DescOggettoInterventi = new ArrayList<String>();
        if (EdificioInterventi == null) EdificioInterventi = new ArrayList<String>();
        if (PianoInterventi == null) PianoInterventi = new ArrayList<String>();
        if (VanoInterventi == null) VanoInterventi = new ArrayList<String>();




        IDInterventi.clear();
        IDInterventiOnQueue.clear();

        CodInterventi.clear();
        DescInterventi.clear();
        DescInterventiAux.clear();
        ExtDescInterventi.clear();
        DataAperturaIntervento.clear();
        DataScadenzaIntervento.clear();
        DataChiusuraIntervento.clear();
        StatoIntervento.clear();
        IDOggettoInterventi.clear();
        IDVanoInterventi.clear();
        IDPainoInterventi.clear();
        IDEdificioInterventi.clear();
        IDComplessoInterventi.clear();
        CodOggettoInterventi.clear();
        DescOggettoInterventi.clear();
        EdificioInterventi.clear();
        PianoInterventi.clear();
        VanoInterventi.clear();

        return;
    }
}



    /**
 * Created by Cristian on 23/04/2015.
 */
public class APPData {

        public static boolean Initialized = false;


        // Cifratura dei parametri
        public static boolean bEncript = false;


        public static int LOGGED_IN = 0;
        public static String COD_ESITO = null;
        public static String ID_SESSIONE = null;
        public static String MSG_ESITO = null;
        public static boolean LOGGED_IN_BY_SERVER = false;
        public static boolean JUST_LOGGED_OFF = false;
        public static boolean LOGGED_IN_OFFLINE = false;


        // Risposta dal login
        public static String LAST_LOGIN_RESPONSE = null;

        // Risposta dal profilo utente
        public static String LAST_USER_PROFILE_RESPONSE = null;

        // Risposta etichette Oggetto
        public static String LAST_OJECT_LABELS_RESPONSE = null;


        // Date sincronizzazione
        public static String LAST_READ_COMPLESSO = null;
        public static String LAST_READ_EDIFICIO = null;
        public static String LAST_READ_PIANO = null;
        public static String LAST_READ_VANO = null;
        public static String LAST_READ_OGGETTO = null;
        public static String LAST_READ_INTERVENTO = null;
        public static String LAST_READ_DWG = null;


        // Testo ricerca nel disegno
        public static String LAST_SEARCH_TEXT_ON_DWG;


        public static String USER_LOGIN = "muve";
        public static String USER_LOGIN_ID = "";
        public static String USER_DESCRIZIONE = null;
        public static String DEVICE_PKPOOL = null;
        public static String DEVICE_LONG = null;
        public static String DEVICE_LAT = null;
        public static String DEVICE_IMEI = null;
        public static String DEVICE_ID_COMPLESSO = null;



        /////////////////////////////////////////////////////////////////////
        // Gestione Coordinate oggetto manutentivo
        //


        public static Integer LAST_SEL_IDCOMPLESSO = 0;
        public static Integer LAST_SEL_IDEDIFICIO = 0;
        public static Integer LAST_SEL_IDPIANO = 0;
        public static Integer LAST_SEL_IDVANO = 0;
        public static Integer LAST_SEL_IDOGGETTO = 0;
        public static String LAST_SEL_IDINTERVENO = "";

        public static String LAST_SEL_COMPLESSO = "";
        public static String LAST_SEL_EDIFICIO = "";
        public static String LAST_SEL_PIANO = "";
        public static String LAST_SEL_VANO = "";
        public static String LAST_SEL_OGGETTO = "";
        public static String LAST_SEL_OGGETTO_DESC = "";


        public static String LAST_SEL_INTERVENO = "";
        // public static String Complesso = ""; // "Complesso N.1";
        // public static String Edificio = ""; // "Palazzo Ducale";
        // public static String Piano = ""; // "1° Piano";
        // public static String Vano = ""; // "Vano X";
        // public static String Oggetto = ""; // "[Vuoto]";
        // public static String IDOggetto = "";

        public static String SHOW_IF_INTERVENTO = "0";
        public static String SHOW_LAYER_LAMPADINE = "true";
        public static String SHOW_LAYER_EMERGENZE = "true";


        public static float DRAW_DETECT_ZONE_ZOOM = 50.0f;
        public static float DRAW_DETECT_OBJECT_ZOOM = 20.0f;

        public static String KEEP_LOGGED = "0";
        public static String AUTO_SYNC = "0";
        public static String STORE_TYPED1 = "0";
        public static String AVVIO_NUOVO_INTERVENTO = "0";
        public static String CLOSE_ON_NEW_INTERVENTO = "0";



        public static String LAST_ENV = null;
        public static String LAST_ENV_ON_DWG = null;

        public static String LAST_ENV_ON_DWG_PARAM = null;
        public static String LAST_ENV_ON_DWG_PARAM1 = null;
        public static String LAST_ENV_ON_DWG_PARAM2 = null;
        public static String LAST_ENV_ON_DWG_PARAM3 = null;
        public static String LAST_ENV_ON_DWG_PARAM4 = null;
        public static String LAST_ENV_ON_DWG_PARAM5 = null;
        public static String LAST_ENV_ON_DWG_PARAM6 = null;
        public static String LAST_ENV_ON_DWG_PARAM7 = null;
        public static String LAST_ENV_ON_DWG_PARAM8 = null;

        public static String LAST_ENV_CAMERA_X = null;
        public static String LAST_ENV_CAMERA_Y = null;
        public static String LAST_ENV_CAMERA_WX = null;
        public static String LAST_ENV_CAMERA_WY = null;



        /////////////////////////////////////////////////////////////////////
        // Gestione Elenco interventi associati all'oggetto manutentivo
        //
        public static APPInterventi appInterventi = null;

        public static boolean nuovoIntervento = true;
        public static int InterventiNID = 1;
        public static int InterventiNIDonServer = 1;


        public static String TYPED_INTERVENTO_DESC = "";
        public static String TYPED_INTERVENTO_EXT_DESC = "";



        /////////////////////////////////////////////////////////////////////
        // Gestione Campi dell'oggetto manutentivo da visualizzare
        //
        public static int NumObjectCustomFields = 0;
        public static ArrayList<String> ObjectCustomFields = null;
        public static ArrayList<String> ObjectCustomLabels = null;
        public static ArrayList<String> ObjectCustomLabelsOnDB = null;

        public static int MAX_OBECT_CUSTOM_FIELDS = 16;



        /////////////////////////////////////////////////////////////////////
        // Gestione Campi Oggetti manutentivi
        //
        public static APPOggetti appOggetti = null;
        public static APPOggetti appOggettiIfIntervento = null;
        public static APPOggetti appOggettiPerVano = null;
        public static APPOggetti appSelezioneOggetti = null;


        /////////////////////////////////////////////////////////////////////
        // Gestione Campi Complessi
        //
        public static int cComplesso = -1;
        public static int NumComplessi = 0;

        public static ArrayList<Integer>IDComplessi = null;
        public static ArrayList<String>Complessi = null;


        /////////////////////////////////////////////////////////////////////
        // Gestione Campi Edifici
        //
        public static int cEdificio = -1;
        public static int NumEdifici = 0;

        public static ArrayList<Integer>IDEdifici = null;
        public static ArrayList<String>Edifici = null;
        public static ArrayList<String>CodEdifici = null;
        public static ArrayList<String>GpsLongEdifici = null;
        public static ArrayList<String>GpsLatEdifici = null;

        /////////////////////////////////////////////////////////////////////
        // Gestione Campi Piani
        //
        public static int cPiano = -1;
        public static int NumPiani = 0;

        public static ArrayList<Integer>IDPiani = null;
        public static ArrayList<String>Piani = null;
        public static ArrayList<String>CodPiani = null;
        public static ArrayList<String>DWGPiani = null;
        public static ArrayList<String>IDDWGPiani = null;
        public static ArrayList<String>PianiDesc = null;



        /////////////////////////////////////////////////////////////////////
        // Gestione Campi Vani
        //
        public static int cVano = -1;
        public static int NumVani = 0;

        public static ArrayList<Integer>IDVani = null;
        public static ArrayList<String>Vani = null;
        public static ArrayList<String>CodVani = null;






        /////////////////////////////////////////////////////////////////////
        // Gestione Campi Tabella DWG
        //
        public static int cDwg = -1;
        public static int NumDwg = 0;

        public static ArrayList<Integer>IDDwg = null;
        public static ArrayList<String>CodDwg = null;
        public static ArrayList<String>VerDwg = null;
        public static ArrayList<String>VerDescDwg = null;
        public static ArrayList<String>DescDwg = null;





        /////////////////////////////////////////////////////////////////////
        // Gestione Tabella Queue
        //
        public static int cQueue = 0;
        public static int NumQueue = 0;

        public static ArrayList<Integer> IDQueue;
        public static ArrayList<String> DescQueue;



        /////////////////////////////////////////////////////////////////////
        // Gestione Tabella Stati
        //
        public static ArrayList<String> InterventiStatusList;



        static int init_app_data() {

            if (!Initialized) {

                Initialized = true;


                appOggetti = new APPOggetti();

                appInterventi = new APPInterventi();

                appOggettiPerVano = new APPOggetti();

                appSelezioneOggetti = new APPOggetti();

                APPData.appOggettiIfIntervento = new APPOggetti();


                // Campi variabili da visualizzare (relativi all'oggetto manutentivo) nella Activity Creazione Intervento
                APPData.NumObjectCustomFields = 0;
                APPData.ObjectCustomFields = new ArrayList<String>();
                APPData.ObjectCustomLabels = new ArrayList<String>();
                APPData.ObjectCustomLabelsOnDB = new ArrayList<String>();
                APPData.MAX_OBECT_CUSTOM_FIELDS = 16;


                // Esempio x DEBUG
                if (Constants.ADD_CUSTOM_FIELD_AS_DEBUG) {
                    APPData.ObjectCustomFields.add("10");
                    APPData.ObjectCustomLabels.add("Power(W)");
                    APPData.ObjectCustomLabelsOnDB.add("POWER");
                    APPData.NumObjectCustomFields++;

                    APPData.ObjectCustomFields.add("EDISON");
                    APPData.ObjectCustomLabels.add("Connector");
                    APPData.ObjectCustomLabelsOnDB.add("CONNECTOR");
                    APPData.NumObjectCustomFields++;

                    APPData.ObjectCustomFields.add("A/E");
                    APPData.ObjectCustomLabels.add("Model");
                    APPData.ObjectCustomLabelsOnDB.add("MODEL");
                    APPData.NumObjectCustomFields++;
                }
            }


            InterventiStatusList = new ArrayList<String>();
            InterventiStatusList.add(" ");
            InterventiStatusList.add("A");
            InterventiStatusList.add("O");
            InterventiStatusList.add("N");
            InterventiStatusList.add("...");

        return 1;
    }



    public static boolean isPKValid ( ) {
        if (APPData.DEVICE_PKPOOL == null || APPData.DEVICE_PKPOOL.isEmpty()) {
            return false;
        }
        return true;
    }



    public static int sIndexOf ( ArrayList<String>array, String ssearch ) {
        if (array != null) {
                for (int i = 0; i < array.size(); i++) {
                    if (ssearch.compareToIgnoreCase(array.get(i)) == 0) {
                        return i;
                    }
                }
            }
        return -1;
        }

        public static int adIndexOf ( ArrayAdapter<String>array, String ssearch ) {
            if (array != null) {
                for (int i = 0; i < array.getCount(); i++) {
                    if (ssearch.compareToIgnoreCase(array.getItem(i)) == 0) {
                        return i;
                    }
                }
            }
            return -1;
        }


    public static int iIndexOf ( ArrayList<Integer>array, Integer search ) {
        if (array != null) {
            int isearch = search.intValue();
            for (int i = 0; i < array.size(); i++) {
                if (isearch==(int)array.get(i)) {
                    return i;
                }
            }
        }
        return -1;
    }



    public static int delay = 1000; // delay for 5 sec.
    public static int period = 60000; // repeat every 60 secs.

    public static Timer timer = new Timer();

    public static int de_queue_loop ( ) {

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    APPQueueSQL.de_queue(null, "SERVER", null, null);
                } catch (Exception e) {
                    System.out.println("de_queue exception : "+e.getMessage());
                }
            }
        }, delay, period);

        return 1;
    }



    public static String getServiceURL ( String ServiceName, boolean bEncript ) {
        String fullURL = Constants.SERVER_PROTOCOL + Constants.SERVER_URL + (ServiceName.charAt(0)!='/'?"/":"") + ServiceName;

        if (Constants.SERVER_PORT > 0) fullURL += ":" + Constants.SERVER_PORT;

        if (APPData.ID_SESSIONE != null) {
            fullURL += "?idsessione=";
            try {
                if (bEncript) {
                    byte[] outBuffer = Crypt.encrypt(Constants.IV_BYTES, Constants.KEY_BYTES, APPData.ID_SESSIONE.getBytes());
                    fullURL += Base64.encodeToString(outBuffer, 0, outBuffer.length, Base64.DEFAULT);
                } else {
                    fullURL += APPData.ID_SESSIONE;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }
        }

        return fullURL;
    }

    public static void save () {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        try {
            APPData.LAST_ENV = "";
            mainActivity.sqliteWrapper.update_setup_record("LastEnviroment", APPData.LAST_ENV);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // debug
        // Toast.makeText(mainActivity.context, "save SERVER_URL:"+Constants.SERVER_URL, Toast.LENGTH_LONG).show();

        try {
            if (Constants.SERVER_PROTOCOL != null && !Constants.SERVER_PROTOCOL.isEmpty()) mainActivity.sqliteWrapper.update_setup_record("SERVER_PROTOCOL", Constants.SERVER_PROTOCOL);
            if (Constants.SERVER_URL != null && !Constants.SERVER_URL.isEmpty()) mainActivity.sqliteWrapper.update_setup_record("SERVER_URL", Constants.SERVER_URL);
            if (Constants.SERVER_PORT >= 0) mainActivity.sqliteWrapper.update_setup_record("SERVER_PORT", String.valueOf(Constants.SERVER_PORT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



class APPUtil {

    /**
     * @return true if match
     */
    static public boolean compareIgnoreCase (String str1, String str2) {
        if (str2.charAt(0)=='>' || str2.charAt(0)=='<' || str2.charAt(0)=='=') {
            if (str2.charAt(0) == '>' && str2.charAt(0) != '=') {
                if (Float.parseFloat(str1) > Float.parseFloat(str2)) {
                    return true;
                }
            } else if (str2.charAt(0) == '<' && str2.charAt(0) != '=') {
                if (Float.parseFloat(str1) < Float.parseFloat(str2)) {
                    return true;
                }
            } else if (str2.charAt(0) == '>' && str2.charAt(0) == '=') {
                if (Float.parseFloat(str1) >= Float.parseFloat(str2)) {
                    return true;
                }
            } else if (str2.charAt(0) == '<' && str2.charAt(0) == '=') {
                if (Float.parseFloat(str1) <= Float.parseFloat(str2)) {
                    return true;
                }
            } else if (str2.charAt(0)=='=') {
                if (Float.parseFloat(str1) == Float.parseFloat(str2)) {
                    return true;
                }
            }
        } else {
            if (str1.toLowerCase().contains(str2.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


    /**
     * @return
     */
    public static String get_date () {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c.getTime());
    }

    public static String get_date_time () {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(c.getTime());
    }


    public static String get_app_date_time (String datetime, int Format ) {
        if (datetime != null) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;

            try {
                date = df.parse(datetime);
            } catch (ParseException e) {
                date = null;
            }

            if (date==null) {
                df = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = df.parse(datetime);
                } catch (ParseException e) {
                    date = null;
                }
            }

            if (date!=null) {
                if (Format == 0) {
                    df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                } else if (Format == 1) {
                    df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                } else if (Format == 2) {
                    df = new SimpleDateFormat("dd-MM-yyyy");
                } else {
                    df = new SimpleDateFormat("yyyy-MM-dd");
                }
                return df.format(date);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }



    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }









    static MyRunnable GLCallback = null;

    static public boolean getSelectFromListview(ArrayList<String>pStringArray, ArrayList<Integer>VctInterventi, int NumVctInterventi,
                                                String Title, final Activity activity, View v, MyRunnable pCallback, Integer pID) {
        GLCallback = (MyRunnable)pCallback;
        GLCallback.myID = pID;
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                activity);
        builderSingle.setIcon(R.drawable.logo3);
        builderSingle.setTitle(Title != null ? Title : "Seleziona una riga...");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                activity,
                android.R.layout.select_dialog_singlechoice);

        String str = null;
        int NumItems = 0;
        if (VctInterventi != null) {
            NumItems = NumVctInterventi;
        } else {
            NumItems = pStringArray.size();
        }
        for (int i=0; i<NumItems; i++) {
            if (VctInterventi != null) {
                try {
                    str = pStringArray.get(VctInterventi.get(i));
                } catch (Exception e) {
                    e.printStackTrace();;
                }
            } else {
                str = pStringArray.get(i);
            }

            if (APPData.adIndexOf(arrayAdapter, str) < 0) {
                arrayAdapter.add(str);
            }
        }



        builderSingle.setNegativeButton("Annulla",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (GLCallback != null) {
                            GLCallback.myParam = arrayAdapter.getItem(which);
                            MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();
                            Handler mainHandler = new Handler(mainActivity.context.getMainLooper());
                            mainHandler.post(GLCallback);
                        }
                    }
                });

        builderSingle.show();
        return true;
    }





    static public boolean getSelectFromCheckbox(ArrayList<String>pStringArray, ArrayList<Boolean>pBooleanArray,
                                                ArrayList<Integer>VctInterventi, int NumVctInterventi,
                                                String Title, final Activity activity, View v, MyRunnable pCallback, Integer pID) {
        GLCallback = (MyRunnable)pCallback;
        GLCallback.myID = pID;


        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                activity);
        builderSingle.setIcon(R.drawable.logo3);
        builderSingle.setTitle(Title != null ? Title : "Imposta gli stati...");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                activity,
                android.R.layout.select_dialog_singlechoice);

        final CharSequence[] items = pStringArray.toArray(new CharSequence[pStringArray.size()]);

        final boolean[] states = new boolean[pBooleanArray.size()];
        for (int i = 0; i < pBooleanArray.size(); i++) {
            states[i] = pBooleanArray.get(i);
        }

        builderSingle.setMultiChoiceItems(items, states,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            states[indexSelected] = true;
                        } else {
                            states[indexSelected] = false;
                        }
                    }
                });


        /*
        String str = null;
        Integer value = 0;

        int NumItems = 0;
        if (VctInterventi != null) {
            NumItems = NumVctInterventi;
        } else {
            NumItems = pStringArray.size();
        }
        for (int i=0; i<NumItems; i++) {
            if (VctInterventi != null) {
                try {
                    str = pStringArray.get(VctInterventi.get(i));
                    value = pIntegerArray.get(VctInterventi.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                str = pStringArray.get(i);
                value = pIntegerArray.get(i);
            }

            if (APPData.adIndexOf(arrayAdapter, str) < 0) {
                arrayAdapter.add(str);
            }
        }
        */


        builderSingle.setNegativeButton("Chiudi",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (GLCallback != null) {
                            GLCallback.booleanArray = states;
                            GLCallback.myParam = null;
                            MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();
                            Handler mainHandler = new Handler(mainActivity.context.getMainLooper());
                            mainHandler.post(GLCallback);
                        }
                        dialog.dismiss();
                    }
                });

        /*
        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (GLCallback != null) {
                            GLCallback.myParam = arrayAdapter.getItem(which);
                            MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();
                            Handler mainHandler = new Handler(mainActivity.context.getMainLooper());
                            mainHandler.post(GLCallback);
                        }
                    }
                });
                */

        builderSingle.show();
        return true;
    }
}







class MyRunnable implements Runnable {

    public String myParam = null;
    public Integer myID = null;
    boolean [] booleanArray = null;

    public MyRunnable(String myParam, Integer myID){
        this.myParam = myParam;
        this.myID = myID;
    }

    public void run(){
    }
}

