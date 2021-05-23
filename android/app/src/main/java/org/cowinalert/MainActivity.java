package org.cowinalert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.List;

import Classes.LocalUser;
import Interfaces.FirebaseInterface;
import Utils.FirebaseCalls;

public class MainActivity extends AppCompatActivity implements FirebaseInterface {

    public static final String TAG = "CowinAlarm:::";
    public static final int RC_SIGN_IN = 0;

    //Objects from layout
    EditText etZipcode, etDistrict, etState;
    Button button_letMeKnow;
    Switch plus18, plus45, covishield, covaxin, sputnikv, free, paid;
    // Storing the dta from objects
    String district,state,zipcode;
    // Others
    Boolean searchZipcode;
    private final long TIMER = 60*1000; //milliseconds
    Intent intent;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    private LocalUser localUser;
    private FirebaseCalls firebaseCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etZipcode = (EditText) findViewById(R.id.zipcode);
        etDistrict = (EditText) findViewById(R.id.district);
        etState = (EditText) findViewById(R.id.state);
        button_letMeKnow = (Button)findViewById(R.id.button_letMeKnow);
        plus18 = (Switch) findViewById(R.id.switch_plus18);
        plus45 = (Switch) findViewById(R.id.switch_plus45);
        covishield = (Switch) findViewById(R.id.switch_coviShield);
        covaxin = (Switch) findViewById(R.id.switch4_covaxin);
        sputnikv = (Switch) findViewById(R.id.switch_sputnikv);
        free = (Switch) findViewById(R.id.switch_free);
        paid = (Switch) findViewById(R.id.switch_paid);

        button_letMeKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localUser.setPlus18(plus18.isChecked());
                localUser.setPlus45(plus45.isChecked());
                localUser.setCovishield(covishield.isChecked());
                localUser.setCovaxin(covaxin.isChecked());
                localUser.setSputnikV(sputnikv.isChecked());
                localUser.setFree(free.isChecked());
                localUser.setPaid(paid.isChecked());
                localUser.setPin(etZipcode.getText().toString().equalsIgnoreCase("") ? -1 : Integer.valueOf(etZipcode.getText().toString()));
                localUser.setDistrict(etDistrict.getText().toString().equalsIgnoreCase("") ? -1 : Integer.valueOf(etDistrict.getText().toString()));
                localUser.setState(etState.getText().toString().equalsIgnoreCase("") ? -1 : Integer.valueOf(etState.getText().toString()));
                firebaseCalls.uploadLocalUserData(localUser);
            }
        });


        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        firebaseCalls = new FirebaseCalls(this);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            //Launch Auth
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build()/*,
                    new AuthUI.IdpConfig.FacebookBuilder().build(),
                    new AuthUI.IdpConfig.TwitterBuilder().build()*/);

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }else{
            localUser = new LocalUser(this, FirebaseAuth.getInstance().getCurrentUser().getUid());
            Log.d(TAG,localUser.toString());
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                localUser = new LocalUser(this,FirebaseAuth.getInstance().getCurrentUser().getUid());
                Log.d(TAG,localUser.toString());
                firebaseCalls.getUserDataFromFirebase(localUser.getUid());
                Toast.makeText(this, getString(R.string.toast_login_successfully), Toast.LENGTH_LONG).show();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this, getString(R.string.toast_login_error), Toast.LENGTH_LONG).show();
                if(response!=null) Log.e(TAG, response.getError().getMessage());
            }
        }
    }

    @Override
    public void onGetUserDataSuccess(DocumentSnapshot data) {
        Log.d(TAG,"Hey, wait a second, we already have a user on the ddbb, get his/her data back "+data.getData().toString());
        localUser.setLocalUserFromFirebase(data.getData());
    }


}