package com.apapps.event;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.internal.zzt.TAG;

public class TabFragment1 extends Fragment{

    RecyclerView RV;
    EventAdapter adapter;
    List<Event> eventList;
    private ProgressBar progressBar;
    private TextView text;

    public TabFragment1(){

    }

    @SuppressLint("ValidFragment")
    public TabFragment1(Context context){
        super();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.activity_tab_fragment1, container, false);

        RV = (RecyclerView) root.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar)root.findViewById(R.id.progressBar);
        text = (TextView)root.findViewById(R.id.text);
        progressBar.setVisibility(View.VISIBLE);
        eventList = new ArrayList<>();
        adapter = new EventAdapter(getContext(),eventList, true);
        RV.setLayoutManager(new LinearLayoutManager(getContext()));
        RV.setAdapter(adapter);
        readEvents();

        return root;
    }



    private void readEvents(){
        new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String currentDate = DateFormat.getDateTimeInstance().format(new Date());
        DatabaseReference dbInstance = FirebaseDatabase.getInstance().getReference("events");
                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                    Event event = singleSnapshot.getValue(Event.class);
                                    eventList.add(event);
                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                            else{
                                progressBar.setVisibility(View.GONE);
                                text.setVisibility(View.VISIBLE);
                                //Toast.makeText(getContext(),"No events to show.",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "Cancelled", databaseError.toException());
                        }
                    };

        dbInstance.orderByChild("eventDate").startAt(currentDate).addListenerForSingleValueEvent(eventListener);

        //Event a = new Event("Event 1","12-12-2020","New Delhi","NIL");
        //eventList.add(a);
        //Event b = new Event("Event 2","12-12-2020","New Delhi","NIL");
        //eventList.add(b);
    }



}
