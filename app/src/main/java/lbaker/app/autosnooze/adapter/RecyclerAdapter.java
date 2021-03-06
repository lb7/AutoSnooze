package lbaker.app.autosnooze.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import lbaker.app.autosnooze.R;
import lbaker.app.autosnooze.alarm.Alarm;
import lbaker.app.autosnooze.ui.activity.EditAlarmActivity;
import lbaker.app.autosnooze.util.AlarmUtils;

/**
 * Created by Luke on 12/29/2014.
 *
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.AlarmViewHolder> {

    private List<Alarm> alarmList;
    private Realm realm;
    private Context context;

    public RecyclerAdapter(List<Alarm> alarmList, Realm realm, Context context) {
        this.realm = realm;
        this.alarmList = alarmList;
        this.context = context;
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.alarm_list, viewGroup, false);

        return new AlarmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AlarmViewHolder alarmVH, final int i) {
        final Alarm alarm = alarmList.get(i);
        alarmVH.timeView.setText(AlarmUtils.printAlarm(alarm));
        alarmVH.daysView.setText(AlarmUtils.printDays(alarm, context.getResources()));

        alarmVH.toggle.setChecked(alarm.isEnabled());
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public void addItem(Alarm alarm) {
        alarmList.add(alarm);
        if (alarm.isEnabled()) {
            AlarmUtils.setAlarm(alarm, context);
        }
        notifyDataSetChanged();
    }

    public void refresh() {
        RealmResults<Alarm> results = realm.allObjectsSorted(Alarm.class, "hour", Sort.ASCENDING, "minute",
                Sort.ASCENDING);

        alarmList = new ArrayList<>(results);
        notifyDataSetChanged();
    }

    public List<Alarm> getDataSet() {
        return alarmList;
    }

    public void clearDataSet() {
        for (Alarm alarm : alarmList) {
            AlarmUtils.cancelAlarm(alarm, context);

            realm.beginTransaction();
            alarm.removeFromRealm();
            realm.commitTransaction();
        }
        alarmList.clear();
        notifyDataSetChanged();
    }

    public void generateTestData() {
        for (int idx = 0; idx < 50; idx++) {
            Random random = new Random();
            int hour = random.nextInt(23);
            int minute = random.nextInt(59);

            realm.beginTransaction();

            Alarm alarm = realm.createObject(Alarm.class);
            alarm.setHour(hour);
            alarm.setMinute(minute);
            alarm.setEnabled(false);
            alarm.setDays(new byte[7]);

            realm.commitTransaction();
            addItem(alarm);
        }
        refresh();
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.text_time)     protected TextView timeView;
        @Bind(R.id.text_days)     protected TextView daysView;
        @Bind(R.id.toggle)        protected Switch toggle;
        @Bind(R.id.button_delete) protected ImageButton delete;
        @Bind(R.id.container)     protected View container;

        public AlarmViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()) {
                    Alarm alarm = alarmList.get(getAdapterPosition());

                    realm.beginTransaction();
                    alarm.setEnabled(isChecked);
                    realm.commitTransaction();

                    if (isChecked) {
                        AlarmUtils.setAlarm(alarm, context);
                    } else {
                        AlarmUtils.cancelAlarm(alarm, context);
                        AlarmUtils.cancelNotification(alarm.getId(), context);
                    }
                }
            });

            delete.setOnClickListener(view -> {
                Alarm alarm = alarmList.get(getAdapterPosition());
                AlarmUtils.cancelAlarm(alarm, context);
                AlarmUtils.cancelNotification(alarm.getId(), context);

                realm.beginTransaction();
                alarm.removeFromRealm();
                realm.commitTransaction();

                int idx = getAdapterPosition();

                alarmList.remove(idx);
                notifyItemRemoved(idx);

                Snackbar.make(view.getRootView().findViewById(R.id.coordinator), "Alarm deleted",
                        Snackbar.LENGTH_LONG).show();
            });

            container.setOnClickListener(view -> {
                Intent intent = new Intent(context, EditAlarmActivity.class)
                        .putExtra("id", alarmList.get(getAdapterPosition()).getId())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });
        }
    }
}
