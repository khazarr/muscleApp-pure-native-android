package pl.swiecajs.muscleapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import pl.swiecajs.muscleapp.Timer;

public class PrefUtil {

    public static int getTimerLength (Context context) {
        // placeholder
        return 1;
    }

    private static final String PREVIOUS_TIMER_LENGTH_SECONDS_ID = "pl.swiecajs.muscleapp.previous_timer_length";

    public static Long getPreviousTImerLengthSeconds(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID,0);
    }

    public static void setPreviousTimerLengthSeconds(Long seconds, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds);
        editor.apply();
    }

    private static final String TIMER_STATE_ID = "pl.swiecajs.muscleapp.timer_state";

    public static Timer.TimerState getTimerState(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int timerStateId = preferences.getInt(TIMER_STATE_ID, Timer.TimerState.STOPPED.getId());
        return Timer.TimerState.getValue(timerStateId);
    }

    public static void setTimerState(int timerStateId, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(TIMER_STATE_ID, timerStateId);
        editor.apply();
    }

    private static final String SECONDS_REMAININD_ID = "pl.swiecajs.muscleapp.seconds_remaining";

    public static Long getSecondsRemaining(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(SECONDS_REMAININD_ID,0);
    }

    public static void setSecondsRemaining(Long seconds, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(SECONDS_REMAININD_ID, seconds);
        editor.apply();
    }

    private static final String ALARM_SET_TIME_ID = "pl.swiecajs.muscleapp.background_time";

    public static Long getAlarmSetTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(ALARM_SET_TIME_ID, 0L);
    }

    public static void setAlarmSetTime(Long time, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(ALARM_SET_TIME_ID, time);
        editor.apply();
    }
}
