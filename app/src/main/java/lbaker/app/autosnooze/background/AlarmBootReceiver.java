package lbaker.app.autosnooze.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.realm.Realm;
import io.realm.RealmResults;
import lbaker.app.autosnooze.alarm.Alarm;
import lbaker.app.autosnooze.util.AlarmUtils;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Luke on 5/13/2015.
 *
 */
public class AlarmBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

        Observable
                .create(subscriber -> {
            Realm realm = Realm.getInstance(context);

            RealmResults<Alarm> enabledAlarms = realm.where(Alarm.class)
                    .equalTo("isEnabled", true)
                    .findAll();

            for (Alarm alarm : enabledAlarms) {
                AlarmUtils.setAlarm(alarm, context);
            }

            realm.close();
        })
                .subscribeOn(Schedulers.newThread())
                .subscribe();
    }
}
