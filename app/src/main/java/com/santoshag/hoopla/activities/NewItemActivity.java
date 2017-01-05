package com.santoshag.hoopla.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.santoshag.hoopla.models.TodoItem;
import com.santoshag.hoopla.utils.ProximityIntentReceiver;
import com.santoshag.hoopla.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class NewItemActivity extends AppCompatActivity {
    EditText etTitle;
    EditText etNotes;
    Spinner spPriority;
    DatePicker dpDueDate;
    LocationManager locationManager;
    String placeAddress;
    String placeName;
    int year;
    int month;
    int day;
    Double latitude;
    Double longitude;
    TextView tvCalendar;
    TextView tvLocation;
    ImageView ivNavigate;
    ImageView ivDeleteLocation;
    ImageView ivGoogleStaticImgForLocation;
    Boolean isLocationSet = false;

    int PLACE_PICKER_REQUEST = 1;
    private static final String PROX_ALERT_INTENT =
            "com.santoshag.hoopla.ProximityAlert";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        tvCalendar = (TextView) findViewById(R.id.tvCalendar);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        ivNavigate = (ImageView) findViewById(R.id.ivNavigate);
        ivDeleteLocation = (ImageView) findViewById(R.id.ivDeleteLocation);
        ivGoogleStaticImgForLocation = (ImageView) findViewById(R.id.googleStaticImgForLocation);

        initDueDate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void initDueDate(){
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        String dueDate = getStringForDate(year, month, day);
        tvCalendar.setText(dueDate);
    }

    //set tool bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newtaskmenu, menu);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.new_task);
        getSupportActionBar().setIcon(R.drawable.ic_app_icon);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                isLocationSet = true;
                Place place = PlacePicker.getPlace(data, this);

                placeName = place.getName().toString();
                placeAddress = place.getAddress().toString();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                tvLocation.setText(placeName + "\n" + placeAddress);
                setImageViewLocationAndOptions();
            }
        }
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
                    ivDeleteLocation.setVisibility(View.VISIBLE);

                }
            }
        };
        setImageFromUrl.execute();



    }

    //handle tool bar actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_save_new_item:
                saveItem();
                Intent i = new Intent(NewItemActivity.this, MainActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }

        return true;
    }

    //save todo item to db
    private void saveItem() {
        etTitle = (EditText) findViewById(R.id.etItemTitle);
        etNotes = (EditText) findViewById(R.id.etItemNotes);
        spPriority = (Spinner) findViewById(R.id.spPriority);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);

        //store date as string as activeandroid had issues with serializing date
        TodoItem newItem = new TodoItem(etTitle.getText().toString(), etNotes.getText().toString(), spPriority.getSelectedItemPosition(), calendar.getTime().toString(), isLocationSet, placeName, placeAddress, latitude, longitude);
        newItem.save();

        if (isLocationSet) {
            Log.i("SAG", "setting proximity alert for itemid: " + newItem.getId());
            saveProximityAlertPoint(latitude, longitude, newItem.getId());
        }

    }

    private void saveProximityAlertPoint(double latitude, double longitude, Long itemId) {

        Intent intent = new Intent(PROX_ALERT_INTENT);
        intent.putExtra("itemId", itemId);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, itemId.intValue(), intent, 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    10);
        }


        locationManager.addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                200, // the radius of the central point of the alert region, in meters
                -1, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );

        try{
        IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
        registerReceiver(new ProximityIntentReceiver(), filter);
            Log.i("SAG", "intent registerred " + latitude + " " + longitude + " itemid: " + itemId
            );
        }catch (SecurityException e){
            e.printStackTrace();
        }

    }

    public void navigateWithGMaps(View view){
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", latitude, longitude, placeName);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);

    }

    public void removeTodoLocation(View view) {
        isLocationSet = false;
        placeName = null;
        placeAddress = null;
        latitude = null;
        longitude = null;

        tvLocation.setText(R.string.location_filler);
        ivNavigate.setVisibility(View.INVISIBLE);
        ivDeleteLocation.setVisibility(View.INVISIBLE);
        ivGoogleStaticImgForLocation.setVisibility(View.INVISIBLE);

    }

    public void setLocation(View view) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
    }

    public void setDueDate(View view){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                new mDateSetListener(), year, month, day);
        dialog.show();

    }

    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int datePickerYear, int monthOfYear,
                              int dayOfMonth) {
            year = datePickerYear;
            month = monthOfYear;
            day = dayOfMonth;

            String dueDate = getStringForDate(year, month, day);
            tvCalendar.setText(dueDate);

        }
    }

    private String getStringForDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);
        Date date = calendar.getTime();
        return new SimpleDateFormat("EEE", Locale.ENGLISH).format(date.getTime())  + ", " + new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime()) + " " + new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime()) + " " + new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date.getTime());
    }

}
