package fr.anto.notificationscheduler;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represent a notification in the database.
 */
@Entity
class Notification {

    @PrimaryKey(autoGenerate = true)
    protected long id;
    protected String title;
    protected long time;
    protected String actions;
    protected int color;
    protected int small_icon;
    protected int large_icon;
    protected String content;
    protected String channel_id;
    protected String group_id;
    protected String click_intent;

    protected Notification(long id, String title, long time, String actions, int color, int small_icon, int large_icon, String content, String channel_id, String group_id, String click_intent) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.actions = actions;
        this.color = color;
        this.small_icon = small_icon;
        this.large_icon = large_icon;
        this.content = content;
        this.channel_id = channel_id;
        this.group_id = group_id;
        this.click_intent = click_intent;
    }
}
