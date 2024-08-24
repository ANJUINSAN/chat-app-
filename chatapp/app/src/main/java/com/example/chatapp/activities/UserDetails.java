package com.example.chatapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.example.chatapp.adapters.UserAdapter;
import com.example.chatapp.databinding.ActivityMainBinding;
import com.example.chatapp.databinding.ActivityUserDetailsBinding;
import com.example.chatapp.models.User;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserDetails extends BaseActivity {
    ActivityUserDetailsBinding binding;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding.btnbackUp.setOnClickListener(v -> {
            onBackPressed();
        });
        loadUserDetails();

    }


    public void loadUserDetails() {
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.roundImage.setImageBitmap(bitmap);
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USER)
                .get()
                .addOnCompleteListener(task->{
                    String currentUserId=preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful()&& task.getResult()!=null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                binding.emailtv.setText(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                                binding.phonetv.setText(queryDocumentSnapshot.getString(Constants.KEY_PHONE));

                            }

                        }
                    }
                });

        binding.nametv.setText(preferenceManager.getString(Constants.KEY_NAME));
    }

}
