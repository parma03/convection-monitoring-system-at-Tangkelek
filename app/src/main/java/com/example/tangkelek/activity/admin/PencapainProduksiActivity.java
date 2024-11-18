package com.example.tangkelek.activity.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.adapter.AdminProdukusiAdapter;
import com.example.tangkelek.databinding.ActivityPencapainProduksiBinding;
import com.example.tangkelek.model.KonveksiModel;
import com.example.tangkelek.model.MonitoringModel;
import com.example.tangkelek.util.ApiServer;
import com.example.tangkelek.util.PrefManager;
import com.google.gson.Gson;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PencapainProduksiActivity extends AppCompatActivity {
    private ActivityPencapainProduksiBinding binding;
    PrefManager prefManager;
    private List<KonveksiModel> konveksiModelList;
    private static final String URL_PRODUKSI = ApiServer.site_url_admin + "getProduksi.php";
    private static final String URL_PRINT = ApiServer.site_url_admin + "printPencapaian.php";
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPencapainProduksiBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        AndroidNetworking.initialize(this);
        dialog = new SpotsDialog.Builder().setContext(PencapainProduksiActivity.this).setMessage("Loading ......").setCancelable(false).build();

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(PencapainProduksiActivity.this));
        konveksiModelList = new ArrayList<>();

        dataProduksi();

        switch (prefManager.getTipe()){
            case "admin":{
                AndroidNetworking.get(ApiServer.site_url_admin+"getInfo.php")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("response", "response::" + response);
                                try {
                                    if (response.getString("code").equals("1")){
                                        JSONArray array = response.getJSONArray("data");
                                        JSONObject data = array.getJSONObject(0);
                                        binding.txtJMLDikerjakan.setText("Dikerjakan : "+data.getString("JMLDikerjakan")+"/"+data.getString("JMLProduksi"));
                                        binding.txtJMLSelesai.setText("Selesai : "+data.getString("JMLSelesai")+"/"+data.getString("JMLProduksi"));
                                        binding.txtUkuran1.setText("S: "+data.getString("ukuran_s")+"|M: "+data.getString("ukuran_m")+"|L: "+data.getString("ukuran_l")+"|XL: "+data.getString("ukuran_xl"));
                                        binding.txtUkuran2.setText("XXL: "+data.getString("ukuran_xxl")+"|XXXL: "+data.getString("ukuran_xxxl")+"|5XL: "+data.getString("ukuran_5xl"));
                                        binding.txtTotal.setText("TOTAL : "+data.getString("JMLTotal"));
                                    }else {
                                        binding.txtJMLDikerjakan.setText("0");
                                        binding.txtJMLSelesai.setText("0");
                                        binding.txtUkuran2.setText("0");
                                        binding.txtUkuran1.setText("0");
                                        binding.txtTotal.setText("0");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                binding.txtJMLDikerjakan.setText("0");
                                binding.txtJMLSelesai.setText("0");
                                binding.txtUkuran2.setText("0");
                                binding.txtUkuran1.setText("0");
                                binding.txtTotal.setText("0");
                            }
                        });
                break;
            }
            case "pengawas":{
                binding.btnCetak.setVisibility(View.GONE);
                binding.btnClear.setVisibility(View.GONE);
                break;
            }
            case "manajer":{
                binding.btnCetak.setVisibility(View.GONE);
                binding.btnClear.setVisibility(View.GONE);
                break;
            }
        }

        binding.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupDialog.getInstance(PencapainProduksiActivity.this)
                        .setStyle(Styles.ANDROID_DEFAULT)
                        .setHeading("Konfigurasi Hapus")
                        .setDescription("Membersihkan Data Konveksi Selesai & Ditolak, lanjutkan ?")
                        .setCancelable(false)
                        .showDialog(new OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClicked(Dialog dialog) {
                                hapusData();
                                refresh();
                                super.onPositiveClicked(dialog);
                            }

                            @Override
                            public void onNegativeClicked(Dialog dialog) {
                                super.onNegativeClicked(dialog);
                            }
                        });
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PencapainProduksiActivity.this, MainAdminActivity.class);
                startActivity(intent);
            }
        });

        binding.btnCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);

                WebView webView = new WebView(PencapainProduksiActivity.this);
                webView.loadUrl(URL_PRINT);

                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        PrintDocumentAdapter adapter = webView.createPrintDocumentAdapter("Laporan Pencapaian");
                        PrintAttributes.Builder builder = new PrintAttributes.Builder();
                        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A5);
                        PrintJob printJob = printManager.print("Laporan Pencapaian", adapter, builder.build());
                    }
                });
            }
        });

    }

    private void dataProduksi() {
        dialog.show();
        AndroidNetworking.get(URL_PRODUKSI)
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
                                    KonveksiModel konveksi = gson.fromJson(konveksiObject.toString(), KonveksiModel.class);
                                    konveksiModelList.add(konveksi);
                                }
                                AdminProdukusiAdapter adapter = new AdminProdukusiAdapter(PencapainProduksiActivity.this, konveksiModelList);
                                binding.recyclerView.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("catch", "produksimodel::" + e.toString());
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

    public void hapusData() {

        AndroidNetworking.get(ApiServer.site_url_admin + "clearKonveksi.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if ("Konveksi berhasil dihapus".equals(message)) {
                                PopupDialog.getInstance(PencapainProduksiActivity.this)
                                        .setStyle(Styles.SUCCESS)
                                        .setHeading("BERHASIL !!!")
                                        .setDescription("Hapus Data Konveksi Berhasil")
                                        .setCancelable(false)
                                        .showDialog(new OnDialogButtonClickListener() {
                                            @Override
                                            public void onDismissClicked(Dialog dialog) {
                                                super.onDismissClicked(dialog);
                                            }
                                        });
                            } else {
                                PopupDialog.getInstance(PencapainProduksiActivity.this)
                                        .setStyle(Styles.FAILED)
                                        .setHeading("GAGAL !!!")
                                        .setDescription("Gagal Hapus Data Konveksi")
                                        .setCancelable(false)
                                        .showDialog(new OnDialogButtonClickListener() {
                                            @Override
                                            public void onDismissClicked(Dialog dialog) {
                                                super.onDismissClicked(dialog);
                                            }
                                        });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            PopupDialog.getInstance(PencapainProduksiActivity.this)
                                    .setStyle(Styles.FAILED)
                                    .setHeading("GAGAL !!!")
                                    .setDescription("Gagal Hapus Data Konveksi")
                                    .setCancelable(false)
                                    .showDialog(new OnDialogButtonClickListener() {
                                        @Override
                                        public void onDismissClicked(Dialog dialog) {
                                            super.onDismissClicked(dialog);
                                        }
                                    });                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        PopupDialog.getInstance(PencapainProduksiActivity.this)
                                .setStyle(Styles.FAILED)
                                .setHeading("GAGAL !!!")
                                .setDescription("Gagal Hapus : "+ anError.getErrorBody())
                                .setCancelable(false)
                                .showDialog(new OnDialogButtonClickListener() {
                                    @Override
                                    public void onDismissClicked(Dialog dialog) {
                                        super.onDismissClicked(dialog);
                                    }
                                });
                        Toast.makeText(PencapainProduksiActivity.this, "Hapus gagal: " + anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void refresh() {
        Intent intent = new Intent(PencapainProduksiActivity.this, PencapainProduksiActivity.class);
        startActivity(intent);
    }
}