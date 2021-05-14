package com.example.mannaprototype;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mannaprototype.models.ResidentModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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

//        Query query = firebaseFirestore.collection("residents").whereEqualTo("idNumber","npnmqr489399");
        Query query = firebaseFirestore.collection("residents");
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
                holder.blockLot.setText(model.getBlockAndLot());
                Log.e("Recycler View", String.valueOf(position));
            }

        };
        recyclerViewResident.setHasFixedSize(false);
        recyclerViewResident.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewResident.setAdapter(adapter);
        adapter.startListening();
        return view;


    }

    public class ResidentViewHolder extends RecyclerView.ViewHolder {
        private TextView fullName;
        private TextView blockLot;
        public ResidentViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullname);
            blockLot = itemView.findViewById(R.id.blocklot);
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