package pl.swiecajs.muscleapp.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.net.Uri;
import android.app.TaskStackBuilder;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import pl.swiecajs.muscleapp.AppConstants;
import pl.swiecajs.muscleapp.R;
import pl.swiecajs.muscleapp.Timer;
import pl.swiecajs.muscleapp.TimerNotificationActionReceiver;

public class NotificationUtil {
    private static String CHANNEL_ID_TIMER = "menu_timer";
    private static String CHANNEL_NAME_TIMER = "MuscleApp Timer";
    private static String TIMER_ID = 0;

    public static void showTimerExpired(Context context) {
        Intent startIntent = new Intent(context, TimerNotificationActionReceiver.class);
        startIntent.setAction(AppConstants.ACTION_START);
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(context,
                0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true);

        nBuilder.setContentTitle("Timer Expired")
                .setContentText("Start again?")
                .setContentIntent(getPendingIntentWithStack(context, Timer.class))
                .addAction(R.drawable.ic_play, "Start", startPendingIntent);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    private static NotificationCompat.Builder getBasicNotificationBuilder(Context context, String channelId, boolean playSound) {
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_timer)
                .setAutoCancel(true)
                .setDefaults(0);
        if(playSound) nBuilder.setSound(notificationSound);
        return nBuilder;
    }

    private static PendingIntent getPendingIntentWithStack(Context context, Class javaClass){
        Intent resultIntent = new Intent(context, javaClass);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(javaClass);
        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
