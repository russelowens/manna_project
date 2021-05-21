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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mannaprototype.models.Notification;
import com.example.mannaprototype.models.ResidentModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ResidentFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerViewResident;
    private FirestoreRecyclerAdapter adapter;

    public ResidentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resident_frame, container, false);
        // Inflate the layout for this fragment
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerViewResident = view.findViewById(R.id.recyclerViewResident);

        String[] COUNTRIES = new String[] {"All", "Resident", "Guard"};

        ArrayAdapter<String> adapter2 =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.dropdown_menu_popup_item,
                        COUNTRIES);

        AutoCompleteTextView editTextFilledExposedDropdown =
                view.findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter2);

        SwitchMaterial switchMaterial = view.findViewById(R.id.showActive);
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            que(switchMaterial, editTextFilledExposedDropdown.getText().toString());
        });

        editTextFilledExposedDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            que(switchMaterial, COUNTRIES[position]);
        });

        recyclerViewResident.setHasFixedSize(false);
        recyclerViewResident.setLayoutManager(new LinearLayoutManager(getActivity()));

        Query query = firebaseFirestore.collection("residents").orderBy("dateTime", Query.Direction.DESCENDING);
        initRecyclerview(query);


        return view;
    }

    private void que(SwitchMaterial switchMaterial, String type) {
        Query query = firebaseFirestore.collection("residents");
        if (!type.equalsIgnoreCase("all")) {
            if(type.equalsIgnoreCase("resident")) {
                switchMaterial.setVisibility(View.VISIBLE);
            }else {
                switchMaterial.setVisibility(View.GONE);
            }
            query = query.whereEqualTo("userType", type);
            if (switchMaterial.getVisibility() == View.VISIBLE && switchMaterial.isChecked()) {
                query = query.whereEqualTo("status", "OUT");
            }
        }
        query = query.orderBy("dateTime", Query.Direction.DESCENDING);
        initRecyclerview(query);
    }

    private void initRecyclerview(Query query) {
        FirestoreRecyclerOptions<ResidentModel> options = new FirestoreRecyclerOptions.Builder<ResidentModel>()
                .setQuery(query, ResidentModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ResidentModel, ResidentViewHolder>(options) {
            @NonNull
            @Override
            public ResidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_resident, parent,false);
                return new ResidentViewHolder(view2);
            }

            @Override
            protected void onBindViewHolder(@NonNull ResidentViewHolder holder, int position, @NonNull ResidentModel model) {
                holder.fullName.setText(model.getFullName()+" - "+model.getUserType());
                holder.blockLot.setText(model.getBlockAndLot() + (model.getStatus() != null ? (model.getStatus().equalsIgnoreCase("IN") ? " (ACTIVE)" : " (INACTIVE)") : ""));

                if (model.getUserType().equalsIgnoreCase("resident") && model.getStatus().equalsIgnoreCase("IN")) {
                    holder.card.setOnClickListener(v -> {
                        ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                                "Loading. Please wait...", true);

                        FirebaseFirestore.getInstance().collection("inout")
                                .whereEqualTo("idNumber", model.getIdNumber())
                                .whereEqualTo("inout", "IN")
                                .get()
                                .addOnCompleteListener(task -> {
                                    dialog.dismiss();
                                    int number_current_visitors = 0;
                                    if (task.isComplete() && task.isSuccessful() && task.getResult() != null) {
                                        number_current_visitors = task.getResult().getDocuments().size();
                                    }

                                    new AlertDialog.Builder(getActivity())
                                            .setTitle("Visitors")
                                            .setMessage("Current visitors of " + model.getFullName() + " - " + String.valueOf(number_current_visitors))
                                            .setNegativeButton("OKAY", null)
                                            .show();
                                });
                    });
                }
            }

        };

        recyclerViewResident.setAdapter(adapter);
        adapter.startListening();
    }

    public class ResidentViewHolder extends RecyclerView.ViewHolder {
        private TextView fullName;
        private TextView blockLot;
        private CardView card;
        public ResidentViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullname);
            blockLot = itemView.findViewById(R.id.blocklot);
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