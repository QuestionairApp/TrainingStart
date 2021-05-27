package de.mksoft.demotrainingsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrainingInfo extends AppCompatActivity {
    ArrayList<TrainingInfoItem> itemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_info);
    }
    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences pref=getSharedPreferences("TrainingApp", MODE_PRIVATE);
        String itemString=pref.getString("training","");
        try{
            JSONArray arr=new JSONArray(itemString);
            itemList=new ArrayList<>();

            for(int i=0; i<arr.length(); i++){
                JSONObject obj=arr.getJSONObject(i);

                String label=obj.getString("label");

                String data=obj.getString("data");
                TrainingInfoItem item=new TrainingInfoItem(label, data);
                itemList.add(item);


            }


        } catch(JSONException e){
            Intent i=new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }

        ListView lv=findViewById(R.id.listView);
        TrainingInfoAdapter adapter=new TrainingInfoAdapter(this, itemList);
        lv.setAdapter(adapter);


    }
}