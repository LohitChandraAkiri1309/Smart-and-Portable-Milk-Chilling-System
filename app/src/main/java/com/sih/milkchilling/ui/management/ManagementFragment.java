package com.sih.milkchilling.ui.management;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sih.milkchilling.MilkChillingApp;
import com.sih.milkchilling.R;
import com.sih.milkchilling.data.local.FarmerEntity;
import com.sih.milkchilling.data.repository.MilkChillingRepository;
import com.sih.milkchilling.ui.adapters.FarmerAdapter;

import java.util.Random;

public class ManagementFragment extends Fragment {

    private RecyclerView rvFarmers;
    private FarmerAdapter adapter;
    private FloatingActionButton fabAdd;
    private MilkChillingRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_management, container, false);

        rvFarmers = view.findViewById(R.id.rv_farmers);
        rvFarmers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FarmerAdapter();
        rvFarmers.setAdapter(adapter);

        fabAdd = view.findViewById(R.id.fab_add_farmer);
        fabAdd.setOnClickListener(v -> showAddFarmerDialog());

        repository = MilkChillingApp.getInstance().getRepository();
        repository.getAllFarmersLiveData().observe(getViewLifecycleOwner(), farmers -> {
            if (farmers != null) adapter.setFarmers(farmers);
        });

        return view;
    }

    private void showAddFarmerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Register New Farmer");

        View dialogView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
        // Build simple input fields dynamically
        EditText inputName = new EditText(getContext());
        inputName.setHint("Farmer Name");
        EditText inputPhone = new EditText(getContext());
        inputPhone.setHint("Phone Number");
        EditText inputVillage = new EditText(getContext());
        inputVillage.setHint("Village / Route");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        layout.addView(inputName);
        layout.addView(inputPhone);
        layout.addView(inputVillage);

        builder.setView(layout);

        builder.setPositiveButton("Register", (dialog, which) -> {
            String name = inputName.getText().toString();
            String phone = inputPhone.getText().toString();
            String village = inputVillage.getText().toString();

            if (!TextUtils.isEmpty(name)) {
                String id = "F-" + (100 + new Random().nextInt(900));
                repository.addFarmer(new FarmerEntity(id, name, phone, village, "CC-01", null));
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
