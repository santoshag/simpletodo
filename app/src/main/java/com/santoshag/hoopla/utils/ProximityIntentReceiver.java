package com.santoshag.hoopla.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.activeandroid.query.Select;
import com.santoshag.hoopla.R;
import com.santoshag.hoopla.activities.NewItemActivity;
import com.santoshag.hoopla.models.TodoItem;

public class ProximityIntentReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Long itemId = intent.getLongExtra("itemId", 0);

        Log.i("SAG", "received " + intent + " itemid:" + itemId);
        TodoItem todoItem = (TodoItem) new Select().from(TodoItem.class)
                .where("id = ?", itemId).executeSingle();
        if (itemId == -1 || todoItem == null){
            return;
        }


        Boolean entering = intent.getBooleanExtra(key, false);
        if (entering) {
            Log.d(getClass().getSimpleName(), "entering");
        } else {
            Log.d(getClass().getSimpleName(), "exiting");
        }


        // prepare intent which is triggered if the
        // notification is selected

        Intent notificationIntent = new Intent(context, NewItemActivity.class);
        notificationIntent.putExtra("dbItemIndex", itemId);
        notificationIntent.putExtra("editItem", true);

        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notificationIntent, 0);


        int color = context.getResources().getColor(R.color.app_icon_color);
        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(context)
                .setContentTitle(todoItem.title)
                .setContentText("Task reminder based on your location")
                .setSmallIcon(R.drawable.ic_launcher)
                .setColor(color)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(itemId.intValue(), n);

//        Intent i = new Intent();
//        i.set("com.santoshag.hoopla.activities", "ViewTodoItemActivity");
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.putExtra("dbItemIndex", itemId);
//        context.startActivity(i);

    }
}