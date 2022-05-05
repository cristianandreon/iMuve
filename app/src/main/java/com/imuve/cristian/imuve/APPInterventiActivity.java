package com.imuve.cristian.imuve;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class APPInterventiActivity extends ActionBarActivity {

    public String ObjectID = null;
    public String ObjectCode = null;
    public String ObjectDesc = null;
    public String Key = null;

    String CodInterventi = null;
    String StatoIntervento = null;
    String DescInterventi = null;
    String ExtDescInterventi = null;

    public String DataAperturaIntervento = null;
    public String DataScadenzaIntervento = null;
    public String DataChiusuraIntervento = null;


    public int cIntervento = -1;

    private ArrayList<String> CodInterventiList = null;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        reset_env_data();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        Bundle extras = getIntent().getExtras();

        super.onCreate(savedInstanceState);


        cIntervento = 0;


        // Lettura inteventi
        // leggi_interventi();
        APPInterventiSQL.leggi_interventi(null, null, null, null, APPData.LAST_SEL_IDOGGETTO, APPData.appInterventi);

        if (APPData.appInterventi.NumInterventi <= 0) {
            APPData.nuovoIntervento = true;
            cIntervento = -1;
        } else {
            APPData.nuovoIntervento = false;
        }

        String nuovoIntervento = extras.getString("nuovoIntervento");
        if (nuovoIntervento != null && !nuovoIntervento.isEmpty()) {
            APPData.nuovoIntervento = Boolean.parseBoolean(nuovoIntervento);
            if (APPData.nuovoIntervento)cIntervento = -1;
        }

        if (APPData.AVVIO_NUOVO_INTERVENTO != null && APPData.AVVIO_NUOVO_INTERVENTO.compareToIgnoreCase("1") == 0) {
            APPData.nuovoIntervento = true;
            cIntervento = -1;
        }



        if (cIntervento >= APPData.appInterventi.NumInterventi) {
            cIntervento = APPData.appInterventi.NumInterventi - 1;
        }


        ObjectID = extras.getString("ObjectID");
        ObjectCode = extras.getString("ObjectCode");
        ObjectDesc = extras.getString("ObjectDesc");
        Key = extras.getString("Key");



        if (APPData.nuovoIntervento) {
        } else {
            String scIntervento = extras.getString("cIntervento");
            if (scIntervento != null && !scIntervento.isEmpty()) {
                Integer curIntervento = 0;

                try {
                    curIntervento = Integer.parseInt(scIntervento);
                } catch (Exception e) {
                    curIntervento = 0;
                }


                if (curIntervento > 0 && curIntervento < APPData.appInterventi.NumInterventi) {
                    cIntervento = curIntervento;
                } else if (scIntervento.compareToIgnoreCase("SearchByCod") == 0) {
                    try {
                        String InterventoID = extras.getString("InterventoCod");
                        cIntervento = APPData.sIndexOf(APPData.appInterventi.CodInterventi, InterventoID);
                    } catch (Exception e) {
                    }
                }
            }
        }




        if (ObjectID == null || ObjectID.isEmpty()) {
            ObjectID = String.valueOf(APPData.LAST_SEL_IDOGGETTO);
        }
        if (ObjectCode == null || ObjectCode.isEmpty()) {
            ObjectCode = APPData.LAST_SEL_OGGETTO;
        }
        if (ObjectDesc == null || ObjectDesc.isEmpty()) {
            ObjectDesc = APPData.LAST_SEL_OGGETTO_DESC;
        }



        rebuild_spinner(false);


        setup_nuovo_intervento(APPData.nuovoIntervento);




        // Titolo activity
        ActionBar actionBar = getSupportActionBar();
        // actionBar.setHomeButtonEnabled(true);
        // actionBar.setDisplayHomeAsUpEnabled(false);
        // actionBar.setDisplayShowHomeEnabled(false);


        try {

            // Imposta la personalizzazione dell'actionBar
            actionBar.setCustomView(R.layout.interventi_actionbar_item);

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);

            setActionbarTitle(actionBar);
            actionBar.show();

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            // Creazione oggetti activity
            setContentView(R.layout.activity_interventi);

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            for (int i = 0; i < APPData.ObjectCustomLabels.size(); i++) {
                String FieldValue = "";
                APPData.ObjectCustomFields.set(i, FieldValue);
            }

            APPOggettiSQL.valorizza_etichette_oggetti(Integer.parseInt(ObjectID), mainActivity.context, APPInterventiActivity.this);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


        ((TextView)this.findViewById(R.id.Complesso)).setText(APPData.LAST_SEL_COMPLESSO);
        ((TextView)this.findViewById(R.id.Edificio)).setText(APPData.LAST_SEL_EDIFICIO);
        ((TextView)this.findViewById(R.id.Piano)).setText(APPData.LAST_SEL_PIANO);
        ((TextView)this.findViewById(R.id.Vano)).setText(APPData.LAST_SEL_VANO);
        ((TextView)this.findViewById(R.id.Obj)).setText(ObjectCode + "." + ObjectDesc);


        // List view oggetti componenti
        /*
        try {

            ListView lv = (ListView) findViewById(R.id.lvInterventiCustumFields);
            SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, APPData.ObjectCustomFields, null, APPData.ObjectCustomLabels, 0);
            lv.setAdapter(ListAdapter);

            // register onClickListener to handle click events on each item
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                    // String str=[position];
                    System.err.println("onItemClick:" + position);
                }
            });

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        */


        //////////////////////////////////////
        // Griglia campi personalizzati
        //
        try {

            GridView gv = (GridView) findViewById(R.id.gridView);

            SingleRecordAdapter GridAdapter = new SingleRecordAdapter(this, APPData.ObjectCustomFields, null, APPData.ObjectCustomLabels, 0);

            gv.setAdapter(GridAdapter);

            // register onClickListener to handle click events on each item
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                    // String str=[position];
                    System.err.println("onItemClick:" + position);
                }
            });

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


        /////////////////////////////////////////////////
        // Nemu navigazione interventi esistenti
        //
        if (APPData.appInterventi.NumInterventi >= 0) {
            refresh_spinner(actionBar, true);
        }

        refreshCurIntervento();


        Button button = (Button) findViewById(R.id.ok);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onOK(v);
            }
        });

        button = (Button) findViewById(R.id.cancel);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onCancel(v);
            }
        });

        button = (Button) findViewById(R.id.btOk);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onOK(v);
            }
        });

        button = (Button) findViewById(R.id.btCancel);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onCancel(v);
            }
        });



        /*
        ImageButton ibutton = (ImageButton) findViewById(R.id.ibtNew);
        if (ibutton != null) {
            ibutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onNew(v);
                }
            });
        }

        ibutton = (ImageButton) findViewById(R.id.ibtUpdate);
        if (ibutton != null) {
            ibutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onUpdate(v);
                }
            });
        }

        ibutton = (ImageButton) findViewById(R.id.ibtDelete);
        if (ibutton != null) {
            ibutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onDelete(v);
                }
            });
        }
         */


        button = (Button) findViewById(R.id.btNew);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onNew(v);
                }
            });
        }

        button = (Button) findViewById(R.id.btUpdate);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onUpdate(v);
                }
            });
        }

        button = (Button) findViewById(R.id.btDelete);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onDelete(v);
                }
            });
        }

        button = (Button) findViewById(R.id.btSync);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onSync(v);
                }
            });
        }

        button = (Button) findViewById(R.id.btSync2);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onSync(v);
                }
            });
        }


        final MyRunnable after_list_selected = new MyRunnable(null, null) {
            @Override
            public void run() {
                if (myParam != null) {
                    if (myID != null) {
                        TextView et = ((TextView) findViewById(myID));
                        if (et != null) {
                            et.setText(myParam);
                        }
                    }
                }
            }
        };



        TextView et = ((TextView)this.findViewById(R.id.etStato));
        if (et != null) {
              et.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    APPUtil.getSelectFromListview(APPData.InterventiStatusList, null, 0, "Seleziona uno stato...", APPInterventiActivity.this, null, after_list_selected, R.id.etStato);
                    return true;
                }
            });
        }



        ///////////////////////////////////
        // Immagine di sfondo
        //
        View backgroundimage = findViewById(R.id.background);
        if(backgroundimage != null) {
            Drawable background = backgroundimage.getBackground();
            background.setAlpha(40);
        }



        ////////////////////////////////////////////////
        // Registrazione coordinate Ambiente
        //
        store_env_param();

    }


    private void store_env_param() {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        APPData.LAST_ENV_ON_DWG = "Interventi";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWG",APPData.LAST_ENV_ON_DWG);
        APPData.LAST_ENV_ON_DWG_PARAM = ObjectID;
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam",APPData.LAST_ENV_ON_DWG_PARAM);
        APPData.LAST_ENV_ON_DWG_PARAM1 = ObjectCode;
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam1", APPData.LAST_ENV_ON_DWG_PARAM1);
        APPData.LAST_ENV_ON_DWG_PARAM2 = String.valueOf(cIntervento);
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam2", APPData.LAST_ENV_ON_DWG_PARAM2);
        APPData.LAST_ENV_ON_DWG_PARAM3 = String.valueOf(APPData.nuovoIntervento);
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam3", APPData.LAST_ENV_ON_DWG_PARAM3);
        APPData.LAST_ENV_ON_DWG_PARAM4 = String.valueOf(APPData.LAST_SEL_IDCOMPLESSO);
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam4", APPData.LAST_ENV_ON_DWG_PARAM4);
        APPData.LAST_ENV_ON_DWG_PARAM5 = String.valueOf(APPData.LAST_SEL_IDEDIFICIO);
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam5",APPData.LAST_ENV_ON_DWG_PARAM5);
        APPData.LAST_ENV_ON_DWG_PARAM6 = String.valueOf(APPData.LAST_SEL_IDPIANO);
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam6",APPData.LAST_ENV_ON_DWG_PARAM6);
        APPData.LAST_ENV_ON_DWG_PARAM7 = String.valueOf(APPData.LAST_SEL_IDVANO);
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam7",APPData.LAST_ENV_ON_DWG_PARAM7);
    }

    public void reset_env_data() {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        APPData.LAST_ENV_ON_DWG = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWG", APPData.LAST_ENV_ON_DWG);
        APPData.LAST_ENV_ON_DWG_PARAM = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam", APPData.LAST_ENV_ON_DWG_PARAM);
        APPData.LAST_ENV_ON_DWG_PARAM1 = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam1", APPData.LAST_ENV_ON_DWG_PARAM1);
        APPData.LAST_ENV_ON_DWG_PARAM2 = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam2", APPData.LAST_ENV_ON_DWG_PARAM2);
        APPData.LAST_ENV_ON_DWG_PARAM3 = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam3", APPData.LAST_ENV_ON_DWG_PARAM3);
        APPData.LAST_ENV_ON_DWG_PARAM4 = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam4", APPData.LAST_ENV_ON_DWG_PARAM4);
        APPData.LAST_ENV_ON_DWG_PARAM5 = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam5", APPData.LAST_ENV_ON_DWG_PARAM5);
        APPData.LAST_ENV_ON_DWG_PARAM6 = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam6", APPData.LAST_ENV_ON_DWG_PARAM6);
        APPData.LAST_ENV_ON_DWG_PARAM7 = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam7", APPData.LAST_ENV_ON_DWG_PARAM7);
    }








    public void setup_nuovo_intervento ( boolean as_new ) {
        String id_oggetto = "";


        try {
            id_oggetto = ObjectID; // APPData.appOggetti.IDOggetti.get(APPData.appOggetti.cOggetto);
        } catch (Exception e) {
            e.getMessage();
        }

        // CodInterventi = cod_edificio + "." + cod_piano + "." + cod_oggetto + "." + id_oggetto + "." + APPData.InterventiNID;
        CodInterventi = id_oggetto + "." + APPData.InterventiNID;

        if (as_new) {
            if (APPData.STORE_TYPED1.compareToIgnoreCase("1")==0) {
                DescInterventi = APPData.TYPED_INTERVENTO_DESC;
                ExtDescInterventi = APPData.TYPED_INTERVENTO_EXT_DESC;
            } else {
                DescInterventi = "";
                ExtDescInterventi = "";
            }

            DataAperturaIntervento = APPUtil.get_date_time();
            DataScadenzaIntervento = "";
            DataChiusuraIntervento = "";
            StatoIntervento = "N";

        } else {
            // Duplicazione
            // DescInterventi = ((TextView)this.findViewById(R.id.etDescIntervento)).getText().toString();
            // ExtDescInterventi = ((TextView)this.findViewById(R.id.etExtDescIntervento)).getText().toString();
            // DataAperturaIntervento = ((TextView)this.findViewById(R.id.tvDataApertura)).getText().toString();
            // DataScadenzaIntervento = ((TextView)this.findViewById(R.id.tvDataScadenza)).getText().toString();
            // DataChiusuraIntervento = ((TextView)this.findViewById(R.id.tvDataChiusura)).getText().toString();
        }

    }


    public void setActionbarTitle(ActionBar actionBar) {

        String Title = "Oggetto Manutentivo : " + APPData.LAST_SEL_OGGETTO+"."+APPData.LAST_SEL_OGGETTO_DESC;
        if (APPData.nuovoIntervento) {
            Title += "  [ Nuovo Intervento ]";
        } else {
            Title += "  " + (APPData.appInterventi.NumInterventi > 0 ? ("[ " + (APPData.appInterventi.cIntervento + 1) + " / " + APPData.appInterventi.NumInterventi + " ]") : "");
        }

        actionBar.setTitle(Title);
    }










    public void refresh_spinner( ActionBar actionBar, boolean startup ) {

        try {

            String[] elenco_interventi = (CodInterventiList != null && !CodInterventiList.isEmpty()) ? ((String[]) CodInterventiList.toArray(new String[CodInterventiList.size()])) : (new String[]{""});

            ArrayAdapter<String> interventi_adapter = new ArrayAdapter<String>(
                    actionBar.getThemedContext(), android.R.layout.simple_dropdown_item_1line,
                    elenco_interventi);

            Spinner InterventiSpinner = (Spinner) this.findViewById(R.id.spinInterventi);

            if (InterventiSpinner != null) {
                InterventiSpinner.setAdapter(interventi_adapter);

                if (startup) {

                    InterventiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position1B, long id) {

                            save_typed_data();

                            setCurIntervento(position1B - 1);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }
                    });

                    InterventiSpinner.setSelection(cIntervento+1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void rebuild_spinner ( boolean setCurrent ) {
        // Lettura inteventi
        // leggi_interventi();
        APPInterventiSQL.leggi_interventi(null, null, null, null, APPData.LAST_SEL_IDOGGETTO, APPData.appInterventi);

        // Elenco interventi
        if (CodInterventiList == null) CodInterventiList = new ArrayList<String>();
        CodInterventiList.clear();
        CodInterventiList.add("<Nuovo>");

        try {
            for (int i=0; i<APPData.appInterventi.NumInterventi; i++){
                String strInterventi = "";
                if (APPData.appInterventi.IDInterventiOnQueue.get(i) != null && APPData.appInterventi.IDInterventiOnQueue.get(i) > 0) {
                    strInterventi = "[+] " + APPData.appInterventi.CodInterventi.get(i);
                } else {
                    strInterventi = APPData.appInterventi.CodInterventi.get(i);
                }
                strInterventi += " - " + APPData.appInterventi.DescInterventi.get(i);
                CodInterventiList.add(strInterventi);
            }
        } catch (Exception e) {
        }


        refresh_spinner(getSupportActionBar(), false);

        if (setCurrent) {
            Spinner InterventiSpinner = (Spinner) this.findViewById(R.id.spinInterventi);
            if (cIntervento >= APPData.appInterventi.NumInterventi) cIntervento = APPData.appInterventi.NumInterventi-1;
            InterventiSpinner.setSelection(cIntervento + 1);
        }
    }



    public void refreshCurIntervento ( ) {

        try {

            if (APPData.nuovoIntervento) {

                DataAperturaIntervento = APPUtil.get_date_time();
                DataChiusuraIntervento = "";
                DataChiusuraIntervento = "";

            } else {
                if (APPData.appInterventi.CodInterventi != null && !APPData.appInterventi.CodInterventi.isEmpty()) {
                    if (!APPData.appInterventi.CodInterventi.get(cIntervento).isEmpty()) {
                        CodInterventi = APPData.appInterventi.CodInterventi.get(cIntervento).toString();
                    }
                }
                if (APPData.appInterventi.DescInterventi != null && !APPData.appInterventi.DescInterventi.isEmpty()) {
                    if (!APPData.appInterventi.DescInterventi.get(cIntervento).isEmpty()) {
                        DescInterventi = APPData.appInterventi.DescInterventi.get(cIntervento).toString();
                    }
                }
                if (APPData.appInterventi.StatoIntervento != null && !APPData.appInterventi.StatoIntervento.isEmpty()) {
                    if (!APPData.appInterventi.StatoIntervento.get(cIntervento).isEmpty()) {
                        StatoIntervento = APPData.appInterventi.StatoIntervento.get(cIntervento).toString();
                    }
                }

                if (APPData.appInterventi.ExtDescInterventi != null && !APPData.appInterventi.ExtDescInterventi.isEmpty()) {
                    if (!APPData.appInterventi.ExtDescInterventi.get(cIntervento).isEmpty()) {
                        ExtDescInterventi = APPData.appInterventi.ExtDescInterventi.get(cIntervento).toString();
                    }
                }

                if (APPData.appInterventi.DataAperturaIntervento != null && !APPData.appInterventi.DataAperturaIntervento.isEmpty()) {
                    if (!APPData.appInterventi.DataAperturaIntervento.get(cIntervento).isEmpty()) {
                        DataAperturaIntervento = APPData.appInterventi.DataAperturaIntervento.get(cIntervento).toString();
                    }
                }

                if (APPData.appInterventi.DataScadenzaIntervento != null && !APPData.appInterventi.DataScadenzaIntervento.isEmpty()) {
                    if (!APPData.appInterventi.DataScadenzaIntervento.get(cIntervento).isEmpty()) {
                        DataScadenzaIntervento = APPData.appInterventi.DataScadenzaIntervento.get(cIntervento).toString();
                    }
                }

                if (APPData.appInterventi.DataChiusuraIntervento != null && !APPData.appInterventi.DataChiusuraIntervento.isEmpty()) {
                    if (!APPData.appInterventi.DataChiusuraIntervento.get(cIntervento).isEmpty()) {
                        DataChiusuraIntervento = APPData.appInterventi.DataChiusuraIntervento.get(cIntervento).toString();
                    }
                }
            }

            if (DataAperturaIntervento != null && DataAperturaIntervento.compareToIgnoreCase("null") == 0) {
                DataAperturaIntervento = "---";
            }
            if (DataScadenzaIntervento != null && DataScadenzaIntervento.compareToIgnoreCase("null") == 0) {
                DataScadenzaIntervento = "---";
            }
            if (DataChiusuraIntervento != null && DataChiusuraIntervento.compareToIgnoreCase("null") == 0) {
                DataChiusuraIntervento = "---";
            }


            String FmtDataAperturaIntervento = "";
            String FmtDataScadenzaIntervento = "";
            String FmtDataChiusuraIntervento = "";

            try {

                FmtDataAperturaIntervento = APPUtil.get_app_date_time(DataAperturaIntervento, 2);
                FmtDataScadenzaIntervento = APPUtil.get_app_date_time(DataScadenzaIntervento, 2);
                FmtDataChiusuraIntervento = APPUtil.get_app_date_time(DataChiusuraIntervento, 2);

            } catch (Exception e) {
                e.printStackTrace();
            }

            ((TextView)this.findViewById(R.id.tvCodiceIntervento)).setText(CodInterventi);
            ((TextView)this.findViewById(R.id.etStato)).setText(StatoIntervento);
            ((TextView)this.findViewById(R.id.etDescIntervento)).setText(DescInterventi);
            ((TextView)this.findViewById(R.id.etExtDescIntervento)).setText(ExtDescInterventi);

            ((TextView)this.findViewById(R.id.tvDataApertura)).setText(FmtDataAperturaIntervento!= null?FmtDataAperturaIntervento:"");
            ((TextView)this.findViewById(R.id.tvDataScadenza)).setText(FmtDataScadenzaIntervento!= null?FmtDataScadenzaIntervento:"");
            ((TextView)this.findViewById(R.id.tvDataChiusura)).setText(FmtDataChiusuraIntervento!= null?FmtDataChiusuraIntervento:"");



            String interventoID = null, queueID = null;
            if (cIntervento >= 0 && cIntervento < APPData.appInterventi.NumInterventi) {
                interventoID = String.valueOf(APPData.appInterventi.IDInterventi.get(cIntervento));
                queueID = String.valueOf(APPData.appInterventi.IDInterventiOnQueue.get(cIntervento));
            }

            boolean isWritable = true;
            if (APPData.nuovoIntervento) {
            } else {
                if (queueID == null || queueID.isEmpty()) {
                    isWritable = false;
                } else {
                    if (Integer.parseInt(queueID) <= 0) {
                        isWritable = false;
                    }
                }
            }

            if (isWritable) {
                ((TextView)this.findViewById(R.id.etDescIntervento)).setEnabled(true);
                ((TextView)this.findViewById(R.id.etExtDescIntervento)).setEnabled(true);
                ((Button)findViewById(R.id.btSync)).setEnabled(true);
                ((Button)findViewById(R.id.btSync)).setVisibility(View.VISIBLE);
                ((TextView)this.findViewById(R.id.etStato)).setEnabled(false);
            } else {
                ((TextView)this.findViewById(R.id.etDescIntervento)).setEnabled(false);
                ((TextView)this.findViewById(R.id.etExtDescIntervento)).setEnabled(false);
                ((Button)findViewById(R.id.btSync)).setEnabled(false);
                ((Button)findViewById(R.id.btSync)).setVisibility(View.INVISIBLE);
                ((TextView)this.findViewById(R.id.etStato)).setEnabled(false);
            }


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    public void setCurIntervento ( int pIntervento ) {
        try {
            if (pIntervento >= 0 && pIntervento < APPData.appInterventi.NumInterventi) {
                APPData.nuovoIntervento = false;

                if (cIntervento != pIntervento) {
                    if (isChanged()) {
                        if (DialogBox.DialogBox("ATTENZIONE", "Aggiornare l'interevento corrente ?", 0 + 1 + 2, APPInterventiActivity.this)) {
                            if (crea_aggiorna_intervento() <= 0) {
                                DialogBox.DialogBox("ATTENZIONE", "Operazione fallita!", 0 + 0, APPInterventiActivity.this);
                            }
                        }
                    }

                    cIntervento = pIntervento;
                }

            } else {
                APPData.nuovoIntervento = true;
                setup_nuovo_intervento(true);
                cIntervento = -1;
            }

            refreshCurIntervento();

            /////////////////////////////////////////
            // Registrazione coordinate ambiente
            //
            store_env_param();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appinterventi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionOk) {
            onOK(null);
            return true;
        }

        if (id == R.id.actionCancel) {
            onCancel(null);
            return true;
        }
        if (id == R.id.actionSync) {
            onSync(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }







    public int crea_aggiorna_intervento() {

        try {

            String sCodiceIntervento = ((TextView) this.findViewById(R.id.tvCodiceIntervento)).getText().toString();
            String sDescIntervento = ((TextView) this.findViewById(R.id.etDescIntervento)).getText().toString();
            String sExtDescIntervento = ((TextView) this.findViewById(R.id.etExtDescIntervento)).getText().toString();
            String sStatoIntervento = ((TextView) this.findViewById(R.id.etStato)).getText().toString();
            String interventoID = null, queueID = null;


            if (!APPData.nuovoIntervento) {
                interventoID = String.valueOf(APPData.appInterventi.IDInterventi.get(cIntervento));
                queueID = String.valueOf(APPData.appInterventi.IDInterventiOnQueue.get(cIntervento));
            }

            if (sCodiceIntervento == null || sCodiceIntervento.isEmpty()) {
                DialogBox.DialogBox("ATTENZIONE", "Codice Intervento non valido", 0 + 0, APPInterventiActivity.this);
                return 0;
            }

            if (sDescIntervento == null || sDescIntervento.isEmpty()) {
                DialogBox.DialogBox("ATTENZIONE", "Desccrizione Intervento non valida", 0 + 0, APPInterventiActivity.this);
                return 0;
            }

            if (!APPData.nuovoIntervento) {
                APPData.appInterventi.DescInterventi.set(cIntervento, sDescIntervento);
                APPData.appInterventi.ExtDescInterventi.set(cIntervento, sExtDescIntervento);
            }

            boolean isSupported = false;

            if (APPData.nuovoIntervento) {
                isSupported = true;
            } else {
                if (!isChanged()) {
                    isSupported = true;
                }
                if (queueID != null && !queueID.isEmpty()) {
                    if (Integer.parseInt(queueID) > 0) {
                        isSupported = true;
                    }
                }
            }

            if (isSupported) {

                String DbDataAperturaIntervento = APPUtil.get_app_date_time(DataAperturaIntervento, -1);
                String DbDataScadenzaIntervento = APPUtil.get_app_date_time(DataScadenzaIntervento, -1);
                String DbDataChiusuraIntervento = APPUtil.get_app_date_time(DataChiusuraIntervento, -1);

                return APPInterventiSQL.nuovo_intervento(
                        interventoID, queueID,
                        sCodiceIntervento, sDescIntervento, sExtDescIntervento, sStatoIntervento, DbDataAperturaIntervento,
                        APPData.LAST_SEL_COMPLESSO, APPData.LAST_SEL_EDIFICIO, APPData.LAST_SEL_PIANO, APPData.LAST_SEL_VANO, String.valueOf(APPData.LAST_SEL_IDOGGETTO), APPData.LAST_SEL_OGGETTO
                );
            } else {
                // N.B.: La funzione supporta l'aggiornamento ma il server non suppporta l'update
                DialogBox.DialogBox("ATTENZIONE", "Operazione non supportata dal server", 0 + 0, APPInterventiActivity.this);
                return 0;
            }

        } catch (Exception e) { e.printStackTrace(); }

    return 0;
    }




    public boolean isChanged () {
        try {

            String CodiceIntervento = ((TextView) this.findViewById(R.id.tvCodiceIntervento)).getText().toString();
            String DescIntervento = ((TextView) this.findViewById(R.id.etDescIntervento)).getText().toString();
            String ExtDescIntervento = ((TextView) this.findViewById(R.id.etExtDescIntervento)).getText().toString();
            String sStatoIntervento = ((TextView) this.findViewById(R.id.etStato)).getText().toString();

            if (    APPData.appInterventi.DescInterventi.get(cIntervento).compareToIgnoreCase(DescIntervento) != 0 ||
                    APPData.appInterventi.ExtDescInterventi.get(cIntervento).compareToIgnoreCase(ExtDescIntervento) != 0 ||
                    APPData.appInterventi.StatoIntervento.get(cIntervento).compareToIgnoreCase(sStatoIntervento) != 0
                    ) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }



    public void save_typed_data() {
        try {
            if (APPData.STORE_TYPED1.compareToIgnoreCase("1")==0) {
                if (APPData.nuovoIntervento) {
                    // Salva i valori predefiniti
                    String str = ((TextView) findViewById(R.id.etDescIntervento)).getText().toString();
                    APPData.TYPED_INTERVENTO_DESC = str != null ? str : "";
                    str = ((TextView) findViewById(R.id.etExtDescIntervento)).getText().toString();
                    APPData.TYPED_INTERVENTO_EXT_DESC = str != null ? str : "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void onOK(View v) {
        save_typed_data();
        int res = crea_aggiorna_intervento();
        if (res > 0) {
            reset_env_data();
            finish();
        }
    }


    public void onSync(View v) {
        try {
            if (isChanged()) {
                if (DialogBox.DialogBox("ATTENZIONE", (APPData.nuovoIntervento ? "Aggiornare" : "Creare") + " l'interevento ?", 0 + 1 + 2, APPInterventiActivity.this)) {
                    save_typed_data();
                    int res = crea_aggiorna_intervento();
                    if (res > 0) {
                        if (cIntervento == -1) {
                            if (APPData.appInterventi.NumInterventi > 0) {
                                cIntervento = APPData.appInterventi.NumInterventi - 1;
                            }
                        }
                    }
                }
            }

            if (NetworkActivity.isOnline() > 0) {
                if (cIntervento >= 0) {
                    if (APPData.appInterventi.IDInterventiOnQueue.get(cIntervento) > 0) {
                        if (DialogBox.DialogBox("ATTENZIONE", "Inviare l'intervento " + APPData.appInterventi.CodInterventi.get(cIntervento) + " ?", 0 + 1 + 2, APPInterventiActivity.this)) {
                            if (APPQueueSQL.de_queue(null, "SERVER", APPData.appInterventi.IDInterventiOnQueue.get(cIntervento), APPData.appInterventi.IDInterventi.get(cIntervento)) != 1) {
                                DialogBox.DialogBox("ATTENZIONE", "Sincronizzazione Intervento fallita!", 0 + 0, APPInterventiActivity.this);
                            } else {
                                rebuild_spinner(true);
                                refreshCurIntervento();
                            }
                        }
                    }
                }
            } else {
                DialogBox.DialogBox("ATTENZIONE", "Connessione mancante", 0 + 0, APPInterventiActivity.this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void onCancel(View v) {
        save_typed_data();
        reset_env_data();
        finish();
    }





    public void onNew(View v) {
        try {
            if (!APPData.nuovoIntervento) {
                if (isChanged()) {
                    if (DialogBox.DialogBox("ATTENZIONE", "Aggiornare l'interevento corrente ?", 0 + 1 + 2, APPInterventiActivity.this)) {
                        if (crea_aggiorna_intervento() <= 0) {
                            DialogBox.DialogBox("ATTENZIONE", "Operazione fallita!", 0 + 0, APPInterventiActivity.this);
                        } else {
                        }
                    } else {
                        return;
                    }
                }
                APPData.nuovoIntervento = true;
                cIntervento = -1;

                setup_nuovo_intervento(false);
                rebuild_spinner(false);

            } else {

                if (DialogBox.DialogBox("ATTENZIONE", "Creare l'interevento ?", 0 + 1 + 2, APPInterventiActivity.this)) {
                    save_typed_data();
                    if (crea_aggiorna_intervento() > 0) {
                        cIntervento = APPData.appInterventi.NumInterventi;
                        if (APPData.CLOSE_ON_NEW_INTERVENTO != null && APPData.CLOSE_ON_NEW_INTERVENTO.compareToIgnoreCase("1") == 0) {
                            reset_env_data();
                            finish();
                            return;
                        }
                    }

                    APPData.nuovoIntervento = false;

                    /*
                    cIntervento = -1;
                    setup_nuovo_intervento(true);
                    */

                    rebuild_spinner(true);

                    APPUtil.hideSoftKeyboard(this);
                }
            }

            refreshCurIntervento();

        } catch (Exception e) { e.printStackTrace(); }
    }




    public void onUpdate(View v) {
        try {
            if (!APPData.nuovoIntervento) {
                if (isChanged()) {
                    if (DialogBox.DialogBox("ATTENZIONE", "Aggiornare l'interevento corrente ?", 0 + 1 + 2, APPInterventiActivity.this)) {
                        if (crea_aggiorna_intervento() <= 0) {
                            DialogBox.DialogBox("ATTENZIONE", "Operazione fallita!", 0 + 0, APPInterventiActivity.this);
                        } else {
                        }
                    }
                }

                rebuild_spinner(false);
                APPUtil.hideSoftKeyboard(this);

                Spinner InterventiSpinner = (Spinner) this.findViewById(R.id.spinInterventi);
                InterventiSpinner.setSelection(cIntervento+1);


            } else {
                if (DialogBox.DialogBox("ATTENZIONE", "Crare l'interevento ?", 0 + 1 + 2, APPInterventiActivity.this)) {
                    onOK(v);
                }
            }

            refreshCurIntervento();

        } catch (Exception e) { e.printStackTrace(); }
    }


    public void onDelete(View v) {

        try {

            if (!APPData.nuovoIntervento) {

                if (cIntervento >= 0 && cIntervento < APPData.appInterventi.NumInterventi) {

                    if (APPData.appInterventi.IDInterventiOnQueue.get(cIntervento) != null && APPData.appInterventi.IDInterventiOnQueue.get(cIntervento)>0) {

                        if (DialogBox.DialogBox("ATTENZIONE", "Eliminare l'interevento corrente ?", 0 + 1 + 2, APPInterventiActivity.this)) {

                            APPInterventiSQL.cancella_intervento(String.valueOf(APPData.appInterventi.IDInterventi.get(cIntervento)), String.valueOf(APPData.appInterventi.IDInterventiOnQueue.get(cIntervento)));

                            // leggi_interventi();
                            APPInterventiSQL.leggi_interventi(APPData.LAST_SEL_COMPLESSO, APPData.LAST_SEL_EDIFICIO, APPData.LAST_SEL_PIANO, APPData.LAST_SEL_VANO, APPData.LAST_SEL_IDOGGETTO, APPData.appInterventi);

                            cIntervento--;

                            rebuild_spinner(true);
                            // refresh_spinner(getSupportActionBar(), false);

                            refreshCurIntervento();

                        }

                    } else {
                        DialogBox.DialogBox("ATTENZIONE", "Non è possibile eliminare interventi già inviati al server", 0 + 0, APPInterventiActivity.this);
                    }
                }
            } else {
                ((TextView) this.findViewById(R.id.etDescIntervento)).setText(DescInterventi);
                ((TextView) this.findViewById(R.id.etExtDescIntervento)).setText(ExtDescInterventi);
                ((TextView) this.findViewById(R.id.etStato)).setText(StatoIntervento);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }


}






class SingleRecordAdapter extends BaseAdapter implements ListAdapter {
    int Count = 0, labelCount = 0, curItem = -1;

    List<String> ObjectCustomLabels = null;
    List<String> ObjectCustomFields = null;
    List<String> ObjectCustomFields2 = null;

    List<String> Row2ObjectCustomFields1 = null;
    List<String> Row2ObjectCustomFields2 = null;
    List<String> Row2ObjectCustomFields3 = null;

    List<String> Row3ObjectCustomFields1 = null;
    List<String> Row3ObjectCustomFields2 = null;
    List<String> Row3ObjectCustomFields3 = null;

    List<String> Row4ObjectCustomFields1 = null;
    List<String> Row4ObjectCustomFields2 = null;
    List<String> Row4ObjectCustomFields3 = null;

    String Label1 = null, Label2 = null, Label3 = null, Label4 = null, Label5 = null, Label6 = null, Label7 = null, Label8 = null, Label9 = null, Label10 = null, Label11 = null, Label12 = null;

    ArrayList<Integer> VctInterventi = null;
    int NumVctInterventi = 0;

    Activity context = null;
    int rec_id_xml = R.layout.rec_lv_appinterventi;

    int DEF_BK_COLOR;
    int DEF_SEL_COLOR;



    public SingleRecordAdapter(Activity context, List<String> ObjectCustomFields, List<String> ObjectCustomFields2, List<String>ObjectCustomLabels, int rec_id_xml) {
        try {

            SetRecordAdapterRows(ObjectCustomFields, ObjectCustomFields2, ObjectCustomLabels);

            this.context = context;
            this.rec_id_xml = rec_id_xml!=0?rec_id_xml:R.layout.rec_lv_appinterventi;

            DEF_BK_COLOR = 0;
            DEF_SEL_COLOR = Color.rgb(100,100,200);

        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void SetRecordAdapterRows(List<String> ObjectCustomFields1, List<String> ObjectCustomFields2, List<String>ObjectCustomFields3 ) {
        try {
            this.ObjectCustomFields = ObjectCustomFields1;
            this.ObjectCustomFields2 = ObjectCustomFields2;
            this.ObjectCustomLabels = ObjectCustomFields3;
            this.Count = this.ObjectCustomFields!=null?this.ObjectCustomFields.size():0;
            this.labelCount = this.ObjectCustomLabels!=null?this.ObjectCustomLabels.size():0;
        } catch (Exception e) {
            e.getMessage();
        }
    }


    public void ExtendRecordAdapterlabels(String Label1, String Label2, String Label3,
                                        String Label4, String Label5, String Label6,
                                        String Label7, String Label8, String Label9,
                                        String Label10, String Label11, String Label12
    ) {
        try {
            this.Label1 = Label1;
            this.Label2 = Label2;
            this.Label3 = Label3;
            this.Label4 = Label4;
            this.Label5 = Label5;
            this.Label6 = Label6;
            this.Label7 = Label7;
            this.Label8 = Label8;
            this.Label9 = Label9;
            this.Label10 = Label10;
            this.Label11 = Label11;
            this.Label12 = Label12;
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void ExtendRecordAdapterRows (
            List<String> ObjectCustomFields1, List<String> ObjectCustomFields2, List<String>ObjectCustomFields3,
            List<String> ObjectCustomFields4, List<String> ObjectCustomFields5, List<String>ObjectCustomFields6,
            List<String> ObjectCustomFields7, List<String> ObjectCustomFields8, List<String>ObjectCustomFields9
    ) {
        try {
            Row2ObjectCustomFields1 = ObjectCustomFields1;
            Row2ObjectCustomFields2 = ObjectCustomFields2;
            Row2ObjectCustomFields3 = ObjectCustomFields3;
            Row3ObjectCustomFields1 = ObjectCustomFields4;
            Row3ObjectCustomFields2 = ObjectCustomFields5;
            Row3ObjectCustomFields3 = ObjectCustomFields6;
            Row4ObjectCustomFields1 = ObjectCustomFields7;
            Row4ObjectCustomFields2 = ObjectCustomFields8;
            Row4ObjectCustomFields3 = ObjectCustomFields9;
        } catch (Exception e) {
            e.getMessage();
        }
    }



    public void SetFilterRecordAdapterRows ( ArrayList<Integer> VctInterventi, int NumVctInterventi ) {
        try {
            this.VctInterventi = VctInterventi;
            this.NumVctInterventi = NumVctInterventi;
        } catch (Exception e) {
            e.getMessage();
        }
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {

        if (VctInterventi != null) {
            return NumVctInterventi;
        } else {
            return Count;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        int iRec = -1;

        try {

            if (VctInterventi != null) {
                if (position < VctInterventi.size()){
                    iRec = VctInterventi.get(position);
                }
            } else {
                iRec = position;
            }

            if (iRec < 0) return null;



            String field = "", label = "";
            String field1 = "", field2 = "", field3 = "";

            try {
                field2 = iRec<Count?(this.ObjectCustomFields2!=null?(this.ObjectCustomFields2.get(iRec)!=null?this.ObjectCustomFields2.get(iRec):""):""):("");
                field = iRec<Count?(this.ObjectCustomFields!=null?(this.ObjectCustomFields.get(iRec)!=null?this.ObjectCustomFields.get(iRec):""):""):("");
                label = iRec<labelCount?(this.ObjectCustomLabels!=null?(this.ObjectCustomLabels.get(iRec)!=null?this.ObjectCustomLabels.get(iRec):""):""):("");
            } catch (Exception e) {
            }

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(this.rec_id_xml, parent, false);
            }

            if (rec_id_xml == R.layout.rec_lv_appinterventi) {

                TextView tvt = ((TextView) view.findViewById(R.id.Text1));
                if (field != null) {
                    tvt.setText(field);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }

                TextView tvl = ((TextView) view.findViewById(R.id.Text2));
                if (label != null) {
                    tvl.setText(label);
                } else {
                    ViewGroup.LayoutParams params = tvl.getLayoutParams();
                    params.height = 0;
                    tvl.setLayoutParams(params);
                    tvl.setHeight(0);
                }

            } else if (rec_id_xml == R.layout.rec_lv_2field) {
                TextView tvt = ((TextView) view.findViewById(R.id.Text1));
                if (field != null) {
                    tvt.setText(field);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }

                TextView tvl = ((TextView) view.findViewById(R.id.Text2));
                if (label != null) {
                    tvl.setText(field2);
                } else {
                    ViewGroup.LayoutParams params = tvl.getLayoutParams();
                    params.height = 0;
                    tvl.setLayoutParams(params);
                    tvl.setHeight(0);
                }

            } else if (rec_id_xml == R.layout.rec_lv_3fields) {

                TextView tvt = ((TextView) view.findViewById(R.id.Text1));
                if (field != null) {
                    tvt.setText(label);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }

                TextView tvl = ((TextView) view.findViewById(R.id.Text2));
                if (label != null) {
                    tvl.setText(field);
                } else {
                    ViewGroup.LayoutParams params = tvl.getLayoutParams();
                    params.height = 0;
                    tvl.setLayoutParams(params);
                    tvl.setHeight(0);
                }

                tvl = ((TextView) view.findViewById(R.id.Text3));
                if (label != null) {
                    tvl.setText(field2);
                } else {
                    ViewGroup.LayoutParams params = tvl.getLayoutParams();
                    params.height = 0;
                    tvl.setLayoutParams(params);
                    tvl.setHeight(0);
                }




            } else if (rec_id_xml == R.layout.rec_lv_9fields || rec_id_xml == R.layout.rec_lv_12fields) {

                TextView tvt = ((TextView) view.findViewById(R.id.Text1));
                if (field != null) {
                    tvt.setText(label);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }

                tvt = ((TextView) view.findViewById(R.id.Text2));
                if (label != null) {
                    tvt.setText(field);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }

                tvt = ((TextView) view.findViewById(R.id.Text3));
                if (label != null) {
                    tvt.setText(field2);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }


                try {
                    field1 = iRec < Count ? (this.Row2ObjectCustomFields1 != null ? (this.Row2ObjectCustomFields1.get(iRec) != null ? this.Row2ObjectCustomFields1.get(iRec) : "") : "") : ("");
                    field2 = iRec < Count ? (this.Row2ObjectCustomFields2 != null ? (this.Row2ObjectCustomFields2.get(iRec) != null ? this.Row2ObjectCustomFields2.get(iRec) : "") : "") : ("");
                    field3 = iRec < Count ? (this.Row2ObjectCustomFields3 != null ? (this.Row2ObjectCustomFields3.get(iRec) != null ? this.Row2ObjectCustomFields3.get(iRec) : "") : "") : ("");
                } catch (Exception e) {
                }


                tvt = ((TextView) view.findViewById(R.id.Text12));
                if (field != null) {
                    tvt.setText(field1);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }

                tvt = ((TextView) view.findViewById(R.id.Text22));
                if (label != null) {
                    tvt.setText(field2);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }

                tvt = ((TextView) view.findViewById(R.id.Text32));
                if (label != null) {
                    tvt.setText(field3);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }


                try {
                    field1 = iRec < Count ? (this.Row3ObjectCustomFields1 != null ? (this.Row3ObjectCustomFields1.get(iRec) != null ? this.Row3ObjectCustomFields1.get(iRec) : "") : "") : ("");
                    field2 = iRec < Count ? (this.Row3ObjectCustomFields2 != null ? (this.Row3ObjectCustomFields2.get(iRec) != null ? this.Row3ObjectCustomFields2.get(iRec) : "") : "") : ("");
                    field3 = iRec < Count ? (this.Row3ObjectCustomFields3 != null ? (this.Row3ObjectCustomFields3.get(iRec) != null ? this.Row3ObjectCustomFields3.get(iRec) : "") : "") : ("");
                } catch (Exception e) {
                }

                tvt = ((TextView) view.findViewById(R.id.Text13));
                if (field != null) {
                    tvt.setText(field1);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }

                tvt = ((TextView) view.findViewById(R.id.Text23));
                if (label != null) {
                    tvt.setText(field2);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }

                tvt = ((TextView) view.findViewById(R.id.Text33));
                if (label != null) {
                    tvt.setText(field3);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }


                if (rec_id_xml == R.layout.rec_lv_12fields) {

                    try {
                        field1 = iRec < Count ? (this.Row4ObjectCustomFields1 != null ? (this.Row4ObjectCustomFields1.get(iRec) != null ? this.Row4ObjectCustomFields1.get(iRec) : "") : "") : ("");
                        field2 = iRec < Count ? (this.Row4ObjectCustomFields2 != null ? (this.Row4ObjectCustomFields2.get(iRec) != null ? this.Row4ObjectCustomFields2.get(iRec) : "") : "") : ("");
                        field3 = iRec < Count ? (this.Row4ObjectCustomFields3 != null ? (this.Row4ObjectCustomFields3.get(iRec) != null ? this.Row4ObjectCustomFields3.get(iRec) : "") : "") : ("");
                    } catch (Exception e) {
                    }

                    tvt = ((TextView) view.findViewById(R.id.Text14));
                    if (field != null) {
                        tvt.setText(field1);
                    } else {
                        ViewGroup.LayoutParams params = tvt.getLayoutParams();
                        params.height = 0;
                        tvt.setLayoutParams(params);
                        tvt.setHeight(0);
                    }

                    tvt = ((TextView) view.findViewById(R.id.Text24));
                    if (label != null) {
                        tvt.setText(field2);
                    } else {
                        ViewGroup.LayoutParams params = tvt.getLayoutParams();
                        params.height = 0;
                        tvt.setLayoutParams(params);
                        tvt.setHeight(0);
                    }

                    tvt = ((TextView) view.findViewById(R.id.Text34));
                    if (label != null) {
                        tvt.setText(field3);
                    } else {
                        ViewGroup.LayoutParams params = tvt.getLayoutParams();
                        params.height = 0;
                        tvt.setLayoutParams(params);
                        tvt.setHeight(0);
                    }
                }

                tvt = ((TextView) view.findViewById(R.id.tvLabel11));
                if (Label1 != null) {
                    tvt.setText(Label1);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }
                tvt = ((TextView) view.findViewById(R.id.tvLabel21));
                if (Label2 != null) {
                    tvt.setText(Label2);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }
                tvt = ((TextView) view.findViewById(R.id.tvLabel31));
                if (Label3 != null) {
                    tvt.setText(Label3);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }
                tvt = ((TextView) view.findViewById(R.id.tvLabel12));
                if (Label4 != null) {
                    tvt.setText(Label4);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }
                tvt = ((TextView) view.findViewById(R.id.tvLabel22));
                if (Label5 != null) {
                    tvt.setText(Label5);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }
                tvt = ((TextView) view.findViewById(R.id.tvLabel32));
                if (Label6 != null) {
                    tvt.setText(Label6);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }
                tvt = ((TextView) view.findViewById(R.id.tvLabel13));
                if (Label7 != null) {
                    tvt.setText(Label7);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }
                tvt = ((TextView) view.findViewById(R.id.tvLabel23));
                if (Label8 != null) {
                    tvt.setText(Label8);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }
                tvt = ((TextView) view.findViewById(R.id.tvLabel33));
                if (Label9 != null) {
                    tvt.setText(Label9);
                } else {
                    ViewGroup.LayoutParams params = tvt.getLayoutParams();
                    params.height = 0;
                    tvt.setLayoutParams(params);
                    tvt.setHeight(0);
                }

                if (rec_id_xml == R.layout.rec_lv_12fields) {
                    tvt = ((TextView) view.findViewById(R.id.tvLabel14));
                    if (Label10 != null) {
                        tvt.setText(Label10);
                    } else {
                        ViewGroup.LayoutParams params = tvt.getLayoutParams();
                        params.height = 0;
                        tvt.setLayoutParams(params);
                        tvt.setHeight(0);
                    }
                    tvt = ((TextView) view.findViewById(R.id.tvLabel24));
                    if (Label11 != null) {
                        tvt.setText(Label11);
                    } else {
                        ViewGroup.LayoutParams params = tvt.getLayoutParams();
                        params.height = 0;
                        tvt.setLayoutParams(params);
                        tvt.setHeight(0);
                    }
                    tvt = ((TextView) view.findViewById(R.id.tvLabel34));
                    if (Label12 != null) {
                        tvt.setText(Label12);
                    } else {
                        ViewGroup.LayoutParams params = tvt.getLayoutParams();
                        params.height = 0;
                        tvt.setLayoutParams(params);
                        tvt.setHeight(0);
                    }
                }
            }

            if (DEF_BK_COLOR == 0)
                DEF_BK_COLOR = view.getDrawingCacheBackgroundColor();

            if (curItem >= 0) {
                if (curItem == position) {
                    view.setBackgroundColor(DEF_SEL_COLOR);
                } else {
                    view.setBackgroundColor(DEF_BK_COLOR);
                }
            } else {
                view.setBackgroundColor(DEF_BK_COLOR);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        // ((ImageView)view.findViewById(R.id.Image)).setImageURI(Uri.parse(""));
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}