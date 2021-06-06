package Utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.cowinalert.MainActivity;

import java.util.HashMap;
import java.util.Map;

import Classes.LocalUser;
import Interfaces.FirebaseInterface;

import static org.cowinalert.MainActivity.TAG;

public class FirebaseCalls {

    private FirebaseFirestore db;
    private FirebaseInterface firebaseInterface;

    public FirebaseCalls(FirebaseInterface firebaseInterface){
        db = FirebaseFirestore.getInstance();
        this.firebaseInterface =  firebaseInterface;
    }

    public void createUserForTheFirstTime(String uid){
        Map<String, Object> payload = new HashMap<>();
        payload.put(LocalUser.KEY_UID , uid);
        db.collection("users")
                .document(uid)
                .set(payload)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"User created for the first time");
                        firebaseInterface.userCreatedForTheFirstTime(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating user", e);
                        firebaseInterface.userCreatedForTheFirstTime(false);
                    }
                });
    }
    public void uploadLocalUserData(LocalUser user){
        db.collection("users")
                .document(user.getUid())
                .update(user.getLocalUserForFirebase())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"User data updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating user data", e);
                    }
                });
    }

    public void updateUserToken(String userUID, String token){

        Map<String, Object> payload = new HashMap<>();
        payload.put(LocalUser.KEY_TOKEN , token);

        db.collection("users")
                .document(userUID)
                .update(payload)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"User token updated");
                        firebaseInterface.tokenSuccessfullyUpdated(token);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating user token", e);
                    }
                });
    }

    public  void getUserDataFromFirebase(String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        firebaseInterface.onGetUserDataSuccess(document);
                    } else {
                        Log.d(TAG, "No such document");
                        firebaseInterface.onGetUserDataSuccess(null);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}
