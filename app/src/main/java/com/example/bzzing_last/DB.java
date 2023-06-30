package com.example.bzzing_last;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DB {

    FirebaseFirestore db;
    StorageReference storageRef;

    MainActivityHandler activity;
    WaitingRoomHandler waitingRoom;
    GameStartedHandler gameStarted;
    AfterHummingHandler afterHumming;
    NextPlayerHandler nextPlayer;
    EndHandler end;


    private ListenerRegistration documentChanges;
    private ListenerRegistration storageChanges;
    private ListenerRegistration chooseChanges;
    private ListenerRegistration endChanges;


    public DB() {
        db = FirebaseFirestore.getInstance();
    }

    public void setActivity(MainActivityHandler activity) {
        this.activity = activity;
    }

    public void setWaitingRoom(WaitingRoomHandler waitingRoom) {
        this.waitingRoom = waitingRoom;
    }

    public void setGameStarted(GameStartedHandler gameStarted) {
        this.gameStarted = gameStarted;
        this.storageRef = FirebaseStorage.getInstance().getReference();
    }

    public void setAfterUploadHumming(AfterHummingHandler afterHumming) {
        this.afterHumming = afterHumming;
    }

    public void setNextPlayer(NextPlayerHandler nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public void setEnd(EndHandler endHandler)
    {
        this.end = endHandler;
        this.storageRef = FirebaseStorage.getInstance().getReference();
    }


    public void updateGameRoom()//הפעולה מעדכנת את הGameRoom וקוראת בהתאם לactivity או לgameStarted
    {
        GameRoom gameRoom = AppUtilities.gameRoom;
        db.collection("GameRooms")
                .document(gameRoom.getRoomCode())
                .update(gameRoom.GameRoomToHashMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            if (activity != null)
                                activity.handleUpdateGameRoom(true);
                            else if (activity != null)
                                activity.handleUpdateGameRoom(false);
                    }
                });
    }

    public void updateAll() {
        GameRoom gameRoom = AppUtilities.gameRoom;
        db.collection("GameRooms")
                .document(gameRoom.getRoomCode())
                .update(gameRoom.GameRoomToHashMap());
    }

    public void updateField(String field) {
        GameRoom gameRoom = AppUtilities.gameRoom;
        db.collection("GameRooms")
                .document(gameRoom.getRoomCode())
                .update(gameRoom.getField(field));
    }


    public void addGameRoom() {//הפעולה מוסיפה GameRoom לdatabase
        GameRoom gameRoom = AppUtilities.gameRoom;
        db.collection("GameRooms").document(gameRoom.getRoomCode())
                .set(gameRoom.GameRoomToHashMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (activity != null)
                            activity.handleAddGameRoom(task.isSuccessful());
                        else
                            activity.handleAddGameRoom(false);
                    }
                });
    }

    public void findGameRoomByNumber(int code) { //הפעולה בודקת אם חדר המשחק קיים לפי הקוד ומחזירה הודעה בהתאם
        db.collection("GameRooms")
                .document("" + code)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if (activity != null)
                                    activity.handleFindGameRoomByNumber("", new GameRoom((HashMap<String, Object>) document.getData()));
                            } else {
                                if (activity != null)
                                    activity.handleFindGameRoomByNumber("gameRoomNotExist", null);
                            }
                        } else {
                            if (activity != null)
                                activity.handleFindGameRoomByNumber("failed", null);
                        }
                    }
                });
    }

    public void roomExist(String roomCode) {//בודקת אם חדר המשחק קיים ומחזירה הודעה בהתאם
        DocumentReference docRef = db.collection("GameRooms").document(roomCode);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (activity != null)
                            activity.roomExistResult(true, roomCode);
                    } else if (activity != null)
                        activity.roomExistResult(false, roomCode);
                } else if (activity != null)
                    activity.roomExistResult(false, roomCode);
            }
        });
    }


    public void listenToDocumentChanges(Activity ac) {
        GameRoom gameRoom = AppUtilities.gameRoom;
        documentChanges = db.collection("GameRooms").document(gameRoom.getRoomCode()).addSnapshotListener(ac, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (value != null) {
                    if (waitingRoom != null) {
                        waitingRoom.updateDocumentChanges(new GameRoom((HashMap<String, Object>) value.getData()));
                    }
                }
            }
        });
    }


    public void stopListeningDocumentChanges() {
        if (documentChanges != null) {
            documentChanges.remove();
        }
    }


    public void uploadHum(GameStarted gameStarted, int n)//הפעולה מעלה את קטע הקול לfirebase Storage
    {
        String filePath = gameStarted.getRecordingFilePath();
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e("TAG", "File does not exist at " + filePath);
            return;
        }
        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageRef.child("Humming").child(AppUtilities.gameRoom.getRoomCode()).child("player" + n);
        Uri uri = Uri.fromFile(file);
        fileRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    gameStarted.moveIntent();
                } else {
                    Toast.makeText(gameStarted, "Can't upload humming. Hum again!", Toast.LENGTH_SHORT).show();
                    gameStarted.timer();
                }
            }
        });
    }

    public void getHumming(int n)//הפעולה בודקת אם יש קטע קול בfirebase storage וקוראת לפעולה playHumming
    {
        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageRef.child("Humming").child(AppUtilities.gameRoom.getRoomCode()).child("player " + n);
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUrl) {
                AppUtilities.gameRoom.playHumming(downloadUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("TAG", "Failed to retrieve download URL", exception);
            }
        });
    }


    public void listenToStorageChanges(Activity ac) {
        GameRoom gameRoom = AppUtilities.gameRoom;

        storageChanges = db.collection("GameRooms").document(gameRoom.getRoomCode())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (value != null && value.exists()) {
                            Map<String, Object> data = value.getData();
                            if (data != null) {
                                Boolean uploadFinished = (Boolean) data.get("uploadFinished");
                                if (uploadFinished != null && uploadFinished) {
                                    gameStarted.fragmentChoose();
                                }
                            }
                        }
                    }
                });

    }

    public void stopListeningStorageChanges() {
        if (storageChanges != null) {
            storageChanges.remove();
        }
    }


    public void listenToChoosingChanges(Activity ac) {
        GameRoom gameRoom = AppUtilities.gameRoom;
        chooseChanges = db.collection("GameRooms").document(gameRoom.getRoomCode())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (value != null && value.exists()) {
                            if (gameStarted != null) {
                                Map<String, Object> data = value.getData();
                                if (data != null) {
                                    gameStarted.updateDocumentChanges(new GameRoom((HashMap<String, Object>) value.getData()));
                                }
                            }
                        }
                    }
                });

    }

    public void stopListeningChooseChanges() {
        if (chooseChanges != null) {
            chooseChanges.remove();
        }
    }

    public void listenToEndChanges(Activity ac) {
        GameRoom gameRoom = AppUtilities.gameRoom;
        endChanges = db.collection("GameRooms").document(gameRoom.getRoomCode())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (value != null && value.exists()) {
                            if (afterHumming != null) {
                                Map<String, Object> data = value.getData();
                                if (data != null) {

                                    afterHumming.updateDocumentChanges(new GameRoom((HashMap<String, Object>) value.getData()));
                                }
                            }
                        }
                    }
                });

    }

    public void stopListeningEndChanges() {
        if (endChanges != null) {
            endChanges.remove();
        }
    }

    public void getUpdates() {
        db.collection("GameRooms").document(AppUtilities.gameRoom.getRoomCode())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (value != null && value.exists()) {
                            if (gameStarted != null) {
                                Map<String, Object> data = value.getData();
                                if (data != null) {
                                    gameStarted.updateDocumentChanges(new GameRoom((HashMap<String, Object>) value.getData()));
                                }
                            }
                        }
                    }
                });

    }

    public void updateeeThisGameRoom() {
        db.collection("GameRooms").document(AppUtilities.gameRoom.getRoomCode())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                            gameStarted.updateDocumentChanges(new GameRoom((HashMap<String, Object>) documentSnapshot.getData()));
                        }
                    }
                });
    }

    public void updateThisGameRoom() {
        db.collection("GameRooms").document(AppUtilities.gameRoom.getRoomCode())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            nextPlayer.updateGameRoom(new GameRoom((HashMap<String, Object>) document.getData()));
                        }
                    }
                });

    }


    public void listen()
    {
        GameRoom gameRoom = AppUtilities.gameRoom;
        db.collection("GameRooms").document(gameRoom.getRoomCode())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (value != null && value.exists()) {
                            if (end != null) {
                                Map<String, Object> data = value.getData();
                                if (data != null) {

                                    end.update(new GameRoom((HashMap<String, Object>) value.getData()));
                                }
                            }
                        }
                    }
                });

    }




    public void deleteGameRoom()
    {
        String roomCode = AppUtilities.gameRoom.getRoomCode();

        db.collection("GameRooms").document(roomCode)
                .delete();
    }

    public void deleteHum(int n)
    {
        String roomCode = AppUtilities.gameRoom.getRoomCode();
        storageRef.child("Humming/" + roomCode + "/player" + n)
                .delete();
    }

    public void deleteStorage()
    {
        String roomCode = AppUtilities.gameRoom.getRoomCode();
        storageRef.child("Humming/" + roomCode)
                .delete();
    }
}