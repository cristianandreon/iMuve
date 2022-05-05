package com.imuve.cristian.imuve;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


class SYNCTable {
    View view = null;
    ProgressDialog progress = null;
    String table_name = null;
    int RetVal = 0;
    String Message = null;

    Context context = null;
    Activity activity = null;
    boolean multiple = false;
}



public class ViewTablesActivity extends Activity {

    SYNCTable syncTable = new SYNCTable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tables);

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();


        try {

            try {

                mainActivity.setup_listview_complessi((Activity)ViewTablesActivity.this, R.id.lvGTComplessi, 0, true, 0);
                mainActivity.setup_listview_edifici((Activity)ViewTablesActivity.this, R.id.lvGTEdifici, 0, true, 0);
                mainActivity.setup_listview_piani((Activity)ViewTablesActivity.this, R.id.lvGTPiani, 0, true, 0);
                mainActivity.setup_listview_vani((Activity)ViewTablesActivity.this, R.id.lvGTVani, 0, true, 0);
                mainActivity.setup_listview_oggetti((Activity)ViewTablesActivity.this, R.id.lvGTOggetti, 0, true, 0);
                mainActivity.setup_listview_interventi((Activity)ViewTablesActivity.this, R.id.lvGTInterventi, 0, true, R.layout.rec_lv_3fields);
                mainActivity.setup_listview_queue((Activity)ViewTablesActivity.this, R.id.lvGTQueue, 0, true, 0);
                mainActivity.setup_listview_dwg((Activity)ViewTablesActivity.this, R.id.lvGTDwg, 0, true, R.layout.rec_lv_3fields);

            } catch (Exception e) {
                e.printStackTrace();
            }



            Button button = (Button) findViewById(R.id.btSyncComplessi);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    sync_table(ViewTablesActivity.this, v, "complessi", false);
                }
            });

            button = (Button) findViewById(R.id.btSyncEdifici);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    sync_table(ViewTablesActivity.this, v, "edifici", false);
                }
            });

            button = (Button) findViewById(R.id.btSyncPiani);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    sync_table(ViewTablesActivity.this, v, "piani", false);
                }
            });

            button = (Button) findViewById(R.id.btSyncVani);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    sync_table(ViewTablesActivity.this, v, "vani", false);
                }
            });

            button = (Button) findViewById(R.id.btSyncOggetti);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    sync_table(ViewTablesActivity.this, v, "oggetti", false);
                }
            });

            button = (Button) findViewById(R.id.btSyncInterventi);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    sync_table(ViewTablesActivity.this, v, "interventi", false);
                }
            });

            button = (Button) findViewById(R.id.btSyncDwg);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                    if (DialogBox.DialogBox("ATTENZIONE", "Questa operazione potrebbe impiegare parecchio tempo\r\nSincronizzare tutti i disegni dell'applicativo ?", 0 + 1 + 2, ViewTablesActivity.this)) {
                        sync_table(ViewTablesActivity.this, v, "dwg", false);
                    }
                }
            });

            button = (Button) findViewById(R.id.btSyncAll);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                    if (DialogBox.DialogBox("ATTENZIONE", "Questa operazione potrebbe impiegare molto tempo\r\nSi consiglia di effettuare questa operazione con la connessione wifi e la batteria in carica\n\r\nSincronizzare tutti le tabelle dell'applicazione ?", 0 + 1 + 2, ViewTablesActivity.this)) {
                        on_sync_all(v);
                    }
                }
            });


            button = (Button) findViewById(R.id.btSyncQueue);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    sync_table(ViewTablesActivity.this, v, "queue", false);
                }
            });




            // Cancellazione Tabelle
            button = (Button) findViewById(R.id.btDropQueue);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (DialogBox.DialogBox("ATTENZIONE", "Eliminare tutte le modifiche ?", 0+ 1+2, ViewTablesActivity.this)) {
                        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                        try {
                            mainActivity.sqliteWrapper.db.execSQL("DROP TABLE queue");
                        } catch (Exception e) {
                            System.err.print(e.getMessage());
                        }
                        mainActivity.setup_listview_queue((Activity) ViewTablesActivity.this, R.id.lvGTQueue, 0, true, 0);
                        do_refresh();
                    }
                }
            });



            ///////////////////////////////////
            // Immagine di sfondo
            //
            View backgroundimage = findViewById(R.id.background);
            if(backgroundimage != null) {
                Drawable background = backgroundimage.getBackground();
                background.setAlpha(40);
            }



            do_refresh();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void on_sync_all( View v ) {

        syncTable.view = v;
        syncTable.activity = ViewTablesActivity.this;
        syncTable.context = getApplicationContext();

        syncTable.progress = new ProgressDialog(ViewTablesActivity.this);

        try {
            syncTable.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            syncTable.progress.setMessage("Sincronizzazione in corso...");
            syncTable.progress.setIndeterminate(true);
            syncTable.progress.setCancelable(false);
            syncTable.progress.show();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    do_sync_all();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        mThread.start();
    }



    public void do_sync_all() {

        syncTable.Message = "";

        update_progress_message(syncTable.progress, "[Fase 0/7] Invio Interventi in corso...");

        if (APPQueueSQL.de_queue(null, "SERVER", null, null) != 1) {
            syncTable.Message += "Errore nell'invio degli interventi al server";
            }


        update_progress_message(syncTable.progress, "[Fase 1/7] Sincronizzazione Complessi in corso...");
        sync_table(syncTable.activity, syncTable.view, "complessi", true);

        update_progress_message(syncTable.progress, "[Fase 2/7] Sincronizzazione Edifici in corso...");
        sync_table(syncTable.activity, syncTable.view, "edifici", true);

        update_progress_message(syncTable.progress, "[Fase 3/7] Sincronizzazione Piani in corso...");
        sync_table(syncTable.activity, syncTable.view, "piani", true);

        update_progress_message(syncTable.progress, "[Fase 4/7] Sincronizzazione Vani in corso...");
        sync_table(syncTable.activity, syncTable.view, "vani", true);

        update_progress_message(syncTable.progress, "[Fase 5/7] Sincronizzazione Oggetti in corso...");
        sync_table(syncTable.activity, syncTable.view, "oggetti", true);

        update_progress_message(syncTable.progress, "[Fase 6/7] Sincronizzazione Interventi in corso...");
        sync_table(syncTable.activity, syncTable.view, "interventi", true);

        update_progress_message(syncTable.progress, "[Fase 7/7] Sincronizzazione Disegni in corso...");
        sync_table(syncTable.activity, syncTable.view, "disegni", true);



        Handler mainHandler = new Handler(syncTable.context.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                done_sync_all();
            }
        };
        mainHandler.post(myRunnable);

    }

    public void done_sync_all() {

        if (syncTable.progress != null)
            syncTable.progress.dismiss();

        if (syncTable.Message != null) {
            if (!syncTable.Message.isEmpty()) {
                DialogBox.DialogBox("ATTENZIONE", syncTable.Message, 0+0, ViewTablesActivity.this);
            }
        }


        //////////////////////////////////////////
        // Rinfresco liste pagina principale
        //
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        mainActivity.startup_list_view();

        /*
        mainActivity.setup_listview_complessi(syncTable.activity, R.id.lvGTComplessi, 0, true, 0);
        mainActivity.setup_listview_edifici(syncTable.activity, R.id.lvGTEdifici, 0, true, 0);
        mainActivity.setup_listview_piani(syncTable.activity, R.id.lvGTPiani, 0, true, 0);
        mainActivity.setup_listview_vani(syncTable.activity, R.id.lvGTVani, 0, true, 0);
        mainActivity.setup_listview_oggetti(syncTable.activity, R.id.lvGTOggetti, 0, true, 0);
        mainActivity.setup_listview_interventi(syncTable.activity, R.id.lvGTInterventi, 0, true, R.layout.rec_lv_3fields);
        mainActivity.setup_listview_dwg(syncTable.activity, R.id.lvGTDwg, 0, true, R.layout.rec_lv_3fields);
        mainActivity.setup_listview_queue(syncTable.activity, R.id.lvGTQueue, 0, true, R.layout.rec_lv_3fields);
        */



        do_refresh();


    }


    private void update_progress_message ( final ProgressDialog progressDialog, final String msg ) {
        Handler mainHandler = new Handler(syncTable.context.getMainLooper());
        Runnable update_progress_message_runnable = new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && msg != null) progressDialog.setMessage(msg);
            }
        };
        mainHandler.post(update_progress_message_runnable);
    }





    public boolean sync_table(Activity activity, View view, String table_name, boolean multiple) {

        syncTable.table_name = table_name;

        if (multiple) {

            do_sync_table();

        } else {

            syncTable.view = view;
            syncTable.activity = activity;
            syncTable.context = getApplicationContext();
            syncTable.multiple = false;


            syncTable.progress = new ProgressDialog(this);

            syncTable.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            syncTable.progress.setMessage("Sincronizzazione tabella " + table_name + " in corso...");
            syncTable.progress.setIndeterminate(true);
            syncTable.progress.setCancelable(false);
            syncTable.progress.show();


            Thread mThread = new Thread() {
                @Override
                public void run() {
                    try {
                        do_sync_table();
                    } catch (Exception e) {
                        syncTable.RetVal = -20;
                        syncTable.Message = "Exception error:" + e.getMessage();
                    }
                    try {
                        syncTable.progress.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!syncTable.multiple) {
                        Handler mainHandler = new Handler(syncTable.context.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                done_sync_table();
                            }
                        };
                        mainHandler.post(myRunnable);
                    }
                }
            };

            mThread.start();
        }

        return true;
    }





    public boolean do_sync_table() {
        syncTable.RetVal = 1;
        if (syncTable.table_name.compareToIgnoreCase("complessi")==0) {
            if (APPComplessiSQL.sincronizza_complessi(null, syncTable.context, syncTable.activity, syncTable) < 0) {
                syncTable.RetVal = -1;
            }
        } else if (syncTable.table_name.compareToIgnoreCase("edifici")==0) {
            if (APPEdificiSQL.sincronizza_edifici(null, syncTable.context, syncTable.activity, syncTable) < 0) {
                syncTable.RetVal = -1;
            }
        } else if (syncTable.table_name.compareToIgnoreCase("piani")==0) {
            if (APPPianiSQL.sincronizza_piani(null, syncTable.context, syncTable.activity, syncTable) < 0) {
                syncTable.RetVal = -1;
            }
        } else if (syncTable.table_name.compareToIgnoreCase("vani")==0) {
            if (APPVaniSQL.sincronizza_vani(null, syncTable.context, syncTable.activity, syncTable) < 0) {
                syncTable.RetVal = -1;
            }
        } else if (syncTable.table_name.compareToIgnoreCase("oggetti")==0) {
            if (APPOggettiSQL.sincronizza_oggetti(null, null, null, null, syncTable.context, syncTable.activity, syncTable) < 0) {
                syncTable.RetVal = -1;
            }
        } else if (syncTable.table_name.compareToIgnoreCase("interventi")==0) {
            if (APPInterventiSQL.sincronizza_interventi(null, null, null, null, null, syncTable.context, syncTable.activity, syncTable) < 0) {
                syncTable.RetVal = -1;
            }
        } else if (syncTable.table_name.compareToIgnoreCase("dwg")==0 || syncTable.table_name.compareToIgnoreCase("disegni")==0) {
            //TODO: Remove Test filter if present
            if (APPDwgSQL.sincronizza_dwg(null, syncTable.context, syncTable.activity, syncTable) < 0) {
                syncTable.RetVal = -1;
            }
        } else if (syncTable.table_name.compareToIgnoreCase("queue")==0) {
            if (APPInterventiSQL.leggi_interventi(null, null, null, null, 0, APPData.appInterventi) < 0) {
                syncTable.RetVal = -1;
            }
            if (APPQueueSQL.de_queue(null, "SERVER", null, null) < 0) {
                syncTable.RetVal = -2;
            }
        }

        return true;
    }





    public boolean done_sync_table() {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        if (syncTable.RetVal < 0) {
            if (syncTable.Message != null && !syncTable.Message.isEmpty()) {
                DialogBox.DialogBox("ATTENZIONE", syncTable.Message, 0 + 0, syncTable.activity);
            } else {
                DialogBox.DialogBox("ATTENZIONE", "Sincronizzazione fallita", 0 + 0, syncTable.activity);
            }

        } else {
            if (syncTable.table_name.compareToIgnoreCase("complessi")==0) {
                mainActivity.setup_listview_complessi(syncTable.activity, R.id.lvGTComplessi, 0, true, 0);
            } else if (syncTable.table_name.compareToIgnoreCase("edifici")==0) {
                mainActivity.setup_listview_edifici(syncTable.activity, R.id.lvGTEdifici, 0, true, 0);
            } else if (syncTable.table_name.compareToIgnoreCase("piani")==0) {
                mainActivity.setup_listview_piani(syncTable.activity, R.id.lvGTPiani, 0, true, 0);
            } else if (syncTable.table_name.compareToIgnoreCase("vani")==0) {
                mainActivity.setup_listview_vani(syncTable.activity, R.id.lvGTVani, 0, true, 0);
            } else if (syncTable.table_name.compareToIgnoreCase("oggetti")==0) {
                mainActivity.setup_listview_oggetti(syncTable.activity, R.id.lvGTOggetti, 0, true, 0);
            } else if (syncTable.table_name.compareToIgnoreCase("interventi")==0) {
                mainActivity.setup_listview_interventi(syncTable.activity, R.id.lvGTInterventi, 0, true, R.layout.rec_lv_3fields);
            } else if (syncTable.table_name.compareToIgnoreCase("dwg")==0) {
                mainActivity.setup_listview_dwg(syncTable.activity, R.id.lvGTDwg, 0, true, R.layout.rec_lv_3fields);
            } else if (syncTable.table_name.compareToIgnoreCase("queue") == 0) {
                mainActivity.setup_listview_queue(syncTable.activity, R.id.lvGTQueue, 0, true, R.layout.rec_lv_3fields);
            }
        }

        do_refresh();

        if (syncTable.progress != null)
            syncTable.progress.dismiss();

        return true;
    }






    private  void do_refresh() {

    TextView tv = (TextView) findViewById(R.id.tvComplessi);
    tv.setText("Coda [" + APPData.IDQueue.size() + "]");


    tv = (TextView) findViewById(R.id.tvComplessi);
    tv.setText("Complessi [" + APPData.IDComplessi.size() + "]");
    tv = (TextView) findViewById(R.id.tvComplessiDataAgg);
    tv.setText(APPData.LAST_READ_COMPLESSO);

    tv = (TextView) findViewById(R.id.tvEdifici);
    tv.setText("Edifici [" + APPData.IDEdifici.size() + "]");
    tv = (TextView) findViewById(R.id.tvEdificiDataAgg);
    tv.setText(APPData.LAST_READ_EDIFICIO);

    tv = (TextView) findViewById(R.id.tvPiani);
    tv.setText("Piani [" + APPData.IDPiani.size() + "]");
    tv = (TextView) findViewById(R.id.tvPianiDataAgg);
    tv.setText(APPData.LAST_READ_PIANO);

    tv = (TextView) findViewById(R.id.tvVani);
    tv.setText("Vani [" + APPData.IDVani.size() + "]");
    tv = (TextView) findViewById(R.id.tvVaniDataAgg);
    tv.setText(APPData.LAST_READ_VANO);

    tv = (TextView) findViewById(R.id.tvOggetti);
    tv.setText("Oggetti [" + APPData.appOggetti.IDOggetti.size() + "]");
    tv = (TextView) findViewById(R.id.tvOggettiDataAgg);
    tv.setText(APPData.LAST_READ_OGGETTO);

    tv = (TextView) findViewById(R.id.tvInterventi);
    tv.setText("Interventi [" + APPData.appInterventi.IDInterventi.size() + "]");
    tv = (TextView) findViewById(R.id.tvInterventiDataAgg);
    tv.setText(APPData.LAST_READ_INTERVENTO);

    tv = (TextView) findViewById(R.id.tvDisegni);
    tv.setText("Disegni [" + APPData.IDDwg.size() + "]");
    tv = (TextView) findViewById(R.id.tvDwgDataAgg);
    tv.setText(APPData.LAST_READ_DWG);
}



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_tables, menu);
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
}
