package com.imuve.cristian.imuve;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;


/**
 * Created by Cristian on 23/04/2015.
 */
public class APPPianiSQL {

    public APPPianiSQL(Context context) {

    }



    public static int leggi_piani ( Integer IDEdificio ) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();


        APPData.NumPiani= 0;

        if (APPData.IDPiani == null) APPData.IDPiani = new ArrayList<Integer>();
        if (APPData.Piani == null) APPData.Piani = new ArrayList<String>();
        if (APPData.CodPiani == null) APPData.CodPiani = new ArrayList<String>();
        if (APPData.PianiDesc == null) APPData.PianiDesc = new ArrayList<String>();
        if (APPData.DWGPiani == null) APPData.DWGPiani = new ArrayList<String>();
        if (APPData.IDDWGPiani == null) APPData.IDDWGPiani = new ArrayList<String>();

        APPData.IDPiani.clear();
        APPData.Piani.clear();
        APPData.CodPiani.clear();
        APPData.PianiDesc.clear();
        APPData.DWGPiani.clear();
        APPData.IDDWGPiani.clear();



        if (IDEdificio < 0) return 0;


        // Lettura tabella COMPLESSO
        /*
            "CREATE TABLE IF NOT EXISTS PIANO (IDPIANO TEXT,IDEDIFICIO TEXT,CDPIANO TEXT,DSPIANO TEXT)");
            "CREATE TABLE IF NOT EXISTS DWG (IDDWG text,IDPIANO text,DWGNAME text,DWGVERSION text,DWGDATA text)");
            */

        // String[] cols = new String[] { "IDPIANO", "IDEDIFICIO","CDPIANO","DSPIANO" };
        String whereClause = null;
        if (IDEdificio > 0) whereClause = "PIANO.IDEDIFICIO='"+IDEdificio+"'";

        String rawQuesry = "SELECT DWG.id AS DWG_ID, DWG.IDDWG, DWG.DWGNAME AS DWG_DWGNAME, DWG.IDDWG AS DWG_IDDWG, DWG.IDPIANO AS DWG_IDPIANO" +
                ",PIANO.IDPIANO, PIANO.IDEDIFICIO, CDPIANO, DSPIANO" +
                ",EDIFICIO.CDEDIFICIO AS CDEDIFICIO, EDIFICIO.DSEDIFICIO AS DSEDIFICIO" +
                " FROM PIANO";
        rawQuesry += " LEFT JOIN DWG ON PIANO.IDPIANO = DWG.IDPIANO ";
        rawQuesry += " LEFT JOIN EDIFICIO ON PIANO.IDEDIFICIO=EDIFICIO.IDEDIFICIO ";


        if (whereClause != null) {
            rawQuesry += " WHERE (";
            rawQuesry += whereClause;
            rawQuesry += ")";
        }


        Cursor mCursor = null;

