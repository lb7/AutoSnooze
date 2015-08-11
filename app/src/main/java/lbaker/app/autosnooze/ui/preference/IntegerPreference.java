package lbaker.app.autosnooze.ui.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

import lbaker.app.autosnooze.R;

/**
 * Created by Luke on 8/4/2015.
 *
 */
public class IntegerPreference extends DialogPreference {

    public static final int DEFAULT_VALUE = 0;

    protected int currentVal;

    public EditText editText;

    public IntegerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.dialog_integer_preference);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            currentVal = getPersistedInt(DEFAULT_VALUE);
        } else {
            currentVal = (Integer) defaultValue;
            persistInt(currentVal);
        }

        //refreshSummary();
    }

    @Override
    protected void onBindDialogView(@NotNull View view) {
        super.onBindDialogView(view);

        editText = (EditText) view.findViewById(R.id.text_notification_time);
        editText.setText(Integer.toString(currentVal));
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);

        if (which == DialogInterface.BUTTON_POSITIVE) {
            currentVal = Integer.parseInt(editText.getText().toString());
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(currentVal);
        }
    }


}
