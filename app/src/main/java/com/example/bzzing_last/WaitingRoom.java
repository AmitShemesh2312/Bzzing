package com.example.bzzing_last;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

public class WaitingRoom extends AppCompatActivity implements WaitingRoomHandler {

    private DB database;
    private boolean readyButton;
    private String name;
    private boolean next = true;

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
        textView.setText(gameRoom.getPlayers().size() + " / " + gameRoom.getMaxPlayers());
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

//    @Override
//    protected void onStop() {//כאשר שחקן יוצא מן האפליקציה, אם השחקן האחרון יצא הוא נסגר, אם לא הפעולה תעדכן את החדר בהתאם
//        super.onStop();
//        if(!AppUtilities.gameRoom.getEverybodyReady())
//        {
//            GameRoom gameRoom = AppUtilities.gameRoom;
//            ArrayList<Player> arr = gameRoom.getPlayers();
//
//            int index = getPlayerIndex();
//            if (index != -1) {
//                if (gameRoom.getPlayersNum() == 1) {
//                    database.deleteGameRoom();
//                }
//                else {
//                    arr.remove(index);
//                    gameRoom.setPlayersNum(-1);
//                    database.updateAll();
//                }
//            }
//            finish();
//        }
//    }


    @Override
    public void updateDocumentChanges(GameRoom g) {//אם לא כולם מוכנים, הפעולה תעדכן את הGameRoom
        AppUtilities.gameRoom = g;

        if(!g.getActivePlayer().equals("") && next)
        {
            goToGameStarted();
            next = false;
        }

        if (!g.everybodyReady())
            showPlayer();

        else
        {
            if(name.equals(AppUtilities.gameRoom.getPlayers().get(0).getName()))
                setRules();
        }
    }


    public void imReady(View view) {//כאשר שחקן לוחץ על הכפתור, אם מספר השחקנים גדול מ1 הפעולה תעדכן את הכפתור ואת הdatabase
        GameRoom gameRoom = AppUtilities.gameRoom;
        if (gameRoom.getPlayers().size() > 1) {
            int index = gameRoom.getPlayerIndex(name);
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
            database.updateField("players");
        } else
            Toast.makeText(this, "Bring Another Friend!", Toast.LENGTH_SHORT).show();
    }


    public void setRules()
    {
        GameRoom gameRoom = AppUtilities.gameRoom;
        gameRoom.setActivePlayer(name);

        for (int i = 0; i < gameRoom.getPlayers().size(); i++) {
            if (!gameRoom.getPlayers().get(i).getName().equals(name))
                gameRoom.addGuesser(new Guesser(gameRoom.getPlayers().get(i).getName()));
        }


        database.updateAll();

        goToGameStarted();
    }

    public void goToGameStarted()//הפעולה מעבירה intent
    {
        database.stopListeningDocumentChanges();
        Intent intent = new Intent(this, nextPlayer.class);
        intent.putExtra("name", name);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {//הפעולה חוסמת את אפשרות הלחיצה על כפתור החזור
        return;
    }
}