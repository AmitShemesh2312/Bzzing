package com.example.bzzing_last;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentOthersChoose#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOthersChoose extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String name = "";

    public void setName(String name)
    {
        this.name = name;
    }


    public FragmentOthersChoose() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment othersChoose.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOthersChoose newInstance(String param1, String param2) {
        FragmentOthersChoose fragment = new FragmentOthersChoose();
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
        return inflater.inflate(R.layout.fragment_others_choose, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        writeNames();
    }
    public void writeNames()
    {
        GameRoom gameRoom = AppUtilities.gameRoom;

        ArrayList<Guesser> guessers = gameRoom.getGuessers();
        for (int i = 0; i < guessers.size(); i++)
        {
            int id = getResources().getIdentifier("player_name" + i, "id", getContext().getPackageName());
            TextView textView = getView().findViewById(id);
            textView.setText(guessers.get(i).getName());
            if(!guessers.get(i).getSongGuess().equals(""))
            {
                textView.setTextColor(Color.parseColor("#126C08"));
            }
            else {
                textView.setTextColor(Color.parseColor("#CF2500"));
            }
        }
    }
}