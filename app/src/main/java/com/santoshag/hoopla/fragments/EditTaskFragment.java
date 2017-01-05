package com.santoshag.hoopla.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.santoshag.hoopla.R;
import com.santoshag.hoopla.activities.MainActivity;
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
// ...

public class EditTaskFragment extends DialogFragment {

    EditText etTitle;
    EditText etNotes;
    Spinner spPriority;
    TodoItem todoItem;
    TextView tvCalendar;
    TextView tvLocation;
    ImageView ivLocation;
    ImageView ivNavigate;
    ImageView ivCalendar;
    ImageView ivDeleteLocation;
    ImageView ivGoogleStaticImgForLocation;
    Boolean isLocationSet;
    int year;
    int month;
    int day;
    String placeAddress;
    String placeName;
    Double latitude;
    Double longitude;
    LocationManager locationManager;

    int PLACE_PICKER_REQUEST = 1;
    private static final String PROX_ALERT_INTENT =
            "com.santoshag.hoopla.ProximityAlert";

    public EditTaskFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }


    public static EditTaskFragment newInstance(Long editTaskId) {
        EditTaskFragment frag = new EditTaskFragment();
        Bundle args = new Bundle();
        args.putLong("editTaskId", editTaskId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_item, container);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        todoItem = (TodoItem) new Select().from(TodoItem.class)
                .where("id = ?", getArguments().getLong("editTaskId")).executeSingle();


    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnSave = (Button) view.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem(view);
            }
        });

        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog(view);
            }
        });


        etTitle = (EditText) view.findViewById(R.id.etItemTitle);
        etNotes = (EditText) view.findViewById(R.id.etItemNotes);
        spPriority = (Spinner) view.findViewById(R.id.spPriority);
        tvCalendar = (TextView) view.findViewById(R.id.tvCalendar);
        ivCalendar = (ImageView) view.findViewById(R.id.ivCalendar);
        tvLocation = (TextView) view.findViewById(R.id.tvLocation);
        ivLocation = (ImageView) view.findViewById(R.id.ivLocation);
        ivNavigate = (ImageView) view.findViewById(R.id.ivNavigate);
        ivDeleteLocation = (ImageView) view.findViewById(R.id.ivDeleteLocation);
        ivGoogleStaticImgForLocation = (ImageView) view.findViewById(R.id.ivGoogleStaticImgForLocation);


        etTitle.setText(todoItem.title);
        etNotes.setText(todoItem.notes);
        spPriority.setSelection(todoItem.priority);
        isLocationSet = todoItem.isLocationSet;

        String dueDateString = todoItem.dueDate;
        DateFormat df = new SimpleDateFormat("EEE MMM dd h:mm:ss z yyyy", Locale.ENGLISH);
        Date dueDate;
        try {
            dueDate = df.parse(dueDateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dueDate);

            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            String month = (String) android.text.format.DateFormat.format("MM", dueDate); //06
            String year = (String) android.text.format.DateFormat.format("yyyy", dueDate); //2013
            String day = (String) android.text.format.DateFormat.format("dd", dueDate); //20
            String dueDateStr = getStringForDate(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
            tvCalendar.setText(dueDateStr);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        ivCalendar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");

            }
        });

        ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setLocation(view);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
            }
        });



        ivDeleteLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeTodoLocation(view);
            }
        });

        if(isLocationSet){

            placeName = todoItem.placeName;
            placeAddress = todoItem.placeAddress;
            latitude = todoItem.latitude;
            longitude = todoItem.longitude;
            tvLocation.setText(placeName + "\n" + placeAddress);

            setImageViewLocationAndOptions();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                isLocationSet = true;
                Place place = PlacePicker.getPlace(data, getActivity());

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


    public void setLocation(View view) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
    }

    public void saveItem(View view) {
        etTitle = (EditText) view.findViewById(R.id.etItemTitle);
        etNotes = (EditText) view.findViewById(R.id.etItemNotes);
        spPriority = (Spinner) view.findViewById(R.id.spPriority);



        todoItem.title = etTitle.getText().toString();
        todoItem.notes = etNotes.getText().toString();
        todoItem.priority = spPriority.getSelectedItemPosition();
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);
        todoItem.dueDate = calendar.getTime().toString();
        todoItem.isLocationSet = isLocationSet;
        todoItem.placeName = placeName;
        todoItem.placeAddress = placeAddress;
        todoItem.latitude = latitude;
        todoItem.longitude = longitude;

        todoItem.save();
        if (isLocationSet) {
            Toast.makeText(getActivity(), "itemid:" + todoItem.getId(), Toast.LENGTH_SHORT);
            saveProximityAlertPoint(latitude, longitude, todoItem.getId());
        }

        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        this.dismiss();

    }

    private void saveProximityAlertPoint(double latitude, double longitude, Long itemId) {

        Intent intent = new Intent(PROX_ALERT_INTENT);
        intent.putExtra("itemId", itemId);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    10);
        }

        LocationManager mLocationManager;

        locationManager.addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                200, // the radius of the central point of the alert region, in meters
                -1, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );

        try {
            IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
            getActivity().registerReceiver(new ProximityIntentReceiver(), filter);
            Log.i("SAG", "intent registerred " + latitude + " " + longitude);
        } catch (SecurityException e) {
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

    @SuppressLint("ValidFragment")
    public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            year = yy;
            month = mm;
            day = dd;
            tvCalendar.setText(getStringForDate(year, month, day));
        }

    }

    private static String getStringForDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);
        Date date = calendar.getTime();
        return new SimpleDateFormat("EEE", Locale.ENGLISH).format(date.getTime())  + ", " + new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime()) + " " + new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime()) + " " + new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date.getTime());

    }


    public void cancelDialog(View view) {
        this.dismiss();
    }
}