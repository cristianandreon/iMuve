package com.imuve.cristian.imuve;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class APPVaniActivity extends ActionBarActivity {

    public String VanoID;
    public String VanoCode;
    public String Key;




    private void store_env_param() {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        APPData.LAST_ENV_ON_DWG = "Vani";
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWG",APPData.LAST_ENV_ON_DWG);
        APPData.LAST_ENV_ON_DWG_PARAM = VanoID;
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam",APPData.LAST_ENV_ON_DWG_PARAM);
        APPData.LAST_ENV_ON_DWG_PARAM1 = VanoCode;
        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam1", APPData.LAST_ENV_ON_DWG_PARAM1);
        APPData.LAST_ENV_ON_DWG_PARAM2 = Key;

        mainActivity.sqliteWrapper.update_setup_record("LastEnviromentOnDWGParam2", APPData.LAST_ENV_ON_DWG_PARAM2);
        APPData.LAST_ENV_ON_DWG_PARAM3 = "";
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();


        super.onCreate(savedInstanceState);



        Bundle extras = getIntent().getExtras();

        VanoID = extras.getString("VanoID");
        VanoCode = extras.getString("VanoCode");
        Key = extras.getString("Key");

        if (VanoCode == null || VanoCode.isEmpty()) {
            VanoCode = APPData.LAST_SEL_VANO;
        }
        if (VanoID == null || VanoID.isEmpty()) {
            VanoID = String.valueOf(APPData.LAST_SEL_IDVANO);
        }


        try {

            // Lettura interventi nel vano
            APPOggettiSQL.leggi_oggetti(0, 0, 0, Integer.valueOf(VanoID)/*APPData.LAST_SEL_VANO*/, APPData.appOggettiPerVano, APPData.appOggettiIfIntervento);

            /*
            String IDOggetto = "100";
            APPData.appOggettiPerVano.IDOggetti.add(Integer.parseInt(IDOggetto));

            String CodOggetto = "CODICE OGG 1";
            APPData.appOggettiPerVano.CodOggetti.add(CodOggetto);

            String DescOggetto = "DESCRIZIONE OGG 1";
            APPData.appOggettiPerVano.DescOggetti.add(DescOggetto);
            APPData.appOggettiPerVano.NumOggetti++;


            IDOggetto = "200";
            APPData.appOggettiPerVano.IDOggetti.add(Integer.parseInt(IDOggetto));

            CodOggetto = "CODICE OGG 2";
            APPData.appOggettiPerVano.CodOggetti.add(CodOggetto);

            DescOggetto = "DESCRIZIONE OGG 2";
            APPData.appOggettiPerVano.DescOggetti.add(DescOggetto);
            APPData.appOggettiPerVano.NumOggetti++;


            IDOggetto = "300";
            APPData.appOggettiPerVano.IDOggetti.add(Integer.parseInt(IDOggetto));

            CodOggetto = "CODICE OGG 3";
            APPData.appOggettiPerVano.CodOggetti.add(CodOggetto);

            DescOggetto = "DESCRIZIONE OGG 3";
            APPData.appOggettiPerVano.DescOggetti.add(DescOggetto);
            APPData.appOggettiPerVano.NumOggetti++;
            */


        } catch (Exception e) {
            e.printStackTrace();
        }



        // Titolo activity
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        // actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.title_bar_gray)));

        String Title = "Vano : " + VanoCode;
        actionBar.setTitle(Title);

        actionBar.show();




        try {

            setContentView(R.layout.activity_vani);

        } catch (Exception e) {
            e.printStackTrace();
        }





        ((TextView)this.findViewById(R.id.Complesso)).setText(APPData.LAST_SEL_COMPLESSO);
        ((TextView)this.findViewById(R.id.Edificio)).setText(APPData.LAST_SEL_EDIFICIO);
        ((TextView)this.findViewById(R.id.Piano)).setText(APPData.LAST_SEL_PIANO);
        ((TextView)this.findViewById(R.id.Vano)).setText(APPData.LAST_SEL_VANO);


        try {


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }



        try {

            ListView lv = (ListView)findViewById(R.id.lvVani);

            SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, APPData.appOggettiPerVano.CodOggetti, APPData.appOggettiPerVano.DescOggetti, APPData.appOggettiPerVano.ExtDescOggetti, R.layout.rec_lv_3fields);

            lv.setAdapter(ListAdapter);


            // register onClickListener to handle click events on each item
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3) {
                    if (position < APPData.appOggettiPerVano.IDOggetti.size()) {
                        try {
                            APPData.LAST_SEL_OGGETTO = APPData.appOggettiPerVano.CodOggetti.get(position);
                            APPData.LAST_SEL_IDOGGETTO = APPData.appOggettiPerVano.IDOggetti.get(position);
                            APPData.LAST_SEL_OGGETTO = APPData.appOggettiPerVano.CodOggetti.get(position);
                            APPData.LAST_SEL_OGGETTO_DESC = APPData.appOggettiPerVano.DescOggetti.get(position);

                            Intent myIntent = new Intent(APPVaniActivity.this, APPInterventiActivity.class);

                            myIntent.putExtra("cIntervento", "0");
                            myIntent.putExtra("ObjectID", APPData.LAST_SEL_IDOGGETTO);
                            myIntent.putExtra("ObjectCode", APPData.LAST_SEL_OGGETTO);
                            myIntent.putExtra("ObjectDesc", APPData.LAST_SEL_OGGETTO_DESC);

                            myIntent.putExtra("Key", ""); //Optional parameters

                            startActivity(myIntent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    // System.err.println("onItemClick:"+position);
                }
            });

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


        try {

            Button button = (Button) findViewById(R.id.ok);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onOK(v);
                }
            });


            APPUtil.hideSoftKeyboard(APPVaniActivity.this);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }



        ///////////////////////////////////
        // Immagine di sfondo
        //
        View backgroundimage = findViewById(R.id.background);
        if(backgroundimage != null) {
            Drawable background = backgroundimage.getBackground();
            background.setAlpha(40);
        }

        /////////////////////////////////////////
        // Registrazione coordinate ambiente
        //
        store_env_param();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appvani, menu);
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
            /*
            if (APPVaniSQL.sincronizza_vani(null, getApplicationContext(), APPVaniActivity.this, null) < 0) {
            }

            APPOggettiSQL.sincronizza_oggetti(fltIDComplesso, fltIDEdificio, fltIDPiano, fltIDVano, getApplicationContext(), APPVaniActivity.this, null);
            */

        } else if (id == R.id.action_exit) {
            this.onCancel(null);
        }

        return super.onOptionsItemSelected(item);
    }




    public void onOK(View v) {
        int res = 1;
        if (res > 0) {
            reset_env_data();
            finish();
        }
    }

    public void onCancel(View v) {
        reset_env_data();
        finish();
    }

    @Override
    public void onBackPressed() {
        reset_env_data();
        super.onBackPressed();
    }



}





