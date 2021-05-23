package com.example.cowin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

public class Notification_receiver extends BroadcastReceiver  {
    ArrayList<String> dataServer;
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.contains("ZIPCODE")){
            String zipcode = action.split(":")[1];
            System.out.println(action);
            //dataServer = this.sendPetition(zipcode);
        }
        else{
            String state = action.split("/")[0];
            String district = action.split("/")[1];
            System.out.println(action);
            //dataServer = this.sendPetition(state,district);
        }
        // Needed a conditional to either crete notification or not depending on
        // whether was found a slot for vaccination or not → I guess

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context,"channelId")
                .setSmallIcon(R.drawable.vaccine_icon)
                .setContentTitle("Vaccine Available")
                .setContentText("Please check the app to find where you can get the vaccine")
                //.setWhen(1000)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(100, notification.build());

        System.out.println("Set Notification");
    }
    private ArrayList<String> sendPetition(String zipcode){
        ArrayList<String> data = new ArrayList<String>();
        //Execute petition to server to execute python script
        return data;
    }
    private ArrayList<String> sendPetition(String state, String district){
        ArrayList<String> data = new ArrayList<String>();
        //Execute petition to server to execute python script
        return data;
    }

}
