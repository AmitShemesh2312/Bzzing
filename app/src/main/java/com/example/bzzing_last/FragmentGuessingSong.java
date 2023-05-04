package com.example.bzzing_last;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentGuessingSong#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentGuessingSong extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentGuessingSong() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentGuessingSong.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentGuessingSong newInstance(String param1, String param2) {
        FragmentGuessingSong fragment = new FragmentGuessingSong();
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
        return inflater.inflate(R.layout.fragment_guessing_song, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GameRoom gameRoom = AppUtilities.gameRoom;

        TextView textView = view.findViewById(R.id.player_turn_name);
        textView.setText(gameRoom.getPlayers().get(gameRoom.getRounds()).getName());


        for (int i = 0; i < 4; i++) {

            Song song = gameRoom.getSongs().get(i);

            int imageId = getResources().getIdentifier("image_guess" + i, "id", getContext().getPackageName());
            int id = getResources().getIdentifier(song.getImage(), "drawable", getContext().getPackageName());
            ImageView img = view.findViewById(imageId);
            img.setImageResource(id);

            int nameId = getResources().getIdentifier("song_name_guess" + i, "id", getContext().getPackageName());
            TextView name = view.findViewById(nameId);
            name.setText(song.getName());

            int singerId = getResources().getIdentifier("singer_name_guess" + i, "id", getContext().getPackageName());
            TextView singer = view.findViewById(singerId);
            singer.setText(song.getSinger());
        }
    }
}