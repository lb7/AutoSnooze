package lbaker.app.autosnooze;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;
import lbaker.app.autosnooze.util.AlarmUtils;

/**
 * Created by Luke on 12/29/2014.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.AlarmViewHolder> {

    private List<AlarmInfo> alarmInfoList;
    private Realm realm;
    private Context context;

    public RecyclerAdapter(List<AlarmInfo> alarmInfoList, Realm realm, Context context) {
        this.realm = realm;
        this.alarmInfoList = alarmInfoList;
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
        final AlarmInfo alarmInfo = alarmInfoList.get(i);
        alarmViewHolder.timeView.setText(AlarmUtils.printAlarm(alarmInfo));

        alarmViewHolder.toggle.setChecked(alarmInfo.isEnabled());
        alarmViewHolder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                realm.beginTransaction();

                alarmInfo.setEnabled(isChecked);
                realm.copyToRealm(alarmInfo);

                realm.commitTransaction();

                if (isChecked) {
                    AlarmUtils.setAlarm(alarmInfo, context);
                } else {
                    AlarmUtils.cancelAlarm(alarmInfo, context);
                }
            }
        });

        alarmViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, AlarmActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context,
                        alarmInfo.getId(),
                        intent,
                        PendingIntent.FLAG_ONE_SHOT
                );
                alarmManager.cancel(pendingIntent);

                realm.beginTransaction();
                alarmInfo.removeFromRealm();
                realm.commitTransaction();

                alarmInfoList.remove(i);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmInfoList.size();
    }

    void addItem(AlarmInfo alarmInfo) {
        alarmInfoList.add(alarmInfo);
        //setAlarm(alarmInfo);
        AlarmUtils.setAlarm(alarmInfo, context);
        notifyDataSetChanged();
    }




    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        protected TextView timeView;
        protected Switch toggle;
        protected ImageButton delete;

        public AlarmViewHolder(View v) {
            super(v);
            timeView = (TextView) v.findViewById(R.id.text_time);
            toggle = (Switch) v.findViewById(R.id.toggle);
            delete = (ImageButton) v.findViewById(R.id.button_delete);
        }
    }
}
