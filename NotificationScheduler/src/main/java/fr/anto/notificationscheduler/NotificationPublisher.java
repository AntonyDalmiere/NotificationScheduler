package fr.anto.notificationscheduler;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URISyntaxException;


public class NotificationPublisher extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.i("NotificationScheduler","Notification publisher started");
        long notificationId = Long.valueOf(intent.getStringExtra("notification_id"));
        Notification notification = NotificationDatabase.getInstance(context).notificationDAO().getNotification(notificationId);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,notification.channel_id);

        if(notification.small_icon != -1) {
            mBuilder.setSmallIcon(notification.small_icon);
        }else{
            Log.e("NotificationScheduler","Small icon not set");
            return;
        }
        if(notification.large_icon != -1) {
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), notification.large_icon);
            mBuilder.setLargeIcon(largeIcon);
        }
        mBuilder.setContentTitle(notification.title);
        mBuilder.setContentText(notification.content);
        mBuilder.setColor(notification.color);
        mBuilder.setVibrate(new long[] { 1000,1000,1000 });

        JSONArray arr;
        try {
             arr = new JSONArray(notification.actions);
        } catch (JSONException e) {
            arr = new JSONArray();
        }

        for (int i = 0; i < arr.length(); i++) {
            NotificationScheduler.Action act;
            try {
                act = new NotificationScheduler.Action(arr.getJSONObject(i));
            } catch (JSONException e) {
                break;
            }
            Intent tent = new Intent(context,ActionReceiver.class);
                tent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                tent.putExtra("_id",notificationId);
                tent.putExtra("index",i);
                tent.putExtra("action",act.actionIntent);
                tent.putExtra("collapse",act.collapse);
                tent.putExtra("dismiss",act.dismiss);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,(int)notificationId*3+i,tent,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.addAction(notification.small_icon,act.actionText,pendingIntent);
        }
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setAutoCancel(true);

        if(! notification.group_id.equals("")) {
            mBuilder.setGroup(notification.group_id);
        }
        if(! notification.click_intent.equals("") )
        {
            try {
                Intent intentClick = Intent.parseUri(notification.click_intent,Intent.URI_ALLOW_UNSAFE);
                intentClick.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingintentClick = PendingIntent.getActivity(context,0,intentClick,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(pendingintentClick);
            } catch (URISyntaxException e) {
                Log.e("BetterNotify","Cant create clickIntent with intent");
            }
        }
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
        mNotificationManager.notify((int)notificationId, mBuilder.build());
        NotificationScheduler.cancel(context,notificationId);
    }


}