package lbaker.app.autosnooze;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), R.style.AppTheme_TimePicker, this, hour, minute, false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        /*RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler);
        RecyclerAdapter adapter = (RecyclerAdapter) recyclerView.getAdapter();

        Realm realm = Realm.getInstance(getActivity());
        realm.beginTransaction();

        AlarmInfo alarmInfo = realm.createObject(AlarmInfo.class);
        alarmInfo.setHour(hourOfDay);
        alarmInfo.setMinute(minute);
        alarmInfo.setEnabled(true);
        alarmInfo.setId((int) System.currentTimeMillis() / 1000);

        adapter.addItem(alarmInfo);

        realm.commitTransaction();
        realm.close();*/

        Intent intent = new Intent(getActivity(), EditAlarmActivity.class);
        intent.putExtra("hour", hourOfDay)
                .putExtra("minute", minute)
                .putExtra("id", (int) System.currentTimeMillis() / 1000);

        getActivity().startActivityForResult(intent, MainActivity.NEW_ALARM_REQUEST_CODE);
    }
}
