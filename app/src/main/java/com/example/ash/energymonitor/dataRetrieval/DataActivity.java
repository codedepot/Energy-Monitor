package com.example.ash.energymonitor.dataRetrieval;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.Enum;
import com.dropbox.client2.session.Session.AccessType;
import com.example.ash.energymonitor.DashboardListActivity;


/**
 * Created by Mehrdad on 1/30/2015.
 */
public class DataActivity extends AsyncTask<String, Void, String> {

    final static private String APP_KEY = "62hdf2xcvsnggyf";
    final static private String APP_SECRET = "68ogil29378xo5h";
    final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
    private DropboxAPI<AndroidAuthSession> mDBApi;


    @Override
    public String doInBackground(String... params){
       // Log.i("Proto", "Here is good out " + params[0].substring(params[0].length() -5) + "  " +params[0].substring(params[0].length() -5).equals(".txt") );
        if(params[0].substring(params[0].length() -4).equals(".txt")){
            //Log.i("Proto", "We are inside@!");
            try {
                File file = new File(params[0]);
                Log.i("Proto", "Before input stream " + params[0]);
//                FileOutputStream outputStream = new FileOutputStream(file);
                FileInputStream inputStream = new FileInputStream(file);
//                DropboxAPI.Entry response = mDBApi.putFile("magnum-opus.txt", inputStream,
//                        file.length(), null, null);
                Log.i("Proto", "Success ");
            }catch(Exception e){
                Log.i("Proto", "Failed!@, here is the error" + e);

            }
        }
        String result ="";

        try{
            String urlString = params[0];
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try{
                InputStream reader = new BufferedInputStream(urlConnection.getInputStream());
                String text = "";
                int i = 0;
                while((i=reader.read())!=-1){
                    text += (char)i;

                }
                Log.i("EmonLog", "HTTP Response: "+text);
                result = text;

            }catch (Exception e){
                Log.i("EmonLog", "HTTP Exception: "+e);
            }finally{
                Log.i("EmonLog", "HTTP Disconnecting");
                urlConnection.disconnect();
            }
        }catch(Exception e){
            e.printStackTrace();
            Log.i("EmonLog", "HTTP Exception: " + e);
        }
     return result;
    }


    private void dropBox(){
        // In the class declaration section:


// And later in some initialization function:
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        // MyActivity below should be your activity class name
       // mDBApi.getSession().startOAuth2Authentication(DashboardListActivity.this);

    }

}
