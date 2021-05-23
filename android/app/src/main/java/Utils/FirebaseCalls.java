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

    public void uploadLocalUserData(LocalUser user){

        db.collection("users")
                .document(user.getUid())
                .set(user.getLocalUserForFirebase())
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
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}
