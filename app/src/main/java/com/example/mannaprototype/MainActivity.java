package com.example.mannaprototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.example.mannaprototype.models.ResidentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button nextbutton;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextbutton = findViewById(R.id.btnlogin);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        nextbutton.setOnClickListener(v -> {
//            String txtUsername = username.getText().toString();
//            String txtPass = password.getText().toString();
//
//            mAuth.signInWithEmailAndPassword(txtUsername, txtPass)
//                    .addOnCompleteListener(task -> {
//                       if (task.isComplete() && task.isSuccessful() && task.getResult() != null) {
//                           haveUser(task.getResult().getUser());
//                       }else {
//                           // TODO: Show the invalid username and password message
//                       }
//                    });
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null) {
            haveUser(mAuth.getCurrentUser());
        }
    }

    private void haveUser(FirebaseUser user) {
        firebaseFirestore.collection("residents").document(user.getUid()).get()
                .addOnCompleteListener(task -> {
                   if (task.isComplete() && task.isSuccessful() && task.getResult() != null) {
                       ResidentModel resident = task.getResult().toObject(ResidentModel.class);
                       openActivity2(resident);
                   }else {
                       // TODO: No existing user found
                   }
                });
    }

    public void openActivity2(ResidentModel resident){
        Intent intent = new Intent(this, Home.class);
                        intent.putExtra("resident", resident);
                        startActivity(intent);

        startActivity(intent);
    }
}