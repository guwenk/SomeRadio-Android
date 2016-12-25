package com.github.guwenk.smuradio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private String pass = "HackMe"; //Неплохо бы добавить хэширование пароля, например, SHA-1
    MediaPlayer mediaPlayer;
    AudioManager am;
    boolean bRadioCreate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        am = (AudioManager) getSystemService(AUDIO_SERVICE);

        final ToggleButton toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton.isChecked()){
                    mediaPlayer = new MediaPlayer();
                    new RadioPlayer().execute();
                }else{
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        });

        Button btnToTrackOrder = (Button)findViewById(R.id.btnToTrackOrder);
        btnToTrackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VoteActivity.class);
                startActivity(intent);
            }
        });

        final EditText etPass = (EditText)findViewById(R.id.etPass);

        Button btnNext = (Button)findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(etPass.getText().toString(), pass)){
                        new UrlRequest("next").start();
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        Button btnPrev = (Button)findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(etPass.getText().toString(), pass)){
                        new UrlRequest("prev").start();
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    protected class RadioPlayer extends AsyncTask<String, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Загрузка может занять более 10 секунд", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                mediaPlayer.setDataSource("http://free.radioheart.ru:8000/guwenk");
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.prepareAsync();
                bRadioCreate = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
