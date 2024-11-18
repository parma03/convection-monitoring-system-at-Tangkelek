package com.example.tangkelek.activity.admin;

import static android.content.Context.PRINT_SERVICE;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.example.tangkelek.adapter.AdminMonitoringAdapter;
import com.example.tangkelek.databinding.FragmentMonitoringAdminBinding;
import com.example.tangkelek.model.KonveksiModel;
import com.example.tangkelek.model.KonveksiModel1;
import com.example.tangkelek.model.MonitoringModel;
import com.example.tangkelek.model.TahapanModel;
import com.example.tangkelek.util.ApiServer;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

public class MonitoringAdminFragment extends Fragment {
    private FragmentMonitoringAdminBinding binding;
    private List<KonveksiModel> konveksiModelList;
    private List<KonveksiModel1> konveksiModel1List;
    private List<MonitoringModel> monitoringModelList;
    private List<TahapanModel> tahapanModelList;
    private List<String> spinnerItems;
    private AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMonitoringAdminBinding.inflate(inflater, container, false);
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
        AndroidNetworking.get(ApiServer.site_url_admin + "getKonveksi.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();

                            Log.d("response", "response::" + response);
                            if (response.getString("code").equalsIgnoreCase("1")) {
                                JSONArray array = response.getJSONArray("data");
                                konveksiModelList.clear();
                                Gson gson = new Gson();

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject konveksiObject = array.getJSONObject(i);
                                    KonveksiModel isi = gson.fromJson(konveksiObject + "", KonveksiModel.class);
                                    konveksiModelList.add(isi);
                                    spinnerItems.add(isi.getNama_produk() + " ("+isi.getStatus()+")");
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
                                        String selectedNamaProduk = konveksiModelList.get(position).getNama_produk();
                                        dataMonitoring(selectedNamaProduk, selectedIdKonveksi, selectedJenis, selectedJumlah, selectedTanggal, selectedTanggalPengambilan, selectedTglSelesai);
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

    private void dataMonitoring(String nama_produk, String id_konveksi, String jenis, String jumlah, String tanggal, String tanggal_pengambilan, String tgl_selesai) {
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
            binding.buttonCetak.setVisibility(View.VISIBLE);
            binding.buttonCetak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PrintManager printManager = (PrintManager) requireContext().getSystemService(PRINT_SERVICE);

                    WebView webView = new WebView(getContext());
                    webView.loadUrl(ApiServer.site_url_admin + "printSingleKonveksi.php?nama_produk="+nama_produk+" &id_konveksi="+id_konveksi);

                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            PrintDocumentAdapter adapter = webView.createPrintDocumentAdapter("Laporan User");
                            printManager.print("Laporan Konveksi", adapter, null);
                        }
                    });
                }
            });
        } else {
            binding.txtDurasi.setText("On Progress");
            binding.buttonCetak.setVisibility(View.GONE);
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
                                AdminMonitoringAdapter adapter = new AdminMonitoringAdapter(requireContext(), tahapanModelList, konveksiModel1List, monitoringModelList);
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