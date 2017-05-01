package com.apapps.event;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import static com.apapps.event.R.id.venue;

public class NewEvent extends AppCompatActivity {

    private EditText eventName, eventDate, venu, eventDescription;
    private DatabaseReference DbInstance;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        eventName = (EditText) findViewById(R.id.eventName);
        eventDate = (EditText) findViewById(R.id.eventDate);
        venu = (EditText) findViewById(venue);
        eventDescription = (EditText) findViewById(R.id.eventDescription);
        Button addDetails = (Button) findViewById(R.id.btn_done);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        DbInstance = instance.getReference("events");
        instance.getReference("app_title").setValue("Database");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("Create New Event");

        addDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eName = eventName.getText().toString();
                String eDate = eventDate.getText().toString();
                String venue = venu.getText().toString();
                String eDesc = eventDescription.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                createEvent(eName, eDate, venue, eDesc);
            }
        });


        venu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPlace(v);
            }
        });
    }

    public void createEvent(String eName, String eDate, String venue, String eDesc) {


        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(NewEvent.this, Login.class));
                    finish();
                }
            }
        };

        if(user!=null){
            String uId = user.getUid();
            DatabaseReference PushInstance = DbInstance.push();
            String eId = PushInstance.getKey();
            Event event = new Event(eId, eName, eDate, venue, eDesc, uId);
            DbInstance.child(eId).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(NewEvent.this, "Event created!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(NewEvent.this, HomeScreen.class));
                    } else {
                        Toast.makeText(NewEvent.this, "Failed to create event. Please try again later.", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public void findPlace(View view) {

        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());

                ((EditText) findViewById(R.id.venue))
                        .setText(place.getName()+",\n"+
                                place.getAddress());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
