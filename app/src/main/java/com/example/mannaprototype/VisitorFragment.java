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

import com.example.mannaprototype.models.InOutModel;
import com.example.mannaprototype.models.ResidentModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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


        firebaseFirestore.collection("inout")
                .orderBy("dateTime", Query.Direction.DESCENDING)
//                .whereEqualTo("age", "55")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.e("Results",document.getId() + " => " + document.getData());
                            testingOnly += document.getData().get("contact").toString();
                        }
                    } else {
                        Log.e("Error","Something went wrong");
                    }
                });







        Query query = firebaseFirestore.collection("inout");
        FirestoreRecyclerOptions<InOutModel> options = new FirestoreRecyclerOptions.Builder<InOutModel>()
                .setQuery(query, InOutModel.class)
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
                holder.datetime.setText(model.getDateTime().toString());
                holder.inout.setText(model.getInout());
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
        private TextView contact;
        private TextView datetime;
        private TextView inout;
        public ResidentViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullname);
            blockLot = itemView.findViewById(R.id.blocklot);
            contact = itemView.findViewById(R.id.contact);
            datetime = itemView.findViewById(R.id.datetime);
            inout = itemView.findViewById(R.id.inout);
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