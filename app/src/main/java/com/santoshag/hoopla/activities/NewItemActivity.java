package com.santoshag.hoopla.activities;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.ribell.colorpickerview.ColorPickerView;
import com.ribell.colorpickerview.interfaces.ColorPickerViewListener;
import com.santoshag.hoopla.R;
import com.santoshag.hoopla.adapters.CustomTodoItemAdapter;
import com.santoshag.hoopla.models.TodoItem;
import com.santoshag.hoopla.utils.ProximityIntentReceiver;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class NewItemActivity extends AppCompatActivity implements ColorPickerViewListener {
    private static final String TAG = "NewItemActivity";
    EditText etTitle;
    EditText etNotes;
    DatePicker dpDueDate;
    LocationManager locationManager;
    String placeAddress;
    String placeName;
    int year;
    int month;
    int day;
    int priority_color = 0;
    Double latitude;
    Double longitude;
    TextView tvCalendar;
    TextView tvLocation;
    ImageView ivNavigate;
    ImageView ivDeleteLocation;
    ImageView ivGoogleStaticImgForLocation;
    Boolean isLocationSet = false;
    Long itemId;
    Long dbItemIndex;
    TodoItem todoItem;
    boolean editItem;
    boolean changed = false;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.ivLogo)
    ImageView ivLogo;


    int PLACE_PICKER_REQUEST = 1;
    private static final String PROX_ALERT_INTENT =
            "com.santoshag.hoopla.ProximityAlert";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        ButterKnife.bind(this);

        etTitle = (EditText) findViewById(R.id.etItemTitle);
        etNotes = (EditText) findViewById(R.id.etItemNotes);
        tvCalendar = (TextView) findViewById(R.id.tvCalendar);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        ivNavigate = (ImageView) findViewById(R.id.ivNavigate);
        ivDeleteLocation = (ImageView) findViewById(R.id.ivDeleteLocation);
        ivGoogleStaticImgForLocation = (ImageView) findViewById(R.id.googleStaticImgForLocation);
        ColorPickerView colorPickerView = (ColorPickerView) findViewById(R.id.nsvSpectrum);
        colorPickerView.setListener(this);
