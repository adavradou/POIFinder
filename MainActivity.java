package com.example.unipitouristapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {


    final static int REQUESTCODE = 324;
    LocationManager locationManager;
    DatabaseReference reff;
    POI poi;
    private GoogleMap mMap;
    EditText radiusEditText;
    Button updateButton;
    int selectedRadius = 0;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    String TAG = "MapActivity";
    float DEFAULT_ZOOM = 3f;
    Circle circle;
    private Marker marker;
    String markerTitle, previousMarkerTitle = "";
    PoiLogDbHelper mDbHelper;

    public static boolean showButton = false; //The update button is hidden, till the admin logins.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radiusEditText = findViewById(R.id.radiusEditText);
        updateButton = findViewById(R.id.updateButton);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mDbHelper = new PoiLogDbHelper(getApplicationContext());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        reff = FirebaseDatabase.getInstance().getReference().child("POI");
        insertFireBaseData(); //Call it to add entries to the Firebase.

    }


    @Override
    protected void onResume() {
        if (showButton){
            updateButton.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    private void getDeviceLocation() { //Get the location of the device.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task location = mFusedLocationProviderClient.getLastLocation();
            ((Task) location).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                DEFAULT_ZOOM);
                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(MainActivity.this, "unable to get current location",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException:" + e.getMessage());
        }
    }


    private void moveCamera(LatLng latLng, float zoom) { //Camera is always moved at the device's position.
        Log.d(TAG, "moveCamera: moving camera to: lat: " + latLng.latitude + "long: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    //Request permission to use the GPS.
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this, "GPS is already on.", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    this);
        } else
            Toast.makeText(this, "Permission needed before starting the app.",
                    Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();

        this.mMap = googleMap;

        getDeviceLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);
    }

    public void okButtonMethod (View view){

        //Check if permission is granted, otherwise ask for permission.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUESTCODE);
        }else{
            Toast.makeText(this,"Permission already Granted!",Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,
                    this);
            this.onLocationChanged(null);
        }

        try {
            selectedRadius = Integer.parseInt(radiusEditText.getText().toString());
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        Toast.makeText(MainActivity.this, "Current notification radius is: " + selectedRadius,
                Toast.LENGTH_SHORT).show();
    }


    public void onLocationChanged(Location location) {


        if (location == null) {
        } else {

            mMap.clear();

            createCircle(location.getLatitude(), location.getLongitude());
            checkPoiDistances();
        }
    }

    //Create the notification radius on the map.
    public void createCircle(double poiLat, double poiLong){
        this.circle = this.mMap.addCircle(new CircleOptions()
                .center(new LatLng(poiLat, poiLong))
                .radius(selectedRadius)
                .strokeWidth(0f)
                .fillColor(0x550000FF));
    }

    //Check if there is a POI inside the notification radius.
    public void checkPoiDistances(){

        reff.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                float minPOIDistance = selectedRadius+1; //This will make sure, that only the closest POI is shown.
                double minPOILatitude = 0;
                double minPOILongitude = 0;
                String minPOITitle = "", minPOICategory = "";

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) { //For each node of tree

                    double poiLatitude = Double.parseDouble((String) postSnapshot.child("latitude").getValue());
                    double poiLongitude = Double.parseDouble((String) postSnapshot.child("longitude").getValue());
                    String poiTitle = (String) postSnapshot.child("title").getValue();
                    String poiCategory = (String) postSnapshot.child("category").getValue();

                    float[] distance = new float[2];

                    Location.distanceBetween(poiLatitude, poiLongitude, circle.getCenter().latitude,
                            circle.getCenter().longitude, distance);

                    if (distance[0] > circle.getRadius()) { //Outside of radius.
                        //Toast.makeText(getBaseContext(), "Out of range", Toast.LENGTH_SHORT).show();

                    } else { //Inside radius.
                        //Toast.makeText(getBaseContext(), "A POI found in: " + distance[0] + "m",
                        //Toast.LENGTH_LONG).show();
                        if (distance[0] < minPOIDistance){ //Keep the data of the closest POI.
                            minPOIDistance = distance[0];
                            minPOILatitude = poiLatitude;
                            minPOILongitude = poiLongitude;
                            minPOITitle = poiTitle;
                            minPOICategory = poiCategory;
                        }
                    }
                }

                addPoiMarker(minPOILatitude, minPOILongitude, circle.getCenter().latitude, circle.getCenter().longitude, minPOITitle, minPOIDistance, minPOICategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Add a marker on the map of the closest POI.
    public void addPoiMarker(double poiLat, double poiLong, double userLat, double userLong, String title, float minDistance, String category){

        mMap.clear();

        createCircle(userLat, userLong);
        markerTitle = title;

        LatLng markerPosition = null;

        markerPosition = new LatLng(poiLat, poiLong);
        marker = mMap.addMarker(new MarkerOptions().position(markerPosition));

        //Check if this POI is the same as the one found before. If yes, do nothing, otherwise, write to db.
        if (previousMarkerTitle != title){
            previousMarkerTitle = title;
            Toast.makeText(getBaseContext(), "A POI found in: " + minDistance + "m",Toast.LENGTH_SHORT).show();
            writeToPoiLogDb(poiLat, poiLong, category);
        }
    }

    //Create a new POI object of the closest POI found.
    public void writeToPoiLogDb(double poiLat, double poiLong, String category){

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        double currentLatitude = poiLat;
        double currentLongitude = poiLong;

        PoiLog poiLog = new PoiLog(String.valueOf(markerTitle) , String.valueOf(category)
                , String.valueOf(currentLatitude), String.valueOf(currentLongitude)
                , String.valueOf(Calendar.getInstance().getTime()));

        addNewEntry(poiLog);
        Toast.makeText(this, "Entry added!", Toast.LENGTH_SHORT).show();

    }

    //Add the closest POI found to the local db.
    public void addNewEntry(PoiLog poiLog){

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PoiLogDbContract.PoiLogEntry.COLUMN_NAME_POITITLE, poiLog.getPoiLog_title());
        values.put(PoiLogDbContract.PoiLogEntry.COLUMN_NAME_POICATEGORY, poiLog.getPoiLog_category());
        values.put(PoiLogDbContract.PoiLogEntry.COLUMN_NAME_LATITUDE, poiLog.getPoiLog_latitude());
        values.put(PoiLogDbContract.PoiLogEntry.COLUMN_NAME_LONGITUDE, poiLog.getPoiLog_longitude());
        values.put(PoiLogDbContract.PoiLogEntry.COLUMN_NAME_TIMESTAMP, poiLog.getPoiLog_timestamp());

        //Inserting Row
        db.insert(PoiLogDbContract.PoiLogEntry.TABLE_NAME, null, values);
        db.close();
    }


    @Override
    public boolean onMarkerClick(Marker marker) { //When clicking on the marker, go to another activity.
        poiDetailsActivity(markerTitle); //Go to PoiDetails activity.
        return false;
    }

    public void poiDetailsActivity(String title) { //This activity contains the details of a POI.
        Intent intent = new Intent(getApplicationContext(), PoiDetails.class);
        intent.putExtra("my message", title);
        startActivity(intent);
    }

    //Show statistics of the POIs found.
    public void statisticsButtonMethod(View view){

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        long numRows = DatabaseUtils.queryNumEntries(db, "poiLogDetails"); //Get number of entries in local db.

        String stringMessage = "You have found " + numRows + " POIs.\n\n";

        //For each category show the corresponding percentage.
        List<String> PoiCategoriesList = new ArrayList<String>();
        PoiCategoriesList.add("Food");
        PoiCategoriesList.add("Sightseeing");
        PoiCategoriesList.add("Coffee");
        PoiCategoriesList.add("Fun");

        for(String category : PoiCategoriesList){

            Cursor cursor = db.rawQuery("Select * from poiLogDetails where PoiCategory = ? ",
                    new String[]{category});
            int total_count=cursor.getCount();

            if (total_count != 0){
                BigDecimal BigDec1 = new BigDecimal(total_count*100);
                BigDecimal BigDec2 = new BigDecimal(numRows);
                BigDecimal percentage = BigDec1.divide(BigDec2);

                stringMessage = stringMessage + percentage + "% of them were " + category + "\n";
            }
        }


        messageShow(stringMessage);
    }

    //Display the statistics in an Alert Dialog.
    public void messageShow(String message){
        AlertDialog.Builder abuilder;
        abuilder = new AlertDialog.Builder(this);
        abuilder.setTitle("Statistics");
        abuilder.setMessage(message);
        abuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = abuilder.create();
        dialog.show();
    }

    public void updateButtonMethod(View view){
        updateActivity(); //Go to UpdateActivity activity.
    }

    public void updateActivity() {
        Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
        startActivity(intent);
    }

    public void authenticationButtonMethod(View view){
        loginActivity(); //Go to AdminLogin activity.
    }

    public void loginActivity() {
        Intent intent = new Intent(getApplicationContext(), AdminLogin.class);
        startActivity(intent);
    }

    //This is called to create the Firebase entries.
    public void insertFireBaseData(){
        poi = new POI("Mikro Cafe", "35.516808", "24.025209",
                "Coffee", "Coffee house with a very nice view in front of the sea, " +
                "located at the “Koum Kapi” area of Chania.",
                "https://www.zarpanews.gr/wp-content/uploads/2016/09/kolona-2.jpg");
        reff = FirebaseDatabase.getInstance().getReference().child("POI");
        reff.child("POI1").setValue(poi);

        poi = new POI("Bougatsa Iordanis", "35.513202", "24.019830",
                "Food", "A traditional “bougatsa” made of Mizithra cheese. " +
                "It has been working since 1924!",
                "http://www.iordanis.gr/files/photo/Optimized010.jpg");
        reff = FirebaseDatabase.getInstance().getReference().child("POI");
        reff.child("POI2").setValue(poi);

        poi = new POI("Thalassino Ageri", "35.520072", "24.037467",
                "Food", "A traditional greek “taverna”, next to the sea, " +
                "where you can eat fresh fish.",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS0BeXoxdS3AUhvXFOAwC6A0E5nQzfxlgKVLiQDb6InBq2gyTOQ9A");
        reff = FirebaseDatabase.getInstance().getReference().child("POI");
        reff.child("POI3").setValue(poi);

        poi = new POI("Venizelos Graves", "35.525153", "24.056097",
                "Sightseeing", "The graves of Eleftherios and Sofoklis Venizelos. " +
                "They are located inside a wonderful park with a panoramic view of Chania.",
                "https://starcretetransfers.gr/wordpress/wp-content/uploads/2017/03/chania-venizelos-graves-4.jpg");
        reff = FirebaseDatabase.getInstance().getReference().child("POI");
        reff.child("POI4").setValue(poi);

        poi = new POI("The lighthouse of Chania", "35.519635", "24.016776",
                "Sightseeing", "The original Venetian lighthouse was built around the late " +
                "16th century to protect the harbour.",
                "https://live.staticflickr.com/4472/26174182869_32247287c2_b.jpg");
        reff = FirebaseDatabase.getInstance().getReference().child("POI");
        reff.child("POI5").setValue(poi);

        poi = new POI("Paintland Chania", "35.491570", "24.041231",
                "Fun", "Paintland Chania is the only organized paintball field in the city of Chania. " +
                "In covers an area of 11 acres and includes a total of 3 different fields.",
                "https://media-cdn.tripadvisor.com/media/photo-s/07/d9/03/12/paintland-chania-paintball.jpg");
        reff = FirebaseDatabase.getInstance().getReference().child("POI");
        reff.child("POI6").setValue(poi);
    }




    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}


