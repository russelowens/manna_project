package com.example.mannaprototype;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mannaprototype.models.ResidentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class RegistrationFragment extends Fragment {
    EditText registerfullname;
    EditText registerage;
    EditText registercontact;
    EditText registeraddress;
    EditText registerusername;
    EditText registerpassword;
    EditText editLatitude, editLongitude;
    RadioButton radiovisitor;
    RadioButton radioresident;
    RadioButton radioguard;
    RadioGroup radiogroup;
    Button btnsubmit;

    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    CollectionReference residents;

    private void forinit(View view){
        registerfullname = view.findViewById(R.id.registerfullname);
        registerage = view.findViewById(R.id.registerage);
        registercontact = view.findViewById(R.id.registercontact);
        registeraddress= view.findViewById(R.id.registeraddress);
        registerusername = view.findViewById(R.id.registerusername);
        registerpassword = view.findViewById(R.id.registerpassword);
//        radiovisitor = view.findViewById(R.id.radiovisitor);
        radioresident = view.findViewById(R.id.radioresident);
        radioguard = view.findViewById(R.id.radioguard);
        radiogroup = view.findViewById(R.id.radiogroup);
        btnsubmit = view.findViewById(R.id.btnsubmit);

        editLatitude = view.findViewById(R.id.latitude);
        editLongitude = view.findViewById(R.id.longitude);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        residents = mFirestore.collection("residents");
    }
    private void clearData(View view){
        registerfullname.setText(null);
        registerage.setText(null);
        registercontact.setText(null);
        registeraddress.setText(null);
        registerusername.setText(null);
        registerpassword.setText(null);
        editLongitude.setText(null);
        editLatitude.setText(null);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        residents = mFirestore.collection("residents");
    }

    public RegistrationFragment() {
        // Required empty public constructor
    }

    private void retrieveData() {
        residents.whereEqualTo("fullname", "fullname").get()
            .addOnSuccessListener(querySnapshot -> {
                querySnapshot.forEach(queryDocumentSnapshot -> {
                    String fullname = (String) queryDocumentSnapshot.getData().get("fullname");
                    Log.e("REGISTER", fullname);
                });
            });
    }

    private void signup(String username, String password, ResidentModel resident){
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnSuccessListener(v -> {
                    resident.setIdNumber(v.getUser().getUid());
                    residents.document(v.getUser().getUid()).set(resident)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()) {

                                    SharedPreferences pref = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
                                    mAuth.signOut();
                                    mAuth.signInWithEmailAndPassword(pref.getString("username", null), pref.getString("password", null))
                                            .addOnSuccessListener(task1 -> {
                                                Toast.makeText(getActivity(), "Users successfully registered", Toast.LENGTH_LONG).show();
                                            });
                                }else {
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .addOnFailureListener(v -> Log.e("LOGIN", v.getMessage()));
    }

    private void visitor_registration(String fullname, String address, String age, String contact, String username, String password){
        HashMap<String, Object> resident = new HashMap<>();
        resident.put("fullname", fullname);
        resident.put("address", address);
        resident.put("age", age);
        resident.put("contact", contact);
        resident.put("username", username);
        resident.put("password", password);

        residents.add(resident)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        // TODO: INSERT HERE THE P0AGE
                        Log.e("LOGIN", "SUCCESS");
                    }else {
                        Log.e("LOGIN", task.getException().getMessage());
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        forinit(view);

//        radiovisitor.setOnClickListener(v -> {
//            registerusername.setVisibility(View.GONE);
//            registerpassword.setVisibility(View.GONE);
//            registeraddress.setHint("Address");
//            clearData(view);
//        });
        radioresident.setOnClickListener(v -> {
            registerusername.setVisibility(View.VISIBLE);
            registerpassword.setVisibility(View.VISIBLE);
            registeraddress.setHint("Lot and Block");
            editLatitude.setVisibility(View.VISIBLE);
            editLongitude.setVisibility(View.VISIBLE);
            clearData(view);
        });
        radioguard.setOnClickListener(v -> {
            registerusername.setVisibility(View.VISIBLE);
            registerpassword.setVisibility(View.VISIBLE);
            registeraddress.setHint("Address");
            editLatitude.setVisibility(View.GONE);
            editLongitude.setVisibility(View.GONE);
            clearData(view);
        });
        btnsubmit.setOnClickListener(v -> {
            int selectedId = radiogroup.getCheckedRadioButtonId();
            if(selectedId == R.id.radioresident || selectedId == R.id.radioguard){
                ResidentModel resident = new ResidentModel();
                resident.setFullName(registerfullname.getText().toString());
                resident.setBlockAndLot(registeraddress.getText().toString());
                resident.setAge(registerage.getText().toString());
                resident.setContact(registercontact.getText().toString());
                resident.setUserName(registerusername.getText().toString());
                resident.setPassword(registerpassword.getText().toString());
                resident.setDateTime(FieldValue.serverTimestamp());
                if(selectedId == R.id.radioresident){
                    resident.setUserType("Resident");
                    resident.setLatitude(Double.parseDouble(editLatitude.getText().toString()));
                    resident.setLongitude(Double.parseDouble(editLongitude.getText().toString()));
                    resident.setStatus("IN");
                }else{
                    resident.setUserType("Guard");
                }
                signup(registerusername.getText().toString(), registerpassword.getText().toString(), resident);
                Log.e("Success","Successfully registered!");
            }
//            else if(selectedId == R.id.radiovisitor){
//                InOutModel inout = new InOutModel();
//                inout.setIdNumber("");
//                inout.setFullName(registerfullname.getText().toString());
//                inout.setBlockAndLot(registeraddress.getText().toString());
//                inout.setAge(registerage.getText().toString());
//                inout.setContact(registercontact.getText().toString());
//                inout.setUserType("Visitor");
//                inout.setInout("IN");
//                CollectionReference collectioninout = FirebaseFirestore.getInstance().collection("inout");
//                collectioninout.add(inout);
//                Log.e("Success","Successfully registered!");
//            }
//            else if(selectedId == R.id.radiovisitor){
//                InOutModel inout = new InOutModel();
//                inout.setIdNumber("");
//                inout.setFullName(registerfullname.getText().toString());
//                inout.setBlockAndLot(registeraddress.getText().toString());
//                inout.setAge(registerage.getText().toString());
//                inout.setContact(registercontact.getText().toString());
//                inout.setUserType("Visitor");
//                inout.setInout("IN");
//                inout.setDateTime(FieldValue.serverTimestamp());
//                CollectionReference collectioninout = FirebaseFirestore.getInstance().collection("inout");
//                collectioninout.add(inout);
//                Log.e("Success","Successfully registered!");
//            }
            clearData(view);
        });


        return view;
    }
}