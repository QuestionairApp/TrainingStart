package de.mksoft.demotrainingsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button scanBtn;
    private IntentIntegrator qrScan = new IntentIntegrator(this);
    private Rsa rsa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanBtn=findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!=null){
            if(result.getContents()==null){
                Toast.makeText(this, "Result not foung", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    String cont=result.getContents();
                    JSONObject obj = new JSONObject(result.getContents());
                    String key=obj.getString("key");
                    String filename=obj.getString("fileName");
                    String signatur=obj.getString("signature");
                    StringBuilder sb=new StringBuilder(key);
                    sb.append("::");
                    sb.append(filename);
                    String toHash=sb.toString();
                    try {
                        MessageDigest md = MessageDigest.getInstance("SHA-512");
                        byte[] toHashBytes = md.digest(toHash.getBytes());
                        StringBuilder sbi = new StringBuilder();
                        for(int i=0;i<toHashBytes.length;i++)
                        {
                            sbi.append(Integer.toString((toHashBytes[i] & 0xff) + 0x100, 16).substring(1));
                        }
                        Rsa rsa=new Rsa(this);
                        PublicKey pub=rsa.getPublicKey();
                        boolean v=rsa.verify(pub, sbi.toString(), signatur);
                        if(v){
                            Toast.makeText(this, "Key and Filename verified", Toast.LENGTH_SHORT).show();
                            SharedPreferences pref=this.getSharedPreferences("TrainingApp", MODE_PRIVATE);
                            SharedPreferences.Editor editor= pref.edit();
                            editor.putString("privateKey", key);
                        }else {
                            Toast.makeText(this, "Key and Filename not verified", Toast.LENGTH_SHORT).show();
                        }
                    } catch(Exception e) {
                        System.out.println(e);
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
