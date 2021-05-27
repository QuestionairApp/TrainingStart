package de.mksoft.demotrainingsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
                String[] content=new String[3];
                String dummy;
                StringBuilder sb=new StringBuilder();
                int cnt=0;
                //content[0] - RSA-encrypted AES Key base64-encoded
                //content[1] - Trennlinie zwischen AES Key und Payload
                //content[2] - AES encrypted JSON-Object base64 encoded, zwei Doppelpunkte, iv base64 encoded
                while((str=in.readLine())!=null){
                    content[cnt]=str;
                    cnt++;
                }
                String payload[]=content[2].split("::");

                String cont=sb.toString();
                Rsa rsa=new Rsa(mainActivity);
                RSAPrivateKey privateKey=rsa.getPrivateKeyFromPreferences();

                if(privateKey!=null) {
                    String plain = rsa.decrypt(content[0], privateKey);
                    byte[] aesKey=Base64.decode(plain, Base64.DEFAULT);
                    byte[] iv=Base64.decode(payload[1], Base64.DEFAULT);
                    byte[] daten=Base64.decode(payload[0], Base64.DEFAULT);
                    SecretKeySpec skeySpec = new SecretKeySpec(aesKey, "AES/CBC/PKCS7Padding");
                    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                    cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
                    byte[] decrypted = cipher.doFinal(daten);
                    String trainingDataJson=new String(Base64.decode(decrypted, Base64.DEFAULT), StandardCharsets.UTF_8);
                    SharedPreferences prefs=context.getSharedPreferences("TrainingApp", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit= prefs.edit();
                    edit.putString("training", trainingDataJson);
                    edit.apply();
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
