package com.imuve.cristian.imuve;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;


/**
 * Created by Cristian on 23/04/2015.
 */
public class APPComplessiSQL {

    public APPComplessiSQL(Context context) {

    }


    public static int leggi_complessi(Integer IDComplesso) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        APPData.NumComplessi = 0;

        if (APPData.IDComplessi == null) APPData.IDComplessi = new ArrayList<Integer>();
        if (APPData.Complessi == null) APPData.Complessi = new ArrayList<String>();


        APPData.IDComplessi.clear();
        APPData.Complessi.clear();


        if (IDComplesso < 0) return 0;


        // Lettura tabella COMPLESSO
        /*
            db.execSQL("CREATE TABLE IF NOT EXISTS COMPLESSO (IDCOMPLESSO TEXT,CDCOMPLESSO TEXT,DSCOMPLESSO TEXT)");
                        */

        String[] cols = new String[]{"IDCOMPLESSO", "CDCOMPLESSO", "DSCOMPLESSO"};

        String whereClause = null;
        if (IDComplesso > 0) whereClause = "IDCOMPLESSO='" + IDComplesso + "'";

        Cursor mCursor = null;

        try {

            mCursor = mainActivity.sqliteWrapper.db.query(false, "COMPLESSO", cols, whereClause, null, null, null, null, null);

            if (mCursor != null) {

                if (mCursor.moveToFirst()) {

                    do {
                        // System.err.println("[" + mCursor.getString(0) + "," + mCursor.getString(1) + "," + mCursor.getString(2) + "]");

                        try {
                            String str = mCursor.getString(mCursor.getColumnIndex("IDCOMPLESSO")) != null ? mCursor.getString(mCursor.getColumnIndex("IDCOMPLESSO")) : "";
                            APPData.IDComplessi.add(Integer.parseInt(str));
                            try {
                                str = mCursor.getString(mCursor.getColumnIndex("DSCOMPLESSO")) != null ? mCursor.getString(mCursor.getColumnIndex("DSCOMPLESSO")) : "";
                                APPData.Complessi.add(str);
                                APPData.NumComplessi++;
                            } catch (NumberFormatException e) {
                                APPData.Complessi.add("");
                            }
                        } catch (NumberFormatException e) {
                        }

                    } while (mCursor.moveToNext());
                }
            }

        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

        mCursor.close();

        if (APPData.cComplesso > 0) {
            if (APPData.cComplesso >= APPData.NumComplessi)
                APPData.cComplesso = APPData.NumComplessi > 0 ? APPData.NumComplessi - 1 : 0;
        }

        return 1;
    }


    /////////////////////////////////
    // Legge i complessi dal server
    //
    public static int sincronizza_complessi(String fltIDComplesso,
                                            Context context, Activity activity,
                                            SYNCTable syncTable) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        int res = 0;


        /*
        # Servizio get posizione  (il tipo posizione può essere COMPLESSO|EDIFICIO|PIANO|VANO|UNITA)
        # metodo : POST
        # path : /oggetti-service/posizione
        # esempio chiamata
        http://pamap.geisoft.org/services/pamap/oggetti-service/posizione?idpiano=&idsessione=&idvano=&idedificio=10&start=&idunita=&type_position=PIANO&size=&pk=&cd=&idcomplesso=
        # esempio risposta
        {"codEsito":"S","data":[{"idpiano":"4","cdpiano":"01","idedificio":"10","dspiano":"Primo piano di palazzo ducale","utenteIns":"azienda","utenteAgg":"azienda","dataIns":"2015-04-16 16:22:21.0","dataAgg":"2015-04-16 16:22:31.0"},{"idpiano":"5","cdpiano":"02","idedificio":"10","dspiano":"Secondo piano di palazzo ducale","utenteIns":"azienda","utenteAgg":"azienda","dataIns":"2015-04-16 16:22:32.0","dataAgg":"2015-04-16 16:22:41.0"},{"idpiano":"6","cdpiano":"03","idedificio":"10","dspiano":"Terzo piano di palazzo ducale","utenteIns":"azienda","utenteAgg":"azienda","dataIns":"2015-04-16 16:22:42.0","dataAgg":"2015-04-16 16:22:50.0"}]}
        */

