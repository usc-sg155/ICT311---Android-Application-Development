package com.example.mycheckins;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.BatchUpdateException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegDetailActivity extends AppCompatActivity {
    EditText etTitle;
    EditText etPlace;
    EditText etDetail;
    Button btnPickDate;
    TextView tvDate;
    TextView tvLocation;
    Button btnShowMap;
    ImageView ivPhoto;
    Button btnTakePhoto;
    Button btnShare;
    Button btnDelete;

    Calendar c;
    DatePickerDialog dpd;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath = "";

    int actState = 0;
    int locState = 0;
    double latitude = 0.0;
    double longtitude = 0.0;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            if (locState == 1)
                return;
            latitude = location.getLatitude();
            longtitude = location.getLongitude();
            tvLocation = (TextView) findViewById(R.id.locationView);
            String lon = String.valueOf(longtitude);
            String lat = String.valueOf(latitude);
            tvLocation.setText("Latitude:" + lat + "\nLongtitude:" + lon);
            locState = 1;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_detail);

        Intent in = getIntent();
        String title = in.getStringExtra("title");
        if (title.equals("showRegAct")) {
            //Toast.makeText(this, "Equals", Toast.LENGTH_SHORT).show();
            pickUpDate();
            locationStamp();
            prepareTakePhoto();
            prepareShare();
            btnDelete = (Button) findViewById(R.id.btnDelete);
            btnDelete.setVisibility(View.GONE);
            actState = 1;
        } else {
            loadFromDatabase(title);
            actState = 2;
        }
        prepareMapShow();
        locState = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (actState != 1)
            return;
        etTitle = (EditText) findViewById(R.id.titleInput);
        etPlace = (EditText) findViewById(R.id.placeInput);
        etDetail = (EditText) findViewById(R.id.detailInput);
        tvDate = (TextView) findViewById(R.id.dateView);
        tvLocation = (TextView) findViewById(R.id.locationView);

        String title = etTitle.getText().toString().isEmpty()?" ":etTitle.getText().toString();
        String place = etPlace.getText().toString().isEmpty()?" ":etPlace.getText().toString();
        String detail = etDetail.getText().toString().isEmpty()?" ":etDetail.getText().toString();
        String date = tvDate.getText().toString().isEmpty()?" ":tvDate.getText().toString();
        String location = tvLocation.getText().toString().isEmpty()?" ":tvLocation.getText().toString();
        currentPhotoPath = currentPhotoPath.isEmpty()?" ":currentPhotoPath;

        DBManager dbMan = new DBManager(getApplicationContext());
        dbMan.InsertNewRecord(title, place, detail, date, location, currentPhotoPath);
        //Toast.makeText(this, "Destroyed", Toast.LENGTH_SHORT).show();

        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
    }

    private void loadFromDatabase(String title) {
        DBManager dbMan = new DBManager(getApplicationContext());
        final String[] res = dbMan.GetOneRecord(title);
        if (res == null)
            return;
        etTitle = (EditText) findViewById(R.id.titleInput);
        etPlace = (EditText) findViewById(R.id.placeInput);
        etDetail = (EditText) findViewById(R.id.detailInput);
        tvDate = (TextView) findViewById(R.id.dateView);
        tvLocation = (TextView) findViewById(R.id.locationView);
        ivPhoto = (ImageView) findViewById(R.id.imageView);
        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
        btnShare = (Button) findViewById(R.id.btnShare);
        btnPickDate = (Button) findViewById(R.id.btnPickDate);

        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBManager dbMan = new DBManager(getApplicationContext());
                dbMan.DeleteOneRow(res[0], res[1], res[2]);
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
            }
        });

        etTitle.setText(res[0]);
        etPlace.setText(res[1]);
        etDetail.setText(res[2]);
        tvDate.setText(res[3]);
        tvLocation.setText(res[4]);
        String imgPath = res[5];

        ivPhoto.setImageBitmap(BitmapFactory.decodeFile(imgPath));
        etTitle.setEnabled(false);
        etPlace.setEnabled(false);
        etDetail.setEnabled(false);
        btnPickDate.setEnabled(false);
        btnTakePhoto.setEnabled(false);
        btnShare.setVisibility(View.GONE);
    }

    private void pickUpDate() {
        tvDate = (TextView) findViewById(R.id.dateView);
        btnPickDate = (Button) findViewById(R.id.btnPickDate);

        c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        tvDate.setText(year + "/" + (month + 1) + "/" + day);

        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                dpd = new DatePickerDialog(RegDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        tvDate.setText(mYear + "/" + (mMonth + 1) + "/" + mDay);
                    }
                }, year, month, day);
                dpd.show();
            }
        });
    }

    private  void locationStamp(){
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
    }

    private void prepareTakePhoto() {
        ivPhoto = (ImageView) findViewById(R.id.imageView);
        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivPhoto.setImageBitmap(imageBitmap);
            saveBitmap(imageBitmap);
        }
    }

    private File saveBitmap(Bitmap bmp) {
        String extStorageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        OutputStream outStream = null;
        // String temp = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File file = new File(extStorageDirectory, imageFileName);

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        currentPhotoPath = file.getAbsolutePath();
        return file;
    }

    private void prepareShare() {
        btnShare = (Button) findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);

                etTitle = (EditText) findViewById(R.id.titleInput);
                etPlace = (EditText) findViewById(R.id.placeInput);
                etDetail = (EditText) findViewById(R.id.detailInput);
                tvDate = (TextView) findViewById(R.id.dateView);

                String msg;
                msg = "Title : " + etTitle.getText().toString() + "\n";
                msg += "Place : " + etPlace.getText().toString() + "\n";
                msg += "Details : " + etDetail.getText().toString() + "\n";
                msg += "Date : " + tvDate.getText().toString();

                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }
    double lat;
    double lon;

    private void prepareMapShow() {
        btnShowMap = (Button) findViewById(R.id.btnShowMap);
        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLocation = (TextView) findViewById(R.id.locationView);
                String latlong = tvLocation.getText().toString();
                latlong = latlong.replace("Latitude:","");
                latlong = latlong.replace("Longtitude:","");
                String[] str = latlong.split("\n");
                Toast.makeText(getApplicationContext(), str[0] + str[1], Toast.LENGTH_SHORT).show();

                lat = Double.parseDouble(str[0]);
                lon = Double.parseDouble(str[1]);
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", lat, lon);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
                //Intent showMapsActivity = new Intent(getApplicationContext(), MapsActivity.class);
                //startActivity(showMapsActivity);
            }
        });
    }
}
