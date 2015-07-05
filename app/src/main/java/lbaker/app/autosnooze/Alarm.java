package lbaker.app.autosnooze;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Luke on 12/29/2014.
 *
 */
public class Alarm extends RealmObject {

    private int hour;
    private int minute;
    private int id;
    private int snoozeDuration;
    private int snoozeQuantity;

    private boolean isEnabled;
    private boolean repeating;
    private boolean snoozeEnabled;

    private byte[] days;

    private RealmList<SnoozeAlarm> snoozeAlarms;

    public Alarm() {
        this(0, 0);
    }

    public Alarm(int hour, int minute) {
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

    public int getSnoozeDuration() {
        return snoozeDuration;
    }

    public void setSnoozeDuration(int snoozeDuration) {
        this.snoozeDuration = snoozeDuration;
    }

    public int getSnoozeQuantity() {
        return snoozeQuantity;
    }

    public void setSnoozeQuantity(int snoozeQuantity) {
        this.snoozeQuantity = snoozeQuantity;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isSnoozeEnabled() {
        return snoozeEnabled;
    }

    public void setSnoozeEnabled(boolean snoozeEnabled) {
        this.snoozeEnabled = snoozeEnabled;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public byte[] getDays() {
        return days;
    }

    public void setDays(byte[] days) {
        this.days = days;
    }

    public RealmList<SnoozeAlarm> getSnoozeAlarms() {
        return snoozeAlarms;
    }

    public void setSnoozeAlarms(RealmList<SnoozeAlarm> snoozeAlarms) {
        this.snoozeAlarms = snoozeAlarms;
    }
}
