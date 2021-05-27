package de.mksoft.demotrainingsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.PublicKey;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final IntentIntegrator qrScan = new IntentIntegrator(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button scanBtn=findViewById(R.id.scanBtn);
        Button trainingInfo=findViewById(R.id.btnToTraining);
        scanBtn.setOnClickListener(this);
        trainingInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(), TrainingInfo.class);
                startActivity(i);
            }
        });
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
                Toast.makeText(this, "Result not found", Toast.LENGTH_SHORT).show();
            } else {
                try {
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
                            SharedPreferences pref=this.getSharedPreferences("TrainingApp", MODE_PRIVATE);
                            SharedPreferences.Editor editor= pref.edit();
                            editor.putString("privateKey", key);
                            editor.apply();
                            String address="http://training.mkservices.de/anschreiben/tmp/"+filename;
                            new DownloadTask(this, address);
                        }else {
                            Toast.makeText(this, "Key and Filename not verified", Toast.LENGTH_SHORT).show();
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
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
