package lbaker.app.autosnooze;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.realm.Realm;
import io.realm.RealmResults;
import lbaker.app.autosnooze.util.AlarmUtils;

/**
 * Created by Luke on 5/13/2015.
 */
public class AlarmBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Realm realm = Realm.getInstance(context);

        RealmResults<AlarmInfo> enabledAlarms = realm.where(AlarmInfo.class)
                .equalTo("isEnabled", true)
                .findAll();

        for (AlarmInfo alarmInfo : enabledAlarms) {
            AlarmUtils.setAlarm(alarmInfo, context);
        }

        realm.close();
    }
}
