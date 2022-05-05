package com.imuve.cristian.imuve;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

// import org.apache.commons.net.util.Base64;



/**
 * Created by Cristian on 23/04/2015.


 N.B.: limite di 2MB sul cursor window di Android nell'accesso a SQLite

 Problema prestazioni base64decode su 4mb

 */



public class APPDwgSQL {

    public APPDwgSQL(Context context) {

    }





    public static int leggi_dwgs ( Integer IDEdificio ) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();


        APPData.NumDwg= 0;

        if (APPData.IDDwg == null) APPData.IDDwg = new ArrayList<Integer>();
        if (APPData.CodDwg == null) APPData.CodDwg = new ArrayList<String>();
        if (APPData.VerDwg == null) APPData.VerDwg = new ArrayList<String>();
        if (APPData.VerDescDwg == null) APPData.VerDescDwg = new ArrayList<String>();
        if (APPData.DescDwg == null) APPData.DescDwg = new ArrayList<String>();


        APPData.IDDwg.clear();
        APPData.CodDwg.clear();
        APPData.VerDwg.clear();
        APPData.VerDescDwg.clear();
        APPData.DescDwg.clear();


        if (IDEdificio < 0) return 0;


        // Lettura tabella DWG
        /*
            "CREATE TABLE IF NOT EXISTS DWG (IDDWG text,IDPIANO text,DWGNAME text,DWGVERSION text,DWGDATA text)");
            */

        // String[] cols = new String[] { "IDPIANO", "IDEDIFICIO","CDPIANO","DSPIANO" };
        String whereClause = null;
        if (IDEdificio > 0) whereClause = "IDEDIFICIO='"+IDEdificio+"'";

        String rawQuesry = "SELECT DWG.id as id,DWG.IDDWG AS IDDWG" +
                ",DWG.IDPIANO AS IDPIANO" +
                ",DWGVERSION,DWGNAME" +
                ",PIANO.CDPIANO AS CDPIANO" +
                ",EDIFICIO.CDEDIFICIO AS CDEDIFICIO" +
                ",EDIFICIO.DSEDIFICIO AS DSEDIFICIO" +
                " FROM DWG";

        rawQuesry += " LEFT JOIN PIANO ON DWG.IDPIANO=PIANO.IDPIANO";
        rawQuesry += " LEFT JOIN EDIFICIO ON PIANO.IDEDIFICIO=EDIFICIO.IDEDIFICIO";

        if (whereClause != null) {
            rawQuesry += " WHERE (";
            rawQuesry += whereClause;
            rawQuesry += ")";
        }


        Cursor mCursor = null;

