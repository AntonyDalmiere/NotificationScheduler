package fr.anto.notificationscheduler

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * A private class used internally to start the scheduling of pending notifications.
 */
class Booter : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val listNotifications = NotificationDatabase.getInstance(context).notificationDAO().blockingAllNotifications
        for (notif in listNotifications) {
            NotificationScheduler.scheduleNotification(context, notif.id, notif.time)
        }
    }
}