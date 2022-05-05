package com.imuve.cristian.imuve;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;


/**
 * Created by Cristian on 23/04/2015.
 */
public class APPOggettiSQL {


    public static int leggi_oggetti(Integer IDComplesso, Integer IDEdificio, Integer IDPiano, Integer IDVano,
                                    APPOggetti pAppOggetti, APPOggetti pAppOggettiIfIntervento) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        if (pAppOggetti == null) return 0;

        pAppOggetti.setup();


        if (pAppOggettiIfIntervento != null) {
            pAppOggettiIfIntervento.setup();
        }


        if (IDVano < 0) return 0;


        ContentValues values = new ContentValues();




        // Lettura tabella OGGETTO
        /*
        db.execSQL("CREATE TABLE IF NOT EXISTS OGGETTO (IDOGGETTO TEXT,IDOGGETTO_P TEXT,CODICE TEXT,DESCRIZIONE TEXT," +
                "STATO TEXT, TIPO_DISPONIBILITA TEXT, SERIALNUMBER TEXT, PRODUTTORE TEXT, IDFORNITORE TEXT, IDARTICOLO TEXT, CDMODELLO TEXT," +
                "IDCOMPLESSO TEXT, IDEDIFICIO TEXT, IDPIANO TEXT, IDVANO TEXT, " +
                "IDUNITA TEXT, IDAREA TEXT, IDCATEGORIA TEXT, IDGRUPPO TEXT" +

                CDCONDIZIONE TEXT,DTCONTROLLO TEXT,CODGARANZIA TEXT,TIPO_GARANZIA TEXT,DT_INIZIO_GAR TEXT,DT_FINE_GAR TEXT,DT_INSTALLAZIONE TEXT,
                DT_COLLAUDO_GAR TEXT,INSTALLATORE TEXT,IDIMPIANTO TEXT,T_ALTEZZA TEXT,T_LARGHEZZA TEXT,T_PROFONDITA TEXT,T_PESO TEXT,T_COLORE TEXT,
                T_CONSUMO_ELETT TEXT,T_VOLTAGGIO TEXT,T_WATT_EQUIVAL TEXT,T_POS_ALTEZZA TEXT,T_NR_OGGETTI TEXT,NOTE TEXT,DATA_INS TEXT,DATA_AGG TEXT,
                UTENTE_INS TEXT,UTENTE_AGG TEXT,ENTE_INS TEXT,ENTE_AGG TEXT


        // Ulteriori Colonne

                        */

        String[] cols = new String[]{"IDVANO", "IDOGGETTO", "CODICE", "DESCRIZIONE"};


        String rawQuery = "SELECT " +
                "OGGETTO.IDVANO,OGGETTO.IDOGGETTO,OGGETTO.CODICE,OGGETTO.DESCRIZIONE,OGGETTO.PRODUTTORE" +
                ",INTERVENTI.IDINTERVENTO AS INTERVENTI_IDINTERVENTO" +
                " FROM OGGETTO";

        rawQuery += " LEFT JOIN INTERVENTI ON OGGETTO.IDOGGETTO = INTERVENTI.IDOGGETTO ";

        int nFilter = 0;
        if (IDVano > 0) {
            if (nFilter==0) rawQuery += " WHERE ("; else rawQuery += " AND ";
            nFilter++;
            rawQuery += "IDVANO='" + IDVano + "'";
        }
        if (IDPiano > 0) {
            if (nFilter==0) rawQuery += " WHERE ("; else rawQuery += " AND ";
            nFilter++;
            rawQuery += "IDPIANO='" + IDPiano + "'";
        }
        if (IDEdificio > 0) {
            if (nFilter==0) rawQuery += " WHERE (";  else rawQuery += " AND ";
            nFilter++;
            rawQuery += "IDEDIFICIO='" + IDEdificio + "'";
        }
        if (IDComplesso > 0) {
            if (nFilter==0) rawQuery += " WHERE (";
            nFilter++;
            rawQuery += "IDCOMPLESSO='" + IDComplesso + "'";
        }

