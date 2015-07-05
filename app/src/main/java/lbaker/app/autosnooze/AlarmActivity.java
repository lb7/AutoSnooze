package lbaker.app.autosnooze;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import io.realm.Realm;
import lbaker.app.autosnooze.util.AlarmUtils;


public class AlarmActivity extends AppCompatActivity {

    public static final String LOG_TAG = AlarmActivity.class.getSimpleName();

    private PowerManager.WakeLock wakeLock;
    private Ringtone ringtone;
    private Vibrator vibrator;
    private AudioManager audioManager;

    private int originalVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE,
                        AlarmActivity.class.getSimpleName());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (wakeLock != null && !wakeLock.isHeld()) wakeLock.acquire();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wakeLock != null && !wakeLock.isHeld()) wakeLock.acquire();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Uri alarmURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmURI);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aa = new AudioAttributes.Builder().setContentType(AudioAttributes.USAGE_ALARM).build();
            ringtone.setAudioAttributes(aa);
        }

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (maxVolume * 0.75), 0);

        ringtone.play();

        if (vibrator.hasVibrator()) {
            vibrator.vibrate(new long[]{0, 1000, 1000, 1000}, 2);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //The resetting of the alarm needs to be done in onDestroy to get around
        //onPause and onStop being called twice when the screen is locked.
        int id = getIntent().getIntExtra("id", 0);

        Realm realm = Realm.getInstance(AlarmActivity.this);
        AlarmInfo alarmInfo = realm.where(AlarmInfo.class)
                .equalTo("id", id)
                .findAll()
                .first();

        if (alarmInfo.isRepeating()) {
            AlarmUtils.setAlarm(alarmInfo, getApplicationContext());
        } else {
            realm.beginTransaction();
            alarmInfo.setEnabled(false);
            realm.commitTransaction();
        }

        realm.close();
        ringtone.stop();
        vibrator.cancel();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Destroys activity on back button press. The user doesn't need to
        //return to the AlarmActivity.
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
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
