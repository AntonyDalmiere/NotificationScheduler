package fr.anto.notificationscheduler;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;
/**
 * A private class used internally to start the scheduling of pending notifications.
 */
public class Booter extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        List<Notification> listNotifications = NotificationDatabase.getInstance(context).notificationDAO().getBlockingAllNotifications();
        for(Notification notif : listNotifications)
        {
            NotificationScheduler.scheduleNotification(context,notif.id,notif.time);
        }
    }
}
