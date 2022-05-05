package com.imuve.cristian.imuve;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by Cristian on 23/04/2015.
 */
public class APPInterventiSQL {

    public APPInterventiSQL(Context context) {

    }


    public static int nuovo_intervento (
            String localID, String queueID,
            String Cod, String Desc, String ExtendedDesc, String StatoIntervento, String DataApertura,
            String Complesso, String Edificio, String Piano, String Vano,
            String ObjectID, String ObjectCode ) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();



        String sqlStatement = null;
        String newID = null;
        boolean bUpdatePoolKey = false;



        if (APPData.nuovoIntervento) {

            newID = APPData.DEVICE_PKPOOL + String.valueOf(APPData.InterventiNID++);

            bUpdatePoolKey = true;

            int res = mainActivity.sqliteWrapper.update_setup_record( "InterventiNID", String.valueOf(APPData.InterventiNID));

            // Constants.IntervemtiNID = APPData.InterventiNID;
            // APPPRefs.save_shared_app_prefs(null);




            /*
            sqlStatement = "INSERT INTO queue (Operation,Status,TableName,LocalID,ExtID,Field1,Field2,Field3,Field4,Field5,Field6,Field7) VALUES (" +
                    "'INSERT'" + "," + "''" + "," + "'Interventi'" + "," + newID + ",'" + newID + "','" + Cod + "','" + Desc + "','" +
                    Complesso + "','" + Edificio + "','" + Piano + "','" + Vano + "','" + ObjectID +
                    "' )";
                    */

            ContentValues values = new ContentValues();
            values.put("Operation", "INSERT");
            values.put("TableName", "Interventi");
            values.put("LocalID", newID);
            values.put("ExtID", newID);
            values.put("Field1", Cod);



            try {
                queueID = String.valueOf(mainActivity.sqliteWrapper.db.insert("queue", "", values));
            } catch (Exception e) {
                System.err.printf(e.getMessage());
            }

            sqlStatement = "INSERT INTO INTERVENTI (IDINTERVENTO,CDINTERVENTO,DSINTERVENTO,DSESTESA,STATO,DATA_APERTURA,IDOGGETTO,IDQUEUE) VALUES (" +
                     "'" + newID + "','" + Cod + "','" + Desc + "','" + ExtendedDesc + "','" + StatoIntervento + "','" + DataApertura + "','" + ObjectID + "','" + queueID + "'" + ")";

            try {
                mainActivity.sqliteWrapper.execute(sqlStatement);
            } catch (Exception e) {
                System.err.printf(e.getMessage());
            }



        } else {

            // Aggiornamento Intervento esistente

            boolean insertIntoQueue = false, insertIntoInterventi = false;


            if (localID != null && localID.isEmpty()) {
                // Alfanumerico
                insertIntoInterventi = true;
            } else {
            }

            if (queueID.isEmpty()) {
                insertIntoQueue = true;
            } else {
                if (Integer.parseInt(queueID) <= 0) {
                    insertIntoQueue = true;
                }
            }



            if (insertIntoQueue) {
                // Inserimento in coda
                /* sqlStatement = "INSERT INTO queue (Operation,Status,TableName,LocalID,ExtID,Field1,Field2,Field3,Field4,Field5,Field6,Field7) VALUES (" +
                        "'UPDATE'" + "," + "''" + "," + "'Interventi'" + ",'" + localID + "','" + localID + "','" + Cod + "','" + Desc + "','" +
                        Complesso + "','" + Edificio + "','" + Piano + "','" + Vano + "','" + ObjectID + "' )";
                        */
                sqlStatement = "INSERT INTO queue (Operation,Status,TableName,LocalID,ExtID) VALUES (" +
                        "'UPDATE'" + "," + "''" + "," + "'Interventi'" + ",'" + localID + "','" + localID + "'" +
                        ")";

                try {
                    mainActivity.sqliteWrapper.execute(sqlStatement);
                } catch (Exception e) {
                    System.err.printf(e.getMessage());
                }

            } else {
                // Aggiornamento coda
                /*
                sqlStatement = "UPDATE queue SET " +
                        "Field1=" + "'" + Cod + "'" +
                        ",Field2=" + "'" + Desc + "'" +
                        ",Field3=" + "'" + Complesso + "'" +
                        ",Field4=" + "'" + Edificio + "'" +
                        ",Field5=" + "'" + Piano + "'" +
                        ",Field6=" + "'" + Vano + "'" +
                        ",Field7=" + "'" + ObjectID + "'" +
                        ")" +
                        "WHERE (id=" + queueID + ")";
            */
            }






            if (insertIntoInterventi) {
            } else {
                if (localID != null && !localID.isEmpty()) {
                    // Aggiornamento tabella Interventi
                    sqlStatement = "UPDATE INTERVENTI SET " +
                            // "CDINTERVENTO=" + "'" + Cod + "'" +
                            "DSINTERVENTO=" + "'" + Desc + "'" +
                            ",DSESTESA=" + "'" + ExtendedDesc + "'" +
                            "" +
                            " WHERE (" +
                            "IDINTERVENTO=" + "'" + localID + "'" +
                            ")";
                    try {
                        mainActivity.sqliteWrapper.execute(sqlStatement);
                    } catch (Exception e) {
                        System.err.printf(e.getMessage());
                    }
                } else {
                    // Modifica di un record non esistente ?!
                }
            }
        }



