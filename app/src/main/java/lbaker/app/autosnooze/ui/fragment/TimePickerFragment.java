package lbaker.app.autosnooze.ui.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;

import lbaker.app.autosnooze.R;
import lbaker.app.autosnooze.ui.activity.EditAlarmActivity;
import lbaker.app.autosnooze.ui.activity.MainActivity;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), R.style.AppTheme_TimePicker, this, hour, minute, false);
    }

    //todo: Fix this method being called twice on API < 21. Results in the alarm editing screen popping twice.
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Intent intent = new Intent(getActivity(), EditAlarmActivity.class);
        intent.putExtra("hour", hourOfDay)
                .putExtra("minute", minute)
                .putExtra("id", (int) (System.currentTimeMillis() / 1000));

        getActivity().startActivityForResult(intent, MainActivity.NEW_ALARM_REQUEST_CODE);
    }
}
