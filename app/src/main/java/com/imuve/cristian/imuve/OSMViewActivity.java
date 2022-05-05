package com.imuve.cristian.imuve;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class OSMViewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osmview);

        Bundle extras = getIntent().getExtras();
        String key = extras.getString("Url");
        String gpsLongExtra = extras.getString("gpsLong");
        String gpsLatExtra = extras.getString("gpsLat");
        String mapZoomExtra = extras.getString("mapZoom");
        String gpsLong = gpsLongExtra!=null?gpsLongExtra:"0.0";
        String gpsLat = gpsLatExtra!=null?gpsLatExtra:"0.0";
        String mapZoom = mapZoomExtra!=null?mapZoomExtra:"15.0";

        WebView webView = (WebView)findViewById(R.id.wvMap);
        if (webView != null) {
            if (key != null && !key.isEmpty()) {

                WebSettings settings = webView.getSettings();
                settings.setJavaScriptEnabled(true);
                webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                webView.getSettings().setUseWideViewPort(false);

                webView.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        // Log.i(TAG, "Processing webview url click...");
                        view.loadUrl(url);
                        return true;
                    }

                    public void onPageFinished(WebView view, String url) {
                        // Log.i(TAG, "Finished loading URL: " + url);
                    }

                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        // Log.e(TAG, "Error: " + description);
                        // Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                    }
                });

                String fullURL = key + "?gpsLong="+gpsLong+"&gpsLat="+gpsLat+"&mapZoom="+mapZoom;
                webView.loadUrl(fullURL);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_osmview, menu);
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
