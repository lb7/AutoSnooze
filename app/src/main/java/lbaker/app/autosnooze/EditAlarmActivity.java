package lbaker.app.autosnooze;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import butterknife.ButterKnife;
import butterknife.OnClick;
import lbaker.app.autosnooze.util.AlarmUtils;


public class EditAlarmActivity extends AppCompatActivity {

    private int hour;
    private int minute;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewCompat.setElevation(toolbar, 10);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        hour = intent.getIntExtra("hour", 0);
        minute = intent.getIntExtra("minute", 0);
        id = intent.getIntExtra("id", 0);

        TextView timeView = (TextView) findViewById(R.id.text_alarm);

        //This AlarmInfo is used for printing purposes only.
        //Not persisted in any way.
        timeView.setText(AlarmUtils.printAlarm(new AlarmInfo(hour, minute)));
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
        Intent result = new Intent();

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

        result.putExtra("hour", hour)
                .putExtra("minute", minute)
                .putExtra("id", id)
                .putExtra("days", days)
                .putExtra("repeat", repeat);

        setResult(RESULT_OK, result);
        finish();
    }
}
