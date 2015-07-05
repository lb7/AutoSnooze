package lbaker.app.autosnooze;

import io.realm.RealmObject;

/**
 * Created by Luke on 7/5/2015.
 *
 */
public class SnoozeAlarm extends RealmObject {
    private int hour;
    private int minute;
    private int id;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