//        colorPickerView.onClick(colorPickerView.getRootView(),(ViewGroup) colorPickerView.getRootView(), 0);

        onColorPickerClick(0);

        initDueDate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
         editItem = getIntent().getBooleanExtra("editItem", false);

        if (editItem) {
            dbItemIndex = getIntent().getLongExtra("dbItemIndex", -1);
            todoItem = (TodoItem) new Select().from(TodoItem.class)
                    .where("id = ?", dbItemIndex).executeSingle();
            toolbarTitle.setText(todoItem.title);
            populateItemFields();
            ivLogo.setImageResource(R.drawable.circle3);
            ivLogo.setColorFilter(ContextCompat.getColor(this,CustomTodoItemAdapter.getPriorityColor(todoItem.priority)));

        }else{
            toolbarTitle.setText("New to-do");
            ivLogo.setImageResource(R.drawable.circle3);
            ivLogo.setColorFilter(ContextCompat.getColor(this,CustomTodoItemAdapter.getPriorityColor(2)));
        }
    }

    private void populateItemFields() {
        etTitle.setText(todoItem.title);
        etNotes.setText(todoItem.notes);
//        todoItem.dueDate = calendar.getTime().toString();
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

        isLocationSet = todoItem.isLocationSet;
        if (isLocationSet) {

            placeName = todoItem.placeName;
            placeAddress = todoItem.placeAddress;
            latitude = todoItem.latitude;
            longitude = todoItem.longitude;

            LinearLayout llLocation = (LinearLayout) findViewById(R.id.llLocation);
            llLocation.setVisibility(View.VISIBLE);
            TextView tvLocation = (TextView) findViewById(R.id.tvLocation);
            ivNavigate = (ImageView) findViewById(R.id.ivNavigate);
            ivGoogleStaticImgForLocation = (ImageView) findViewById(R.id.googleStaticImgForLocation);
            tvLocation.setText(todoItem.placeName + "\n" + todoItem.placeAddress);

            if (latitude != null && longitude != null) {
                setImageViewLocationAndOptions();
            }
        }

//        todoItem.isLocationSet = isLocationSet;
//        todoItem.placeName = placeName;
//        todoItem.placeAddress = placeAddress;
//        todoItem.latitude = latitude;
//        todoItem.longitude = longitude;


    }

        private void initDueDate() {
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

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    private void setImageViewLocationAndOptions() {
        AsyncTask<Void, Void, Bitmap> setImageFromUrl = new AsyncTask<Void, Void, Bitmap>() {
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
                if (bmp != null) {
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
                saveItem(null);
                break;
            default:
                break;
        }

        return true;
    }

    public void cancelCreate(View view) {
        finish();
    }

    public void saveItem(View view) {

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);

        String title = etTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            title = "New to-do";

        }

        if(editItem) {
            todoItem.title = title;
            todoItem.notes = etNotes.getText().toString();
            todoItem.dueDate = calendar.getTime().toString();
            todoItem.isLocationSet = isLocationSet;
            todoItem.priority = priority_color;
            todoItem.placeName = placeName;
            todoItem.placeAddress = placeAddress;
            todoItem.latitude = latitude;
            todoItem.longitude = longitude;
            //isLocationSet, placeName, placeAddress, latitude, longitude
            todoItem.save();
        }else {

            //store date as string as activeandroid had issues with serializing date
            todoItem = new TodoItem(title, etNotes.getText().toString(), priority_color, calendar.getTime().toString(), isLocationSet, placeName, placeAddress, latitude, longitude);
            todoItem.save();
        }

        if (isLocationSet && changed) {
            itemId = todoItem.getId();
            getPermissionToAccessFineLocation();
            Log.i("SAG", "setting proximity alert for itemid: " + todoItem.getId());
        } else {

            Intent i = new Intent(NewItemActivity.this, MainActivity.class);
            startActivity(i);
        }

    }


    // Identifier for the permission request
    private static final int ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST = 1;

    // Called when the user is performing an action which requires the app to read the
    // user's contacts
    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToAccessFineLocation() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Access location permission granted", Toast.LENGTH_SHORT).show();
                saveProximityAlertPoint(latitude, longitude, itemId);
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = false;

                if (showRationale) {
                    // do something here to handle degraded mode
                } else {
                    Toast.makeText(this, "Access location permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void saveProximityAlertPoint(double latitude, double longitude, Long itemId) {

        Intent intent = new Intent(PROX_ALERT_INTENT);
        intent.putExtra("itemId", itemId);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, itemId.intValue(), intent, 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }


        locationManager.addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                200, // the radius of the central point of the alert region, in meters
                -1, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );

        try {
            IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
            registerReceiver(new ProximityIntentReceiver(), filter);
            Log.i("SAG", "intent registerred " + latitude + " " + longitude + " itemid: " + itemId
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(NewItemActivity.this, MainActivity.class);
        startActivity(i);

    }

    public void navigateWithGMaps(View view) {
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
        if(editItem){
            changed = true;
        }
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
    }

    public void setDueDate(View view) {
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

    private String getStringForDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);
        Date date = calendar.getTime();
//        String dueDateText = stringMonth + " " + ordinal(Integer.parseInt(day)) + ", " + year;

        return new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime()) + " " + CustomTodoItemAdapter.ordinal(Integer.parseInt(new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime()))) + ", " + new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date.getTime());
    }

    @Override
    public void onColorPickerClick(int colorPosition) {

        priority_color = colorPosition;
        ivLogo.setColorFilter(ContextCompat.getColor(this,CustomTodoItemAdapter.getPriorityColor(priority_color)));

    }

}
