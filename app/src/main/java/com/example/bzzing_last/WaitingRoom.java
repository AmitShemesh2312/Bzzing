package com.example.bzzing_last;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class WaitingRoom extends AppCompatActivity implements WaitingRoomHandler {

    private DB database;
    private boolean readyButton;

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);
        database = new DB();
        database.setWaitingRoom(this);

        readyButton = true;
        name = getIntent().getStringExtra("name");

        database.listenToDocumentChanges(this);

        showRoomCode();
    }

    public void showRoomCode()//הפעולה מדפיסה את הקוד לחדר על המסך
    {
        TextView textView = findViewById(R.id.roomCodeText);
        textView.setText(AppUtilities.gameRoom.getRoomCode());
    }

    public void showPlayerNum()//הפעולה מדפיסה את מספר השחקנים על המסך
    {
        GameRoom gameRoom = AppUtilities.gameRoom;
        int resID = getResources().getIdentifier("playersNum", "id", getPackageName());
        TextView textView = findViewById(resID);
        textView.setText(gameRoom.getPlayersNum() + " / " + gameRoom.getMaxPlayers());
    }

    private void showPlayer() {//הפעולה מעדכנת את מערך השמות המודפס על המסך
        GameRoom gameRoom = AppUtilities.gameRoom;
        ArrayList<Player> arr = gameRoom.getPlayers();

        for (int i = 1; i < gameRoom.getMaxPlayers() + 1; i++) {
            int resID = getResources().getIdentifier("player" + i, "id", getPackageName());
            TextView textView = findViewById(resID);
            textView.setText("");

            int dotID = getResources().getIdentifier("dot" + i, "id", getPackageName());
            ImageView imageView = findViewById(dotID);
            imageView.setVisibility(View.INVISIBLE);
        }

        for (int i = 1; i < arr.size() + 1; i++) {
            int resID = getResources().getIdentifier("player" + i, "id", getPackageName());
            TextView textView = findViewById(resID);

            textView.setText(i + ") " + arr.get(i - 1).getName());

            if (arr.get(i - 1).getName().equals(name)) {
                int dotID = getResources().getIdentifier("dot" + i, "id", getPackageName());
                ImageView imageView = findViewById(dotID);
                imageView.setVisibility(View.VISIBLE);
            }
        }

        showPlayerNum();
    }

   /* @Override
    protected void onStop() {//כאשר שחקן יוצא מן האפליקציה, אם השחקן האחרון יצא הוא נסגר, אם לא הפעולה תעדכן את החדר בהתאם
        super.onStop();
        if(!AppUtilities.gameRoom.getEverybodyReady())
        {
            GameRoom gameRoom = AppUtilities.gameRoom;
            ArrayList<Player> arr = gameRoom.getPlayers();

            int index = getPlayerIndex();
            if (index != -1) {
                if (gameRoom.getPlayersNum() == 1) {
                    database.deleteGameRoom();
                }
                else {
                    arr.remove(index);
                    gameRoom.setPlayersNum(-1);
                    database.updateAll();
                }
            }
            finish();
        }
    }*/


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

    @Override
    public void updateDocumentChanges(GameRoom g) {//אם לא כולם מוכנים, הפעולה תעדכן את הGameRoom
        GameRoom gameRoom = AppUtilities.gameRoom;

        if (!gameRoom.getEverybodyReady()) {
            AppUtilities.gameRoom = g;

            everybodyReady();

            showPlayer();
        }
    }


    public void imReady(View view) {//כאשר שחקן לוחץ על הכפתור, אם מספר השחקנים גדול מ1 הפעולה תעדכן את הכפתור ואת הdatabase
        GameRoom gameRoom = AppUtilities.gameRoom;
        if (gameRoom.getPlayersNum() > 1) {
            int index = getPlayerIndex();
            Button b = findViewById(R.id.ready);
            if (index != -1) {
                if (readyButton) {
                    gameRoom.getPlayers().get(index).setReady(true);
                    b.setBackgroundColor(Color.parseColor("#B22222"));
                    b.setText("CANCEL");
                    readyButton = false;
                } else {
                    gameRoom.getPlayers().get(index).setReady(false);
                    b.setBackgroundColor(Color.parseColor("#126C08"));
                    b.setText("READY!");
                    readyButton = true;
                }
            }
            database.updateAll();
        } else
            Toast.makeText(this, "Bring Another Friend!", Toast.LENGTH_SHORT).show();
    }

    public void everybodyReady()//הפעולה בודקת אם כל השחקנים מוכנים. במידה וכן, תעדכן את הGameRoom ותקרא לפעולה goToGameStarted()
    {
        GameRoom gameRoom = AppUtilities.gameRoom;
        boolean allReady = true;

        for (int i = 0; i < gameRoom.getPlayersNum(); i++) {
            if (!gameRoom.getPlayers().get(i).getReady())
                allReady = false;
        }
        if (allReady) {
            gameRoom.setEverybodyReady(true);
            database.updateAll();
            goToGameStarted();
        }
    }

    public void goToGameStarted()//הפעולה מעבירה intent
    {
        Intent intent = new Intent(this, GameStarted.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {//הפעולה חוסמת את אפשרות הלחיצה על כפתור החזור
        return;
    }
}