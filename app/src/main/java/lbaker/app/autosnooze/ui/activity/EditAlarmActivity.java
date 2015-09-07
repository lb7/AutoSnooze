package lbaker.app.autosnooze.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.realm.Realm;
import lbaker.app.autosnooze.R;
import lbaker.app.autosnooze.alarm.Alarm;
import lbaker.app.autosnooze.util.AlarmUtils;

// TODO: 8/25/2015 Add option to pick alert tone.


public class EditAlarmActivity extends AppCompatActivity {

    private int hour;
    private int minute;
    private int id;

    private boolean snoozeEnabled;
    private boolean creatingAlarm;

    @Bind(R.id.text_alarm)           TextView timeView;
    @Bind(R.id.text_snooze_duration) EditText editSnoozeDuration;
    @Bind(R.id.text_snooze_quantity) EditText editSnoozeQuantity;
    @Bind(R.id.check_snooze)         CheckBox checkSnooze;

    private Realm realm;
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        ButterKnife.bind(this);
        realm = Realm.getInstance(getApplicationContext());

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewCompat.setElevation(toolbar, 10);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Intent intent = getIntent();
        creatingAlarm = intent.getBooleanExtra("creatingAlarm", false);
        id = intent.getIntExtra("id", 0);

        if (creatingAlarm) {
            realm.beginTransaction();
            alarm = realm.createObject(Alarm.class);
            realm.commitTransaction();

            hour = intent.getIntExtra("hour", 0);
            minute = intent.getIntExtra("minute", 0);

            //This Alarm is used for printing purposes only.
            //Not persisted in any way.
            timeView.setText(AlarmUtils.printAlarm(new Alarm(hour, minute)));
        } else {
            restoreState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_alarm, menu);
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

    @OnClick(R.id.button_save)
    void saveChanges() {
        int snoozeDuration = 0;
        int snoozeQuantity = 0;

        if (checkSnooze.isChecked()) {
            if (editSnoozeDuration.getText().toString().trim().length() == 0) {
                editSnoozeDuration.setError(getString(R.string.error_input_required));
            }
            if (editSnoozeQuantity.getText().toString().trim().length() == 0) {
                editSnoozeQuantity.setError(getString(R.string.error_input_required));
            }
            if (editSnoozeQuantity.getError() != null || editSnoozeDuration.getError() != null) {
                return;
            }
            snoozeDuration = Integer.parseInt(editSnoozeDuration.getText().toString());
            snoozeQuantity = Integer.parseInt(editSnoozeQuantity.getText().toString());
        }

        boolean repeat = false;
        byte[] days = new byte[AlarmUtils.NUM_DAYS_WEEK];
        ViewGroup buttonContainer = (ViewGroup) findViewById(R.id.container_day);

        for (int idx = 0; idx < buttonContainer.getChildCount(); idx++) {
            ToggleButton toggleButton = (ToggleButton) buttonContainer.getChildAt(idx);
            days[idx] = (byte) (toggleButton.isChecked() ? 1 : 0);
            if (toggleButton.isChecked()) {
                days[idx] = 1;
                repeat = true;
            }
        }

        realm = Realm.getInstance(getApplicationContext());
        realm.beginTransaction();

        alarm.setHour(hour);
        alarm.setMinute(minute);
        alarm.setId(id);
        alarm.setDays(days);
        alarm.setRepeating(repeat);
        alarm.setSnoozeDuration(snoozeDuration);
        alarm.setSnoozeQuantity(snoozeQuantity);
        alarm.setSnoozeEnabled(snoozeEnabled);
        alarm.setEnabled(true);

        realm.commitTransaction();
        realm.close();

        if (!creatingAlarm) {
            AlarmUtils.setAlarm(alarm, getApplicationContext());
        }

        finish();
    }

    @OnCheckedChanged(R.id.check_snooze)
    void snoozeChanged(boolean checked) {
        snoozeEnabled = checked;
        editSnoozeQuantity.setEnabled(checked);
        editSnoozeDuration.setEnabled(checked);
        if (!checked) {
            editSnoozeDuration.setError(null);
            editSnoozeDuration.clearFocus();

            editSnoozeQuantity.setError(null);
            editSnoozeQuantity.clearFocus();
        }
    }

    private void restoreState() {
        alarm = realm.where(Alarm.class).equalTo("id", id).findFirst();

        hour = alarm.getHour();
        minute = alarm.getMinute();
        snoozeEnabled = alarm.isSnoozeEnabled();

        checkSnooze.setChecked(snoozeEnabled);
        timeView.setText(AlarmUtils.printAlarm(alarm));

        if (snoozeEnabled) {
            editSnoozeDuration.setText(String.valueOf(alarm.getSnoozeDuration()));
            editSnoozeQuantity.setText(String.valueOf(alarm.getSnoozeQuantity()));
        }

        final ViewGroup buttonContainer = (ViewGroup) findViewById(R.id.container_day);
        byte[] days = alarm.getDays();
        for (int idx = 0; idx < buttonContainer.getChildCount(); idx++) {
            if (days[idx] == 1) {
                ToggleButton toggleButton = (ToggleButton) buttonContainer.getChildAt(idx);
                toggleButton.setChecked(true);
            }
        }
    }

}
