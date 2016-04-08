package lbaker.app.autosnooze.ui.activity;

import android.content.Intent;
import android.media.AudioManager;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import lbaker.app.autosnooze.R;
import lbaker.app.autosnooze.adapter.RecyclerAdapter;
import lbaker.app.autosnooze.alarm.Alarm;
import lbaker.app.autosnooze.ui.AlarmListItemDecoration;
import lbaker.app.autosnooze.ui.fragment.TimePickerFragment;


public class MainActivity extends AppCompatActivity {

    private Realm realm;
    private RecyclerAdapter recyclerAdapter;

    @Bind(R.id.recycler) public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setVolumeControlStream(AudioManager.STREAM_ALARM);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewCompat.setElevation(toolbar, 10);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        realm = Realm.getInstance(realmConfig);

        RealmResults<Alarm> results = realm.allObjectsSorted(Alarm.class, "hour", Sort.ASCENDING, "minute",
                Sort.ASCENDING);

        List<Alarm> alarmList = new ArrayList<>(results);

        recyclerAdapter = new RecyclerAdapter(alarmList, realm, getApplicationContext());
        recyclerView.addItemDecoration(new AlarmListItemDecoration(getResources()));
        recyclerView.setAdapter(recyclerAdapter);

        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimePickerDialog();
            }
        });*/

        fab.setOnClickListener(view -> createTimePickerDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerAdapter.refresh();
    }

    @Override
    protected void onStart() {
        super.onStart();
        scrollToLocation(getIntent());
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
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_test_data:
                recyclerAdapter.generateTestData();
                break;
            case R.id.action_clear_data:
                recyclerAdapter.clearDataSet();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        scrollToLocation(intent);
    }

    private void createTimePickerDialog() {
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void scrollToLocation(Intent intent) {
        if (intent.getBooleanExtra("fromNotification", false)) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            List<Alarm> alarms = recyclerAdapter.getDataSet();

            for (int idx = 0; idx < alarms.size(); idx++) {
                if (intent.getIntExtra("id", 0) == alarms.get(idx).getId()) {
                    layoutManager.scrollToPositionWithOffset(idx, 0);
                    break;
                }
            }
        }
    }


}
