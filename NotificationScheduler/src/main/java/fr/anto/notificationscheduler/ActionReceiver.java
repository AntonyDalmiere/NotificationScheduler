package fr.anto.notificationscheduler;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * A private class used internally when users interact with the notification.
 */
public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /* Get information about the clicked notification */
        String notificationId = intent.getStringExtra("_id");
        String action = intent.getStringExtra("action");

        /* Try to launch the intent received */
        try {
            Intent tent = Intent.parseUri(action, 0);
            context.startActivity(tent);
        }catch (Exception e){
            Log.e("NotificationScheduler",e.toString());
        }

        /* if collapse is true hide the notification bar */
        if(intent.getBooleanExtra("collapse",true)) {
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }

        /* if dismiss is true hide the notification */
        if(intent.getBooleanExtra("dismiss",true)){
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(Integer.parseInt(notificationId));
        }
    }
}

