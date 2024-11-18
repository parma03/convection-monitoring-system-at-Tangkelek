package com.example.tangkelek.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.R;
import com.example.tangkelek.activity.admin.DialogUpdateProduksiActivity;
import com.example.tangkelek.activity.admin.DialogUpdateUserActivity;
import com.example.tangkelek.activity.admin.ProduksiAdminFragment;
import com.example.tangkelek.activity.admin.UserAdminFragment;
import com.example.tangkelek.model.KonveksiModel;
import com.example.tangkelek.model.UserModel;
import com.example.tangkelek.util.ApiServer;
import com.example.tangkelek.util.PrefManager;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdminProdukusiAdapter extends RecyclerView.Adapter<AdminProdukusiAdapter.KonveksiModelViewHolder> implements DialogUpdateProduksiActivity.OnProduksiUpdatedListener {
    private Context context;
    List<KonveksiModel> konveksiModelList;
    PrefManager prefManager;

    public AdminProdukusiAdapter(Context context, List<KonveksiModel> konveksiModelList) {
        this.context = context;
        this.konveksiModelList = konveksiModelList;
        this.prefManager = new PrefManager(context);
    }

    @NonNull
    @Override
    public AdminProdukusiAdapter.KonveksiModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_produksi, null);
        return new AdminProdukusiAdapter.KonveksiModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProdukusiAdapter.KonveksiModelViewHolder holder, int position) {
        KonveksiModel konveksiModel = konveksiModelList.get(position);
        holder.txtNameProduk.setText(konveksiModel.getNama_produk()+" Untuk Tanggal "+konveksiModel.getTanggal_pengambilan());
        if (konveksiModel.getGambar() != null && !konveksiModel.getGambar().equals("null")) {
            Picasso.get()
                    .load(ApiServer.site_url_gambar_produk + konveksiModel.getGambar())
                    .into(holder.imgDesain);
        } else {
            holder.imgDesain.setImageResource(R.drawable.baseline_add_24);
        }
        holder.txtJenis.setText(konveksiModel.getJenis());
        holder.txtS.setText("S : "+konveksiModel.getUkuran_s());
        holder.txtM.setText("M : "+konveksiModel.getUkuran_m());
        holder.txtL.setText("L : "+konveksiModel.getUkuran_l());
        holder.txtXL.setText("XL : "+konveksiModel.getUkuran_xl());
        holder.txtXXL.setText("XXL : "+konveksiModel.getUkuran_xxl());
        holder.txtXXXL.setText("XXXL : "+konveksiModel.getUkuran_xxxl());
        holder.txt5XL.setText("5XL : "+konveksiModel.getUkuran_5xl());
        holder.txtJumlah.setText("Total "+konveksiModel.getJumlah()+" PCS");

        switch (prefManager.getTipe()){
            case "admin":{
                switch (konveksiModel.status){
                    case "Dikerjakan":{
                        holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.abuabu)));
                        holder.txtStatus.setText("Dikerjakan");
                        holder.layoutDelete.setVisibility(View.GONE);
                         holder.layoutTerima.setVisibility(View.GONE);
                        holder.layoutTolak.setVisibility(View.GONE);
                        break;
                    }
                    case "Diajukan":{
                        holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple_200)));
                        holder.txtStatus.setText("Diajukan");
                        holder.layoutTerima.setVisibility(View.GONE);
                        holder.layoutTolak.setVisibility(View.GONE);
                        break;
                    }
                    case "Selesai":{
                        holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
                        holder.txtStatus.setText("Selesai");
                        holder.layoutDelete.setVisibility(View.GONE);
                        holder.layoutTerima.setVisibility(View.GONE);
                        holder.layoutTolak.setVisibility(View.GONE);
                        break;
                    }
                    case "Ditolak":{
                        holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                        holder.txtStatus.setText("Ditolak");
                        holder.layoutTerima.setVisibility(View.GONE);
                        holder.layoutTolak.setVisibility(View.GONE);
                        break;
                    }
                }
                break;
            }
            case "manajer":{
                switch (konveksiModel.status){
                    case "Dikerjakan":{
                        holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.abuabu)));
                        holder.txtStatus.setText("Dikerjakan");
                        holder.layoutDelete.setVisibility(View.GONE);
                        holder.layoutUpdate.setVisibility(View.GONE);
                        holder.layoutTerima.setVisibility(View.GONE);
                        holder.layoutTolak.setVisibility(View.GONE);
                        break;
                    }
                    case "Diajukan":{
                        holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple_200)));
                        holder.txtStatus.setText("Diajukan");
                        holder.layoutDelete.setVisibility(View.GONE);
                        holder.layoutUpdate.setVisibility(View.GONE);
                        break;
                    }
                    case "Selesai":{
                        holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
                        holder.txtStatus.setText("Selesai");
                        holder.layoutDelete.setVisibility(View.GONE);
                        holder.layoutUpdate.setVisibility(View.GONE);
                        holder.layoutTerima.setVisibility(View.GONE);
                        holder.layoutTolak.setVisibility(View.GONE);
                        break;
                    }
                    case "Ditolak":{
                        holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                        holder.txtStatus.setText("Ditolak");
                        holder.layoutDelete.setVisibility(View.GONE);
                        holder.layoutUpdate.setVisibility(View.GONE);
                        holder.layoutTerima.setVisibility(View.GONE);
                        holder.layoutTolak.setVisibility(View.GONE);
                        break;
                    }
                }
                break;
            }
            case "pengawas": {
                switch (konveksiModel.status) {
                    case "Dikerjakan": {
                        holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.abuabu)));
                        holder.txtStatus.setText("Dikerjakan");
                        holder.layoutDelete.setVisibility(View.GONE);
                        holder.layoutUpdate.setVisibility(View.GONE);
                        holder.layoutTerima.setVisibility(View.GONE);
                        holder.layoutTolak.setVisibility(View.GONE);
                        break;
                    }
                    case "Diajukan": {
                        holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple_200)));
                        holder.txtStatus.setText("Diajukan");
                        holder.layoutDelete.setVisibility(View.GONE);
                        holder.layoutUpdate.setVisibility(View.GONE);
                        holder.layoutTerima.setVisibility(View.GONE);
                        holder.layoutTolak.setVisibility(View.GONE);
                        break;
                    }
                    case "Selesai": {
                        holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
                        holder.txtStatus.setText("Selesai");
                        holder.layoutDelete.setVisibility(View.GONE);
                        holder.layoutUpdate.setVisibility(View.GONE);
                        holder.layoutTerima.setVisibility(View.GONE);
                        holder.layoutTolak.setVisibility(View.GONE);
                        break;
                    }
                    case "Ditolak": {
                        holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                        holder.txtStatus.setText("Ditolak");
                        holder.layoutDelete.setVisibility(View.GONE);
                        holder.layoutUpdate.setVisibility(View.GONE);
                        holder.layoutTerima.setVisibility(View.GONE);
                        holder.layoutTolak.setVisibility(View.GONE);
                        break;
                    }
                }
                break;
            }
        }

        holder.layoutUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog(konveksiModel);
            }
        });

        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupDialog.getInstance(context)
                        .setStyle(Styles.ANDROID_DEFAULT)
                        .setHeading("Konfigurasi Hapus")
                        .setDescription("Data Produksi akan dihapus, lanjutkan ?")
                        .setCancelable(false)
                        .showDialog(new OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClicked(Dialog dialog) {
                                hapusData(konveksiModel.id_konveksi);
                                konveksiModelList.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                super.onPositiveClicked(dialog);
                            }

                            @Override
                            public void onNegativeClicked(Dialog dialog) {
                                super.onNegativeClicked(dialog);
                            }
                        });
            }
        });

        holder.layoutTerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupDialog.getInstance(context)
                        .setStyle(Styles.ANDROID_DEFAULT)
                        .setHeading("Terima Produk")
                        .setDescription("Data Produksi akan diterima, lanjutkan ?")
                        .setCancelable(false)
                        .showDialog(new OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClicked(Dialog dialog) {
                                terimaData(konveksiModel.id_konveksi);
                                konveksiModelList.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                super.onPositiveClicked(dialog);
                            }

                            @Override
                            public void onNegativeClicked(Dialog dialog) {
                                super.onNegativeClicked(dialog);
                            }
                        });
            }
        });

        holder.layoutTolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupDialog.getInstance(context)
                        .setStyle(Styles.ANDROID_DEFAULT)
                        .setHeading("Tolak Produk")
                        .setDescription("Data Produksi akan ditolak, lanjutkan ?")
                        .setCancelable(false)
                        .showDialog(new OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClicked(Dialog dialog) {
                                tolakData(konveksiModel.id_konveksi);
                                konveksiModelList.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                super.onPositiveClicked(dialog);
                            }

                            @Override
                            public void onNegativeClicked(Dialog dialog) {
                                super.onNegativeClicked(dialog);
                            }
                        });
            }
        });
    }

    private void showUpdateDialog(KonveksiModel konveksiModel) {
        DialogUpdateProduksiActivity dialog = new DialogUpdateProduksiActivity();
        Bundle args = new Bundle();
        args.putString("id_konveksi", konveksiModel.getId_konveksi());
        args.putString("nama_produk", konveksiModel.getNama_produk());
        args.putString("gambar", konveksiModel.getGambar());
        args.putString("jenis", konveksiModel.getJenis());
        args.putString("ukuran_s", konveksiModel.getUkuran_s());
        args.putString("ukuran_m", konveksiModel.getUkuran_m());
        args.putString("ukuran_l", konveksiModel.getUkuran_l());
        args.putString("ukuran_xl", konveksiModel.getUkuran_xl());
        args.putString("ukuran_xxl", konveksiModel.getUkuran_xxl());
        args.putString("ukuran_xxxl", konveksiModel.getUkuran_xxxl());
        args.putString("ukuran_5xl", konveksiModel.getUkuran_5xl());
        args.putString("tanggal_pengambilan", konveksiModel.getTanggal_pengambilan());
        dialog.setArguments(args);

        AppCompatActivity activity = (AppCompatActivity) context;
        dialog.setOnProduksiUpdatedListener(this);
        dialog.showNow(activity.getSupportFragmentManager(), "update_produksi");
    }

    @Override
    public void onProduksiAdded(String message) {
        showSuccessNotification(message);
    }

    @Override
    public void onProduksiError(String errorMessage) {
        showErrorNotification(errorMessage);
    }

    private void UpdateProduksiList() {
        Fragment produksiAdminFragment = new ProduksiAdminFragment();
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, produksiAdminFragment);
        transaction.commit();
    }

    private void showSuccessNotification(String message) {
        PopupDialog.getInstance(context)
                .setStyle(Styles.SUCCESS)
                .setHeading("BERHASIL !!!")
                .setDescription(message)
                .setCancelable(false)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        UpdateProduksiList();
                        super.onDismissClicked(dialog);
                    }
                });
    }

    private void showErrorNotification(String message) {
        PopupDialog.getInstance(context)
                .setStyle(Styles.FAILED)
                .setHeading("GAGAL !!!")
                .setDescription(message)
                .setCancelable(false)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        UpdateProduksiList();
                        super.onDismissClicked(dialog);
                    }
                });
    }

    public int getItemCount() {
        return konveksiModelList.size();
    }

    public void hapusData(String id_konveksi) {
        if (konveksiModelList.isEmpty()) {
            Toast.makeText(context, "Data Konveksi kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_admin + "deleteKonveksi.php")
                .addBodyParameter("id_konveksi", id_konveksi)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if ("Konveksi berhasil dihapus".equals(message)) {
                                PopupDialog.getInstance(context)
                                        .setStyle(Styles.SUCCESS)
                                        .setHeading("BERHASIL !!!")
                                        .setDescription("Hapus Data Produksi Berhasil")
                                        .setCancelable(false)
                                        .showDialog(new OnDialogButtonClickListener() {
                                            @Override
                                            public void onDismissClicked(Dialog dialog) {
                                                super.onDismissClicked(dialog);
                                            }
                                        });
                            } else {
                                PopupDialog.getInstance(context)
                                        .setStyle(Styles.FAILED)
                                        .setHeading("GAGAL !!!")
                                        .setDescription("Gagal Hapus Data Produksi")
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
                            PopupDialog.getInstance(context)
                                    .setStyle(Styles.FAILED)
                                    .setHeading("GAGAL !!!")
                                    .setDescription("Gagal Hapus Data Produksi")
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
                        PopupDialog.getInstance(context)
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
                        Toast.makeText(context, "Hapus gagal: " + anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void tolakData(String id_konveksi) {
        if (konveksiModelList.isEmpty()) {
            Toast.makeText(context, "Data Konveksi kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_admin + "TolakKonveksi.php")
                .addBodyParameter("id_konveksi", id_konveksi)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if ("Konveksi berhasil dihapus".equals(message)) {
                                PopupDialog.getInstance(context)
                                        .setStyle(Styles.SUCCESS)
                                        .setHeading("BERHASIL !!!")
                                        .setDescription("Hapus Data Produksi Berhasil")
                                        .setCancelable(false)
                                        .showDialog(new OnDialogButtonClickListener() {
                                            @Override
                                            public void onDismissClicked(Dialog dialog) {
                                                super.onDismissClicked(dialog);
                                            }
                                        });
                            } else {
                                PopupDialog.getInstance(context)
                                        .setStyle(Styles.FAILED)
                                        .setHeading("GAGAL !!!")
                                        .setDescription("Gagal Hapus Data Produksi")
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
                            PopupDialog.getInstance(context)
                                    .setStyle(Styles.FAILED)
                                    .setHeading("GAGAL !!!")
                                    .setDescription("Gagal Hapus Data Produksi")
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
                        PopupDialog.getInstance(context)
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
                        Toast.makeText(context, "Hapus gagal: " + anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void terimaData(String id_konveksi) {
        if (konveksiModelList.isEmpty()) {
            Toast.makeText(context, "Data Konveksi kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_admin + "terimaKonveksi.php")
                .addBodyParameter("id_konveksi", id_konveksi)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if ("Konveksi berhasil dihapus".equals(message)) {
                                PopupDialog.getInstance(context)
                                        .setStyle(Styles.SUCCESS)
                                        .setHeading("BERHASIL !!!")
                                        .setDescription("Hapus Data Produksi Berhasil")
                                        .setCancelable(false)
                                        .showDialog(new OnDialogButtonClickListener() {
                                            @Override
                                            public void onDismissClicked(Dialog dialog) {
                                                super.onDismissClicked(dialog);
                                            }
                                        });
                            } else {
                                PopupDialog.getInstance(context)
                                        .setStyle(Styles.FAILED)
                                        .setHeading("GAGAL !!!")
                                        .setDescription("Gagal Hapus Data Produksi")
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
                            PopupDialog.getInstance(context)
                                    .setStyle(Styles.FAILED)
                                    .setHeading("GAGAL !!!")
                                    .setDescription("Gagal Hapus Data Produksi")
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
                        PopupDialog.getInstance(context)
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
                        Toast.makeText(context, "Hapus gagal: " + anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class KonveksiModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtNameProduk, txtJenis, txtJumlah, txtStatus, txtS, txtM, txtL, txtXL, txtXXL, txtXXXL, txt5XL;
        ImageView imgDesain;
        RelativeLayout layoutDelete, layoutUpdate, layoutTerima, layoutTolak;

        private Context context;

        public KonveksiModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNameProduk = itemView.findViewById(R.id.txtNameProduk);
            txtJenis = itemView.findViewById(R.id.txtJenis);
            imgDesain = itemView.findViewById(R.id.imgDesain);
            txtS = itemView.findViewById(R.id.txtS);
            txtM = itemView.findViewById(R.id.txtM);
            txtL = itemView.findViewById(R.id.txtL);
            txtXL = itemView.findViewById(R.id.txtXL);
            txtXXL = itemView.findViewById(R.id.txtXXL);
            txtXXXL = itemView.findViewById(R.id.txtXXXL);
            txt5XL = itemView.findViewById(R.id.txt5XL);
            txtJumlah = itemView.findViewById(R.id.txtJumlah);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            layoutDelete = itemView.findViewById(R.id.layoutDelete);
            layoutUpdate = itemView.findViewById(R.id.layoutEdit);
            layoutTerima = itemView.findViewById(R.id.layoutTerima);
            layoutTolak = itemView.findViewById(R.id.layoutTolak);
        }

    }

}