        try {

            long maxSize = mainActivity.sqliteWrapper.db.getMaximumSize();
            long maxPageSize = mainActivity.sqliteWrapper.db.getPageSize();


            mCursor = mainActivity.sqliteWrapper.db.rawQuery(rawQuesry, null);

            if (mCursor != null) {

                if (mCursor.moveToFirst()) {
                    Integer Id = 0;
                    String str = null, strCodPiano = null;


                    int nRecs = mCursor.getCount();
                    int nCols = mCursor.getColumnCount();

                    if (nRecs <= 0) {
                    }

                    do {

                        // System.err.println("[" + mCursor.getString(0) + "," + mCursor.getString(1) + "," + mCursor.getString(2) + "]");

                        try {
                            str = mCursor.getString(0)!=null?mCursor.getString(0):"";
                            Id = Integer.parseInt(str);
                        } catch (NumberFormatException e) {
                        }

                        APPData.IDDwg.add(Id);

                            try {

                                String strDescEdificio = mCursor.getString(mCursor.getColumnIndex("DSEDIFICIO"))!=null?mCursor.getString(mCursor.getColumnIndex("DSEDIFICIO")):"";
                                String strCodEdificio = mCursor.getString(mCursor.getColumnIndex("CDEDIFICIO"))!=null?mCursor.getString(mCursor.getColumnIndex("CDEDIFICIO")):"";
                                String strCodpiano = mCursor.getString(mCursor.getColumnIndex("CDPIANO"))!=null?mCursor.getString(mCursor.getColumnIndex("CDPIANO")):"";

                                String strIDPiano = mCursor.getString(mCursor.getColumnIndex("IDPIANO"))!=null?mCursor.getString(mCursor.getColumnIndex("IDPIANO")):"";

                                String strDWGName = "";

                                // APPData. .add(str);

                                str = mCursor.getString(mCursor.getColumnIndex("DWGVERSION"))!=null?mCursor.getString(mCursor.getColumnIndex("DWGVERSION")):"";

                                if (str != null && !str.isEmpty()) {
                                    String sign = str.substring(0, 2);
                                    if (sign.charAt(0) != 'A' || sign.charAt(1) != 'C') {
                                        str = "*Inv*";
                                    } else {
                                        String version = str.substring(2, 6);
                                        if (Integer.parseInt(version)==1012) {
                                            str = "R13";
                                        } else if (Integer.parseInt(version)==1014) {
                                            str = "R14";
                                        } else if (Integer.parseInt(version)==1015) {
                                            str = "2000/2";
                                        } else if (Integer.parseInt(version)==1018) {
                                            str = "*2003/4/5";
                                        } else if (Integer.parseInt(version)==1021) {
                                            str = "*2007/8/9";
                                        } else if (Integer.parseInt(version)==1024) {
                                            str = "*2010/11/12";
                                        } else if (Integer.parseInt(version)==1027) {
                                            str = "*2013/14/15/16";
                                        } else if (Integer.parseInt(version)==1027) {
                                            str = "*INV* : "+version+"";
                                        }
                                    }
                                }


                                APPData.VerDwg.add(str);

                                try {
                                    String sIDDWG = mCursor.getString(mCursor.getColumnIndex("id")) != null ? mCursor.getString(mCursor.getColumnIndex("id")) : "";
                                    APPData.VerDescDwg.add("["+sIDDWG+"]"+"\n"+str);
                                } catch (Exception e) { APPData.VerDescDwg.add("["+"!"+"]"+"-"+str); }


                                try {
                                    strDWGName = mCursor.getString(mCursor.getColumnIndex("DWGNAME")) != null ? mCursor.getString(mCursor.getColumnIndex("DWGNAME")) : "";
                                } catch (Exception e) { str = ""; }

                                // APPData.DescDwg.add(str + ((str!=null && !str.isEmpty())?"-":" ") + "["+strDescEdificio+"]");
                                APPData.DescDwg.add(strCodpiano+"-"+strCodEdificio);

                                try {
                                    strCodPiano = mCursor.getString(mCursor.getColumnIndex("CDPIANO")) != null ? mCursor.getString(mCursor.getColumnIndex("CDPIANO")) : "";
                                } catch (Exception e) { strCodPiano = ""; }



                                // APPData.CodDwg.add(strCodPiano);
                                APPData.CodDwg.add(strDWGName);

                                APPData.NumDwg++;

                            } catch (Exception e) {
                            }

                    } while (mCursor.moveToNext());
                }
            }


        } catch (Throwable e) {
            System.err.print(e.getMessage());
            System.err.println("rawQuesry:");
            System.err.println(rawQuesry);
        }

        if (mCursor != null) {
            mCursor.close();
        }

        if (APPData.cPiano > 0) {
            if (APPData.cPiano >= APPData.NumPiani) APPData.cPiano = APPData.NumPiani>0 ? APPData.NumPiani-1 : 0;
        }

