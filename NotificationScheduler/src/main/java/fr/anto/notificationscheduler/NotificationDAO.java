package fr.anto.notificationscheduler;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotificationDAO {
    @Query("SELECT * FROM Notification WHERE id = :id")
    Notification getNotification(long id);

    @Query("SELECT * FROM Notification")
    List<Notification> getBlockingAllNotifications();

    @Query("SELECT * FROM Notification WHERE group_id = :groupName")
    List<Notification> getBlockingGroupNotifications(String groupName);


    @Insert
    long insertNotification(Notification item);

    @Update
    int updateNotification(Notification item);

    @Query("DELETE FROM Notification WHERE id = :id")
    int deleteNotification(long id);

    @Query("DELETE FROM Notification WHERE group_id = :group")
    int deleteGroup(String group);
}
