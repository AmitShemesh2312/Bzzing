package com.example.bzzing_last;

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

import com.google.firebase.database.core.SyncTree;

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

        fragmentManager = getSupportFragmentManager();

        name = getIntent().getStringExtra("name");


        database.listenToChoosingChanges(this);

        GameRoom gameRoom = AppUtilities.gameRoom;
        String activePlayer = gameRoom.getPlayers().get(gameRoom.getRounds()).getName();
        if (name.equals(activePlayer))
        {
            gameRoom.setActivePlayer(activePlayer);
            setRules();
        }
        else
            database.listenToStorageChanges(this);
    }

    public void setRules() {
        GameRoom gameRoom = AppUtilities.gameRoom;
        String activePlayer = gameRoom.getPlayers().get(gameRoom.getRounds()).getName();
        for (int i = 0; i < gameRoom.getPlayers().size(); i++) {
            if(!gameRoom.getPlayers().get(i).getName().equals(activePlayer))
            {
                AppUtilities.gameRoom.addNotPlayer(gameRoom.getPlayers().get(i));
            }
        }
        database.updateAll();
        fragmentSongPicker();
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

    @Override
    public void playHumming(Uri downloadUrl) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(downloadUrl.toString());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer = null;
                }
            });
        } catch (IOException e) {
            Log.e("TAG", "Failed to play audio file", e);
        }
    }


    public void songChoice(View view)//הפעולה מעדכנת את התכונות songName וpic לפי בחירת השיר של המשתמש ומבצעת אפקט
    {
        if (chosen) {
            ObjectAnimator animation = ObjectAnimator.ofFloat(pic, "translationY", 0);
            animation.setDuration(400);
            animation.start();
        }

        String position = view.getTag().toString();
        int picId = getResources().getIdentifier("image" + position, "id", getPackageName());

        pic = findViewById(picId);

        int s = getResources().getIdentifier("name" + position, "id", getPackageName());
        TextView textView = findViewById(s);
        songName = textView.getText().toString();


        ObjectAnimator animation = ObjectAnimator.ofFloat(pic, "translationY", -100f);
        animation.setDuration(400);
        animation.start();
        chosen = true;
    }


    public void startHum(View view) {//אם המשתמש בחר שיר הפעולה תעביר fragment ותעדכן את הfirebase לשיר שבחר
        if (pic != null) {
            chosen = false;
            fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, FragmentHumming.class, null).commit();
            AppUtilities.gameRoom.setCurrentSong(songName);
            database.updateAll();
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

    public String getSongName() {
        return songName;
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


    public void stopRecord()//הפעולה מפסיקה את ההקלטה
    {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
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


    public void dialogAnimation()//הפעולה מראה אנימציה של המסך של טעינה
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_animation);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        database.uploadHum(this, getPlayerIndex());
    }

    public int getPlayerIndex()//הפעולה מחזירה איזה מקום השחקן במערך השחקנים
    {
        ArrayList<Player> arr = AppUtilities.gameRoom.getPlayers();

        int index = -1;
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).getName().equals(name)) {
                index = i;
            }
        }
        return index;
    }

    public int getNotPlayersIndex()
    {
        ArrayList<Player> arr = AppUtilities.gameRoom.getNot_players();
        int index = -1;
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).getName().equals(name)) {
                index = i;
            }
        }
        return index;

    }


    public void moveIntent()//הפעולה מפסיקה את אנימצית הטעינה ומעבירה intent
    {
        AppUtilities.gameRoom.setUploadFinished(true);
        database.updateAll();

        dialog.dismiss();
        dialog = null;
        Intent intent = new Intent(this, AfterHumming.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    public void fragmentChoose() {
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, FragmentGuessingSong.class, null).commit();
    }


    public void chosenGuessedSong(View view) {
        if (chosen) {
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(pic, "scaleX", 1.2f, 1.0f);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(pic, "scaleY", 1.2f, 1.0f);
            animatorX.setDuration(400);
            animatorY.setDuration(400);
            animatorY.start();
            animatorX.start();
        }
        int position = Integer.parseInt(view.getTag().toString());

        int imageId = getResources().getIdentifier("image_guess" + position, "id", getPackageName());
        pic = findViewById(imageId);

        pic.setPivotX(pic.getWidth() / 2);
        pic.setPivotY(pic.getHeight() / 2);

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(pic, "scaleX", 1.0f, 1.2f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(pic, "scaleY", 1.0f, 1.2f);
        animatorX.setDuration(400);
        animatorY.setDuration(400);

        animatorX.start();
        animatorY.start();

        songName = AppUtilities.gameRoom.getSongs().get(AppUtilities.gameRoom.getRounds() + position).getName();
        chosen = true;
    }

    public void songGuess(View view) {
        if (chosen) {
            AppUtilities.gameRoom.getPlayers().get(getPlayerIndex()).setSongGuess(songName);
            //AppUtilities.gameRoom.getNot_players().get(getNotPlayersIndex()).setSongGuess(songName);
            database.updateAll();
            Intent intent = new Intent(this, AfterHumming.class);
            intent.putExtra("name", name);
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