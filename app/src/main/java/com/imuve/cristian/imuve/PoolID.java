package com.imuve.cristian.imuve;

import android.content.Context;

/**
 * Created by Cristian on 23/04/2015.
 */
public class PoolID {

    public int ID = 1;
    public String ID_HEADER = "00";
    public String Table = "";

    private Context context;
    private MainActivity mainActivity;


    public PoolID(Context context, String Table, String ID_HEADER, int Mode) {
        // super(context);

        mainActivity = (MainActivity) MainActivity.getClassInstance();

        mainActivity.sqliteWrapper.getDatabaseName();

        this.ID_HEADER = ID_HEADER;
        this.Table = Table;

    }

}
