package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final int NANOSECONDS_CONVERT = 1000000000;
    public static final int MICROSECONDS_CONVERT = 1000;
    public static final int SEXAGESIMA_CONVERT = 60;
    public static final int SEXAGESIMA_FORMAT_CONVERT = 2;
    public static final String BROADCAST_TIME_UPDATE = "BROADCAST_TIME_UPDATE";
    private Button start;
    private TextView tiempo;
    private long inicio;
    private Long terminado=null;
    private RelativeLayout layout;
    private boolean running=false;
    private Button stop;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.tiempo = (TextView) findViewById(R.id.tiempo);
        this.layout = (RelativeLayout) findViewById(R.id.layout);

        this.start = (Button) findViewById(R.id.start);
        this.stop = (Button) findViewById(R.id.stop);


        boolean[] click = {false};
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (click[0] == false) {
                    click[0] = true;
                    MainActivity.this.inicio = System.nanoTime();
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            running=true;
                            terminado=null;
                            while (running){
                                sendBroadcast();
                            }

                            sendBroadcast();

                        }
                    });
                    t.start();
                }
                else{
                    click[0] = false;
                    terminado = System.nanoTime();
                    running=false;
                }
                return false;
            }
        });


        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BROADCAST_TIME_UPDATE)) {

                    if ( terminado != null){
                        String cronometro = getCronometroValue(terminado);
                        tiempo.setText(cronometro);
                        terminado=null;
                    }else{
                        String cronometro = getCronometroValue(System.nanoTime());
                        tiempo.setText(cronometro);

                    }
                }


            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_TIME_UPDATE));





        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.inicio = System.nanoTime();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        running=true;
                        terminado=null;
                        while (running){
                            sendBroadcast();
                        }

                        sendBroadcast();

                    }
                });
                t.start();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminado = System.nanoTime();
                running=false;

            }
        });



    }

    private void sendBroadcast() {
        Intent intent = new Intent(BROADCAST_TIME_UPDATE);
        MainActivity.this.sendBroadcast(intent);
    }

    private String getCronometroValue(long actual) {
        long dif = actual - MainActivity.this.inicio;

        long segundos = dif/ NANOSECONDS_CONVERT;
        long mili = dif - (segundos*NANOSECONDS_CONVERT);
        mili = mili/ MICROSECONDS_CONVERT;


        long minutos = segundos / SEXAGESIMA_CONVERT;
        segundos = segundos % SEXAGESIMA_CONVERT;

        String segString = formatSexagesima(segundos);
        String minutosString = formatSexagesima(minutos);

        // Делаем трехзначное число
       String mil = String.valueOf(mili);
       if(mil.length() > 3){
           mil = mil.substring(0,3);
       }

        // Изначально было mili
        String cronometro =  minutosString + ":" + segString + ":" + mil;
        return cronometro;
    }

    @SuppressLint("DefaultLocale")
    private String formatSexagesima(long value ){
        return String.format("%0" + SEXAGESIMA_FORMAT_CONVERT + "d", value);
    }

}