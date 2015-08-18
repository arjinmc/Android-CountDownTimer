package com.arjinmc.countdowntimer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //ui countdown
    private Button btnStart;
    private Button btnStop;
    private TextView tvTime;

    //service countdown
    private Button btnServiceStart;
    private Button btnServiceStop;
    private TextView tvServiceTime;

    private long timer_unit = 1000;
    private long distination_total = timer_unit*10;
    private long timer_couting;


    private int timerStatus = CountDownTimerUtil.PREPARE;

    private Timer timer;
    private TimerTask timerTask;

    private CountDownTimerService countDownTimerService;


    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if(timer_couting==distination_total){
                        btnStart.setText("START");
                    }
                    tvTime.setText(formateTimer(timer_couting));
                    break;
                case 2:
                    tvServiceTime.setText(formateTimer(countDownTimerService.getCountingTime()));
                    break;
            }
        }
    };

    private class MyCountDownLisener implements CountDownTimerListener {

        @Override
        public void onChange() {
           mHandler.sendEmptyMessage(2);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);
        tvTime = (TextView) findViewById(R.id.tv_time);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        initTimerStatus();


        btnServiceStart = (Button) findViewById(R.id.btn_start2);
        btnServiceStop = (Button) findViewById(R.id.btn_stop2);
        tvServiceTime = (TextView) findViewById(R.id.tv_time2);

        btnServiceStart.setOnClickListener(this);
        btnServiceStop.setOnClickListener(this);

        countDownTimerService = CountDownTimerService.getInstance(new MyCountDownLisener());
        initServiceCountDownTimerStatus();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                switch (timerStatus){
                    case CountDownTimerUtil.PREPARE:
                        startTimer();
                        timerStatus = CountDownTimerUtil.START;
                        btnStart.setText("PAUSE");
                        break;
                    case CountDownTimerUtil.START:
                        timer.cancel();
                        timerStatus = CountDownTimerUtil.PASUSE;
                        btnStart.setText("RESUME");
                        break;
                    case CountDownTimerUtil.PASUSE:
                        startTimer();
                        timerStatus = CountDownTimerUtil.START;
                        btnStart.setText("PAUSE");
                        break;
                }
                break;
            case R.id.btn_stop:
                if(timer!=null){
                    timer.cancel();
                    initTimerStatus();
                    mHandler.sendEmptyMessage(1);
                }
                break;
            case R.id.btn_start2:
                switch (countDownTimerService.getTimerStatus()){
                    case CountDownTimerUtil.PREPARE:
                        countDownTimerService.startCountDown();
                        btnServiceStart.setText("PAUSE");
                        break;
                    case CountDownTimerUtil.START:
                        countDownTimerService.pauseCountDown();
                        btnServiceStart.setText("RESUME");
                        break;
                    case CountDownTimerUtil.PASUSE:
                        countDownTimerService.startCountDown();
                        btnServiceStart.setText("PAUSE");
                        break;
                }
                break;
            case R.id.btn_stop2:
                btnServiceStart.setText("START");
                countDownTimerService.stopCountDown();
                break;
        }
    }

    /**
     * init timer status
     */
    private void initTimerStatus(){
        timerStatus = CountDownTimerUtil.PREPARE;
        timer_couting = distination_total;
    }

    /**
     * start count down
     */
    private void startTimer(){
        timer = new Timer();
        timerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(timerTask, 0, timer_unit);
    }

    /**
     * formate timer shown in textview
     * @param time
     * @return
     */
    private String formateTimer(long time){
        String str = "00:00:00";
        int hour = 0;
        if(time>=1000*3600){
            hour = (int)(time/(1000*3600));
            time -= hour*1000*3600;
        }
        int minute = 0;
        if(time>=1000*60){
            minute = (int)(time/(1000*60));
            time -= minute*1000*60;
        }
        int sec = (int)(time/1000);
        str = formateNumber(hour)+":"+formateNumber(minute)+":"+formateNumber(sec);
        return str;
    }

    /**
     * formate time number with two numbers auto add 0
     * @param time
     * @return
     */
    private String formateNumber(int time){
        return String.format("%02d", time);
    }


    /**
     * count down task
     */
    private class MyTimerTask extends TimerTask{

        @Override
        public void run() {
            timer_couting -=timer_unit;
            if(timer_couting==0){
                cancel();
                initTimerStatus();
            }
            mHandler.sendEmptyMessage(1);
        }
    }

    /**
     * init countdowntimer buttons status for servce
     */
    private void initServiceCountDownTimerStatus(){
        switch (countDownTimerService.getTimerStatus()){
            case CountDownTimerUtil.PREPARE:
                btnServiceStart.setText("START");
                break;
            case CountDownTimerUtil.START:
                btnServiceStart.setText("PAUSE");
                break;
            case CountDownTimerUtil.PASUSE:
                btnServiceStart.setText("RESUME");
                break;
        }
    }

}