        return 1;
    }








    public static byte[] leggi_dwg(Integer IDdwg, boolean bForceRead) {

        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        byte [] bytesDWG = null;

        try {

            // "CREATE TABLE IF NOT EXISTS DWG (IDDWG text,IDPIANO text,DWGNAME text,DWGVERSION text,DWGDATA text)");

            if (IDdwg > 0) {

                String rawQuesry = "SELECT IDDWG,DWGNAME,DWGVERSION,DWGDATA FROM DWG";

                rawQuesry += " WHERE (";
                rawQuesry += "id='"+IDdwg+"'";
                rawQuesry += ") LIMIT 0,1";


                Cursor mCursor = null;

                try {

                    mCursor = mainActivity.sqliteWrapper.db.rawQuery(rawQuesry, null);

                    if (mCursor != null) {

                        int nRecs = mCursor.getCount();
                        int nCols = mCursor.getColumnCount();

                        if (nRecs <= 0) {
                            System.err.print("leggi_dwg : No records in query for IDdwg:"+IDdwg);
                            return null;
                        }

                        if (mCursor.moveToFirst()) {

                            boolean bCompressedData = false;

                            do {

                                try {
                                    System.err.println("[" + mCursor.getString(0) + "," + mCursor.getString(1) + "," + mCursor.getString(2) + "]");
                                } catch (Exception e) {
                                    System.err.print(e.getMessage());
                                }

                                try {

                                    String str = mCursor.getString(mCursor.getColumnIndex("IDDWG")) != null ? mCursor.getString(mCursor.getColumnIndex("IDDWG")) : "";
                                    Integer IDDWG = Integer.parseInt(str);

                                } catch (Exception e) {
                                    System.err.print(e.getMessage());
                                }

                                try {

                                    String str = mCursor.getString(mCursor.getColumnIndex("DWGNAME"))!=null?mCursor.getString(mCursor.getColumnIndex("DWGNAME")):"";
                                    if (str != null && !str.isEmpty()) {
                                        if (str.charAt(0)=='*') bCompressedData = true;
                                    }
                                } catch (Exception e) {
                                    System.err.print(e.getMessage());
                                }

                                try {

                                    String str = mCursor.getString(mCursor.getColumnIndex("DWGDATA"))!=null?mCursor.getString(mCursor.getColumnIndex("DWGDATA")):"";
                                    if (str != null && !str.isEmpty()) {
                                        if (bCompressedData) {
                                            // bytesDWG = Utility.decompress(Base64.decodeBase64(str)).getBytes();
                                            bytesDWG = Utility.decompress(Base64.decode(str, Base64.DEFAULT));
                                        } else {
                                            // bytesDWG = Base64.decodeBase64(str);
                                            bytesDWG = Base64.decode(str, Base64.DEFAULT);
                                        }
                                    }

                                } catch (Exception e) {
                                    System.err.print(e.getMessage());
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
            }

        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

        return bytesDWG;
    }





    /////////////////////////////////
    // Legge i DWG dal server
    //
    public static int sincronizza_dwg ( String pfltIDPiano,
                                          Context context, Activity activity,
                                          SYNCTable syncTable
    ) {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        int iIDPiano, sIDPiano, eIDPiano, res = 0, retVal = 0;
        String fltIDPiano = null;

        /*
        */

        // "CREATE TABLE IF NOT EXISTS DWG (IDDWG text,IDPIANO text,DWGNAME text,DWGVERSION text,DWGDATA text)");

        if (pfltIDPiano == null) {
            sIDPiano = 0;
            eIDPiano = APPData.IDPiani.size();
        } else {
            sIDPiano = Integer.parseInt(pfltIDPiano);
            eIDPiano = sIDPiano+1;
        }

        // res = jsonParser.ParseURL("http://cristianandreon.com/test.php", labelsParam, valuesParam, resultTags, null, null, false, false, true);

        String ErrortString = "";
        for (iIDPiano = sIDPiano; iIDPiano < eIDPiano; iIDPiano++) {

            if (pfltIDPiano == null) {
                fltIDPiano = String.valueOf(APPData.IDPiani.get(iIDPiano));
            } else {
                fltIDPiano = pfltIDPiano;
            }


            if (NetworkActivity.isOnline() > 0) {
                JSONParser jsonParser = new JSONParser(null);

                /*
                # Servizio get mappa piano
                # metodo : POST
                # path : /mappe-service/get-dwg-id-piano
                # esempio chiamata
                /services/pamap/mappe-service/get-dwg-id-piano?idpiano=1&idsessione=1cec5003-3aff-43e1-bc06-53e858507a53
                # la risposta saranno i bytes del file
                */

                String serviceURL = APPData.getServiceURL("mappe-service/get-dwg-id-piano", APPData.bEncript);
                String[] labelsParam = new String[]{"idpiano"};
                String[] valuesParam = new String[]{fltIDPiano != null ? fltIDPiano : ""};
                String[] resultTags = new String[]{"codEsito"};
                String fieldsTags = "data";
                byte[] bytesDWG = null;

                update_progress_message(syncTable, "Lettura disegno " + (iIDPiano + 1) + "/" + eIDPiano + "...");


                String sDescPiano = "";

                try {

                    int iIndex = APPData.iIndexOf(APPData.IDPiani, Integer.parseInt(fltIDPiano));

                    if (iIndex >= 0 && iIndex < APPData.PianiDesc.size()) {
                        sDescPiano = APPData.PianiDesc.get(iIndex);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                res = jsonParser.ParseURL(serviceURL, labelsParam, valuesParam, resultTags, null, fieldsTags, null, null, Constants.ENCRYPT, Constants.DECRYPT, false);
                if (res > 0) {
                    // Store into SQLite...

                    // APPData.COD_ESITO = jsonParser.Header != null ? jsonParser.Header.get(0) : "";
                    if (jsonParser.rawHttpContent != null && jsonParser.rawHttpContentLen > 0) {


                        /////////////////////////////////////
                        // Decodifica dei dati letti
                        //

                        update_progress_message ( syncTable, "Analisi dati disegno " + (iIDPiano + 1) + "/" + eIDPiano + " [ID Piano:"+fltIDPiano+"] - [1/3] - Decodifica dati ("+(jsonParser.rawHttpContentLen/1024)+" Kb)..." );

                        boolean bUseCompressedData = true;
                        byte bCompressedContent[] = null;
                        String sCompressedContent = null;

                        if (bUseCompressedData) {

                            try {

                                // bytesDWG = Base64.decodeBase64(jsonParser.rawHttpContent);
                                bytesDWG = Base64.decode(jsonParser.rawHttpContent, Base64.DEFAULT);

                                byte[] Version = Arrays.copyOf(bytesDWG, 6);
                                String nVersion = Version.toString().substring(2);
                                int iVersion = 0;

                                try {
                                    iVersion = Integer.parseInt(nVersion);
                                    if (iVersion >= 1021 || iVersion < 1012) {
                                        ErrortString += "INVALID DWG SIGN.:" + Version.toString()  + "\r\n";
                                        if (syncTable != null) {
                                            syncTable.Message = ErrortString;
                                            syncTable.RetVal = -11;
                                        }
                                        return -111;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                update_progress_message ( syncTable, "Analisi dati disegno " + (iIDPiano + 1) + "/" + eIDPiano + " [ID Piano:"+fltIDPiano+"] - [2/3] - Compressione dati ("+(jsonParser.rawHttpContentLen/1024)+" Kb)..." );

                                bCompressedContent = Utility.compress(bytesDWG);




                                update_progress_message ( syncTable, "Analisi dati disegno " + (iIDPiano + 1) + "/" + eIDPiano + " [ID Piano:"+fltIDPiano+"] - [3/3] - Ricodifica dati ("+(jsonParser.rawHttpContentLen/1024)+" Kb)..." );

                                // sCompressedContent = Base64.encodeBase64String(bCompressedContent);
                                sCompressedContent = Base64.encodeToString(bCompressedContent, Base64.DEFAULT);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            System.err.println("Compress DWG result : Original Size : "+(jsonParser.rawHttpContentLen/1024)+"Kb - Compressed Size : "+(sCompressedContent.length()/1024)+"Kb");
                            jsonParser.rawHttpContent = sCompressedContent;
                            jsonParser.rawHttpContentLen = sCompressedContent.length();


                        } else {
                            System.err.println("UnCompressed DWG : Size (b64encoded) : "+jsonParser.rawHttpContentLen);
                            // bytesDWG = Base64.decodeBase64(jsonParser.rawHttpContent);
                            bytesDWG = Base64.decode(jsonParser.rawHttpContent, Base64.DEFAULT);
                            bytesDWG[6] = 0;
                        }




                        ////////////////////////////////////////////
                        // Verifica dimensione dati
                        //

                        if (jsonParser.rawHttpContentLen > 2*1024*1024) {
                            String message = "Dimensioni DWG fuori limite\r\n\rDimensione DWG : " + (jsonParser.rawHttpContentLen/1024.0/1024.0) + " > 2MB";
                            if (syncTable != null) {
                                if (syncTable.progress != null) syncTable.progress.setMessage(message);
                                syncTable.Message += "\r\n"+message;

                            } else {
                                if (context != null) {
                                    if (Looper.myLooper() == Looper.getMainLooper()) {
                                        try {
                                            DialogBox.ShowMessage(message, context, 1);
                                        } catch (Exception e) {
                                            e.getMessage();
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                            ErrortString += message + "\r\n";
                            if (syncTable != null) {
                                syncTable.Message = ErrortString;
                                syncTable.RetVal = -11;
                            }
                            return -11;
                        }








                        long last_insert_id = 0;

                        ContentValues values = new ContentValues();

                        values.put("IDPIANO", fltIDPiano);


                        String dwgName = bUseCompressedData?"*":"";

                        dwgName += "PIANO"+fltIDPiano;
                        values.put("DWGNAME", dwgName);






                        ////////////////////////////////////////////
                        // Cancellazione records esistenti
                        //

                        update_progress_message ( syncTable, "Analisi dati disegno " + (iIDPiano + 1) + "/" + eIDPiano + " [ID Piano:"+fltIDPiano+"] - Eliminazione dati precedente disegno in corso...");


                        int nFilter = 0;
                        String deleteSQL = "DELETE FROM DWG WHERE (";
                        if (fltIDPiano != null && !fltIDPiano.isEmpty()) {
                            deleteSQL += "IDPIANO=" + fltIDPiano;
                            nFilter++;
                        }
                        /*
                        if (fltIDPiano != null && !fltIDPiano.isEmpty()) {
                            if (nFilter>0) deleteSQL += " AND ";
                            deleteSQL += "IDPIANO=" + fltIDPiano;
                            nFilter++;
                        }*/
                        if (nFilter == 0) {
                            deleteSQL += "1==1";
                        }
                        deleteSQL += ")";

                        try {
                            mainActivity.sqliteWrapper.db.execSQL(deleteSQL);
                        } catch (Exception e) {
                            e.printStackTrace();
                            syncTable.Message += "\r\nFatal Error in dwg sync deleteSQL ["+"IDPiano"+fltIDPiano+"]";
                        }





                        String version = new String(bytesDWG, 0, 6);
                        values.put("DWGVERSION", version);

                        values.put("DWGDATA", jsonParser.rawHttpContent);



                        update_progress_message(syncTable, "Analisi dati disegno " + (iIDPiano + 1) + "/" + eIDPiano + " [ID Piano:" + fltIDPiano + "] - Registrazione dati del disegno (" + (jsonParser.rawHttpContentLen/1024)+" Kb) in corso...");

                        try {
                            last_insert_id = mainActivity.sqliteWrapper.db.insert("DWG", "", values);
                            if (last_insert_id <= 0) {
                                String message = "Scruttura del DWG sul Piano fallita!";
                                if (syncTable != null) {
                                    syncTable.Message += message;
                                } else {
                                    if (context != null) {
                                        if (Looper.myLooper() == Looper.getMainLooper()) {
                                            try {
                                                DialogBox.ShowMessage(message, context, 1);
                                            } catch (Exception e) {
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (syncTable != null) {
                                syncTable.Message += "\r\nFatal Error in dwg sync insertSQL [IDPiano:" + fltIDPiano + "] - [ Error : " + e.getMessage() + "]";
                            }
                        }

                        if (last_insert_id <= 0) {
                            // NON NECESSARIA : LEFT JOIN
                        } else {
                            retVal++;
                        }

                    }

                } else {

                    if (jsonParser.rawHttpStatus != 200 && jsonParser.rawHttpStatus != 201 && jsonParser.rawHttpStatus != 202 && jsonParser.rawHttpStatus != 203) {

                        String message = "Errore lettura disegno " + (iIDPiano + 1) + "/" + eIDPiano + "\r\nID Piano:" + fltIDPiano + " - Piano:"+sDescPiano+"\r\nRisposta dal server non valida [HTTP status:" + jsonParser.rawHttpStatus + "]\r\n";

                        update_progress_message(syncTable, message);

                        if (syncTable != null) {

                        } else {
                            if (context != null) {
                                if (Looper.myLooper() == Looper.getMainLooper()) {
                                    try {
                                        DialogBox.ShowMessage(message, context, 1);
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                        ErrortString += message + "\r\n";


                    } else {
                        /*
                        String message = "Lettura disegno " + iIDPiano + "/" + eIDPiano + " in corso...";
                        if (syncTable != null) {
                            syncTable.progress.setMessage();
                        } else {
                            if (context != null) {
                                if (Looper.myLooper() == Looper.getMainLooper()) {
                                    try {
                                        DialogBox.ShowMessage(message, context, 1);
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                        ErrortString += message + "\r\n";
                        */
                    }
                }
            } else {
                syncTable.Message += "\r\nDWG Sync failed due to temporary offline ["+"IDPiano"+fltIDPiano+"]";
            }
        }


        if (!ErrortString.isEmpty()) {
            if (syncTable != null) {
                syncTable.Message = ErrortString;
                syncTable.RetVal = retVal;
            } else {
                if (activity != null) {
                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        try {
                            if (DialogBox.DialogBox("ATTENZIONE", ErrortString, 0 + 0, activity)) {
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }

        if (pfltIDPiano == null) {
            APPData.LAST_READ_DWG = APPUtil.get_date_time();
            mainActivity.sqliteWrapper.update_setup_record("LastReadDWG", APPData.LAST_READ_DWG);
        }

        return retVal;
    }



    private static void update_progress_message(SYNCTable syncTable, final String msg) {
        try {
            if (syncTable != null) {
                final SYNCTable psyncTable = syncTable;
                Handler mainHandler = new Handler(syncTable.context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (psyncTable.progress != null)
                            psyncTable.progress.setMessage(msg);
                    }
                };
                mainHandler.post(myRunnable);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
