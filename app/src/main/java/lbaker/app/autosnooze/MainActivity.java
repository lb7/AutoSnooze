package lbaker.app.autosnooze;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {

    public static final int NEW_ALARM_REQUEST_CODE = 1;

    private Realm realm;
    private RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        ImageButton fab = (ImageButton) findViewById(R.id.fab);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        realm = Realm.getInstance(this);
        RealmResults<AlarmInfo> results = realm.allObjectsSorted(AlarmInfo.class, "hour", true, "minute", true);

        List<AlarmInfo> alarmList = new ArrayList<>(results);

        recyclerAdapter = new RecyclerAdapter(alarmList, realm, this);
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
            case R.id.action_new:
                createTimePickerDialog();
                break;
            case R.id.action_set_alarm:
                setTestAlarm();
                break;
            case R.id.action_cancel_alarm:
                cancelTestAlarm();
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

                    AlarmInfo alarmInfo = realm.createObject(AlarmInfo.class);
                    alarmInfo.setHour(data.getIntExtra("hour", 0));
                    alarmInfo.setMinute(data.getIntExtra("minute", 0));
                    alarmInfo.setId(data.getIntExtra("id", 0));
                    alarmInfo.setDays(data.getByteArrayExtra("days"));
                    alarmInfo.setEnabled(true);

                    realm.commitTransaction();

                    recyclerAdapter.addItem(alarmInfo);
                }
                break;
            default:
                break;
        }
    }

    private void setTestAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    1,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
            );
            AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(System.currentTimeMillis() + 10 * 1000, pendingIntent);

            alarmManager.setAlarmClock(info, pendingIntent);
        }
    }

    private void cancelTestAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingIntent);
    }

    private void createTimePickerDialog() {
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }
}