        try {

            // mCursor = mainActivity.sqliteWrapper.db.query(false, "PIANO", cols, whereClause, null, null, null, null, null);
            mCursor = mainActivity.sqliteWrapper.db.rawQuery(rawQuesry, null);

            if (mCursor != null) {

                if (mCursor.moveToFirst()) {

                    int numError = 0;

                    do {

                        // System.err.println("[" + mCursor.getString(0) + "," + mCursor.getString(1) + "," + mCursor.getString(2) + "]");

                        try {
                            String strIDPiano = mCursor.getString(mCursor.getColumnIndex("IDPIANO"))!=null?mCursor.getString(mCursor.getColumnIndex("IDPIANO")):"";
                            APPData.IDPiani.add(Integer.parseInt(strIDPiano));
                            try {

                                String str = mCursor.getString(mCursor.getColumnIndex("CDPIANO"))!=null?mCursor.getString(mCursor.getColumnIndex("CDPIANO")):"";
                                APPData.CodPiani.add(str);

                                String sDSPIANO = mCursor.getString(mCursor.getColumnIndex("DSPIANO"))!=null?mCursor.getString(mCursor.getColumnIndex("DSPIANO")):"";
                                APPData.Piani.add(sDSPIANO);

                                str = mCursor.getString(mCursor.getColumnIndex("IDDWG"))!=null?mCursor.getString(mCursor.getColumnIndex("IDDWG")):"";
                                // APPData.IDDWGPiani.add(str);

                                String sDWG_ID = mCursor.getString(mCursor.getColumnIndex("DWG_ID"))!=null?mCursor.getString(mCursor.getColumnIndex("DWG_ID")):"";
                                APPData.IDDWGPiani.add(sDWG_ID);


                                str = mCursor.getString(mCursor.getColumnIndex("DWG_DWGNAME"))!=null?mCursor.getString(mCursor.getColumnIndex("DWG_DWGNAME")):"";
                                APPData.DWGPiani.add(str);



                                String sDescPiano = "";
                                String sDescEdificio = "";

                                sDescPiano = mCursor.getString(mCursor.getColumnIndex("DSPIANO"))!=null?mCursor.getString(mCursor.getColumnIndex("DSPIANO")):"";
                                APPData.DWGPiani.add(str);

                                sDescEdificio = mCursor.getString(mCursor.getColumnIndex("DSEDIFICIO"))!=null?mCursor.getString(mCursor.getColumnIndex("DSEDIFICIO")):"";
                                APPData.DWGPiani.add(str);


                                str = mCursor.getString(mCursor.getColumnIndex("DWG_DWGNAME"))!=null?mCursor.getString(mCursor.getColumnIndex("DWG_DWGNAME")):"";
                                APPData.DWGPiani.add(str);

                                // if (sDWG_ID == null || sDWG_ID.isEmpty()) sPianoDesc += "...";
                                // sPianoDesc += sDSPIANO;

                                APPData.PianiDesc.add(sDescPiano+"-"+sDescEdificio);

                                APPData.NumPiani++;
                            } catch (NumberFormatException e) { APPData.Piani.add(""); }
                        } catch (NumberFormatException e) {  }
                    } while (mCursor.moveToNext());
                }
            }


        } catch (Throwable e) {
            System.err.print(e.getMessage());
        }

        if (mCursor != null) {
            mCursor.close();
        }

        if (APPData.cPiano > 0) {
            if (APPData.cPiano >= APPData.NumPiani) APPData.cPiano = APPData.NumPiani>0 ? APPData.NumPiani-1 : 0;
        }

        return 1;
    }


    /////////////////////////////////
    // Legge i complessi dal server
    //
    public static int sincronizza_piani ( String fltEdificio,
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

        // "CREATE TABLE IF NOT EXISTS PIANO (IDPIANO text,IDEDIFICIO text,CDPIANO text,DSPIANO text)"


        String serviceURL = APPData.getServiceURL("oggetti-service/posizione", APPData.bEncript);
        String[] labelsParam = new String[]{"type_position", "idedificio"};
        String[] valuesParam = new String[]{"PIANO", fltEdificio != null ? fltEdificio : "", ""};
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
                    String deleteSQL = "DELETE FROM PIANO WHERE (";
                    if (fltEdificio != null && !fltEdificio.isEmpty()) {
                        deleteSQL += "IDEDIFICIO=" + fltEdificio;
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

                        int colIndex = jsonParser.Labels.indexOf("idpiano");
                        if (colIndex >= 0) {
                            String Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                            values.put("IDPIANO", Field);
                            colIndex = jsonParser.Labels.indexOf("idedificio");
                            if (colIndex >= 0) {
                                Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                values.put("IDEDIFICIO", Field);
                                colIndex = jsonParser.Labels.indexOf("cdpiano");
                                if (colIndex >= 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("CDPIANO", Field);
                                    colIndex = jsonParser.Labels.indexOf("dspiano");
                                    if (colIndex >= 0) {
                                        Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                        values.put("DSPIANO", Field);
                                    }
                                    mainActivity.sqliteWrapper.db.insert("PIANO", "", values);
                                }
                            }
                        } else {
                        }
                    }

                    APPData.LAST_READ_PIANO = APPUtil.get_date_time();
                    mainActivity.sqliteWrapper.update_setup_record("LastReadPiano", APPData.LAST_READ_PIANO);
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
