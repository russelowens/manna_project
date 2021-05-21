package com.example.mannaprototype;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.Calendar;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.mannaprototype.models.InOutModel;
import com.example.mannaprototype.models.ResidentModel;
import com.example.mannaprototype.models.Scan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.Result;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFragment extends Fragment {

    private CodeScanner mCodeScanner;
    private FirebaseFirestore firebaseFirestore;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CAMERA = 1000;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanFragment newInstance(String param1, String param2) {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_scan, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        firebaseFirestore = FirebaseFirestore.getInstance();

        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(result -> {
            activity.runOnUiThread(() -> Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show());
            Log.e("QR RESULT", result.getText());

            firebaseFirestore.collection("residents")
                    .whereEqualTo("idNumber", result.getText())
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0) {

                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            Log.e("Results",document.getId() + " => " + document.getData());

                            ResidentModel residentModel = document.toObject(ResidentModel.class);
                            if (residentModel == null) {
                                Toast.makeText(getActivity(), "Resident did not exist", Toast.LENGTH_LONG).show();
                                return;
                            }

                            String status = residentModel.getStatus() == null ? "IN" : (residentModel.getStatus().equals("IN") ? "OUT" : "IN");

                            InOutModel inout = new InOutModel();
                            inout.setIdNumber(result.getText());
                            inout.setFullName(residentModel.getFullName());
                            inout.setBlockAndLot(residentModel.getBlockAndLot());
                            inout.setContact(residentModel.getContact());
                            inout.setAge(residentModel.getContact());
                            inout.setInout(status);
                            inout.setUserType("Resident");
                            inout.setDateTime(FieldValue.serverTimestamp());

                            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

                            mFirestore.collection("inout").add(inout);

                            Map<String, Object> update = new HashMap<>();
                            update.put("status", status);
                            mFirestore.collection("residents").document(document.getId()).set(update, SetOptions.merge());

                        } else {
                            Log.e("Error","Something went wrong");
                        }
                    });

        });
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            Log.e("DB", "PERMISSION GRANTED");
            mCodeScanner.startPreview();
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    public void onPause() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mCodeScanner.releaseResources();
        }
        super.onPause();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (permissions[0].equals(Manifest.permission.CAMERA)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCodeScanner.startPreview();
            }
        }
    }
}