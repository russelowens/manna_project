package com.example.mannaprototype;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.mannaprototype.models.InOutModel;
import com.example.mannaprototype.models.Notification;
import com.example.mannaprototype.models.ResidentModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.tapadoo.alerter.Alerter;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class Home extends AppCompatActivity {
    private DrawerLayout drawer;
    private Button btn;
    private NavigationView navigationView;
    private ResidentModel resident;
    private List<ResidentModel> residentModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout);

        drawer = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.myframelayout, new HomeFragment());
        transaction.commit();

        resident = (ResidentModel) getIntent().getSerializableExtra("resident");

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.btnhome){
                setFragment(new HomeFragment());
            }else if(menuItem.getItemId() == R.id.btnprofile){
                setFragment(new ProfileFragment());
            }else if(menuItem.getItemId() == R.id.btnregistration){
                setFragment(new RegistrationFragment());
            }else if(menuItem.getItemId() == R.id.btnscan){
                setFragment(new ScanFragment());
            }else if(menuItem.getItemId() == R.id.btnresident){
                setFragment(new ResidentFragment());
            }else if(menuItem.getItemId() == R.id.btnvisitor){
                setFragment(new VisitorFragment());
            }else if(menuItem.getItemId() == R.id.btnlogout){
                FirebaseAuth.getInstance().signOut();
                finish();
            }else{
                setFragment(new HomeFragment());
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        Menu menu = navigationView.getMenu();

        if (resident.getUserType().equalsIgnoreCase("resident")) {
            menu.findItem(R.id.btnregistration).setVisible(false);
            menu.findItem(R.id.btnscan).setVisible(false);
            menu.findItem(R.id.btnresident).setVisible(false);
            menu.findItem(R.id.btnvisitor).setVisible(false);
        }else if (resident.getUserType().equalsIgnoreCase("guard")) {
            menu.findItem(R.id.btnprofile).setVisible(false);
        }

        FirebaseFirestore.getInstance().collection("notifications")
                .whereEqualTo("resident_id", FirebaseAuth.getInstance().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener((value, error) -> {
                    if(value != null && !value.isEmpty()) {
                        if(value.getMetadata().isFromCache()) return;
                        value.getDocumentChanges().forEach(documentChange -> {
                            if(documentChange.getType() == DocumentChange.Type.ADDED) {
                                Notification notification = value.getDocumentChanges().get(0).getDocument().toObject(Notification.class);
                                Alerter.create(Home.this)
                                        .setTitle("Notification")
                                        .setText(notification.getVisitor_name() + " wants to visit you.")
                                        .enableSwipeToDismiss()
                                        .setDuration(4000)//4sec
                                        .enableVibration(true)
                                        .setBackgroundColorRes(R.color.purple_700)
                                        .setProgressColorRes(R.color.purple_700)
                                        .setOnClickListener(view -> actionVisitor(notification)).show();
                            }
                        });
                    }
                });

    }

    private void actionVisitor(Notification notification) {
        ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        FirebaseFirestore.getInstance().collection("inout").document(notification.getInout_id()).get()
                .addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isComplete() && task.isSuccessful() && task.getResult() != null){
                        InOutModel inOutModel = task.getResult().toObject(InOutModel.class);
                        new AlertDialog.Builder(this)
                                .setTitle("New Visitor")
                                .setMessage(inOutModel.getFullName() + "\n" + inOutModel.getBlockAndLot() + "\nDo you want to accept this visitor?")
                                .setNeutralButton("CANCEL", null)
                                .setNegativeButton("REJECT", (dialog1, which) -> {
                                    Map<String, Object> update = new HashMap<>();
                                    update.put("inout", "REJECTED");
                                    FirebaseFirestore.getInstance().collection("inout").document(notification.getInout_id()).set(update, SetOptions.merge());


                                    FirebaseFirestore.getInstance().collection("notifications")
                                            .whereEqualTo("inout_id", notification.getInout_id())
                                            .whereEqualTo("visitor_name", notification.getVisitor_name())
                                            .whereEqualTo("done", notification.isDone())
                                            .limit(1)
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isComplete() && task1.isSuccessful() && task1.getResult() != null) {
                                                    Map<String, Object> update1 = new HashMap<>();
                                                    update1.put("done", true);
                                                    FirebaseFirestore.getInstance().collection("notifications").document(task1.getResult().getDocuments().get(0).getId())
                                                            .set(update1, SetOptions.merge());
                                                }
                                            });

                                    Toast.makeText(this, "Visitor has been rejected", Toast.LENGTH_SHORT).show();
                                })
                                .setPositiveButton("ACCEPT", (dialog1, which) -> {
                                    Map<String, Object> update = new HashMap<>();
                                    update.put("inout", "IN");
                                    FirebaseFirestore.getInstance().collection("inout").document(notification.getInout_id()).set(update, SetOptions.merge());
                                    Toast.makeText(this, "Visitor has been accepted", Toast.LENGTH_SHORT).show();

                                    FirebaseFirestore.getInstance().collection("notifications")
                                            .whereEqualTo("inout_id", notification.getInout_id())
                                            .whereEqualTo("visitor_name", notification.getVisitor_name())
                                            .whereEqualTo("done", notification.isDone())
                                            .limit(1)
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isComplete() && task1.isSuccessful() && task1.getResult() != null) {
                                                    Map<String, Object> update1 = new HashMap<>();
                                                    update1.put("done", true);
                                                    FirebaseFirestore.getInstance().collection("notifications").document(task1.getResult().getDocuments().get(0).getId())
                                                            .set(update1, SetOptions.merge());
                                                }
                                            });
                                })
                                .show();
                    }
                });

    }

    public void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    protected void onStart() {
        FirebaseFirestore.getInstance().collection("residents")
                .whereEqualTo("userType", "Resident")
                .whereEqualTo("status", "IN")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isComplete() && task.isSuccessful() && task.getResult() != null) {
                        residentModels.clear();
                        task.getResult().getDocuments().forEach(documentSnapshot -> {
                            ResidentModel resident = documentSnapshot.toObject(ResidentModel.class);
                            if (resident != null) {
                                resident.setIdNumber(documentSnapshot.getId());
                            }
                            residentModels.add(resident);
                        });
                    }
                });

        super.onStart();
    }

    public void printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        Log.e("Home", String.valueOf(different));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.openDrawer(GravityCompat.START);
                }else{
                    drawer.closeDrawer(GravityCompat.START);
                }
                return true;
            case R.id.notification:
                startActivity(new Intent(Home.this, NotificationActivity.class));
                return true;
            case R.id.map:
                startActivity(new Intent(Home.this, MapsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    protected void setFragment(Fragment fragment) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.myframelayout, fragment);
        t.commit();
    }

    private boolean isValidatedForm(List<TextInputEditText> editTexts) {
        for (int i = 0; i < editTexts.size(); i++) {
            if (editTexts.get(i) != null && editTexts.get(i).getText() != null &&
                    editTexts.get(i).getText().toString().trim().length() == 0) {
                return false;
            }
        }
        return true;
    }

    public void fab(View view) {
        View v = LayoutInflater.from(this).inflate(R.layout.new_visitor_layout, null);
        TextInputEditText fullNameEdit = v.findViewById(R.id.fullname);
        TextInputEditText addressEdit = v.findViewById(R.id.address);
        TextInputEditText ageEdit = v.findViewById(R.id.age);
        TextInputEditText contactEdit = v.findViewById(R.id.contact);
        SearchableSpinner searchableSpinner = v.findViewById(R.id.spinner);

        List<String> names = new ArrayList<>();
        residentModels.forEach(residentModel -> {
            names.add(residentModel.getFullName());
        });

        InOutModel inOutModel = new InOutModel();

        searchableSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names));
        searchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > -1) {
                    inOutModel.setIdNumber(residentModels.get(position).getIdNumber());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<TextInputEditText> editTexts = new ArrayList<>();
        editTexts.add(fullNameEdit); editTexts.add(addressEdit); editTexts.add(ageEdit); editTexts.add(contactEdit);

        new AlertDialog.Builder(this)
                .setTitle("New Visitor")
                .setView(v)
                .setPositiveButton("SUBMIT", (dialog, which) -> {

                    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();


                    if (isValidatedForm(editTexts)) {

                        inOutModel.setFullName(fullNameEdit.getText().toString());
                        inOutModel.setBlockAndLot(addressEdit.getText().toString());
                        inOutModel.setAge(ageEdit.getText().toString());
                        inOutModel.setContact(contactEdit.getText().toString());
                        inOutModel.setUserType("Visitor");
                        inOutModel.setInout("Pending");
                        inOutModel.setDateTime(FieldValue.serverTimestamp());
                        CollectionReference collectionInOut = mFirestore.collection("inout");
                        CollectionReference notifications = mFirestore.collection("notifications");

                        collectionInOut.add(inOutModel)
                                .addOnCompleteListener(task -> {
                                   if (task.isComplete() && task.isSuccessful() && task.getResult() != null) {
                                       Notification notification = new Notification(FieldValue.serverTimestamp(), inOutModel.getIdNumber(),
                                               inOutModel.getFullName(), task.getResult().getId(), false);
                                       notifications.add(notification);
                                   }
                                });

                    }else {
                        Toast.makeText(this, "Please complete the form to proceed", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .setCancelable(false)
                .show();
    }
}