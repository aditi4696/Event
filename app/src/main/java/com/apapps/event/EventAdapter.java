package com.apapps.event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.apapps.event.R.id.attend;

/**
 * Created by Aditi on 4/6/2017.
 */

class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder>{

    private Context mContext;
    private List<Event> eventList;
    private boolean showAttending;
    private FirebaseDatabase Instance;
    private DatabaseReference DbInstance, eventRef, iconRef;
    private ImageView attendingIcon;
    private int count;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView event_name, event_date, attending_count, attendText;
        public ImageView thumbnail, attend, showVenue;
        public MyViewHolder(View view) {
            super(view);
            event_name = (TextView) view.findViewById(R.id.event_name);
            event_date = (TextView) view.findViewById(R.id.event_date);
            attending_count = (TextView) view.findViewById(R.id.attending_count);
            attend = (ImageView) view.findViewById(R.id.attend);
            attendText = (TextView) view.findViewById(R.id.attendText);
            showVenue = (ImageView) view.findViewById(R.id.show_venue);
            thumbnail = (ImageView) view.findViewById(R.id.event_photo);
        }
    }

        public EventAdapter(Context mContext, List<Event> eventList, boolean showAttending) {
            this.mContext = mContext;
            this.eventList = eventList;
            this.showAttending = showAttending;
        }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_event, parent, false);
        attendingIcon = (ImageView) itemView.findViewById(attend);
        Instance = FirebaseDatabase.getInstance();
        //Context context =  parent.getContext();
        //Fragment fragment1 = ((Activity)context).getFragmentManager().findFragmentById(R.id.TabFragment1);
        //if(fragment1.getClass().equals(TabFragment1.class)){
        if(!showAttending)
        attendingIcon.setVisibility(View.GONE);


        //}

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Toast.makeText(mContext,"No. of events- "+eventList.size(),Toast.LENGTH_SHORT).show();
        final Event event = eventList.get(position);
        holder.event_name.setText(event.getName());
        holder.event_date.setText(event.getEventDate());
        holder.attending_count.setText(String.valueOf(event.getAttendeesCount()));

        count = event.getAttendeesCount();
        final String eventId = event.geteId();
        eventRef = Instance.getReference("events");
        iconRef = Instance.getReference("users");
        DbInstance = Instance.getReference("attendees");


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uId = user.getUid();
        iconRef.child(uId).child("attending").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        //Toast.makeText(mContext,"Key- " + data.getKey(),Toast.LENGTH_SHORT).show();
                        if (data.getKey().equals(eventId)) {
                            holder.attend.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
                            holder.attendText.setText("Going");
                            holder.attend.setEnabled(false);
                        }
                    }
                } else {
                    Toast.makeText(mContext, "unable to retrieve", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.attend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //edit DB. Increment the count. Add attendee details
                //Toast.makeText(mContext,"Attending..."+eventId,Toast.LENGTH_SHORT).show();
                count = count + 1;
                holder.attend.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
                holder.attendText.setText("Going");
                holder.attending_count.setText(String.valueOf(event.getAttendeesCount() + 1));
                holder.attend.setEnabled(false);
                addAttendee(eventId, count);
                //change the imageview to a different icon
                //attendingLocal.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
                //attendingLocal.setEnabled(false);
            }
        });

        holder.showVenue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String address = event.getVenue();
                //markOnMap(getLocationFromAddress(address));
                Intent intent = new Intent(mContext, ShowVenue.class);
                intent.putExtra("address", address);
                mContext.startActivity(intent);
            }
        });
    }




    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private void addAttendee(final String eventId, final int count){


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uId = user.getUid();
        new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    mContext.startActivity(new Intent(mContext, Login.class));
                    ((Activity)mContext).finish();
                }
            }
        };

        if(user!=null) {
            DatabaseReference PushInstance = DbInstance.push();
            String aId = PushInstance.getKey();
            final Attendees newAttendee = new Attendees(aId,eventId,uId);
            DbInstance.child(aId).setValue(newAttendee).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Map<String,Object> taskMap = new HashMap<>();
                        taskMap.put("attendeesCount",count);
                        eventRef.child(eventId).updateChildren(taskMap);
                        Toast.makeText(mContext, "You're Attending.", Toast.LENGTH_SHORT).show();
                        onAttendeeAdded(newAttendee);
                    } else {
                        Toast.makeText(mContext, "Failed to add you to attendee's list. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void onAttendeeAdded(Attendees attendee){
        DbInstance = Instance.getReference("events");
        String uId = attendee.getUserId();
        String eId = attendee.getEventId();
        DbInstance.child(eId).child("attendees").child(uId).setValue(true);
        DbInstance = Instance.getReference("users");
        DbInstance.child(uId).child("attending").child(eId).setValue(true);
    }


    }
