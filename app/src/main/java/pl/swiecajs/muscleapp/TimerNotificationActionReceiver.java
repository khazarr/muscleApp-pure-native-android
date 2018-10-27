package pl.swiecajs.muscleapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pl.swiecajs.muscleapp.util.NotificationUtil;
import pl.swiecajs.muscleapp.util.PrefUtil;

public class TimerNotificationActionReceiver extends BroadcastReceiver {

    Long secondsRemaining, wakeUpTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case AppConstants.ACTION_STOP:
                TimerActivity.removeAlarm(context);
                PrefUtil.setTimerState(TimerActivity.TimerState.STOPPED.getId(), context);
                NotificationUtil.hideTimerNotification(context);
                break;

            case AppConstants.ACTION_PAUSE:
                secondsRemaining = PrefUtil.getSecondsRemaining(context);
                Long alarmSetTime = PrefUtil.getAlarmSetTime(context);
                Long nowSeconds = TimerActivity.getNowSeconds();

                secondsRemaining -= nowSeconds - alarmSetTime;
                PrefUtil.setSecondsRemaining(secondsRemaining, context);

                TimerActivity.removeAlarm(context);
                PrefUtil.setTimerState(TimerActivity.TimerState.PAUSED.getId(), context);
                NotificationUtil.showTimerPaused(context);
                break;

            case AppConstants.ACTION_RESUME:
                secondsRemaining = PrefUtil.getSecondsRemaining(context);
                wakeUpTime = TimerActivity.setAlarm(context, TimerActivity.getNowSeconds(), secondsRemaining);
                PrefUtil.setTimerState(TimerActivity.TimerState.RUNNING.getId(), context);
                NotificationUtil.showTimerRunning(context, wakeUpTime);
                break;

            case AppConstants.ACTION_START:
                int minutesRemaining = PrefUtil.getTimerLength(context);
                secondsRemaining = minutesRemaining * 90L;
                wakeUpTime = TimerActivity.setAlarm(context, TimerActivity.getNowSeconds(), secondsRemaining);
                PrefUtil.setTimerState(TimerActivity.TimerState.RUNNING.getId(), context);
                PrefUtil.setSecondsRemaining(secondsRemaining, context);
                NotificationUtil.showTimerRunning(context, wakeUpTime);
                break;
        }
    }
}
