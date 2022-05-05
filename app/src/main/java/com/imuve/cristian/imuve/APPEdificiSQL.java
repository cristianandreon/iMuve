package com.imuve.cristian.imuve;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;


/**
 * Created by Cristian on 23/04/2015.
 */
public class APPEdificiSQL {

    public APPEdificiSQL(Context context) {

    }



    public static int leggi_edifici ( Integer IDComplesso ) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();


        APPData.NumEdifici = 0;

        if (APPData.IDEdifici == null) APPData.IDEdifici = new ArrayList<Integer>();
        if (APPData.Edifici == null) APPData.Edifici = new ArrayList<String>();
        if (APPData.CodEdifici == null) APPData.CodEdifici = new ArrayList<String>();
        if (APPData.GpsLongEdifici == null) APPData.GpsLongEdifici = new ArrayList<String>();
        if (APPData.GpsLatEdifici == null) APPData.GpsLatEdifici = new ArrayList<String>();


        APPData.IDEdifici.clear();
        APPData.Edifici.clear();
        APPData.CodEdifici.clear();
        APPData.GpsLongEdifici.clear();
        APPData.GpsLatEdifici.clear();


        if (IDComplesso < 0) return 0;



        // Lettura tabella EDIFICIO
        /*
            db.execSQL("CREATE TABLE IF NOT EXISTS EDIFICIO (IDEDIFICIO TEXT,IDCOMPLESSO TEXT,CDEDIFICIO TEXT,DSEDIFICIO TEXT)");
                        */

        String[] cols = new String[] { "IDEDIFICIO", "IDCOMPLESSO", "CDEDIFICIO", "DSEDIFICIO", "GPSLONG", "GPSLAT" };

        String whereClause = null;
        if (IDComplesso > 0) whereClause = "IDCOMPLESSO='"+IDComplesso+"'";

        Cursor mCursor = null;

        try {

            mCursor = mainActivity.sqliteWrapper.db.query(false, "EDIFICIO", cols, whereClause, null, null, null, null, null);

            if (mCursor != null) {

                if (mCursor.moveToFirst()) {

                    do {
                        System.err.println("[" + mCursor.getString(0) + "," + mCursor.getString(1) + "," + mCursor.getString(2) + "]");

                        try {

                            String str = mCursor.getString(mCursor.getColumnIndex("IDEDIFICIO"))!=null?mCursor.getString(mCursor.getColumnIndex("IDEDIFICIO")):"";
                            APPData.IDEdifici.add(Integer.parseInt(str));

                            try {

                                str = mCursor.getString(mCursor.getColumnIndex("CDEDIFICIO"))!=null?mCursor.getString(mCursor.getColumnIndex("CDEDIFICIO")):"";
                                APPData.CodEdifici.add(str);

                                str = mCursor.getString(mCursor.getColumnIndex("DSEDIFICIO"))!=null?mCursor.getString(mCursor.getColumnIndex("DSEDIFICIO")):"";
                                APPData.Edifici.add(str);

                                try {
                                    str = mCursor.getString(mCursor.getColumnIndex("GPSLONG"))!=null?mCursor.getString(mCursor.getColumnIndex("GPSLONG")):"";
                                    APPData.GpsLongEdifici.add(str);
                                } catch (Exception e) {  }

                                try {
                                    str = mCursor.getString(mCursor.getColumnIndex("GPSLAT"))!=null?mCursor.getString(mCursor.getColumnIndex("GPSLAT")):"";
                                    APPData.GpsLatEdifici.add(str);
                                } catch (Exception e) {  }


                                APPData.NumEdifici++;

                            } catch (NumberFormatException e) { APPData.Edifici.add(""); }

                        } catch (Exception e) {  }

                    } while (mCursor.moveToNext());
                }
            }

        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

        if (mCursor != null) {
            mCursor.close();
        }


        if (APPData.cEdificio > 0) {
            if (APPData.cEdificio >= APPData.NumEdifici) APPData.cEdificio = APPData.NumEdifici>0 ? APPData.NumEdifici-1 : 0;
        }

        return 1;
    }


    /////////////////////////////////
    // Legge i complessi dal server
    //
    public static int sincronizza_edifici ( String fltIDComplesso,
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
        "{\"codEsito\":\"S\",\"data\":[{\"citta\":\"VENEZIA\",\"indirizzo\":\"SAN MARCO 1\",\"utenteIns\":\"azienda\",\"utenteAgg\":\"admin\",\"gpsLon\":null,\"enteAgg\":null,\"cdedificio\":\"PDUC\",\"provincia\":null,\"enteIns\":null,\"idedificio\":\"10\",\"cdnazione\":null,\"gpsLat\":null,\"dsedificio\":\"PALAZZO DUCALE\",\"dataIns\":\"2015-04-16 16:04:14.0\",\"dataAgg\":\"2015-05-12 23:24:18.0\",\"idcomplesso\":\"1\"},{\"citta\":\"VENEZIA\",\"indirizzo\":\"PIAZZETTA S.MARCO\",\"utenteIns\":\"azienda\",\"utenteAgg\":\"admin\",\"gpsLon\":null,\"enteAgg\":null,\"cdedificio\":\"PNUOVE\",\"provincia\":\"VE\",\"enteIns\":null,\"idedificio\":\"9\",\"cdnazione\":\"ita\",\"gpsLat\":null,\"dsedificio\":\"PRIGIONI NUOVE\",\"dataIns\":\"2015-04-15 15:24:41.0\",\"dataAgg\":\"2015-05-12 23:25:11.0\",\"idcomplesso\":\"1\"}]}"
        */


        // "CREATE TABLE IF NOT EXISTS EDIFICIO (IDEDIFICIO text,IDCOMPLESSO text,CDEDIFICIO text,DSEDIFICIO text)"


        String serviceURL = APPData.getServiceURL("oggetti-service/posizione", APPData.bEncript);
        String[] labelsParam = new String[]{"type_position", "idcomplesso"};
        String[] valuesParam = new String[]{"EDIFICIO", fltIDComplesso != null ? fltIDComplesso : "", ""};
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
                    String deleteSQL = "DELETE FROM EDIFICIO WHERE (";
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

                        int colIndex = jsonParser.Labels.indexOf("idedificio");
                        if (colIndex >= 0) {
                            String Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                            values.put("IDEDIFICIO", Field);
                            colIndex = jsonParser.Labels.indexOf("idcomplesso");
                            if (colIndex >= 0) {
                                Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                values.put("IDCOMPLESSO", Field);
                                colIndex = jsonParser.Labels.indexOf("cdedificio");
                                if (colIndex >= 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("CDEDIFICIO", Field);
                                    colIndex = jsonParser.Labels.indexOf("dsedificio");
                                    if (colIndex >= 0) {
                                        Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                        values.put("DSEDIFICIO", Field);

                                        colIndex = jsonParser.Labels.indexOf("gpsLon");
                                        if (colIndex >= 0) {
                                            Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                            values.put("GPSLONG", Field);
                                        }
                                        colIndex = jsonParser.Labels.indexOf("gpsLat");
                                        if (colIndex >= 0) {
                                            Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                            values.put("GPSLAT", Field);
                                        }

                                        mainActivity.sqliteWrapper.db.insert("EDIFICIO", "", values);
                                    }
                                }
                            }
                        } else {
                        }
                    }

                    APPData.LAST_READ_EDIFICIO = APPUtil.get_date_time();
                    mainActivity.sqliteWrapper.update_setup_record("LastReadEdificio", APPData.LAST_READ_EDIFICIO);
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
