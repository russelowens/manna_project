package com.example.mannaprototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mannaprototype.models.ResidentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
            String txtUsername = username.getText().toString();
            String txtPass = password.getText().toString();

            ProgressDialog dialog = ProgressDialog.show(this, "",
                    "Loading. Please wait...", true);

            mAuth.signInWithEmailAndPassword(txtUsername, txtPass)
                    .addOnCompleteListener(task -> {
                        dialog.dismiss();
                       if (task.isComplete() && task.isSuccessful() && task.getResult() != null) {

                           SharedPreferences pref = getSharedPreferences("MyPref", MODE_PRIVATE);
                           SharedPreferences.Editor editor = pref.edit();
                           editor.putString("username", txtUsername);
                           editor.putString("password", txtPass);
                           editor.apply();

                           haveUser(task.getResult().getUser());
                       }else {
                           Toast.makeText(this, "Invalid username or password", Toast.LENGTH_LONG).show();
                       }
                    });
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
                       Toast.makeText(this, "No existing user found", Toast.LENGTH_LONG).show();
                   }
                });
    }

    public void openActivity2(ResidentModel resident){
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("resident", resident);
        startActivity(intent);
    }
}