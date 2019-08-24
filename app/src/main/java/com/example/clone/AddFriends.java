package com.example.clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

public class AddFriends extends AppCompatActivity {
    private EditText inputUserEmail;
    private Button searchBtn;
    private TextView findUser;
    private String email="sup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        final EditText inputUserEmail = findViewById(R.id.inputEmail);
        Button searchBtn = findViewById(R.id.searchBtn);
        final TextView findUser = findViewById(R.id.findUser);
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = inputUserEmail.getText().toString();
                firebaseAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        if(task.isSuccessful()){
                            findUser.setText(email);

                        }
                        else{
                            findUser.setText("Can't find user");
                        }
                    }
                });
            }
        });

    }
}
