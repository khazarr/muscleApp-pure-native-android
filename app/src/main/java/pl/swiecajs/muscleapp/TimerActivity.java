package pl.swiecajs.muscleapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import pl.swiecajs.muscleapp.util.NotificationUtil;
import pl.swiecajs.muscleapp.util.PrefUtil;

public class TimerActivity extends AppCompatActivity {

    public enum TimerState {
        STOPPED(0),
        PAUSED(1),
        RUNNING(2);

        private int id;

        TimerState(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static TimerState getValue(int id) {
            for(TimerState e: TimerState.values()) {
                if(e.id == id) {
                    return e;
                }
            }
            return null;// not found
        }
    }
    
    public static Long setAlarm(Context context, Long nowSeconds, Long secondsRemaining) {
        Long wakeUpTime = (nowSeconds + secondsRemaining) * 1000;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimerExpiredReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent);
        PrefUtil.setAlarmSetTime(nowSeconds, context);
        return wakeUpTime;
    }

    public static void removeAlarm(Context context){
        Intent intent = new Intent(context, TimerExpiredReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        PrefUtil.setAlarmSetTime(0L, context);
    }

    public static Long nowSeconds = Calendar.getInstance().getTimeInMillis()/1000;
    public static Long getNowSeconds(){
        nowSeconds = Calendar.getInstance().getTimeInMillis()/1000;
        return nowSeconds;
    }



    private CountDownTimer timer;
    private Long timerLengthSeconds = 0L;
    private Long secondsRemaining = 0L;
    private TimerState timerState = TimerState.STOPPED;


    private ProgressBar progressBar;
    private TextView countdownText;
    private FloatingActionButton fab_start;
    private FloatingActionButton fab_stop;
    private FloatingActionButton fab_pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_timer);
        getSupportActionBar().setTitle("   MuscleApp");

        progressBar = findViewById(R.id.progress_countdown);
        countdownText = findViewById(R.id.textView_countdown);
        fab_start = findViewById(R.id.fab_start);
        fab_stop = findViewById(R.id.fab_stop);
        fab_pause = findViewById(R.id.fab_pause);

        fab_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
                timerState = TimerState.RUNNING;
                updateButtons();
            }
        });

        fab_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                timerState = TimerState.PAUSED;
                updateButtons();
            }
        });

        fab_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                onTimerFinished();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        initTimer();
        removeAlarm(this);
        NotificationUtil.hideTimerNotification(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(timerState == TimerState.RUNNING) {
            timer.cancel();
            Long wakeUpTime = setAlarm(this, getNowSeconds(), secondsRemaining);
            NotificationUtil.showTimerRunning(this, wakeUpTime);
        } else if(timerState == TimerState.PAUSED) {
            NotificationUtil.showTimerPaused(this);
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this);
        PrefUtil.setSecondsRemaining(secondsRemaining, this);
        PrefUtil.setTimerState(timerState.getId(),this);


    }

    private void initTimer() {
        timerState = PrefUtil.getTimerState(this);

        if (timerState == TimerState.STOPPED) {
            setNewTimerLength();
        } else {
            setPreviousTimerLength();
        }

        secondsRemaining = (timerState == TimerState.RUNNING || timerState == TimerState.PAUSED)
                ? PrefUtil.getSecondsRemaining(this)
                : timerLengthSeconds;

        Long alarmSetTime = PrefUtil.getAlarmSetTime(this);
        if (alarmSetTime > 0) {
            secondsRemaining -= getNowSeconds() - alarmSetTime;
        }



        if (secondsRemaining <= 0){
            onTimerFinished();
        } else if (timerState == TimerState.RUNNING) {
            startTimer();
        }

        updateButtons();
        updateCountdownUI();
    }

    private void onTimerFinished() {
        timerState = TimerState.STOPPED;

        setNewTimerLength();


        progressBar.setProgress(0);

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this);
        secondsRemaining = timerLengthSeconds;

        updateButtons();
        updateCountdownUI();
    }

    private void startTimer() {
        timerState = TimerState.RUNNING;
        timer = new CountDownTimer(secondsRemaining * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsRemaining = millisUntilFinished / 1000;

                updateCountdownUI();
            }

            @Override
            public void onFinish() {
                onTimerFinished();
            }
        }.start();
    }

    private void setNewTimerLength() {
        int lengthInMinutes = PrefUtil.getTimerLength(this);
        timerLengthSeconds = lengthInMinutes * 90L; // 1.5min
        progressBar.setMax(Math.toIntExact(timerLengthSeconds));
    }

    private void setPreviousTimerLength() {
        timerLengthSeconds = PrefUtil.getPreviousTImerLengthSeconds(this);
        progressBar.setMax(Math.toIntExact(timerLengthSeconds));
    }

    private void updateCountdownUI() {
        Long minutesUntilFinished = secondsRemaining / 60;
        Long secondsUntilFinished = secondsRemaining - minutesUntilFinished * 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutesUntilFinished, secondsUntilFinished);
        countdownText.setText(timeLeftFormatted);

        progressBar.setProgress((timerLengthSeconds.intValue()-secondsRemaining.intValue()));
    }

    private void updateButtons() {
        switch (timerState) {
            case RUNNING:
                fab_start.setEnabled(false);
                fab_pause.setEnabled(true);
                fab_stop.setEnabled(true);
                break;
            case STOPPED:
                fab_start.setEnabled(true);
                fab_pause.setEnabled(false);
                fab_stop.setEnabled(false);
                break;
            case PAUSED:
                fab_start.setEnabled(true);
                fab_pause.setEnabled(false);
                fab_stop.setEnabled(true);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
