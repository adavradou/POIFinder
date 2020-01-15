package com.example.unipitouristapp;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PoiDetails extends AppCompatActivity { //Display all the POI's details.

    DatabaseReference reff;
    ImageView imageView;
    TextView titleTextView, latTextView, longTextView, categoryTextView, descriptionTextView;
    String markerTitle;

    private MyTts myTts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_details);

        markerTitle = getIntent().getStringExtra("my message");

        imageView = findViewById(R.id.image_view);

        titleTextView = findViewById(R.id.titleTextView);
        latTextView = findViewById(R.id.latTextView);
        longTextView = findViewById(R.id.longTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

        reff = FirebaseDatabase.getInstance().getReference("POI");

        getFirebaseEntries();

        myTts = new MyTts(this);

    }


    public void getFirebaseEntries(){

        Query query = FirebaseDatabase.getInstance().getReference("POI").orderByChild("title").equalTo(markerTitle);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) { //For each node of tree

                        //Get all values of the Marker clicked.
                        String poiTitle = (String) postSnapshot.child("title").getValue();
                        String poiLatitude = (String) postSnapshot.child("latitude").getValue();
                        String poiLongitude = (String) postSnapshot.child("longitude").getValue();
                        String poiCategory = (String) postSnapshot.child("category").getValue();
                        String poiDescription = (String) postSnapshot.child("description").getValue();
                        String poiUrl = (String) postSnapshot.child("url").getValue();

                        //Show on the activity.
                        titleTextView.setText(poiTitle);
                        latTextView.setText(poiLatitude);
                        longTextView.setText(poiLongitude);
                        categoryTextView.setText(poiCategory);
                        descriptionTextView.setText(poiDescription);
                        Picasso.with(getBaseContext()).load(poiUrl).fit().centerCrop().into(imageView);
                    }
                }
                else{
                    Toast.makeText(PoiDetails.this,"No POI found with that title", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void audioButtonMethod(View view){
        String text = descriptionTextView.getText().toString();

        myTts.speak(text); //The description will be heard in audio.

    }
}
