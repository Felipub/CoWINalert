package Interfaces;

import com.google.firebase.firestore.DocumentSnapshot;

public interface FirebaseInterface {
    public void onGetUserDataSuccess(DocumentSnapshot data);
    public void tokenSuccessfullyUpdated(String token);
    public void userCreatedForTheFirstTime(boolean successfully);
}
