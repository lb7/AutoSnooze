package lbaker.app.autosnooze.ui.preference;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.AttributeSet;

import io.realm.Realm;
import io.realm.RealmResults;
import lbaker.app.autosnooze.alarm.Alarm;
import lbaker.app.autosnooze.background.NotificationService;
import lbaker.app.autosnooze.util.AlarmUtils;

/**
 * Created by Luke on 8/5/2015.
 *
 */
public class NotificationIntervalPreference extends IntegerPreference {

    public static final int DEFAULT_VALUE = 15;

    private int prevVal;

    public NotificationIntervalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        prevVal = currentVal;
    }

    @Override
    protected void onAttachedToActivity() {
        super.onAttachedToActivity();
        refreshSummary();
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        refreshSummary();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        refreshSummary();
        rescheduleNotifications();
    }

    private void refreshSummary() {
        setSummary("Show a notification " + currentVal + " minutes before alarm");
    }

    private void rescheduleNotifications() {
        Context context = getContext().getApplicationContext();

        Realm realm = Realm.getInstance(context);
        RealmResults<Alarm> alarms = realm.where(Alarm.class)
                .equalTo("isEnabled", true)
                .findAll();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent;

        NotificationManagerCompat.from(context).cancelAll();
        for (Alarm alarm: alarms) {
            pendingIntent = PendingIntent.getService(context, alarm.getId(), intent, 0);
            alarmManager.cancel(pendingIntent);

            AlarmUtils.scheduleNotification(AlarmUtils.findNextAlarmTime(alarm), alarm, context);
        }
    }
}
