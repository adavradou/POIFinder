package com.example.unipitouristapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class UpdateActivity extends AppCompatActivity {

    private Button updateButton, selectButton;
    private EditText poiSearchText, poiCategory, poiLatitude, poiLongitude, poiDescription, poiTitle, poiUrl;
    DatabaseReference reff;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        reff = FirebaseDatabase.getInstance().getReference().child("POI");

        //Initialize all UI elements.
        poiSearchText = (EditText) findViewById(R.id.poiUpdateEditText);
        poiCategory = (EditText) findViewById(R.id.poiTitleEditText);
        poiLatitude = (EditText) findViewById(R.id.poiLatitudeEditText);
        poiLongitude = (EditText) findViewById(R.id.poiLongitudeEditText);
        poiDescription = (EditText) findViewById(R.id.poiCategoryEditText);
        poiTitle = (EditText) findViewById(R.id.poiDescriptionEditText);
        poiUrl = (EditText) findViewById(R.id.poiUrlEditText);
        updateButton = (Button) findViewById(R.id.updateButton);
        selectButton = (Button) findViewById(R.id.selectButton);

        updateButton.setVisibility(View.INVISIBLE);
        hideFields();

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPoi();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePoi();
            }
        });
    }

    //Searches for the name of the node the user typed and returns the corresponding data in the EditTexts, if exists.
    private void selectPoi() {

        String poiEditText = poiSearchText.getText().toString().trim();

        if (poiEditText.length() == 0) {
            Toast.makeText(UpdateActivity.this, "Please fill all the fields.", Toast.LENGTH_SHORT).show();
        } else {

            try{

                listener = reff.child(poiEditText).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            poiTitle.setText(snapshot.child("title").getValue().toString());
                            poiLatitude.setText(snapshot.child("latitude").getValue().toString());
                            poiLongitude.setText(snapshot.child("longitude").getValue().toString());
                            poiCategory.setText(snapshot.child("category").getValue().toString());
                            poiDescription.setText(snapshot.child("description").getValue().toString());
                            poiUrl.setText(snapshot.child("url").getValue().toString());

                            updateButton.setVisibility(View.VISIBLE);
                            selectButton.setVisibility(View.INVISIBLE);
                            showFields();
                            setFocus();
                        }
                        else{
                            Toast.makeText(UpdateActivity.this, "No entry found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


            }catch (Exception e){
                Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    //Updates the note in the Firebase with the new entries inside the EditTexts.
    private void updatePoi() {

        String poitEditText = poiSearchText.getText().toString().trim();
        String poiCategoryEditText = poiCategory.getText().toString().trim();
        String poiLatitudeEditText = poiLatitude.getText().toString().trim();
        String poiLongitudeEditText = poiLongitude.getText().toString().trim();
        String poiDescriptionEditText = poiDescription.getText().toString().trim();
        String poiTitleEditText = poiTitle.getText().toString().trim();
        String poiUrlEditText = poiUrl.getText().toString().trim();


        //Validate that there are no empty fields.
        if ((poitEditText.length() == 0 || poitEditText.length() > 50) || (poiCategoryEditText.length() == 0 || poiCategoryEditText.length() > 50) ||
                (poiLatitudeEditText.length() == 0 || poiLatitudeEditText.length() > 50) || (poiLongitudeEditText.length() == 0 || poiLongitudeEditText.length() > 50) ||
                (poiDescriptionEditText.length() == 0 || poiDescriptionEditText.length() > 250) ||
                (poiTitleEditText.length() == 0 || poiTitleEditText.length() > 50) || (poiUrlEditText.length() == 0 || poiUrlEditText.length() > 500)) {
            Toast.makeText(UpdateActivity.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();

        } else {

            try{

                reff.child(poitEditText).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        dataSnapshot.getRef().child("category").setValue(poiCategory.getText().toString().trim());
                        dataSnapshot.getRef().child("latitude").setValue(poiLatitude.getText().toString().trim());
                        dataSnapshot.getRef().child("longitude").setValue(poiLongitude.getText().toString().trim());
                        dataSnapshot.getRef().child("description").setValue(poiDescription.getText().toString().trim());
                        dataSnapshot.getRef().child("title").setValue(poiTitle.getText().toString().trim());
                        dataSnapshot.getRef().child("url").setValue(poiUrl.getText().toString().trim());

                        Toast.makeText(UpdateActivity.this, "Data has been updated Successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                updateButton.setVisibility(View.INVISIBLE);
                selectButton.setVisibility(View.VISIBLE);
                hideFields();

            }catch (Exception e){
                Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    //according to life-cycle activity stop Listener
    @Override
    public void onStop() {
        super.onStop();
        if (listener != null) {
            reff.removeEventListener(listener);
        }
    }

    //according to life-cycle activity start Listener again on Activity Restart
    @Override
    public void onRestart() {
        super.onRestart();
        reff.addValueEventListener(listener);
    }

    //Show the EditTexts containing the POI's data.
    private void showFields(){
        poiSearchText.setVisibility(View.INVISIBLE);
        poiTitle.setVisibility(View.VISIBLE);
        poiLatitude.setVisibility(View.VISIBLE);
        poiLongitude.setVisibility(View.VISIBLE);
        poiCategory.setVisibility(View.VISIBLE);
        poiDescription.setVisibility(View.VISIBLE);
        poiUrl.setVisibility(View.VISIBLE);
    }

    //Hide the EditTexts containing the POI's data.
    private void hideFields(){
        poiSearchText.setVisibility(View.VISIBLE);
        poiTitle.setVisibility(View.INVISIBLE);
        poiLatitude.setVisibility(View.INVISIBLE);
        poiLongitude.setVisibility(View.INVISIBLE);
        poiCategory.setVisibility(View.INVISIBLE);
        poiDescription.setVisibility(View.INVISIBLE);
        poiUrl.setVisibility(View.INVISIBLE);
    }

    //Focus on the EditTexts.
    private void setFocus(){
        poiTitle.setFocusable(true);
        poiLatitude.setFocusable(true);
        poiLongitude.setFocusable(true);
        poiCategory.setFocusable(true);
        poiDescription.setFocusable(true);
        poiUrl.setFocusable(true);
        poiTitle.setFocusableInTouchMode(true);
        poiLatitude.setFocusableInTouchMode(true);
        poiLongitude.setFocusableInTouchMode(true);
        poiCategory.setFocusableInTouchMode(true);
        poiDescription.setFocusableInTouchMode(true);
        poiUrl.setFocusableInTouchMode(true);
    }
}
