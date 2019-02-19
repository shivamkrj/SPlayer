package com.example.splayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button buttonNext,buttonPrevious,buttonPlay;
    TextView songTextLabel;
    SeekBar songSeekBar;
    static MediaPlayer mediaPlayer;
    int position;
    String songName;
    String sName;
    ArrayList<File> mySongs;
    Thread updateSeekBar;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonPlay = findViewById(R.id.buttonPlay);
        songSeekBar = findViewById(R.id.seekBar);
        songTextLabel = findViewById(R.id.song_name_text_view);

        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while(currentPosition<totalDuration){
                    try{
                        sleep(500);
                        if(mediaPlayer!=null)
                        currentPosition = mediaPlayer.getCurrentPosition();
                        songSeekBar.setProgress(currentPosition);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent intent = getIntent();
        songName = intent.getStringExtra("SONG_NAME");
        position = intent.getIntExtra("pos",0);
        mySongs = (ArrayList)intent.getParcelableArrayListExtra("ALL_SONGS");

        sName = mySongs.get(position).getName().toString();
        songTextLabel.setText(sName);
        songTextLabel.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();
        songSeekBar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

    }

    public void pausePlayButton(View view) {
        songSeekBar.setMax(mediaPlayer.getDuration());
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            buttonPlay.setText("Play");
        }else{
            mediaPlayer.start();
            buttonPlay.setText("Pause");
        }
    }

    public void nextButton(View view) {
        position = (position+1)%mySongs.size();
        mediaPlayer.stop();
        mediaPlayer.release();
        Uri uri = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        sName = mySongs.get(position).toString();
        songTextLabel.setText(sName);
        mediaPlayer.start();
        songSeekBar.setProgress(0);
        songSeekBar.setMax(mediaPlayer.getDuration());
    }

    public void previousButton(View view) {
        position = position-1;
        if(position<0)
            position = mySongs.size()-1;
        mediaPlayer.stop();
        mediaPlayer.release();
        Uri uri = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        sName = mySongs.get(position).toString();
        songTextLabel.setText(sName);
        mediaPlayer.start();
        songSeekBar.setProgress(0);
        songSeekBar.setMax(mediaPlayer.getDuration());
    }
}
