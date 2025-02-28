package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatapp.adapters.UserAdapter;
import com.example.chatapp.databinding.ActivityUserBinding;
import com.example.chatapp.listeners.UserListener;
import com.example.chatapp.models.User;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserActivity extends BaseActivity implements UserListener {
ActivityUserBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
       preferenceManager=new PreferenceManager(getApplicationContext());
       setListener();
       getUser();
    }
    private  void setListener(){
        binding.imageback.setOnClickListener(v->{
            onBackPressed();
        });
    }

    private void getUser(){
        loading(true);
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USER)
                .get()
                .addOnCompleteListener(task->{
                    loading(false);
                    String currentUserId=preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful()&& task.getResult()!=null){
                        List<User> users=new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            if(currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user=new User();
                            user.name=queryDocumentSnapshot.getString(Constants.KEY_NAME).toUpperCase(Locale.ROOT);
                            user.email=queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image=queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token=queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id=queryDocumentSnapshot.getId();
                            users.add(user);

                        }
                        if(users.size()>0){
                            UserAdapter userAdapter=new UserAdapter(users, this);
                            binding.userRecycleView.setAdapter(userAdapter);
                            binding.userRecycleView.setVisibility(View.VISIBLE);
                        }
                        else {
                            showErrorMessage();
                        }
                    }else{
                        showErrorMessage();
                    }

                });
    }
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","no user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }
    private void loading(Boolean isLoading){
        if(isLoading){

            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.progressbar.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent =new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}