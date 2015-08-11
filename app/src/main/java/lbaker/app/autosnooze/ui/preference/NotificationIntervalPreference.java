package lbaker.app.autosnooze.ui.preference;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Luke on 8/5/2015.
 */
public class NotificationIntervalPreference extends IntegerPreference {

    public static final int DEFAULT_VALUE = 5;

    public NotificationIntervalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToActivity() {
        super.onAttachedToActivity();
        refreshSummary();
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        refreshSummary();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        refreshSummary();
    }

    private void refreshSummary() {
        setSummary("Show a notification " + currentVal + " minutes before alarm");
    }
}
