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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentWaitingOthersScoring#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentWaitingOthersScoring extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentWaitingOthersScoring() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentWaitingOthersScoring.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentWaitingOthersScoring newInstance(String param1, String param2) {
        FragmentWaitingOthersScoring fragment = new FragmentWaitingOthersScoring();
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
        return inflater.inflate(R.layout.fragment_waiting_others_scoring, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        writeNames();
    }

    public void writeNames()
    {
        GameRoom gameRoom = AppUtilities.gameRoom;

        for (int i = 0; i < gameRoom.getMaxPlayers(); i++) {
            int id = getResources().getIdentifier( "waitingOthersScoring_Player" + i, "id", getContext().getPackageName());
            TextView textView = getView().findViewById(id);
            textView.setText("");
        }

        for (int i = 0; i < gameRoom.getPlayers().size(); i++) {
            Player p = gameRoom.getPlayers().get(i);
            int id = getResources().getIdentifier( "waitingOthersScoring_Player" + i, "id", getContext().getPackageName());
            TextView textView = getView().findViewById(id);
            textView.setText(p.getName());
            if(p.getDoneScoring())
            {
                textView.setTextColor(Color.parseColor("#126C08"));
            }
            else {
                textView.setTextColor(Color.parseColor("#CF2500"));
            }
        }
    }
}