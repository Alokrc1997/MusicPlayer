package com.example.alok.pyratemusic;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Music extends AppCompatActivity {
    SeekBar seekBar;
    ImageView play, back, next;
    TextView left, right, name;
    static MediaPlayer mediaPlayer;
    String sname;
    String[] songname;
    int pos;
    ArrayList<File> songsList;
    Thread thread;
    int newPos,max;
    SimpleDateFormat dateFormat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        getSupportActionBar().setTitle("Music");
        seekBar = findViewById(R.id.seek);
        play = findViewById(R.id.play);
        name = findViewById(R.id.songName);
        back = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        left = findViewById(R.id.timeLeft);
        right = findViewById(R.id.timeRight);




        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.stop();
            //mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        if (bundle != null) {
            songsList = (ArrayList) bundle.getParcelableArrayList("songlist");
            songname = bundle.getStringArray("songname");
            pos = bundle.getInt("pos", 0);

        }
        name.setText(songname[pos]);
        name.setSelected(true);

        Uri uri = Uri.parse(songsList.get(pos).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        updateThread();


        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if (b)
                    mediaPlayer.seekTo(seekBar.getProgress());

                dateFormat = new SimpleDateFormat("mm:ss");
                left.setText(dateFormat.format(new Date(newPos-30*60*1000)));
                right.setText(dateFormat.format(new Date(max-30*60*1000)));



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setMax(mediaPlayer.getDuration());
                if (mediaPlayer.isPlaying()) {
                    play.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
                    mediaPlayer.pause();
                } else {
                    play.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    if(mediaPlayer==null)
                    Toast.makeText(Music.this,"hello",Toast.LENGTH_SHORT).show();
                    mediaPlayer.start();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              next();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                try{
                    mediaPlayer.reset();
                    mediaPlayer.stop();
                    if(pos==0)
                        pos=songsList.size();
                    Uri u = Uri.parse(songsList.get(--pos).toString());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                    sname = songname[pos];
                    name.setText(sname);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                    updateThread();
                } else {
                    Toast.makeText(Music.this, "Error", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void next() {
        play.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
        try{
            mediaPlayer.reset();
            mediaPlayer.stop();
            pos=(++pos)%songsList.size();
            Uri u = Uri.parse(songsList.get(pos).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
            sname = songname[pos];
            name.setText(sname);

        }catch (Exception e)
        {
            e.printStackTrace();
        }



        if (mediaPlayer != null) {
            mediaPlayer.start();
            updateThread();
        } else {
            Toast.makeText(Music.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateThread() {

        thread = new Thread() {
            @Override
            public void run() {


                try {
                    while (mediaPlayer.isPlaying() && mediaPlayer != null) {
                        thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                 newPos = mediaPlayer.getCurrentPosition();
                                 max = mediaPlayer.getDuration();


                                seekBar.setMax(max);
                                seekBar.setProgress(newPos);

                                if(dateFormat.format(new Date(newPos-30*60*1000)).compareTo(dateFormat.format(new Date(max-30*60*1000-1000)))==0)
                                {
                                    next();
                                }



                            }
                        });

                    }



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            };


        thread.start();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.about:
            {
                startActivity(new Intent(Music.this,About.class));
                break;
            }
            case R.id.exit:
            {
               moveTaskToBack(true);
               break;
            }

        }
        return super.onOptionsItemSelected(item);
    }


}