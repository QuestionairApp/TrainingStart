package de.mksoft.zxingclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonScan;
    private TextView textViewName, textViewAddress;
    private IntentIntegrator qrScan = new IntentIntegrator(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonScan=findViewById(R.id.buttonScan);
        textViewAddress=findViewById(R.id.textViewAddress);
        textViewName=findViewById(R.id.textViewName);

        buttonScan.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!=null){
            if(result.getContents()==null){
                Toast.makeText(this, "Result not foung", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject obj = new JSONObject(result.getContents());
                    textViewName.setText(obj.getString("name"));
                    textViewAddress.setText(obj.getString("address"));
                } catch(JSONException e){
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View view) {
        qrScan.initiateScan();
    }
}