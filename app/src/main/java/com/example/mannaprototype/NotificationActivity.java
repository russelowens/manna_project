package com.example.mannaprototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mannaprototype.models.InOutModel;
import com.example.mannaprototype.models.Notification;
import com.example.mannaprototype.models.ResidentModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerViewResident;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //

        recyclerViewResident = findViewById(R.id.recyclerView);

        recyclerViewResident.setHasFixedSize(false);
        recyclerViewResident.setLayoutManager(new LinearLayoutManager(this));

        Query query = FirebaseFirestore.getInstance().collection("notifications")
                .whereEqualTo("done", false)
                .whereEqualTo("resident_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        initRecycler(query);
    }

    private void initRecycler(Query query) {
        Query finalQuery = query.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(finalQuery, Notification.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Notification, VisitorFragment.ResidentViewHolder>(options) {
            @NonNull
            @Override
            public VisitorFragment.ResidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_in_out, parent,false);
                return new VisitorFragment.ResidentViewHolder(view2);
            }

            @Override
            protected void onBindViewHolder(@NonNull VisitorFragment.ResidentViewHolder holder, int position, @NonNull Notification model) {
                holder.fullName.setText(model.getVisitor_name());
                holder.blockLot.setText(model.getVisitor_name() + " wants to visit you.");
                holder.contact.setVisibility(View.GONE);
                holder.datetime.setVisibility(View.GONE);
                holder.inout.setVisibility(View.GONE);

                holder.itemView.setOnClickListener(v -> actionVisitor(model));
            }

        };

        recyclerViewResident.setAdapter(adapter);
        adapter.startListening();
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}