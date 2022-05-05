package com.imuve.cristian.imuve;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.ArrayList;


public class DWGViewerActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {


    public static View mGLView;

    public ArrayList<String> Piani = null;
    public int cPiano = -1;

    public ArrayList<String> Vani = null;
    public int cVano = -1;

    public ArrayList<String> Oggetti = null;
    public int cOggetto = -1;

    public ArrayList<String> LayersName = null;
    public ArrayList<Integer> LayersValues = null;
    public ArrayList<Boolean> LayersON = null;
    public int clayer = -1;


    boolean ShowIfIntervento = false;
    boolean ShowLayerLampadine = true;
    boolean ShowLayerEmergenze = true;

    boolean bShowLayerMsg = true;
    boolean isLoading = true;

    SearchView mSearchView = null;
    ActionBarSearchView sctionBarSearchView = null;


    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private MyGLSurfaceView mMyGLSurfaceView;


    private CompoundButton.OnCheckedChangeListener ckLampadinelistener;
    private CompoundButton.OnCheckedChangeListener ckEmergenzelistener;


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        reset_env_param();
    }


    @Override
    public void onResume() {
        super.onResume();
        getWindow().getDecorView().setAlpha(1.0f);
        mGLView.setAlpha(1.0f);
        // Lettura Vani e Oggetti
        if (APPData.cPiano >= 0 && APPData.cPiano < APPData.NumPiani) {
            Integer IDPiano = APPData.IDPiani.get(APPData.cPiano);
            APPOggettiSQL.leggi_oggetti(0, 0, IDPiano, 0, APPData.appOggetti, APPData.appOggettiIfIntervento);
            ActionBar actionBar = getSupportActionBar();
            setup_oggetti_spinner(actionBar, true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dwgviewer);


        ShowIfIntervento = (boolean) Boolean.valueOf(APPData.SHOW_IF_INTERVENTO);
        ShowLayerEmergenze = (boolean) Boolean.valueOf(APPData.SHOW_LAYER_EMERGENZE);
        ShowLayerLampadine = (boolean) Boolean.valueOf(APPData.SHOW_LAYER_LAMPADINE);


        ActionBar actionBar = getSupportActionBar();

        // Imposta la personalizzazione dell'actionBar
        actionBar.setCustomView(R.layout.dwg_actionbar_item);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        // actionBar.setBackgroundDrawable();


        // Lettura Vani e Oggetti
        if (APPData.cPiano >= 0 && APPData.cPiano < APPData.NumPiani) {

            Integer IDPiano = APPData.IDPiani.get(APPData.cPiano);

            // Lettura di tutti i Vavi del piano
            try {
                APPVaniSQL.leggi_vani(IDPiano);
            } catch (Exception e) {
                e.getMessage();
            }

            // Lettura di tutti gli oggetti del piano
            try {
                APPOggettiSQL.leggi_oggetti(0, 0, IDPiano, 0, APPData.appOggetti, APPData.appOggettiIfIntervento);
            } catch (Exception e) {
                e.getMessage();
            }

        } else {
            DialogBox.DialogBox("ATTENZIONE", "Piano non selezionato", 0 + 0, DWGViewerActivity.this);
            finish();
            return;
        }


        ////////////////////////////////
        // Spinners
        //
        setup_piani_spinner(actionBar, true);

        setup_vani_spinner(actionBar, true);

        setup_oggetti_spinner(actionBar, true);


        ////////////////////////////////
        // Filtri sui layer
        //
        setup_layer_emergenze_filter(false);

        setup_layer_lampadine_filter(false);


        EditText et = (EditText) findViewById(R.id.etSearch);
        if (et != null) {
            et.setText(APPData.LAST_SEARCH_TEXT_ON_DWG != null ? APPData.LAST_SEARCH_TEXT_ON_DWG : "");
        }


        CheckBox mCheckbox = (CheckBox) findViewById(R.id.ckShowIfIntervento);
        if (mCheckbox != null) {
            mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                    if (((CheckBox) buttonView).isChecked()) {
                        ShowIfIntervento = true;
                        APPData.SHOW_IF_INTERVENTO = "true";
                    } else {
                        ShowIfIntervento = false;
                        APPData.SHOW_IF_INTERVENTO = "false";
                    }

                    show_ifintervento_on_dwg();

                }
            });
        }
        mCheckbox.setChecked(ShowIfIntervento);


        mCheckbox = (CheckBox) findViewById(R.id.ckLampadine);
        if (mCheckbox != null) {
            mCheckbox.setChecked(ShowLayerLampadine);
            ckLampadinelistener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (((CheckBox) buttonView).isChecked()) {
                        ShowLayerLampadine = true;
                        APPData.SHOW_LAYER_LAMPADINE = "true";
                    } else {
                        ShowLayerLampadine = false;
                        APPData.SHOW_LAYER_LAMPADINE = "false";
                    }
                    setup_layer_lampadine_filter(isLoading);
                }
            };
            mCheckbox.setOnCheckedChangeListener(ckLampadinelistener);
        }

        mCheckbox = (CheckBox) findViewById(R.id.ckEmergenze);
        if (mCheckbox != null) {
            mCheckbox.setChecked(ShowLayerEmergenze);
            ckEmergenzelistener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (((CheckBox) buttonView).isChecked()) {
                        ShowLayerEmergenze = true;
                        APPData.SHOW_LAYER_EMERGENZE = "true";
                    } else {
                        ShowLayerEmergenze = false;
                        APPData.SHOW_LAYER_EMERGENZE = "false";
                    }
                    setup_layer_emergenze_filter(isLoading);
                }
            };
            mCheckbox.setOnCheckedChangeListener(ckEmergenzelistener);
        }


        // Create a GLSurfaceView instance and set it as the ContentView for this Activity.
        mMyGLSurfaceView = new MyGLSurfaceView(this);
        mMyGLSurfaceView.dwgViewerActivity = this;
        mMyGLSurfaceView.mainActivity = mainActivity;
        mMyGLSurfaceView.setAlpha(1.0f);

        mGLView = (View) mMyGLSurfaceView;

        mainActivity.dwgViewerActivity = this;


        restore_camera_env_param();


        if (APPData.LAST_ENV_ON_DWG.compareToIgnoreCase("Interventi") == 0) {
            this.getWindow().getDecorView().setAlpha(0.2f);
            mainActivity.run_interventi_activity(this.getApplicationContext(), DWGViewerActivity.this);

        } else if (APPData.LAST_ENV_ON_DWG.compareToIgnoreCase("Vani") == 0) {
            this.getWindow().getDecorView().setAlpha(0.2f);
            mainActivity.run_vani_activity(this.getApplicationContext(), DWGViewerActivity.this);

        } else {
            save_env_param();
        }


        ///////////////////////
        // Azioni iniziali
        //
        try {

            Bundle extras = getIntent().getExtras();

            String key = extras.getString("key");
            String search = extras.getString("search");

            if (key != null) {
                if (key.compareToIgnoreCase("locateVano") == 0) {
                    // SearchMode = 2    -> Exact search
                    // SearchMode = 1    -> Free search
                    // SearchMode = 0    -> Exact key search
                    doSearchText(search, 0);
                } else if (key.compareToIgnoreCase("locateOggetto") == 0) {
                    // SearchMode = 2    -> Exact search
                    // SearchMode = 1    -> Free search
                    // SearchMode = 0    -> Exact key search
                    doSearchText(search, 0);
                } else {
                    mMyGLSurfaceView.zoomExtens();
                }
            } else {
                mMyGLSurfaceView.zoomExtens();
            }

            extras.putString("key", "");
            extras.putString("search", "");

        } catch (Exception e) {
            e.printStackTrace();
            ;
        }


        ////////////////////////////////////////////
        // Lettura dei piani
        //

        try {

            LayersName = new ArrayList<String>();
            LayersValues = new ArrayList<Integer>();
            LayersON = new ArrayList<Boolean>();

            mainActivity.GetLayers();

        } catch (Exception e) {
            e.printStackTrace();
        }


        isLoading = false;
        mMyGLSurfaceView.requestRender();
    }


    public void onGetLayers(String Name, Integer Values) {
        if (LayersName != null) {
            LayersName.add(Name);
        }
        if (LayersValues != null) {
            LayersValues.add(Values);
        }
        if (LayersON != null) {
            Boolean isON = ((Values | 0x02) == 0x02) ? false : true;
            LayersON.add(isON);
        }
    }


    private void save_env_param() {

        try {

            MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
            APPData.LAST_ENV = "DWG_VIEWER";
            mainActivity.sqliteWrapper.update_setup_record("LastEnviroment", APPData.LAST_ENV);

            APPData.LAST_ENV_CAMERA_X = String.valueOf(mainActivity.GetCamera(0));
            mainActivity.sqliteWrapper.update_setup_record("LastEnviromentCamX", APPData.LAST_ENV_CAMERA_X);

            APPData.LAST_ENV_CAMERA_Y = String.valueOf(mainActivity.GetCamera(1));
            mainActivity.sqliteWrapper.update_setup_record("LastEnviromentCamY", APPData.LAST_ENV_CAMERA_Y);

            APPData.LAST_ENV_CAMERA_WX = String.valueOf(mainActivity.GetCamera(2));
            mainActivity.sqliteWrapper.update_setup_record("LastEnviromentCamWX", APPData.LAST_ENV_CAMERA_WX);

            APPData.LAST_ENV_CAMERA_WY = String.valueOf(mainActivity.GetCamera(3));
            mainActivity.sqliteWrapper.update_setup_record("LastEnviromentCamWY", APPData.LAST_ENV_CAMERA_WY);

        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }


    private void reset_env_param() {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        APPData.LAST_ENV = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviroment", APPData.LAST_ENV);

        APPData.LAST_ENV_ON_DWG = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWG", APPData.LAST_ENV_ON_DWG);

        APPData.LAST_ENV_ON_DWG_PARAM = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam", APPData.LAST_ENV_ON_DWG_PARAM);


        APPData.LAST_ENV_CAMERA_X = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentCamX", APPData.LAST_ENV_CAMERA_X);

        APPData.LAST_ENV_CAMERA_Y = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentCamY", APPData.LAST_ENV_CAMERA_Y);

        APPData.LAST_ENV_CAMERA_WX = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentCamWX", APPData.LAST_ENV_CAMERA_WX);

        APPData.LAST_ENV_CAMERA_WY = "";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentCamWY", APPData.LAST_ENV_CAMERA_WY);

    }


    ///////////////////////////
    // Ripristino telecamera
    //
    private void restore_camera_env_param() {
        try {
            MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
            if (APPData.LAST_ENV_CAMERA_X != null && !APPData.LAST_ENV_CAMERA_X.isEmpty()) {
                float LAST_ENV_CAMERA_X = 0.0f, LAST_ENV_CAMERA_Y = 0.0f, LAST_ENV_CAMERA_WX = 0.0f, LAST_ENV_CAMERA_WY = 0.0f;
                try {
                    LAST_ENV_CAMERA_X = Float.parseFloat(APPData.LAST_ENV_CAMERA_X);
                } catch (Exception e) {
                }
                try {
                    LAST_ENV_CAMERA_Y = Float.parseFloat(APPData.LAST_ENV_CAMERA_Y);
                } catch (Exception e) {
                }
                try {
                    LAST_ENV_CAMERA_WX = Float.parseFloat(APPData.LAST_ENV_CAMERA_WX);
                } catch (Exception e) {
                }
                try {
                    LAST_ENV_CAMERA_WY = Float.parseFloat(APPData.LAST_ENV_CAMERA_WY);
                } catch (Exception e) {
                }
                mainActivity.SetCamera(LAST_ENV_CAMERA_X, LAST_ENV_CAMERA_Y, LAST_ENV_CAMERA_WX, LAST_ENV_CAMERA_WY);
            } else {
                mMyGLSurfaceView.zoomExtens();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setup_layer_emergenze_filter(boolean bMsg) {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        Boolean anyError = false;

        try {
            if (mainActivity.SetLayer("CODICE EMERGENZE", ShowLayerEmergenze) <= 0) {
                if (bMsg) if (bShowLayerMsg)
                    DialogBox.DialogBox("ATTENZIONE", "Layer \"CODICE EMERGENZE\" non trovato!", 0 + 0, DWGViewerActivity.this);
                anyError = true;
            }
            if (mainActivity.SetLayer("LAMPADE DI EMERGENZA", ShowLayerEmergenze) <= 0) {
                if (bMsg) if (bShowLayerMsg)
                    DialogBox.DialogBox("ATTENZIONE", "Layer \"LAMPADE DI EMERGENZA\" non trovato!", 0 + 0, DWGViewerActivity.this);
                anyError = true;
            }

            if (LayersName != null) {
                for (int i = 0; i < LayersName.size(); i++) {
                    if (LayersName.get(i).compareToIgnoreCase("CODICE EMERGENZE") == 0) {
                        LayersON.set(i, ShowLayerEmergenze);
                    }
                    if (LayersName.get(i).compareToIgnoreCase("LAMPADE DI EMERGENZA") == 0) {
                        LayersON.set(i, ShowLayerEmergenze);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (anyError) if (bShowLayerMsg) bShowLayerMsg = false;
        if (mMyGLSurfaceView != null) mMyGLSurfaceView.requestRender();
    }


    public void setup_layer_lampadine_filter(boolean bMsg) {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        Boolean anyError = false;

        try {
            if (mainActivity.SetLayer("CODICE LAMPADINE", ShowLayerLampadine) <= 0) {
                if (bMsg) if (bShowLayerMsg)
                    DialogBox.DialogBox("ATTENZIONE", "Layer \"CODICE LAMPADINE\" non trovato!", 0 + 0, DWGViewerActivity.this);
                anyError = true;
            }
            if (mainActivity.SetLayer("LAMPADINE", ShowLayerLampadine) <= 0) {
                if (bMsg) if (bShowLayerMsg)
                    DialogBox.DialogBox("ATTENZIONE", "Layer \"LAMPADINE\" non trovato!", 0 + 0, DWGViewerActivity.this);
                anyError = true;
            }

            if (LayersName != null) {
                for (int i = 0; i < LayersName.size(); i++) {
                    if (LayersName.get(i).compareToIgnoreCase("CODICE LAMPADINE") == 0) {
                        LayersON.set(i, ShowLayerLampadine);
                    }
                    if (LayersName.get(i).compareToIgnoreCase("LAMPADINE") == 0) {
                        LayersON.set(i, ShowLayerLampadine);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (anyError) if (bShowLayerMsg) bShowLayerMsg = false;
        if (mMyGLSurfaceView != null) mMyGLSurfaceView.requestRender();
    }


    public void setup_piani_spinner(ActionBar actionBar, boolean startup) {

        try {


            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

            if (Piani == null) Piani = new ArrayList<String>();
            Piani.clear();
            Piani.add("<Piano>");


            try {
                Piani.addAll(APPData.Piani);
            } catch (Exception e) {
            }

            String[] elenco_piani = (Piani != null && !Piani.isEmpty()) ? ((String[]) Piani.toArray(new String[Piani.size()])) : (new String[]{""}); //

            ArrayAdapter<String> vani_adapter = new ArrayAdapter<String>(
                    actionBar.getThemedContext(), R.layout.simple_dropdown_item_right,
                    elenco_piani);

            Spinner PianiSpinner = (Spinner) findViewById(R.id.spinPiani);
            if (PianiSpinner != null) {

                PianiSpinner.setAdapter(vani_adapter);

                if (startup) {
                    PianiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            if (do_change_current_piano(parentView, position) < 0) {
                                /* Spinner PianiSpinner = (Spinner)parentView;
                                if (PianiSpinner != null) {
                                    PianiSpinner.setSelection(APPData.cPiano+1);
                                }
                                */
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }
                    });


                    cPiano = APPData.cPiano >= 0 ? (APPData.cPiano + 1) : 0;
                    PianiSpinner.setSelection(cPiano);
                }
            }


            // Set up the dropdown list navigation in the action bar.
            actionBar.setListNavigationCallbacks(
                    // Specify a SpinnerAdapter to populate the dropdown list.
                    new ArrayAdapter<String>(
                            actionBar.getThemedContext(),
                            android.R.layout.simple_list_item_1,
                            android.R.id.text1,
                            new String[]{""}),
                    this);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int do_change_current_piano(AdapterView<?> parentView, int position) {
        try {
            if (APPData.Piani != null && !APPData.Piani.isEmpty()) {
                if (position > 0 && position <= APPData.Piani.size()) {
                    if (position - 1 != APPData.cPiano) {

                        String IDDWGPiano = APPData.IDDWGPiani.get(position - 1);
                        String IDPiano = String.valueOf(APPData.IDPiani.get(position - 1));

                        if (IDDWGPiano != null && !IDDWGPiano.isEmpty()) {

                            if (Integer.parseInt(IDDWGPiano) > 0) {
                                MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

                                // mGLView.setAlpha(0.2f);
                                // mGLView.setEnabled(false);

                                try {

                                    cPiano = position;
                                    APPData.cPiano = position - 1;

                                    APPData.LAST_SEL_IDPIANO = APPData.IDPiani.get(APPData.cPiano);
                                    mainActivity.sqliteWrapper.update_setup_record("LastSelIDPiano", String.valueOf(APPData.LAST_SEL_IDPIANO));

                                    cVano = 0;
                                    APPData.cVano = -1;
                                    APPData.LAST_SEL_IDVANO = 0;
                                    APPData.LAST_SEL_VANO = "";
                                    APPData.LAST_SEL_IDOGGETTO = 0;
                                    APPData.LAST_SEL_OGGETTO = "";
                                    APPData.LAST_SEL_OGGETTO_DESC = "";
                                    APPData.LAST_SEL_IDINTERVENO = "";
                                    APPData.LAST_SEL_INTERVENO = "";

                                    cOggetto = 0;
                                    APPData.appOggetti.cOggetto = -1;

                                    APPData.appInterventi.cIntervento = -1;

                                } catch (Exception e) {
                                    e.getMessage();
                                }


                                try {
                                    mainActivity.setup_listview_piani(mainActivity, R.id.lvPiani, APPData.cPiano, false, 0);
                                } catch (Exception e) {
                                    e.getMessage();
                                }

                                try {
                                    mainActivity.setup_listview_vani(mainActivity, R.id.lvVani, -1, false, 0);
                                } catch (Exception e) {
                                    e.getMessage();
                                }

                                try {
                                    mainActivity.setup_listview_oggetti(mainActivity, R.id.lvOggetti, -1, false, 0);
                                } catch (Exception e) {
                                    e.getMessage();
                                }

                                try {
                                    mainActivity.setup_listview_interventi(mainActivity, R.id.lvInterventi, -1, false, 0);
                                } catch (Exception e) {
                                    e.getMessage();
                                }

                                // Lettura del Vani nel piano
                                try {
                                    APPVaniSQL.leggi_vani(Integer.valueOf(IDPiano));
                                } catch (Exception e) {
                                    e.getMessage();
                                }

                                // Lettura degli Oggetti nel piano
                                try {
                                    APPOggettiSQL.leggi_oggetti(0, 0, Integer.valueOf(IDPiano), 0, APPData.appOggetti, APPData.appOggettiIfIntervento);
                                } catch (Exception e) {
                                    e.getMessage();
                                }

                                Runnable myRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        done_change_current_piano();
                                    }
                                };


                                try {
                                    mainActivity.openDWG(getWindow().getDecorView(), false, false, APPData.cPiano, false, false, null, this, getApplicationContext(), myRunnable);
                                } catch (Exception e) {
                                    e.getMessage();
                                }


                                // mGLView.setAlpha(1.0f);
                                // mGLView.setEnabled(true);

                                return position;

                            } else {
                                DialogBox.DialogBox("ATTENZIONE", "ID Disegno non valido!", 0 + 0, DWGViewerActivity.this);
                            }

                        } else {
                            if (DialogBox.DialogBox("ATTENZIONE", "ID Disegno vuoto!\r\nEseguire la sincronizzazione ora ?", 0 + 1 + 2, DWGViewerActivity.this)) {
                                MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

                                Runnable myRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        done_change_current_piano();
                                    }
                                };

                                mainActivity.onSyncDWG(parentView, Integer.valueOf(IDPiano), false, DWGViewerActivity.this, getApplicationContext(), myRunnable);
                                // PianiSpinner.setSelection(position);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    public void done_change_current_piano() {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        try {
            setup_vani_spinner(getSupportActionBar(), false);
        } catch (Exception e) {
            e.getMessage();
        }

        try {
            setup_oggetti_spinner(getSupportActionBar(), false);
        } catch (Exception e) {
            e.getMessage();
        }

        // Rilettura piani
        try {

            if (LayersName == null) LayersName = new ArrayList<String>();
            if (LayersValues == null) LayersValues = new ArrayList<Integer>();
            if (LayersON == null) LayersON = new ArrayList<Boolean>();

            LayersName.clear();
            LayersValues.clear();
            LayersON.clear();

            mainActivity.GetLayers();

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            CheckBox mCheckbox = (CheckBox) findViewById(R.id.ckEmergenze);
            if (mCheckbox != null) {
                mCheckbox.setOnCheckedChangeListener(null);
                ShowLayerEmergenze = true;
                mCheckbox.setChecked(true);
                mCheckbox.setOnCheckedChangeListener(ckLampadinelistener);
            }
            mCheckbox = (CheckBox) findViewById(R.id.ckLampadine);
            if (mCheckbox != null) {
                mCheckbox.setOnCheckedChangeListener(null);
                ShowLayerLampadine = true;
                mCheckbox.setChecked(true);
                mCheckbox.setOnCheckedChangeListener(ckEmergenzelistener);
            }

            show_ifintervento_on_dwg();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mMyGLSurfaceView.requestRender();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void show_ifintervento_on_dwg() {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        String Filters = "";
        int numFilters = 0;
        int Mode = 0;

        if(ShowIfIntervento)
            Mode=1;
        else
            Mode=2;

        APPOggetti appOggetti = ShowIfIntervento ? APPData.appOggettiIfIntervento : APPData.appOggetti;

        for(int i = 0; i<appOggetti.CodOggetti.size();i++) {
            if (!appOggetti.CodOggetti.get(i).isEmpty()) {
                if (numFilters > 0) Filters += ",";
                Filters += appOggetti.CodOggetti.get(i);
                numFilters++;
            }
        }

        int nflt = mainActivity.FilterDwgText(Filters, numFilters, Mode);

        setup_oggetti_spinner(getSupportActionBar(),false);

        if(mMyGLSurfaceView!=null) mMyGLSurfaceView.requestRender();
    }

    public void sync_dwg () {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        try {

            if (cPiano > 0) {
                Integer fltIDPiano = APPData.IDPiani.get(cPiano - 1);

                if (DialogBox.DialogBox("ATTENZIONE", "Sincronizzare il disegno di questo piano ?\r\n", 0 + 1 + 2, DWGViewerActivity.this)) {

                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            mMyGLSurfaceView.zoomExtens();
                            done_change_current_piano();
                        }
                    };


                    mainActivity.onSyncDWG(getWindow().getDecorView(), fltIDPiano, false, DWGViewerActivity.this, getApplicationContext(), myRunnable);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sync_interventy () {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        try {

            if (cPiano > 0) {
                Integer fltIDPiano = APPData.IDPiani.get(cPiano - 1);

                if (DialogBox.DialogBox("ATTENZIONE", "Sincronizzare gli interventi di questo piano ?\r\n", 0 + 1 + 2, DWGViewerActivity.this)) {

                    ////////////////////////////////////////////////////////////
                    // Lettura Tabella locale Interventi filtrando per piano
                    //
                    String fltIDEdificio = null; // APPData.IDEdifici.get(APPData.cEdificio);
                    String fltIDComplesso = String.valueOf(APPData.IDComplessi.get(APPData.cComplesso));
                    // String fltIDPiano = String.valueOf(APPData.IDPiani.get(APPData.cPiano));


                    APPInterventiSQL.leggi_interventi(null, null, String.valueOf(fltIDPiano), null, null, APPData.appInterventi);

                    int n_err = 0, n_add = 0, n = APPData.appInterventi.IDInterventiOnQueue.size();
                    for (int i = 0; i < n; i++) {
                        if (i < APPData.appInterventi.NumInterventi) {
                            Integer IDOnQueue = 0;
                            try {
                                IDOnQueue = APPData.appInterventi.IDInterventiOnQueue.get(i);
                            } catch (Exception e) { IDOnQueue = 0; }

                            if (IDOnQueue > 0) {
                                if (APPQueueSQL.de_queue(null, "SERVER", APPData.appInterventi.IDInterventiOnQueue.get(i), APPData.appInterventi.IDInterventi.get(i)) != 1) {
                                    n_err++;
                                } else {
                                    n_add++;
                                }
                            }
                        }
                    }

                    //////////////////////////////////////////////////////////
                    // Lettura eventuali interventi presenti nel server
                    //
                    if (APPInterventiSQL.sincronizza_interventi(null, null, String.valueOf(fltIDPiano), null, null, getApplicationContext(), this, null) > 0) {
                        APPData.appInterventi.cIntervento = -1;
                    }




                    ////////////////////////////////////////////////////////////////////////
                    // Rilettura Tabella locale Interventi dopo la sincronizzazione
                    //
                    APPInterventiSQL.leggi_interventi(null, null, null, null, null, APPData.appInterventi);

                    if (n_err > 0) {
                        String str = " Sincronizzazione Interventi fallit" + (n_err > 1 ? "e" : "a") + "!";
                        DialogBox.DialogBox("ATTENZIONE", str, 0 + 0, this);
                    } else {
                        String str = null;
                        if (n_add > 0) {
                            str = "Sincronizzazione completata\r\n\r\n" + n_add + " Intervent" + (n_add > 1 ? "i" : "o") + " inviat" + (n_add > 1 ? "i" : "o") + "";
                        } else {
                            str = "Sincronizzazione completata\r\n\nNessun intervento inviato";
                        }
                        DialogBox.DialogBox("ATTENZIONE", str, 0 + 0, this);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    public void setup_vani_spinner ( ActionBar actionBar, boolean startup ) {

        try {

            try {

                if (Vani == null) Vani = new ArrayList<String>();
                Vani.clear();
                Vani.add("<"+(APPData.Vani!=null?APPData.Vani.size():"0")+" Vani>");
                cVano = 0;

                Vani.addAll(APPData.Vani);

            } catch (Exception e) {
            }

            String[] elenco_vani = (Vani != null && !Vani.isEmpty()) ? ((String[]) Vani.toArray(new String[Vani.size()])) : (new String[]{""}); //

            ArrayAdapter<String> vani_adapter = new ArrayAdapter<String>(
                    actionBar.getThemedContext(), R.layout.simple_dropdown_item_right,
                    elenco_vani);



            Spinner VaniSpinner = (Spinner) findViewById(R.id.spinVani);
            if (VaniSpinner != null) {
                VaniSpinner.setAdapter(vani_adapter);
                VaniSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        if (APPData.Vani != null && !APPData.Vani.isEmpty()) {
                            if (position > 0 && position <= APPData.Vani.size()) {
                                if (position - 1 != cVano) {
                                    cVano = position - 1;
                                } else {
                                }
                                // SearchMode = 2    -> Exact search
                                // SearchMode = 1    -> Free search
                                // SearchMode = 0    -> Exact key search
                                if (mMyGLSurfaceView.searchForTxt(APPData.Vani.get(cVano), "VANO", 2) <= 0) {
                                    String LAST_SEL_VANO = APPData.Vani.get(cVano);
                                    String LAST_SEL_IDVANO = String.valueOf(APPData.IDVani.get(cVano));

                                    // getWindow().getDecorView().setAlpha(0.2f);
                                    mGLView.setAlpha(0.2f);

                                    // Salvataggio telecamera
                                    MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                                    mainActivity.WriteCamera();

                                    Intent myIntent = new Intent(DWGViewerActivity.this, APPVaniActivity.class);

                                    myIntent.putExtra("VanoID", LAST_SEL_IDVANO);
                                    myIntent.putExtra("VanoCode", LAST_SEL_VANO);
                                    myIntent.putExtra("Key", ""); //Optional parameters
                                    startActivity(myIntent);
                                }
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });
                VaniSpinner.setSelection(cVano);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }






    public void setup_oggetti_spinner ( ActionBar actionBar, boolean startup ) {

        try {

            try {

                APPOggetti appOggetti = ShowIfIntervento?APPData.appOggettiIfIntervento:APPData.appOggetti;

                if (Oggetti == null) Oggetti = new ArrayList<String>();
                Oggetti.clear();
                Oggetti.add("<"+(appOggetti!=null?appOggetti.IDOggetti.size():"0")+" Oggetti>");
                cOggetto = 0;

                if (appOggetti != null)
                    Oggetti.addAll(appOggetti.CodOggetti);

            } catch (Exception e) {
            }


            String[] elenco_oggetti = (Oggetti != null && !Oggetti.isEmpty()) ? ((String[]) Oggetti.toArray(new String[Oggetti.size()])) : (new String[]{""}); //

            ArrayAdapter<String> oggetti_adapter = new ArrayAdapter<String>(
                    actionBar.getThemedContext(), R.layout.simple_dropdown_item_right,
                    elenco_oggetti);


            Spinner OggettiSpinner = (Spinner) findViewById(R.id.spinOggetti);
            if (OggettiSpinner != null) {
                OggettiSpinner.setAdapter(oggetti_adapter);
                if (startup) {
                    OggettiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            APPOggetti appOggetti = ShowIfIntervento ? APPData.appOggettiIfIntervento : APPData.appOggetti;

                            if (appOggetti != null) {
                                if (appOggetti.CodOggetti != null && !appOggetti.CodOggetti.isEmpty()) {
                                    if (position > 0 && position <= appOggetti.CodOggetti.size()) {
                                        if (position - 1 != appOggetti.cOggetto) {
                                            appOggetti.cOggetto = position - 1;
                                        }
                                        // SearchMode = 2    -> Exact search
                                        // SearchMode = 1    -> Free search
                                        // SearchMode = 0    -> Exact key search
                                        mMyGLSurfaceView.searchForTxt(appOggetti.CodOggetti.get(position - 1), "OGGETTO", 0);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }
                    });
                    OggettiSpinner.setSelection(cOggetto);
                }
            }


        } catch (Exception e) {
        e.printStackTrace();
        }
    }






        @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_dwgviewer, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView)searchItem.getActionView();
        setupSearchView(searchItem);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // SearchMode = 2    -> Exact search
                // SearchMode = 1    -> Free search
                // SearchMode = 0    -> Exact key search
                doSearchText(null, 1);
                return true;
            }
        });


        return true;
    }





    // SearchMode = 2    -> Exact search
    // SearchMode = 1    -> Free search
    // SearchMode = 0    -> Exact key search
    public boolean doSearchText(String pSearch, int searchMode) {
        String searchTest = null;

        if (pSearch != null) {
            searchTest = pSearch;
        } else {
            EditText et = (EditText) findViewById(R.id.etSearch);
            if (et != null) {
                searchTest = et.getText().toString();
                if (searchTest != null && !searchTest.isEmpty()) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et.getWindowToken(), 0);

                    // Salva la stringa da cercare
                    APPData.LAST_SEARCH_TEXT_ON_DWG = searchTest;
                    MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                    mainActivity.sqliteWrapper.update_setup_record("LastSearchTextOnWG", APPData.LAST_SEARCH_TEXT_ON_DWG);
                }
            }
        }

        if (searchTest != null && !searchTest.isEmpty()) {
            final String search_text = searchTest;
            final int search_mode = searchMode;
            this.runOnUiThread ( new Runnable() {
                @Override public void run() {
                    MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                    mainActivity.lastClickOn = true;
                    mainActivity.lastClickCount = 1;
                    // SearchMode = 2    -> Exact search
                    // SearchMode = 1    -> Free search
                    // SearchMode = 0    -> Exact key search
                    mMyGLSurfaceView.searchForTxt(search_text, "", search_mode);
                }
            });
        }

        return false;
    }




    final MyRunnable after_layer_managed = new MyRunnable(null, null) {
        @Override
        public void run() {
            try {
                if (booleanArray != null) {
                    if (booleanArray.length > 0) {
                        int EmergenzeCounter = 0;
                        int LampadineCounter = 0;

                        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                        int anyError = 0;

                        for (int i = 0; i < booleanArray.length; i++) {

                            LayersON.set(i, booleanArray[i]);

                            if (mainActivity.SetLayer(LayersName.get(i), booleanArray[i]) <= 0) {
                                anyError++;
                            }

                            if (LayersName.get(i).compareToIgnoreCase("CODICE EMERGENZE")==0 || LayersName.get(i).compareToIgnoreCase("CODICE EMERGENZA")==0) {
                                if (booleanArray[i]) EmergenzeCounter++; else EmergenzeCounter--;
                            }
                            if (LayersName.get(i).compareToIgnoreCase("LAMPADE DI EMERGENZA")==0 || LayersName.get(i).compareToIgnoreCase("LAMPADINE DI EMERGENZA")==0) {
                                if (booleanArray[i]) EmergenzeCounter++; else EmergenzeCounter--;
                            }
                            if (LayersName.get(i).compareToIgnoreCase("CODICE LAMPADINE")==0 || LayersName.get(i).compareToIgnoreCase("CODICE LAMPADE")==0) {
                                if (booleanArray[i]) LampadineCounter++; else LampadineCounter--;
                            }
                            if (LayersName.get(i).compareToIgnoreCase("LAMPADINE")==0 || LayersName.get(i).compareToIgnoreCase("LAMPADE")==0) {
                                if (booleanArray[i]) LampadineCounter++; else LampadineCounter--;
                            }
                        }

                        CheckBox mCheckbox = (CheckBox) findViewById(R.id.ckEmergenze);
                        if (mCheckbox!=null) {
                            if (EmergenzeCounter == 2) {
                                mCheckbox.setChecked(true);
                                ShowLayerEmergenze = true;
                            } else if (EmergenzeCounter == -2) {
                                mCheckbox.setChecked(false);
                                ShowLayerEmergenze = false;
                            } else {
                            }
                        }
                        mCheckbox = (CheckBox) findViewById(R.id.ckLampadine);
                        if (mCheckbox!=null) {
                            if (LampadineCounter == 2) {
                                mCheckbox.setChecked(true);
                                ShowLayerLampadine = true;
                            } else if (LampadineCounter == -2) {
                                mCheckbox.setChecked(false);
                                ShowLayerLampadine = false;
                            } else {
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMyGLSurfaceView.requestRender();
        }
    };



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
            float X = mainActivity.GetCamera(0);
            float Y = mainActivity.GetCamera(1);
            float WX = mainActivity.GetCamera(2);

            mMyGLSurfaceView.zoomExtens();
            mMyGLSurfaceView.requestRender();


        } else if (id == R.id.action_sync_dwg) {
            sync_dwg();


        } else if (id == R.id.action_sync_interventi) {
            sync_interventy();


        } else if (id == R.id.action_layers) {

            APPUtil.getSelectFromCheckbox(LayersName, LayersON, null, 0, "Gestione livelli", DWGViewerActivity.this, getWindow().getDecorView(), after_layer_managed, -1);





            // Demo mappe
            /*
        } else if (id == R.id.action_show_map || id == R.id.action_show_map2 || id == R.id.action_show_map3) {
            Intent myIntent = new Intent(DWGViewerActivity.this, OSMViewActivity.class);
            String Url = null;

            if (id == R.id.action_show_map) {
                Url = "http://customers.cristianandreon.com/Geisoft/testmap.html";
            } else if (id == R.id.action_show_map2) {
                Url = "http://customers.cristianandreon.com/Geisoft/testmap2.html";
            } else if (id == R.id.action_show_map3) {
                Url = "http://customers.cristianandreon.com/Geisoft/testmap3.html";
            }

            MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

            mainActivity.run_map_activity(mainActivity.context, DWGViewerActivity.this, Url, null, null, null);
            */


            // Exit
        } else if (id == 16908332) {
            reset_env_param();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // View rootView = inflater.inflate(R.layout.fragment_dwgviewer, container, false);
            return mGLView;
        }
    }





    private void setupSearchView(MenuItem searchItem) {

        try {

            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

            sctionBarSearchView = new ActionBarSearchView(this);
            if (mSearchView != null)
                mSearchView.setOnQueryTextListener(sctionBarSearchView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}









class ActionBarSearchView extends Activity implements SearchView.OnQueryTextListener {

    Activity mActivity = null;

    ActionBarSearchView(Activity activity) {
        mActivity = activity;
    }

    public boolean onQueryTextChange(String newText) {
        // mStatusView.setText("Query = " + newText);
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        // mStatusView.setText("Query = " + query + " : submitted");
        return false;
    }

    public boolean onClose() {
        // mStatusView.setText("Closed!");
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }
}


