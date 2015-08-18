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

    private Button btnStart;
    private Button btnStop;
    private TextView tvTime;

    private long timer_unit = 1000;
    private long distination_total = timer_unit*10;
    private long timer_couting;

    private final int PREPARE = 0;
    private final int START = 1;
    private final int PASUSE = 2;

    private int timerSatus = PREPARE;

    private Timer timer;
    private TimerTask timerTask;


    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(timer_couting==distination_total){
                btnStart.setText("START");
            }
            tvTime.setText(formateTimer(timer_couting));
        }
    };

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


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                switch (timerSatus){
                    case PREPARE:
                        startTimer();
                        timerSatus = START;
                        btnStart.setText("PAUSE");
                        break;
                    case START:
                        timer.cancel();
                        timerSatus = PASUSE;
                        btnStart.setText("RESUME");
                        break;
                    case PASUSE:
                        startTimer();
                        timerSatus = START;
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
        }
    }

    /**
     * init timer status
     */
    private void initTimerStatus(){
        timerSatus = PREPARE;
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

}

