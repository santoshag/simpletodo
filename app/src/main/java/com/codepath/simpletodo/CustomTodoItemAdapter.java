package com.codepath.simpletodo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by santoshag on 5/31/16.
 */
public class CustomTodoItemAdapter extends ArrayAdapter<TodoItem> {
    public CustomTodoItemAdapter(Context context, ArrayList<TodoItem> todoItems) {
        super(context, 0, todoItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TodoItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }
        // Lookup view for data population
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvPriority = (TextView) convertView.findViewById(R.id.tvPriority);
        TextView tvDueDate = (TextView) convertView.findViewById(R.id.tvDueDate);

        // Populate the data into the template view using the data object
        tvTitle.setText(item.title);
        tvPriority.setText(TodoItem.getPriorityInString(item.priority));
        tvPriority.setTextColor(TodoItem.getPriorityColor(item.priority));

        String startDateString = item.dueDate;
        DateFormat df = new SimpleDateFormat("EEE MMM dd h:mm:ss z yyyy", Locale.ENGLISH);
        Date startDate;
        try {
            startDate = df.parse(startDateString);
            String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", startDate);//Thursday
            String stringMonth = (String) android.text.format.DateFormat.format("MMM", startDate); //Jun
            String intMonth = (String) android.text.format.DateFormat.format("MM", startDate); //06
            String year = (String) android.text.format.DateFormat.format("yyyy", startDate); //2013
            String day = (String) android.text.format.DateFormat.format("dd", startDate); //20

            String dueDateText = "Due on "+ dayOfTheWeek + ", " +  stringMonth + " " + day + " " +year;
            tvDueDate.setText(dueDateText);
            tvDueDate.setTypeface(null, Typeface.ITALIC);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
