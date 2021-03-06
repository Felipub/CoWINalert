package org.cowinalert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import Classes.LocalUser;
import Interfaces.FirebaseInterface;
import Utils.FirebaseCalls;
import Utils.jsonUtils;

public class MainActivity extends AppCompatActivity implements FirebaseInterface {

    public static final String TAG                = "CowinAlarm:::";
    public static final int    RC_SIGN_IN         = 0;
    public static final String PREFS              = "cowin_prefs";
    public static final String PREFS_ACTUAL_TOKEN = "actual_token";
    public static final String PREFS_LAST_TOKEN   = "last_token";

    //Objects from layout
    EditText etZipcode;
    Button button_letMeKnow;
    Switch plus18, plus45, covishield, covaxin, sputnikv, free, paid;
    Spinner sDistrict, sState;
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

    HashMap<String, Integer> districtsValues       = new HashMap<>();
    HashMap<String, Integer> statesValues          = new HashMap<>();
    HashMap<Integer, String> valuesDistricts       = new HashMap<>();
    HashMap<Integer, String> valuesStates          = new HashMap<>();
    HashMap<String, List<String>> statesDistrictis = new HashMap<>();

    ArrayAdapter<String> statesAdapter;
    ArrayAdapter<String> districtAdapter;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs  = getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE);
        editor = prefs.edit();

        setContentView(R.layout.activity_main);
        etZipcode = (EditText) findViewById(R.id.zipcode);
        sDistrict = (Spinner) findViewById(R.id.district);
        sState = (Spinner) findViewById(R.id.state);
        button_letMeKnow = (Button)findViewById(R.id.button_letMeKnow);
        plus18 = (Switch) findViewById(R.id.switch_plus18);
        plus45 = (Switch) findViewById(R.id.switch_plus45);
        covishield = (Switch) findViewById(R.id.switch_coviShield);
        covaxin = (Switch) findViewById(R.id.switch4_covaxin);
        sputnikv = (Switch) findViewById(R.id.switch_sputnikv);
        free = (Switch) findViewById(R.id.switch_free);
        paid = (Switch) findViewById(R.id.switch_paid);

        JSONArray listStates = jsonUtils.getListOfStatesAndDistricts(this);
        districtsValues = new HashMap<>();
        statesValues = new HashMap<>();
        valuesDistricts = new HashMap<>(); //aggg no me gusta esto pero...ahora mismo no se me ocurre otra cosa :/
        valuesStates = new HashMap<>();
        statesDistrictis = new HashMap<>();
        List<String> states =  new ArrayList<String>();

        for (int i=0; i < listStates.length(); i++) {
            try {
                JSONObject state = listStates.getJSONObject(i);

                String state_name = state.getString("state_name");
                int state_id = state.getInt("state_id");
                states.add(state_name);
                statesValues.put(state_name,state_id);
                valuesStates.put(state_id,state_name);

                List<String> districts =  new ArrayList<String>();
                JSONArray jsonStateDistricts = state.getJSONArray("districts");
                for(int j=0; j < jsonStateDistricts.length(); j++) {
                    String district_name = jsonStateDistricts.getJSONObject(j).getString("district_name");
                    int district_id = jsonStateDistricts.getJSONObject(j).getInt("district_id");
                    districts.add(district_name);
                    districtsValues.put(district_name,district_id);
                    valuesDistricts.put(district_id,district_name);
                }
                statesDistrictis.put(state_name,districts);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        statesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, states);
        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sState.setAdapter(statesAdapter);
        sState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                fullfillDistrictsFromState((String)parentView.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


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
                localUser.setDistrict(districtsValues.get(sDistrict.getSelectedItem().toString()));
                localUser.setState(statesValues.get(sState.getSelectedItem().toString()));
                localUser.setDose1(false);
                localUser.setDose2(false);
                localUser.setNotify(false);
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
            setupUIFromLocalUser();
            userTokenCheck();
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

        if(data==null){
            Log.d(TAG,"User not in ddbb");
            firebaseCalls.createUserForTheFirstTime(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }else {
            Log.d(TAG, "Hey, wait a second, we already have a user on the ddbb, get his/her data back " + data.getData().toString());
            localUser.setLocalUserFromFirebase(data.getData());
            userTokenCheck();
            setupUIFromLocalUser();
        }
    }

    @Override
    public void tokenSuccessfullyUpdated(String token) {
        Log.d(TAG,"TOKEN successfully saved in Firebase");
        editor.putString(MainActivity.PREFS_LAST_TOKEN, token);
        editor.commit();
    }

    @Override
    public void userCreatedForTheFirstTime(boolean successfully) {
       if(successfully){
           userTokenCheck();
       }
    }

    private void fullfillDistrictsFromState(String state){
        districtAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, statesDistrictis.get(state));
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sDistrict.setAdapter(districtAdapter);
    }

    int lazyAuxIndexPosition;
    private void setupUIFromLocalUser(){
        //etDistrict.setText(localUser.getDistrict()==-1?"":String.valueOf(localUser.getDistrict()));
        //etState   .setText(localUser.getState()==-1?"":String.valueOf(localUser.getState()));
        etZipcode .setText(localUser.getPin()==-1?"":String.valueOf(localUser.getPin()));
        plus18.setChecked(localUser.isPlus18());
        plus45.setChecked(localUser.isPlus45());
        covaxin.setChecked(localUser.isCovaxin());
        sputnikv.setChecked(localUser.isSputnikV());
        covishield.setChecked(localUser.isCovishield());
        free.setChecked(localUser.isFree());
        paid.setChecked(localUser.isPaid());

        String userState = valuesStates.get(((Long)localUser.getState()).intValue());
        lazyAuxIndexPosition = statesAdapter.getPosition(userState);
        sState.setSelection(lazyAuxIndexPosition);

        fullfillDistrictsFromState(userState);
        String userDistrict = valuesDistricts.get(((Long)localUser.getDistrict()).intValue());
        lazyAuxIndexPosition = districtAdapter.getPosition(userDistrict);
        sDistrict.postDelayed(new Runnable() {
            public void run() {
                sDistrict.setSelection(lazyAuxIndexPosition);
            }
        }, 100);
    }

    private void userTokenCheck(){
        if(localUser==null) return;

        Log.d(TAG,"Checking User Token");
        String actualToken = prefs.getString(PREFS_ACTUAL_TOKEN,"-1");
        String lastToken   = prefs.getString(PREFS_LAST_TOKEN  ,"-1");

        if(actualToken.equalsIgnoreCase("-1")){
            //It is the user first time, so go and get the token
            Log.d(TAG,"Checking User Token - first time");
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.d(TAG, "Checking User Token - Fetching FCM registration token failed", task.getException());
                                retryUserTokenCheck(1000);
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();

                            editor.putString(MainActivity.PREFS_ACTUAL_TOKEN, token);
                            editor.commit();
                            firebaseCalls.updateUserToken(localUser.getUid(), token);
                            Log.d(TAG,"Checking User Token - saved successfully");
                        }
                    });

        }else if(!actualToken.equalsIgnoreCase(lastToken)){
            //update
            Log.d(TAG,"Checking User Token - It has changed, lets update it");
            firebaseCalls.updateUserToken(localUser.getUid(), actualToken);
            retryUserTokenCheck(5000); //just in case
        }else{
            Log.d(TAG,"Checking User Token - everything sweet and smooth");
        }
    }


    private void retryUserTokenCheck(long milliseconds){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                userTokenCheck();
            }
        }, milliseconds);
    }


}