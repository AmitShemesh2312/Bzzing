package com.example.bzzing_last;

public interface MainActivityHandler {
    void handleAddGameRoom(boolean success);
    void roomExistResult(boolean success, String roomCode);
    void handleFindGameRoomByNumber(String respond, GameRoom gameRoom);
    void handleUpdateGameRoom(boolean b);
}
