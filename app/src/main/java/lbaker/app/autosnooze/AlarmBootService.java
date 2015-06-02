package lbaker.app.autosnooze;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import io.realm.Realm;
import io.realm.RealmResults;
import lbaker.app.autosnooze.util.AlarmUtils;

/**
 * Created by Luke on 5/13/2015.
 */
public class AlarmBootService extends IntentService {

    public AlarmBootService() {
        super(AlarmBootService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Debug.waitForDebugger();

        Context context = getApplicationContext();
        Realm realm = Realm.getInstance(getApplicationContext());

        RealmResults<AlarmInfo> enabledAlarms = realm.where(AlarmInfo.class)
                .equalTo("isEnabled", true)
                .findAll();

        for (AlarmInfo alarmInfo : enabledAlarms) {
            AlarmUtils.setAlarm(alarmInfo, context);
        }

        realm.close();
    }
}
