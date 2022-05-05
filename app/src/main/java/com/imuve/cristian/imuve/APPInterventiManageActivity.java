package com.imuve.cristian.imuve;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;




public class APPInterventiManageActivity extends ActionBarActivity {

    public int NumInterventi = 0;

    public String fltCodOggetto = null;
    public String fltDescOggetto = null;
    public String fltCodIntervento = null;
    public String fltDescIntervento = null;
    public String fltDescEstIntervento = null;
    public String fltDataAperturaIntervento = null;
    public String fltDataScadenzaIntervento = null;
    public String fltDataChiusuraIntervento = null;
    public String fltStatoIntervento = null;

    public String fltEdificio = null;
    public String fltPiano = null;
    public String fltVano = null;

    public String fltQueueIntervento = null;

    public String OnOKCallback = null;

    private ListView GLlv = null;

    private APPInterventi appInterventi = null;
    private int NumVctInterventi = 0;
    private int cIntervento = -1;
    private ArrayList<Integer> VctInterventi = null;


    private boolean GLEnableRealtimeFilter = true;

    private boolean JustCreated = false;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        reset_env_data();
    }


    @Override
    public void onResume() {
        super.onResume();
        getWindow().getDecorView().setAlpha(1.0f);
        if (JustCreated) {
            JustCreated = false;
        } else {
            leggi_interventi();
            filter_and_refresh();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();


        super.onCreate(savedInstanceState);

        JustCreated = true;


        Bundle extras = getIntent().getExtras();


        try {
            OnOKCallback = extras.getString("OnOKCallback");
        } catch (Exception e) {
            e.printStackTrace();
        }


        ///////////////////////////
        // Lettura Interventi
        //

        if (appInterventi == null) appInterventi = new APPInterventi();


        leggi_interventi();


        NumVctInterventi = 0;
        if (VctInterventi == null) VctInterventi = new ArrayList<Integer>();


        // Titolo activity
        try {

            android.support.v7.app.ActionBar actionBar = getSupportActionBar();

            // Imposta la personalizzazione dell'actionBar
            actionBar.setCustomView(R.layout.interventi_manage_actionbar_item);

            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);

            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);

            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            // actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.title_bar_gray)));

            String Title = "Gestione interventi [" + appInterventi.NumInterventi + " elementi]...Clicca sulle etichette per selezionare da un elenco";
            actionBar.setTitle(Title);

            TextView ev = ((TextView) this.findViewById(R.id.etActbarTitle));
            if (ev != null) {
                ev.setText(Title);
            }

            actionBar.show();

        } catch (Exception e) {
            e.printStackTrace();
            ;
        }

        setContentView(R.layout.activity_interventi_manage);


        GLlv = (ListView) findViewById(R.id.lvOggettiSelection);


        final MyRunnable after_list_selected = new MyRunnable(null, null) {
            @Override
            public void run() {
                if (myParam != null) {
                    if (myID != null) {
                        EditText et = ((EditText) findViewById(myID));
                        if (et != null) {
                            et.setText(myParam);
                        }
                    }
                }
            }
        };


        try {

            EditText et = ((EditText) this.findViewById(R.id.etOggetto));
            if (et != null) {
                et.setText(fltCodOggetto);
                et.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        fltCodOggetto = s.toString();
                        filter_and_refresh();
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                et.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.CodOggettoInterventi, VctInterventi, NumVctInterventi, "Seleziona un Oggetto...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etOggetto);
                        return true;
                    }
                });
            }

            et = ((EditText) this.findViewById(R.id.etDescOggetto));
            if (et != null) {
                et.setText(fltDescOggetto);
                et.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        fltDescOggetto = s.toString();
                        filter_and_refresh();
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                et.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.DescOggettoInterventi, VctInterventi, NumVctInterventi, "Seleziona un Oggetto...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etDescOggetto);
                        return true;
                    }
                });
            }

            et = ((EditText) this.findViewById(R.id.etCodIntervento));
            if (et != null) {
                et.setText(fltCodIntervento);
                et.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        fltCodIntervento = s.toString();
                        filter_and_refresh();
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                et.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.CodInterventi, VctInterventi, NumVctInterventi, "Seleziona un intervento...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etVano);
                        return true;
                    }
                });
            }

            et = ((EditText) this.findViewById(R.id.etDescIntervento));
            if (et != null) {
                et.setText(fltDescIntervento);
                et.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        fltDescIntervento = s.toString();
                        filter_and_refresh();
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                et.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.DescInterventi, VctInterventi, NumVctInterventi, "Seleziona un Intervento...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etDescIntervento);
                        return true;
                    }
                });
            }

            et = ((EditText) this.findViewById(R.id.etStato));
            if (et != null) {
                et.setText(fltStatoIntervento);
                et.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        fltStatoIntervento = s.toString();
                        filter_and_refresh();
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                et.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.StatoIntervento, VctInterventi, NumVctInterventi, "Seleziona uno Stato...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etStato);
                        return true;
                    }
                });
            }

            et = ((EditText) this.findViewById(R.id.etDataApertura));
            if (et != null) {
                et.setText(fltDataAperturaIntervento);
                et.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        fltDataAperturaIntervento = s.toString();
                        filter_and_refresh();
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                et.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.DataAperturaIntervento, VctInterventi, NumVctInterventi, "Seleziona una Data...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etDataApertura);
                        return true;
                    }
                });
            }

            et = ((EditText) this.findViewById(R.id.etDataChiusura));
            if (et != null) {
                et.setText(fltDataChiusuraIntervento);
                et.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        fltDataChiusuraIntervento = s.toString();
                        filter_and_refresh();
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                et.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.DataChiusuraIntervento, VctInterventi, NumVctInterventi, "Seleziona una Data...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etDataChiusura);
                        return true;
                    }
                });
            }

            et = ((EditText) this.findViewById(R.id.etDataScadenza));
            if (et != null) {
                et.setText(fltDataScadenzaIntervento);
                et.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        fltDataScadenzaIntervento = s.toString();
                        filter_and_refresh();
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                et.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.DataScadenzaIntervento, VctInterventi, NumVctInterventi, "Seleziona una Data...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etDataScadenza);
                        return true;
                    }
                });
            }

            et = ((EditText) this.findViewById(R.id.etEdificio));
            if (et != null) {
                et.setText(fltEdificio);
                et.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        fltEdificio = s.toString();
                        filter_and_refresh();
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                et.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.EdificioInterventi, VctInterventi, NumVctInterventi, "Seleziona una Edificio...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etEdificio);
                        return true;
                    }
                });
            }

            et = ((EditText) this.findViewById(R.id.etPiano));
            if (et != null) {
                et.setText(fltPiano);
                et.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        fltPiano = s.toString();
                        filter_and_refresh();
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                et.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.PianoInterventi, VctInterventi, NumVctInterventi, "Seleziona una Piano...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etPiano);
                        return true;
                    }
                });
            }
            et = ((EditText) this.findViewById(R.id.etVano));
            if (et != null) {
                et.setText(fltVano);
                et.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        fltVano = s.toString();
                        filter_and_refresh();
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                et.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.VanoInterventi, VctInterventi, NumVctInterventi, "Seleziona una Vano...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etVano);
                        return true;
                    }
                });
            }


            CheckBox ck = ((CheckBox) this.findViewById(R.id.ckToSync));
            if (ck != null) {
                ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        fltQueueIntervento = isChecked ? "1" : "0";
                        filter_and_refresh();
                    }
                });
            }


            /////////////////////////////////
            // Filtri sulle etichette
            //
            TextView tv = ((TextView) this.findViewById(R.id.tvOggetto));
            if (tv != null) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.CodOggettoInterventi, VctInterventi, NumVctInterventi, "Seleziona un Oggetto...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etOggetto);
                    }
                });
            }

            tv = ((TextView) this.findViewById(R.id.tvInterventi));
            if (tv != null) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.CodInterventi, VctInterventi, NumVctInterventi, "Seleziona un intervento...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etVano);
                    }
                });
            }

            tv = ((TextView) this.findViewById(R.id.tvDescOggetto));
            if (tv != null) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.DescOggettoInterventi, VctInterventi, NumVctInterventi, "Seleziona un Oggetto...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etDescOggetto);
                    }
                });
            }

            tv = ((TextView) this.findViewById(R.id.tvDescintervento));
            if (tv != null) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.DescInterventi, VctInterventi, NumVctInterventi, "Seleziona un Intervento...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etDescIntervento);
                    }
                });
            }

            tv = ((TextView) this.findViewById(R.id.tvStato));
            if (tv != null) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.StatoIntervento, VctInterventi, NumVctInterventi, "Seleziona uno Stato...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etStato);
                    }
                });
            }

            tv = ((TextView) this.findViewById(R.id.tvDataApertura));
            if (tv != null) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.DataAperturaIntervento, VctInterventi, NumVctInterventi, "Seleziona una Data...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etDataApertura);
                    }
                });
            }

            tv = ((TextView) this.findViewById(R.id.tvDataChiusura));
            if (tv != null) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.DataChiusuraIntervento, VctInterventi, NumVctInterventi, "Seleziona una Data...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etDataChiusura);
                    }
                });
            }

            tv = ((TextView) this.findViewById(R.id.tvDataScadenza));
            if (tv != null) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.DataScadenzaIntervento, VctInterventi, NumVctInterventi, "Seleziona una Data...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etDataScadenza);
                    }
                });
            }

            tv = ((TextView) this.findViewById(R.id.tvEdificio));
            if (tv != null) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.EdificioInterventi, VctInterventi, NumVctInterventi, "Seleziona una Edificio...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etEdificio);
                    }
                });
            }

            tv = ((TextView) this.findViewById(R.id.tvPiano));
            if (tv != null) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.PianoInterventi, VctInterventi, NumVctInterventi, "Seleziona una Piano...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etPiano);
                    }
                });
            }

            tv = ((TextView) this.findViewById(R.id.tvVano));
            if (tv != null) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        APPUtil.getSelectFromListview(appInterventi.VanoInterventi, VctInterventi, NumVctInterventi, "Seleziona una Vano...", APPInterventiManageActivity.this, null, after_list_selected, R.id.etVano);
                    }
                });
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


        read_and_filter_interventi();


        try {

            if (GLlv != null) {
                SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, appInterventi.ExtDescInterventi, appInterventi.DescInterventi, appInterventi.CodInterventi, R.layout.rec_lv_12fields);
                ListAdapter.ExtendRecordAdapterRows(appInterventi.CodOggettoInterventi, appInterventi.DescOggettoInterventi, appInterventi.StatoIntervento, appInterventi.DataAperturaIntervento, appInterventi.DataChiusuraIntervento, appInterventi.DataScadenzaIntervento, appInterventi.EdificioInterventi, appInterventi.PianoInterventi, appInterventi.VanoInterventi);
                ListAdapter.ExtendRecordAdapterlabels("Codice", "Descrizione", "Desc.estesa", "Oggetto", "Desc.oggetto", "Stato", "Apertura", "Chiusura", "Scadenza", "Edificio", "Piano", "Vano");

                GLlv.setAdapter(ListAdapter);

                GLlv.setOnItemLongClickListener(new OnItemLongClickListener() {
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        run_interventi_activity(position);
                        return true;
                    }
                });


                // register onClickListener to handle click events on each item
                GLlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                        if (position < appInterventi.IDInterventi.size()) {
                            SingleRecordAdapter sa = (SingleRecordAdapter) arg0.getAdapter();

                            sa.curItem = position;

                            cIntervento = position;

                            ListView lv = (ListView) arg0;

                            lv.setItemChecked(position, true);

                            v.setBackgroundColor(Color.BLUE);
                        }
                    }
                });
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


        Button button = (Button) findViewById(R.id.btOk);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onOK(v);
                }
            });
        }

        button = (Button) findViewById(R.id.btCancel);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onCancel(v);
                }
            });
        }


        button = (Button) findViewById(R.id.btSync);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    syncronize();
                }
            });
        }

        button = (Button) findViewById(R.id.btSyncAll);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    syncronize_all();
                }
            });
        }


        ///////////////////////////////////
        // Immagine di sfondo
        //
        View backgroundimage = findViewById(R.id.background);
        if (backgroundimage != null) {
            Drawable background = backgroundimage.getBackground();
            background.setAlpha(40);
        }


        ////////////////////////////////////////////////
        // Registrazione coordinate Ambiente
        //
        store_env_param();
    }


    private void leggi_interventi() {

    String fltIDEdificio = null; // APPData.IDEdifici.get(APPData.cEdificio);
    String fltIDComplesso = null;

    try {
        fltIDComplesso = APPData.cComplesso >= 0 ? String.valueOf(APPData.IDComplessi.get(APPData.cComplesso)) : null;
    } catch (Exception e) { }

    APPInterventiSQL.leggi_interventi(fltIDComplesso, fltIDEdificio, null, null, null, appInterventi);
}


    private void store_env_param() {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        APPData.LAST_ENV_ON_DWG = "GestioneInterventi";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWG", APPData.LAST_ENV_ON_DWG);
    }

    public void reset_env_data() {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        APPData.LAST_ENV_ON_DWG = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWG", APPData.LAST_ENV_ON_DWG);
    }




    public void run_interventi_activity(int position) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        try {

            if (position >= 0) {
                SingleRecordAdapter sa = (SingleRecordAdapter) GLlv.getAdapter();
                if (sa.curItem != position) {
                    sa.curItem = position;
                }
            }

            this.getWindow().getDecorView().setAlpha(0.3f);

            Handler mainHandler = new Handler(mainActivity.context.getMainLooper());


            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {

                    SingleRecordAdapter sa = (SingleRecordAdapter) GLlv.getAdapter();
                    if (sa.curItem >= 0) {
                        int iRec = VctInterventi != null ? VctInterventi.get(sa.curItem) : sa.curItem;
                        String CodInterventi = null;
                        // int LAST_SEL_IDOGGETTO = -1;

                        try {
                            String sIDOggettoInterventi = appInterventi.IDOggettoInterventi.get(iRec);
                            if (sIDOggettoInterventi != null) {
                                APPData.LAST_SEL_IDOGGETTO = Integer.valueOf(sIDOggettoInterventi);
                            }
                        } catch (Exception e) {
                            APPData.LAST_SEL_IDOGGETTO = -1;
                        }

                        if (APPData.LAST_SEL_IDOGGETTO < 0) {
                            DialogBox.DialogBox("ATTENZIONE", "ID Oggetto non valido\r\nL'oggetto al quale si riferisce l'intervento non e' definito", 0 + 0, APPInterventiManageActivity.this);
                            return;
                        }



                        try {
                            CodInterventi = appInterventi.CodInterventi.get(iRec);
                        } catch (Exception e) {
                            CodInterventi = null;
                        }

                        if (CodInterventi == null) {
                            DialogBox.DialogBox("ATTENZIONE", "Codice Intervento non valido", 0 + 0, APPInterventiManageActivity.this);
                            return;
                        }


                        try {
                            APPData.LAST_SEL_IDCOMPLESSO = Integer.valueOf(appInterventi.IDComplessoInterventi.get(iRec));
                            APPData.LAST_SEL_IDEDIFICIO = Integer.valueOf(appInterventi.IDOggettoInterventi.get(iRec));
                            APPData.LAST_SEL_IDPIANO = Integer.valueOf(appInterventi.IDOggettoInterventi.get(iRec));
                            APPData.LAST_SEL_IDVANO = Integer.valueOf(appInterventi.IDOggettoInterventi.get(iRec));
                        } catch (Exception e) {
                            DialogBox.DialogBox("ATTENZIONE", "Dati Complesso/Edificio/Paino/Vano non validi", 0 + 0, APPInterventiManageActivity.this);
                            return;
                        }

                        try {
                            APPData.LAST_SEL_OGGETTO = appInterventi.CodOggettoInterventi.get(iRec);
                            APPData.LAST_SEL_OGGETTO_DESC = appInterventi.DescOggettoInterventi.get(iRec);
                        } catch (Exception e) {
                        }

                        try {
                            APPData.LAST_SEL_COMPLESSO = APPData.Complessi.get(APPData.iIndexOf(APPData.IDComplessi, APPData.LAST_SEL_IDCOMPLESSO));
                        } catch (Exception e) {
                        }
                        try {
                            APPData.LAST_SEL_EDIFICIO = appInterventi.EdificioInterventi.get(iRec);
                        } catch (Exception e) {
                        }
                        try {
                            APPData.LAST_SEL_PIANO = appInterventi.PianoInterventi.get(iRec);
                        } catch (Exception e) {
                        }
                        try {
                            APPData.LAST_SEL_VANO = appInterventi.VanoInterventi.get(iRec);
                        } catch (Exception e) {
                        }

                        Intent myIntent = new Intent(APPInterventiManageActivity.this, APPInterventiActivity.class);

                        try {

                            APPData.nuovoIntervento = false;
                            myIntent.putExtra("ObjectID", APPData.LAST_SEL_IDOGGETTO);
                            myIntent.putExtra("ObjectCode", APPData.LAST_SEL_OGGETTO);
                            myIntent.putExtra("ObjectDesc", APPData.LAST_SEL_OGGETTO_DESC);
                            myIntent.putExtra("InterventoCod", CodInterventi);
                            myIntent.putExtra("cIntervento", "SearchByCod");


                            myIntent.putExtra("nuovoIntervento", "");


                        } catch (Exception e) {
                        }

                        startActivity(myIntent);
                    }
                }
            };

            // APPInterventiManageActivity.this.runOnUiThread(myRunnable);

            mainHandler.post(myRunnable);

        } catch(Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appinterventi_manage, menu);
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


        } else if (id == R.id.action_sync) {
            syncronize_all();


        } else if (id == R.id.action_sync_all) {
            syncronize();

        } else if (id == R.id.action_locate) {
            MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
            int n = VctInterventi!=null?NumVctInterventi:NumInterventi;
            if (n > 0) {
                if (cIntervento >= 0 && cIntervento < n) {
                    int iRec = VctInterventi != null ? VctInterventi.get(cIntervento) : cIntervento;
                    String sOggetto = null;
                    Integer IDPiano = -1;

                    // if (DialogBox.DialogBox("ATTENZIONE", "Sincronizzare l'intervento selezionato ?", 0 + 1 + 2, this)) {
                    // }
                    try {
                        IDPiano = Integer.valueOf(appInterventi.IDPainoInterventi.get(iRec));
                        sOggetto = appInterventi.CodOggettoInterventi.get(iRec);
                    } catch (Exception e) {
                        IDPiano = -1;
                        e.printStackTrace();
                    }


                    if (IDPiano < 0) {
                        DialogBox.DialogBox("ATTENZIONE", "Piano non associato all'intervento!", 0 + 0, APPInterventiManageActivity.this);
                    } else {
                        int cPiano = APPData.iIndexOf(APPData.IDPiani, IDPiano);
                        if (cPiano >= 0) {
                            mainActivity.openDWG(this.getWindow().getDecorView(), false, true, cPiano, false, true, sOggetto, APPInterventiManageActivity.this, getApplicationContext(), null);
                        }
                    }
                }
            }

        } else if (id == R.id.action_reset_filter) {

            GLEnableRealtimeFilter = false;

            TextView tv = ((TextView) this.findViewById(R.id.etOggetto));
            if (tv != null) {
                tv.setText("");
            }
            tv = ((TextView) this.findViewById(R.id.etCodIntervento));
            if (tv != null) {
                tv.setText("");
            }
            tv = ((TextView) this.findViewById(R.id.etDescOggetto));
            if (tv != null) {
                tv.setText("");
            }
            tv = ((TextView) this.findViewById(R.id.etDescIntervento));
            if (tv != null) {
                tv.setText("");
            }
            tv = ((TextView) this.findViewById(R.id.etStato));
            if (tv != null) {
                tv.setText("");
            }

            tv = ((TextView) this.findViewById(R.id.etDataApertura));
            if (tv != null) {
                tv.setText("");
            }

            tv = ((TextView) this.findViewById(R.id.etDataChiusura));
            if (tv != null) {
                tv.setText("");
            }

            tv = ((TextView) this.findViewById(R.id.etDataScadenza));
            if (tv != null) {
                tv.setText("");
            }

            tv = ((TextView) this.findViewById(R.id.etEdificio));
            if (tv != null) {
                tv.setText("");
            }

            tv = ((TextView) this.findViewById(R.id.etPiano));
            if (tv != null) {
                tv.setText("");
            }

            tv = ((TextView) this.findViewById(R.id.etVano));
            if (tv != null) {
                tv.setText("");
            }

            GLEnableRealtimeFilter = true;

            filter_and_refresh();


        } else if (id == R.id.action_open) {
            if (GLlv != null) {
                run_interventi_activity(-1);
            }


        } else if (id == R.id.action_exit) {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }



    public void onOK(View v) {

        APPData.LAST_ENV_ON_DWG = "";
        finish();

        if (OnOKCallback != null) {
            if (OnOKCallback.compareToIgnoreCase("...") == 0) {
                // MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                // mainActivity...;
            }
        }
    }

    public void onCancel(View v) {
        APPData.LAST_ENV_ON_DWG = "";
        finish();
    }



    public int syncronize() {
        int n = VctInterventi!=null?NumVctInterventi:NumInterventi;
        if (n > 0) {
            if (cIntervento >= 0 && cIntervento < n) {
                int iRec = VctInterventi != null ? VctInterventi.get(cIntervento) : cIntervento;

                // if (DialogBox.DialogBox("ATTENZIONE", "Sincronizzare l'intervento selezionato ?", 0 + 1 + 2, this)) {
                // }
                try {
                    int IDInterventiOnQueue = appInterventi.IDInterventiOnQueue.get(iRec);
                    if (IDInterventiOnQueue > 0) {
                        String InterventoID = appInterventi.IDInterventi.get(iRec);
                        if (APPQueueSQL.de_queue(null, "SERVER", IDInterventiOnQueue, InterventoID) < 1) {
                            DialogBox.DialogBox("ATTENZIONE", "Sincronizzazione Intervento fallita!", 0 + 0, this);
                        } else {
                            leggi_interventi();
                            filter_and_refresh();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            // return syncronize_all();
        }

        return 1;
    }



    public int syncronize_all() {
        int n = VctInterventi!=null?NumVctInterventi:NumInterventi, n_err = 0;
        if (n > 0) {
            if (DialogBox.DialogBox("ATTENZIONE", "Sincronizzare tutti gli interventi visualizzati [" + n + " elementi]?", 0 + 1 + 2, this)) {
                for (int i = 0; i < n; i++) {
                    int iRec = VctInterventi != null ? VctInterventi.get(i) : i;
                    if (iRec < appInterventi.NumInterventi) {
                        int IDInterventiOnQueue = appInterventi.IDInterventiOnQueue.get(iRec);
                        if (IDInterventiOnQueue > 0 ) {
                            if (APPQueueSQL.de_queue(null, "SERVER", IDInterventiOnQueue, appInterventi.IDInterventi.get(iRec)) != 1) {
                                n_err++;
                            }
                        } else {
                        }
                    }
                }

                //////////////////////////////////////////////////////////
                // Lettura eventuali interventi presenti nel server
                //
                if (APPInterventiSQL.sincronizza_interventi(null, null, null, null, null, getApplicationContext(), this, null) > 0) {
                    appInterventi.cIntervento = -1;
                }

                String fltIDEdificio = null; // APPData.IDEdifici.get(APPData.cEdificio);
                String fltIDComplesso = String.valueOf(APPData.IDComplessi.get(APPData.cComplesso));

                APPInterventiSQL.leggi_interventi(fltIDComplesso, fltIDEdificio, null, null, null, appInterventi);

                leggi_interventi();
                filter_and_refresh();

                if (n_err > 0) {
                    String str = " Sincronizzazione Interventi fallita [" + n_err + " errori]";
                    DialogBox.DialogBox("ATTENZIONE", str, 0 + 0, this);
                }
            }
        }
        return 1;
    }





    public int filter_and_refresh() {

        if (GLEnableRealtimeFilter) {

            read_and_filter_interventi();

            if (GLlv != null) {
                SingleRecordAdapter sa = (SingleRecordAdapter) GLlv.getAdapter();
                sa.SetRecordAdapterRows(appInterventi.DescInterventiAux, appInterventi.ExtDescInterventi, appInterventi.CodInterventi);
                sa.ExtendRecordAdapterRows(appInterventi.CodOggettoInterventi, appInterventi.DescOggettoInterventi, appInterventi.StatoIntervento, appInterventi.DataAperturaIntervento, appInterventi.DataChiusuraIntervento, appInterventi.DataScadenzaIntervento, appInterventi.EdificioInterventi, appInterventi.PianoInterventi, appInterventi.VanoInterventi);
                sa.SetFilterRecordAdapterRows(VctInterventi, NumVctInterventi);

                sa.notifyDataSetChanged();
                GLlv.invalidateViews();
                sa.curItem = -1;
            }

            this.cIntervento = -1;

            String Title = "Gestione Interventi: " + NumVctInterventi + "/" + NumInterventi;

            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(Title);

            TextView ev = ((TextView) this.findViewById(R.id.etActbarTitle));
            if (ev != null) {
                ev.setText(Title);
            }
        }

        return 1;
    }




    public int read_and_filter_interventi() {

        try {

            NumInterventi = appInterventi.NumInterventi;
            NumVctInterventi = 0;
            if (VctInterventi==null) VctInterventi = new ArrayList<Integer>();
            VctInterventi.clear();

            for (int i = 0; i < appInterventi.IDInterventi.size(); i++) {
                boolean fltOut = false;
                if (fltCodOggetto != null && !fltCodOggetto.isEmpty()) {
                    if (!APPUtil.compareIgnoreCase(appInterventi.CodOggettoInterventi.get(i), fltCodOggetto)) {
                        fltOut = true;
                    }
                }
                if (fltDescOggetto != null && !fltDescOggetto.isEmpty()) {
                    if (!APPUtil.compareIgnoreCase(appInterventi.DescOggettoInterventi.get(i), fltDescOggetto)) {
                        fltOut = true;
                    }
                }
                if (fltCodIntervento != null && !fltCodIntervento.isEmpty()) {
                    if (!APPUtil.compareIgnoreCase(appInterventi.CodInterventi.get(i), fltCodIntervento)) {
                        fltOut = true;
                    }
                }
                if (fltDescIntervento != null && !fltDescIntervento.isEmpty()) {
                    if (!APPUtil.compareIgnoreCase(appInterventi.DescInterventi.get(i), fltDescIntervento)) {
                        fltOut = true;
                    }
                }

                if (fltDescEstIntervento != null && !fltDescEstIntervento.isEmpty()) {
                    if (!APPUtil.compareIgnoreCase(appInterventi.ExtDescInterventi.get(i), fltDescEstIntervento)) {
                        fltOut = true;
                    }
                }

                if (fltDescEstIntervento != null && !fltDescEstIntervento.isEmpty()) {
                    if (!APPUtil.compareIgnoreCase(appInterventi.ExtDescInterventi.get(i), fltDescEstIntervento)) {
                        fltOut = true;
                    }
                }
                if (fltDataAperturaIntervento != null && !fltDataAperturaIntervento.isEmpty()) {
                    if (!APPUtil.compareIgnoreCase(appInterventi.DataAperturaIntervento.get(i), fltDataAperturaIntervento)) {
                        fltOut = true;
                    }
                }
                if (fltDataScadenzaIntervento != null && !fltDataScadenzaIntervento.isEmpty()) {
                    if (!APPUtil.compareIgnoreCase(appInterventi.DataScadenzaIntervento.get(i), fltDataScadenzaIntervento)) {
                        fltOut = true;
                    }
                }
                if (fltDataChiusuraIntervento != null && !fltDataChiusuraIntervento.isEmpty()) {
                    if (!APPUtil.compareIgnoreCase(appInterventi.DataChiusuraIntervento.get(i), fltDataChiusuraIntervento)) {
                        fltOut = true;
                    }
                }
                if (fltStatoIntervento != null && !fltStatoIntervento.isEmpty()) {
                    if (!APPUtil.compareIgnoreCase(appInterventi.StatoIntervento.get(i), fltStatoIntervento)) {
                        fltOut = true;
                    }
                }
                if (fltEdificio != null && !fltEdificio.isEmpty()) {
                    if (!APPUtil.compareIgnoreCase(appInterventi.EdificioInterventi.get(i), fltEdificio)) {
                        fltOut = true;
                    }
                }
                if (fltPiano != null && !fltPiano.isEmpty()) {
                    if (!APPUtil.compareIgnoreCase(appInterventi.PianoInterventi.get(i), fltPiano)) {
                        fltOut = true;
                    }
                }
                if (fltVano != null && !fltVano.isEmpty()) {
                    if (!APPUtil.compareIgnoreCase(appInterventi.VanoInterventi.get(i), fltVano)) {
                        fltOut = true;
                    }
                }

                if (fltQueueIntervento != null && !fltQueueIntervento.isEmpty()) {
                    if (fltQueueIntervento.compareToIgnoreCase("1") == 0) {
                        // In coda
                        if (appInterventi.IDInterventiOnQueue.get(i) > 0) {
                        } else {
                            fltOut = true;
                        }
                    } else {
                        // NON In coda
                        if (appInterventi.IDInterventiOnQueue.get(i) > 0) {
                            fltOut = true;
                        }
                    }
                }

                if (fltOut) {
                    // appInterventi.IDInterventi.set(i, null);
                } else {
                    NumVctInterventi++;
                    VctInterventi.add(i);
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    return 1;
    }



}




