package com.example.bzzing_last;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSongReveal#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSongReveal extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String name = "";
    private NumberPicker numberPicker;
    public void setName(String name)
    {
        this.name = name;
    }

    public FragmentSongReveal() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSongReveal.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSongReveal newInstance(String param1, String param2) {
        FragmentSongReveal fragment = new FragmentSongReveal();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song_reveal, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GameRoom gameRoom = AppUtilities.gameRoom;
        borders();

        int index = gameRoom.getCurrentSongIndex();

        ImageView imageView = getView().findViewById(R.id.image);
        int id = getResources().getIdentifier(gameRoom.getSongs().get(index).getImage(), "drawable", getContext().getPackageName());
        imageView.setImageResource(id);

        TextView songName = getView().findViewById(R.id.songName);
        songName.setText(gameRoom.getSongs().get(index).getName());

        TextView singer = getView().findViewById(R.id.singerName);
        singer.setText(gameRoom.getSongs().get(index).getSinger());



        TextView textView = getView().findViewById(R.id.who);
        textView.setText(gameRoom.getActivePlayer());

        picker();
    }

    public void borders()
    {
        GameRoom gameRoom = AppUtilities.gameRoom;
        View dividerUp = getView().findViewById(R.id.dividerUp);
        View dividerLeft = getView().findViewById(R.id.dividerLeft);
        View dividerRight = getView().findViewById(R.id.dividerRight);
        View dividerBottom = getView().findViewById(R.id.dividerBottom);
        if(gameRoom.getNotPlayers().get(gameRoom.getNotPlayerIndex(name)).getSongGuess().equals(gameRoom.getCurrentSong()))
        {
            dividerUp.setBackgroundColor(Color.parseColor("#3d8c40"));
            dividerLeft.setBackgroundColor(Color.parseColor("#3d8c40"));
            dividerRight.setBackgroundColor(Color.parseColor("#3d8c40"));
            dividerBottom.setBackgroundColor(Color.parseColor("#3d8c40"));
        }
        else
        {
            dividerUp.setBackgroundColor(Color.parseColor("#b30000"));
            dividerLeft.setBackgroundColor(Color.parseColor("#b30000"));
            dividerRight.setBackgroundColor(Color.parseColor("#b30000"));
            dividerBottom.setBackgroundColor(Color.parseColor("#b30000"));
        }
    }

    public void picker()
    {
        GameRoom gameRoom = AppUtilities.gameRoom;
        numberPicker = getView().findViewById(R.id.numberPicker);


        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        numberPicker.setValue(5);
        numberPicker.setWrapSelectorWheel(false);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                picker.setValue(newVal);
                gameRoom.getNotPlayers().get(gameRoom.getNotPlayerIndex(name)).setRate(newVal);
            }
        });
    }
}