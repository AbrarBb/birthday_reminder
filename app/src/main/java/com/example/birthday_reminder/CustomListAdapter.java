package com.example.birthday_reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<Birthday> {

    private final Context context;
    private final ArrayList<Birthday> values;
    LayoutInflater inflater;

    public CustomListAdapter(@NonNull Context context, @NonNull ArrayList<Birthday> items) {
        super(context, -1, items);
        this.context = context;
        this.values = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View rowView = inflater.inflate(R.layout.birthday_row, parent, false);

        TextView tvName = rowView.findViewById(R.id.tvName);
        TextView tvDOB = rowView.findViewById(R.id.tvDOB);
        TextView tvAge = rowView.findViewById(R.id.tvAge);

       // TextView eventType = rowView.findViewById(R.id.tvEventType);

        Birthday dob = values.get(position);
        tvName.setText(dob.name);
        tvDOB.setText(dob.getFormattedDOB());
        tvAge.setText(dob.getAge());
        return rowView;
    }
}
