package com.example.bzzing_last;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.graphics.Color;


public class NextPlayer extends AppCompatActivity implements NextPlayerHandler {

    private String name;
    private DB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_player);

        database = new DB();

        name = getIntent().getStringExtra("name");

        AppUtilities.gameRoom.setUpdated(false);

        if (name.equals(AppUtilities.gameRoom.getActivePlayer()))
            database.updateField("updated");

        database.getUpdatedGameRoom();

        writeNames();
        timer();
    }


    public void writeNames() {
        GameRoom gameRoom = AppUtilities.gameRoom;

        TextView rounds = findViewById(R.id.rounds);
        rounds.setText((gameRoom.getRounds() + 1) + " / " + gameRoom.getPlayers().size());


        for (int i = 0; i < gameRoom.getPlayers().size(); i++) {
            String player_name = gameRoom.getPlayers().get(i).getName();

            int id = getResources().getIdentifier("nextPlayer_Player" + i, "id", getPackageName());
            TextView textView = findViewById(id);
            textView.setText(player_name);

            if (player_name.equals(gameRoom.getActivePlayer()))
                textView.setTextColor(Color.parseColor("#303F9F"));
        }
    }

    public void timer() {
        TextView timer = findViewById(R.id.nextPlayer_timer);
        new CountDownTimer(3000, 1000) {
            int count = 3;

            @Override
            public void onTick(long l) {
                timer.setText(String.valueOf(count));
                count--;
            }

            @Override
            public void onFinish() {

                Context context = timer.getContext();

                Intent intent = new Intent(context, GameStarted.class);
                intent.putExtra("name", name);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }.start();
    }


    public void updateGameRoom(GameRoom g) {
        AppUtilities.gameRoom = g;
    }

    @Override
    public void onBackPressed() {//הפעולה חוסמת את אפשרות הלחיצה על כפתור החזור
        return;
    }
}