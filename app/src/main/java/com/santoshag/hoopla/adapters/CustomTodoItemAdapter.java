package com.santoshag.hoopla.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.santoshag.hoopla.R;
import com.santoshag.hoopla.models.TodoItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by santoshag on 5/31/16.
 */
public class CustomTodoItemAdapter extends RecyclerView.Adapter<CustomTodoItemAdapter.ViewHolder> {


    private static final String TAG = "CustomTodoItemAdapter";

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvTitle;
        public TextView tvDueDate;
        public View vPriorityLine;
        public TextView tvLocationName;
        public ImageView ivLocation;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            // Lookup view for data population
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDueDate = (TextView) itemView.findViewById(R.id.tvDueDate);
//             vPriorityLine = (View)  itemView.findViewById(R.id.vPriorityLine);
            tvLocationName = (TextView) itemView.findViewById(R.id.tvLocationName);
            ivLocation = (ImageView) itemView.findViewById(R.id.ivLocation);
        }
    }


    // Store a member variable for the contacts
    private List<TodoItem> mItems;

    private Context mContext;

    public CustomTodoItemAdapter(Context context, List<TodoItem> todoItems) {
        mItems = todoItems;
        mContext = context;

    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }


    @Override
    public CustomTodoItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_todo, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(CustomTodoItemAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        TodoItem todoItem = mItems.get(position);

        Log.i("SAM", todoItem.toString());
        // Set item views based on your views and data model
        TextView textView = viewHolder.tvTitle;
        textView.setText(todoItem.title);

        TextView tvDueDate = viewHolder.tvDueDate;


        TextView tvLocationName = viewHolder.tvLocationName;

        ImageView ivLocation = viewHolder.ivLocation;
        //clear out image from image view
        ivLocation.setImageResource(0);


        String startDateString = todoItem.dueDate;
        DateFormat df = new SimpleDateFormat("EEE MMM dd h:mm:ss z yyyy", Locale.ENGLISH);
        Date startDate;
        try {
            startDate = df.parse(startDateString);
            String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", startDate);//Thursday
            String stringMonth = (String) android.text.format.DateFormat.format("MMM", startDate); //Jun
            String intMonth = (String) android.text.format.DateFormat.format("MM", startDate); //06
            String year = (String) android.text.format.DateFormat.format("yyyy", startDate); //2013
            String day = (String) android.text.format.DateFormat.format("dd", startDate); //20

            String dueDateText = "  " + dayOfTheWeek.substring(0, 3) + ", " + stringMonth + " " + day + " " + year;
            tvDueDate.setText(dueDateText);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        Log.i(TAG, todoItem.isLocationSet.toString());

        if (todoItem.isLocationSet) {
            Log.i(TAG, todoItem.placeName);
            tvLocationName.setText(todoItem.placeName);
            tvLocationName.setVisibility(View.VISIBLE);
            ivLocation.setVisibility(View.VISIBLE);
        }

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public int getPriorityColor(int priority) {
        switch (priority) {
            case 0:
                return mContext.getResources().getColor(R.color.priority_low);
            case 1:
                return mContext.getResources().getColor(R.color.priority_medium);
            case 2:
                return mContext.getResources().getColor(R.color.priority_high);
        }
        return mContext.getResources().getColor(R.color.priority_medium);
    }

}
