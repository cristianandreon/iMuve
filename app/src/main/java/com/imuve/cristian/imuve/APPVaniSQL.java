package com.imuve.cristian.imuve;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;


/**
 * Created by Cristian on 23/04/2015.
 */
public class APPVaniSQL {

    public APPVaniSQL(Context context) {

    }



    public static int leggi_vani ( Integer IDPiano ) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        APPData.NumVani = 0;

        if (APPData.IDVani == null) APPData.IDVani = new ArrayList<Integer>();
        if (APPData.Vani == null) APPData.Vani = new ArrayList<String>();
        if (APPData.CodVani == null) APPData.CodVani = new ArrayList<String>();

        APPData.IDVani.clear();
        APPData.Vani.clear();
        APPData.CodVani.clear();


        if (IDPiano < 0) return 0;


        // Lettura tabella VANO
        /*
            db.execSQL("CREATE TABLE IF NOT EXISTS VANO (IDVANO TEXT,IDPIANO TEXT,CDVANO TEXT,DSVANO TEXT)");
                        */

        String[] cols = new String[] { "IDVANO", "IDPIANO","CDVANO","DSVANO" };

        String whereClause = null;
        if (IDPiano > 0) whereClause = "IDPIANO='"+IDPiano+"'";

        Cursor mCursor = null;

        try {

            mCursor = mainActivity.sqliteWrapper.db.query(false, "VANO", cols, whereClause, null, null, null, null, null);

            if (mCursor != null) {

                if (mCursor.moveToFirst()) {

                    do {

                        // System.err.println("[" + mCursor.getString(0) + "," + mCursor.getString(1) + "," + mCursor.getString(2) + "]");

                        try {
                            String str = mCursor.getString(mCursor.getColumnIndex("IDVANO"))!=null?mCursor.getString(mCursor.getColumnIndex("IDVANO")):"";
                            APPData.IDVani.add(Integer.parseInt(str));
                            try {

                                str = mCursor.getString(mCursor.getColumnIndex("CDVANO"))!=null?mCursor.getString(mCursor.getColumnIndex("CDVANO")):"";
                                APPData.CodVani.add(str);

                                str = mCursor.getString(mCursor.getColumnIndex("DSVANO"))!=null?mCursor.getString(mCursor.getColumnIndex("DSVANO")):"";
                                APPData.Vani.add(str);

                                APPData.NumVani++;
                            } catch (NumberFormatException e) { APPData.Vani.add(""); }
                        } catch (NumberFormatException e) {  }
                    } while (mCursor.moveToNext());
                }
            }

        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

        if (mCursor != null) {
            mCursor.close();
        }






        if (Constants.ADD_VANI_AS_DEBUG) {
            APPData.IDVani.add(999);
            APPData.CodVani.add("LN1");
            APPData.Vani.add("LN1");
            APPData.NumVani++;

            APPData.IDVani.add(998);
            APPData.CodVani.add("LN12");
            APPData.Vani.add("LN12");
            APPData.NumVani++;
        }


        if (APPData.cVano > 0) {
            if (APPData.cVano >= APPData.NumVani) APPData.cVano = APPData.NumVani>0 ? APPData.NumVani-1 : 0;
        }

        return 1;
    }


    /////////////////////////////////
    // Legge i complessi dal server
    //
    public static int sincronizza_vani(String fltPiano,
                                       Context context, Activity activity,
                                       SYNCTable syncTable
    ) {
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

        // "CREATE TABLE IF NOT EXISTS VANO (IDVANO text,IDPIANO text,CDVANO text,DSVANO text)"


        String serviceURL = APPData.getServiceURL("oggetti-service/posizione", APPData.bEncript);
        String[] labelsParam = new String[]{"type_position", "idpiano"};
        String[] valuesParam = new String[]{"VANO", fltPiano != null ? fltPiano : "", ""};
        String[] resultTags = new String[]{"codEsito"};
        String fieldsTags = "data";



        if (NetworkActivity.isOnline() > 0) {
            JSONParser jsonParser = new JSONParser(null);

            res = jsonParser.ParseURL(serviceURL, labelsParam, valuesParam, resultTags, null, fieldsTags, null, null, Constants.ENCRYPT, Constants.DECRYPT, true);
            if (res > 0) {


                APPData.COD_ESITO = jsonParser.Header != null ? jsonParser.Header.get(0) : "";
                if (APPData.COD_ESITO.equals("S")) {
                    // Store into SQLite...

                    // Cancellazione records esistenti
                    int nFilter = 0;
                    String deleteSQL = "DELETE FROM VANO WHERE (";
                    if (fltPiano != null && !fltPiano.isEmpty()) {
                        deleteSQL += "IDPIANO=" + fltPiano;
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

                        int colIndex = jsonParser.Labels.indexOf("idvano");
                        if (colIndex >= 0) {
                            String Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                            values.put("IDVANO", Field);
                            colIndex = jsonParser.Labels.indexOf("idpiano");
                            if (colIndex >= 0) {
                                Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                values.put("IDPIANO", Field);
                                colIndex = jsonParser.Labels.indexOf("cdvano");
                                if (colIndex >= 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("CDVANO", Field);
                                    colIndex = jsonParser.Labels.indexOf("dsvano");
                                    if (colIndex >= 0) {
                                        Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                        values.put("DSVANO", Field);
                                        mainActivity.sqliteWrapper.db.insert("VANO", "", values);
                                    }
                                }
                            }
                        } else {
                        }
                    }

                    APPData.LAST_READ_VANO = APPUtil.get_date_time();
                    mainActivity.sqliteWrapper.update_setup_record("LastReadVano", APPData.LAST_READ_VANO);
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

        return res;
    }

}
