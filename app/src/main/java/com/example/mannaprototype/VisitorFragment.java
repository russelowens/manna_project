package com.example.mannaprototype;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class VisitorFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerViewResident;
    private FirestoreRecyclerAdapter adapter;
    private String testingOnly;

    public VisitorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_visitor, container, false);
        // Inflate the layout for this fragment
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerViewResident = view.findViewById(R.id.recyclerViewVisitor);

        Query query = firebaseFirestore.collection("inout");

        SwitchMaterial switchMaterial = view.findViewById(R.id.showActive);
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Query finalQuery = query.whereEqualTo("userType", "Visitor").whereEqualTo("inout", "IN");
                initRecycler(finalQuery);
            }else {
                initRecycler(query);
            }
        });

        recyclerViewResident.setHasFixedSize(false);
        recyclerViewResident.setLayoutManager(new LinearLayoutManager(getActivity()));
        initRecycler(query);

        return view;
    }

    private void initRecycler(Query query) {
        Query finalQuery = query.orderBy("dateTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<InOutModel> options = new FirestoreRecyclerOptions.Builder<InOutModel>()
                .setQuery(finalQuery, InOutModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<InOutModel, VisitorFragment.ResidentViewHolder>(options) {
            @NonNull
            @Override
            public VisitorFragment.ResidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_in_out, parent,false);
                return new VisitorFragment.ResidentViewHolder(view2);
            }

            @Override
            protected void onBindViewHolder(@NonNull VisitorFragment.ResidentViewHolder holder, int position, @NonNull InOutModel model) {
                holder.fullName.setText(model.getFullName()+" - "+model.getUserType());
                holder.blockLot.setText(model.getBlockAndLot());
                holder.contact.setText(model.getContact());
                holder.datetime.setText(model.getDateTime() != null ? model.getDateTime().toString() : "");
                holder.inout.setText(model.getInout());

                if (model.getUserType().equalsIgnoreCase("visitor") && model.getInout().equalsIgnoreCase("IN")) {
                    holder.card.setOnClickListener(v -> {

                        new AlertDialog.Builder(getActivity())
                                .setTitle(model.getFullName())
                                .setMessage("Are you sure to Out this visitor?")
                                .setPositiveButton("YES", (dialog, which) -> {
                                    FirebaseFirestore.getInstance().collection("inout")
                                            .whereEqualTo("fullName", model.getFullName())
                                            .whereEqualTo("idNumber", model.getIdNumber())
                                            .whereEqualTo("inout", model.getInout())
                                            .whereEqualTo("blockAndLot", model.getBlockAndLot())
                                            .get()
                                            .addOnCompleteListener(task -> {
                                                if (task.isComplete() && task.isSuccessful() && task.getResult() != null) {
                                                    Map<String, Object> update = new HashMap<>();
                                                    update.put("inout", "OUT");
                                                    FirebaseFirestore.getInstance().collection("inout").document(task.getResult().getDocuments().get(0).getId())
                                                            .set(update, SetOptions.merge());
                                                }
                                            });
                                })
                                .setNegativeButton("CANCEL", null)
                                .setCancelable(false)
                                .show();


                    });
                }
            }

        };

        recyclerViewResident.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ResidentViewHolder extends RecyclerView.ViewHolder {
        public TextView fullName;
        public TextView blockLot;
        public TextView contact;
        public TextView datetime;
        public TextView inout;
        public CardView card;
        public ResidentViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullname);
            blockLot = itemView.findViewById(R.id.blocklot);
            contact = itemView.findViewById(R.id.contact);
            datetime = itemView.findViewById(R.id.datetime);
            inout = itemView.findViewById(R.id.inout);
            card = itemView.findViewById(R.id.card);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}