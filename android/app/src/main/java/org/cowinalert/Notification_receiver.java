package  org.cowinalert;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class Notification_receiver extends BroadcastReceiver  {
    ArrayList<String> dataServer;
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String action = intent.getAction();
        if (action.contains("ZIPCODE")){
            String zipcode = action.split(":")[1];
            dataServer = this.sendPetition(zipcode);
        }
        else{
            String state = action.split("/")[0];
            String district = action.split("/")[1];
            dataServer = this.sendPetition(state,district);
        }
        // Needed a conditional to either crete notification or not depending on
        // whether was found a slot for vaccination or not → I guess

        Notification.Builder notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.vaccine_icon)
                .setContentTitle("Vaccine Available")
                .setContentText("Please check the app to find where you can get the vaccine")
                .setAutoCancel(true);

        notificationManager.notify(100,notification.build());
    }
    private ArrayList<String> sendPetition(String zipcode){
        ArrayList<String> data = new ArrayList<String>();
        //Excute petition to server to execute python script
        return data;
    }
    private ArrayList<String> sendPetition(String state, String district){
        ArrayList<String> data = new ArrayList<String>();
        //Excute petition to server to execute python script
        return data;
    }

}
