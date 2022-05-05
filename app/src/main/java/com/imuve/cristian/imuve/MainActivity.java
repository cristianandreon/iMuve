package com.imuve.cristian.imuve;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.almworks.sqlite4java.SQLiteException;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static com.imuve.cristian.imuve.APPData.init_app_data;
import static com.imuve.cristian.imuve.Constants.InitConstants;



public class MainActivity extends Activity {

    public static Context context;
    public static MainActivity instance;
    public static MyRenderer myRenderer;
    public static DWGViewerActivity dwgViewerActivity;

    public static SQLiteWrapper sqliteWrapper = null;
    public static SQLiteDatabase.CursorFactory sqliteCurFactory = null;
    public static NetworkActivity networkActivity = null;
    public static Activity mActivity;

    public static GPSManager gpsManager = null;

    public static int ListViewInterventyType = R.layout.rec_lv_3fields;


    static {
        try {
            // Load necessary libraries.
            System.loadLibrary("imuvecpp");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library link error : "+e.getMessage());
        } catch (Throwable t){
            System.err.println("Native code library error : "+t.getMessage());
        }
    }






    @Override
    protected void onResume() {
        super.onResume();

        ///////////////////////////////
        // Exit request from login
        //
        if (APPData.LOGGED_IN == -9) {
            // Toast.makeText(context, "iMuve v1.01 closing...", Toast.LENGTH_LONG).show();
            APPData.LOGGED_IN = 0;
            finish();
            return;
        }

        this.getWindow().getDecorView().setAlpha(1.0f);

        refresh_fields ();

        if (APPData.LOGGED_IN == 1) {
            if (APPData.LAST_ENV == null || APPData.LAST_ENV.isEmpty()) {
                APPData.LAST_ENV = "MAIN";
                sqliteWrapper.update_setup_record("LastEnviroment", APPData.LAST_ENV);
            } else {
                // this.runOnUiThread ( new Runnable() { @Override public void run() { recoveryFromCrash(); } });
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable recoveryFromCrash_runnable = new Runnable() {
                    @Override
                    public void run() {
                        recoveryFromCrash();
                    }
                };
                mainHandler.post(recoveryFromCrash_runnable);
            }
        }




        // A volte on create non viene chiamato
        // if (APPData.LOGGED_IN == 1) {

        startup_list_view();


        /////////////////////////////////
        // Verifica presenza dati
        //
        if (APPData.LOGGED_IN == 1) {
            if (APPData.NumComplessi <= 0) {
                if (APPComplessiSQL.sincronizza_complessi("", getApplicationContext(), MainActivity.this, null) > 0) {
                    APPData.cComplesso = -1;
                    setup_listview_complessi(this, R.id.lvComplessi, -1, false, 0);
                    set_list_view_as_current(R.id.lvComplessi);
                }
            }
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        exitApp(null);
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /*
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        */

        instance = this;

        context = this.getApplicationContext();

        mActivity = this;


        if (!APPData.Initialized) {
            // Setup Costanti
            InitConstants(context);

            // Setup Dati applicazione
            init_app_data();

            // Istanza classe SQLITE
            if (sqliteWrapper == null)
                sqliteWrapper = new SQLiteWrapper(context, "imuve", sqliteCurFactory, 1);

            // Istanza classe NextworkActivity
            if (networkActivity == null) networkActivity = new NetworkActivity();


            if (gpsManager == null) gpsManager = new GPSManager(context);



            //////////////////////////////
            // Lettura dati setup
            //
            try {
                sqliteWrapper.read_setup();
            } catch (SQLiteException e) {
                e.printStackTrace();
            }

            // Controllo
            try {
                sqliteWrapper.check_setup();
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }


        // Poltitica di gestione Thread/UI
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        ///////////////////////////////
        // Exif request from login
        //
        if (APPData.LOGGED_IN == -9) {
            // Toast.makeText(context, "iMuve v1.01 closing...", Toast.LENGTH_LONG).show();
            APPData.LOGGED_IN = 0;
            finish();
            return;
        }


        try {

            setContentView(R.layout.activity_main);

            int res = 0;

            // Versione della parte in C/C++
            Constants.CPP_VERSION = GetVersion();


            refresh_fields();


            Button button = (Button) findViewById(R.id.btOpenDWG);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        ResetCamera();
                        openDWG(v, false, true, APPData.cPiano, false, false, null, MainActivity.this, context, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            button = (Button) findViewById(R.id.btLocateVano);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        ResetCamera();
                        openDWG(v, false, true, APPData.cPiano, true, false, APPData.CodVani.get(APPData.cVano), MainActivity.this, context, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            button = (Button) findViewById(R.id.btLocateOggetto);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        ResetCamera();
                        openDWG(v, false, true, APPData.cPiano, false, true, APPData.appOggetti.CodOggetti.get(APPData.appOggetti.cOggetto), MainActivity.this, context, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            button = (Button) findViewById(R.id.btLogOut);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    logOut(v);
                }
            });

            button = (Button) findViewById(R.id.btExit);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    exitApp(v);
                }
            });


            button = (Button) findViewById(R.id.opendwg);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    openDWG(v, false, true, APPData.cPiano, false, false, null, MainActivity.this, context, null);
                }
            });
            if (!Constants.BUTTONS_AS_DEBUG) button.setVisibility(View.INVISIBLE);

            button = (Button) findViewById(R.id.canvas);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getCanvas(v);
                }
            });
            if (!Constants.BUTTONS_AS_DEBUG) button.setVisibility(View.INVISIBLE);

            button = (Button) findViewById(R.id.xml);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getXML(v);
                }
            });
            if (!Constants.BUTTONS_AS_DEBUG) button.setVisibility(View.INVISIBLE);

            button = (Button) findViewById(R.id.keyTexts);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getKeyTexts(v);
                }
            });
            if (!Constants.BUTTONS_AS_DEBUG) button.setVisibility(View.INVISIBLE);

            button = (Button) findViewById(R.id.json);
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        getJSON(v);
                    }
                });
                if (!Constants.BUTTONS_AS_DEBUG) button.setVisibility(View.INVISIBLE);
            }

            button = (Button) findViewById(R.id.testPDFViewer);
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        testPDFViewer();
                    }
                });
                if (!Constants.BUTTONS_AS_DEBUG) button.setVisibility(View.INVISIBLE);
            }

            button = (Button) findViewById(R.id.btExportDB);
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        ExportDB();
                    }
                });
                if (!Constants.BUTTONS_AS_DEBUG) button.setVisibility(View.INVISIBLE);
            }





                    button = (Button) findViewById(R.id.btInterventiManage);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    run_interventi_manage_activity(context, mActivity, null, null, null, null);
                }
            });



            /*
            button = (Button) findViewById(R.id.btShowMap);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    run_map_activity(context, mActivity, null, null, null, null);
                }
            });
            */




            button = (Button) findViewById(R.id.btSyncComplessi);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (DialogBox.DialogBox("ATTENZIONE", "Sincronizzare la tabella Complessi ?", 0 + 1 + 2, MainActivity.this)) {
                        if (APPComplessiSQL.sincronizza_complessi("", getApplicationContext(), MainActivity.this, null) > 0) {
                            APPData.cComplesso = 0;
                            setup_listview_complessi(mActivity, R.id.lvComplessi, -1, false, 0);
                            set_list_view_as_current(R.id.lvComplessi);
                        }
                    }
                }
            });

            button = (Button) findViewById(R.id.btSyncEdifici);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (APPData.cComplesso >= 0 && APPData.cComplesso <= APPData.NumComplessi) {
                        if (DialogBox.DialogBox("ATTENZIONE", "Sincronizzare la tabella Edifici ?", 0 + 1 + 2, MainActivity.this)) {
                            int fltID = APPData.cComplesso >= 0 ? APPData.IDComplessi.get(APPData.cComplesso) : -1;
                            if (APPEdificiSQL.sincronizza_edifici(String.valueOf(fltID), getApplicationContext(), MainActivity.this, null) > 0) {
                                APPData.cEdificio = -1;
                                setup_listview_edifici(mActivity, R.id.lvEdifici, -1, false, 0);
                                set_list_view_as_current(R.id.lvEdifici);
                            }
                        }
                    }
                }
            });

            button = (Button) findViewById(R.id.btSyncPiani);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (APPData.cEdificio >= 0 && APPData.cEdificio <= APPData.NumEdifici) {
                        if (DialogBox.DialogBox("ATTENZIONE", "Sincronizzare la tabella Piani ?", 0 + 1 + 2, MainActivity.this)) {
                            int fltID = APPData.cEdificio >= 0 ? APPData.IDEdifici.get(APPData.cEdificio) : -1;
                            if (APPPianiSQL.sincronizza_piani(String.valueOf(fltID), getApplicationContext(), MainActivity.this, null) > 0) {
                                APPData.cPiano = -1;
                                setup_listview_piani(mActivity, R.id.lvPiani, -1, false, 0);
                                set_list_view_as_current(R.id.lvPiani);
                            }
                        }
                    }
                }
            });

            button = (Button) findViewById(R.id.btSyncVani);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (APPData.cPiano >= 0 && APPData.cPiano <= APPData.NumPiani) {
                        if (DialogBox.DialogBox("ATTENZIONE", "Sincronizzare la tabella Vani ?", 0 + 1 + 2, MainActivity.this)) {
                            int fltID = APPData.cPiano >= 0 ? APPData.IDPiani.get(APPData.cPiano) : -1;
                            if (APPVaniSQL.sincronizza_vani(String.valueOf(fltID), getApplicationContext(), MainActivity.this, null) > 0) {
                                APPData.cVano = -1;
                                setup_listview_vani(mActivity, R.id.lvVani, -1, false, 0);
                                set_list_view_as_current(R.id.lvVani);
                            }
                        }
                    }
                }
            });

            button = (Button) findViewById(R.id.btSyncDwg);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (APPData.cPiano >= 0 && APPData.cPiano <= APPData.NumPiani) {
                        if (DialogBox.DialogBox("ATTENZIONE", "Sincronizzare il disegno del piano "+APPData.PianiDesc.get(APPData.cPiano)+" ?", 0 + 1 + 2, MainActivity.this)) {
                            int fltID = APPData.cPiano >= 0 ? APPData.IDPiani.get(APPData.cPiano) : -1;
                            ProgressDialog progress = null;
                            SYNCTable syncTable = null;

                            try {
                                progress = new ProgressDialog(MainActivity.this);
                                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progress.setMessage("Apertura disegno in corso...");
                                progress.setIndeterminate(true);
                                progress.setCancelable(false);
                                progress.show();

                                syncTable = new SYNCTable();
                                syncTable.table_name = "Disegni";
                                syncTable.view = v;
                                syncTable.activity = MainActivity.this;
                                syncTable.context = getApplicationContext();
                                syncTable.progress = progress;

                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }

                            if (APPDwgSQL.sincronizza_dwg(String.valueOf(fltID), getApplicationContext(), MainActivity.this, syncTable) > 0) {
                                APPData.cVano = -1;
                                setup_listview_vani(mActivity, R.id.lvVani, -1, false, 0);
                                set_list_view_as_current(R.id.lvVani);
                            }

                            progress.dismiss();
                        }
                    }
                }
            });

            button = (Button) findViewById(R.id.btSyncOggetti);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (APPData.cVano >= 0 && APPData.cVano <= APPData.NumVani) {
                        if (DialogBox.DialogBox("ATTENZIONE", "Sincronizzare la tabella Oggetti ?", 0 + 1 + 2, MainActivity.this)) {
                            int fltID = APPData.cVano >= 0 ? APPData.IDVani.get(APPData.cVano) : -1;
                            if (APPOggettiSQL.sincronizza_oggetti(null, null, null, String.valueOf(fltID), getApplicationContext(), MainActivity.this, null) > 0) {
                                APPData.appOggetti.cOggetto = -1;
                                setup_listview_oggetti(mActivity, R.id.lvOggetti, -1, false, 0);
                                set_list_view_as_current(R.id.lvOggetti);
                            }
                        }
                    }
                }
            });

            button = (Button) findViewById(R.id.btSyncInterventi);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (APPData.appOggetti.cOggetto >= 0 && APPData.appOggetti.cOggetto <= APPData.appOggetti.NumOggetti) {
                        if (DialogBox.DialogBox("ATTENZIONE", "Sincronizzare la tabella Interventi ?", 0 + 1 + 2, MainActivity.this)) {

                            if (APPData.DEVICE_PKPOOL == null) {
                                // Rilettura profilo
                                LoginActivity loginActivity = new LoginActivity();
                                JSONParser jsonParser = new JSONParser(null);
                                loginActivity.onReadUserProfile(jsonParser);
                            }

                            int fltOggettoID = APPData.appOggetti.cOggetto >= 0 ? APPData.appOggetti.IDOggetti.get(APPData.appOggetti.cOggetto) : -1;


                            //////////////////////////////////////////////////////////
                            // Invio interventi presenti nel tablet
                            //
                            for (int i=0; i< APPData.appInterventi.NumInterventi; i++) {
                                Integer fltQueueID = APPData.appInterventi.IDInterventiOnQueue.get(i);
                                String fltInteventoID = APPData.appInterventi.IDInterventi.get(i);
                                if (fltQueueID.intValue() > 0) {
                                    if (APPQueueSQL.de_queue(null, "SERVER", fltQueueID, fltInteventoID) != 1) {
                                        // DialogBox.DialogBox("ATTENZIONE", "Sincronizzazione Intervento fallita!", 0 + 0, APPInterventiActivity.this);
                                    } else {
                                    }
                                }
                            }

                            //////////////////////////////////////////////////////////
                            // Lettura eventuali interventi presenti nel server
                            //
                            if (APPInterventiSQL.sincronizza_interventi(null, null, null, null, String.valueOf(fltOggettoID), getApplicationContext(), MainActivity.this, null) > 0) {
                                APPData.appInterventi.cIntervento = -1;
                            }

                            //////////////////////////////////////////////////////////
                            // Lettura eventuali interventi presenti nel server
                            //
                            if (APPInterventiSQL.leggi_interventi(null, null, null, null, fltOggettoID, APPData.appInterventi) < 0) {
                            }

                            setup_listview_interventi(mActivity, R.id.lvInterventi, -1, false, ListViewInterventyType);
                            set_list_view_as_current(R.id.lvInterventi);
                        }
                    }
                }
            });


            button = (Button) findViewById(R.id.btAddInterventi);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (APPData.appOggetti.cOggetto >= 0 && APPData.appOggetti.cOggetto <= APPData.appOggetti.NumOggetti) {
                        aggiungiIntervento(context);
                    }
                }
            });



            /*
            button = (Button) findViewById(R.id.testparser);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    testXMLParser(v);
                }
            });
            */

            button = (Button) findViewById(R.id.btViewTables);
            if(button!=null) button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    viewTables(v);
                }
            });

            button = (Button) findViewById(R.id.btSettings);
            if(button!=null) button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    openSettings(v);
                }
            });



            // overridePendingTransition(R.anim.abc_shrink_fade_out_from_bottom, R.anim.abc_shrink_fade_out_from_bottom);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


        View backgroundimage = findViewById(R.id.background);
        if (backgroundimage != null) {
            Drawable background = backgroundimage.getBackground();
            background.setAlpha(40);
        }


        /////////////////////////
        // Start with login
        //
        if (APPData.LOGGED_IN <= 0) {
            try {
                if (APPData.LAST_ENV == null || APPData.LAST_ENV.isEmpty()) {
                } else {
                }

                this.getWindow().getDecorView().setAlpha(0.2f);

                Intent myIntent = new Intent(this, LoginActivity.class);
                myIntent.putExtra("key", "value"); //Optional parameters
                startActivity(myIntent);

                return;

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            // Utente loggato : Avvio scodatore
            if (APPData.AUTO_SYNC.compareToIgnoreCase("0") != 0) {
                APPData.de_queue_loop();
            }
        }


        ////////////////////////////////////
        // Recupero ambiente
        //

        try {

            // Eseguito dalla onResume
            // recoveryFromCrash();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }


    private void refresh_fields ( ) {

    TextView tv = (TextView)findViewById(R.id.tv1);
    if(tv!=null)tv.setText("iMuve ver.: "+Constants.getVersion());

    tv=(TextView)findViewById(R.id.tvDebug);
    if(tv!=null)tv.setText("PK:"+(APPData.DEVICE_PKPOOL!=null?APPData.DEVICE_PKPOOL:"[!]")+" - "+"NID: "+APPData.InterventiNID + (APPData.DEVICE_ID_COMPLESSO != null ? (" - C:" + APPData.DEVICE_ID_COMPLESSO) : ""));
    // if (!Constants.BUTTONS_AS_DEBUG) tv.setVisibility(View.INVISIBLE);


    // Nome utente Autenticato
    tv=(TextView)findViewById(R.id.tvWellcome);
    if(tv!=null)tv.setText("Benvenuto");

    tv=(TextView)findViewById(R.id.tvUserName);
    if(tv!=null) tv.setText(APPData.USER_LOGIN);
}




    private class CheckTableDataParam {
        View view = null;
        ProgressDialog progress = null;

        boolean openWhenDone = true;
        String Message = null;
        Integer RetVal = 0;
    }

    CheckTableDataParam checkTableDataParam = new CheckTableDataParam();

    private boolean on_check_tables_data (boolean bAsync) {


        if ((APPData.NumEdifici <= 0 && APPData.cComplesso >= 0 && APPData.cComplesso < APPData.NumComplessi)
                || (APPData.NumPiani <= 0 && APPData.cComplesso >= 0 && APPData.cComplesso < APPData.NumComplessi && APPData.cEdificio >= 0 && APPData.cEdificio < APPData.NumEdifici)
                ) {
            if (NetworkActivity.isOnline() > 0) {

                if (DialogBox.DialogBox("ATTENZIONE", "Dati operativi non definiti\r\nPer proseguire è necessario sincronizzare le tabelle\r\nL'operazione potrebbe richiedere diversi minuti...\n\nEsesuire l'operazione ora ?", 0 + 1 + 2, MainActivity.this)) {

                        try {

                            if (bAsync) {

                                checkTableDataParam.progress = new ProgressDialog(this);

                                checkTableDataParam.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                checkTableDataParam.progress.setMessage("Sincronizzazione in corso...");
                                checkTableDataParam.progress.setIndeterminate(true);
                                checkTableDataParam.progress.setCancelable(false);
                                checkTableDataParam.progress.show();

                                Thread mThread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            do_check_tables_data();
                                        } catch (Exception e) {
                                            checkTableDataParam.RetVal = -20;
                                            checkTableDataParam.Message = "Exception error:"+e.getMessage();
                                        }
                                        try {
                                            checkTableDataParam.progress.dismiss();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Handler mainHandler = new Handler(context.getMainLooper());
                                        Runnable myRunnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                done_check_tables_data();
                                            }
                                        };

                                        mainHandler.post(myRunnable);
                                    }
                                };

                                mThread.start();

                            } else {
                                do_check_tables_data();
                                done_check_tables_data();
                            }
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
            }
        return true;
    }



    private boolean do_check_tables_data () {

        if (NetworkActivity.isOnline() > 0) {

            String fltIDComplesso = String.valueOf(APPData.IDComplessi.get(APPData.cComplesso));

            update_progress_message(checkTableDataParam.progress, "Sincronizzazione edifici...");

            if (APPEdificiSQL.sincronizza_edifici(fltIDComplesso, getApplicationContext(), MainActivity.this, null) > 0) {
                APPData.cEdificio = 0;
            } else {
                return false;
            }


            update_progress_message(checkTableDataParam.progress, "Lettura edifici...");

            APPEdificiSQL.leggi_edifici(Integer.valueOf(fltIDComplesso));

            for (int i_edificio=0; i_edificio<APPData.NumEdifici; i_edificio++) {
                Integer fltEdificio = APPData.IDEdifici.get(i_edificio);
                if (APPPianiSQL.sincronizza_piani(String.valueOf(fltEdificio), getApplicationContext(), MainActivity.this, null) > 0) {
                    APPData.cPiano = 0;
                } else {
                    // Fallita sincronizzazione piani
                }

                // Lettura Piani
                APPPianiSQL.leggi_piani(fltEdificio);

                update_progress_message(checkTableDataParam.progress, "Sincronizzazione edificio " + (i_edificio + 1) + "/" + APPData.NumEdifici + " ...");

                for (int i_piano = 0; i_piano < APPData.NumPiani; i_piano++) {

                    Integer id_piano = APPData.IDPiani.get(i_piano);

                    // Sincronizzazione dwg
                    update_progress_message(checkTableDataParam.progress, "Sincronizzazione dwg sul piano "+(i_piano+1)+"/"+APPData.NumPiani+" dell'edificio " + (i_edificio + 1) + "/" + APPData.NumEdifici + " ...");
                    if (APPDwgSQL.sincronizza_dwg(String.valueOf(id_piano), null, null, null) > 0) {
                    } else {
                        // Fallita sincronizzazione
                    }

                    // Sincronizzazione vani
                    update_progress_message(checkTableDataParam.progress, "Sincronizzazione vani sul piano "+(i_piano+1)+"/"+APPData.NumPiani+" dell'edificio " + (i_edificio + 1) + "/" + APPData.NumEdifici + " ...");
                    if (APPVaniSQL.sincronizza_vani(String.valueOf(id_piano), null, null, null) > 0) {
                    } else {
                        // Fallita sincronizzazione
                    }

                    // Sincronizzazione oggetti
                    update_progress_message(checkTableDataParam.progress, "Sincronizzazione oggetti sul piano "+(i_piano+1)+"/"+APPData.NumPiani+" dell'edificio " + (i_edificio + 1) + "/" + APPData.NumEdifici + " ...");
                    if (APPOggettiSQL.sincronizza_oggetti(null, null, String.valueOf(id_piano), null, null, null, null) > 0) {
                    } else {
                        // Fallita sincronizzazione
                    }
                }
            }

            update_progress_message(checkTableDataParam.progress, "Sincronizzazione interventi...");

            // Sincronizzazione interventi
            if (APPInterventiSQL.sincronizza_interventi(fltIDComplesso, null, null, null, null, null, null, null) > 0) {
            } else {
                // Fallita sincronizzazione
            }


            // APPData.cComplesso = -1;
            APPData.cEdificio = -1;
            APPData.cPiano = -1;
            APPData.cVano = -1;
            APPData.appOggetti.cOggetto = -1;
            APPData.appInterventi.cIntervento = -1;


            update_progress_message(checkTableDataParam.progress, "Eseguito");

            ///////////////////////////////////
            // Creazione pool di chiavi ?????
            //
            // APPPkPool.scrivi_pk_counter();

        }

        return true;
    }



    private boolean done_check_tables_data () {
        setup_listview_edifici(this, R.id.lvEdifici, -1, false, 0);
        setup_listview_piani(this, R.id.lvPiani, APPData.cPiano, false, 0);
        setup_listview_vani(this, R.id.lvVani, APPData.cVano, false, 0);
        setup_listview_oggetti(this, R.id.lvOggetti, APPData.appOggetti.cOggetto, false, 0);
        setup_listview_interventi(this, R.id.lvInterventi, APPData.appInterventi.cIntervento, false, ListViewInterventyType);
        return true;
    }




    private void update_progress_message ( final ProgressDialog progressDialog, final String msg ) {
        Handler mainHandler = new Handler(context.getMainLooper());
        Runnable update_progress_message_runnable = new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && msg != null) progressDialog.setMessage(msg);
            }
        };
        mainHandler.post(update_progress_message_runnable);
    }









    /////////////////////////////////////////
    // Funzione Vista Gestione Interventi
    //

    public void run_interventi_manage_activity ( Context context, final Activity activity,
                                                String pURL, String pGpsLong, String pGpsLat, String pMapZoom ) {

        try {

            Intent myIntent = new Intent(activity, APPInterventiManageActivity.class);

            // myIntent.putExtra("Url", pURL != null ? pURL : "http://customers.cristianandreon.com/Geisoft/testmap3.html");

            startActivity(myIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /////////////////////////////////////////
    // Funzione Vista su Mappa
    //

    public void run_map_activity ( Context context, final Activity activity,
                                   String pURL, String pGpsLong, String pGpsLat, String pMapZoom ) {

        try {

            getWindow().getDecorView().setAlpha(0.3f);

            Intent myIntent = new Intent(activity, OSMViewActivity.class);

            myIntent.putExtra("Url", pURL != null ? pURL : "http://customers.cristianandreon.eu/Geisoft/testmap3.html");

            myIntent.putExtra("gpsLong", pGpsLong!=null?pGpsLong:APPData.GpsLongEdifici.get(APPData.cEdificio));

            myIntent.putExtra("gpsLat", pGpsLat!=null?pGpsLat:APPData.GpsLatEdifici.get(APPData.cEdificio));

            myIntent.putExtra("mapZoom", pMapZoom!=null?pMapZoom:"17.0");

            startActivity(myIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /////////////////////////////////////////
    // Funzione Avvio activity Interventy
    //

    public void run_interventi_activity ( Context context, final Activity activity ) {

        try {

            MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
            Handler mainHandler = new Handler(context.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {

                    try {
                        APPData.LAST_SEL_IDOGGETTO = Integer.valueOf(APPData.LAST_ENV_ON_DWG_PARAM);
                        APPData.LAST_SEL_IDCOMPLESSO = Integer.valueOf(APPData.LAST_ENV_ON_DWG_PARAM4);
                        APPData.LAST_SEL_IDEDIFICIO = Integer.valueOf(APPData.LAST_ENV_ON_DWG_PARAM5);
                        APPData.LAST_SEL_IDPIANO = Integer.valueOf(APPData.LAST_ENV_ON_DWG_PARAM6);
                        APPData.LAST_SEL_IDVANO = Integer.valueOf(APPData.LAST_ENV_ON_DWG_PARAM7);
                    } catch (Exception e) {
                    }


                    try {
                        APPData.LAST_SEL_OGGETTO_DESC = APPData.appOggetti.DescOggetti.get(APPData.iIndexOf(APPData.appOggetti.IDOggetti, APPData.LAST_SEL_IDOGGETTO));
                    } catch (Exception e) {
                    }
                    try {
                        APPData.LAST_SEL_OGGETTO = APPData.appOggetti.CodOggetti.get(APPData.iIndexOf(APPData.appOggetti.IDOggetti, APPData.LAST_SEL_IDOGGETTO));
                    } catch (Exception e) {
                    }
                    try {
                        APPData.LAST_SEL_COMPLESSO = APPData.Complessi.get(APPData.iIndexOf(APPData.IDComplessi, APPData.LAST_SEL_IDCOMPLESSO));
                    } catch (Exception e) {
                    }
                    try {
                        APPData.LAST_SEL_EDIFICIO = APPData.Edifici.get(APPData.iIndexOf(APPData.IDEdifici, APPData.LAST_SEL_IDEDIFICIO));
                    } catch (Exception e) {
                    }
                    try {
                        APPData.LAST_SEL_PIANO = APPData.Piani.get(APPData.iIndexOf(APPData.IDPiani, APPData.LAST_SEL_IDPIANO));
                    } catch (Exception e) {
                    }
                    try {
                        APPData.LAST_SEL_VANO = APPData.Vani.get(APPData.iIndexOf(APPData.IDVani, APPData.LAST_SEL_IDVANO));
                    } catch (Exception e) {
                    }

                    getWindow().getDecorView().setAlpha(0.3f);

                    Intent myIntent = new Intent(activity, APPInterventiActivity.class);

                    try {
                        APPData.nuovoIntervento = Boolean.parseBoolean(APPData.LAST_ENV_ON_DWG_PARAM3);
                        myIntent.putExtra("ObjectID", APPData.LAST_SEL_IDOGGETTO);
                        myIntent.putExtra("ObjectCode", APPData.LAST_SEL_OGGETTO);
                        myIntent.putExtra("ObjectDesc", APPData.LAST_SEL_OGGETTO_DESC);
                        myIntent.putExtra("cIntervento", APPData.LAST_ENV_ON_DWG_PARAM2);
                        myIntent.putExtra("nuovoIntervento", APPData.LAST_ENV_ON_DWG_PARAM3);
                    } catch (Exception e) {
                    }

                    startActivity(myIntent);
                }
            };

            mainHandler.post(myRunnable);

        } catch(Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }



    /////////////////////////////////////////
    // Funzione Avvio activity Vani
    //
    public void run_vani_activity ( Context context, final Activity activity ) {

        try {

            MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
            Handler mainHandler = new Handler(context.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        APPData.LAST_SEL_VANO = APPData.LAST_ENV_ON_DWG_PARAM1;
                        APPData.LAST_SEL_IDVANO = Integer.valueOf(APPData.LAST_ENV_ON_DWG_PARAM);
                    } catch (Exception e) {
                    }

                    try {
                        APPData.LAST_SEL_IDCOMPLESSO = Integer.valueOf(APPData.LAST_ENV_ON_DWG_PARAM4);
                        APPData.LAST_SEL_IDEDIFICIO = Integer.valueOf(APPData.LAST_ENV_ON_DWG_PARAM5);
                        APPData.LAST_SEL_IDPIANO = Integer.valueOf(APPData.LAST_ENV_ON_DWG_PARAM6);
                        APPData.LAST_SEL_IDVANO = Integer.valueOf(APPData.LAST_ENV_ON_DWG_PARAM7);
                    } catch (Exception e) {
                    }

                    try {
                        APPData.LAST_SEL_COMPLESSO = APPData.Complessi.get(APPData.iIndexOf(APPData.IDComplessi, APPData.LAST_SEL_IDCOMPLESSO));
                    } catch (Exception e) {
                    }
                    try {
                        APPData.LAST_SEL_EDIFICIO = APPData.Edifici.get(APPData.iIndexOf(APPData.IDEdifici, APPData.LAST_SEL_IDEDIFICIO));
                    } catch (Exception e) {
                    }
                    try {
                        APPData.LAST_SEL_PIANO = APPData.Piani.get(APPData.iIndexOf(APPData.IDPiani, APPData.LAST_SEL_IDPIANO));
                    } catch (Exception e) {
                    }
                    try {
                        APPData.LAST_SEL_VANO = APPData.Vani.get(APPData.iIndexOf(APPData.IDVani, APPData.LAST_SEL_IDVANO));
                    } catch (Exception e) {
                    }

                    getWindow().getDecorView().setAlpha(0.3f);

                    Intent myIntent = new Intent(activity, APPVaniActivity.class);

                    try {
                        myIntent.putExtra("VanoID", APPData.LAST_ENV_ON_DWG_PARAM);
                        myIntent.putExtra("VanoCode", APPData.LAST_ENV_ON_DWG_PARAM1);
                        myIntent.putExtra("Key", ""); //Optional parameters
                    } catch (Exception e) {
                    }

                    startActivity(myIntent);
                }
            };

            mainHandler.post(myRunnable);
        } catch(Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }





    /////////////////////////////////////////
    // Funzione Avvio activity Interventy
    //
    public void run_gestione_interventi_activity ( Context context, final Activity activity ) {

        try {

            MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
            Handler mainHandler = new Handler(context.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {

                    getWindow().getDecorView().setAlpha(0.3f);

                    Intent myIntent = new Intent(activity, APPInterventiManageActivity.class);

                    try {
                        myIntent.putExtra("Key", ""); //Optional parameters
                    } catch (Exception e) {
                    }

                    startActivity(myIntent);
                }
            };

            mainHandler.post(myRunnable);
        } catch(Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }









    /////////////////////////////////////////
    // Funzione di Logout
    //
    private void logOut(View v) {

        LoginActivity.doLogOut();

        try {
            APPData.JUST_LOGGED_OFF = true;
            Intent myIntent = new Intent(this, LoginActivity.class);
            myIntent.putExtra("key", "value"); //Optional parameters
            startActivity(myIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int exitApp(View v) {

        if (DialogBox.DialogBox("ATTENZIONE", "Chiudere l'applicazione?", 0 + 1 + 2, MainActivity.this)) {

            try {

                Toast.makeText(context, "iMuve v1.01 closing...", Toast.LENGTH_LONG).show();

                // Salvataggio dati cfg
                APPData.save();

                try {
                    LoginActivity.doLogOut();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                finish();

                return 1;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }





    private void openSettings(View v) {

        try {
            getWindow().getDecorView().setAlpha(0.3f);
            Intent myIntent = new Intent(this, SettingsActivity.class);
            myIntent.putExtra("key", "value"); //Optional parameters
            startActivity(myIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void recoveryFromCrash() {
        if (APPData.LOGGED_IN == 1) {
            if (APPData.LAST_ENV.compareToIgnoreCase("DWG_VIEWER") == 0) {

                getWindow().getDecorView().setAlpha(0.2f);

                this.runOnUiThread ( new Runnable() { @Override public void run() { openDWG(getWindow().getDecorView(), true, true, APPData.cPiano, false, false, null, mActivity, context, null); } });

            } else {
                if (APPData.LAST_ENV_ON_DWG.compareToIgnoreCase("Interventi") == 0) {
                    this.getWindow().getDecorView().setAlpha(0.2f);

                    // Lettura delle listview
                    startup_list_view();

                    // Avvio activity
                    run_interventi_activity(context, MainActivity.this);

                } else if (APPData.LAST_ENV_ON_DWG.compareToIgnoreCase("GestioneInterventi") == 0) {

                    this.getWindow().getDecorView().setAlpha(0.2f);

                    // Avvio activity
                    run_gestione_interventi_activity(context, MainActivity.this);

                } else {
                }
            }
        }
    }












    private int[] lvObjectArray = {R.id.lvComplessi, R.id.lvEdifici, R.id.lvPiani, R.id.lvVani, R.id.lvOggetti, R.id.lvInterventi};
    private int[] lvSubObjectArray = {R.id.btSyncComplessi, R.id.btSyncEdifici, R.id.btSyncPiani, R.id.btSyncVani, R.id.btSyncOggetti, R.id.btSyncInterventi};
    private int[] lvSubObjectArray2 = {0, 0, 0, R.id.btSyncDwg, 0, R.id.btAddInterventi};
    private int mlvCurObject = 0;

    private int set_list_view_as_current(int lvCurObject) {
        int sdk = android.os.Build.VERSION.SDK_INT;

        if (mlvCurObject != lvCurObject) {
            mlvCurObject = lvCurObject;
            for (int i = 0; i < lvObjectArray.length; i++) {
                ListView lv = (ListView)findViewById(lvObjectArray[i]);
                ViewGroup.LayoutParams params = lv.getLayoutParams();
                Drawable bkRes = null;
                int visible = 0;

                if (lvCurObject == lvObjectArray[i]) {
                    lv.setAlpha(1.0f);
                    params.height = Constants.DEFAULT_LV_OPEN_ROW_HEIGHT;
                    visible = View.VISIBLE;
                    bkRes = getResources().getDrawable(R.drawable.shape2);

                } else {
                    params.height = Constants.DEFAULT_LV_CLOSED_ROW_HEIGHT;
                    lv.setAlpha(0.4f);
                    visible = View.INVISIBLE;
                }
                lv.setLayoutParams(params);

                try {
                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        lv.setBackgroundDrawable( bkRes );
                    } else {
                        lv.setBackground( bkRes );
                    }
                } catch (Exception e) {}


                if (lvSubObjectArray[i] != 0) {
                    Button bt = (Button)findViewById(lvSubObjectArray[i]);
                    if (bt != null) bt.setVisibility(visible);
                }

                if (lvSubObjectArray2[i] != 0) {
                    Button bt = (Button)findViewById(lvSubObjectArray2[i]);
                    if (bt != null) bt.setVisibility(visible);
                }
            }

            Button bt = (Button) null;

            /*
            bt = (Button) findViewById(R.id.btShowMap);
            if (bt != null) {
                ViewGroup.LayoutParams params = bt.getLayoutParams();
                if (mlvCurObject == R.id.lvComplessi || mlvCurObject == R.id.lvEdifici) {
                    params.height = 0;
                } else {
                    params.height = -2;
                }
                bt.setLayoutParams(params);
            }
            */

            bt = (Button) findViewById(R.id.btOpenDWG);
            if (bt != null) {
                ViewGroup.LayoutParams params = bt.getLayoutParams();
                if (mlvCurObject == R.id.lvComplessi || mlvCurObject == R.id.lvEdifici || mlvCurObject == R.id.lvPiani) {
                    params.height = 0;
                } else {
                    params.height = -2;
                }
                bt.setLayoutParams(params);
            }

            bt = (Button) findViewById(R.id.btLocateVano);
            if (bt != null) {
                ViewGroup.LayoutParams params = bt.getLayoutParams();
                if (mlvCurObject == R.id.lvComplessi || mlvCurObject == R.id.lvEdifici || mlvCurObject == R.id.lvPiani || mlvCurObject == R.id.lvVani) {
                    params.height = 0;
                } else {
                    params.height = -2;
                }
                bt.setLayoutParams(params);
            }

            bt = (Button) findViewById(R.id.btLocateOggetto);
            if (bt != null) {
                ViewGroup.LayoutParams params = bt.getLayoutParams();
                if (mlvCurObject == R.id.lvComplessi || mlvCurObject == R.id.lvEdifici || mlvCurObject == R.id.lvPiani || mlvCurObject == R.id.lvVani || mlvCurObject == R.id.lvOggetti) {
                    params.height = 0;
                } else {
                    params.height = -2;
                }
                bt.setLayoutParams(params);
            }


            TextView tv = (TextView) this.findViewById(R.id.tvCurStat);
            if (mlvCurObject == R.id.lvComplessi) {
                if (tv != null) tv.setText("Seleziona il Complesso");
            } else if (mlvCurObject == R.id.lvEdifici) {
                if (tv != null) tv.setText("Seleziona l'Edificio");
            } else if (mlvCurObject == R.id.lvPiani) {
                if (tv != null) tv.setText("Seleziona il Piano");
            } else if (mlvCurObject == R.id.lvVani) {
                if (tv != null) tv.setText("Seleziona il Vano");
            } else if (mlvCurObject == R.id.lvOggetti) {
                if (tv != null) tv.setText("Seleziona l'Oggetto");
            } else if (mlvCurObject == R.id.lvInterventi) {
                if (tv != null) tv.setText("Aggiungi o visualizza un intervento");
            } else if (mlvCurObject == R.id.lvInterventi) {
                if (tv != null) tv.setText("Seleziona un oggetto...");
            }

            // if (!Constants.BUTTONS_AS_DEBUG) tv.setVisibility(View.INVISIBLE);

            return 1;
        }
        return 0;
    }






    public void startup_list_view() {

        try {
            APPData.LAST_SEL_COMPLESSO = APPData.Complessi.get(APPData.iIndexOf(APPData.IDComplessi, APPData.LAST_SEL_IDCOMPLESSO));
        } catch (Exception e) {
        }
        try {
            APPData.LAST_SEL_EDIFICIO = APPData.Edifici.get(APPData.iIndexOf(APPData.IDEdifici, APPData.LAST_SEL_IDEDIFICIO));
        } catch (Exception e) {
        }
        try {
            APPData.LAST_SEL_PIANO = APPData.Piani.get(APPData.iIndexOf(APPData.IDPiani, APPData.LAST_SEL_IDPIANO));
        } catch (Exception e) {
        }
        try {
            APPData.LAST_SEL_VANO = APPData.Vani.get(APPData.iIndexOf(APPData.IDVani, APPData.LAST_SEL_IDVANO));
        } catch (Exception e) {
        }
        try {
            APPData.LAST_SEL_OGGETTO = APPData.appOggetti.CodOggetti.get(APPData.iIndexOf(APPData.appOggetti.IDOggetti, APPData.LAST_SEL_IDOGGETTO));
        } catch (Exception e) {
        }
        try {
            APPData.LAST_SEL_OGGETTO_DESC = APPData.appOggetti.DescOggetti.get(APPData.iIndexOf(APPData.appOggetti.IDOggetti, APPData.LAST_SEL_IDOGGETTO));
        } catch (Exception e) {
        }

        setup_listview_complessi(this, R.id.lvComplessi, -1, false, 0);
        setup_listview_edifici(this, R.id.lvEdifici, -1, false, 0);
        setup_listview_piani(this, R.id.lvPiani, -1, false, 0);
        setup_listview_vani(this, R.id.lvVani, -1, false, 0);
        setup_listview_oggetti(this, R.id.lvOggetti, -1, false, 0);
        setup_listview_interventi(this, R.id.lvInterventi, -1, false, ListViewInterventyType);


        if (APPData.appOggetti.cOggetto >= 0) {
            set_list_view_as_current(R.id.lvInterventi);
        } else {
            if (APPData.cVano >= 0) {
                set_list_view_as_current(R.id.lvOggetti);
            } else {
                if (APPData.cPiano >= 0) {
                    set_list_view_as_current(R.id.lvVani);
                } else {
                    if (APPData.cEdificio >= 0) {
                        set_list_view_as_current(R.id.lvPiani);
                    } else {
                        if (APPData.cComplesso >= 0) {
                            set_list_view_as_current(R.id.lvEdifici);
                        } else {
                            set_list_view_as_current(R.id.lvComplessi);
                        }
                    }
                }
            }
        }
    }





    public void setup_listview_complessi(Activity v, int lvObject, int cObject, boolean readAll, int xml_id) {
        /////////////////////////////////
        // Lettura tabella Complesso
        //
        Integer IDComplesso = 0;

        try {
            IDComplesso = IDComplesso.parseInt(APPData.DEVICE_ID_COMPLESSO != null ? APPData.DEVICE_ID_COMPLESSO : "0");
        } catch (Exception e) { }

        APPComplessiSQL.leggi_complessi(IDComplesso);

        if (APPData.LAST_SEL_IDCOMPLESSO != null && APPData.LAST_SEL_IDCOMPLESSO > 0) APPData.cComplesso = APPData.iIndexOf(APPData.IDComplessi, APPData.LAST_SEL_IDCOMPLESSO);


        if (cObject < 0) cObject = APPData.cComplesso;

        try {

            final ListView lv = (ListView) v.findViewById(lvObject);

            if (lv != null) {

                SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, APPData.Complessi, null, null, xml_id);
                lv.setAdapter(ListAdapter);

                if (readAll) {

                } else {
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View view, final int position, long id) {
                            boolean bCheckForNeededSync = false;

                            if (APPData.cComplesso != position) {
                                MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                                APPData.cComplesso = position;

                                APPData.LAST_SEL_IDCOMPLESSO = APPData.IDComplessi.get(APPData.cComplesso);
                                sqliteWrapper.update_setup_record("LastSelIDComplesso", String.valueOf(APPData.LAST_SEL_IDCOMPLESSO));

                                setup_listview_edifici(mainActivity.mActivity, R.id.lvEdifici, APPData.cEdificio = -1, false, 0);
                                setup_listview_piani(mainActivity.mActivity, R.id.lvPiani, APPData.cPiano = -1, false, 0);
                                setup_listview_vani(mainActivity.mActivity, R.id.lvVani, APPData.cVano = -1, false, 0);
                                setup_listview_oggetti(mainActivity.mActivity, R.id.lvOggetti, APPData.appOggetti.cOggetto = -1, false, 0);
                                setup_listview_interventi(mainActivity.mActivity, R.id.lvInterventi, APPData.appInterventi.cIntervento = -1, false, ListViewInterventyType);
                                set_list_view_as_current(R.id.lvEdifici);

                                ((ListView) arg0).setItemChecked(position, true);
                                ((ListView) arg0).post(new Runnable() {
                                    public void run() {
                                        lv.setSelectionFromTop(position, 0);
                                    }
                                });

                            bCheckForNeededSync = true;

                            } else {
                                if (set_list_view_as_current(R.id.lvComplessi) == 0) {
                                    // Chisura listview
                                    set_list_view_as_current(R.id.lvEdifici);
                                    bCheckForNeededSync = true;
                                } else {
                                }
                            }

                            ///////////////////////////////////////////////////////////////
                            // Controllo presenza dati con eventuale sincronizzazione
                            //
                            if (bCheckForNeededSync) {
                                Handler mainHandler = new Handler(context.getMainLooper());
                                Runnable on_check_tables_data_runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        //Log.v(TAG, strCharacters);
                                        on_check_tables_data(true);
                                    }
                                };
                                mainHandler.post(on_check_tables_data_runnable);
                            }

                        }
                    });

                    lv.setOnScrollListener(new AbsListView.OnScrollListener() {
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            // set_list_view_as_current (R.id.lvComplessi);
                        }

                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            // set_list_view_as_current (R.id.lvComplessi);
                        }
                    });
                }

                if (cObject >= 0) {
                    lv.setSelection(cObject);
                    lv.setSelectionFromTop(cObject, 0);
                }

            } else {
                DialogBox.DialogBox("ERRORE", "Listview non trovata", 0, MainActivity.this);
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    public void setup_listview_edifici(Activity v, int lvObject, int cObject, boolean readAll, int xml_id) {

        /////////////////////////////////
        // Lettura tabella Edifici
        //

        int fltIDComplesso = -1;
        if (readAll) {
            fltIDComplesso = 0;
        } else {
            fltIDComplesso = APPData.cComplesso >= 0 ? APPData.IDComplessi.get(APPData.cComplesso) : -1;
        }

        APPEdificiSQL.leggi_edifici(fltIDComplesso);

        if (APPData.LAST_SEL_IDEDIFICIO != null && APPData.LAST_SEL_IDEDIFICIO > 0) APPData.cEdificio = APPData.iIndexOf(APPData.IDEdifici, APPData.LAST_SEL_IDEDIFICIO);

        if (cObject < 0) cObject = APPData.cEdificio;


        try {

            final ListView lv = (ListView) v.findViewById(lvObject);

            if (lv != null) {
                SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, APPData.Edifici, null, APPData.CodEdifici, xml_id);
                lv.setAdapter(ListAdapter);

                if (readAll) {

                } else {
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(final AdapterView<?> arg0, View v, final int position, long arg3) {
                            if (APPData.cEdificio != position) {
                                MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                                APPData.cEdificio = position;

                                APPData.LAST_SEL_IDEDIFICIO = APPData.IDEdifici.get(APPData.cEdificio);
                                sqliteWrapper.update_setup_record("LastSelIDEdificio", String.valueOf(APPData.LAST_SEL_IDEDIFICIO));

                                setup_listview_piani(mainActivity.mActivity, R.id.lvPiani, APPData.cPiano = -1, false, 0);
                                setup_listview_vani(mainActivity.mActivity, R.id.lvVani, APPData.cVano = -1, false, 0);
                                setup_listview_oggetti(mainActivity.mActivity, R.id.lvOggetti, APPData.appOggetti.cOggetto = -1, false, 0);
                                setup_listview_interventi(mainActivity.mActivity, R.id.lvInterventi, APPData.appInterventi.cIntervento = -1, false, ListViewInterventyType);
                                set_list_view_as_current(R.id.lvPiani);
                                ((ListView) arg0).post(new Runnable() {
                                    public void run() {
                                        lv.setSelectionFromTop(position, 0);
                                    }
                                });
                            } else {
                                if (set_list_view_as_current(R.id.lvEdifici) == 0) {
                                    set_list_view_as_current(R.id.lvPiani);
                                }
                            }
                        }
                    });
                }
                if (cObject >= 0) {
                    lv.setSelection(cObject);
                    lv.setSelectionFromTop(cObject, 0);
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    public void setup_listview_piani(Activity v, int lvObject, int cObject, boolean readAll, int xml_id) {

        /////////////////////////////////
        // Lettura tabella piani
        //
        int cEdificio = -1;
        if (readAll) {
            cEdificio = 0;
        } else {
            cEdificio = APPData.cEdificio >= 0 ? APPData.IDEdifici.get(APPData.cEdificio) : -1;
        }

        APPPianiSQL.leggi_piani(cEdificio);

        if (APPData.LAST_SEL_IDPIANO != null && APPData.LAST_SEL_IDPIANO > 0) APPData.cPiano = APPData.iIndexOf(APPData.IDPiani, APPData.LAST_SEL_IDPIANO);

        if (cObject < 0) cObject = APPData.cPiano;

        try {

            final ListView lv = (ListView) v.findViewById(lvObject);

            if (lv != null) {
                SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, APPData.PianiDesc, null, APPData.CodPiani, xml_id);
                lv.setAdapter(ListAdapter);

                if (readAll) {

                } else {
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View v, final int position, long arg3) {
                            if (APPData.cPiano != position) {
                                MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                                APPData.cPiano = position;

                                APPData.LAST_SEL_IDPIANO = APPData.IDPiani.get(APPData.cPiano);
                                sqliteWrapper.update_setup_record("LastSelIDPiano", String.valueOf(APPData.LAST_SEL_IDPIANO));

                                setup_listview_vani(mainActivity.mActivity, R.id.lvVani, APPData.cVano = -1, false, 0);
                                setup_listview_oggetti(mainActivity.mActivity, R.id.lvOggetti, APPData.appOggetti.cOggetto = -1, false, 0);
                                setup_listview_interventi(mainActivity.mActivity, R.id.lvInterventi, APPData.appInterventi.cIntervento = -1, false, ListViewInterventyType);
                                set_list_view_as_current(R.id.lvVani);
                                ((ListView) arg0).post(new Runnable() {
                                    public void run() {
                                        lv.setSelectionFromTop(position, 0);
                                    }
                                });
                            } else {
                                if (set_list_view_as_current(R.id.lvPiani) == 0) {
                                    set_list_view_as_current(R.id.lvVani);
                                }
                            }
                        }
                    });
                }

                if (cObject >= 0) {
                    lv.setSelection(cObject);
                    lv.setSelectionFromTop(cObject, 0);
                }
            }

        } catch (Throwable e) {
            System.err.println(e.getMessage());
        }
    }


    public void setup_listview_vani(Activity v, int lvObject, int cObject, boolean readAll, int xml_id) {

        /////////////////////////////////
        // Lettura tabella Vano
        //
        int cPiano = -1;
        if (readAll) {
            cPiano = 0;
        } else {
            cPiano = APPData.cPiano >= 0 ? APPData.IDPiani.get(APPData.cPiano) : -1;
        }

        APPVaniSQL.leggi_vani(cPiano);

        if (APPData.LAST_SEL_IDVANO > 0) APPData.cVano = APPData.iIndexOf(APPData.IDVani, APPData.LAST_SEL_IDVANO);

        if (cObject < 0) cObject = APPData.cVano;

        try {

            final ListView lv = (ListView) v.findViewById(lvObject);

            if (lv != null) {
                SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, APPData.Vani, null, APPData.CodVani, xml_id);
                lv.setAdapter(ListAdapter);

                if (readAll) {

                } else {
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View v, final int position, long arg3) {
                            if (APPData.cVano != position) {
                                MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                                APPData.cVano = position;

                                APPData.LAST_SEL_IDVANO = APPData.IDVani.get(APPData.cVano);
                                sqliteWrapper.update_setup_record("LastSelIDVano", String.valueOf(APPData.LAST_SEL_IDVANO));

                                setup_listview_oggetti(mainActivity.mActivity, R.id.lvOggetti, APPData.appOggetti.cOggetto = -1, false, 0);
                                setup_listview_interventi(mainActivity.mActivity, R.id.lvInterventi, APPData.appInterventi.cIntervento = -1, false, ListViewInterventyType);
                                set_list_view_as_current(R.id.lvOggetti);
                                ((ListView) arg0).post(new Runnable() {
                                    public void run() {
                                        lv.setSelectionFromTop(position, 0);
                                    }
                                });
                            } else {
                                if (set_list_view_as_current(R.id.lvVani) == 0) {
                                    set_list_view_as_current(R.id.lvOggetti);
                                }
                            }
                        }
                    });
                }
                if (cObject >= 0) {
                    lv.setSelection(cObject);
                    lv.setSelectionFromTop(cObject, 0);
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    public void setup_listview_oggetti(Activity v, int lvObject, int cObject, boolean readAll, int xml_id) {

        /////////////////////////////////////////////////////
        // Lettura tabella Oggetto dato il vano corrente
        //

        Integer IDPiano = 0;
        Integer IDVano = -1;

        if (readAll) {
            IDVano = 0;
        } else {
            IDVano = APPData.cVano >= 0 ? APPData.IDVani.get(APPData.cVano) : -1;
            IDPiano = APPData.cPiano >= 0 ? APPData.IDPiani.get(APPData.cPiano) : 0;
        }


        APPOggettiSQL.leggi_oggetti(0, 0, IDPiano, IDVano, APPData.appOggetti, APPData.appOggettiIfIntervento);

        if (APPData.LAST_SEL_IDOGGETTO != null && APPData.LAST_SEL_IDOGGETTO > 0) APPData.appOggetti.cOggetto = APPData.appOggetti.IDOggetti.indexOf(APPData.LAST_SEL_IDOGGETTO);

        if (cObject < 0) cObject = APPData.appOggetti.cOggetto;

        try {

            final ListView lv = (ListView) v.findViewById(lvObject);

            if (lv != null) {
                // lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, APPData.appOggetti.DescOggetti, null, APPData.appOggetti.CodOggetti, xml_id);
                lv.setAdapter(ListAdapter);

                if (readAll) {

                } else {
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View v, final int position, long arg3) {
                            if (APPData.appOggetti.cOggetto != position) {
                                MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                                APPData.appOggetti.cOggetto = position;

                                APPData.LAST_SEL_IDOGGETTO = APPData.appOggetti.IDOggetti.get(APPData.appOggetti.cOggetto);
                                sqliteWrapper.update_setup_record("LastSelIDOggetto", String.valueOf(APPData.LAST_SEL_IDOGGETTO));
                                APPData.LAST_SEL_OGGETTO = APPData.appOggetti.CodOggetti.get(APPData.appOggetti.cOggetto);
                                APPData.LAST_SEL_OGGETTO_DESC = APPData.appOggetti.DescOggetti.get(APPData.appOggetti.cOggetto);

                                setup_listview_interventi(mainActivity.mActivity, R.id.lvInterventi, APPData.appInterventi.cIntervento = -1, false, ListViewInterventyType);
                                set_list_view_as_current(R.id.lvInterventi);
                                ((ListView) arg0).post(new Runnable() {
                                    public void run() {
                                        lv.setSelectionFromTop(position, 0);
                                    }
                                });
                            } else {
                                if (set_list_view_as_current(R.id.lvOggetti) == 0) {
                                    set_list_view_as_current(R.id.lvInterventi);
                                }
                            }
                        }
                    });
                }
                if (cObject >= 0) {
                    lv.setSelection(cObject);
                    lv.setSelectionFromTop(cObject, 0);
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    public void setup_listview_interventi(Activity v, int lvObject, int cObject, boolean readAll, int xml_id) {

        /////////////////////////////////
        // Lettura tabella Interventi
        //
        int cOggetto = -1;
        if (readAll) {
            cOggetto = 0;
        } else {
            cOggetto = APPData.appOggetti.cOggetto >= 0 ? APPData.appOggetti.IDOggetti.get(APPData.appOggetti.cOggetto) : -1;
        }

        APPInterventiSQL.leggi_interventi(null, null, null, null, cOggetto, APPData.appInterventi);

        if (APPData.LAST_SEL_IDINTERVENO != null && !APPData.LAST_SEL_IDINTERVENO.isEmpty()) APPData.appInterventi.cIntervento = APPData.sIndexOf(APPData.appInterventi.IDInterventi, String.valueOf(APPData.LAST_SEL_IDINTERVENO));

        if (cObject < 0) cObject = APPData.appInterventi.cIntervento;

        try {

            ArrayList<String> Labels = new ArrayList<String>();
            Labels.add("Descrizione");

            ListView lv = (ListView) v.findViewById(lvObject);

            if (lv != null) {

                // SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, APPData.ObjectCustomFields, APPData.ObjectCustomLabels);
                SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, APPData.appInterventi.CodInterventi, APPData.appInterventi.DescInterventiAux, APPData.appInterventi.CodOggettoInterventi, xml_id);

                lv.setAdapter(ListAdapter);

                if (readAll) {

                } else {
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                            if (APPData.appInterventi.cIntervento != position) {
                                APPData.appInterventi.cIntervento = position;
                            }
                            APPData.LAST_SEL_IDINTERVENO = APPData.appInterventi.IDInterventi.get(APPData.appInterventi.cIntervento);
                            sqliteWrapper.update_setup_record("LastSelIDIntervento", String.valueOf(APPData.LAST_SEL_IDINTERVENO));
                                doClickOnObject(String.valueOf(APPData.appOggetti.IDOggetti.get(APPData.appOggetti.cOggetto)), APPData.appOggetti.CodOggetti.get(APPData.appOggetti.cOggetto), context, MainActivity.this);
                        }
                    });
                }
                if (cObject >= 0) {
                    lv.setSelection(cObject);
                    lv.setSelectionFromTop(cObject, 0);
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    public void setup_listview_dwg(Activity v, int lvObject, int cObject, boolean readAll, int xml_id) {

        /////////////////////////////////
        // Lettura tabella Oggetto
        //

        int cPiano = -1;
        if (readAll) {
            cPiano = 0;
        } else {
            cPiano = APPData.cPiano >= 0 ? APPData.IDPiani.get(APPData.cPiano) : -1;
        }

        APPDwgSQL.leggi_dwgs(cPiano);

        try {

            final ListView lv = (ListView) v.findViewById(lvObject);

            if (lv != null) {
                // lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, APPData.DescDwg, APPData.CodDwg, APPData.VerDescDwg, xml_id);
                lv.setAdapter(ListAdapter);

                if (readAll) {

                } else {
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View v, final int position, long arg3) {
                            if (APPData.cDwg != position) {
                                MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                                APPData.cDwg = position;
                                // ((ListView) arg0).post(new Runnable() { public void run() { lv.setSelectionFromTop(position, 0); } });
                            } else {
                            }
                        }
                    });
                }
                if (cObject >= 0) {
                    lv.setSelection(cObject);
                    lv.setSelectionFromTop(cObject, 0);
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }






    public void setup_listview_queue(Activity v, int lvObject, int cObject, boolean readAll, int xml_id) {

        /////////////////////////////////
        // Lettura tabella coda
        //
        APPQueueSQL.read_queue();


        try {

            ArrayList<String> Labels = new ArrayList<String>();
            Labels.add("Desc");

            ListView lv = (ListView) v.findViewById(lvObject);
            if (lv != null) {
                SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, APPData.DescQueue, null, Labels, xml_id);
                lv.setAdapter(ListAdapter);

                if (readAll) {

                } else {
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                            // String str=[position];
                            System.err.println("onItemClick:" + position);
                        }
                    });
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }







    private static String[] GLDwgFiles = {
            "appdata/MSN_TEST.dwg",
            "appdata/CTRTest.dwg",
            "appdata/test0.dwg",
            "appdata/test1.dwg",
            "appdata/test2.dwg",
            "appdata/test3.dwg",
            "appdata/pd-floor1-2004-test12.dwg",
            "appdata/pd-floor1-2004-test11.dwg",
            "appdata/pd-floor1-2004-test10.dwg",
            "appdata/pd-floor1-2004-test9.dwg",
            "appdata/pd-floor1-2004-test8.dwg",
            "appdata/pd-floor1-2004-test7.dwg",
            "appdata/pd-floor1-2004-test6.dwg",
            "appdata/pd-floor1-2004-test5.dwg",
            "appdata/pd-floor1-2004-test4.dwg",
            "appdata/pd-floor1-2004-test3.dwg",
            "appdata/pd-floor1-2004-test2.dwg",
            "appdata/PALAZZO DUCALE piano terra.dwg",
            "appdata/PALAZZO DUCALE piano amm terra.dwg",
            "appdata/PALAZZO DUCALE piano ammezzato logge.dwg",
            "appdata/PALAZZO DUCALE PIANO LOGGE.dwg",
            "appdata/PALAZZO DUCALE piano secondo.dwg",
            "appdata/PALAZZO DUCALE piano sottotetto.dwg",
            "appdata/PALAZZO DUCALE piano amm secondo.dwg",
            "appdata/PRIGIONI NUOVE piano amm terra.dwg",
            "appdata/PRIGIONI NUOVE piano primo.dwg",
            "appdata/PRIGIONI NUOVE piano terra.dwg"
    };

    private static int GLNumDwg = GLDwgFiles.length;
    private static int GLCurDwg = 0;


    public int onDrawDwg(int curTab, boolean isTransacting) {
        int retVal = 0;
        try {
            retVal = DrawDwg(curTab, 0 + (isTransacting ? 1 : 0));
        } catch (Exception e) {
            e.getMessage();
        }
        return retVal;
    }










    private class OPENDWGParam {
        View view = null;
        Activity activity = null;
        Context context = null;
        ProgressDialog progress = null;

        Runnable callback = null;

        Integer id_piano = 0;
        String dwg_name = null;
        String dwg_id = null;

        byte[] buffer = null;
        int byteCount = 0;

        boolean TestMode = false;

        String file_name = null;
        String locateString = null;
        boolean locateVano = false;
        boolean locateOggetto = false;
        int cPiano = -1;

        Integer iParam = null;

        boolean openWhenDone = true;
        String Message = null;
        Integer RetVal = 0;
    }

    OPENDWGParam openDWGParam = new OPENDWGParam();




    ///////////////////////////////////////////////
    // Preliminare Apertura DWG (Sincrona)
    //
    public boolean openDWG ( View view, boolean bTestMode, boolean openWhenDone,
                             int cPiano,
                             boolean locateVano, boolean locateOggetto, String locateString,
                             Activity activity, Context context, Runnable callback ) {


        try {

            // openDWGParam.TestMode = bTestMode;
            openDWGParam.TestMode = Constants.DEBUG;
            openDWGParam.view = view;
            openDWGParam.activity = activity;
            openDWGParam.context = context;
            openDWGParam.openWhenDone = openWhenDone;
            openDWGParam.callback = callback;

            openDWGParam.cPiano = cPiano;
            openDWGParam.locateVano = locateVano;
            openDWGParam.locateOggetto = locateOggetto;
            openDWGParam.locateString = locateString;

            if (openDWGParam.TestMode) {

                AssetManager assetManager = getAssets();

                openDWGParam.file_name = GLDwgFiles[GLCurDwg];

                openDWGParam.id_piano = 0;
                openDWGParam.dwg_id = null;


                // GLCurDwg=(GLCurDwg>2?0:GLCurDwg+1);

                if (openDWGParam.file_name != null && !openDWGParam.file_name.isEmpty()) {
                    // String testingDWGB64Enc = Base64.encodeToString(buffer, 0, byteCount, Base64.DEFAULT);
                    onOpenDWG(view);
                } else {
                    return false;
                }

            } else {

                openDWGParam.id_piano = APPData.IDPiani != null ? (cPiano >= 0 && cPiano < APPData.IDPiani.size() ? APPData.IDPiani.get(cPiano) : 0) : 0;
                openDWGParam.dwg_name = APPData.DWGPiani != null ? (cPiano >= 0 && cPiano < APPData.DWGPiani.size() ? APPData.DWGPiani.get(cPiano) : null) : null;
                openDWGParam.dwg_id = APPData.IDDWGPiani != null ? (cPiano >= 0 && cPiano < APPData.IDDWGPiani.size() ? APPData.IDDWGPiani.get(cPiano) : null) : null;

                if (openDWGParam.dwg_id != null && !openDWGParam.dwg_id.isEmpty()) {
                    // Lettura del contenuto dal database (Lettura asincrona)
                    onOpenDWG(view);
                } else {
                    // Richiesta sincronizzazione
                    try {
                        if (openDWGParam.id_piano > 0) {
                            if (NetworkActivity.isOnline() > 0) {
                                if (DialogBox.DialogBox("ATTENZIONE", "Disegno non trovato il questo piano!\r\n\r\nSincronizzare il disegno del piano "+APPData.PianiDesc.get(cPiano)+" ?\r\n", 0 + 1 + 2, MainActivity.this)) {
                                    onSyncDWG(view, null, true, MainActivity.this, context, null);
                                }
                            } else {
                                DialogBox.DialogBox("ATTENZIONE", "Disegno non trovato per questo piano!", 0 + 0, MainActivity.this);
                                return false;
                            }
                        }


                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }

                    return false;
                }
            }


        } catch (Exception e) {
            System.err.println(e.getMessage());
            // e.printStackTrace();
        }

        return true;
    }




    ///////////////////////////////////////////////
    // Avvio lettura DWG (Sincrona)
    //
    public void onOpenDWG(final View view) {
        try {
            if (view != null) {

                // Disabilita il pulsante
                ((Button)findViewById(R.id.btOpenDWG)).setEnabled(false);

                this.getWindow().getDecorView().setAlpha(0.2f);

                try {
                    openDWGParam.progress = new ProgressDialog(openDWGParam.activity);

                    openDWGParam.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    openDWGParam.progress.setMessage("Apertura disegno in corso...");
                    openDWGParam.progress.setIndeterminate(true);
                    openDWGParam.progress.setCancelable(false);
                    openDWGParam.progress.show();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            doOpenDWG();
                        } catch (Exception e) {
                            openDWGParam.RetVal = -20;
                            openDWGParam.Message = "Exception error:"+e.getMessage();
                        }
                        try {
                            openDWGParam.progress.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Handler mainHandler = new Handler(context.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                doneOpenDWG(view);
                            }
                        };

                        mainHandler.post(myRunnable);
                    }
                };

                mThread.start();

            } else {
                doOpenDWG();
                doneOpenDWG(view);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }





    ///////////////////////////////////////////////
    // Esecuzione lettura DWG (Asincrona)
    //
    public void doOpenDWG() {

        try {

            if (openDWGParam.TestMode) {
                AssetManager assetManager = getAssets();

                if (openDWGParam.file_name != null && !openDWGParam.file_name.isEmpty()) {

                    // String str = "Reading " + openDWGParam.file_name + " from assets...";
                    // System.err.println(str);

                    try {
                        InputStream instream = assetManager.open(openDWGParam.file_name);
                        openDWGParam.byteCount = instream.available();
                        openDWGParam.buffer = new byte[openDWGParam.byteCount];
                        int bytesRead = instream.read(openDWGParam.buffer, 0, openDWGParam.byteCount);
                        instream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println(e.getMessage());
                        openDWGParam.id_piano = 0;
                        openDWGParam.dwg_name = null;
                        openDWGParam.dwg_id = null;
                        if (openDWGParam.buffer != null) {
                            openDWGParam.buffer = null;
                        }
                    }

                    // String testingDWGB64Enc = Base64.encodeToString(buffer, 0, byteCount, Base64.DEFAULT);
                }

            } else {

                openDWGParam.id_piano = APPData.IDPiani != null ? (openDWGParam.cPiano >= 0 && openDWGParam.cPiano < APPData.IDPiani.size() ? APPData.IDPiani.get(openDWGParam.cPiano) : 0) : 0;
                openDWGParam.dwg_name = APPData.DWGPiani != null ? (openDWGParam.cPiano >= 0 && openDWGParam.cPiano < APPData.DWGPiani.size() ? APPData.DWGPiani.get(openDWGParam.cPiano) : null) : null;
                openDWGParam.dwg_id = APPData.IDDWGPiani != null ? (openDWGParam.cPiano >= 0 && openDWGParam.cPiano < APPData.IDDWGPiani.size() ? APPData.IDDWGPiani.get(openDWGParam.cPiano) : null) : null;

                if (openDWGParam.dwg_id != null && !openDWGParam.dwg_id.isEmpty()) {

                    /////////////////////////////////////////////////////////////
                    // N.B.: Con zero parametri causa il crash a posteriori
                    //
                    Integer lastDwgID = GetDwgID(0);

                    // String lastError = GetError();

                    if (lastDwgID.intValue() <= 0 || lastDwgID != Integer.parseInt(openDWGParam.dwg_id)) {
                        // Lettura del contenuto dal database
                        update_progress_message(openDWGParam.progress, "Lettura disegno...");
                        openDWGParam.buffer = APPDwgSQL.leggi_dwg(Integer.valueOf(openDWGParam.dwg_id), true);
                        if (openDWGParam.buffer != null && openDWGParam.buffer.length > 0) {
                            openDWGParam.byteCount = openDWGParam.buffer.length;
                        } else {
                            openDWGParam.byteCount = 0;
                            openDWGParam.Message = "Lettura Disegno fallita";
                            openDWGParam.RetVal = -1;
                        }
                    } else {
                        openDWGParam.byteCount = 0;
                        openDWGParam.Message = "Lettura Disegno da cache";
                        openDWGParam.RetVal = 1;
                    }
                } else {
                    openDWGParam.Message = "Disegno non trovato";
                    openDWGParam.RetVal = -2;
                }
            }


            update_progress_message(openDWGParam.progress, "Caricamento disegno ("+(openDWGParam.byteCount/1024)+"Kb) ...");

            int res = LoadDwg(openDWGParam.buffer, openDWGParam.byteCount, openDWGParam.dwg_id!=null?Integer.parseInt(openDWGParam.dwg_id):-1);

            if (res <= 0) {
                if (openDWGParam.buffer == null || openDWGParam.byteCount == 0) {
                    openDWGParam.Message = "LoadDwg error:buffer vuoto, controllare le dimensione del DWG (< 2 MB) e risincronizzare il dwg";
                    openDWGParam.RetVal = -11;
                } else {
                    openDWGParam.Message = "LoadDwg error:"+GetError();
                    openDWGParam.RetVal = -10;
                }
            } else {
                openDWGParam.Message = "[J] No.objects in dwg:" + res;
                openDWGParam.RetVal = 1;
                update_progress_message(openDWGParam.progress, "Disegno caricato : "+(res)+"entita' ...");
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }





    public void doneOpenDWG(View v) {
        try {
            if (openDWGParam.RetVal == 1) {
                if (openDWGParam.openWhenDone) {

                    getWindow().getDecorView().setAlpha(0.3f);

                    Intent myIntent = new Intent(this, DWGViewerActivity.class);
                    if (openDWGParam.locateVano) {
                        myIntent.putExtra("key", "locateVano");
                    } else if (openDWGParam.locateOggetto) {
                        myIntent.putExtra("key", "locateOggetto");
                    }
                    myIntent.putExtra("search", openDWGParam.locateString);
                    startActivity(myIntent);
                }

            } else if (openDWGParam.RetVal == -11) {
                if (DialogBox.DialogBox("ATTENZIONE", "Disegno non valido per questo piano!\r\n\r\nRi-sincronizzare il disegno di questo piano ?\r\n", 0 + 1 + 2, MainActivity.this)) {
                    onSyncDWG(v, null, true, MainActivity.this, context, null);
                }

            } else {
                if (openDWGParam.Message != null && !openDWGParam.Message.isEmpty()) {
                    DialogBox.DialogBox("ATTENZIONE", openDWGParam.Message, 0 + 0, MainActivity.this);
                }
            }


            try {
                if (openDWGParam.callback != null) {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    mainHandler.post(openDWGParam.callback);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Riabilitazione pulsante
            ((Button)findViewById(R.id.btOpenDWG)).setEnabled(true);


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }















    ///////////////////////////////////////////////
    // Avvio Sincronizzazione DWG (Asincrona)
    //
    // Sincrona

    public void onSyncDWG(View view, Integer id_piano, boolean openWhenDone, Activity activity, Context pContext, Runnable callback) {
        try {
            if (view != null) {

                // Disabilita il pulsante
                // ((Button)findViewById(R.id.btOpenDWG)).setEnabled(false);

                openDWGParam.view = view;
                openDWGParam.activity = activity;
                openDWGParam.context = pContext;
                openDWGParam.Message = null;

                openDWGParam.progress = new ProgressDialog(openDWGParam.activity);
                openDWGParam.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                openDWGParam.progress.setMessage("Sincronizzazione disegno in corso...");
                openDWGParam.progress.setIndeterminate(true);
                openDWGParam.progress.setCancelable(false);
                openDWGParam.progress.show();
                openDWGParam.id_piano = id_piano!=null?id_piano:openDWGParam.id_piano;
                openDWGParam.openWhenDone = openWhenDone;
                openDWGParam.callback = callback;

                if (openDWGParam.id_piano != null) {
                    Thread mThread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                doSyncDWG();
                            } catch (Exception e) {
                                openDWGParam.RetVal = -20;
                                openDWGParam.Message = "Exception error:" + e.getMessage();
                            }
                            try {
                                openDWGParam.progress.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Handler mainHandler = new Handler(context.getMainLooper());
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    doneSyncDWG();
                                }
                            };

                            mainHandler.post(myRunnable);
                        }
                    };

                    mThread.start();
                }

            } else {
                doSyncDWG();
                doneSyncDWG();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }




    // Asincrona
    public boolean doSyncDWG() {
        int res = 0;
        SYNCTable syncTable = new SYNCTable();

        syncTable.table_name = "Disegni";
        syncTable.view = openDWGParam.view;
        syncTable.activity = openDWGParam.activity;
        syncTable.context = openDWGParam.context;
        syncTable.progress = openDWGParam.progress;


        if ((res = APPDwgSQL.sincronizza_dwg(String.valueOf(openDWGParam.id_piano), getApplicationContext(), MainActivity.this, syncTable)) > 0) {

            setup_listview_piani(this, R.id.lvPiani, APPData.cPiano, false, 0);

            setup_listview_dwg((Activity) MainActivity.this, R.id.lvGTDwg, 0, true, R.layout.rec_lv_3fields);

            // Sincronizzazione vani
            if (APPVaniSQL.sincronizza_vani(String.valueOf(openDWGParam.id_piano), getApplicationContext(), MainActivity.this, null) > 0) {
                setup_listview_vani((Activity) MainActivity.this, R.id.lvGTVani, 0, false, 0);
                // APPVaniSQL.leggi_vani(APPData.cPiano);
            }

            // Sincronizzazione oggetti
            if (APPOggettiSQL.sincronizza_oggetti(null, null, String.valueOf(openDWGParam.id_piano), null, getApplicationContext(), MainActivity.this, null) > 0) {

                // Leggi gli oggetti del vano corrente nell listview della pagina principale
                setup_listview_oggetti((Activity) MainActivity.this, R.id.lvOggetti, 0, false, 0);

                // Leggi tutti gli oggetti del piano
                Integer IDPiano = APPData.cPiano >= 0 ? APPData.IDPiani.get(APPData.cPiano) : 0;
                Integer IDVano = -1;

                // IDVano = APPData.cVano >= 0 ? APPData.IDVani.get(APPData.cVano) : -1;

                APPOggettiSQL.leggi_oggetti(0, 0, IDPiano, 0, APPData.appOggetti, APPData.appOggettiIfIntervento);

            }

            openDWGParam.dwg_id = APPData.IDDWGPiani != null ? (APPData.cPiano >= 0 && APPData.cPiano < APPData.IDDWGPiani.size() ? APPData.IDDWGPiani.get(APPData.cPiano) : null) : null;
            if (openDWGParam.dwg_id != null && !openDWGParam.dwg_id.isEmpty()) {
                openDWGParam.RetVal = 1;
                return true;
            } else {
                openDWGParam.Message = "Sincronizzazione fallita";
                openDWGParam.RetVal = res;
                return false;
            }

        }

        return false;
    }



    // Asincrona
    public void doneSyncDWG() {
        try {
            if (openDWGParam.RetVal == 1) {
                // ripartenza lettuta DWG
                if (openDWGParam.Message != null && !openDWGParam.Message.isEmpty()) {
                    DialogBox.DialogBox("ATTENZIONE", openDWGParam.Message, 0 + 0, openDWGParam.activity);
                }

                // Lettura del contenuto dal database (Lettura asincrona)
                if (openDWGParam.openWhenDone) {
                    if (openDWGParam.view != null) {
                        onOpenDWG(openDWGParam.view);
                    }
                }

                try {
                    if (openDWGParam.callback != null) {
                        Handler mainHandler = new Handler(context.getMainLooper());
                        mainHandler.post(openDWGParam.callback);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                if (openDWGParam.Message != null && !openDWGParam.Message.isEmpty()) {
                    DialogBox.DialogBox("ATTENZIONE", openDWGParam.Message, 0 + 0, openDWGParam.activity);
                } else {
                    DialogBox.DialogBox("ATTENZIONE", "Lettra DWG fallita, codice:"+openDWGParam.RetVal, 0 + 0, openDWGParam.activity);
                }
            }
            // Riabilitazione pulsante
            // ((Button)findViewById(R.id.btOpenDWG)).setEnabled(true);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }













    public void testPDFViewer() {

        // AssetManager assetManager = getAssets();

        // new PDFViewer().OpenPDFFile(this, "appdata/PAMAP.pdf");
        new PDFViewer().ViewPDFFile(this, "appdata/PAMAP.pdf");

    }






    String Server = "cristianandreon.com";
    String UserName = "cristianandreo";
    String Password = "Xxx533@@";

    public void postFileToFTPServer(String Server, String UserName, String Password,
                                    String Folder, String FileName, String FileContent) {

        try {

            FTPClient ftpClient = new FTPClient();

            try {

                ftpClient.connect(InetAddress.getByName(Server));
                ftpClient.login(UserName, Password);
                ftpClient.changeWorkingDirectory(Folder);
                String reply = ftpClient.getReplyString();

                if (reply.contains("250") || reply.contains("200")) {

                    try {
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(FileName, Context.MODE_PRIVATE));
                        outputStreamWriter.write(FileContent);
                        outputStreamWriter.close();
                    } catch (IOException e) {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }


                    ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);

                    String locaFileName = getFilesDir() + "/" + FileName;

                    BufferedInputStream buffIn = new BufferedInputStream(new FileInputStream(locaFileName));
                    ftpClient.enterLocalPassiveMode();
                    // ProgressInputStream progressInput = new ProgressInputStream(buffIn, progressHandler);

                    boolean result = ftpClient.storeFile(FileName, buffIn);

                    buffIn.close();
                }

                ftpClient.logout();
                ftpClient.disconnect();

            } catch (SocketException e) {
                System.err.println(e.getStackTrace().toString());
            } catch (UnknownHostException e) {
                System.err.println(e.getStackTrace().toString());
            } catch (IOException e) {
                System.err.println(e.getStackTrace().toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }


        return;

    }



    public void run_getCanvas() {

        try {

            String canvasCode = DwgToCanvas(0, 0);
            String str = "[J] Get canvas length :" + canvasCode.length();
            System.err.println(str);

            postFileToFTPServer(Server, UserName, Password, "/customers/Geisoft", "canvas.js", canvasCode);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }


        return;

    }




    public void getCanvas(View view) {
        Thread getCanvasThread = new Thread("getCanvasThread") {
            @Override
            public void run() {
                run_getCanvas();
            }
        };
        getCanvasThread.start();
    }





    public void run_getXML() {

        try {

            String xmlCode = DwgToXML(0, 0);
            String str = "[J] Get XML length :" + xmlCode.length();
            System.err.println(str);

            postFileToFTPServer(Server, UserName, Password, "/customers/Geisoft", "floor.xml", xmlCode);


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }


        return;

    }


    public void run_getKeyTexts() {

        try {

            String txtCode = DwgToXML(0, 1);
            String str = "[J] Get Key Texts length :" + txtCode.length();
            System.err.println(str);

            postFileToFTPServer(Server, UserName, Password, "/customers/Geisoft", APPData.CodPiani.get(APPData.cPiano)+" - KeyTexts.txt", txtCode);


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }


        return;

    }



    public void getXML(View view) {
        Thread getXMLThread = new Thread("getXMLThread") {
            @Override
            public void run() {
                run_getXML();
            }
        };
        getXMLThread.start();
    }



    public void getKeyTexts(View view) {
        Thread getKeytextThread = new Thread("getKeyTexts") {
            @Override
            public void run() {
                run_getKeyTexts();
            }
        };
        getKeytextThread.start();
    }





    public void run_getJSON() {


        try {

            String jsonCode = DwgToJSON(0, 0);
            String str = "[J] Get JSON length :" + jsonCode.length();
            System.err.println(str);

            postFileToFTPServer(Server, UserName, Password, "/customers/Geisoft", "floor.json", jsonCode);


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }


        return;

    }




    public void getJSON(View view) {
        Thread getJSONThread = new Thread("getJSONThread") {
            @Override
            public void run() {
                run_getJSON();
            }
        };
        getJSONThread.start();
    }








    // @Obsolete
    public void testXMLParser(View view) {
        String url = "http://customers.cristianandreon.com/Geisoft/floor.xml";
        // NetworkActivity pNetworkActivity = new NetworkActivity();
        // pNetworkActivity.loadPage(context, url);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        String result = null;
        try {
            result = networkActivity.getUrl(url, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.printf("result:"+result);

    }




    public void viewTables(View view) {
        try {
            getWindow().getDecorView().setAlpha(0.3f);
            Intent myIntent = new Intent(this, ViewTablesActivity.class);
            myIntent.putExtra("key", "value"); //Optional parameters
            startActivity(myIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    /////////////////////////////////////
    // Callback chiamata da JNI
    //
    public int lastClickCurItem = 0;
    public int lastClickCount = 0;
    public boolean lastClickOn = false;
    public String lastSearchText = null;
    public String lastSearchKey = null;
    private int MAX_CLIXK_ITEMS = 256;
    public String [] lastClickKey = new String[MAX_CLIXK_ITEMS], lastClickLayer = new String[MAX_CLIXK_ITEMS];
    public float [] lastClickX = new float[MAX_CLIXK_ITEMS], lastClickY = new float[MAX_CLIXK_ITEMS];
    public float [] lastClickWH = new float[MAX_CLIXK_ITEMS], lastClickHT = new float[MAX_CLIXK_ITEMS], lastClickAngle = new float[MAX_CLIXK_ITEMS];
    public int [] lastClickColor = new int[MAX_CLIXK_ITEMS], lastClickTypeOf = new int[MAX_CLIXK_ITEMS], lastClickeventType = new int[MAX_CLIXK_ITEMS];

    public void onClickText (float x, float y, float wh, float ht, float angle, String Key, String Layer, int Color, int TypeOf, char EventType) {


        if (TypeOf == 0) {
            // Text
        } else if (TypeOf == 1) {
            // MText
        } else {
        }

        if (EventType == 0) {
            // click
        } else if (EventType == 1) {
            // hold ?
        } else if (EventType == 2) {
            // search
        }

        if (EventType == 0) {
            // click / hold

            if (!lastClickOn) {
                // Init
                lastClickCurItem = 0;
            }

            lastClickOn = true;

            if (lastClickCount < MAX_CLIXK_ITEMS) {
                lastClickX[lastClickCount] = x;
                lastClickY[lastClickCount] = y;
                lastClickWH[lastClickCount] = wh;
                lastClickHT[lastClickCount] = ht;
                lastClickAngle[lastClickCount] = angle;
                lastClickKey[lastClickCount] = Key;
                lastClickLayer[lastClickCount] = Layer;
                lastClickColor[lastClickCount] = Color;
                lastClickTypeOf[lastClickCount] = TypeOf;
                lastClickeventType[lastClickCount] = EventType;
                lastClickCount++;
                System.err.printf("onClickText : storing ckick : " + Key);
            } else {
                System.err.printf("onClickText : out of range!");
            }

        } else if (EventType == 1) {


        } else if (EventType == 2) {
            lastClickOn = true;
            if (lastClickCount < MAX_CLIXK_ITEMS) {
                lastClickX[lastClickCount] = x;
                lastClickY[lastClickCount] = y;
                lastClickWH[lastClickCount] = wh;
                lastClickHT[lastClickCount] = ht;
                lastClickCount++;
                // System.err.printf("onClickText : storing text zone  : " + Key);
            } else {
                // System.err.printf("onClickText : out of range!");
            }
        }

        return;
    }




    public void doClickOnObject (String strObjectID, String strObjectCode, Context pContext, Activity activity) {

            /*
            APPData.Complesso = "Complesso N.1";
            APPData.Edificio = "Palazzo Ducale";
            APPData.Piano = "1° Piano";
            APPData.Vano = "Vano X";
            */

        try {

            if (APPData.Complessi != null && APPData.Edifici != null && APPData.Piani != null && APPData.Vani != null) {

                if (APPData.cComplesso < APPData.Complessi.size() && APPData.cComplesso >= 0 &&
                        APPData.cEdificio < APPData.Edifici.size() && APPData.cEdificio >= 0 &&
                        APPData.cPiano < APPData.Piani.size() && APPData.cPiano >= 0
                        ) {

                   if (APPData.cVano < APPData.Vani.size() && APPData.cVano >= 0) {
                   }

                    try {
                        if (APPData.cComplesso >= 0 && APPData.cComplesso < APPData.Complessi.size()) {
                            APPData.LAST_SEL_IDCOMPLESSO = APPData.IDComplessi.get(APPData.cComplesso);
                            APPData.LAST_SEL_COMPLESSO = APPData.Complessi.get(APPData.cComplesso);
                        }
                        if (APPData.cEdificio >= 0 && APPData.cEdificio < APPData.Edifici.size()) {
                            APPData.LAST_SEL_IDEDIFICIO = APPData.IDEdifici.get(APPData.cEdificio);
                            APPData.LAST_SEL_EDIFICIO = APPData.Edifici.get(APPData.cEdificio);
                        }
                        if (APPData.cPiano >= 0 && APPData.cPiano < APPData.Piani.size()) {
                            APPData.LAST_SEL_IDPIANO = APPData.IDPiani.get(APPData.cPiano);
                            APPData.LAST_SEL_PIANO = APPData.Piani.get(APPData.cPiano);
                        }
                        if (APPData.cVano >= 0 && APPData.cVano < APPData.Vani.size()) {
                            APPData.LAST_SEL_IDVANO = APPData.IDVani.get(APPData.cVano);
                            APPData.LAST_SEL_VANO = APPData.Vani.get(APPData.cVano);
                        }

                    } catch (Exception e) {
                        e.getMessage();
                    }
                    // Registrazione su db
                    // ...

                    String strFoundObjectID = "";


                    //////////////////////////////
                    // Analisi tipo di oggetto
                    //
                    String EntityType = "";

                    try {

                        String strObjectCodeWrpd = WrapText(strObjectCode, 1);

                        if (!strObjectCodeWrpd.isEmpty()) {
                            EntityType += strObjectCodeWrpd.charAt(0);
                            EntityType += strObjectCodeWrpd.charAt(1);
                        }

                    } catch (Exception e) {
                        e.getMessage();
                        e.printStackTrace();
                    }

                    // "***SEARCH_ON_VANI_KEY***"
                    if (EntityType.isEmpty()) {

                        //////////////////////////////////
                        // Ricerca sui vani
                        //

                        if (Integer.parseInt(strObjectID) > 0) {
                            strFoundObjectID = strObjectID;
                        } else {
                            // Ricerca per codice (chiamata dal click del testo sul dwg
                            for (int i=0; i<APPData.Vani.size(); i++) {
                                if (APPData.CodVani.get(i).compareTo(strObjectCode) == 0) {
                                    strFoundObjectID = String.valueOf(APPData.IDVani.get(i));
                                    break;
                                }
                            }
                        }

                        if (strObjectCode != null && !strObjectCode.isEmpty() && strFoundObjectID != null && !strFoundObjectID.isEmpty()) {
                            APPData.LAST_SEL_VANO = strObjectCode;
                            APPData.LAST_SEL_IDVANO = Integer.valueOf(strFoundObjectID);

                            if (activity != null) {
                                try {
                                    activity.getWindow().getDecorView().setAlpha(0.6f);
                                } catch (Exception e) {
                                }
                            } else {
                                getWindow().getDecorView().setAlpha(0.3f);
                            }

                            // Salvataggio telecamera
                            WriteCamera();

                            Intent myIntent = new Intent(this, APPVaniActivity.class);

                            myIntent.putExtra("VanoID", strFoundObjectID);
                            myIntent.putExtra("VanoCode", strObjectCode);
                            myIntent.putExtra("Key", lastClickCurItem < lastClickCount ? lastClickKey[lastClickCurItem] : ""); //Optional parameters
                            startActivity(myIntent);

                        } else {
                        }



                    } else {

                        //////////////////////////////////
                        // Ricerca sugli oggetti
                        //

                        int OnjectsFoundCount = 0;
                        int DuplicateID = 0;

                        if (Integer.parseInt(strObjectID) > 0) {
                            strFoundObjectID = strObjectID;
                        } else {

                            // Ricerca per codice (chiamata dal click del testo sul dwg


                            APPData.appSelezioneOggetti.setup();
                            APPData.appSelezioneOggetti.cOggetto = 0;

                            for (int i=0; i<APPData.appOggetti.CodOggetti.size(); i++) {
                                if (APPData.appOggetti.CodOggetti.get(i).compareTo(strObjectCode) == 0) {
                                    strFoundObjectID = String.valueOf(APPData.appOggetti.IDOggetti.get(i));
                                    OnjectsFoundCount++;
                                    // break;
                                    int ObjectID = APPData.appOggetti.IDOggetti.get(i);

                                    if (APPData.iIndexOf(APPData.appSelezioneOggetti.IDOggetti, ObjectID) >= 0) {
                                        DuplicateID++;
                                    }

                                    APPData.appSelezioneOggetti.IDOggetti.add(ObjectID);
                                    APPData.appSelezioneOggetti.CodOggetti.add(APPData.appOggetti.CodOggetti.get(i));
                                    APPData.appSelezioneOggetti.DescOggetti.add(APPData.appOggetti.DescOggetti.get(i));
                                    APPData.appSelezioneOggetti.ExtDescOggetti.add(APPData.appOggetti.ExtDescOggetti.get(i));
                                    APPData.appSelezioneOggetti.NumOggetti++;
                                }
                            }
                        }



                        if (strObjectCode != null && !strObjectCode.isEmpty()) {

                            try {

                                // Test
                                // OnjectsFoundCount++;


                                if (OnjectsFoundCount > 1) {

                                    if (DuplicateID > 0) {
                                        String errMessage = "ID Oggetto duplicato nel database [ID:"+strFoundObjectID+"]!";
                                        DialogBox.ShowMessage(errMessage, pContext, 1);
                                        DialogBox.DialogBox("ERRORE", errMessage, 0, pContext);
                                    } else {
                                        // Risoluzione ambiguità

                                        if (activity != null) {
                                            try {
                                                activity.getWindow().getDecorView().setAlpha(0.6f);
                                            } catch (Exception e) {
                                            }
                                        } else {
                                            getWindow().getDecorView().setAlpha(0.3f);
                                        }

                                        // Salvataggio telecamera
                                        WriteCamera();

                                        Intent myIntent = new Intent(this, APPSelOggettiActivity.class);

                                        myIntent.putExtra("SelecteObjectID", "");
                                        myIntent.putExtra("SelecteObjectCode", "");
                                        myIntent.putExtra("SelecteObjectDesc", "");
                                        myIntent.putExtra("OnOKCallback", "APPInterventiActivity");

                                        startActivity(myIntent);
                                    }



                                } else {
                                    //////////////////////////
                                    // Selezione singola
                                    //

                                    if (strFoundObjectID != null && !strFoundObjectID.isEmpty() && Integer.parseInt(strFoundObjectID) > 0) {
                                        String strObjectDesc = null;
                                        int objIndex = APPData.iIndexOf(APPData.appOggetti.IDOggetti, Integer.valueOf(strFoundObjectID));

                                        try {
                                            strObjectDesc = APPData.appOggetti.DescOggetti.get(objIndex);
                                        } catch (Exception e) {
                                            strObjectDesc = "???";
                                            e.printStackTrace();
                                        }

                                        int n = APPData.Vani.size();
                                        Integer ObjectIDVano = APPData.appOggetti.IDVanno.get(objIndex);
                                        APPData.LAST_SEL_IDVANO = ObjectIDVano;
                                        APPData.LAST_SEL_VANO = null;
                                        for (int i=0; i<n; i++) {
                                            if (APPData.IDVani.get(i).compareTo(ObjectIDVano)==0) {
                                                APPData.LAST_SEL_VANO = APPData.Vani.get(i);
                                                break;
                                            }
                                        }

                                        if (APPData.LAST_SEL_VANO==null) {
                                            DialogBox.DialogBox("ERRORE", "Vano non trovato per l'oggetto '" + strObjectCode + "', ID Oggetto:"+strFoundObjectID, 0, pContext);
                                            APPData.LAST_SEL_VANO = "???";
                                        }

                                        APPData.LAST_SEL_OGGETTO = strObjectCode;
                                        APPData.LAST_SEL_IDOGGETTO = Integer.valueOf(strFoundObjectID);
                                        APPData.LAST_SEL_OGGETTO_DESC = strObjectDesc;

                                        if (activity != null) {
                                            try {
                                                activity.getWindow().getDecorView().setAlpha(0.6f);
                                            } catch (Exception e) {
                                            }
                                        } else {
                                            getWindow().getDecorView().setAlpha(0.3f);
                                        }


                                        Intent myIntent = new Intent(this, APPInterventiActivity.class);

                                        myIntent.putExtra("cIntervento", String.valueOf(APPData.appInterventi.cIntervento));
                                        myIntent.putExtra("ObjectID", strFoundObjectID);
                                        myIntent.putExtra("ObjectCode", strObjectCode);
                                        myIntent.putExtra("ObjectDesc", strObjectDesc);
                                        myIntent.putExtra("Key", lastClickCurItem < lastClickCount ? lastClickKey[lastClickCurItem] : ""); //Optional parameters

                                        startActivity(myIntent);

                                    } else {
                                        if (lastClickKey[lastClickCurItem] != null && !lastClickKey[lastClickCurItem].isEmpty()) {
                                            if (!EntityType.isEmpty()) {
                                                DialogBox.DialogBox("ERRORE", "Oggetto '" + strObjectCode + "' non catalogato nel database!", 0, pContext);
                                            }
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                        }
                        // Oggetto non riconosciuto
                    }

                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public void aggiungiIntervento (Context pContext) {

        try {

            if (APPData.Complessi != null && APPData.Edifici != null && APPData.Piani != null && APPData.Vani != null) {

                if (APPData.cComplesso < APPData.Complessi.size() && APPData.cComplesso >= 0 &&
                        APPData.cEdificio < APPData.Edifici.size() && APPData.cEdificio >= 0 &&
                        APPData.cPiano < APPData.Piani.size() && APPData.cPiano >= 0 &&
                        APPData.cVano < APPData.Vani.size() && APPData.cVano >= 0 &&
                        APPData.appOggetti.cOggetto < APPData.appOggetti.IDOggetti.size() && APPData.appOggetti.cOggetto >= 0
                        ) {

                    APPData.nuovoIntervento = true;


                    try {
                        APPData.LAST_SEL_IDCOMPLESSO = APPData.IDComplessi.get(APPData.cComplesso);
                        APPData.LAST_SEL_COMPLESSO = APPData.Complessi.get(APPData.cComplesso);
                        APPData.LAST_SEL_IDEDIFICIO = APPData.IDEdifici.get(APPData.cEdificio);
                        APPData.LAST_SEL_EDIFICIO = APPData.Edifici.get(APPData.cEdificio);
                        APPData.LAST_SEL_IDPIANO = APPData.IDPiani.get(APPData.cPiano);
                        APPData.LAST_SEL_PIANO = APPData.Piani.get(APPData.cPiano);
                        APPData.LAST_SEL_IDVANO = APPData.IDVani.get(APPData.cVano);
                        APPData.LAST_SEL_VANO = APPData.Vani.get(APPData.cVano);
                        APPData.LAST_SEL_IDOGGETTO = APPData.appOggetti.IDOggetti.get(APPData.appOggetti.cOggetto);
                        APPData.LAST_SEL_OGGETTO = APPData.appOggetti.CodOggetti.get(APPData.appOggetti.cOggetto);
                        APPData.LAST_SEL_OGGETTO_DESC = APPData.appOggetti.DescOggetti.get(APPData.appOggetti.cOggetto);
                    } catch (Exception e) {
                    }

                    String ObjectID = String.valueOf(APPData.appOggetti.IDOggetti.get(APPData.appOggetti.cOggetto));
                    String ObjectCode = APPData.appOggetti.CodOggetti.get(APPData.appOggetti.cOggetto);

                    getWindow().getDecorView().setAlpha(0.3f);

                    Intent myIntent = new Intent(this, APPInterventiActivity.class);

                    myIntent.putExtra("nuovoIntervento", "true");
                    myIntent.putExtra("cIntervento", "0");
                    myIntent.putExtra("ObjectID", ObjectID);
                    myIntent.putExtra("ObjectCode", ObjectCode);
                    myIntent.putExtra("Key", "");

                    startActivity(myIntent);
                }
            }

        } catch (Exception e) {
        e.printStackTrace();
        }
        }




    ///////////////////////////////////////////////
    // Wrapper to MyRendere class
    //
    void onDrawText( String text, int horiz_alignment, int vert_alignment, float x, float y, float ht, float ang, float r, float g, float b, float a, char isMText) {
        myRenderer.onDrawText(text, horiz_alignment, vert_alignment, x, y, ht, ang, r, g, b, a, isMText);
    }


    ///////////////////////////////////////////////
    // Wrapper alla classe che riceve i Layers
    //
    void onGetLayers ( String Name, int Values ) {
        if (dwgViewerActivity != null) {
            dwgViewerActivity.onGetLayers (Name, Integer.valueOf(Values));
        }
    }




    public void WriteCamera() {

    APPData.LAST_ENV_CAMERA_X = String.valueOf(GetCamera(0));
    sqliteWrapper.update_setup_record("LastEnviromentCamX", APPData.LAST_ENV_CAMERA_X);

    APPData.LAST_ENV_CAMERA_Y = String.valueOf(GetCamera(1));
    sqliteWrapper.update_setup_record("LastEnviromentCamY", APPData.LAST_ENV_CAMERA_Y);

    APPData.LAST_ENV_CAMERA_WX = String.valueOf(GetCamera(2));
    sqliteWrapper.update_setup_record("LastEnviromentCamWX", APPData.LAST_ENV_CAMERA_WX);

    APPData.LAST_ENV_CAMERA_WY = String.valueOf(GetCamera(3));
    sqliteWrapper.update_setup_record("LastEnviromentCamWY", APPData.LAST_ENV_CAMERA_WY);

    }

    public void ResetCamera() {

        APPData.LAST_ENV_CAMERA_X = "";
        sqliteWrapper.update_setup_record("LastEnviromentCamX", APPData.LAST_ENV_CAMERA_X);

        APPData.LAST_ENV_CAMERA_Y = "";
        sqliteWrapper.update_setup_record("LastEnviromentCamY", APPData.LAST_ENV_CAMERA_Y);

        APPData.LAST_ENV_CAMERA_WX = "";
        sqliteWrapper.update_setup_record("LastEnviromentCamWX", APPData.LAST_ENV_CAMERA_WX);

        APPData.LAST_ENV_CAMERA_WY = "";
        sqliteWrapper.update_setup_record("LastEnviromentCamWY", APPData.LAST_ENV_CAMERA_WY);

    }











    public void ExportDB() {

        try {

            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                File dbFile = context.getDatabasePath("imuve.db");

                String currentDBPath = dbFile.getAbsolutePath(); // "/data/" + getPackageName() + "/databases/yourdatabasename";
                String backupDBPath = "backupname.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();


                    String Folder = "/customers/Geisoft";
                    String FileName = "imuve.db";

                    FTPClient ftpClient = new FTPClient();

                    src = new FileInputStream(currentDB).getChannel();


                    try {

                        ftpClient.connect(InetAddress.getByName(Server));
                        ftpClient.login(UserName, Password);
                        ftpClient.changeWorkingDirectory(Folder);
                        String reply = ftpClient.getReplyString();

                        if (reply.contains("250") || reply.contains("200")) {

                            try {
                                DataOutputStream outputStreamWriter = new DataOutputStream(openFileOutput(FileName, Context.MODE_PRIVATE));
                                // OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(FileName, Context.MODE_PRIVATE));
                                ByteBuffer buffer = ByteBuffer.allocate(32000);
                                int pos = 0, size = 1;
                                while (size > 0) {
                                    buffer.rewind();
                                    size = src.read(buffer, pos);
                                    try {
                                        outputStreamWriter.write(buffer.array());
                                    } catch (Exception e) {}
                                    pos+=size;
                                }
                                outputStreamWriter.close();

                            } catch (IOException e) {
                                Log.e("Exception", "File write failed: " + e.toString());
                            }


                            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);

                            String locaFileName = getFilesDir() + "/" + FileName;

                            BufferedInputStream buffIn = new BufferedInputStream(new FileInputStream(locaFileName));
                            ftpClient.enterLocalPassiveMode();
                            // ProgressInputStream progressInput = new ProgressInputStream(buffIn, progressHandler);

                            boolean result = ftpClient.storeFile(FileName, buffIn);

                            buffIn.close();
                        }

                        ftpClient.logout();
                        ftpClient.disconnect();

                    } catch (SocketException e) {
                        System.err.println(e.getStackTrace().toString());
                    } catch (UnknownHostException e) {
                        System.err.println(e.getStackTrace().toString());
                    } catch (IOException e) {
                        System.err.println(e.getStackTrace().toString());
                    }

                    src.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Context getClassInstance() {
        return instance;
    }


    void messageMe(String text) { System.err.println("[C] DEBUG "+text); }

    public native float GetVersion ( );
    public native int LoadDwg ( byte[] data, int dataSize, int dwgID);
    public native int GetDwgID ( int Options );
    public native String GetError ();

    public native int DrawDwg(int curTab, int drawOptions);

    public native String DwgToCanvas(int width, int height);
    public native String DwgToXML(int Aux, int Mode);
    public native String DwgToJSON(int Aux, int Mode);

    public native int FindDwgText(String text, int Options);
    public native int FilterDwgText(String Filters, int numFilters, int Mode);
    public native String WrapText(String text, int Mode);

    public native float GetCamera(int Options);
    public native int SetCamera(float x, float y, float wx, float wy);
    public native int SetScreen(float wx, float wy);

    public native int Pan(float dx, float dy, boolean isTransacting);

    public native int Zoom(float x, float y, float scale, boolean isTransacting);
    public native int PushCamera();
    public native int PopCamera();

    public native int SetLayer(String text, boolean isON);
    public native int GetLayers();

    public native int onClickIntToDwg(float touchX, float touchY, int event, boolean Mode);

}