        if (nFilter>0) rawQuery += ")";

        rawQuery += " GROUP BY OGGETTO.IDOGGETTO";


        Cursor mCursor = null;

        try {


            mCursor = mainActivity.sqliteWrapper.db.rawQuery(rawQuery, null);

            // mCursor = mainActivity.sqliteWrapper.db.query(false, "OGGETTO", cols, whereClause, null, null, null, null, null);

            if (mCursor != null) {

                if (mCursor.moveToFirst()) {

                    // System.err.println("Read DB.OGGETTO [IDOGGETTO,CODICE,DESCRIZIONE]...");

                    do {

                        // System.err.println("[" + mCursor.getString(0) + "," + mCursor.getString(1) + "," + mCursor.getString(2) + "]");

                        try {

                            String IDOggetto = mCursor.getString(mCursor.getColumnIndex("IDOGGETTO")) != null ? mCursor.getString(mCursor.getColumnIndex("IDOGGETTO")) : "";
                            pAppOggetti.IDOggetti.add(Integer.parseInt(IDOggetto));




                            try {

                                String CodOggetto = null;
                                String DescOggetto = null;
                                String ExtDescOggetti = null;
                                String OggettoIDVano = null;

                                try {
                                    CodOggetto = mCursor.getString(mCursor.getColumnIndex("CODICE")) != null ? mCursor.getString(mCursor.getColumnIndex("CODICE")) : "";
                                    pAppOggetti.CodOggetti.add(CodOggetto);

                                } catch (Exception e) {
                                }

                                try {
                                    DescOggetto = mCursor.getString(mCursor.getColumnIndex("DESCRIZIONE")) != null ? mCursor.getString(mCursor.getColumnIndex("DESCRIZIONE")) : "";
                                    pAppOggetti.DescOggetti.add(DescOggetto);
                                } catch (Exception e) {
                                }

                                try {
                                    ExtDescOggetti = mCursor.getString(mCursor.getColumnIndex("PRODUTTORE")) != null ? mCursor.getString(mCursor.getColumnIndex("PRODUTTORE")) : "";
                                    pAppOggetti.ExtDescOggetti.add(ExtDescOggetti);
                                } catch (Exception e) {
                                }


                                try {
                                    OggettoIDVano = mCursor.getString(mCursor.getColumnIndex("IDVANO")) != null ? mCursor.getString(mCursor.getColumnIndex("IDVANO")) : "";
                                    pAppOggetti.IDVanno.add(Integer.parseInt(OggettoIDVano));
                                } catch (Exception e) {
                                }

                                pAppOggetti.NumOggetti++;

                                /////////////////////////////////
                                // Presenza interventi
                                //
                                if (pAppOggettiIfIntervento != null) {
                                    try {
                                        int colIndex = mCursor.getColumnIndex("INTERVENTI_IDINTERVENTO");
                                        if (colIndex>= 0) {
                                            String IDIntervento = mCursor.getString(colIndex) != null ? mCursor.getString(colIndex) : "";
                                            if (IDIntervento != null && !IDIntervento.isEmpty()) {
                                                pAppOggettiIfIntervento.IDOggetti.add(Integer.parseInt(IDOggetto));
                                                pAppOggettiIfIntervento.CodOggetti.add(CodOggetto);
                                                pAppOggettiIfIntervento.DescOggetti.add(DescOggetto);
                                                pAppOggettiIfIntervento.ExtDescOggetti.add(ExtDescOggetti);
                                                pAppOggettiIfIntervento.NumOggetti++;
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
                                }

                            } catch (Exception e) {
                            }

                        } catch (NumberFormatException e) {
                        }

                    } while (mCursor.moveToNext());
                }
            }

        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

        if (mCursor != null) {
            mCursor.close();
        }



        if (Constants.ADD_OGGETTI_AS_DEBUG) {
            pAppOggetti.IDOggetti.add(1001);
            pAppOggetti.CodOggetti.add("EM47");
            pAppOggetti.DescOggetti.add("Desc Oggetto 1");
            pAppOggetti.ExtDescOggetti.add("ExtDescOggetti");
            pAppOggetti.NumOggetti++;

            pAppOggetti.IDOggetti.add(1002);
            pAppOggetti.CodOggetti.add("EM47");
            pAppOggetti.DescOggetti.add("Desc Oggetto 2");
            pAppOggetti.ExtDescOggetti.add("ExtDescOggett2");
            pAppOggetti.NumOggetti++;

            pAppOggetti.IDOggetti.add(101);
            pAppOggetti.CodOggetti.add("EM48");
            pAppOggetti.DescOggetti.add("DescOggetto2");
            pAppOggetti.ExtDescOggetti.add("ExtDescOggett2");
            pAppOggetti.NumOggetti++;

            pAppOggetti.IDOggetti.add(102);
            pAppOggetti.CodOggetti.add("EM49");
            pAppOggetti.DescOggetti.add("DescOggetto2");
            pAppOggetti.ExtDescOggetti.add("ExtDescOggett2");
            pAppOggetti.NumOggetti++;
        }

        return 1;
    }





    public static String leggi_oggetto(Integer IDOggetto, String Label, String OutFields ) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();


        if (IDOggetto < 0) return null;


        ContentValues values = new ContentValues();

        String result = null;

        String[] cols = new String[]{ Label };



        String whereClause = null;
        if (IDOggetto > 0) whereClause = "IDOGGETTO='" + IDOggetto + "'";


        Cursor mCursor = null;

        try {


            mCursor = mainActivity.sqliteWrapper.db.query(false, "OGGETTO", cols, whereClause, null, null, null, null, null);

            if (mCursor != null) {

                if (mCursor.moveToFirst()) {


                        try {

                            result = mCursor.getString(mCursor.getColumnIndex(Label)) != null ? mCursor.getString(mCursor.getColumnIndex(Label)) : "";

                            OutFields = result;

                        } catch (NumberFormatException e) {
                        }

                    } while (mCursor.moveToNext());
                }

        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

        if (mCursor != null) {
            mCursor.close();
        }

    return result;
    }









    /////////////////////////////////
    // Legge gli oggetti dal server
    //
    public static int sincronizza_oggetti ( String IDComplesso, String IDEdificio, String IDPiano, String IDVano,
                                            Context context, Activity activity,
                                            SYNCTable syncTable
    ) {

        MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();
        int res = 0;

        /*
        # Servizio get oggetti (i campi riportati sono un sottoinsieme per una configurazione di test in attesa di definire quali campi mostrare effettivamente)
        # metodo : POST
        # path : /oggetti-service/oggetti
        # esempio chiamata
        http://pamap.geisoft.org/services/pamap/oggetti-service/oggetti?date_from=2015-04-01&pk_parent=&size=&idpiano=4&idsessione=1a5e62e6-ea64-4004-a281-bd7e0797e9fd&idedificio=&idvano=&start=&date_to=2015-04-25&idunita=&idcomplesso=&cd=&pk=
        # esempio risposta
        {"codEsito":"S","data":[{"descrizione":"Primo oggetto di test padre","codice":"01","idoggetto":"12"}]}
        */

        String serviceURL = APPData.getServiceURL("oggetti-service/oggetti", APPData.bEncript);
        String[] labelsParam = new String[]{"idcomplesso", "idedificio", "idpiano", "idvano"};
        String[] valuesParam = new String[]{IDComplesso!=null?IDComplesso:"",IDEdificio!=null?IDEdificio:"",IDPiano!=null?IDPiano:"", IDVano!=null?IDVano:""};
        String[] resultTags = new String[]{"codEsito"};
        String fieldsTags = "data";


        if (NetworkActivity.isOnline() > 0) {
            JSONParser jsonParser = new JSONParser(null);

            res = jsonParser.ParseURL(serviceURL, labelsParam, valuesParam, resultTags, null, fieldsTags, null, null, Constants.ENCRYPT, Constants.DECRYPT, true);
            if (res > 0) {
                // Store into SQLite...

                // Cancellazione records esistenti
                int nFilter = 0;
                String deleteSQL = "DELETE FROM OGGETTO WHERE (";
                if (IDComplesso != null && !IDComplesso.isEmpty()) {
                    deleteSQL += "IDCOMPLESSO="+IDComplesso;
                    nFilter++;
                }
                if (IDEdificio != null && !IDEdificio.isEmpty()) {
                    if (nFilter>0) deleteSQL += " AND ";
                    deleteSQL += "IDEDIFICIO="+IDEdificio;
                    nFilter++;
                }
                if (IDPiano != null && !IDPiano.isEmpty()) {
                    if (nFilter>0) deleteSQL += " AND ";
                    deleteSQL += "IDPIANO="+IDPiano;
                    nFilter++;
                }
                if (IDVano != null && !IDVano.isEmpty()) {
                    if (nFilter>0) deleteSQL += " AND ";
                    deleteSQL += "IDVANO="+IDVano;
                    nFilter++;
                }
                if (nFilter==0) {
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
                Integer idOggettoError = 0, ExceptionOggettoError = 0;

                for (int iRec=0; iRec<jsonParser.nRecs; iRec++) {

                    /*CREATE TABLE IF NOT EXISTS OGGETTI (IDOGGETTO TEXT,IDOGGETTO_P TEXT,CODICE TEXT,DESCRIZIONE TEXT," +
                            "STATO TEXT, TIPO_DISPONIBILITA TEXT, SERIALNUMBER TEXT, PRODUTTORE TEXT, IDFORNITORE TEXT, IDARTICOLO TEXT, CDMODELLO TEXT," +
                            "IDCOMPLESSO TEXT, IDEDIFICIO TEXT, IDPIANO TEXT, IDVANO TEXT, " +
                            "IDUNITA TEXT, IDAREA TEXT, IDCATEGORIA TEXT, IDGRUPPO TEXT" */

                    int colIndex = jsonParser.Labels.indexOf("idoggetto");
                    if (colIndex > 0) {

                        try {

                            String Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                            values.put("IDOGGETTO", Field);

                            colIndex = jsonParser.Labels.indexOf("codice");
                            if (colIndex > 0) {
                                Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                values.put("CODICE", Field);

                                if (Field.compareToIgnoreCase("PL09")==0) {
                                    int b = 1;
                                    if (b == 1) {
                                    }
                                }

                                colIndex = jsonParser.Labels.indexOf("idoggetto_p");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDOGGETTO_P", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("idcomplesso");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDCOMPLESSO", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("idedificio");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDEDIFICIO", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("idpiano");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDPIANO", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("idvano");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDVANO", Field);
                                }



                                colIndex = jsonParser.Labels.indexOf("idunita");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDUNITA", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("idarea");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDAREA", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("idcategoria");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDCATEGORIA", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("idgruppo");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDGRUPPO", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("idfornitore");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDFORNITORE", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("idarticolo");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDARTICOLO", Field);
                                }



                                colIndex = jsonParser.Labels.indexOf("descrizione");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("DESCRIZIONE", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("tipo_disponibilita");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("TIPO_DISPONIBILITA", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("serialnumber");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("SERIALNUMBER", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("stato");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("STATO", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("produttore");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("PRODUTTORE", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("cdmodello");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("CDMODELLO", Field);
                                }


                                colIndex = jsonParser.Labels.indexOf("cdcondizione");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("CDCONDIZIONE", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("cdcondizione");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("CDCONDIZIONE", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("dtcontrollo");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("DTCONTROLLO", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("codgaranzia");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("CODGARANZIA", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("tipo_garanzia");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("TIPO_GARANZIA", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("dt_inizio_gar");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("DT_INIZIO_GAR", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("dt_fine_gar");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("DT_FINE_GAR", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("dt_installazione");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("DT_INSTALLAZIONE", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("dt_collaudo_gar");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("DT_COLLAUDO_GAR", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("installatore");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("INSTALLATORE", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("idimpianto");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDIMPIANTO", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("t_altezza");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("T_ALTEZZA", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("t_larghezza");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("T_LARGHEZZA", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("t_profondita");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("T_PROFONDITA", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("t_peso");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("T_PESO", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("t_colore");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                }
                                values.put("T_COLORE", Field);

                                colIndex = jsonParser.Labels.indexOf("t_consumo_elett");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                }
                                values.put("T_CONSUMO_ELETT", Field);

                                colIndex = jsonParser.Labels.indexOf("t_voltaggio");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                }
                                values.put("T_VOLTAGGIO", Field);

                                colIndex = jsonParser.Labels.indexOf("t_watt_equival");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                }
                                values.put("T_WATT_EQUIVAL", Field);

                                colIndex = jsonParser.Labels.indexOf("t_pos_altezza");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                }
                                values.put("T_POS_ALTEZZA", Field);

                                colIndex = jsonParser.Labels.indexOf("t_nr_oggetti");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                }
                                values.put("T_NR_OGGETTI", Field);

                                colIndex = jsonParser.Labels.indexOf("note");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                }
                                values.put("NOTE", Field);

                                colIndex = jsonParser.Labels.indexOf("data_ins");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                }
                                values.put("DATA_INS", Field);

                                colIndex = jsonParser.Labels.indexOf("data_agg");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                }
                                values.put("DATA_AGG", Field);

                                colIndex = jsonParser.Labels.indexOf("utente_ins");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                }
                                values.put("UTENTE_INS", Field);

                                colIndex = jsonParser.Labels.indexOf("utente_agg");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                }
                                values.put("UTENTE_AGG", Field);

                                colIndex = jsonParser.Labels.indexOf("ente_ins");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                }
                                values.put("ENTE_INS", Field);

                                colIndex = jsonParser.Labels.indexOf("ente_agg");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                }
                                values.put("ENTE_AGG", Field);





                                mainActivity.sqliteWrapper.db.insert("OGGETTO","",values);
                            }

                        } catch (Exception e) {
                            ExceptionOggettoError++;
                        }
                    } else {
                    idOggettoError++;
                    }
                }



                APPData.LAST_READ_OGGETTO = APPUtil.get_date_time();
                mainActivity.sqliteWrapper.update_setup_record("LastReadOggetto", APPData.LAST_READ_OGGETTO);

                if (idOggettoError > 0 || ExceptionOggettoError > 0) {
                    if (DialogBox.DialogBox("ATTENZIONE", jsonParser.nRecs + " righe importate :\r\n\r\n"+idOggettoError+" Errori negli ID\r\n"+ExceptionOggettoError+" Errori di Eccezzione", 0 + 0, activity)) {
                    } else {
                        DialogBox.DialogBox("ATTENZIONE", jsonParser.nRecs + " righe importate", 0 + 0, activity);
                    }
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

        } else {
        }

        return res;
    }





    ///////////////////////////////////////////////////
    // Legge Etichette deli oggetti dal server
    //
    public static int leggi_etichette_oggetti ( Context context, Activity activity ) {

        MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();
        int res = 0;

        /*
        # Servizio get etichette oggetti
        # metodo : POST
        # path : /oggetti-service/get-label
        # esempio chiamata
        http://pamap.geisoft.org/services/pamap/oggetti-service/get-label?idsessione=1a5e62e6-ea64-4004-a281-bd7e0797e9fd&type_lookup=OGGETTI_QUERY
        # esempio risposta
        {"codEsito":"S","labels":{"descrizione":"Descrizione","codice":"Codice","idoggetto":"idoggetto","idoggetto_p":"idoggetto_p"}}
        "{\"codEsito\":\"S\",\"labels\":{\"produttore\":\"Produttore\",\"idimpianto\":\"Idimpianto\",\"cdfornitoreProduttore\":\"Cdfornitore Produttore\",\"dtInizioGar\":\"Dt Inizio Gar\",\"utenteIns\":\"Utente Ins\",\"utenteAgg\":\"Utente Agg\",\"nomeInstallatore\":\"Nome Installatore\",\"TAltezza\":\"T Altezza\",\"codice\":\"Codice\",\"cdfornitoreFornitore\":\"Cdfornitore Fornitore\",\"idarticolo\":\"Idarticolo\",\"TColore\":\"T Colore\",\"dtcontrollo\":\"Dtcontrollo\",\"descrizioneOggettoP\":\"Descrizione OggettoP\",\"dtInstallazione\":\"Dt Installazione\",\"dataIns\":\"Data Ins\",\"cdpersonaInstallatore\":\"Cdpersona Installatore\",\"cdcondizione\":\"Cdcondizione\",\"codiceOggettoP\":\"Codice OggettoP\",\"idfornitore\":\"Idfornitore\",\"TPeso\":\"T Peso\",\"cdarticolo\":\"Cdarticolo\",\"TWattEquival\":\"T Watt Equival\",\"TProfondita\":\"T Profondita\",\"TVoltaggio\":\"T Voltaggio\",\"tipoGaranzia\":\"Tipo Garanzia\",\"cdgruppo\":\"Cdgruppo\",\"dataAgg\":\"Data Agg\",\"cdmodello\":\"Cdmodello\",\"stato\":\"Stato\",\"dsarticolo\":\"Dsarticolo\",\"idarea\":\"Idarea\",\"tipoDisponibilita\":\"Tipo Disponibilita\",\"idgruppo\":\"Idgruppo\",\"enteAgg\":\"Ente Agg\",\"TPosAltezza\":\"T Pos Altezza\",\"serialnumber\":\"Serialnumber\",\"descrizione\":\"Descrizione\",\"idvano\":\"Idvano\",\"cdarea\":\"Cdarea\",\"idoggetto\":\"Idoggetto\",\"installatore\":\"Installatore\",\"dsgruppo\":\"Dsgruppo\",\"cognomeInstallatore\":\"Cognome Installatore\",\"dtFineGar\":\"Dt Fine Gar\",\"dtCollaudoGar\":\"Dt Collaudo Gar\",\"note\":\"Note\",\"dspersonaInstallatore\":\"Dspersona Installatore\",\"ragionesocialeProduttore\":\"Ragionesociale Produttore\",\"dsimpianto\":\"Dsimpianto\",\"idcomplesso\":\"Idcomplesso\",\"TConsumoElett\":\"T Consumo Elett\",\"codgaranzia\":\"Codgaranzia\",\"dsarea\":\"Dsarea\",\"cdcategoria\":\"Cdcategoria\",\"cdimpianto\":\"Cdimpianto\",\"idcategoria\":\"Idcategoria\",\"TNrOggetti\":\"T Nr Oggetti\",\"enteIns\":\"Ente Ins\",\"ragionesocialeFornitore\":\"Ragionesociale Fornitore\",\"idpiano\":\"Idpiano\",\"modelloArticolo\":\"Modello Articolo\",\"dscategoria\":\"Dscategoria\",\"idedificio\":\"Idedificio\",\"idoggettoP\":\"IdoggettoP\",\"idunita\":\"Idunita\",\"TLarghezza\":\"T Larghezza\"},\"msgEsito\":null}"
        */

        String serviceURL = APPData.getServiceURL("oggetti-service/get-label", APPData.bEncript);
        String[] labelsParam = new String[]{"type_lookup"};
        String[] valuesParam = new String[]{"V_OGGETTI"};
        String[] resultTags = new String[]{"codEsito"};
        String fieldsTags = "labels";


        JSONParser jsonParser = new JSONParser(null);

        boolean tryToConnect = true;

        ///////////////////////////
        // Recupero da crash
        //
        if (APPData.LAST_ENV != null && !APPData.LAST_ENV.isEmpty()) {
            // Login offline
                if (APPData.LAST_USER_PROFILE_RESPONSE != null && !APPData.LAST_USER_PROFILE_RESPONSE.isEmpty()) {
                    res = jsonParser.ParseString(APPData.LAST_OJECT_LABELS_RESPONSE, resultTags, null, fieldsTags);
                    if (res > 0) {
                        tryToConnect = false;
                    }
                }
            }

        if (tryToConnect) {
            if (NetworkActivity.isOnline() > 0) {
                res = jsonParser.ParseURL(serviceURL, labelsParam, valuesParam, resultTags, null, fieldsTags, null, null, Constants.ENCRYPT, Constants.DECRYPT, true);
            } else {
                // Login offline
                res = jsonParser.ParseString(APPData.LAST_OJECT_LABELS_RESPONSE, resultTags, null, fieldsTags);
            }
        }



        if (res > 0) {

            APPData.COD_ESITO = jsonParser.Header != null ? jsonParser.Header.get(0) : "";
            if (APPData.COD_ESITO.equals("S")) {

                // Aggiunta lettura dal parser
                ContentValues values = new ContentValues();
                boolean bStoreResponse = false;


                APPData.NumObjectCustomFields = 0;
                if (APPData.ObjectCustomFields == null)
                    APPData.ObjectCustomFields = new ArrayList<String>();

                if (APPData.ObjectCustomLabels == null)
                    APPData.ObjectCustomLabels = new ArrayList<String>();

                if (APPData.ObjectCustomLabelsOnDB == null)
                    APPData.ObjectCustomLabelsOnDB = new ArrayList<String>();


                APPData.ObjectCustomFields.clear();
                APPData.ObjectCustomLabels.clear();
                APPData.ObjectCustomLabelsOnDB.clear();


                for (int iField = 0; iField < jsonParser.nCols; iField++) {
                    String Label = null, Field = null;

                    if (jsonParser.nRecs >= 2) {
                        try {
                            Label = jsonParser.Recs.get(iField);
                            Field = jsonParser.Recs.get(iField + jsonParser.nCols);
                        } catch (Exception e) {
                        }
                    }

                    if (Field != null && !Field.isEmpty() &&
                            Label != null && !Label.isEmpty()) {
                        APPData.ObjectCustomFields.add("");
                        APPData.ObjectCustomLabels.add(Field);
                        APPData.ObjectCustomLabelsOnDB.add(Label);

                        APPData.NumObjectCustomFields++;
                        bStoreResponse = true;


                        if (APPData.NumObjectCustomFields >= APPData.MAX_OBECT_CUSTOM_FIELDS) break;
                    }
                    // mainActivity.sqliteWrapper.db.insert("OGGETTO","",values);
                }

                // Store last response
                if (bStoreResponse) {
                    if (jsonParser.rawHttpContent != null) {
                        APPData.LAST_OJECT_LABELS_RESPONSE = jsonParser.rawHttpContent;
                        mainActivity.sqliteWrapper.update_setup_record("LastCustomLabelsResponse", APPData.LAST_OJECT_LABELS_RESPONSE);
                    }
                }
            }
        }

        return res;
    }






    ///////////////////////////////////////////////////
    // Legge Etichette deli oggetti dal server
    //
    public static int valorizza_etichette_oggetti ( int IDOggetto, Context context, Activity activity ) {

        MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();
        int res = 0;


        if (IDOggetto <= 0) return 0;



        String[] cols = new String[] {
                "IDOGGETTO", "IDOGGETTO_P", "CODICE","DESCRIZIONE"
                ,"STATO", "TIPO_DISPONIBILITA", "SERIALNUMBER", "PRODUTTORE", "IDFORNITORE", "IDARTICOLO", "CDMODELLO"
                ,"IDCOMPLESSO", "IDEDIFICIO", "IDPIANO", "IDVANO", "IDUNITA", "IDAREA", "IDCATEGORIA", "IDGRUPPO"
                ,"CDCONDIZIONE" ,"DTCONTROLLO","CODGARANZIA","TIPO_GARANZIA","DT_INIZIO_GAR","DT_FINE_GAR","DT_INSTALLAZIONE"
                ,"DT_COLLAUDO_GAR","INSTALLATORE","IDIMPIANTO","T_ALTEZZA","T_LARGHEZZA","T_PROFONDITA","T_PESO","T_COLORE"
                ,"T_CONSUMO_ELETT","T_VOLTAGGIO","T_WATT_EQUIVAL","T_POS_ALTEZZA","T_NR_OGGETTI","NOTE","DATA_INS","DATA_AGG",
                "UTENTE_INS","UTENTE_AGG","ENTE_INS","ENTE_AGG"
        };

        String whereClause = null;
        if (IDOggetto > 0) whereClause = "IDOGGETTO='"+IDOggetto+"'";

        Cursor mCursor = null;

        try {

            mCursor = mainActivity.sqliteWrapper.db.query(false, "OGGETTO", cols, whereClause, null, null, null, null, null);

            if (mCursor != null) {

                if (mCursor.moveToFirst()) {

                    // System.err.println("Read DB.OGGETTO [IDOGGETTO,CODICE,DESCRIZIONE]...");

                    do {

                        // System.err.println("[" + mCursor.getString(0) + "," + mCursor.getString(1) + "," + mCursor.getString(2) + "]");

                        try {

                            String idOggetto = mCursor.getString(mCursor.getColumnIndex("IDOGGETTO")) != null ? mCursor.getString(mCursor.getColumnIndex("IDOGGETTO")) : "";

                            if (idOggetto != null && !idOggetto.isEmpty()) {
                                for (int iCol=0; iCol<APPData.NumObjectCustomFields; iCol++) {
                                    String label = APPData.ObjectCustomLabelsOnDB.get(iCol);
                                    if (!label.isEmpty()) {
                                        int colIndex = mCursor.getColumnIndex(label.toUpperCase());
                                        if (colIndex >= 0) {
                                            String field = mCursor.getString(colIndex);
                                            APPData.ObjectCustomFields.set(iCol, field);
                                        } else {
                                            // APPData.ObjectCustomFields.set(iCol, "[Label "+label+"not found]");
                                            APPData.ObjectCustomFields.set(iCol, "");
                                            APPData.ObjectCustomLabelsOnDB.set(iCol, "");
                                        }
                                    } else {
                                        APPData.ObjectCustomFields.set(iCol, "");
                                        APPData.ObjectCustomLabelsOnDB.set(iCol, "");
                                    }
                                }


                                for (int iCol=0; iCol<APPData.NumObjectCustomFields; iCol++) {
                                    try {
                                        if (APPData.ObjectCustomFields.get(iCol).isEmpty()) {
                                            APPData.ObjectCustomLabelsOnDB.remove(iCol);
                                            APPData.ObjectCustomLabels.remove(iCol);
                                            APPData.ObjectCustomFields.remove(iCol);
                                            iCol--;
                                            APPData.NumObjectCustomFields--;
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            }

                        } catch (NumberFormatException e) {
                        }

                    } while (mCursor.moveToNext());
                }
            }

        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

        if (mCursor != null) {
            mCursor.close();
        }

        return res;
    }

}
