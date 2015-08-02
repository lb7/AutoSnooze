package lbaker.app.autosnooze.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmList;
import lbaker.app.autosnooze.alarm.Alarm;
import lbaker.app.autosnooze.background.NotificationService;
import lbaker.app.autosnooze.R;
import lbaker.app.autosnooze.alarm.SnoozeAlarm;
import lbaker.app.autosnooze.ui.activity.AlarmActivity;

public class AlarmUtils {

    public static final int NUM_DAYS_WEEK = 7;

    public static void setAlarm(Alarm alarm, Context context) {
        int id = alarm.getId();

        long alarmTime = AlarmUtils.findNextAlarmTime(alarm);

        Intent intent = new Intent(context, AlarmActivity.class);
        intent.putExtra("id", id);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                id,
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );

        setAlarmCompat(alarmTime, pendingIntent, context);

        if (alarm.isSnoozeEnabled()) {
            setSnoozeAlarms(alarm, context);
        }

        scheduleNotification(alarmTime, alarm, context);
    }

    private static void setSnoozeAlarms(Alarm alarm, Context context) {
        RealmList<SnoozeAlarm> snoozeAlarms = alarm.getSnoozeAlarms();

        for (SnoozeAlarm snoozeAlarm : snoozeAlarms) {
            int id = snoozeAlarm.getId();

            long alarmTime = AlarmUtils.findNextAlarmTime(new Alarm(snoozeAlarm.getHour(), snoozeAlarm.getMinute()));

            Intent intent = new Intent(context, AlarmActivity.class);
            intent.putExtra("id", id);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    id,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
            );

            setAlarmCompat(alarmTime, pendingIntent, context);
        }
    }

    //Exact alarm setting is different on several versions of android.
    private static void setAlarmCompat(long time, PendingIntent pendingIntent, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(time, pendingIntent);
            alarmManager.setAlarmClock(info, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    public static long findNextAlarmTime(Alarm alarm) {
        Calendar alarmTime = Calendar.getInstance();

        alarmTime.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        alarmTime.set(Calendar.MINUTE, alarm.getMinute());
        alarmTime.set(Calendar.SECOND, 0);

        if (alarm.isRepeating()) {
            byte[] days = alarm.getDays();
            int currentDay = alarmTime.get(Calendar.DAY_OF_WEEK);

            //Each index of this array corresponds to a day of the week, starting with Sunday.
            //As such idx + 1 will be one of the day constants in Calendar. 0 = Sunday, 1 = Monday, etc.
            for (int idx = currentDay - 1, count = 0; count < NUM_DAYS_WEEK; count++, idx++) {
                if (idx == NUM_DAYS_WEEK) {
                    idx = 0;
                }

                //Alarm is enabled for this day
                if (days[idx] == 1) {
                    //If the alarm should fire on the current day but the time has passed,
                    //decrease the count so the loop will come back to the current day if there
                    //are no other days this alarm should fire.
                    if (currentDay == (idx + 1) &&
                            System.currentTimeMillis() > alarmTime.getTimeInMillis()) {
                        count--;
                    } else {
                        break;
                    }
                }
                alarmTime.add(Calendar.DATE, 1);
            }
        } else {
            if (System.currentTimeMillis() > alarmTime.getTimeInMillis()) {
                alarmTime.add(Calendar.DATE, 1);
            }
        }
        return alarmTime.getTimeInMillis();
    }

    public static void cancelAlarm(Alarm alarm, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                alarm.getId(),
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );

        Intent serviceIntent = new Intent(context, NotificationService.class);
        PendingIntent pendingServiceIntent = PendingIntent.getService(
                context,
                alarm.getId(),
                serviceIntent,
                PendingIntent.FLAG_ONE_SHOT);

        alarmManager.cancel(pendingIntent);
        alarmManager.cancel(pendingServiceIntent);
        if (alarm.isSnoozeEnabled()) {
            AlarmUtils.cancelSnoozeAlarms(alarm, context);
        }
    }

    public static void cancelSnoozeAlarms(Alarm alarm, Context context) {
        RealmList<SnoozeAlarm> snoozeAlarms = alarm.getSnoozeAlarms();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (SnoozeAlarm snoozeAlarm : snoozeAlarms) {
            Intent intent = new Intent(context, AlarmActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    snoozeAlarm.getId(),
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
            );

            alarmManager.cancel(pendingIntent);
        }
    }

    public static String printAlarm(int alarmHour, int alarmMinute) {
        int hour;
        String minute;
        String amPm;

        if (alarmHour > 12) {
            hour = alarmHour - 12;
            amPm = "PM";
        } else if (alarmHour <= 12 && alarmHour != 0) {
            hour = alarmHour;
            amPm = "AM";
        } else {
            hour = 12;
            amPm = "AM";
        }

        if (alarmMinute == 0) {
            minute = "00";
        } else if (alarmMinute > 0 && alarmMinute < 10) {
            minute = "0" + alarmMinute;
        } else {
            minute = Integer.toString(alarmMinute);
        }

        return hour + ":" + minute + " " + amPm;
    }

    public static String printAlarm(Alarm alarm) {
        return AlarmUtils.printAlarm(alarm.getHour(), alarm.getMinute());
    }

    public static String printDays(Alarm alarm, Resources resources) {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] days = alarm.getDays();
        int lastDay = 0;

        for (int idx = 0; idx < days.length; idx++) {
            if (days[idx] == 1) {
                stringBuilder.append(AlarmUtils.getDayText(idx + 1, resources, false))
                        .append(", ");
                lastDay = idx + 1;
            }
        }

        if (stringBuilder.length() == 5) { //There is only one day so use full day text
            stringBuilder.delete(0, stringBuilder.length());
            stringBuilder.append(AlarmUtils.getDayText(lastDay, resources, true));
        } else if (stringBuilder.length() > 0) {
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        } else {
            Calendar currentTime = Calendar.getInstance();

            if (currentTime.get(Calendar.HOUR_OF_DAY) < alarm.getHour() ||
                    (currentTime.get(Calendar.HOUR_OF_DAY) == alarm.getHour() && currentTime.get(Calendar.MINUTE) < alarm.getMinute())){
                stringBuilder.append(resources.getString(R.string.today));
            } else {
                stringBuilder.append(resources.getString(R.string.tomorrow));
            }
        }

        return stringBuilder.toString();
    }

    private static String getDayText(int day, Resources resources, boolean fullText) {
        int dayString = 0;
        if (fullText) {
            switch (day) {
                case Calendar.SUNDAY:
                    dayString = R.string.day_sun_full;
                    break;
                case Calendar.MONDAY:
                    dayString = R.string.day_mon_full;
                    break;
                case Calendar.TUESDAY:
                    dayString = R.string.day_tue_full;
                    break;
                case Calendar.WEDNESDAY:
                    dayString = R.string.day_wed_full;
                    break;
                case Calendar.THURSDAY:
                    dayString = R.string.day_thu_full;
                    break;
                case Calendar.FRIDAY:
                    dayString = R.string.day_fri_full;
                    break;
                case Calendar.SATURDAY:
                    dayString = R.string.day_sat_full;
                    break;
                default:
                    break;
            }
        } else {
            switch (day) {
                case Calendar.SUNDAY:
                    dayString = R.string.day_sun;
                    break;
                case Calendar.MONDAY:
                    dayString = R.string.day_mon;
                    break;
                case Calendar.TUESDAY:
                    dayString = R.string.day_tue;
                    break;
                case Calendar.WEDNESDAY:
                    dayString = R.string.day_wed;
                    break;
                case Calendar.THURSDAY:
                    dayString = R.string.day_thu;
                    break;
                case Calendar.FRIDAY:
                    dayString = R.string.day_fri;
                    break;
                case Calendar.SATURDAY:
                    dayString = R.string.day_sat;
                    break;
                default:
                    break;
            }
        }
        return resources.getString(dayString);
    }

    public static void createSnoozeAlarms(Alarm alarm, Context context) {
        Realm realm = Realm.getInstance(context);

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        alarmTime.set(Calendar.MINUTE, alarm.getMinute());

        realm.beginTransaction();
        RealmList<SnoozeAlarm> snoozeAlarms = alarm.getSnoozeAlarms();

        for (int i = 0; i < alarm.getSnoozeQuantity(); i++) {
            alarmTime.add(Calendar.MINUTE, alarm.getSnoozeDuration());

            SnoozeAlarm snoozeAlarm = realm.createObject(SnoozeAlarm.class);
            snoozeAlarm.setHour(alarmTime.get(Calendar.HOUR_OF_DAY));
            snoozeAlarm.setMinute(alarmTime.get(Calendar.MINUTE));
            snoozeAlarm.setId((int) System.currentTimeMillis() / 1000);

            snoozeAlarms.add(snoozeAlarm);
        }
        realm.commitTransaction();
        realm.close();
    }

    private static void scheduleNotification(long alarmTime, Alarm alarm, Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra("id", alarm.getId());

        PendingIntent pendingIntent = PendingIntent.getService(context,
                alarm.getId(),
                intent,
                0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //todo: replace with dynamic number of minutes set by the user.
        alarmTime -= 60 * 60 * 1000;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }

    public static void cancelNotification(int id, Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(id);
    }
}
