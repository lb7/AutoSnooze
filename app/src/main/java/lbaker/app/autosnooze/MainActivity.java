package lbaker.app.autosnooze;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmResults;
import lbaker.app.autosnooze.util.AlarmUtils;


public class MainActivity extends AppCompatActivity {

    public static final int NEW_ALARM_REQUEST_CODE = 1;

    private Realm realm;
    private RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        realm = Realm.getInstance(this);
        RealmResults<Alarm> results = realm.allObjectsSorted(Alarm.class, "hour", true, "minute", true);

        List<Alarm> alarmList = new ArrayList<>(results);

        recyclerAdapter = new RecyclerAdapter(alarmList, realm, getApplicationContext());
        recyclerView.addItemDecoration(new AlarmListItemDecoration(getResources()));
        recyclerView.setAdapter(recyclerAdapter);

        setSupportActionBar(toolbar);
        ViewCompat.setElevation(toolbar, 10);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimePickerDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerAdapter.refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_notification:
                testNotification();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case NEW_ALARM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    realm.beginTransaction();

                    Alarm alarm = realm.createObject(Alarm.class);
                    alarm.setHour(data.getIntExtra("hour", 0));
                    alarm.setMinute(data.getIntExtra("minute", 0));
                    alarm.setId(data.getIntExtra("id", 0));
                    alarm.setDays(data.getByteArrayExtra("days"));
                    alarm.setRepeating(data.getBooleanExtra("repeat", false));
                    alarm.setSnoozeDuration(data.getIntExtra("snoozeDuration", 0));
                    alarm.setSnoozeQuantity(data.getIntExtra("snoozeQuantity", 0));
                    alarm.setSnoozeEnabled(data.getBooleanExtra("snoozeEnabled", false));
                    alarm.setEnabled(true);

                    realm.commitTransaction();

                    if (alarm.isSnoozeEnabled()) {
                        AlarmUtils.createSnoozeAlarms(alarm, getApplicationContext());
                    }
                    recyclerAdapter.addItem(alarm);
                }
                break;
            default:
                break;
        }
    }

    private void createTimePickerDialog() {
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void testNotification() {

    }
}
