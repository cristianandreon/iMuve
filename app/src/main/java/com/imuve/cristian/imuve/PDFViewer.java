package com.imuve.cristian.imuve;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



/**
 * Created by Cristian on 18/06/2015.
 */
public class PDFViewer {

    private Activity activity = null;
    private Context context = null;


    public void ViewPDFFile ( Activity activity, String PdfFile ) {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        try {

            this.activity = activity;
            this.context = mainActivity.context;

            String localFile = PdfFile.replace("appdata/", "");
            CopyReadAssets (PdfFile, localFile, true);

            // getWindow().getDecorView().setAlpha(0.3f);
            Uri path = Uri.parse("file://" + mainActivity.getFilesDir()+'/'+localFile);

            Intent myIntent = new Intent(activity, DocumentViewerActivity.class);
            myIntent.putExtra("key", path.getPath());
            activity.startActivity(myIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void OpenPDFFile ( Activity activity, String PdfFile ) {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();

        this.activity = activity;
        this.context = mainActivity.context;

        String localFile = PdfFile.replace("appdata/", "");
        CopyReadAssets (PdfFile, localFile, false);

        if (localFile != null) {

            Uri path = Uri.parse("file://" + mainActivity.getFilesDir()+'/'+localFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                System.err.println("No Application Available to View PDF");
                Intent intent2 = Intent.createChooser(intent, "Open File");
                try {
                    activity.startActivity(intent2);
                } catch (ActivityNotFoundException e2) {
                    // Instruct the user to install a PDF reader here, or something
                }
            }
        }
    }


    ///////////////////////////////////////////////////////////
    // TODO : Da Testare la creazione dell'anteprima
    //
    private void CopyReadAssets ( String AssetName, String FileName, boolean bRewrite ) {
        AssetManager assetManager = activity.getAssets();

        InputStream in = null;
        OutputStream out = null;

        File file = new File(activity.getFilesDir(), FileName);
        try {

            if (!file.exists() || bRewrite) {
                in = assetManager.open(AssetName);
                out = context.openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

                copyFile(in, out);
                out.flush();
                out.close();

                in.close();


                /* NON Funziona con il PDF
                in = assetManager.open(AssetName);
                byte[] imageData = null;

                try {

                    final int THUMBNAIL_SIZE = 64;

                    Bitmap imageBitmap = BitmapFactory.decodeStream(in);

                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    imageData = baos.toByteArray();
                    if (imageData != null) {

                    }


                } catch(Exception ex) {
                    ex.printStackTrace();
                }

                in.close();
                */

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /////////////////////////////////////////////
    // TODO : Da sviluppare
    //
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void CreatePDF(View view, String FileName) {

        File file = new File(context.getFilesDir(), FileName);

        try {

            OutputStream outputStream = context.openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);


            // create a new document
            PdfDocument document = new PdfDocument();

            // crate a page description
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(100, 100, 1).create();

            // start a page
            PdfDocument.Page page = document.startPage(pageInfo);

            // draw something on the page
            View content = view;
            content.draw(page.getCanvas());

            // finish the page
            document.finishPage(page);
            // ...
            // add more pages
            // ...
            // write the document content
            document.writeTo(outputStream);

            // close the document
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


}
