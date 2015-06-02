package lbaker.app.autosnooze;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import lbaker.app.autosnooze.util.AlarmUtils;


public class EditAlarmActivity extends AppCompatActivity {

    private int mHour;
    private int mMinute;
    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewCompat.setElevation(toolbar, 10);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        mHour = intent.getIntExtra("hour", 0);
        mMinute = intent.getIntExtra("minute", 0);
        mId = intent.getIntExtra("id", 0);

        TextView timeView = (TextView) findViewById(R.id.text_alarm);
        //This AlarmInfo is used for printing purposes only.
        //Not persisted in any way.
        timeView.setText(AlarmUtils.printAlarm(new AlarmInfo(mHour, mMinute)));

        Button saveButton = (Button) findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
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

    public void saveChanges() {
        Intent result = new Intent();

        byte[] days = new byte[AlarmUtils.NUM_DAYS_WEEK];
        ViewGroup buttonContainer = (ViewGroup) findViewById(R.id.container_day);

        for (int idx = 0; idx < buttonContainer.getChildCount(); idx++) {
            ToggleButton toggleButton = (ToggleButton) buttonContainer.getChildAt(idx);
            days[idx] = (byte) (toggleButton.isChecked() ? 1 : 0);
            /*if (toggleButton.isActivated()) {
                days[idx] = 1;
            } else {
                days[idx] = 0;
            }*/
        }

        result.putExtra("hour", mHour)
                .putExtra("minute", mMinute)
                .putExtra("id", mId)
                .putExtra("days", days);

        setResult(RESULT_OK, result);
        finish();
    }
}
