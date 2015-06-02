package lbaker.app.autosnooze;

import io.realm.RealmObject;

/**
 * Created by Luke on 12/29/2014.
 */
public class AlarmInfo extends RealmObject {

    private int hour;
    private int minute;
    private int id;
    private boolean isEnabled;

    private byte[] days;

    public AlarmInfo() {
        this(0, 0);
    }

    public AlarmInfo(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        this.isEnabled = true;
    }

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

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public byte[] getDays() {
        return days;
    }

    public void setDays(byte[] days) {
        this.days = days;
    }
}
