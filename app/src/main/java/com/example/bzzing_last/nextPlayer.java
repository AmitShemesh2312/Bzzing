package com.example.bzzing_last;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class nextPlayer extends AppCompatActivity {

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_player);

        name = getIntent().getStringExtra("name");

        writeNames();
        timer();
    }

    public void writeNames()
    {
        GameRoom gameRoom = AppUtilities.gameRoom;

        TextView rounds = findViewById(R.id.rounds);
        rounds.setText((gameRoom.getRounds() + 1) + " / " + gameRoom.getPlayers().size());


        for (int i = 0; i < gameRoom.getPlayers().size(); i++) {
            int id = getResources().getIdentifier( "nextPlayer_Player" + i, "id", getPackageName());
            TextView textView = findViewById(id);
            textView.setText(gameRoom.getPlayers().get(i).getName());
        }
    }

    public void timer()
    {
        TextView timer = findViewById(R.id.nextPlayer_timer);
        new CountDownTimer(3000, 1000)
        {
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
}