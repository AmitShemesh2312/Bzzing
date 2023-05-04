package com.example.bzzing_last;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class FragmentSongPicker extends Fragment {

    public FragmentSongPicker() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song_picker, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        setChosenSong();
    }
    public void setChosenSong()//הפעולה מעדכנת את המסך לפי ארבעת השירים הראשונים במערך השירים
    {
        GameRoom gameRoom = AppUtilities.gameRoom;

        for (int i = 0; i < 4; i++) {

            Song song = gameRoom.getSongs().get(i);

            int imageId = getResources().getIdentifier("image" + i, "id", getContext().getPackageName());
            int id = getResources().getIdentifier(song.getImage(), "drawable", getContext().getPackageName());
            ImageView img = getView().findViewById(imageId);
            img.setImageResource(id);


            int nameId = getResources().getIdentifier("name" + i, "id", getContext().getPackageName());
            TextView name = getView().findViewById(nameId);
            name.setText(song.getName());

            int singerId = getResources().getIdentifier("singer" + i, "id", getContext().getPackageName());
            TextView singer = getView().findViewById(singerId);
            singer.setText(song.getSinger());
        }
    }
}