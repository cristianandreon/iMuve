package com.imuve.cristian.imuve;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class APPSelOggettiActivity extends ActionBarActivity {

    public Integer SelecteObjectID = null;
    public String SelecteObjectCode = null;
    public String SelecteObjectDesc = null;
    public String OnOKCallback = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();


        super.onCreate(savedInstanceState);



        Bundle extras = getIntent().getExtras();



        try {
            SelecteObjectID = Integer.valueOf(extras.getString("SelecteObjectID"));
        }catch (Exception e) { e.printStackTrace(); }

        try {
            SelecteObjectCode = extras.getString("SelecteObjectCode");
        }catch (Exception e) { e.printStackTrace(); }

        try {
            SelecteObjectDesc = extras.getString("SelecteObjectDesc");
            }catch (Exception e) { e.printStackTrace(); }

        try {
            OnOKCallback = extras.getString("OnOKCallback");
            }catch (Exception e) { e.printStackTrace(); }





        // Titolo activity
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        // actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.title_bar_gray)));

        String Title = "Seleziona oggetto " + SelecteObjectCode;
        actionBar.setTitle(Title);

        actionBar.show();


        setContentView(R.layout.activity_object_select);



        try {

            ((TextView)this.findViewById(R.id.tvDesc)).setText(SelecteObjectDesc);
            ((TextView)this.findViewById(R.id.tvOggetto)).setText(SelecteObjectCode);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }



        try {

            ListView lv = (ListView)findViewById(R.id.lvOggettiSelection);

            SingleRecordAdapter ListAdapter = new SingleRecordAdapter(this, APPData.appSelezioneOggetti.CodOggetti, APPData.appSelezioneOggetti.DescOggetti, APPData.appSelezioneOggetti.ExtDescOggetti, R.layout.rec_lv_3fields);

            lv.setAdapter(ListAdapter);


            // register onClickListener to handle click events on each item
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3) {
                    if (position >= 0 && position < APPData.appSelezioneOggetti.IDOggetti.size()) {
                        SelecteObjectDesc = APPData.appSelezioneOggetti.DescOggetti.get(position);
                        SelecteObjectCode = APPData.appSelezioneOggetti.CodOggetti.get(position);
                        SelecteObjectID = APPData.appSelezioneOggetti.IDOggetti.get(position);
                        SingleRecordAdapter sa = (SingleRecordAdapter)arg0.getAdapter();

                        sa.curItem = position;
                        ListView lv = (ListView)arg0;
                        lv.setItemChecked(position, true);
                        v.setBackgroundColor(Color.BLUE);

                    }
                    // System.err.println("onItemClick:"+position);
                }
            });

            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position>=0 && position < APPData.appSelezioneOggetti.IDOggetti.size()) {
                        SelecteObjectDesc = APPData.appSelezioneOggetti.DescOggetti.get(position);
                        SelecteObjectCode = APPData.appSelezioneOggetti.CodOggetti.get(position);
                        SelecteObjectID = APPData.appSelezioneOggetti.IDOggetti.get(position);
                        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
                        Handler mainHandler = new Handler(mainActivity.context.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                onOK(null);
                            }
                        };

                        mainHandler.post(myRunnable);
                    }
                    return true;
                }
            });


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }




        Button button = (Button) findViewById(R.id.btOk);
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


        ///////////////////////////////////
        // Immagine di sfondo
        //
        View backgroundimage = findViewById(R.id.background);
        if(backgroundimage != null) {
            Drawable background = backgroundimage.getBackground();
            background.setAlpha(40);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public void onOK(View v) {
        int res = 1;

        if (res > 0)
            finish();
            // MsgBox pMsgBox = new MsgBox("title", "message");

        if (OnOKCallback.compareToIgnoreCase("APPInterventiActivity") == 0) {
            MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();
            mainActivity.doClickOnObject ( String.valueOf(SelecteObjectID), SelecteObjectCode, mainActivity.context, this );
        }
    }

    public void onCancel(View v) {
        finish();
    }


}





