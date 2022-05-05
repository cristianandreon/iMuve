package com.imuve.cristian.imuve;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.almworks.sqlite4java.SQLiteException;

import java.io.File;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
import static java.lang.Integer.parseInt;

/**
 * Created by Cristian on 21/04/2015.
 */


public class SQLiteWrapper extends SQLiteOpenHelper {

    private boolean BUILD_DEBUG_DATA = false;

    public SQLiteDatabase db;
    private SQLiteDatabase.CursorFactory cur;
    private Context context;

    public SQLiteWrapper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }


    public String read_setup_record(String typeOf, String defValue) {

        String[] cols = new String[]{"id", "data"};
        String whereClause = "type='" + typeOf + "'";
        String version = null, id = null;
        Cursor mCursor = db.query(false, "setup", cols, whereClause, null, null, null, null, null);

        String resultString = null;

        try {
            if (mCursor != null) {
                if (mCursor.moveToFirst()) {
                    id = mCursor.getString(0);
                    resultString = mCursor.getString(1);
                    System.err.println("Read DB." + typeOf + ":" + resultString);
                }
            }
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

        if (id == null || id.isEmpty()) {
            if (defValue != null) {
                ContentValues values = new ContentValues();
                values.put("data", defValue);
                values.put("type", typeOf);
                if (db.insert("setup", "", values) == 0) {
                    System.err.println("Setup records failed!");
                } else {
                    resultString = defValue;
                }
                System.err.printf("Init DB." + typeOf + ":" + Constants.APP_VERSION);
            }
        }

        if (mCursor != null) {
            mCursor.close();
        }

        return resultString;
    }





    public int update_setup_record(String typeOf, String newValue) {
        String whereClause = "type='" + typeOf + "'";
        ContentValues values = new ContentValues();
        values.put("data", newValue);
        if (db!=null) {
            return db.update("setup", values, whereClause,null);
        } else {
            return -1;
        }
    }


    public int read_setup() throws SQLiteException {

        try {

            File dbFile = context.getDatabasePath("imuve.db");
            // "/data/data/com.imuve.cristian.imuve/databases/"

            File folder = dbFile.getParentFile();
            boolean bExist = folder.mkdirs();

            String dbPath = dbFile.getAbsolutePath();

            db = openOrCreateDatabase(dbPath, cur);



            // db.execSQL("DROP TABLE setup");
            if (BUILD_DEBUG_DATA) {
                try {
                    db.execSQL("DROP TABLE COMPLESSO");
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                }
                try {
                    db.execSQL("DROP TABLE EDIFICIO");
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                }
                try {
                    db.execSQL("DROP TABLE PIANO");
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                }
                try {
                    db.execSQL("DROP TABLE VANO");
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                }
                try {
                    db.execSQL("DROP TABLE OGGETTO");
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                }
                try {
                    db.execSQL("DROP TABLE INTERVENTI");
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                }
                try {
                    db.execSQL("DROP TABLE DWG");
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                }
                try {
                    db.execSQL("DROP TABLE queue");
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                }
            }




            db.execSQL("CREATE TABLE IF NOT EXISTS setup (id integer primary key autoincrement,data text, type text)");

            db.execSQL("CREATE TABLE IF NOT EXISTS queue (id integer primary key autoincrement,Operation text,Status text,TableName text,LocalID text,ExtID text,Field1 text,Field2 text,Field3 text,Field4 text,Field5 text,Field6 text,Field7 text,Field8 text,Field9 text,Field10 text,Field11 text,Field12 text,Field13 text,Field14 text,Field15 text,Field16 text)");


            /*
            CREATE TABLE DBO.INTERVENTI
                            IDINTERVENTO VARCHAR(10) NOT NULL,
                            R CATEGORIA VARCHAR(1) NOT NULL, -- lista ( O- Oridnario, S-straordinario )
                            R IDATTMANU VARCHAR(10),  -- Id Attivita Manutenzioni Ordinarie collegato
                            R CDINTERVENTO VARCHAR(10) NOT NULL, -- codice generato da sistema
                            R STATO VARCHAR(1) NOT NULL, -- lista ( O- Aperto, A-Assegnato, L-In lavoro, S-sospeso , C-chiuso , R - Rifiutato  )
                            R DSINTERVENTO VARCHAR(255) NOT NULL,
                            R CANALE  VARCHAR(1) NOT NULL, -- lista ( I- Interno, M-Mail, E-Esterno )
                            DSESTESA VARCHAR(1000),
                            R( da a )  DATA_APERTURA DATETIME ,
                            DATA_SCADENZA DATETIME ,
                            DATA_CHIUSURA DATETIME ,
                            R RICHIEDENTE VARCHAR(10), -- persona richiedente
                            R OPERATORE_HELP_DESK VARCHAR(10), -- persona help desk
                            GRAVITA VARCHAR(10) NOT NULL,  -- lista ( 1 Trivial, 2 Minor, 3 Major, 4 Critical, 5 Emergency, 6 Safety )
                            PRIORITA VARCHAR(10) NOT NULL,  -- lista ( 1- Alta, 2 -Media, 3- Bassa,4 - nei ritagli di tempo   )
                            INRITARDO VARCHAR(1), -- lista ( S - SI , N - NO )
                            R ESITO VARCHAR(10),  -- lista (  P - Positivo, N - Negativo , NA - Non assegnato  )
                            ISTRUZIONI  VARCHAR(1000) ,
                            COMMENTI  VARCHAR(1000) ,
                            R IDGRUPPO VARCHAR(10),   -- gruppo manutenzioni
                            R IDOGGETTO VARCHAR(10) , -- oggetto manutentivo
                            R IDPERSONA VARCHAR(10) , -- persona che esegue intervento
                            R IDSQUADRA VARCHAR(10) , -- squadra che esegue intervento
                            DOSSIER VARCHAR( 255 ) , -- codice pratica da wf dukenet
                            */



            /*
            Cancellazione Tabella DWG
            try {
                db.execSQL("DROP TABLE DWG");
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }
            */



            try {
                // Tabella DWG
                // Note IDDWG non valorizzato per mancanza(non necessario) servizio lettura DWG
                db.execSQL("CREATE TABLE IF NOT EXISTS DWG (id integer primary key autoincrement, IDDWG text,IDPIANO text,DWGNAME text,DWGVERSION text,DWGDATA text)");
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }


            try {
                // Tabella Complessi
                db.execSQL("CREATE TABLE IF NOT EXISTS COMPLESSO (IDCOMPLESSO text,CDCOMPLESSO text,DSCOMPLESSO text)");
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }

            try {
                // Tabella EDIFICIO
                db.execSQL("CREATE TABLE IF NOT EXISTS EDIFICIO (IDEDIFICIO text,IDCOMPLESSO text,CDEDIFICIO text,DSEDIFICIO text)");
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }

            try {
                // Tabella PIANO
                db.execSQL("CREATE TABLE IF NOT EXISTS PIANO (IDPIANO text,IDEDIFICIO text,CDPIANO text,DSPIANO text)");
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }

            try {
                // Tabella VANO
                db.execSQL("CREATE TABLE IF NOT EXISTS VANO (IDVANO text,IDPIANO text,CDVANO text,DSVANO text)");
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }


            try {
                // Tabella OGGETTI manutentivi
                db.execSQL("CREATE TABLE IF NOT EXISTS OGGETTO (IDOGGETTO text,IDOGGETTO_P text,CODICE text,DESCRIZIONE text" +
                                ",STATO text, TIPO_DISPONIBILITA text, SERIALNUMBER text, PRODUTTORE text, IDFORNITORE text, IDARTICOLO text, CDMODELLO text" +
                                ",IDCOMPLESSO text, IDEDIFICIO text, IDPIANO text, IDVANO text" +
                                ",IDUNITA text, IDAREA text, IDCATEGORIA text, IDGRUPPO text" +
                                ",CDCONDIZIONE TEXT,DTCONTROLLO TEXT,CODGARANZIA TEXT,TIPO_GARANZIA TEXT,DT_INIZIO_GAR TEXT,DT_FINE_GAR TEXT,DT_INSTALLAZIONE TEXT" +
                                ",DT_COLLAUDO_GAR TEXT,INSTALLATORE TEXT,IDIMPIANTO TEXT,T_ALTEZZA TEXT,T_LARGHEZZA TEXT,T_PROFONDITA TEXT,T_PESO TEXT,T_COLORE TEXT" +
                                ",T_CONSUMO_ELETT TEXT,T_VOLTAGGIO TEXT,T_WATT_EQUIVAL TEXT,T_POS_ALTEZZA TEXT,T_NR_OGGETTI TEXT,NOTE TEXT,DATA_INS TEXT,DATA_AGG TEXT" +
                                ",UTENTE_INS TEXT,UTENTE_AGG TEXT,ENTE_INS TEXT,ENTE_AGG TEXT" +
                                ",IDQUEUE integer" +
                                ")"
                );
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }

            try {
                // Tabella interventi manutentivi
                db.execSQL("CREATE TABLE IF NOT EXISTS INTERVENTI (IDINTERVENTO integer,CATEGORIA text,IDATTMANU text,CDINTERVENTO text" +
                                ",STATO text,DSINTERVENTO text,CANALE text,DSESTESA text" +
                                ",DATA_APERTURA text,DATA_SCADENZA text,DATA_CHIUSURA text" +
                                ",RICHIEDENTE text,OPERATORE_HELP_DESK text,GRAVITA text,PRIORITA text,INRITARDO text,ESITO text,ISTRUZIONI text,COMMENTI text" +
                                ",IDGRUPPO text,IDOGGETTO text,IDPERSONA text,IDSQUADRA text,DOSSIER text" +
                                ",IDQUEUE integer" +
                                ")"
                );
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }



            ///////////////////////////////////////
            // Aggiunta colonne
            //
            try {
                db.execSQL("ALTER TABLE EDIFICIO ADD COLUMN GPSLAT TEXT" );
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }

            try {
                db.execSQL("ALTER TABLE EDIFICIO ADD COLUMN GPSLONG TEXT" );
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }



            // Lettura tabella setup - Versione
            Constants.TABLE_VERSION = read_setup_record("version", Constants.TABLE_VERSION);


            // Lettura campo Next ID per la Tabella INTERVENTI
            APPData.InterventiNID = parseInt(read_setup_record("InterventiNID", "1"));


            APPData.LAST_LOGIN_RESPONSE = read_setup_record("LastLoginResponse", "");

            APPData.LAST_USER_PROFILE_RESPONSE = read_setup_record("LastUserProfileResponse", "");

            APPData.LAST_OJECT_LABELS_RESPONSE = read_setup_record("LastCustomLabelsResponse", "");




            APPData.LAST_READ_COMPLESSO = read_setup_record("LastReadComplesso", "");
            APPData.LAST_READ_EDIFICIO = read_setup_record("LastReadEdificio", "");
            APPData.LAST_READ_PIANO = read_setup_record("LastReadPiano", "");
            APPData.LAST_READ_VANO = read_setup_record("LastReadVano", "");
            APPData.LAST_READ_OGGETTO = read_setup_record("LastReadOggetto", "");
            APPData.LAST_READ_INTERVENTO = read_setup_record("LastReadIntervento", "");
            APPData.LAST_READ_DWG = read_setup_record("LastReadDWG", "");



            APPData.LAST_SEARCH_TEXT_ON_DWG = read_setup_record("LastSearchTextOnWG", "");



            try {
                APPData.LAST_SEL_IDCOMPLESSO = Integer.parseInt(read_setup_record("LastSelIDComplesso", "0"));
            } catch (Exception e) { }
            try {
                APPData.LAST_SEL_IDEDIFICIO = Integer.parseInt(read_setup_record("LastSelIDEdificio", "0"));
            } catch (Exception e) { }
            try {
                APPData.LAST_SEL_IDPIANO = Integer.parseInt(read_setup_record("LastSelIDPiano", "0"));
            } catch (Exception e) { }
            try {
                APPData.LAST_SEL_IDVANO = Integer.parseInt(read_setup_record("LastSelIDVano", "0"));
            } catch (Exception e) { }
            try {
                APPData.LAST_SEL_IDOGGETTO = Integer.parseInt(read_setup_record("LastSelIDOggetto", "0"));
            } catch (Exception e) { }
            try {
                APPData.LAST_SEL_IDINTERVENO = read_setup_record("LastSelIDIntervento", "0");
            } catch (Exception e) { }



            try {
                APPData.DRAW_DETECT_ZONE_ZOOM = Float.parseFloat(read_setup_record("DrawDetectZoneZoom", String.valueOf(APPData.DRAW_DETECT_ZONE_ZOOM)));
            } catch (Exception e) {
            }

            if (APPData.DRAW_DETECT_ZONE_ZOOM < 0.001f || APPData.DRAW_DETECT_ZONE_ZOOM > 1000.0f) APPData.DRAW_DETECT_ZONE_ZOOM = 15.0f;




            try {
                APPData.DRAW_DETECT_OBJECT_ZOOM = Float.parseFloat(read_setup_record("DrawDetectObjectZoom", String.valueOf(APPData.DRAW_DETECT_OBJECT_ZOOM)));
            } catch (Exception e) {
            }
            if (APPData.DRAW_DETECT_OBJECT_ZOOM < 0.001f || APPData.DRAW_DETECT_OBJECT_ZOOM > 1000.0f) APPData.DRAW_DETECT_OBJECT_ZOOM = 10.0f;


            APPData.USER_LOGIN = read_setup_record("UserLogin", APPData.USER_LOGIN);
            APPData.KEEP_LOGGED = read_setup_record("KeepLogged", APPData.KEEP_LOGGED);
            APPData.AUTO_SYNC = read_setup_record("AutoSync", APPData.AUTO_SYNC);
            APPData.STORE_TYPED1 = read_setup_record("StoreTyped1", APPData.STORE_TYPED1);
            APPData.AVVIO_NUOVO_INTERVENTO = read_setup_record("AvvioNuovoIntervento", APPData.AVVIO_NUOVO_INTERVENTO);
            APPData.CLOSE_ON_NEW_INTERVENTO = read_setup_record("ChiudiNuovoIntervento", APPData.CLOSE_ON_NEW_INTERVENTO);




            // Ultimo ambiente : se valorizzato implica un crash
            try {

                APPData.LAST_ENV = read_setup_record("LastEnviroment", "");

                APPData.LAST_ENV_ON_DWG = read_setup_record("LastEnviromentOnDWG", "");
                APPData.LAST_ENV_ON_DWG_PARAM = read_setup_record("LastEnviromentOnDWGParam", "");
                APPData.LAST_ENV_ON_DWG_PARAM1 = read_setup_record("LastEnviromentOnDWGParam1", "");
                APPData.LAST_ENV_ON_DWG_PARAM2 = read_setup_record("LastEnviromentOnDWGParam2", "");
                APPData.LAST_ENV_ON_DWG_PARAM3 = read_setup_record("LastEnviromentOnDWGParam3", "");
                APPData.LAST_ENV_ON_DWG_PARAM4 = read_setup_record("LastEnviromentOnDWGParam4", "");
                APPData.LAST_ENV_ON_DWG_PARAM5 = read_setup_record("LastEnviromentOnDWGParam5", "");
                APPData.LAST_ENV_ON_DWG_PARAM6 = read_setup_record("LastEnviromentOnDWGParam6", "");
                APPData.LAST_ENV_ON_DWG_PARAM7 = read_setup_record("LastEnviromentOnDWGParam7", "");
                APPData.LAST_ENV_ON_DWG_PARAM8 = read_setup_record("LastEnviromentOnDWGParam8", "");

                APPData.LAST_ENV_CAMERA_X = read_setup_record("LastEnviromentCamX", "");
                APPData.LAST_ENV_CAMERA_Y = read_setup_record("LastEnviromentCamY", "");
                APPData.LAST_ENV_CAMERA_WX = read_setup_record("LastEnviromentCamWX", "");
                APPData.LAST_ENV_CAMERA_WY = read_setup_record("LastEnviromentCamWY", "");

            } catch (Exception e) { }




            if (BUILD_DEBUG_DATA) {
                fill_by_sample_data();
            }






            try {
                Constants.SERVER_PROTOCOL = read_setup_record("SERVER_PROTOCOL", "http://");
            } catch (Exception e) { }
            try {
                Constants.SERVER_URL = read_setup_record("SERVER_URL", "dkmap.888sp.com/services/pamap");
            } catch (Exception e) { }
            try {
                Constants.SERVER_PORT = Integer.valueOf(read_setup_record("SERVER_PORT", "0"));
            } catch (Exception e) {
            if (Constants.SERVER_PORT < 0) Constants.SERVER_PORT = 0;
            }


            return 1;

        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

        return 0;
    }




    public int check_setup() throws SQLiteException {
        if (APPData.InterventiNID <= 0) {
            APPData.InterventiNID = 1;
        }

        /*
        if (Constants.IntervemtiNID != null) {
            if (Constants.IntervemtiNID.intValue() > APPData.InterventiNID) {
                APPData.InterventiNID = Constants.IntervemtiNID.intValue();
            } else if (Constants.IntervemtiNID.intValue() < APPData.InterventiNID) {
                Constants.IntervemtiNID = APPData.InterventiNID;
                APPPRefs.save_shared_app_prefs(null);
            } else {
                // OK
            }

        } else {
            Constants.IntervemtiNID = APPData.InterventiNID;
            APPPRefs.save_shared_app_prefs(null);
        }
        */


        return 1;
    }


    public void fill_by_sample_data() {

        // Inserimento valori x TEST in Tabella COMPLESSO
        ContentValues values = new ContentValues();
        values.put("IDCOMPLESSO","1");
        values.put("CDCOMPLESSO","Complesso A");
        db.insert("COMPLESSO","",values);


        values.clear();
        values.put("IDCOMPLESSO","2");
        values.put("CDCOMPLESSO","Complesso B");
        db.insert("COMPLESSO","",values);


        // Inserimento valori x TEST in Tabella EDIFICIO
        values.clear();
        values.put("IDCOMPLESSO","1");
        values.put("IDEDIFICIO","1");
        values.put("CDEDIFICIO","Edificio 1A");
        db.insert("EDIFICIO","",values);

        values.clear();
        values.put("IDCOMPLESSO","1");
        values.put("IDEDIFICIO","2");
        values.put("CDEDIFICIO","Edificio 2A");
        db.insert("EDIFICIO","",values);

        values.clear();
        values.put("IDCOMPLESSO","2");
        values.put("IDEDIFICIO","3");
        values.put("CDEDIFICIO","Edificio 1B");
        db.insert("EDIFICIO","",values);

        values.clear();
        values.put("IDCOMPLESSO","2");
        values.put("IDEDIFICIO","4");
        values.put("CDEDIFICIO","Edificio 2B");
        db.insert("EDIFICIO","",values);

        values.clear();
        values.put("IDCOMPLESSO","2");
        values.put("IDEDIFICIO","5");
        values.put("CDEDIFICIO","Edificio 3B");
        db.insert("EDIFICIO","",values);




        // Inserimento valori x TEST in Tabella PIANO
        values.clear();
        values.put("IDPIANO","1");
        values.put("IDEDIFICIO","1");
        values.put("CDPIANO","Piano T@1 [1]");
        db.insert("PIANO","",values);

        values.clear();
        values.put("IDPIANO","2");
        values.put("IDEDIFICIO","1");
        values.put("CDPIANO","Piano M@1 [2]");
        db.insert("PIANO","",values);

        values.clear();
        values.put("IDPIANO","3");
        values.put("IDEDIFICIO","2");
        values.put("CDPIANO","Piano 1@2 [3]");
        db.insert("PIANO","",values);

        values.clear();
        values.put("IDPIANO","4");
        values.put("IDEDIFICIO","2");
        values.put("CDPIANO","Piano 2@2 [4]");
        db.insert("PIANO","",values);

        values.clear();
        values.put("IDPIANO","5");
        values.put("IDEDIFICIO","2");
        values.put("CDPIANO","Piano 3@2 [5]");
        db.insert("PIANO","",values);


        // Inserimento valori x TEST in Tabella VANO
        values.clear();
        values.put("IDVANO","1");
        values.put("IDPIANO","1");
        values.put("CDVANO","Vano 1@1");
        db.insert("VANO","",values);

        values.clear();
        values.put("IDVANO","2");
        values.put("IDPIANO","1");
        values.put("CDVANO","Vano 2@1");
        db.insert("VANO","",values);

        values.clear();
        values.put("IDVANO","3");
        values.put("IDPIANO","2");
        values.put("CDVANO","Vano 1-2");
        db.insert("VANO","",values);

        values.clear();
        values.put("IDVANO","4");
        values.put("IDPIANO","3");
        values.put("CDVANO","Vano 1-3");
        db.insert("VANO","",values);

        values.clear();
        values.put("IDVANO","5");
        values.put("IDPIANO","3");
        values.put("CDVANO","Vano 2-3");
        db.insert("VANO","",values);

        values.clear();
        values.put("IDVANO","6");
        values.put("IDPIANO","3");
        values.put("CDVANO","Vano 3-3");
        db.insert("VANO","",values);



        // Inserimento valori x TEST in Tabella OGGETTI
        values.clear();
        values.put("IDOGGETTO","1");
        values.put("IDOGGETTO_P","");
        values.put("CODICE","LN12");
        values.put("DESCRIZIONE","Desc Oggetto 1");
        values.put("IDCOMPLESSO","1");
        values.put("IDEDIFICIO","1");
        values.put("IDPIANO","1");
        values.put("IDVANO","1");
        db.insert("OGGETTO","",values);

        values.clear();
        values.put("IDOGGETTO","2");
        values.put("IDOGGETTO_P","");
        values.put("CODICE","LN13");
        values.put("DESCRIZIONE","Desc Oggetto 2");
        values.put("IDCOMPLESSO","1");
        values.put("IDEDIFICIO","1");
        values.put("IDPIANO","1");
        values.put("IDVANO","1");
        db.insert("OGGETTO","",values);

        values.clear();
        values.put("IDOGGETTO","3");
        values.put("IDOGGETTO_P","");
        values.put("CODICE","LN14");
        values.put("DESCRIZIONE","Desc Oggetto 10");
        values.put("IDCOMPLESSO","1");
        values.put("IDEDIFICIO","1");
        values.put("IDPIANO","1");
        values.put("IDVANO","2");
        db.insert("OGGETTO","",values);

        values.clear();
        values.put("IDOGGETTO","4");
        values.put("IDOGGETTO_P","");
        values.put("CODICE","LN15");
        values.put("DESCRIZIONE","Desc Oggetto 11");
        values.put("IDCOMPLESSO","1");
        values.put("IDEDIFICIO","1");
        values.put("IDPIANO","1");
        values.put("IDVANO","2");
        db.insert("OGGETTO","",values);

        values.clear();
        values.put("IDOGGETTO","5");
        values.put("IDOGGETTO_P","");
        values.put("CODICE","LN16");
        values.put("DESCRIZIONE","Desc Oggetto 12");
        values.put("IDCOMPLESSO","1");
        values.put("IDEDIFICIO","1");
        values.put("IDPIANO","1");
        values.put("IDVANO","2");
        db.insert("OGGETTO","",values);

        values.clear();
        values.put("IDOGGETTO","6");
        values.put("IDOGGETTO_P","");
        values.put("CODICE","LN17");
        values.put("DESCRIZIONE","Desc Oggetto A2");
        values.put("IDCOMPLESSO","1");
        values.put("IDEDIFICIO","1");
        values.put("IDPIANO","1");
        values.put("IDVANO","3");
        db.insert("OGGETTO","",values);

        values.clear();
        values.put("IDOGGETTO","7");
        values.put("IDOGGETTO_P","");
        values.put("CODICE","LN18");
        values.put("DESCRIZIONE","Desc Oggetto A3");
        values.put("IDCOMPLESSO","1");
        values.put("IDEDIFICIO","1");
        values.put("IDPIANO","1");
        values.put("IDVANO","3");
        db.insert("OGGETTO","",values);





        // Inserimento valori x TEST in DWG
        values.clear();
        values.put("IDDWG","1");
        values.put("IDPIANO","1");
        values.put("DWGNAME","Dwg 1");
        values.put("DWGVERSION","1");
        values.put("DWGDATA","");
        db.insert("DWG","",values);

        values.clear();
        values.put("IDDWG","2");
        values.put("IDPIANO","2");
        values.put("DWGNAME","Dwg 2");
        values.put("DWGVERSION","1");
        values.put("DWGDATA","");
        db.insert("DWG","",values);

        values.clear();
        values.put("IDDWG","3");
        values.put("IDPIANO","3");
        values.put("DWGNAME","Dwg 3");
        values.put("DWGVERSION","1");
        values.put("DWGDATA","");
        db.insert("DWG","",values);



        values.clear();
        values.put("IDINTERVENTO","1");
        values.put("CDINTERVENTO","Intervento 1");
        values.put("DSINTERVENTO","Descrizione Intervento 1");
        values.put("DSESTESA","Descrizione Estesa Intervento 1");
        values.put("IDOGGETTO","1");
        db.insert("INTERVENTI","",values);

        values.clear();
        values.put("IDINTERVENTO","2");
        values.put("CDINTERVENTO","Intervento 2");
        values.put("DSINTERVENTO","Descrizione Intervento 2");
        values.put("DSESTESA","Descrizione Estesa Intervento 2");
        values.put("IDOGGETTO","2");
        db.insert("INTERVENTI","",values);

        values.clear();
        values.put("IDINTERVENTO","3");
        values.put("CDINTERVENTO","Intervento 3");
        values.put("DSINTERVENTO","Descrizione Intervento 3");
        values.put("DSESTESA","Descrizione Estesa Intervento 3");
        values.put("IDOGGETTO","2");
        db.insert("INTERVENTI","",values);

        values.clear();
        values.put("IDINTERVENTO","4");
        values.put("CDINTERVENTO","Intervento 4");
        values.put("DSINTERVENTO","Descrizione Intervento 4");
        values.put("DSESTESA","Descrizione Estesa Intervento 4");
        values.put("IDOGGETTO","3");
        db.insert("INTERVENTI","",values);

        values.clear();
        values.put("IDINTERVENTO","5");
        values.put("CDINTERVENTO","Intervento 5");
        values.put("DSINTERVENTO","Descrizione Intervento 5");
        values.put("DSESTESA","Descrizione Estesa Intervento 5");
        values.put("IDOGGETTO","4");
        db.insert("INTERVENTI","",values);

        values.clear();
        values.put("IDINTERVENTO","6");
        values.put("CDINTERVENTO","Intervento 6");
        values.put("DSINTERVENTO","Descrizione Intervento 6");
        values.put("DSESTESA","Descrizione Estesa Intervento 6");
        values.put("IDOGGETTO","5");
        db.insert("INTERVENTI","",values);

        values.clear();
        values.put("IDINTERVENTO","7");
        values.put("CDINTERVENTO","Intervento 7");
        values.put("DSINTERVENTO","Descrizione Intervento 7");
        values.put("DSESTESA","Descrizione Estesa Intervento 7");
        values.put("IDOGGETTO","6");
        db.insert("INTERVENTI","",values);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public void close() {
        db.close();
        db = null;
    }



    public void execute(String sqlStatement) {
    if (db!=null) db.execSQL(sqlStatement);
    }

}
