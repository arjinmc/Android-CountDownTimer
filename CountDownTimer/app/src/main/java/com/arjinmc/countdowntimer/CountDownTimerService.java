package com.arjinmc.countdowntimer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * CountDownTimer Service
 * Created by Eminem Lu on 18/8/15.
 * Email arjinmc@hotmail.com
 */
public class CountDownTimerService extends Service {

    private final long timer_unit =1000;
    private long distination_total = timer_unit*100;
    private Timer timer;
    private MyTimerTask timerTask;

    private long timer_couting = distination_total;


    private int timerStatus = CountDownTimerUtil.PREPARE;

    public static CountDownTimerService countDownTimerService;

    private static CountDownTimerListener mCountDownTimerListener;

    public static CountDownTimerService getInstance(CountDownTimerListener countDownTimerListener){
        if(countDownTimerService==null){
            countDownTimerService = new CountDownTimerService();
        }
        setCountDownTimerListener(countDownTimerListener);

        return  countDownTimerService;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * get countdowan time
     * @return
     */
    public long getCountingTime(){
        return timer_couting;
    }

    /**
     * get current timer status
     * @return
     */
    public int getTimerStatus(){
        return  timerStatus;
    }

    /**
     * start
     */
    public void startCountDown(){
        startTimer();
        timerStatus = CountDownTimerUtil.START;
    }

    /**
     * paust
     */
    public void pauseCountDown(){
        timer.cancel();
        timerStatus = CountDownTimerUtil.PASUSE;
    }

    /**
     * stop
     */
    public void stopCountDown(){
        if(timer!=null){
            timer.cancel();
            initTimerStatus();
            mCountDownTimerListener.onChange();
        }
    }

    public static void  setCountDownTimerListener(CountDownTimerListener countDownTimerListener){
        mCountDownTimerListener = countDownTimerListener;
    }

    /**
     * bindr
     */
    public class CountDownTimerServiceBinder extends Binder {
        /**
         * get this service
         * @return
         */
        public CountDownTimerService getService(){
            return CountDownTimerService.this;
        }
    }

    /**
     * count down task
     */
    private class MyTimerTask extends TimerTask {


        @Override
        public void run() {
            timer_couting -=timer_unit;
            Log.e("timmer", timer_couting + "");
            mCountDownTimerListener.onChange();
            if(timer_couting==0){
                cancel();
                initTimerStatus();
            }
        }
    }

    /**
     * init timer status
     */
    private void initTimerStatus(){
        timer_couting = distination_total;
        timerStatus = CountDownTimerUtil.PREPARE;
    }

    /**
     * start count down
     */
    private void startTimer(){
        timer = new Timer();
        timerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(timerTask, 0, timer_unit);
    }
}
