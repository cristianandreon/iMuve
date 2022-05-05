package com.imuve.cristian.imuve;

import android.content.ContentValues;

/**
 * Created by Cristian on 02/06/2015.
 */
public class APPPkPool {


    public static int leggi_pk_counter() {
        MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();
        int res = 0;
        int retVal = 0;

        // Lettura Valore Contatore
        /*
        # Servizio recupera chiave per keypools device (il tablename e' opzionale)
        # metodo : POST
        # path : /login-service/get-keypool
        # esempio chiamata
        http://pamap.geisoft.org/services/pamap/login-service/get-keypool?idsessione=1a64f71a-28fb-4d72-9a66-69386c5c2f85&pkpool=AA&tablename=IDINTERVENTO
                # esempio risposta
        {"codEsito":"S","data":[{"id":{"pkpool":"AA","tablename":"IDINTERVENTO"},"progr":"225"}]}
        */

        String serviceURL = APPData.getServiceURL("login-service/get-keypool", APPData.bEncript);
        String[] labelsParam = new String[]{"tablename", "pkpool"};
        String[] valuesParam = new String[]{"IDINTERVENTO", APPData.DEVICE_PKPOOL};
        String[] resultTags = new String[]{"codEsito"};
        String fieldsTags = "data";

        // "http://pamap.geisoft.org/services/pamap/login-service/get-keypool?idsessione=272ac0aa-b729-4512-951d-8ddd9fc56881&tablename=IDINTERVENTO&pkpool=AD"
        // "{\"codEsito\":\"S\",\"data\":[]}"
        // {"codEsito":"S","data":[{"id":{"pkpool":"AN","tablename":"IDINTERVENTO"},"progr":"1"}]}

        JSONParser jsonParser = new JSONParser(null);


        if (NetworkActivity.isOnline() > 0) {
            res = jsonParser.ParseURL(serviceURL, labelsParam, valuesParam, resultTags, null, fieldsTags, null, null, Constants.ENCRYPT, Constants.DECRYPT, true);
        } else {
            // Login offline
            // res = jsonParser.ParseString(APPData.LAST_..._RESPONSE, resultTags, null, fieldsTags);
        }



        if (res > 0) {

            APPData.COD_ESITO = jsonParser.Header != null ? jsonParser.Header.get(0) : "";
            if (APPData.COD_ESITO.equals("S")) {

                // Aggiunta lettura dal parser
                ContentValues values = new ContentValues();
                boolean bStoreResponse = false;

                int colIndex = jsonParser.Labels.indexOf("pkpool");
                if (colIndex >= 0) {
                    String Field = jsonParser.Recs.get(colIndex);
                    if (Field.compareToIgnoreCase(APPData.DEVICE_PKPOOL) != 0) {
                        // Errore nel servizio ?
                    }
                } else {
                }

                colIndex = jsonParser.Labels.indexOf("progr");
                if (colIndex >= 0) {
                    String Field = jsonParser.Recs.get(colIndex);
                    try {
                        APPData.InterventiNIDonServer = Integer.parseInt(Field);
                        retVal = 1;
                    } catch (Exception e) {
                        APPData.InterventiNIDonServer = 0;
                        retVal= -1;
                    }
                } else {
                    retVal = 1;
                    APPData.InterventiNIDonServer = 0;
                }

                // Store last response
                if (bStoreResponse) {
                    if (jsonParser.rawHttpContent != null) {
                        // APPData.LAST_..._RESPONSE = jsonParser.rawHttpContent;
                        // mainActivity.sqliteWrapper.update_setup_record("Last...Response", APPData.LAST_..._RESPONSE);
                    }
                }
            }
        }

        return retVal;
    }


    public static int scrivi_pk_counter() {
        MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();
        int res = 0;
        int retVal = 0;

        /*
        # Servizio assegna chiave a keypools device
        # metodo : POST
        # path : /login-service/update-keypool
        # esempio chiamata
        http://pamap.geisoft.org/services/pamap/login-service/update-keypool?idsessione=1a64f71a-28fb-4d72-9a66-69386c5c2f85&pkpool=AA&tablename=IDINTERVENTO&progr=1123
                # esempio risposta
        {"eseguito":true,"codEsito":"S"}
        */

        // "http://pamap.geisoft.org/services/pamap/login-service/update-keypool?idsessione=f5af2d96-50c6-4acb-91fc-6b1bad787311&tablename=IDINTERVENTO&pkpool=AD&progr=16"

        if (APPData.DEVICE_PKPOOL != null) {

            String serviceURL = APPData.getServiceURL("login-service/update-keypool", APPData.bEncript);
            String[] labelsParam = new String[]{"tablename", "pkpool", "progr"};
            String[] valuesParam = new String[]{"IDINTERVENTO", APPData.DEVICE_PKPOOL, String.valueOf(APPData.InterventiNID)};
            String[] resultTags = new String[]{"codEsito"};
            String fieldsTags = "data";


            JSONParser jsonParser = new JSONParser(null);


            if (NetworkActivity.isOnline() > 0) {
                res = jsonParser.ParseURL(serviceURL, labelsParam, valuesParam, resultTags, null, fieldsTags, null, null, Constants.ENCRYPT, Constants.DECRYPT, true);
            } else {
                // Login offline
                // res = jsonParser.ParseString(APPData.LAST_..._RESPONSE, resultTags, null, fieldsTags);
            }


            if (res > 0) {
                APPData.COD_ESITO = jsonParser.Header != null ? jsonParser.Header.get(0) : "";
                if (APPData.COD_ESITO.equals("S")) {
                } else {
                    retVal = -1;
                }
            } else {
                retVal = -2;
            }
        }

        return retVal;
    }

}

