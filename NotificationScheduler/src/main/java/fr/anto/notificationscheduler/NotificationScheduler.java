package fr.anto.notificationscheduler;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.UiThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class NotificationScheduler {

    /*
    Create a default NotificationChannel, because
    26+ need this, useful for dev who forgot to set this.
    Only called when channel_id == null
    */
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NOTIFICATIONS";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("DEFAULT_CHANNEL", name, importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private static void  dismiss(Context context,long notifId)
    {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel((int)notifId);
    }

    /** Cancel the scheduled notification and eventually dismiss it.
     * @param context Context of current app.
     * @param notificationId A id of notification.
     */
    public static void cancelAndDismiss(Context context,long notificationId){
        cancel(context,notificationId);
        dismiss(context,notificationId);
    }
    /** Cancel the scheduled notification.
     * @param context Context of current app.
     * @param notificationId A id of notification.
     */
    public static void cancel(Context context,long notificationId){
            NotificationDatabase.getInstance(context).notificationDAO().deleteNotification(notificationId);
    }
    /** Cancel all the notification with the group set to groupId.
     * @param context Context of current app.
     * @param groupId Name of a notification group.
     */
    public static void cancel(Context context,String groupId){
            NotificationDatabase.getInstance(context).notificationDAO().deleteGroup(groupId);
    }
    /** Cancel and eventually dismiss all the notification with the group set to groupId.
     * @param context Context of current app.
     * @param groupId Name of a notification group.
     */
    public static void cancelAndDismiss(Context context,String groupId){
        List<Notification> listNotification = NotificationDatabase.getInstance(context).notificationDAO().getBlockingGroupNotifications(groupId);
        cancel(context,groupId);
        for(Notification notif : listNotification)
        {
            dismiss(context,notif.id);
        }
    }

    protected static void scheduleNotification(Context context, long notificationId, long time) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationPublisher.class);
        intent.putExtra("notification_id",notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,(int)notificationId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time,pendingIntent);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,time,pendingIntent);
        }
        else
        {
            alarmManager.set(AlarmManager.RTC_WAKEUP,time,pendingIntent);
        }
    }
    protected static class Action{
        protected String actionIntent;
        protected String actionText;
        protected Boolean dismiss;
        protected Boolean collapse;

        protected Action(String actionIntent, String actionText, boolean dismiss, boolean collapse) {
            this.actionIntent = actionIntent;
            this.actionText = actionText;
            this.dismiss = dismiss;
            this.collapse = collapse;
        }
        protected Action(JSONObject obj)
        {
            try {
                actionIntent = obj.getString("intent");
            } catch (JSONException e) {
                actionIntent = "";
            }
            try {
                actionText = obj.getString("text");
            } catch (JSONException e) {
                actionText = "";
            }
            try {
                dismiss = obj.getBoolean("dismiss");
            } catch (JSONException e) {
                dismiss = false;
            }
            try {
                collapse = obj.getBoolean("collapse");
            } catch (JSONException e) {
                collapse = false;
            }
        }
        protected JSONObject toJSON()
        {
            JSONObject obj;
            try {
                obj = new JSONObject();
                obj.put("intent",actionIntent);
                obj.put("text",actionText);
                obj.put("dismiss",dismiss);
                obj.put("collapse",collapse);
            } catch (JSONException e) {
                obj = new JSONObject();
            }
            return obj;
        }
    }

    /**
     * A helper class to build a notification.
     * Build the notification and call build() to schedule.
     */
    public static class Builder{

        protected Context context;
        protected String title = "",content = "";
        protected Date time = new Date();
        protected List<Action> actions = new ArrayList<>();

        protected int color = -1;
        protected int small_icon = -1;
        protected int large_icon = -1;

        protected String channel_id,group="";
        protected String action_click = "";

        /** Create a Builder Object from the current context.
         * @param context Context of current app.
         */
        public Builder(Context context){
            this.context = context;
        }
        /** Set the icon of the notification.
         * If not set the notification will not show up.
         * After android 5.0 the icon must be white on transparent background.
         * @param small_icon Id of a resource.
         */
        public Builder small_icon(int small_icon){
            this.small_icon = small_icon;
            return this;
        }
        /** Set the large of the notification.
         * @param large_icon Id of a resource.
         */
        public Builder large_icon(int large_icon){
            this.large_icon = large_icon;
            return this;
        }

        /**
         * Set the icon and font color of the notification.
         * @param red Red proportion (between  and 255)
         * @param green Green proportion (between  and 255)
         * @param blue Blue proportion (between  and 255)
         * @param alpha Alpha proportion (between  and 255)
         */
        public Builder color(int red,int green, int blue,int alpha){
            int color = (alpha & 0xff) << 24 | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
            this.color = color;
            return this;
        }

        public Builder addAction(Intent intent,String text){
            return addAction(intent,text,true,true);
        }

        public Builder addAction(Intent intent,String text,boolean dismiss){
            return addAction(intent,text,dismiss,true);
        }

        public Builder addAction(Intent intent,String text,boolean dismiss,boolean collapse){
            /* To be sure user can start Activity from outside the app */
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            actions.add(new Action(intent.toUri(Intent.URI_ALLOW_UNSAFE),text,dismiss,collapse));
            return this;
        }

        public Builder time(Date time){
            this.time = time;
            return this;
        }

        public Builder time(Calendar time){
            this.time = time.getTime();
            return this;
        }

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }
        public Builder channelID(String idOfChannel)
        {
            this.channel_id = idOfChannel;
            return this;
        }
        public Builder group(String nameOfGroup)
        {
            this.group = nameOfGroup;
            return this;
        }
        public Builder setClickAction(Intent intent)
        {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.action_click = intent.toUri(Intent.URI_ALLOW_UNSAFE);
            return this;
        }

        @UiThread
        public long build(){
            Calendar cal = Calendar.getInstance();
            cal.setTime(time);
            if(channel_id == null)
            {
                createNotificationChannel(context);
                channel_id = "DEFAULT_CHANNEL";
            }
            /* Construct the array of actions */
            JSONArray arrOfActions = new JSONArray();
            for(int i = 0;i<actions.size();i++)
            {
                arrOfActions.put(actions.get(i).toJSON());
            }
            Notification notif = new Notification(0L,title,time.getTime(),arrOfActions.toString(),color,small_icon,large_icon,content,channel_id,group,action_click);
            /* insert the notification in the database */
            long ret = NotificationDatabase.getInstance(context).notificationDAO().insertNotification(notif);
            scheduleNotification(context,ret,cal.getTimeInMillis());
            return ret;
        }

    }

}
