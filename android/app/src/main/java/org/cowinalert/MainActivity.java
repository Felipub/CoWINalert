package org.cowinalert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "CowinAlarm:::";
    //Objects from layout
    EditText etZipcode, etDistrict, etState;
    Button buttonZipcode, buttonCity;
    // Storing the dta from objects
    String district,state,zipcode;
    // Others
    Boolean searchZipcode;
    private final long TIMER = 60*1000; //milliseconds
    Intent intent;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etZipcode = (EditText) findViewById(R.id.zipcode);
        etDistrict = (EditText) findViewById(R.id.district);
        etState = (EditText) findViewById(R.id.state);
        buttonZipcode = (Button)findViewById(R.id.buttonZipcode);
        buttonCity = (Button)findViewById(R.id.buttonCity);

        buttonZipcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel previous alarm and update with new parameters
                if (alarmManager != null){
                    alarmManager.cancel(pendingIntent);
                }
                zipcode = etZipcode.getText().toString();
                // Intent needed to send and recieve information from Notification_reciever
                // Notification_receiver =  class to implement the python script on server
                //                          & creates the alarm that will appear on top
                intent = new Intent(getApplicationContext(), Notification_receiver.class);
                intent.setAction("ZIPCODE:"+ zipcode);
                pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),100,intent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,TIMER,TIMER,pendingIntent);
                if (pendingIntent != null && alarmManager != null) {
                    alarmManager.cancel(pendingIntent);
                }

            }

        });

        buttonCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel previous alarm and update with new parameters
                if (alarmManager != null){
                    alarmManager.cancel(pendingIntent);
                }
                district = etDistrict.getText().toString();
                state = etState.getText().toString();

            }
        });



        //hello world firestore
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        // Add a new document with a generated ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        System.out.println(":::!");
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(":::no");
                        Log.w(TAG, "Error adding document", e);
                    }
                });




    }
    protected void setAlarm(String dataSend){

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}