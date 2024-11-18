package com.example.tangkelek.activity.manajer;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.R;
import com.example.tangkelek.adapter.AdminMonitoringAdapter;
import com.example.tangkelek.adapter.PengawasMonitoringAdapter;
import com.example.tangkelek.databinding.FragmentMonitoringManajerBinding;
import com.example.tangkelek.model.KonveksiModel;
import com.example.tangkelek.model.KonveksiModel1;
import com.example.tangkelek.model.MonitoringModel;
import com.example.tangkelek.model.TahapanModel;
import com.example.tangkelek.util.ApiServer;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

public class MonitoringManajerFragment extends Fragment {
    private FragmentMonitoringManajerBinding binding;
    private List<KonveksiModel> konveksiModelList;
    private List<KonveksiModel1> konveksiModel1List;
    private List<MonitoringModel> monitoringModelList;
    private List<TahapanModel> tahapanModelList;
    private List<String> spinnerItems;
    private AlertDialog dialog;
    private Uri selectedImageUri;
    private static final int REQUEST_IMAGE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMonitoringManajerBinding.inflate(inflater, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AndroidNetworking.initialize(requireContext());
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();

        konveksiModelList = new ArrayList<>();
        konveksiModel1List = new ArrayList<>();
        monitoringModelList = new ArrayList<>();
        tahapanModelList = new ArrayList<>();
        spinnerItems = new ArrayList<>();

        dataKonveksi();

        return binding.getRoot();
    }

    private void dataKonveksi() {
        dialog.show();
        AndroidNetworking.get(ApiServer.site_url_manajer + "getKonveksi.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();
                            if (response.getString("code").equalsIgnoreCase("1")) {
                                JSONArray array = response.getJSONArray("data");
                                konveksiModelList.clear();
                                Gson gson = new Gson();

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject konveksiObject = array.getJSONObject(i);
                                    KonveksiModel isi = gson.fromJson(konveksiObject + "", KonveksiModel.class);
                                    konveksiModelList.add(isi);
                                    spinnerItems.add(isi.getNama_produk() + "("+isi.getStatus()+")");
                                }

                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                                        android.R.layout.simple_spinner_item, spinnerItems);
                                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                binding.spinner.setAdapter(spinnerAdapter);

                                binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                        String selectedIdKonveksi = konveksiModelList.get(position).getId_konveksi();
                                        String selectedJenis = konveksiModelList.get(position).getJenis();
                                        String selectedJumlah = konveksiModelList.get(position).getJumlah();
                                        String selectedTanggal = konveksiModelList.get(position).getTanggal();
                                        String selectedTanggalPengambilan = konveksiModelList.get(position).getTanggal_pengambilan();
                                        String selectedTglSelesai = konveksiModelList.get(position).getTgl_selesai();
                                        dataMonitoring(selectedIdKonveksi, selectedJenis, selectedJumlah, selectedTanggal, selectedTanggalPengambilan, selectedTglSelesai);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                        binding.card2.setVisibility(View.GONE);
                                        binding.textGone.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("catch", "konveksimodel::" + e.toString());
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Log.e("error", "onError: " + anError.getErrorBody());
                    }
                });
    }

    private void dataMonitoring(String id_konveksi, String jenis, String jumlah, String tanggal, String tanggal_pengambilan, String tgl_selesai) {
        binding.txtMonitoring.setText(jenis + "("+jumlah+")");
        binding.txtTanggalBefore.setText("Pengerjaan: "+tanggal);
        binding.txtDateLine.setText("Pengambilan: "+tanggal_pengambilan);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateAwal = null;
        if (tanggal != null) {
            try {
                dateAwal = dateFormat.parse(tanggal);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        Date dateAkhir = null;
        if (tgl_selesai != null) {
            try {
                dateAkhir = dateFormat.parse(tgl_selesai);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        if (tgl_selesai != null) {
            long selisihMillis = dateAkhir.getTime() - dateAwal.getTime();
            long selisihJam = TimeUnit.MILLISECONDS.toHours(selisihMillis) % 24;
            long selisihHari = TimeUnit.MILLISECONDS.toDays(selisihMillis);
            binding.txtDurasi.setText(selisihHari + " Hari " + selisihJam + " Jam");
        } else {
            binding.txtDurasi.setText("On Progress");
        }

        AndroidNetworking.get(ApiServer.site_url_admin + "getMonitoring.php?id_konveksi=" + id_konveksi)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                JSONArray array = response.getJSONArray("data");
                                tahapanModelList.clear();
                                konveksiModel1List.clear();
                                monitoringModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject beritaObject = array.getJSONObject(i);
                                    TahapanModel tahapan = gson.fromJson(beritaObject + "", TahapanModel.class);
                                    tahapanModelList.add(tahapan);
                                    KonveksiModel1 konveksi1 = gson.fromJson(beritaObject + "", KonveksiModel1.class);
                                    konveksiModel1List.add(konveksi1);
                                    KonveksiModel konveksi = gson.fromJson(beritaObject + "", KonveksiModel.class);
                                    konveksiModelList.add(konveksi);
                                    MonitoringModel monitoring = gson.fromJson(beritaObject + "", MonitoringModel.class);
                                    monitoringModelList.add(monitoring);
                                }
                                PengawasMonitoringAdapter adapter = new PengawasMonitoringAdapter(requireContext(), tahapanModelList, konveksiModel1List, monitoringModelList);
                                binding.recyclerView.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
}