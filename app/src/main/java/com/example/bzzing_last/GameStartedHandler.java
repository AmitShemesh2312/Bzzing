package com.example.bzzing_last;

import android.net.Uri;

public interface GameStartedHandler {
     void moveIntent();
     void playHumming(Uri downloadUrl);
     void fragmentChoose();
     void updateDocumentChanges(GameRoom g);

}
