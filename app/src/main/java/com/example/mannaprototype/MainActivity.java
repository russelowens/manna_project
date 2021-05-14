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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button nextbutton;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextbutton = (Button) findViewById(R.id.btnlogin);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        firebaseFirestore = FirebaseFirestore.getInstance();

        nextbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                openActivity2();

            }
        });

    }

    public void openActivity2(){
        Intent intent = new Intent(this, Home.class);
                        intent.putExtra("firstName", "ZXCVBN");
                        startActivity(intent);

        startActivity(intent);
    }
}