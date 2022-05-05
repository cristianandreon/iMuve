package com.imuve.cristian.imuve;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        try {

            ((EditText)this.findViewById(R.id.etZoomArea)).setText(String.valueOf(APPData.DRAW_DETECT_ZONE_ZOOM));
            ((EditText)this.findViewById(R.id.etZoomObj)).setText(String.valueOf(APPData.DRAW_DETECT_OBJECT_ZOOM));

            ((EditText)this.findViewById(R.id.etUserName)).setText(String.valueOf(APPData.USER_LOGIN));

            ((EditText)this.findViewById(R.id.etProtocol)).setText(String.valueOf(Constants.SERVER_PROTOCOL));
            ((EditText)this.findViewById(R.id.etServer)).setText(String.valueOf(Constants.SERVER_URL));
            ((EditText)this.findViewById(R.id.etPort)).setText(String.valueOf(Constants.SERVER_PORT));


            CheckBox ck = ((CheckBox)this.findViewById(R.id.ckAutoLogin));
            ck.setChecked(APPData.KEEP_LOGGED.compareToIgnoreCase("1")==0?true:false);

            // ck = ((CheckBox)this.findViewById(R.id.ckAutoSync));
            // if (ck!=null) ck.setChecked(APPData.AUTO_SYNC.compareToIgnoreCase("1")==0?true:false);

            ck = ((CheckBox)this.findViewById(R.id.ckStoreTypedData));
            if (ck!=null) ck.setChecked(APPData.STORE_TYPED1.compareToIgnoreCase("1")==0?true:false);

            ck = ((CheckBox)this.findViewById(R.id.ckNewIntervento));
            if (ck!=null) ck.setChecked(APPData.AVVIO_NUOVO_INTERVENTO.compareToIgnoreCase("1")==0?true:false);

            ck = ((CheckBox)this.findViewById(R.id.ckInterventiCloseOnNew));
            if (ck!=null) ck.setChecked(APPData.CLOSE_ON_NEW_INTERVENTO.compareToIgnoreCase("1")==0?true:false);

            ck = ((CheckBox)this.findViewById(R.id.ckDebug));
            if(ck!=null) {
                ck.setChecked(Constants.DEBUG);
                ck.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Constants.DEBUG = ((CheckBox)v).isChecked();
                    }
                });
            }

            Button button = (Button) findViewById(R.id.btReset);
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        try {

                            if (DialogBox.DialogBox("ATTENZIONE", "Ripristinare le coordinate del server (dkmap)?", 0 + 1 + 2, SettingsActivity.this)) {
                                String SERVER_PROTOCOL = Constants.SERVER_PROTOCOL;
                                String SERVER_URL = Constants.SERVER_URL;
                                Integer SERVER_PORT = Constants.SERVER_PORT;

                                Constants.reset_server_addr(false);

                                ((EditText) findViewById(R.id.etProtocol)).setText(String.valueOf(Constants.SERVER_PROTOCOL));
                                ((EditText) findViewById(R.id.etServer)).setText(String.valueOf(Constants.SERVER_URL));
                                ((EditText) findViewById(R.id.etPort)).setText(String.valueOf(Constants.SERVER_PORT));

                                Constants.SERVER_PROTOCOL = SERVER_PROTOCOL;
                                Constants.SERVER_URL = SERVER_URL;
                                Constants.SERVER_PORT = SERVER_PORT;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            button = (Button) findViewById(R.id.btTest);
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        try {

                            if (DialogBox.DialogBox("ATTENZIONE", "Impostare le coordinate del server test (geisoft)?", 0 + 1 + 2, SettingsActivity.this)) {
                                String SERVER_PROTOCOL = Constants.SERVER_PROTOCOL;
                                String SERVER_URL = Constants.SERVER_URL;
                                Integer SERVER_PORT = Constants.SERVER_PORT;

                                Constants.reset_server_addr(true);

                                ((EditText) findViewById(R.id.etProtocol)).setText(String.valueOf(Constants.SERVER_PROTOCOL));
                                ((EditText) findViewById(R.id.etServer)).setText(String.valueOf(Constants.SERVER_URL));
                                ((EditText) findViewById(R.id.etPort)).setText(String.valueOf(Constants.SERVER_PORT));

                                Constants.SERVER_PROTOCOL = SERVER_PROTOCOL;
                                Constants.SERVER_URL = SERVER_URL;
                                Constants.SERVER_PORT = SERVER_PORT;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            button = (Button) findViewById(R.id.btCheck);
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        try {

                            String loginURL = APPData.getServiceURL("login-service/logout", APPData.bEncript);
                            String[] labelsParam = new String[]{"username"};
                            String[] valuesParam = new String[]{"TEST"};
                            String[] resultTags = new String[]{"codEsito"};

                            if (Constants.DEBUG) {
                                DialogBox.DialogBox("DEBUG", "NetworkActivity.isOnline()=" + NetworkActivity.isOnline(), 0 + 0, SettingsActivity.this);
                            }

                            if (NetworkActivity.isOnline() > 0) {
                                JSONParser jsonParser = new JSONParser(null);
                                int res = jsonParser.ParseURL(loginURL, labelsParam, valuesParam, resultTags, null, null, null, null, Constants.ENCRYPT, Constants.DECRYPT, true);
                                if (Constants.DEBUG) {
                                    DialogBox.DialogBox("DEBUG", "jsonParser.rawHttpContent:" + jsonParser.rawHttpContent, 0 + 0, SettingsActivity.this);
                                }

                                if (res > 0) {
                                    if (jsonParser.rawHttpStatus == 200 || jsonParser.rawHttpStatus == 201 || jsonParser.rawHttpStatus == 202 || jsonParser.rawHttpStatus == 203) {
                                        if (jsonParser.rawHttpContent != null && !jsonParser.rawHttpContent.isEmpty()) {
                                            if (jsonParser.nHeader >= 1 && !jsonParser.Header.get(0).isEmpty()) {
                                                DialogBox.DialogBox("INFORMAZIONE", "Test collegamento supertato", 0 + 0, SettingsActivity.this);
                                            } else {
                                                DialogBox.DialogBox("ATTENZIONE", "Test collegamento incompleto (risposta inattesa)", 0 + 0, SettingsActivity.this);
                                            }
                                        } else {
                                            DialogBox.DialogBox("ATTENZIONE", "Test collegamento al server fallito", 0 + 0, SettingsActivity.this);
                                        }
                                    } else {
                                        DialogBox.DialogBox("ATTENZIONE", "Test collegamento al server fallito (risposta non valida)", 0 + 0, SettingsActivity.this);
                                    }
                                } else {
                                    DialogBox.DialogBox("ATTENZIONE", "Test collegamento al server fallito (connessione fallita)", 0 + 0, SettingsActivity.this);
                                }
                            } else {
                                DialogBox.DialogBox("ATTENZIONE", "Connessione ad internet assente", 0 + 0, SettingsActivity.this);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }





        try {

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

    }








    public void onOK(View v) {
        int res = 1;
        MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();

        EditText et = ((EditText)this.findViewById(R.id.etZoomArea));
        float DRAW_DETECT_ZONE_ZOOM = Float.parseFloat(et.getText().toString());
        if (DRAW_DETECT_ZONE_ZOOM > 1000f) DRAW_DETECT_ZONE_ZOOM = 1000f;
        if (DRAW_DETECT_ZONE_ZOOM < 0.001f) DRAW_DETECT_ZONE_ZOOM = 0.001f;
        APPData.DRAW_DETECT_ZONE_ZOOM = DRAW_DETECT_ZONE_ZOOM;
        mainActivity.sqliteWrapper.update_setup_record("DrawDetectZoneZoom", String.valueOf(APPData.DRAW_DETECT_ZONE_ZOOM));

        et = ((EditText)this.findViewById(R.id.etZoomObj));
        float DRAW_DETECT_OBJECT_ZOOM = Float.parseFloat(et.getText().toString());
        if (DRAW_DETECT_OBJECT_ZOOM > 1000f) DRAW_DETECT_OBJECT_ZOOM = 1000f;
        if (DRAW_DETECT_OBJECT_ZOOM < 0.001f) DRAW_DETECT_OBJECT_ZOOM = 0.001f;
        APPData.DRAW_DETECT_OBJECT_ZOOM = DRAW_DETECT_OBJECT_ZOOM;
        mainActivity.sqliteWrapper.update_setup_record("DrawDetectObjectZoom", String.valueOf(APPData.DRAW_DETECT_OBJECT_ZOOM));


        try {
            et = ((EditText)this.findViewById(R.id.etProtocol));
            Constants.SERVER_PROTOCOL = et.getText().toString()!=null?et.getText().toString():"";
            if (mainActivity.sqliteWrapper.update_setup_record("SERVER_PROTOCOL", Constants.SERVER_PROTOCOL) > 0) {
                String SERVER_PROTOCOL = mainActivity.sqliteWrapper.read_setup_record("SERVER_PROTOCOL", "");
                if (!SERVER_PROTOCOL.equalsIgnoreCase(Constants.SERVER_PROTOCOL)) {
                    DialogBox.DialogBox("DEBUG", "Error saving config", 0 + 0, SettingsActivity.this);
                }
            } else {
                DialogBox.DialogBox("DEBUG", "Error updating config", 0 + 0, SettingsActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            et = ((EditText)this.findViewById(R.id.etServer));
            Constants.SERVER_URL = et.getText().toString()!=null?et.getText().toString():"";
            if (mainActivity.sqliteWrapper.update_setup_record("SERVER_URL", Constants.SERVER_URL) > 0) {
                String SERVER_URL = mainActivity.sqliteWrapper.read_setup_record("SERVER_URL", "");
                if (!SERVER_URL.equalsIgnoreCase(Constants.SERVER_URL)) {
                    DialogBox.DialogBox("DEBUG", "Error saving config", 0 + 0, SettingsActivity.this);
            }
            } else {
                DialogBox.DialogBox("DEBUG", "Error updating config", 0 + 0, SettingsActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            et = ((EditText) this.findViewById(R.id.etPort));
            Constants.SERVER_PORT = Integer.valueOf(et.getText().toString() != null ? et.getText().toString() : "0");
            mainActivity.sqliteWrapper.update_setup_record("SERVER_PORT", String.valueOf(Constants.SERVER_PORT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        et = ((EditText)this.findViewById(R.id.etUserName));
        APPData.USER_LOGIN = et.getText().toString();
        mainActivity.sqliteWrapper.update_setup_record("UserLogin", APPData.USER_LOGIN);


        CheckBox ck = ((CheckBox)this.findViewById(R.id.ckAutoLogin));
        if(ck!=null) {
            APPData.KEEP_LOGGED = ck.isChecked() ? "1" : "0";
            mainActivity.sqliteWrapper.update_setup_record("KeepLogged", APPData.KEEP_LOGGED);
        }



        /*ck = ((CheckBox)this.findViewById(R.id.ckAutoSync));
        if(ck!=null) {
            APPData.AUTO_SYNC = ck.isChecked() ? "1" : "0";
            mainActivity.sqliteWrapper.update_setup_record("AutoSync", APPData.AUTO_SYNC);
        }*/

        ck = ((CheckBox)this.findViewById(R.id.ckStoreTypedData));
        if(ck!=null) {
            APPData.STORE_TYPED1 = ck.isChecked() ? "1" : "0";
            mainActivity.sqliteWrapper.update_setup_record("StoreTyped1", APPData.STORE_TYPED1);
        }

        ck = ((CheckBox)this.findViewById(R.id.ckStoreTypedData));
        if(ck!=null) {
            APPData.STORE_TYPED1 = ck.isChecked() ? "1" : "0";
            mainActivity.sqliteWrapper.update_setup_record("StoreTyped1", APPData.STORE_TYPED1);
        }

        ck = ((CheckBox)this.findViewById(R.id.ckNewIntervento));
        if(ck!=null) {
            APPData.AVVIO_NUOVO_INTERVENTO = ck.isChecked() ? "1" : "0";
            mainActivity.sqliteWrapper.update_setup_record("AvvioNuovoIntervento", APPData.AVVIO_NUOVO_INTERVENTO);
        }

        ck = ((CheckBox)this.findViewById(R.id.ckInterventiCloseOnNew));
        if(ck!=null) {
            APPData.CLOSE_ON_NEW_INTERVENTO = ck.isChecked() ? "1" : "0";
            mainActivity.sqliteWrapper.update_setup_record("ChiudiNuovoIntervento", APPData.AVVIO_NUOVO_INTERVENTO);
        }




        if (res > 0)
            finish();
    }








    public void onCancel(View v) {
        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
