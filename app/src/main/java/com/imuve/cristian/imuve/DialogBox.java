package com.imuve.cristian.imuve;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.security.acl.NotOwnerException;

/**
 * Created by Cristian on 02/05/2015.
 */
public class DialogBox extends DialogFragment {

    static int btId = 0;


    public DialogBox () {
        super();
    }

    public static void ShowMessage(String Message, Context pContect, int long_time) {
        Toast toast = Toast.makeText(pContect, Message, long_time>0?Toast.LENGTH_LONG:Toast.LENGTH_SHORT);
        toast.show();
    }



    private static boolean mResult;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean DialogBox(String title, String message, int buttonMode, Context context) {

        try {

            // make a handler that throws a runtime exception when a message is received
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message mesg) {
                    throw new NumberFormatException();
                }
            };

            // make a text input dialog and show it
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(title);
            alert.setMessage(message);
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    dialog.cancel();
                    throw new RuntimeException();
                }
            });

            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });

            if ((buttonMode & 1)==1 && (buttonMode & 2)==2) {
                alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mResult = true;
                        handler.sendMessage(handler.obtainMessage());
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mResult = false;
                        handler.sendMessage(handler.obtainMessage());
                    }
                });
            } else {
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mResult = false;
                        handler.sendMessage(handler.obtainMessage());
                    }
                });
            }

            alert.show();

            // loop till a runtime exception is triggered.
            try {

                Looper.loop();

            } catch(NumberFormatException e4) {
                e4.getMessage();
            } catch(RuntimeException e2) {
                e2.getMessage();
            } catch(Exception e3) {
                e3.printStackTrace();
            }

        } catch (Exception e) {
            e.getMessage();
        }

        return mResult;
    }
}
