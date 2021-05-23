package de.mksoft.demotrainingsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.interfaces.RSAPrivateKey;

public class DownloadTask {
    private static final String TAG="Download Task";
    private Context context;
    private MainActivity mainActivity;

    private String downloadURL;
    private ProgressDialog progressDialog;

    public DownloadTask(Context context, String downloadURL){
        this.context=context;
        this.mainActivity=(MainActivity)context;
        this.downloadURL=downloadURL;

        Log.e(TAG, downloadURL);
        new DownloadingTask().execute();

    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog=new ProgressDialog(context);
            progressDialog.setMessage("Downloading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0){
            try{
                URL url=new URL(downloadURL);
                HttpURLConnection c=(HttpURLConnection)url.openConnection();
                c.setRequestMethod("GET");
                c.connect();
                if(c.getResponseCode()!=HttpURLConnection.HTTP_ACCEPTED){
                    Log.e(TAG, "Server returned HTTP "+c.getResponseCode()+" "+c.getResponseMessage());
                }
                InputStreamReader is=new InputStreamReader(c.getInputStream());
                BufferedReader in=new BufferedReader(is);
                String str;
                StringBuilder sb=new StringBuilder();
                while((str=in.readLine())!=null){
                    sb.append(str);
                }
                String cont=sb.toString();
                Rsa rsa=new Rsa(mainActivity);
                RSAPrivateKey privateKey=rsa.getPrivateKeyFromPreferences();
                if(privateKey!=null) {
                    String plain = rsa.decrypt(cont, privateKey);
                    Log.e(TAG, plain);
                }
            } catch(Exception e){
                e.printStackTrace();
                Log.e(TAG, "Download Error Exception "+e.getMessage());
            }
            return null;
        }
    }
}
