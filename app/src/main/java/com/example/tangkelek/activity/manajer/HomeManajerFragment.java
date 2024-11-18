package com.example.tangkelek.activity.manajer;

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
import com.example.tangkelek.activity.admin.MonitoringAdminFragment;
import com.example.tangkelek.activity.admin.UserAdminFragment;
import com.example.tangkelek.databinding.FragmentHomeAdminBinding;
import com.example.tangkelek.databinding.FragmentHomeManajerBinding;
import com.example.tangkelek.util.ApiServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeManajerFragment extends Fragment {
    private FragmentHomeManajerBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeManajerBinding.inflate(inflater, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AndroidNetworking.initialize(requireContext());

        binding.linearPengajuanProduksi.setOnClickListener(new View.OnClickListener() {
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

        binding.linearProduksiSelesai.setOnClickListener(new View.OnClickListener() {
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
                Fragment monitoringManajerFragment = new MonitoringManajerFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, monitoringManajerFragment)
                        .addToBackStack(null)  // Optional: Adds the transaction to the back stack
                        .commit();
            }
        });

        AndroidNetworking.get(ApiServer.site_url_manajer+"checkMonitoring.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("code").equals("1")){
                                JSONArray array = response.getJSONArray("data");
                                JSONObject data = array.getJSONObject(0);
                                binding.txtInfo.setText(data.getString("jenis")+" ("+data.getString("jumlah")+" pcs)");
                            }else {
                                binding.txtInfo.setText("-");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.txtInfo.setText("-");
                    }
                });

        AndroidNetworking.get(ApiServer.site_url_manajer+"checkJumlahProduksi.php?status=Diajukan")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("code").equals("1")){
                                JSONArray array = response.getJSONArray("data");
                                JSONObject data = array.getJSONObject(0);
                                binding.JMLPengajuanProduksi.setText(data.getString("JML"));
                            }else {
                                binding.JMLPengajuanProduksi.setText("0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.JMLPengajuanProduksi.setText("0");
                    }
                });

        AndroidNetworking.get(ApiServer.site_url_manajer+"checkJumlahProduksi.php?status=Selesai")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("code").equals("1")){
                                JSONArray array = response.getJSONArray("data");
                                JSONObject data = array.getJSONObject(0);
                                binding.JMLProduksiSelesai.setText(data.getString("JML"));
                            }else {
                                binding.JMLProduksiSelesai.setText("0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.JMLProduksiSelesai.setText("0");
                    }
                });

        return binding.getRoot();
    }
}