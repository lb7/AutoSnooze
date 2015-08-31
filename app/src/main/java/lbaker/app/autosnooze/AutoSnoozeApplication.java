package lbaker.app.autosnooze;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Luke on 8/30/2015.
 *
 */
public class AutoSnoozeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(getApplicationContext(), new Crashlytics());
    }
}
