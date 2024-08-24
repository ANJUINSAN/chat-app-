package com.example.chatapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.chatapp.databinding.ActivitySignUpBinding;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;


    private String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        setListener();

    }

    public void setListener() {
        binding.loginback.setOnClickListener(v -> onBackPressed());
        binding.btnSignUp.setOnClickListener(v->{
            if(isValidSighUpDetails()){
                signUp();

            }
        });
        binding.frameLayout.setOnClickListener(v->{
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
//        {
//            startActivity(new Intent(getApplicationContext(),SignInActivity.class));});
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUp() {
        loading(true);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        HashMap<String,Object> user=new HashMap<>();
        user.put(Constants.KEY_NAME,binding.nametv.getText().toString());
        user.put(Constants.KEY_PHONE,binding.phonetv.getText().toString());
        user.put(Constants.KEY_EMAIL,binding.emailtv.getText().toString());
        user.put(Constants.KEY_PASSWORD,binding.passwordtv.getText().toString());
        user.put(Constants.KEY_IMAGE,encodedImage);
       database.collection(Constants.KEY_COLLECTION_USER)
               .add(user)
               .addOnSuccessListener(documentReference -> {
                   loading(false);
                   preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                   preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                   preferenceManager.putString(Constants.KEY_NAME,binding.nametv.getText().toString());
                   preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                   Intent intent =new Intent(getApplicationContext(),MainActivity.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                   startActivity(intent);
               })
               .addOnFailureListener(exception->{
                   loading(false);
                   showToast(exception.getMessage());

               });

    }
private String getEncodedImage(Bitmap bitmap){
        int previewwidth=150;
        int previewHeight=bitmap.getHeight()*previewwidth/bitmap.getWidth();
        Bitmap previewBitmap=Bitmap.createScaledBitmap(bitmap,previewwidth,previewHeight,false);
    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
    previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
    byte[] bytes=byteArrayOutputStream.toByteArray();
    return Base64.encodeToString(bytes,Base64.DEFAULT);

}
private final ActivityResultLauncher<Intent> pickImage=registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),result-> {
            if (result.getResultCode() == RESULT_OK) if (result.getData() != null) {
                Uri uri = result.getData().getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    binding.roundImage.setImageBitmap(bitmap);
                    binding.addimgtext.setVisibility(View.GONE);
                    encodedImage = getEncodedImage(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    private Boolean isValidSighUpDetails(){
        if(encodedImage==null){
            showToast("select profile image");
            return false;
        }else if(binding.nametv.getText().toString().trim().isEmpty()){
            showToast("enter name");
            return false;
        }else if(binding.phonetv.getText().toString().trim().isEmpty()){
            showToast("enter Phone number");
            return false;
        }
        else if(binding.emailtv.getText().toString().trim().isEmpty()){
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailtv.getText().toString()).matches()) {
            showToast("enter valid email");
return false;
        }
        else if(binding.passwordtv.getText().toString().trim().isEmpty()){
            showToast("enter password");
            return false;
        }
        else if(binding.confirmpasswordtv.getText().toString().trim().isEmpty()){
            showToast("confirm your password");
            return false;
        }
        else if(!binding.passwordtv.getText().toString().equals(binding.confirmpasswordtv.getText().toString())){
            showToast("password and confirm password must be same");
            return false;
        }
        else{
            return  true;
        }


    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.btnSignUp.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.progressbar.setVisibility(View.INVISIBLE);
            binding.btnSignUp.setVisibility(View.VISIBLE);
        }
    }

}