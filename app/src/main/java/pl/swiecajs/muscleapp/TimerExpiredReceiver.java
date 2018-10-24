package pl.swiecajs.muscleapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pl.swiecajs.muscleapp.util.PrefUtil;

public class TimerExpiredReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: Show  notification
        // an Intent broadcast.

        PrefUtil.setTimerState(Timer.TimerState.STOPPED.getId(), context);
        PrefUtil.setAlarmSetTime(0L, context);
    }
}
