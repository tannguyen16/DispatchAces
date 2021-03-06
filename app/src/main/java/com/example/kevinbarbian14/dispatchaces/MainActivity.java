package com.example.kevinbarbian14.dispatchaces;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {
    private ListView list;
    private DatabaseReference currentRides;
    private DatabaseReference activeRides;
    private DatabaseReference flagStatus;
    private Button add_btn;
    private EditText email_text;
    private EditText from_text;
    private EditText to_text;
    private EditText num_riders;
    private EditText waitTime;
    private ListView activeList;
    private ToggleButton status;
    private boolean statusFlag;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = findViewById(R.id.list);
        activeList = findViewById(R.id.active);
        status = (ToggleButton) findViewById(R.id.status);
//        add_btn = findViewById(R.id.add_btn);
//        email_text = findViewById(R.id.email_text);
//        from_text = findViewById(R.id.start_text);
//        to_text = findViewById(R.id.end_text);
//        num_riders = findViewById(R.id.num_riders);
        //list.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        currentRides = FirebaseDatabase.getInstance().getReference().child("CURRENT RIDES");
        activeRides = FirebaseDatabase.getInstance().getReference().child("ACTIVE RIDES");
        flagStatus = FirebaseDatabase.getInstance().getReference().child("STATUS");
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean statusFlag = preferences.getBoolean("ref",true);  //default is true
        Log.d("MSG",String.valueOf(statusFlag));
        if (statusFlag == true) //if (tgpref) may be enough, not sure
        {
            status.setChecked(true);
            status.setBackgroundColor(Color.GREEN);

        }
        else
        {
            Log.d("MSG","SUCCESS");
            status.setChecked(false);
            status.setBackgroundColor(Color.RED);

        }
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                if (status.isChecked()){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("ref", true); // value to store
                    editor.commit();
                    status.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    Log.d("MSG",String.valueOf(status.isChecked()));
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("ref", false); // value to store
                    editor.commit();
                    status.setBackgroundColor(Color.RED);
                }
                }
        });
        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    flagStatus.child("FLAG").setValue("ON");
                    // The toggle is enabled
                } else {
                    flagStatus.child("FLAG").setValue("OFF");
                    // The toggle is disabled
                }
            }
        });

       // archivedRides = FirebaseDatabase.getInstance().getReference();
        activeRides.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<RideInfo> temp = new ArrayList<RideInfo>();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    RideInfo rider = shot.getValue(RideInfo.class);
                    temp.add(rider);
                }
                RideAdapter arrayAdapter = new RideAdapter(getBaseContext(), temp, false);
                //arrayAdapter.notifyDataSetChanged();
                activeList.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        currentRides.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<RideInfo> temp = new ArrayList<RideInfo>();
                    for (DataSnapshot shot : dataSnapshot.getChildren()) {
                        RideInfo rider = shot.getValue(RideInfo.class);
                        temp.add(rider);
                    }
                    RideAdapter arrayAdapter = new RideAdapter(getBaseContext(), temp,true);

                    //arrayAdapter.notifyDataSetChanged();
                    list.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        add_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String email = email_text.getText().toString().replace(".",",");
//                String from = from_text.getText().toString();
//                String to = to_text.getText().toString();
//                String riders = num_riders.getText().toString();
//                HashMap<String,String> addInfo = new HashMap<String,String>();
//                addInfo.put("Email",email);
//                addInfo.put("From",from);
//                addInfo.put("To",to);
//                addInfo.put("Number of riders",riders);
//                currentRides.child(email).setValue(addInfo);
//
//            }
//        });
//        for (int i = 0; i < 10; i++){
//            HashMap<String,String> addInfo = new HashMap<String,String>();
//            addInfo.put("Email","Email" + i);
//            addInfo.put("From","FROM" +i);
//            addInfo.put("To","TO" + i);
//            addInfo.put("Number of riders","RIDERS" + i);
//            currentRides.child("Email" + i).setValue(addInfo);
//    }

    }

    private void deleteUser(RideInfo rider){
        currentRides.child(rider.getEmail()).setValue(null);
        DatabaseReference archivedRides = FirebaseDatabase.getInstance().getReference().child("ARCHIVED RIDES");
        archivedRides.child(rider.getEmail()).setValue(rider);
    }
    private void clearArchives() {
        DatabaseReference archivedRides = FirebaseDatabase.getInstance().getReference().child("ARCHIVED RIDES");
        archivedRides.setValue(null);
    }



}