        // Aggiornamento contatore nel server
        if (bUpdatePoolKey) {
            // APPPkPool.scrivi_pk_counter();
        }

        // Dump tabella
        // ...

        return 1;
    }







    public static int cancella_intervento ( String localID, String queueID ) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        // Aggiornamento Intervento esistente


        // Eleminazione dalla coda
        if (queueID != null && !queueID.isEmpty()) {
            String sqlStatement = "DELETE FROM queue WHERE (id=" + queueID + ")";

            try {
                mainActivity.sqliteWrapper.execute(sqlStatement);
            } catch (Exception e) {
                System.err.printf(e.getMessage());
            }
        }

        if (localID != null && !localID.isEmpty()) {
            String sqlStatement = "DELETE FROM INTERVENTI WHERE (IDINTERVENTO='" + localID + "')";

            try {
                mainActivity.sqliteWrapper.execute(sqlStatement);
            } catch (Exception e) {
                System.err.printf(e.getMessage());
            }
        }


        // Dump tabella
        // ...

        return 1;
    }


    //////////////////////////////////////////////////
    // Legge gli interventi locali (Tabella queue)
    //

    public static int leggi_interventi ( String __developme_Complesso, String __developme_Edificio, String IDPiano, String __developme_Vano, Integer ObjectID, APPInterventi pAppInterventi ) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();


        try {

            pAppInterventi.setup();


            if (ObjectID != null) {
                if (ObjectID < 0) return 0;
            }


            /*db.execSQL("CREATE TABLE IF NOT EXISTS INTERVENTI (IDINTERVENTO integer,CATEGORIA TEXT,IDATTMANU TEXT,CDINTERVENTO TEXT," +
                    "STATO TEXT,DSINTERVENTO TEXT,CANALE TEXT,DSESTESA " +
                    "TEXT,DATA_APERTURA TEXT,DATA_SCADENZA TEXT,DATA_CHIUSURA TEXT," +
                    "RICHIEDENTE TEXT,OPERATORE_HELP_DESK TEXT,GRAVITA TEXT,PRIORITA TEXT,INRITARDO TEXT,ESITO TEXT,ISTRUZIONI TEXT,COMMENTI TEXT," +
                    "IDGRUPPO TEXT,IDOGGETTO TEXT,IDPERSONA TEXT,IDSQUADRA TEXT,DOSSIER" +
                    */

            /*
            String[] cols = new String[] { "IDINTERVENTO","CDINTERVENTO","DSINTERVENTO","DSESTESA","IDOGGETTO" };
            String whereClause = null;
            if (ObjectID!=null) whereClause = "IDOGGETTO='"+ObjectID+"'";
            */
            String rawQuesry = "SELECT queue.id" +
                    ",OGGETTO.CODICE AS OGGETTO_CODICE" +
                    ",OGGETTO.DESCRIZIONE AS OGGETTO_DESC" +
                    ",OGGETTO.IDCOMPLESSO AS OGGETTO_IDCOMPLESSO" +
                    ",OGGETTO.IDEDIFICIO AS OGGETTO_IDEDIFICIO" +
                    ",OGGETTO.IDPIANO AS OGGETTO_IDPIANO" +
                    ",OGGETTO.IDVANO AS OGGETTO_IDVANO" +
                    ",EDIFICIO.DSEDIFICIO AS OGGETTO_EDIFICIO" +
                    ",PIANO.DSPIANO AS OGGETTO_PIANO" +
                    ",VANO.DSVANO AS OGGETTO_VANO" +
                    ",INTERVENTI.IDINTERVENTO,INTERVENTI.CDINTERVENTO,INTERVENTI.DSINTERVENTO,INTERVENTI.DSESTESA,INTERVENTI.IDOGGETTO AS INTERVENTI_IDOGGETTO" +
                    ",INTERVENTI.DATA_APERTURA,INTERVENTI.DATA_SCADENZA,INTERVENTI.DATA_CHIUSURA" +
                    ",INTERVENTI.STATO" +
                    " FROM INTERVENTI";

            rawQuesry += " LEFT JOIN queue ON INTERVENTI.IDINTERVENTO = queue.LocalID ";
            rawQuesry += " LEFT JOIN OGGETTO ON INTERVENTI.IDOGGETTO = OGGETTO.IDOGGETTO ";
            rawQuesry += " LEFT JOIN VANO ON OGGETTO.IDVANO = VANO.IDVANO";
            rawQuesry += " LEFT JOIN PIANO ON OGGETTO.IDPIANO = PIANO.IDPIANO";
            rawQuesry += " LEFT JOIN EDIFICIO ON OGGETTO.IDEDIFICIO = EDIFICIO.IDEDIFICIO";

            int whereFilter = 0;

            if (ObjectID != null) {
                if (ObjectID > 0) {
                    if (whereFilter == 0) rawQuesry += " WHERE (";
                    rawQuesry += "INTERVENTI.IDOGGETTO='" + ObjectID + "'";
                    whereFilter++;
                }
            }

            if (IDPiano != null) {
                if (whereFilter == 0) rawQuesry += " WHERE (";
                rawQuesry += "OGGETTO.IDPIANO='" + IDPiano + "'";
                whereFilter++;
            }

            if (whereFilter > 0) rawQuesry += ")";


            Cursor mCursor = null;

            try {

                // mCursor = mainActivity.sqliteWrapper.db.query(false, "INTERVENTI", cols, whereClause, null, null, null, null, null);
                mCursor = mainActivity.sqliteWrapper.db.rawQuery(rawQuesry, null);

                if (mCursor != null) {

                    if (mCursor.moveToFirst()) {
                        String queueID = null;

                        do {

                            try {
                                queueID = mCursor.getString(mCursor.getColumnIndex("id"));
                                if (queueID != null)
                                    pAppInterventi.IDInterventiOnQueue.add(Integer.parseInt(queueID));
                                else pAppInterventi.IDInterventiOnQueue.add(0);
                            } catch (NumberFormatException e) {
                                pAppInterventi.IDInterventiOnQueue.add(0);
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("IDINTERVENTO")) != null ? mCursor.getString(mCursor.getColumnIndex("IDINTERVENTO")) : "";
                                pAppInterventi.IDInterventi.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.IDInterventi.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("CDINTERVENTO")) != null ? mCursor.getString(mCursor.getColumnIndex("CDINTERVENTO")) : "";
                                if (queueID != null && !queueID.isEmpty()) {
                                    // str+=" [*]";
                                }
                                pAppInterventi.CodInterventi.add(str);

                            } catch (NumberFormatException e) {
                                pAppInterventi.CodInterventi.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("DSINTERVENTO")) != null ? mCursor.getString(mCursor.getColumnIndex("DSINTERVENTO")) : "";
                                pAppInterventi.DescInterventi.add(str);
                                if (queueID != null && Integer.parseInt(queueID) > 0) {
                                    pAppInterventi.DescInterventiAux.add("[+] " + str);
                                } else {
                                    pAppInterventi.DescInterventiAux.add(str);
                                }
                            } catch (NumberFormatException e) {
                                pAppInterventi.DescInterventi.add("");
                                pAppInterventi.DescInterventiAux.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("DSESTESA")) != null ? mCursor.getString(mCursor.getColumnIndex("DSESTESA")) : "";
                                pAppInterventi.ExtDescInterventi.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.ExtDescInterventi.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("DATA_APERTURA")) != null ? mCursor.getString(mCursor.getColumnIndex("DATA_APERTURA")) : "";
                                if (str.compareToIgnoreCase("null") == 0) str = "";
                                pAppInterventi.DataAperturaIntervento.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.DataAperturaIntervento.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("DATA_CHIUSURA")) != null ? mCursor.getString(mCursor.getColumnIndex("DATA_CHIUSURA")) : "";
                                if (str.compareToIgnoreCase("null") == 0) str = "";
                                pAppInterventi.DataChiusuraIntervento.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.DataChiusuraIntervento.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("DATA_SCADENZA")) != null ? mCursor.getString(mCursor.getColumnIndex("DATA_SCADENZA")) : "";
                                if (str.compareToIgnoreCase("null") == 0) str = "";
                                pAppInterventi.DataScadenzaIntervento.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.DataScadenzaIntervento.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("STATO")) != null ? mCursor.getString(mCursor.getColumnIndex("STATO")) : "";
                                pAppInterventi.StatoIntervento.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.StatoIntervento.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("INTERVENTI_IDOGGETTO")) != null ? mCursor.getString(mCursor.getColumnIndex("INTERVENTI_IDOGGETTO")) : "";
                                pAppInterventi.IDOggettoInterventi.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.IDOggettoInterventi.add("");
                            }


                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("OGGETTO_CODICE")) != null ? mCursor.getString(mCursor.getColumnIndex("OGGETTO_CODICE")) : "";
                                pAppInterventi.CodOggettoInterventi.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.CodOggettoInterventi.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("OGGETTO_DESC")) != null ? mCursor.getString(mCursor.getColumnIndex("OGGETTO_DESC")) : "";
                                pAppInterventi.DescOggettoInterventi.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.DescOggettoInterventi.add("");
                            }


                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("OGGETTO_EDIFICIO")) != null ? mCursor.getString(mCursor.getColumnIndex("OGGETTO_EDIFICIO")) : "";
                                pAppInterventi.EdificioInterventi.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.EdificioInterventi.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("OGGETTO_PIANO")) != null ? mCursor.getString(mCursor.getColumnIndex("OGGETTO_PIANO")) : "";
                                pAppInterventi.PianoInterventi.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.PianoInterventi.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("OGGETTO_VANO")) != null ? mCursor.getString(mCursor.getColumnIndex("OGGETTO_VANO")) : "";
                                pAppInterventi.VanoInterventi.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.VanoInterventi.add("");
                            }


                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("OGGETTO_IDVANO")) != null ? mCursor.getString(mCursor.getColumnIndex("OGGETTO_IDVANO")) : "";
                                pAppInterventi.IDVanoInterventi.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.IDVanoInterventi.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("OGGETTO_IDPIANO")) != null ? mCursor.getString(mCursor.getColumnIndex("OGGETTO_IDPIANO")) : "";
                                pAppInterventi.IDPainoInterventi.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.IDPainoInterventi.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("OGGETTO_IDEDIFICIO")) != null ? mCursor.getString(mCursor.getColumnIndex("OGGETTO_IDEDIFICIO")) : "";
                                pAppInterventi.IDEdificioInterventi.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.IDEdificioInterventi.add("");
                            }

                            try {
                                String str = mCursor.getString(mCursor.getColumnIndex("OGGETTO_IDCOMPLESSO")) != null ? mCursor.getString(mCursor.getColumnIndex("OGGETTO_IDCOMPLESSO")) : "";
                                pAppInterventi.IDComplessoInterventi.add(str);
                            } catch (NumberFormatException e) {
                                pAppInterventi.IDComplessoInterventi.add("");
                            }

                            pAppInterventi.NumInterventi++;

                        } while (mCursor.moveToNext());
                    }
                }

            } catch (Exception e) {
                System.err.print(e.getMessage());
            }

            if (mCursor != null) {
                mCursor.close();
            }

            return 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }




    /////////////////////////////////////
    // Legge gli interventi dal server
    //
    public static int sincronizza_interventi ( String fltIDComplesso, String fltIDEdificio, String fltIDPiano, String fltIDVano, String fltIDOggetto,
                                               Context context, Activity activity,
                                               SYNCTable syncTable
    ) {

        MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();
        int res = 0;

        /*
        # Servizio get interventi
        # metodo : POST
        # path : /oggetti-service/interventi
        # esempio chiamata

        http://pamap.geisoft.org/services/pamap/oggetti-service/interventi?idsessione=1a5e62e6-ea64-4004-a281-bd7e0797e9fd&date_from=&start=1&date_to=&size=5&pk=&cd=&idpiano=&idedificio=&idvano=&idunita=&idcomplesso=

        http://pamap.geisoft.org/services/pamap/oggetti-service/interventi?idsessione=1a5e62e6-ea64-4004-a281-bd7e0797e9fd&date_from=&start=1&date_to=&size=5&pk=&cd=&idpiano=&idedificio=&idvano=&idunita=&idcomplesso=
        # esempio risposta
        {"codEsito":"S","data":[{"gravita":"2","idintervento":"1890","stato":"O","dsintervento":"Squadra: Squadra di test, gruppo: ee","idgruppo":"1","cdsquadra":"01","dsoperativaAttmanu":"test","dssquadra":"Squadra di test","categoria":"O","canale":"I","dsattmanu":"test1","cdintervento":"16/04/2015","dataApertura":"2015-04-16 00:00:00.0","idattmanu":"5","dsgruppo":"ee","idsquadra":"2","cdgruppo":"32","priorita":"2"},{"gravita":"2","idintervento":"1891","stato":"O","dsintervento":"Squadra: Squadra di test, gruppo: ee","idgruppo":"1","cdsquadra":"01","dsoperativaAttmanu":"test","dssquadra":"Squadra di test","categoria":"O","canale":"I","dsattmanu":"test1","cdintervento":"16/07/2015","dataApertura":"2015-07-16 00:00:00.0","idattmanu":"5","dsgruppo":"ee","idsquadra":"2","cdgruppo":"32","priorita":"2"},{"gravita":"2","idintervento":"1892","stato":"O","dsintervento":"Squadra: Squadra di test, gruppo: ee","idgruppo":"1","cdsquadra":"01","dsoperativaAttmanu":"test","dssquadra":"Squadra di test","categoria":"O","canale":"I","dsattmanu":"test1","cdintervento":"15/10/2015","dataApertura":"2015-10-15 00:00:00.0","idattmanu":"5","dsgruppo":"ee","idsquadra":"2","cdgruppo":"32","priorita":"2"},{"gravita":"2","idintervento":"1893","stato":"O","dsintervento":"Squadra: Squadra di test, gruppo: ee","idgruppo":"1","cdsquadra":"01","dsoperativaAttmanu":"test","dssquadra":"Squadra di test","categoria":"O","canale":"I","dsattmanu":"test1","cdintervento":"17/12/2015","dataApertura":"2015-12-17 00:00:00.0","idattmanu":"5","dsgruppo":"ee","idsquadra":"2","cdgruppo":"32","priorita":"2"},{"gravita":"2","idintervento":"1894","stato":"O","dsintervento":"Squadra: Squadra di test, gruppo: ee","idgruppo":"1","cdsquadra":"01","dsoperativaAttmanu":"test","dssquadra":"Squadra di test","categoria":"O","canale":"I","dsattmanu":"test1","cdintervento":"18/02/2016","dataApertura":"2016-02-18 00:00:00.0","idattmanu":"5","dsgruppo":"ee","idsquadra":"2","cdgruppo":"32","priorita":"2"}]}
        */

        String serviceURL = APPData.getServiceURL("oggetti-service/interventi", APPData.bEncript);
        String[] labelsParam = new String[]{"idcomplesso", "idedificio", "idpiano", "idvano", "idoggetto"};
        String[] valuesParam = new String[]{fltIDComplesso!=null?fltIDComplesso:"",fltIDEdificio!=null?fltIDEdificio:"",fltIDPiano!=null?fltIDPiano:"", fltIDVano!=null?fltIDVano:"", fltIDOggetto!=null?fltIDOggetto:""};
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
                    String deleteSQL = "DELETE FROM INTERVENTI";
                    // deleteSQL += " INNER JOIN OGGETTO ON INTERVENTI.IDOGGETTO = OGGETTO.IDOGGETTO ";

                    ///////////////////////////////////////////////////////
                    // 23-07-2015
                    // N.B.: Query verificata con SQLITE Browser :
                    // SELECT INTERVENTI.IDINTERVENTO FROM INTERVENTI LEFT JOIN OGGETTO ON INTERVENTI.IDOGGETTO=OGGETTO.IDOGGETTO WHERE OGGETTO.IDEDIFICIO=9
                    //
                    deleteSQL += " WHERE (";
                    if (fltIDComplesso != null && !fltIDComplesso.isEmpty()) {
                        if (nFilter > 0) deleteSQL += " AND ";
                        // deleteSQL += "OGGETTO.IDCOMPLESSO=" + fltIDComplesso;
                        deleteSQL += "INTERVENTI.IDINTERVENTO IN (" +
                                " SELECT INTERVENTI.IDINTERVENTO FROM INTERVENTI"+
                                " LEFT JOIN OGGETTO ON INTERVENTI.IDOGGETTO=OGGETTO.IDOGGETTO WHERE OGGETTO.IDCOMPLESSO="+fltIDComplesso+")";
                        nFilter++;
                    }
                    if (fltIDEdificio != null && !fltIDEdificio.isEmpty()) {
                        if (nFilter > 0) deleteSQL += " AND ";
                        // deleteSQL += "OGGETTO.IDEDIFICIO=" + fltIDEdificio;
                        deleteSQL += "INTERVENTI.IDINTERVENTO IN (" +
                                " SELECT INTERVENTI.IDINTERVENTO FROM INTERVENTI"+
                                " LEFT JOIN OGGETTO ON INTERVENTI.IDOGGETTO=OGGETTO.IDOGGETTO WHERE OGGETTO.IDEDIFICIO="+fltIDEdificio+")";
                        nFilter++;
                    }
                    if (fltIDPiano != null && !fltIDPiano.isEmpty()) {
                        if (nFilter > 0) deleteSQL += " AND ";
                        // deleteSQL += "OGGETTO.IDPIANO=" + fltIDPiano;
                        deleteSQL += "INTERVENTI.IDINTERVENTO IN (" +
                                " SELECT INTERVENTI.IDINTERVENTO FROM INTERVENTI"+
                                " LEFT JOIN OGGETTO ON INTERVENTI.IDOGGETTO=OGGETTO.IDOGGETTO WHERE OGGETTO.IDPIANO="+fltIDPiano+")";
                        nFilter++;
                    }
                    if (fltIDVano != null && !fltIDVano.isEmpty()) {
                        if (nFilter > 0) deleteSQL += " AND ";
                        // deleteSQL += "OGGETTO.IDVANO=" + fltIDVano;
                        deleteSQL += "INTERVENTI.IDINTERVENTO IN (" +
                                " SELECT INTERVENTI.IDINTERVENTO FROM INTERVENTI"+
                                " LEFT JOIN OGGETTO ON INTERVENTI.IDOGGETTO=OGGETTO.IDOGGETTO WHERE OGGETTO.IDVANO="+fltIDVano+")";
                        nFilter++;
                    }
                    if (fltIDOggetto != null && !fltIDOggetto.isEmpty()) {
                        if (nFilter > 0) deleteSQL += " AND ";
                        deleteSQL += "INTERVENTI.IDOGGETTO=" + fltIDOggetto;
                        nFilter++;
                    }
                    if (fltIDOggetto != null && !fltIDOggetto.isEmpty()) {
                        if (nFilter > 0) deleteSQL += " AND ";
                        deleteSQL += "INTERVENTI.IDQUEUE=''";
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
                        String sIDOGGETTO = null;
                        String sIDINTERVENTO = null;
                        String sCDINTERVENTO = null;
                        String sDSINTERVENTO = null;

                        /*
                        "CREATE TABLE IF NOT EXISTS INTERVENTI (IDINTERVENTO integer,CATEGORIA text,IDATTMANU text,CDINTERVENTO text" +
                                ",STATO text,DSINTERVENTO text,CANALE text,DSESTESA text" +
                                ",DATA_APERTURA text,DATA_SCADENZA text,DATA_CHIUSURA text" +
                                ",RICHIEDENTE text,OPERATORE_HELP_DESK text,GRAVITA text,PRIORITA text,INRITARDO text,ESITO text,ISTRUZIONI text,COMMENTI text" +
                                ",IDGRUPPO text,IDOGGETTO text,IDPERSONA text,IDSQUADRA text,DOSSIER text" +
                                ",IDQUEUE integer" +
                                ")"
                                */

                        try {

                            int colIndex = jsonParser.Labels.indexOf("idintervento");
                            if (colIndex > 0) {
                                String Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                sIDINTERVENTO = Field;
                                values.put("IDINTERVENTO", Field);

                                colIndex = jsonParser.Labels.indexOf("idoggetto");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    sIDOGGETTO = Field;
                                    values.put("IDOGGETTO", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("idgruppo");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDGRUPPO", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("idattmanu");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDATTMANU", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("idpersona");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDPERSONA", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("idsquadra");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("IDSQUADRA", Field);
                                }


                                colIndex = jsonParser.Labels.indexOf("cdintervento");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    sCDINTERVENTO = Field;
                                    values.put("CDINTERVENTO", Field);

                                }
                                colIndex = jsonParser.Labels.indexOf("dsintervento");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    sDSINTERVENTO = Field;
                                    values.put("DSINTERVENTO", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("dsestesa");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("DSESTESA", Field);
                                }


                                colIndex = jsonParser.Labels.indexOf("categoria");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("CATEGORIA", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("stato");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("STATO", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("canale");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("CANALE", Field);
                                }

                                colIndex = jsonParser.Labels.indexOf("dataApertura");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("DATA_APERTURA", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("dataScadenza");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("DATA_SCADENZA", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("dataChiusura");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("DATA_CHIUSURA", Field);
                                }


                                colIndex = jsonParser.Labels.indexOf("richiedente");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("RICHIEDENTE", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("operatore_help_desk");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("OPERATORE_HELP_DESK", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("gravita");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("GRAVITA", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("priorita");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("PRIORITA", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("inritardo");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("INRITARDO", Field);
                                }


                                colIndex = jsonParser.Labels.indexOf("istruzioni");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("ISTRUZIONI", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("commenti");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("COMMENTI", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("esito");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("ESITO", Field);
                                }
                                colIndex = jsonParser.Labels.indexOf("dossier");
                                if (colIndex > 0) {
                                    Field = jsonParser.Recs.get(colIndex + jsonParser.nCols * iRec);
                                    values.put("DOSSIER", Field);
                                }

                                Field = "";
                                values.put("IDQUEUE", Field);


                                boolean bProceed = true;
                                if (fltIDOggetto != null && !fltIDOggetto.isEmpty()) {
                                    if (!sIDOGGETTO.equalsIgnoreCase(fltIDOggetto)) {
                                        bProceed = false;
                                    }
                                }


                                if (bProceed) {
                                    if (sIDOGGETTO != null && !sIDOGGETTO.isEmpty()) {
                                        int last_insert_id = (int) mainActivity.sqliteWrapper.db.insert("INTERVENTI", "", values);
                                        if (last_insert_id <= 0) {
                                            String message = "Errore nell'inserimento dell' internvento ID:" + sIDINTERVENTO + " nel db locale";
                                            if (syncTable == null) {
                                                if (context != null) {
                                                    DialogBox.ShowMessage(message, context, 1);
                                                    if (activity != null) {
                                                        if (DialogBox.DialogBox("ATTENZIONE", message, 0 + 1 + 2, context)) {
                                                        }
                                                    }
                                                } else {
                                                    syncTable.RetVal = -1;
                                                    syncTable.Message += message;
                                                }
                                            }
                                        } else {
                                            System.err.println("["+(iRec+1)+"/"+jsonParser.nRecs+"] - Intervento ID:"+sIDINTERVENTO+" - CD:"+sCDINTERVENTO+" - DS:"+sDSINTERVENTO+"OK ");
                                        }
                                    } else {
                                        System.err.println("["+(iRec+1)+"/"+jsonParser.nRecs+"] - Intervento ID:"+sIDINTERVENTO+" - CD:"+sCDINTERVENTO+" - l'ID oggetto associato è vouto!");
                                    }
                                } else {
                                    System.err.println("["+(iRec+1)+"/"+jsonParser.nRecs+"] - Intervento ID:"+sIDINTERVENTO+" CD:"+sCDINTERVENTO+" - l'oggetto avente ID:"+sIDOGGETTO+" è stato filtrato!");
                                }


                            } else {
                                System.err.println("["+(iRec+1)+"/"+jsonParser.nRecs+"] - tag idintervento non trovato!");
                            }


                        } catch (Exception e) {
                            System.err.println("["+(iRec+1)+"/"+jsonParser.nRecs+"] - Intervento ID:"+sIDINTERVENTO+" - CD:"+sCDINTERVENTO+" - oggetto ID:"+sIDOGGETTO+" - " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    APPData.LAST_READ_INTERVENTO = APPUtil.get_date_time();
                    mainActivity.sqliteWrapper.update_setup_record("LastReadIntervento", APPData.LAST_READ_INTERVENTO);


                    /////////////////////////////////////////
                    // Aggiornamento contatore nel server
                    //
                    APPPkPool.scrivi_pk_counter();
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







    /////////////////////////////////////
    // Crea l'intervento nel server
    //
    public static int invia_intervento ( String idintervento,
                                         String CodIntervento, String DsIntervento, String DsEstesa,
                                         String DataApertura,
                                         String IDoggetto,
                                         String IDQueue,
                                         Context context, Activity activity ) {

        MainActivity mainActivity = (MainActivity)MainActivity.getClassInstance();
        int res = 0;


        if (!idintervento.isEmpty()) {


            /*
            # Servizio inserisci interventi
            # metodo : POST
            # path : /oggetti-service/insert-interventi
            # esempio chiamata
            http://pamap.geisoft.org/services/services/pamap/oggetti-service/interventi-chiusi?idsessione=1a5e62e6-ea64-4004-a281-bd7e0797e9fd
            BODY-CONTENT: gravita=2&categoria=I&dsestesa=2015-05-03&canale=I&stato=A&cdintervento=TEST01&dsintervento=prova+da+test&idoggetto=12&idrichiedente=1&idattmanu&priorita=2&pk=AA001122
            # esempio risposta
            {"codEsito":"S","data":[
                {"gravita":"2","categoria":"O","canale":"I","idintervento":"1889","stato":"O","dataApertura":"2015-02-19 00:00:00.0","cdintervento":"19/02/2015","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},
                {"gravita":"2","categoria":"O","canale":"I","idintervento":"1890","stato":"O","dataApertura":"2015-04-16 00:00:00.0","cdintervento":"16/04/2015","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","categoria":"O","canale":"I","idintervento":"1891","stato":"O","dataApertura":"2015-07-16 00:00:00.0","cdintervento":"16/07/2015","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","categoria":"O","canale":"I","idintervento":"1892","stato":"O","dataApertura":"2015-10-15 00:00:00.0","cdintervento":"15/10/2015","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","categoria":"O","canale":"I","idintervento":"1893","stato":"O","dataApertura":"2015-12-17 00:00:00.0","cdintervento":"17/12/2015","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","categoria":"O","canale":"I","idintervento":"1894","stato":"O","dataApertura":"2016-02-18 00:00:00.0","cdintervento":"18/02/2016","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","categoria":"O","canale":"I","idintervento":"1895","stato":"O","dataApertura":"2016-04-21 00:00:00.0","cdintervento":"21/04/2016","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","categoria":"O","canale":"I","idintervento":"1896","stato":"O","dataApertura":"2016-07-21 00:00:00.0","cdintervento":"21/07/2016","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","categoria":"O","canale":"I","idintervento":"1897","stato":"O","dataApertura":"2016-10-20 00:00:00.0","cdintervento":"20/10/2016","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","categoria":"O","canale":"I","idintervento":"1898","stato":"O","dataApertura":"2016-12-15 00:00:00.0","cdintervento":"15/12/2016","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","categoria":"O","canale":"I","idintervento":"1899","stato":"O","dataApertura":"2017-02-16 00:00:00.0","cdintervento":"16/02/2017","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","categoria":"O","canale":"I","idintervento":"1900","stato":"O","dataApertura":"2017-04-20 00:00:00.0","cdintervento":"20/04/2017","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","categoria":"O","canale":"I","idintervento":"1901","stato":"O","dataApertura":"2017-07-20 00:00:00.0","cdintervento":"20/07/2017","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","categoria":"O","canale":"I","idintervento":"1902","stato":"O","dataApertura":"2017-10-19 00:00:00.0","cdintervento":"19/10/2017","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","categoria":"O","canale":"I","idintervento":"1903","stato":"O","dataApertura":"2017-12-21 00:00:00.0","cdintervento":"21/12/2017","dsintervento":"Squadra: Squadra di test, gruppo: ee","idattmanu":"5","idgruppo":"1","idsquadra":"2","priorita":"2"},{"gravita":"2","stato":"O","idpersona":"2","idintervento":"1905","dsintervento":"Squadra: Squadra di test, gruppo: ee","idgruppo":"1","categoria":"O","canale":"I","cdintervento":"07/09/2015","dataApertura":"2015-09-07 00:00:00.0","idattmanu":"6","idsquadra":"2","priorita":"2"},{"gravita":"2","stato":"O","idpersona":"2","idintervento":"1906","dsintervento":"Squadra: Squadra di test, gruppo: ee","idgruppo":"1","categoria":"O","canale":"I","cdintervento":"07/01/2016","dataApertura":"2016-01-07 00:00:00.0","idattmanu":"6","idsquadra":"2","priorita":"2"},{"gravita":"2","stato":"O","idpersona":"2","idintervento":"1907","dsintervento":"Squadra: Squadra di test, gruppo: ee","idgruppo":"1","categoria":"O","canale":"I","cdintervento":"07/03/2016","dataApertura":"2016-03-07 00:00:00.0","idattmanu":"6","idsquadra":"2","priorita":"2"},{"gravita":"2","stato":"O","idpersona":"2","idintervento":"1908","dsintervento":"Squadra: Squadra di test, gruppo: ee","idgruppo":"1","categoria":"O","canale":"I","cdintervento":"07/09/2016","dataApertura":"2016-09-07 00:00:00.0","idattmanu":"6","idsquadra":"2","priorita":"2"},{"gravita":"2","stato":"O","idpersona":"2","idintervento":"1909","dsintervento":"Squadra: Squadra di test, gruppo: ee","idgruppo":"1","categoria":"O","canale":"I","cdintervento":"07/01/2017","dataApertura":"2017-01-07 00:00:00.0","idattmanu":"6","idsquadra":"2","priorita":"2"},{"gravita":"2","stato":"O","idpersona":"2","idintervento":"1910","dsintervento":"Squadra: Squadra di test, gruppo: ee","idgruppo":"1","categoria":"O","canale":"I","cdintervento":"07/03/2017","dataApertura":"2017-03-07 00:00:00.0","dataChiusura":"2017-04-07 00:00:00.0","idattmanu":"6","idsquadra":"2","priorita":"2"},{"gravita":"2","stato":"O","idpersona":"2","idintervento":"1911","dsintervento":"Squadra: Squadra di test, gruppo: ee","idgruppo":"1","categoria":"O","canale":"I","cdintervento":"07/09/2017","dataApertura":"2017-09-07 00:00:00.0","dataChiusura":"2017-04-07 00:00:00.0","idattmanu":"6","idsquadra":"2","priorita":"2"},
                {"gravita":"2","categoria":"S","canale":"I","idintervento":"AA001122","dsestesa":"ds estesa","stato":"A","cdintervento":"TEST01","dsintervento":"ds intervento","idoggetto":"12","priorita":"2"}]}

                Oppure
            {"intervento":null,"codEsito":"E","msgEsito":"org.hibernate.PropertyValueException: not-null property references a null or transient value: com.geisoft.pamap.model.persistence.pamap.command.Interventi.priorita"}



            {"intervento":null,"codEsito":"E","msgEsito":"org.hibernate.exception.DataException: could not insert: [com.geisoft.pamap.model.persistence.pamap.command.Interventi]; nested exception is javax.persistence.PersistenceException: org.hibernate.exception.DataException: could not insert: [com.geisoft.pamap.model.persistence.pamap.command.Interventi]"}

url:http://pamap.geisoft.org/services/pamap/oggetti-service/insert-interventi?idsessione=e59455ab-8b67-48d7-80bf-ca40098863f7
BodyContent:gravita=2&categoria=I&dsestesa=&canale=I&stato=A&cdintervento=9233.2&dsintervento=xxxxxxxx+&idoggetto=9233&idrichiedente=1&idattmanu=&priorita=2&pk=ADAD2


             */


            // PARAMETRI :
            // gravita=2&
            // categoria=I&
            // dsestesa=2015-05-03&
            // canale=I&
            // stato=A&
            // cdintervento=TEST01&
            // dsintervento=prova+da+test&
            // idoggetto=12&
            // idrichiedente=1&
            // idattmanu&
            // priorita=2
            // &pk=AA001122


            // N.B.: Formato Data : "YYYY-MM-DD HH:MM:SS
            String Categoria = "S";
            String Canale = "I";
            String Stato = "N";
            String Gravita = "2";
            String IDAttmanu = "";
            String IDGruppo = "";
            String Priorita = "2";
            String IDrichiedente = APPData.USER_LOGIN_ID!=null?APPData.USER_LOGIN_ID:"";

            String[] labelsParam = null;
            String[] valuesParam = null;

            String serviceURL = APPData.getServiceURL("oggetti-service/insert-interventi", APPData.bEncript);
            String[] bodyLabelsParam = new String[]{
                    "gravita",
                    "categoria",
                    "dsestesa",
                    "canale",
                    "stato",
                    "cdintervento",
                    "dsintervento",
                    "idoggetto",
                    "idrichiedente",
                    "idattmanu",
                    "priorita",
                    "pk",
                    "dataapertura"
            };

            String[] bodyValuesParam = new String[]{
                    Gravita,
                    Categoria,
                    DsEstesa,
                    Canale,
                    Stato,
                    CodIntervento,
                    DsIntervento,
                    IDoggetto,
                    IDrichiedente,
                    IDAttmanu,
                    Priorita,
                    idintervento,
                    DataApertura
            };


            String[] resultTags = new String[]{"codEsito", "data"};


            // URL = http://pamap.geisoft.org/services/pamap/oggetti-service/insert-interventi?idsessione=6dc4d17e-c149-4965-8e42-3d53b8c95d91
            // gravita=2&categoria=S&dsestesa=&canale=I&stato=N&cdintervento=9233.17&dsintervento=gggg&idoggetto=9233&idrichiedente=1&idattmanu=&priorita=2&pk=AF17&dataapertura=2015-07-09

            if (NetworkActivity.isOnline() > 0) {
                JSONParser jsonParser = new JSONParser(null);

                res = jsonParser.ParseURL(serviceURL, labelsParam, valuesParam, resultTags, null, null, bodyLabelsParam, bodyValuesParam, Constants.ENCRYPT, Constants.DECRYPT, true);
                if (res > 0) {
                    String COD_ESITO = jsonParser.Header != null ? jsonParser.Header.get(0) : "";
                    if (COD_ESITO.equals("S")) {
                        res = 1;
                    } else {
                        res = -1;
                    }


                } else {
                    if (jsonParser.rawHttpStatus != 200 && jsonParser.rawHttpStatus != 201 && jsonParser.rawHttpStatus != 202 && jsonParser.rawHttpStatus != 203) {
                        String message = "Risposta dal server non valida [HTTP status:" + jsonParser.rawHttpStatus + "]";
                        DialogBox.ShowMessage(message, context, 1);
                        message = "Server non disponibile!";
                        if (activity != null) {
                            if (DialogBox.DialogBox("ATTENZIONE", message, 0 + 1 + 2, activity)) {
                            }
                        }
                    }
                }

            } else {
            }
        }

        return res;
    }





}
