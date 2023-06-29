package com.example.bzzing_last;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.core.SyncTree;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class GameStarted extends AppCompatActivity implements GameStartedHandler {
    DB database = new DB();
    private String name;

    private FragmentManager fragmentManager;

    private boolean chosen = false;
    private ImageView pic;
    private String songName;
    private int halfSeconds = 0;

    private MediaRecorder mediaRecorder;

    private MediaPlayer mediaPlayer;


    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_started);

        database.setGameStarted(this);

        database.updateeeThisGameRoom();

        fragmentManager = getSupportFragmentManager();

        name = getIntent().getStringExtra("name");


        database.listenToChoosingChanges(this);


        GameRoom gameRoom = AppUtilities.gameRoom;

        String activePlayer = gameRoom.getPlayers().get(gameRoom.getRounds()).getName();
        if (name.equals(activePlayer))
            fragmentSongPicker();
        else
            database.listenToStorageChanges(this);
    }




    public void fragmentSongPicker() { //אם השם שהתקבל בעזרת intent שווה לשם של שחקן, לשחקן יועבר fragment
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, FragmentSongPicker.class, null).commit();
    }


    public void hear(View view) {
        if (mediaPlayer == null)
            database.getHumming(AppUtilities.gameRoom.getRounds());
        else {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }


    public void songChoice(View view)//הפעולה מעדכנת את התכונות songName וpic לפי בחירת השיר של המשתמש ומבצעת אפקט
    {
        if (chosen) {
            ObjectAnimator animationX = ObjectAnimator.ofFloat(pic, "scaleX", 1.2f, 1.0f);
            ObjectAnimator animationY = ObjectAnimator.ofFloat(pic, "scaleY", 1.2f, 1.0f);
            animationX.setDuration(400);
            animationY.setDuration(400);
            animationX.start();
            animationY.start();

            ObjectAnimator animationUp = ObjectAnimator.ofFloat(pic, "translationY", 0);
            animationUp.setDuration(400);
            animationUp.start();
        }
        String position = view.getTag().toString();

        int picId = getResources().getIdentifier("image" + position, "id", getPackageName());

        pic = findViewById(picId);

        int s = getResources().getIdentifier("name" + position, "id", getPackageName());
        TextView textView = findViewById(s);
        songName = textView.getText().toString();


        pic.setPivotX(pic.getWidth() / 2);
        pic.setPivotY(pic.getHeight() / 2);
        ObjectAnimator animationX = ObjectAnimator.ofFloat(pic, "scaleX", 1.0f, 1.2f);
        ObjectAnimator animationY = ObjectAnimator.ofFloat(pic, "scaleY", 1.0f, 1.2f);
        animationX.setDuration(400);
        animationY.setDuration(400);
        animationX.start();
        animationY.start();

        ObjectAnimator animationUp = ObjectAnimator.ofFloat(pic, "translationY", -30f);
        animationUp.setDuration(400);
        animationUp.start();
        chosen = true;
    }


    public void startHum(View view) {//אם המשתמש בחר שיר הפעולה תעביר fragment ותעדכן את הfirebase לשיר שבחר
        if (pic != null) {
            chosen = false;
            fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, FragmentHumming.class, null).commit();
            AppUtilities.gameRoom.setCurrentSong(songName);
            database.updateField("currentSong");
            timer();
        } else
            Toast.makeText(this, "Choose a Song To Hum!", Toast.LENGTH_SHORT).show();
    }

    public void timer()//הפעולה מבצעת טיימר של 3 שניות כדי להתכונן
    {
        new CountDownTimer(3000, 100) {
            public void onTick(long millisUntilFinished) {
                DecimalFormat formatter = new DecimalFormat("#0.0");
                String time = formatter.format(millisUntilFinished / 1000.0);
                TextView countdownTimer = findViewById(R.id.countdown_timer);
                countdownTimer.setText(time);
            }

            public void onFinish() {
                TextView countdownTimer = findViewById(R.id.countdown_timer);
                countdownTimer.setText("0.0");
                startRecord();
            }
        }.start();
    }


    public void Record()//הפעולה מתחילה הקלטה
    {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecordingFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "Humming.mp3");
        return file.getPath();
    }

    private void startRecord()//הפעולה מבצעת טיימר של 5 שניות בזמן שמקליטים את הזמזום
    {
        ImageView imageView = findViewById(R.id.microphone);
        Record();
        new CountDownTimer(5000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                DecimalFormat formatter = new DecimalFormat("#0.0");
                String time = formatter.format(millisUntilFinished / 1000.0);
                TextView countdownTimer = findViewById(R.id.countdown_timer);
                countdownTimer.setText(time);
                halfSeconds++;
                if (halfSeconds % 5 == 1) {
                    if (imageView.getVisibility() == View.INVISIBLE) {
                        imageView.setVisibility(View.VISIBLE);
                    } else {
                        imageView.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFinish() {
                stopRecord();
                TextView countdownTimer = findViewById(R.id.countdown_timer);
                countdownTimer.setText("0.0");
                imageView.setVisibility(View.INVISIBLE);
                dialogAnimation();
            }
        }.start();
    }

    public void stopRecord()//הפעולה מפסיקה את ההקלטה
    {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    public void dialogAnimation()//הפעולה מראה אנימציה של המסך של טעינה
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_animation);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        database.uploadHum(this, AppUtilities.gameRoom.getPlayerIndex(name));
    }


    public void moveIntent()//הפעולה מפסיקה את אנימצית הטעינה ומעבירה intent
    {
        AppUtilities.gameRoom.setUploadFinished(true);
        database.updateField("uploadFinished");

        dialog.dismiss();
        dialog = null;


        Intent intent = new Intent(this, AfterHumming.class);
        intent.putExtra("name", name);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void fragmentChoose() {
        database.stopListeningStorageChanges();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, FragmentGuessingSong.class, null).commit();
    }


    public void chosenGuessedSong(View view) {
        if (chosen) {
            ObjectAnimator animationX = ObjectAnimator.ofFloat(pic, "scaleX", 1.2f, 1.0f);
            ObjectAnimator animationY = ObjectAnimator.ofFloat(pic, "scaleY", 1.2f, 1.0f);
            animationX.setDuration(400);
            animationY.setDuration(400);
            animationX.start();
            animationY.start();

            ObjectAnimator animationUp = ObjectAnimator.ofFloat(pic, "translationY", 0);
            animationUp.setDuration(400);
            animationUp.start();
        }
        String position = view.getTag().toString();

        int picId = getResources().getIdentifier("image_guess" + position, "id", getPackageName());

        pic = findViewById(picId);

        int s = getResources().getIdentifier("song_name_guess" + position, "id", getPackageName());
        TextView textView = findViewById(s);
        songName = textView.getText().toString();


        pic.setPivotX(pic.getWidth() / 2);
        pic.setPivotY(pic.getHeight() / 2);
        ObjectAnimator animationX = ObjectAnimator.ofFloat(pic, "scaleX", 1.0f, 1.2f);
        ObjectAnimator animationY = ObjectAnimator.ofFloat(pic, "scaleY", 1.0f, 1.2f);
        animationX.setDuration(400);
        animationY.setDuration(400);
        animationX.start();
        animationY.start();

        ObjectAnimator animationUp = ObjectAnimator.ofFloat(pic, "translationY", -30f);
        animationUp.setDuration(400);
        animationUp.start();
        chosen = true;
    }

    public void songGuess(View view) {
        if (chosen) {
            AppUtilities.gameRoom.getNotPlayers().get(AppUtilities.gameRoom.getNotPlayerIndex(name)).setSongGuess(songName);
            database.updateField("notPlayers");

            database.stopListeningChooseChanges();

            Intent intent = new Intent(this, AfterHumming.class);
            intent.putExtra("name", name);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else
            Toast.makeText(this, "Guess The Song!", Toast.LENGTH_SHORT).show();
    }


    public void updateDocumentChanges(GameRoom g) {
        AppUtilities.gameRoom = g;
    }


    public void onBackPressed() {//הפעולה חוסמת את אפשרות הלחיצה על כפתור החזור
        return;
    }
}