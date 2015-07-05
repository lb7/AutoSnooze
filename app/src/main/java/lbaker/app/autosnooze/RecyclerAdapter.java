package lbaker.app.autosnooze;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
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
    public void onBindViewHolder(AlarmViewHolder alarmViewHolder, final int i) {
        final Alarm alarm = alarmList.get(i);
        alarmViewHolder.timeView.setText(AlarmUtils.printAlarm(alarm));
        alarmViewHolder.daysView.setText(AlarmUtils.printDays(alarm, context.getResources()));

        alarmViewHolder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                realm.beginTransaction();
                alarm.setEnabled(isChecked);
                realm.commitTransaction();

                if (isChecked) {
                    AlarmUtils.setAlarm(alarm, context);
                } else {
                    AlarmUtils.cancelAlarm(alarm, context);
                }
            }
        });
        alarmViewHolder.toggle.setChecked(alarm.isEnabled());

        alarmViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, AlarmActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context,
                        alarm.getId(),
                        intent,
                        PendingIntent.FLAG_ONE_SHOT
                );
                alarmManager.cancel(pendingIntent);

                realm.beginTransaction();
                alarm.removeFromRealm();
                realm.commitTransaction();

                alarmList.remove(i);
                notifyDataSetChanged();*/
                AlarmUtils.cancelAlarm(alarm, context);

                realm.beginTransaction();
                alarm.removeFromRealm();
                realm.commitTransaction();

                alarmList.remove(i);
                notifyDataSetChanged();
            }
        });

        /*alarmViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.expand);
                set.setTarget(v);
                set.start();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    void addItem(Alarm alarm) {
        alarmList.add(alarm);
        AlarmUtils.setAlarm(alarm, context);
        notifyDataSetChanged();
    }

    void refresh() {
        RealmResults<Alarm> results = realm.allObjectsSorted(Alarm.class, "hour", true, "minute", true);

        alarmList = new ArrayList<>(results);
        notifyDataSetChanged();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.text_time)     protected TextView timeView;
        @Bind(R.id.text_days)     protected TextView daysView;
        @Bind(R.id.toggle)        protected Switch toggle;
        @Bind(R.id.button_delete) protected ImageButton delete;
        @Bind(R.id.container)     protected View container;

        public AlarmViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
