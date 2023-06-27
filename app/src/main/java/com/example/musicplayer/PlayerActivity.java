package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button btnplay,btnnext,btnprev,btnfastforward,btnfastbackward;
    TextView txtsname,txtsstart,txtstop;
    SeekBar seekmusic;
    String sname;
    public static final String EXTRA_NAME="song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ImageView imageview;
    ArrayList<File> mySongs;
    Thread updateSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnprev=findViewById(R.id.btnnprev);
        btnnext=findViewById(R.id.btnnext);
        btnplay=findViewById(R.id.playbtn);
        btnfastforward=findViewById(R.id.ff);
        btnfastbackward=findViewById(R.id.fr);
        txtsname=findViewById(R.id.txtsn);
        txtsstart=findViewById(R.id.txtstart);
        txtstop=findViewById(R.id.txtend);
        seekmusic=findViewById(R.id.seekbar);
        imageview=findViewById(R.id.imageview);
        /*barVisualizer.findViewById(R.id.blast);*/


        Intent i=getIntent();
        Bundle bundle=i.getExtras();
        mySongs=(ArrayList) bundle.getParcelableArrayList("songs");
        position=bundle.getInt("pos",0);
        txtsname.setSelected(true);
        sname=mySongs.get(position).getName();
        txtsname.setText(sname);

        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Uri uri=Uri.parse(mySongs.get(position).toString());

        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        updateSeekBar=new Thread()
        {
            @Override
            public  void run() throws  IllegalStateException {
                int totalDuration=mediaPlayer.getDuration();
                int currPosition=0;
                while(currPosition<totalDuration)
                {
                    try
                    {
                        sleep(500);
                        currPosition=mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(currPosition);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };



        seekmusic.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();


        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.av_dark_blue), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.av_deep_orange),PorterDuff.Mode.SRC_IN);
        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    btnplay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else
                {
                    btnplay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                    TranslateAnimation MoveAnimation=new TranslateAnimation(-25,25,-25,25);
                    MoveAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                    MoveAnimation.setDuration(600);
                    MoveAnimation.setFillEnabled(true);
                    MoveAnimation.setFillAfter(true);
                    MoveAnimation.setRepeatMode(MoveAnimation.REVERSE);
                    MoveAnimation.setRepeatCount(1);
                    imageview.startAnimation(MoveAnimation);

                }
            }

        });

        /*int audioSessionId=mediaPlayer.getAudioSessionId();
        if(audioSessionId!=-1)
        {
            barVisualizer.setAudioSessionId(audioSessionId);
        }*/

        String endTime=createTime(mediaPlayer.getDuration());
        txtstop.setText(endTime);

        final Handler handler =new Handler();
        final int delay=1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime=createTime(mediaPlayer.getCurrentPosition());
                txtsstart.setText(currentTime);
                handler.postDelayed(this,delay);
            }
        },delay);


        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=(position+1)%mySongs.size();
                Uri uri=Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                sname=mySongs.get(position).getName();
                String endTime=createTime(mediaPlayer.getDuration());
                txtstop.setText(endTime);
                txtsname.setText(sname);
                mediaPlayer.start();
                startAnimation(imageview,360f);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btnnext.performClick() ;
                    }

                });
            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=(position-1)%mySongs.size();
                if(position<0)
                    position =mySongs.size()-1;

                Uri uri=Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                sname=mySongs.get(position).getName();
                String endTime=createTime(mediaPlayer.getDuration());
                txtstop.setText(endTime);
                txtsname.setText(sname);
                mediaPlayer.start();
                startAnimation(imageview,-360f);
            }
        });
        btnfastforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        btnfastbackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });

    }
    public void startAnimation(View view,Float degree)
    {
        ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(view,"rotation",0f,degree);
        objectAnimator.setDuration(1000);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();

    }
    public String createTime(int duration)
    {
        String time="";
        int min=duration/1000/60;
        int sec=duration/1000%60;
        time+=min+":";
        if(sec<10)
        {
            time+="0";
        }
        time+=sec;
        return time;
    }
}
