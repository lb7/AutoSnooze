package lbaker.app.autosnooze.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;

import java.util.Calendar;

import lbaker.app.autosnooze.AlarmActivity;
import lbaker.app.autosnooze.AlarmInfo;
import lbaker.app.autosnooze.R;

public class AlarmUtils {

    public static final int NUM_DAYS_WEEK = 7;

    public static void setAlarm(AlarmInfo alarmInfo, Context context) {
        int id = alarmInfo.getId();

        long alarmTime = AlarmUtils.findNextAlarmTime(alarmInfo);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmActivity.class);
        intent.putExtra("id", id);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                id,
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(alarmTime, pendingIntent);
            alarmManager.setAlarmClock(info, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }

    public static long findNextAlarmTime(AlarmInfo alarmInfo) {
        Calendar alarmTime = Calendar.getInstance();

        alarmTime.set(Calendar.HOUR_OF_DAY, alarmInfo.getHour());
        alarmTime.set(Calendar.MINUTE, alarmInfo.getMinute());
        alarmTime.set(Calendar.SECOND, 0);

        if (alarmInfo.isRepeat()) {
            byte[] days = alarmInfo.getDays();
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
                    if (currentDay == (idx + 1) && System.currentTimeMillis() > alarmTime.getTimeInMillis()) {
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

    public static void cancelAlarm(AlarmInfo alarmInfo, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                alarmInfo.getId(),
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );

        alarmManager.cancel(pendingIntent);
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

    public static String printAlarm(AlarmInfo alarmInfo) {
        return AlarmUtils.printAlarm(alarmInfo.getHour(), alarmInfo.getMinute());
    }

    public static String printDays(AlarmInfo alarmInfo, Resources resources) {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] days = alarmInfo.getDays();
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

            if (currentTime.get(Calendar.HOUR_OF_DAY) < alarmInfo.getHour() ||
                    (currentTime.get(Calendar.HOUR_OF_DAY) == alarmInfo.getHour() && currentTime.get(Calendar.MINUTE) < alarmInfo.getMinute())){
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
}
