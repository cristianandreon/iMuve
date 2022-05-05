package com.imuve.cristian.imuve;

import android.content.Context;
import org.apache.commons.net.util.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Cristian on 22/04/2015.
 */


public class JSONParser {

    public Context context = null;
    JSONObject jsonObject = null;

    String rawHttpContent = null;
    String rawHttpContentType = null;
    int rawHttpStatus = 0;
    long rawHttpContentLen;

    ArrayList<String> Header = null;
    int nHeader = 0;

    ArrayList<String> Labels = null;
    int nLabels = 0;


    ArrayList<String> Recs = null;
    int nRecs = 0;
    int nField = 0;
    int cField = 0;
    int nCols = 0;

    public JSONParser (Context context) {
        jsonObject = new JSONObject();
        this.context = context;
    }



    /* json sample :
    {
        "list": [
        {
            "account": 1,
                "name": "card",
                "number": "xxxxx xxxx xxxx 2002",
        },
        {
            "account": 2,
                "name": "card2",
                "number": "xxxxx xxxx xxxx 3003",
        }
        ],
        "count": 2
    }
    */


    public int ParseURL (String strURL,
                         String [] PostLabel, String [] PostFields,
                         String[] jsonTags, String jsonHeaderTag, String jsonFieldTag,
                         String [] BodyPostLabel, String [] BodyPostFields,
                         boolean bEncrypt,
                         boolean bDecrypt,
                         boolean bParseJSON) {
        int res = 0;

        try {

            rawHttpContent = "";
            rawHttpContentType = "";
            rawHttpStatus = 0;



            if (PostLabel != null && PostFields != null) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();

                int cgiSepPos = strURL.indexOf('?');
                if (cgiSepPos >= 0 && cgiSepPos < strURL.length()) {
                    strURL += "&";
                } else if (cgiSepPos == strURL.length()) {
                    strURL += "";
                } else {
                    strURL += "?";
                }

                for (int i=0; i<PostLabel.length; i++) {
                    if (bEncrypt) {
                        pairs.add(new BasicNameValuePair(PostLabel[i], Crypt.encrypt(PostFields[i].getBytes()).toString()));
                    } else {
                        pairs.add(new BasicNameValuePair(PostLabel[i], PostFields[i]));
                    }
                }
                strURL += URLEncodedUtils.format(pairs, "utf-8");
                // http://pamap.geisoft.org/services/pamap/login-service/login-device?username=azienda&imei=AAAAAA-BB-CCCCCC-E&longitude=45.01&latitude=1234.66
            }

            HttpClient hc = new DefaultHttpClient();
            HttpPost post = new HttpPost(strURL);

            post.setHeader(HTTP.USER_AGENT, "iMuve v1.01");

            post.setHeader("ACCEPT", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            post.setHeader("ACCEPT-LANGUAGE", "it-IT,it;q=0.8,en-US;q=0.5,en;q=0.3");
            // post.setHeader("ACCEPT-ENCODING", "gzip, deflate");


            URL url = new URL(strURL);
            String baseUrl = url.getProtocol() + "://" + url.getHost();
            // post.setHeader("host", baseUrl);

            if (BodyPostLabel != null && BodyPostFields != null) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                for (int i=0; i<BodyPostLabel.length; i++) {
                    if (bEncrypt) {
                        pairs.add(new BasicNameValuePair(BodyPostLabel[i], Crypt.encrypt(BodyPostFields[i].getBytes()).toString()));
                    } else {
                        pairs.add(new BasicNameValuePair(BodyPostLabel[i], BodyPostFields[i]));
                    }
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
                post.setEntity(entity);

                // post.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");

                /*
                System.err.println("url:" + url);
                String BodyContent = EntityUtils.toString(entity);
                System.err.println("BodyContent:" + BodyContent);
                */


            } else {
            }


            HttpParams httpParameters = hc.getParams();
            // Set the timeout in milliseconds until a connection is established. The default value is zero, that means the timeout is not used.
            int timeoutConnection = 30000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 35000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            Header[] header = post.getAllHeaders();
            HttpResponse rp = hc.execute(post);

            rawHttpContentType = rp.getEntity().getContentType().getValue();

            rawHttpStatus = rp.getStatusLine().getStatusCode();

            rawHttpContentLen = rp.getEntity().getContentLength();

            if(rawHttpStatus == HttpStatus.SC_OK) {

                if (Recs == null) Recs = new ArrayList<String>();

                nHeader = 0;
                nRecs = 0;
                nField = 0;
                cField = 0;


                try {

                    if (bDecrypt) {
                        String httpResult = EntityUtils.toString(rp.getEntity());
                        byte[] httpEcryptedResult = Base64.decodeBase64(httpResult);
                        byte[] hhtpDecriptedResult = Crypt.qdecrypt(httpEcryptedResult);
                        rawHttpContent = hhtpDecriptedResult != null ? String.valueOf(hhtpDecriptedResult) : null;
                    } else {
                        rawHttpContent = EntityUtils.toString(rp.getEntity());
                    }

                    if (bParseJSON) {
                        res = ParseString(rawHttpContent, jsonTags, jsonHeaderTag, jsonFieldTag);
                    } else {
                        res = 1;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    res = -1;
                }
            }



        } catch(Exception e){
            e.printStackTrace();
            res = -1;
        }


        return res;
    }
    
    
    public int ParseString ( String str, String[] jsonTags, String jsonArrayHeaderTag, String jsonArrayFieldTag) {

        int res = 0;

        try {

            // N.B.: manca il set insensitive

            jsonObject = new JSONObject(str);

            nHeader = 0;
            if (Header == null) Header = new ArrayList<String>();
            Header.clear();

            if (Labels == null) Labels = new ArrayList<String>();
            Labels.clear();
            nLabels = 0;

            nRecs = 0;
            nField = 0;
            if (Recs == null) Recs = new ArrayList<String>();
            Recs.clear();

            if (jsonTags != null) {
                ParseObject(jsonObject, jsonTags, false);
            }


            if (jsonArrayHeaderTag != null) {
                JSONArray jsonArray = jsonObject.getJSONArray(jsonArrayHeaderTag);
                nHeader += jsonArray.length();
                for (int iField = 0; iField < nHeader; iField++) {
                    try {
                        Header.add(jsonArray.getString(iField));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (jsonArrayFieldTag != null) {
                JSONArray jsonArray = null;

                try {
                    jsonArray = jsonObject.getJSONArray(jsonArrayFieldTag);
                    } catch (JSONException e) {
                    jsonArray = null;
                }

                if (jsonArray != null) {
                    nRecs = jsonArray.length();

                    for (int iRec = 0; iRec < nRecs; iRec++) {
                        JSONObject jsonObjectRec = jsonArray.getJSONObject(iRec);
                        JSONArray jsonNames = jsonObjectRec.names();

                        if (nLabels == 0) {
                            nLabels = jsonNames.length();
                            for (int iField = 0; iField < jsonNames.length(); iField++) {
                                Labels.add(jsonNames.getString(iField));
                            }
                            nCols = nLabels;
                        }

                        for (int iField = 0; iField < nCols; iField++) {
                            try {
                                String paramName = jsonObjectRec.getString(Labels.get(iField));
                                Recs.add(paramName);
                                cField++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Recs.add("");
                                cField++;
                            }
                        }
                    }

                    nField = Recs.size();
                    // nRecs = nField / nCols;
                } else {
                    // Pone l'oggetto pei campi Recs
                    if (ParseObject(jsonObject.getJSONObject(jsonArrayFieldTag), jsonTags, true) > 0) {
                    }
                }
            }

            res = 1;

        } catch (JSONException e) {
            e.printStackTrace();
            res = -1;
        }

        return res;
    }


    private int ParseObject ( JSONObject pjsonObject, String [] jsonTags, boolean toFields) {
        int res = 0;

        if (toFields) {
            JSONArray jsonArray = pjsonObject.names();
            if (jsonArray != null) {
                // Header.clear();

                nCols = pjsonObject.length();

                for (int iField = 0; iField < nCols; iField++) {
                    try {
                        Recs.add(jsonArray.getString(iField));
                        cField++;
                        nField++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                nRecs++;

                for (int iField = 0; iField < nCols; iField++) {
                    try {
                        Recs.add(pjsonObject.getString(jsonArray.getString(iField)));
                        cField++;
                        nField++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                nRecs++;

                res = 1;
            }

        } else {

            for (int iField = 0; iField < jsonTags.length; iField++) {
                try {
                    JSONObject subJSONObject = null;
                    try {
                        subJSONObject = pjsonObject.getJSONObject(jsonTags[iField]);
                    } catch (JSONException e) {
                    } catch (Exception e) {
                    }
                    if (subJSONObject != null) {
                        ParseObject(subJSONObject, jsonTags, toFields);
                    } else {
                        try {
                            Header.add(pjsonObject.getString(jsonTags[iField]));
                            nHeader++;
                        } catch (JSONException e) {
                            e.getMessage();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        return res;
    }



}
