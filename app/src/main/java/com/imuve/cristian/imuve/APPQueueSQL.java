package com.imuve.cristian.imuve;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;


/**
 * Created by Cristian on 23/04/2015.
 */
public class APPQueueSQL {

    public APPQueueSQL(Context context) {

    }



    public static int read_queue ( ) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        APPData.NumQueue = 0;

        if (APPData.IDQueue == null) APPData.IDQueue = new ArrayList<Integer>();
        if (APPData.DescQueue == null) APPData.DescQueue = new ArrayList<String>();


        APPData.IDQueue.clear();
        APPData.DescQueue.clear();


        String[] cols = new String[]{"id", "Operation", "Status", "TableName", "LocalID", "ExtID", "Field1", "Field2", "Field3"};
        String whereClause = null;

        Cursor mCursor = null;


        try {

            mCursor = mainActivity.sqliteWrapper.db.query(false, "queue", cols, whereClause, null, null, null, null, null);

            if (mCursor != null) {
                if (mCursor.moveToFirst()) {

                    System.err.println("Read DB.queue [ID,Operation,Status,TableName,LocalID,ExtID,Field1,Field2,Field3]...");

                    do {
                        // System.err.println("[" + mCursor.getString(0) + "," + mCursor.getString(1) + "," + mCursor.getString(2) + "," + mCursor.getString(3) + "," + mCursor.getString(4) + "," + mCursor.getString(5) + "," + mCursor.getString(6) + "," + mCursor.getString(7) + "," + mCursor.getString(8) + "]");
                        try {
                            APPData.IDQueue.add(Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("id"))));
                        } catch (NumberFormatException e) { APPData.IDQueue.add(0); }
                        try {
                            String info = "";
                            String TableName = mCursor.getString(mCursor.getColumnIndex("TableName"))!=null?mCursor.getString(mCursor.getColumnIndex("TableName")):"[VUOTO]";
                            String Operation = mCursor.getString(mCursor.getColumnIndex("Operation"))!=null?mCursor.getString(mCursor.getColumnIndex("Operation")):"[VUOTO]";
                            String Code = (mCursor.getString(mCursor.getColumnIndex("Field1"))!=null?mCursor.getString(mCursor.getColumnIndex("Field1")):"[VUOTO]");

                            if (Operation.compareToIgnoreCase("INSERT") == 0) {
                                info += "[+] ";
                            } else if (Operation.compareToIgnoreCase("UPDATE") == 0) {
                                info += "[>] ";
                            } else if (Operation.compareToIgnoreCase("DELETE") == 0) {
                                info += "[-] ";
                            } else {
                                info += "[?] ";
                            }
                            info += TableName;
                            info += ".";
                            info += Code;

                            // + "," + (mCursor.getString(mCursor.getColumnIndex("Field2")) + "," + mCursor.getString(mCursor.getColumnIndex("Field2"));
                            APPData.DescQueue.add(info);
                        } catch (NumberFormatException e) { APPData.DescQueue.add(""); }

                        APPData.NumQueue++;

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
    }









    ////////////////////////////////////////////////////////////////////////////////////////////
    // Scodatore : processa il record presenti nella coda
    // Reason :     "LOCAL" ->  NESSUNA AZIONE
    // Reason :     "SERVER" ->  Processa la coda per trasferire le modifiche/aggiunte al server

    public static int de_queue ( String tableFilter, String Reason, Integer IDQueueFilter, String ExtIDForCheck ) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        ArrayList<Integer> IDQueueArray = new ArrayList<Integer>();
        ArrayList<String> IDAnterventoArray = new ArrayList<String>();

        int nProcessed = 0, errID = 0;


        String[] cols = new String[]{"id", "Operation", "Status", "TableName", "LocalID", "ExtID", "Field1", "Field2", "Field3"};
        String whereClause = "";
        int nFilter = 0;

        boolean bUpdatePoolKey = true;


        if (tableFilter != null && !tableFilter.isEmpty()) {
            whereClause += "TableName='" + tableFilter + "'";
            nFilter++;
        }

        if (IDQueueFilter != null && IDQueueFilter > 0) {
            if (nFilter>0) whereClause += " AND ";
            whereClause += "id=" + IDQueueFilter + "";
            nFilter++;
        }


        Cursor mCursor = null;

        try {

            mCursor = mainActivity.sqliteWrapper.db.query(false, "queue", cols, whereClause, null, null, null, null, null);

            if (mCursor != null) {

                if (mCursor.moveToFirst()) {
                    String table = null;
                    String operation = null;
                    Integer IDQueue = 0;
                    String locID = null, extID = null;

                    // System.err.println("DeQueue DB.queue [ID,Operation,Status,TableName,LocalID,ExtID,Field1,Field2,Field3]...");

                    do {

                        // System.err.println("[" + mCursor.getString(0) + "," + mCursor.getString(1) + "," + mCursor.getString(2) + "," + mCursor.getString(3) + "," + mCursor.getString(4) + "," + mCursor.getString(5) + "," + mCursor.getString(6) + "," + mCursor.getString(7) + "," + mCursor.getString(8) + "]");

                        try {
                            IDQueue = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("id")));
                            locID = mCursor.getString(mCursor.getColumnIndex("LocalID"));
                            extID = mCursor.getString(mCursor.getColumnIndex("ExtID"));
                        } catch (NumberFormatException e) { }

                        try {
                            table = mCursor.getString(mCursor.getColumnIndex("TableName"));
                            operation = mCursor.getString(mCursor.getColumnIndex("Operation"));

                        } catch (NumberFormatException e) { }


                        if (ExtIDForCheck != null) {
                            if (ExtIDForCheck.compareToIgnoreCase(extID) != 0) {
                                System.err.println("ID checksum failed!");
                            }
                        }

                        if (IDQueue > 0 && table != null && !table.isEmpty()) {

                            if (Reason == "LOCAL") {
                            } else {
                                if (Reason == "SERVER") {

                                    if (NetworkActivity.isOnline() > 0) {

                                        if (extID != null && !extID.isEmpty()) {

                                            if (table.compareToIgnoreCase("INTERVENTI") == 0 && (operation.compareToIgnoreCase("INSERT") == 0 || operation.compareToIgnoreCase("UPDATE") == 0) ) {
                                                String[] cols2 = new String[]{"IDINTERVENTO", "CDINTERVENTO", "DSINTERVENTO", "DSESTESA", "IDOGGETTO", "DATA_APERTURA", "IDQUEUE"};
                                                String whereClause2 = "IDINTERVENTO='" + extID + "'";
                                                Cursor mCursor2 = null;

                                                try {


                                            /*db.execSQL("CREATE TABLE IF NOT EXISTS INTERVENTI (IDINTERVENTO integer,CATEGORIA TEXT,IDATTMANU TEXT,CDINTERVENTO TEXT," +
                                                    "STATO TEXT,DSINTERVENTO TEXT,CANALE TEXT,DSESTESA " +
                                                    "TEXT,DATA_APERTURA TEXT,DATA_SCADENZA TEXT,DATA_CHIUSURA TEXT," +
                                                    "RICHIEDENTE TEXT,OPERATORE_HELP_DESK TEXT,GRAVITA TEXT,PRIORITA TEXT,INRITARDO TEXT,ESITO TEXT,ISTRUZIONI TEXT,COMMENTI TEXT," +
                                                    "IDGRUPPO TEXT,IDOGGETTO TEXT,IDPERSONA TEXT,IDSQUADRA TEXT,DOSSIER" +
                                                    */

                                                    mCursor2 = mainActivity.sqliteWrapper.db.query(false, "INTERVENTI", cols2, whereClause2, null, null, null, null, null);

                                                    if (mCursor2 != null) {

                                                        if (mCursor2.moveToFirst()) {
                                                            String idintervento = null;

                                                            try {
                                                                idintervento = mCursor2.getString(mCursor2.getColumnIndex("IDINTERVENTO"));
                                                            } catch (Exception e) {
                                                            }

                                                            if (idintervento != null && !idintervento.isEmpty()) {

                                                                if (APPData.isPKValid()) {

                                                                    try {

                                                                        String IDintervento = mCursor2.getString(mCursor2.getColumnIndex("IDINTERVENTO"));
                                                                        String cdintervento = mCursor2.getString(mCursor2.getColumnIndex("CDINTERVENTO"));
                                                                        String dsintervento = mCursor2.getString(mCursor2.getColumnIndex("DSINTERVENTO"));
                                                                        String dsestesa = mCursor2.getString(mCursor2.getColumnIndex("DSESTESA"));
                                                                        String idoggetto = mCursor2.getString(mCursor2.getColumnIndex("IDOGGETTO"));
                                                                        String dataapertura = mCursor2.getString(mCursor2.getColumnIndex("DATA_APERTURA"));
                                                                        String IDQueue2 = mCursor2.getString(mCursor2.getColumnIndex("IDQUEUE"));

                                                                        if (APPInterventiSQL.invia_intervento(idintervento, cdintervento, dsintervento, dsestesa, dataapertura, idoggetto, String.valueOf(IDQueue), null, null) > 0) {
                                                                            IDQueueArray.add(IDQueue);
                                                                            IDAnterventoArray.add(IDintervento);
                                                                            if (IDQueue2.compareToIgnoreCase(String.valueOf(IDQueue)) != 0) {
                                                                                errID++;
                                                                            } else {
                                                                                nProcessed++;
                                                                            }

                                                                            ///////////////////////////////////////////
                                                                            // Aggiornamento contatore nel server
                                                                            //
                                                                            if (bUpdatePoolKey) {
                                                                                APPPkPool.scrivi_pk_counter();
                                                                                bUpdatePoolKey = false;
                                                                            }

                                                                        } else {
                                                                            // Inserimento nel server fallito
                                                                        }

                                                                    } catch (Exception e) {
                                                                        e.getMessage();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                }
                                            }
                                        } else {
                                        // ID Non valido
                                        }
                                    }
                                }
                            }
                        }
                    } while (mCursor.moveToNext());
                }
            }



            // Aggiustamento tabelle SQLite...

            // Cancellazione records esistenti su coda
            if (IDQueueArray != null && IDQueueArray.size() > 0) {
                for (int i = 0; i<IDQueueArray.size(); i++) {
                    Integer IDQueue = IDQueueArray.get(i);
                    String deleteSQL = "DELETE FROM queue WHERE (";
                    deleteSQL += "id=" + IDQueue;
                    deleteSQL += ")";

                    try {
                        mainActivity.sqliteWrapper.db.execSQL(deleteSQL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String IDIntervento = IDAnterventoArray.get(i);
                    String updateSQL = "UPDATE INTERVENTI set";
                    updateSQL += " IDQUEUE=''";
                    updateSQL += "";
                    updateSQL += " WHERE IDINTERVENTO='" + IDIntervento + "'";
                    updateSQL += "";

                    try {
                        mainActivity.sqliteWrapper.db.execSQL(updateSQL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            } catch (Exception e) {
                System.err.print(e.getMessage());
            }

        if (mCursor != null) {
            mCursor.close();
        }

        if (errID > 0) {
        }

        return nProcessed;
    }
}
