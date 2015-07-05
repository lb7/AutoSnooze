package lbaker.app.autosnooze;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.realm.Realm;
import io.realm.RealmResults;
import lbaker.app.autosnooze.util.AlarmUtils;

/**
 * Created by Luke on 5/13/2015.
 *
 */
public class AlarmBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Realm realm = Realm.getInstance(context);

        RealmResults<Alarm> enabledAlarms = realm.where(Alarm.class)
                .equalTo("isEnabled", true)
                .findAll();

        for (Alarm alarm : enabledAlarms) {
            AlarmUtils.setAlarm(alarm, context);
        }

        realm.close();
    }
}
