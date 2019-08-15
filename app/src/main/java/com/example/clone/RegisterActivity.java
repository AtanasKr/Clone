package com.example.clone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private ImageView avatarPic;
    private EditText regName;
    private EditText regEmail;
    private EditText regPass;
    private Button btnConfirm;
    private FirebaseAuth firebaseAuth;
    private  String displayUserName;
    private static final int CHOOSE_IMAGE=101;
    Uri uriProfileImage;
    ProgressDialog progressDialog;
    String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        idAssign();
        firebaseAuth =FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validate()){
                    //Upload Email and Password to firebase;
                    String reg_Email = regEmail.getText().toString().trim();
                    String reg_Password = regPass.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(reg_Email,reg_Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this,"Registration successful",Toast.LENGTH_SHORT);
                                saveUserInformation();
                                Intent intent = new Intent(RegisterActivity.this,UserActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(RegisterActivity.this,"Registration failed",Toast.LENGTH_SHORT);
                            }

                        }
                    });
                }

            }
        });
        avatarPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

    }

    private void saveUserInformation() {
        displayUserName = regName.getText().toString();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(displayUserName).setPhotoUri(Uri.parse(profileImageUrl)).build();

        user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(RegisterActivity.this,"User Information saved",Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==CHOOSE_IMAGE&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            data.getData();
            uriProfileImage=data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                avatarPic.setImageBitmap(bitmap);
                //Upload Image to firebase
                uploadImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadImageToFirebase() {
        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/"+displayUserName+".jpg");

        if(uriProfileImage!=null){
            progressDialog.show();
            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this,"Error uploading the picture",Toast.LENGTH_SHORT);
                }
            });
        }
    }

    public void idAssign(){
        avatarPic = findViewById(R.id.imageButton);
        regName = findViewById(R.id.regName);
        regEmail = findViewById(R.id.regEmail);
        regPass = findViewById(R.id.regPassword);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private boolean validate() {
        Boolean result = false;

        String name = regName.getText().toString();
        String password = regPass.getText().toString();
        String email = regEmail.getText().toString();
        if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT);
        }
        else{
            result = true;
        }
        return result;
    }
    private void imageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent,"Select Image"),CHOOSE_IMAGE);

    }
}
