package com.imuve.cristian.imuve;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;

import java.io.File;


public class DocumentViewerActivity extends ActionBarActivity {

    private String FileURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_viewer);


        Bundle extras = getIntent().getExtras();
        try {
            FileURL = extras.getString("key");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (FileURL.endsWith(".pdf")) {
            ImageView imageView = (ImageView) findViewById(R.id.ivView);
            render_pdf(FileURL, imageView );
        } else{
            WebView webView = (WebView) findViewById(R.id.wvView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(FileURL);
        }
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void render_pdf ( String FileURL, ImageView imageView ) {

        try {

            // create a new renderer
            File file = new File(FileURL);
            ParcelFileDescriptor fileDesc = null;

            try {
                fileDesc = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            } catch (Exception e) {
                e.printStackTrace();
            }

            PdfRenderer renderer = null;
            try {
                renderer = new PdfRenderer(fileDesc);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888); // this creates a MUTABLE bitmap;

            // let us just render all pages
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);

                // say we render for showing on the screen
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                // do stuff with the bitmap
                if (bitmap != null) {
                    // Canvas canvas = new Canvas();
                    // canvas.drawBitmap(bitmap, 0, 0, null);
                    // view.draw(canvas);
                    imageView.setImageBitmap(bitmap);
                }

                // close the page
                page.close();
            }

            // close the renderer
            renderer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_document_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