        // "CREATE TABLE IF NOT EXISTS COMPLESSO (IDCOMPLESSO TEXT,CDCOMPLESSO TEXT,DSCOMPLESSO TEXT)"


        try {

            String serviceURL = APPData.getServiceURL("oggetti-service/posizione", APPData.bEncript);
            String[] labelsParam = new String[]{"type_position", "idcomplesso"};
            String[] valuesParam = new String[]{"COMPLESSO", fltIDComplesso != null ? fltIDComplesso : "", ""};
            String[] resultTags = new String[]{"codEsito"};
            String fieldsTags = "data";


            if (NetworkActivity.isOnline() > 0) {
                JSONParser jsonParser = new JSONParser(null);
                res = jsonParser.ParseURL(serviceURL, labelsParam, valuesParam, resultTags, null, fieldsTags, null, null, Constants.ENCRYPT, Constants.DECRYPT, true);
                if (res > 0) {
                    // Store into SQLite...

                    APPData.COD_ESITO = jsonParser.Header != null ? jsonParser.Header.get(0) : "";
                    if (APPData.COD_ESITO.equals("S")) {

                        // Cancellazione records esistenti
                        int nFilter = 0;
                        String deleteSQL = "DELETE FROM COMPLESSO WHERE (";
                        if (fltIDComplesso != null && !fltIDComplesso.isEmpty()) {
                            deleteSQL += "IDCOMPLESSO=" + fltIDComplesso;
                            nFilter++;
                        }
                        if (nFilter == 0) {
                            deleteSQL += "1==1";
                        }
                        deleteSQL += ")";

                        try {
                            mainActivity.sqliteWrapper.db.execSQL(deleteSQL);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        // Aggiunta lettura dal parser
                        ContentValues values = new ContentValues();
                        for (int iRec = 0; iRec < jsonParser.nRecs; iRec++) {


                            int colIndex = jsonParser.Labels.indexOf("idcomplesso");
                            if (colIndex >= 0) {
                                String Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                values.put("IDCOMPLESSO", Field);
                                colIndex = jsonParser.Labels.indexOf("cdcomplesso");
                                if (colIndex >= 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("CDCOMPLESSO", Field);
                                    colIndex = jsonParser.Labels.indexOf("dscomplesso");
                                    if (colIndex >= 0) {
                                        Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                        values.put("DSCOMPLESSO", Field);
                                        mainActivity.sqliteWrapper.db.insert("COMPLESSO", "", values);
                                    }
                                }
                            } else {
                            }
                        }

                        APPData.LAST_READ_COMPLESSO = APPUtil.get_date_time();
                        mainActivity.sqliteWrapper.update_setup_record("LastReadComplesso", APPData.LAST_READ_COMPLESSO);
                    }


                } else {
                    if (jsonParser.rawHttpStatus != 200 && jsonParser.rawHttpStatus != 201 && jsonParser.rawHttpStatus != 202 && jsonParser.rawHttpStatus != 203) {
                        String message = "Risposta dal server non valida [HTTP status:" + jsonParser.rawHttpStatus + "]";
                        if (syncTable == null) {
                            if (context != null) {
                                DialogBox.ShowMessage(message, context, 1);
                                message = "Server non disponibile!";
                                if (activity != null) {
                                    if (DialogBox.DialogBox("ATTENZIONE", message, 0 + 1 + 2, context)) {
                                    }
                                }
                            } else {
                                syncTable.RetVal = -1;
                                syncTable.Message += message;
                            }
                        }
                    }
                }
            }

        } catch (Exception e){
            if (syncTable == null) {
            } else {
                syncTable.RetVal = -1;
                syncTable.Message += "Exception : " + e.getMessage();
            }
            res = -1;
        }

        return res;
    }

}
