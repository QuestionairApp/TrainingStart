package de.mksoft.demotrainingsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button scanBtn;
    private IntentIntegrator qrScan = new IntentIntegrator(this);
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
