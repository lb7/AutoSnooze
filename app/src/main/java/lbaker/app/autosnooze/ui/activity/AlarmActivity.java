package lbaker.app.autosnooze.ui.activity;

import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import io.realm.Realm;
import io.realm.RealmResults;
import lbaker.app.autosnooze.R;
import lbaker.app.autosnooze.alarm.Alarm;
import lbaker.app.autosnooze.util.AlarmUtils;


public class AlarmActivity extends AppCompatActivity {

    public static final String LOG_TAG = AlarmActivity.class.getSimpleName();

    private Ringtone ringtone;
    private Vibrator vibrator;
    //private AudioManager audioManager;

    //private int originalVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Uri alarmURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmURI);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            ringtone.setAudioAttributes(aa);
        }

        //audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

        // TODO: 8/10/2015 add setting for user specified volume. Global and per alarm.
        //int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        //audioManager.setStreamVolume(AudioManager.STREAM_ALARM, (int) (maxVolume * 0.75), 0);
        ringtone.play();

        if (vibrator.hasVibrator()) {
            vibrator.vibrate(new long[]{0, 1000, 1000, 1000}, 2);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onPause() {
        super.onPause();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= 20 && powerManager.isInteractive() ||
                powerManager.isScreenOn()) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //The resetting of the alarm needs to be done in onDestroy to get around
        //onPause and onStop being called twice when the screen is locked.
        int id = getIntent().getIntExtra("id", 0);

        Realm realm = Realm.getInstance(getApplicationContext());
        RealmResults<Alarm> realmResults = realm.where(Alarm.class)
                .equalTo("id", id)
                .findAll();

        //this alarm is a regular alarm and should be acted on. Snooze alarms require no action.
        //At least as of now.
        if (!realmResults.isEmpty()) {
            Alarm alarm = realmResults.first();

            if (alarm.isRepeating()) {
                AlarmUtils.setAlarm(alarm, getApplicationContext());
            } else {
                realm.beginTransaction();
                alarm.setEnabled(false);
                realm.commitTransaction();
            }

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(getApplicationContext());
            notificationManager.cancel(alarm.getId());
        }

        realm.close();
        ringtone.stop();
        vibrator.cancel();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
