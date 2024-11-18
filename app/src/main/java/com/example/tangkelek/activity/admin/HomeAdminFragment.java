package com.example.tangkelek.activity.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.R;
import com.example.tangkelek.databinding.FragmentHomeAdminBinding;
import com.example.tangkelek.util.ApiServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeAdminFragment extends Fragment {
    private FragmentHomeAdminBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AndroidNetworking.initialize(requireContext());

        binding.linearManajer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment userAdminFragment = new UserAdminFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, userAdminFragment)
                        .addToBackStack(null)  // Optional: Adds the transaction to the back stack
                        .commit();
            }
        });

        binding.linearTMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment userAdminFragment = new UserAdminFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, userAdminFragment)
                        .addToBackStack(null)  // Optional: Adds the transaction to the back stack
                        .commit();
            }
        });

        binding.btnMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment monitoringAdminFragment = new MonitoringAdminFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, monitoringAdminFragment)
                        .addToBackStack(null)  // Optional: Adds the transaction to the back stack
                        .commit();
            }
        });

        AndroidNetworking.get(ApiServer.site_url_admin+"checkMonitoring.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("code").equals("1")){
                                JSONArray array = response.getJSONArray("data");
                                JSONObject data = array.getJSONObject(0);
                                binding.txtInfo.setText(data.getString("jenis"));
                            }else {
                                binding.txtInfo.setText("0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.JMLManajer.setText("0");
                    }
                });

        AndroidNetworking.get(ApiServer.site_url_admin+"checkJumlahUser.php?role=manajer")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("code").equals("1")){
                                JSONArray array = response.getJSONArray("data");
                                JSONObject data = array.getJSONObject(0);
                                binding.JMLManajer.setText(data.getString("JML"));
                            }else {
                                binding.JMLManajer.setText("0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.JMLManajer.setText("0");
                    }
                });

        AndroidNetworking.get(ApiServer.site_url_admin+"checkJumlahUser.php?role=pengawas")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("code").equals("1")){
                                JSONArray array = response.getJSONArray("data");
                                JSONObject data = array.getJSONObject(0);
                                binding.JMLTMonitoring.setText(data.getString("JML"));
                            }else {
                                binding.JMLTMonitoring.setText("0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.JMLTMonitoring.setText("0");
                    }
                });

        return binding.getRoot();
    }
}