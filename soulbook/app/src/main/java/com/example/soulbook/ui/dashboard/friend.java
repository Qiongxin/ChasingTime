package com.example.soulbook.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.soulbook.R;
import com.example.soulbook.datasave;
//import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
//import com.google.firebase.auth.UserRecord;

/**
* This is a class that shows the function of the frined section in dashboard. 
* This class used to search for friends and shows the list of all your friends.
* This part is not complete.
*/

public class friend extends AppCompatActivity implements searchFriend.OnFragmentInteractionListener{
    //class to handle all friend operations
    private ImageButton searchfriend, backbutton;
    private ListView friendlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        searchfriend = findViewById(R.id.friendpage_searchfriend);
        backbutton = findViewById(R.id.friendpage_backbutton);
        friendlist = findViewById(R.id.friendpage_friendlist);

        searchfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new searchFriend().show(getSupportFragmentManager(), "search friend");
            }
        });
    }

    @Override
    public void onOkPressed(String email) {
        //UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
        //String Uid = userRecord.getUid();
        //String Uid;
        //addFriendFragment in = new addFriendFragment();
        //Bundle input = new Bundle();
        //datasave a = new datasave();
        //Uid = a.getIdByEmail(email, friend.this);
        //input.putString("nickname", a.getNicknameById(Uid));
        //in.setArguments(input);
        //in.show(getSupportFragmentManager(),"friend");
    }
}
