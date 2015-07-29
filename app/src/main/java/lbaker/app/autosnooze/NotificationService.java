package lbaker.app.autosnooze;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import org.jetbrains.annotations.NotNull;

import io.realm.Realm;
import lbaker.app.autosnooze.util.AlarmUtils;

/**
 * Created by Luke on 7/20/2015.
 *
 */
public class NotificationService extends Service {

    @Override
    public IBinder onBind(@NotNull Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(@NotNull Intent intent, int flags, int startId) {
        int id = intent.getIntExtra("id", 0);

        Realm realm = Realm.getInstance(getApplicationContext());
        Alarm alarm = realm.where(Alarm.class)
                .equalTo("id", id)
                .findFirst();

        Intent actionIntent = new Intent(getApplicationContext(), MainActivity.class)
                .putExtra("id", id)
                .putExtra("fromNotification", true)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent actionPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                (int) (System.currentTimeMillis() / 1000),
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.title_notification_upcoming))
                .setContentText(AlarmUtils.printAlarm(alarm))
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setContentIntent(actionPendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(id, notification);
        return START_NOT_STICKY;
    }
}
