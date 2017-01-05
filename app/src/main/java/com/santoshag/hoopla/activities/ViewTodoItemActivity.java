package com.santoshag.hoopla.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.santoshag.hoopla.fragments.EditTaskFragment;
import com.santoshag.hoopla.models.TodoItem;
import com.santoshag.hoopla.R;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewTodoItemActivity extends AppCompatActivity {
    TextView tvItemTitle;
    TextView tvItemNotes;
    TextView tvPriority;
    TextView tvCalendar;
    Long dbItemIndex;
    TodoItem todoItem;
    EditTaskFragment editItemFragment;
    ImageView ivNavigate;
    ImageView ivGoogleStaticImgForLocation;
    Double latitude;
    Double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        //set tool bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.view_task);
        getSupportActionBar().setIcon(R.drawable.ic_app_icon);

        dbItemIndex = getIntent().getLongExtra("dbItemIndex", -1);
        todoItem = (TodoItem) new Select().from(TodoItem.class)
                .where("id = ?", dbItemIndex).executeSingle();

        populateItemFields();
    }

    //set tool bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viewtaskmenu, menu);
        return true;
    }

    //handle tool bar actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_edit_item:
                showEditDialog();
                break;
            case R.id.action_delete_item:
                deleteItem();
            default:
                break;
        }

        return true;
    }

    private void deleteItem(){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set title
            alertDialogBuilder.setTitle("Delete Task");
            alertDialogBuilder.setIcon(R.drawable.ic_delete_black_18dp);

            // set dialog message
            alertDialogBuilder
                    .setMessage("Delete " + todoItem.title + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            TodoItem.delete(TodoItem.class, todoItem.getId());
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();


    }

    //dialogfragment for edit todo item
    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();

        editItemFragment = EditTaskFragment.newInstance(dbItemIndex);
        editItemFragment.show(fm, "fragment_edit_name");
    }


    private void populateItemFields() {
        int position = getIntent().getIntExtra("position", -1);

        tvItemTitle = (TextView) findViewById(R.id.tvItemTitle);
        tvItemNotes = (TextView) findViewById(R.id.tvItemNotes);
        tvPriority = (TextView) findViewById(R.id.tvPriority);
        tvCalendar = (TextView) findViewById(R.id.tvCalendar);

        tvItemTitle.setText(todoItem.title);
        tvItemNotes.setText(todoItem.notes);
        tvPriority.setText(TodoItem.getPriorityInString(todoItem.priority));
        tvPriority.setTextColor(getPriorityColor(todoItem.priority));

        String dueDateString = todoItem.dueDate;
        DateFormat df = new SimpleDateFormat("EEE MMM dd h:mm:ss z yyyy", Locale.ENGLISH);
        Date dueDate;
        try {
            dueDate = df.parse(dueDateString);

            String month = (String) android.text.format.DateFormat.format("MM", dueDate); //06
            String year = (String) android.text.format.DateFormat.format("yyyy", dueDate); //2013
            String day = (String) android.text.format.DateFormat.format("dd", dueDate); //20
            String dueDateStr = getStringForDate(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
            tvCalendar.setText(dueDateStr);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(todoItem.isLocationSet){
            LinearLayout llLocation = (LinearLayout) findViewById(R.id.llLocation);
            llLocation.setVisibility(View.VISIBLE);
            TextView tvLocation = (TextView) findViewById(R.id.tvLocation);
            ivNavigate = (ImageView) findViewById(R.id.ivNavigate);
            ivGoogleStaticImgForLocation = (ImageView) findViewById(R.id.googleStaticImgForLocation);
            tvLocation.setText(todoItem.placeName + "\n" + todoItem.placeAddress);
            latitude = todoItem.latitude;
            longitude = todoItem.longitude;
            if (latitude != null && longitude != null) {
                setImageViewLocationAndOptions();
            }
        }

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private void setImageViewLocationAndOptions(){
        AsyncTask<Void, Void, Bitmap> setImageFromUrl = new AsyncTask<Void, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bmp = null;

                HttpClient httpclient = new DefaultHttpClient();
                String STATIC_MAP_API_ENDPOINT = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=15&size=280x150&markers=color:blue%7Clabel:S&sensor=false";
                HttpGet request = new HttpGet(STATIC_MAP_API_ENDPOINT);

                InputStream in = null;
                try {
                    in = httpclient.execute(request).getEntity().getContent();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bmp;
            }
            protected void onPostExecute(Bitmap bmp) {
                if (bmp!=null) {

                    ivGoogleStaticImgForLocation.setImageBitmap(bmp);
                    ivGoogleStaticImgForLocation.setVisibility(View.VISIBLE);
                    ivNavigate.setVisibility(View.VISIBLE);

                }
            }
        };
        setImageFromUrl.execute();

    }

    private String getStringForDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);
        Date date = calendar.getTime();

        return new SimpleDateFormat("EEE", Locale.ENGLISH).format(date.getTime())  + ", " + new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime()) + " " + new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime()) + " " + new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date.getTime());

    }

    public void navigateWithGMaps(View view){
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", latitude, longitude, todoItem.placeName);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);

    }

    int getPriorityColor(int priority) {
        switch (priority) {
            case 0:
                return getApplicationContext().getResources().getColor(R.color.priority_low);
            case 1:
                return getApplicationContext().getResources().getColor(R.color.priority_medium);
            case 2:
                return getApplicationContext().getResources().getColor(R.color.priority_high);
        }
        return getApplicationContext().getResources().getColor(R.color.priority_medium);
    }

}
