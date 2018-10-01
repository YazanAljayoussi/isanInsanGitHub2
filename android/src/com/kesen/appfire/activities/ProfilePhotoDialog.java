package com.kesen.appfire.activities;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mygdx.game.R;
import com.kesen.appfire.model.realms.User;
import com.kesen.appfire.utils.BitmapUtils;
import com.kesen.appfire.utils.FileUtils;
import com.kesen.appfire.utils.FireManager;
import com.kesen.appfire.utils.IntentUtils;
import com.kesen.appfire.utils.RealmHelper;
import com.kesen.appfire.utils.Util;


public class ProfilePhotoDialog extends AppCompatActivity {


    private ImageView imageViewUserProfileDialog;
    private TextView tvUsernameDialog;

    private ImageButton buttonInfoDialog;
    private ImageButton buttonMessageDialog;
    private User user;


    FireManager.OnGetUserPhoto onGetUserPhoto;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if  (!Util.isOreoOrAbove()){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_photo_dialog);
        initViews();


        String uid = getIntent().getStringExtra(IntentUtils.UID);
        user = RealmHelper.getInstance().getUser(uid);
        tvUsernameDialog.setText(user.getUserName());

        loadUserImg();


        //show the image in ProfilePhotoActivity
        imageViewUserProfileDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePhotoDialog.this, ProfilePhotoActivity.class);
                intent.putExtra(IntentUtils.UID, user.getUid());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        //show the user info
        buttonInfoDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfilePhotoDialog.this, UserDetailsActivity.class);
                intent.putExtra(IntentUtils.UID, user.getUid());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        //start Chat with this user
        buttonMessageDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfilePhotoDialog.this, ChatActivity.class);
                intent.putExtra(IntentUtils.UID, user.getUid());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


    }

    private void loadUserImg() {
        //check if image is exists in database and in storage
        //if it's available show it
        if (user.getUserLocalPhoto() != null && FileUtils.isFileExists(user.getUserLocalPhoto())) {
            Glide.with(this)
                    .load(user.getUserLocalPhoto())
                    .into(imageViewUserProfileDialog);

            //otherwise show thumbImg if it's exists
        } else if (user.getThumbImg() != null) {
            byte[] bytes = BitmapUtils.encodeImageAsBytes(user.getThumbImg());
            Glide.with(this).load(bytes).asBitmap().into(imageViewUserProfileDialog);
        }
    }


    private void initViews() {
        imageViewUserProfileDialog = findViewById(R.id.image_view_user_profile_dialog);
        tvUsernameDialog = findViewById(R.id.tv_username_dialog);
        buttonInfoDialog = findViewById(R.id.button_info_dialog);
        buttonMessageDialog = findViewById(R.id.button_message_dialog);
    }

    @Override
    protected void onStop() {
        super.onStop();
        onGetUserPhoto = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //load user info once it's downloaded
        onGetUserPhoto = new FireManager.OnGetUserPhoto() {
            @Override
            //load thumb img while the full image is downloading
            public void onGetThumb(String thumbImg) {
                try {
                    Glide.with(ProfilePhotoDialog.this)
                            .load(BitmapUtils.encodeImageAsBytes(thumbImg))
                            .asBitmap()
                            .into(imageViewUserProfileDialog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onGetPhoto(String photoPath) {
                try {
                    Glide.with(ProfilePhotoDialog.this).load(photoPath).into(imageViewUserProfileDialog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        //check if there is a new image for this user
        //if yes ,download it and show it
        FireManager.checkAndDownloadUserPhoto(user, onGetUserPhoto);


    }

}
