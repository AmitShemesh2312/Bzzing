package com.example.bzzing_last;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements MainActivityHandler {
    private int choice = 0;
    private DB database = new DB();
    private String roomCode = "";

    private int MICROPHONE_PERMISSION_CODE = 200;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database.setActivity(this);

        String name = getIntent().getStringExtra("name");

        if (name != null)
        {
            TextView textView = findViewById(R.id.typeName);
            textView.setText(name);
        }



        Button b = findViewById(R.id.btnNextPage);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMicrophonePermission();
            }
        });
    }


    public void roomChoice(View view) { //בודקת על איזה כפתור לחץ המשתמש. לפתוח חדר חדש או להתחבר לחדר קיים
        Button button = (Button)view;
        String b = button.getResources().getResourceEntryName(button.getId());
        button.setBackgroundColor(Color.parseColor("#AA00FF"));
        EditText roomCode = findViewById(R.id.typeRoomCode);
        if(b.equals("btnCreateRoom")) {
            choice = 1;
            roomCode.setVisibility(View.INVISIBLE);
            findViewById(R.id.btnJoinRoom).setBackgroundColor(Color.parseColor("#3F51B5"));
        }
        else {
            choice = 2;
            roomCode.setVisibility(View.VISIBLE);
            findViewById(R.id.btnCreateRoom).setBackgroundColor(Color.parseColor("#3F51B5"));
        }
    }

    public void nextPage() { //אם המשתמש לחץ על המשך, הפעולה תפעל בהתאם לפי הכפתור בו בחר
        Button b = findViewById(R.id.btnNextPage);
        b.setEnabled(false);

        EditText name = findViewById(R.id.typeName);
        String n = name.getText().toString();
        EditText rC = findViewById(R.id.typeRoomCode);
        this.roomCode = rC.getText().toString();
        if (choice != 0 && !n.equals(""))
        {
            if(choice == 1)
                createRoom();
            else{
                if (this.roomCode.equals("")) {
                    Toast.makeText(this, "Enter room code", Toast.LENGTH_SHORT).show();
                    b.setEnabled(true);
                }
                else
                    database.findGameRoomByNumber(Integer.parseInt(this.roomCode));
            }
        }
        else {
            Toast.makeText(this, "Enter the required fields", Toast.LENGTH_SHORT).show();
            b.setEnabled(true);
        }
    }


    private void createRoom() { //אם השם קטן מ8 תווים, הפעולה תיצור חדר משחק חדש ותכניס אותו אליו
        Button b = (Button) findViewById(R.id.btnNextPage);
        EditText userName = findViewById(R.id.typeName);
        String name = userName.getText().toString();

        if(name.length() < 8)
        {
            AppUtilities.gameRoom = new GameRoom();
            AppUtilities.gameRoom.addPlayer(new Player(name));

            randomNumbers();
        }
        else
        {
            Toast.makeText(this, "Name is Too Long!", Toast.LENGTH_SHORT).show();
            b.setEnabled(true);
        }
    }



    @Override
    public void handleFindGameRoomByNumber(String roomExist, GameRoom g) //במידה והחדר קיים תקרא לפעולה validateJoinName, ובמידה שלא תחזיר הודעה מתאימה
    {
        Button b = findViewById(R.id.btnNextPage);

        if(roomExist.equals("failed"))
        {
            Toast.makeText(this, "Try Again!", Toast.LENGTH_SHORT).show();
            b.setEnabled(true);
        }
        else if(roomExist.equals("gameRoomNotExist"))
        {
            Toast.makeText(this, "Room Doesn't Exist!", Toast.LENGTH_SHORT).show();
            b.setEnabled(true);
        }
        else
        {
            AppUtilities.gameRoom = g;
            if(g.everybodyReady())
            {
                Toast.makeText(this, "Game is Already Running!", Toast.LENGTH_SHORT).show();
                AppUtilities.gameRoom = null;
                b.setEnabled(true);
            }
            else {
                validateJoinName();
            }
        }
    }

    public void validateJoinName(){//בודקת אם יש מקום בחדר והשם תקין, אם כן תקרא לjoinRoom
        GameRoom gameRoom = AppUtilities.gameRoom;

        Button b = findViewById(R.id.btnNextPage);

        ArrayList<Player> arr = gameRoom.getPlayers();
        
        EditText text = findViewById(R.id.typeName);
        String name = text.getText().toString();

        if(gameRoom.getPlayers().size() < gameRoom.getMaxPlayers()) {
            boolean exist = false;
            for (int i = 0; i < gameRoom.getPlayers().size(); i++) {
                if(arr.get(i).getName().equals(name))
                    exist = true;
            }
            if(exist){
                Toast.makeText(this, "Name is Taken!", Toast.LENGTH_SHORT).show();
                b.setEnabled(true);
            }
            else{
                if(!(name.length() < 8))
                {
                    Toast.makeText(this, "Name is Too Long!", Toast.LENGTH_SHORT).show();
                    b.setEnabled(true);
                }
                else
                    joinRoom();
            }
        }
        else
        {
            Toast.makeText(this, "Game is Already Full!", Toast.LENGTH_SHORT).show();
            b.setEnabled(true);
        }
    }
    public void joinRoom() //הפעולה מעדכנת את הdatabase
    {
        EditText text = findViewById(R.id.typeName);
        String name = text.getText().toString();

        AppUtilities.gameRoom.addPlayer(new Player(name));
        database.updateGameRoom();
    }
    public void handleUpdateGameRoom(boolean updateSucceed)// אם DB הצליח לעדכן את הFireBase, יעביר לWaiting Room
    {
        EditText text = findViewById(R.id.typeName);
        String name = text.getText().toString();
        if (updateSucceed)
        {
            Intent intent = new Intent(this, WaitingRoom.class);
            intent.putExtra("name", name);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else
        {
            Button button = findViewById(R.id.btnNextPage);
            Toast.makeText(this, "Try Again!", Toast.LENGTH_SHORT).show();
            button.setEnabled(true);
        }
    }


    public void handleAddGameRoom(boolean addedGameRoom) {//אם DB העלה GameRoom חדש לFireBase הפעולה תעביר לWaiting Room
        if (addedGameRoom){
            EditText text = findViewById(R.id.typeName);
            String name = text.getText().toString();

            Intent intent = new Intent(MainActivity.this, WaitingRoom.class);
            intent.putExtra("name", name);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else {
            Button b = findViewById(R.id.btnNextPage);
            Toast.makeText(this, "Try Again!", Toast.LENGTH_SHORT).show();
            b.setEnabled(true);
        }
    }

    @Override
    public void roomExistResult(boolean alreadyExist, String roomCode) {// אם אין חדר בעל אותו קוד הפעולה תוסיף GameRoom לdatabase. אם יש תקרא שוב לrandomNumbers
        if (alreadyExist)
            randomNumbers();
        else
        {
            AppUtilities.gameRoom.setRoomCode(roomCode);
            database.addGameRoom();
        }
    }

    public void randomNumbers()//מגריל מספר לחדר משחק
    {
        Random rnd = new Random();
        int r = rnd.nextInt(899999) + 100000;
        String rC = "" + r;
        database.roomExist(rC);
    }


    public void getMicrophonePermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
        else
            nextPage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MICROPHONE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                nextPage();
            else
                Toast.makeText(this, "Give a permission to the microphone!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {//הפעולה חוסמת את אפשרות הלחיצה על כפתור החזור
        return;
    }
}