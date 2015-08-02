package lbaker.app.autosnooze.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;

import io.realm.Realm;
import lbaker.app.autosnooze.alarm.Alarm;
import lbaker.app.autosnooze.util.AlarmUtils;

/**
 * Created by Luke on 8/1/2015.
 *
 */
public class CancelAlarmService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int id = intent.getIntExtra("id", 0);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancel(id);

        Realm realm = Realm.getInstance(getApplicationContext());
        Alarm alarm = realm.where(Alarm.class)
                .equalTo("id", id)
                .findFirst();

        realm.beginTransaction();
        alarm.setEnabled(false);
        realm.commitTransaction();

        AlarmUtils.cancelAlarm(alarm, getApplicationContext());

        realm.close();

        return START_NOT_STICKY;
    }
}
