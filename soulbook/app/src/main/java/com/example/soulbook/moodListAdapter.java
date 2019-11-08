package com.example.soulbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.soulbook.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * this is an adapter for moodlist, where
 */
public class moodListAdapter extends BaseAdapter {
    private Context context;
    ArrayList<mood> moods;
    ArrayList<String> nicknames, moodId;
    private LayoutInflater mLayoutInflater;
    private ImageView listViewAvatar, photos[] = new ImageView[9];
    private TextView listViewNickName, listViewMoodText, listViewTime, listViewLocation, listViewLikeList, listViewEmoji;
    private ImageButton deleteButton, likeButton;
    private String nickname;
    private HomeFragment m;
    private boolean ifshow;
    ArrayList<File> photoFile;

    public moodListAdapter(Context context, ArrayList<mood> moods, ArrayList<String> nicknames, ArrayList<String> moodId, HomeFragment m, boolean show){
        this.m = m;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.moods = moods;
        this.nicknames = nicknames;
        this.moodId = moodId;
        ifshow = show;
    }

    @Override
    public int getCount() {
        return moods.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(R.layout.mood_view_content, null);
        listViewEmoji = convertView.findViewById(R.id.listview_emoji);
        listViewAvatar = convertView.findViewById(R.id.listview_avatar);
        listViewNickName = convertView.findViewById(R.id.listview_nickname);
        listViewMoodText = convertView.findViewById(R.id.listview_mood_text);
        listViewTime = convertView.findViewById(R.id.listview_mood_time);
        listViewLocation = convertView.findViewById(R.id.listview_location);
        listViewLikeList = convertView.findViewById(R.id.listview_like_list);
        deleteButton = convertView.findViewById(R.id.listview_delete_mood);
        likeButton = convertView.findViewById(R.id.listview_like_button);
        photos[0] = convertView.findViewById(R.id.listview_photo1);
        photos[1] = convertView.findViewById(R.id.listview_photo2);
        photos[2] = convertView.findViewById(R.id.listview_photo3);
        photos[3] = convertView.findViewById(R.id.listview_photo4);
        photos[4] = convertView.findViewById(R.id.listview_photo5);
        photos[5] = convertView.findViewById(R.id.listview_photo6);
        photos[6] = convertView.findViewById(R.id.listview_photo7);
        photos[7] = convertView.findViewById(R.id.listview_photo8);
        photos[8] = convertView.findViewById(R.id.listview_photo9);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(0,0);
        LinearLayout.LayoutParams mParams2 = new LinearLayout.LayoutParams(150,150);
        for (int i = 0 ;i < 9; i++){
            photos[i].setLayoutParams(mParams);
        }
        final mood thismood = moods.get(moods.size() - 1 - position);
        final String Id = moodId.get(moods.size() - 1 - position);
        try {
            photoFile = getMoodphotos(Id, thismood);
        } catch (IOException e) {
            Toast.makeText(context,"get photo fail", Toast.LENGTH_LONG).show();
        }
        //Toast.makeText(context,String.valueOf(thismood.getPhotonumber() + ":" + photoFile.size()), Toast.LENGTH_LONG).show();
        likeButton.setVisibility(View.INVISIBLE);
        listViewLikeList.setHeight(0);

        listViewEmoji.setText(new String(Character.toChars(emotionToEmojiUnicode(thismood.getEmotion()))));
        if (thismood.getPoster().equals(datasave.UserId)){
            deleteButton.setVisibility(View.VISIBLE);
        }
        else{
            deleteButton.setVisibility(View.INVISIBLE);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datasave a = new datasave();
                m.removemood(moods.size() - 1 -position);
            }
        });
        if (ifshow){
            listViewMoodText.setText(thismood.getContent());
            for (int i = 0; i <thismood.getPhotonumber(); i++){
                photos[i].setLayoutParams(mParams2);
                photos[i].setImageURI(Uri.parse(photoFile.get(i).toString()));
            }
        }
        else{
            listViewMoodText.setHeight(0);
        }
        listViewNickName.setText(nicknames.get(moods.size() - 1 -position));
        listViewTime.setText(thismood.getTime().printTime());
        return convertView;
    }

    public int emotionToEmojiUnicode(String emotion){
        switch (emotion){
            case "Happiness":
                return 0x1F603;
            case "Fear":
                return 0x1F628;
            case "Sadness":
                return 0x1F62D;
            case "Anger":
                return 0x1F620;
            case "Surprise":
                return 0x1F603;
            case "Disgust":
                return 0x1F635;
            case "Excitement":
                return 0x1F606;
        }
        return 0x1F251;
    }

    /**
     * a method to get photos of a mood
     * @param moodId
     *   Id of the mood
     * @param m
     *   mood m
     * @return
     * @throws IOException
     */
    private ArrayList<File> getMoodphotos(String moodId, mood m) throws IOException {
        StorageReference a = FirebaseStorage.getInstance().getReference().child("moodphoto").child(moodId);
        final ArrayList<File> result = new ArrayList<>();
        final File localFile = File.createTempFile("image", ".jpg");
        int number = m.getPhotonumber();
        for (int i = 0 ; i < number; i++){
            a.child(i+".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                }
            });
            result.add(localFile);
        }
        return result;
    }

}
