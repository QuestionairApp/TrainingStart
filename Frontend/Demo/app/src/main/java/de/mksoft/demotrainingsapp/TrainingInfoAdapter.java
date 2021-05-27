package de.mksoft.demotrainingsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class TrainingInfoAdapter extends ArrayAdapter<TrainingInfoItem> {
    private final Context context;
    private final ArrayList<TrainingInfoItem> items;
    public TrainingInfoAdapter(@NonNull Context context, ArrayList<TrainingInfoItem> items) {
        super(context, -1, items);
        this.context=context;
        this.items=items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.layout_list_item, parent, false);
        TextView label=row.findViewById(R.id.labelTxt);
        TextView data=row.findViewById(R.id.dataTxt);
        label.setText(items.get(position).label);
        data.setText(items.get(position).data);
        return row;
    }
}
