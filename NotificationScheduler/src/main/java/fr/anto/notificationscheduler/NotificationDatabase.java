package fr.anto.notificationscheduler;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Notification.class}, version = 1, exportSchema = false)
public abstract class NotificationDatabase extends RoomDatabase {
    /* Shared instance */
    private static volatile NotificationDatabase INSTANCE;

    protected abstract NotificationDAO notificationDAO();

    private final static String DATABASE_FILENAME = "listNotification.db";
    public static NotificationDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (NotificationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NotificationDatabase.class, DATABASE_FILENAME).allowMainThreadQueries().build();//We allow run query on UI threads because we make query at phone startup.
                }
            }
        }
        return INSTANCE;
    }



}
