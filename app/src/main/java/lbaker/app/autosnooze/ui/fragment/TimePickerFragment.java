package lbaker.app.autosnooze.ui.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.widget.TimePicker;

import java.util.Calendar;

import lbaker.app.autosnooze.R;
import lbaker.app.autosnooze.ui.activity.EditAlarmActivity;
import lbaker.app.autosnooze.util.AlarmUtils;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog
        .OnTimeSetListener {

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour;
        int minute;

        Bundle args = getArguments();

        TimePickerDialog.OnTimeSetListener onTimeSetListener = null;
        if (args != null) {
            if (args.getBoolean("isEditing")) {
                onTimeSetListener = (TimePickerDialog.OnTimeSetListener) getActivity();
            }
            hour = args.getInt("hour");
            minute = args.getInt("minute");
        } else {
            onTimeSetListener = this;
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new TimePickerDialog(getActivity(), R.style.AppTheme_TimePicker,
                    onTimeSetListener, hour, minute, false);
        } else {
            ContextThemeWrapper themeWrapper = new ContextThemeWrapper(getActivity(),
                    R.style.AppTheme_TimePicker_Compat);
            return new TimePickerDialog(themeWrapper, onTimeSetListener, hour, minute, false);
        }

    }

    // Called twice on some versions of Android. Once when the "confirm" button is pushed and once
    // when the dialog is actually dismissed. So it gets called even if the user dismisses the
    // dialog. Workaround by only opening the activity if the dialog is shown i.e. when the user
    // clicks the positive button on the dialog.
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (view.isShown()) {
            Intent intent = new Intent(getActivity(), EditAlarmActivity.class);
            intent.putExtra("hour", hourOfDay)
                    .putExtra("minute", minute)
                    .putExtra("id", AlarmUtils.generateId())
                    .putExtra("creatingAlarm", true);

            getActivity().startActivity(intent);
        }
    }
}
