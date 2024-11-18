package com.example.tangkelek.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.R;
import com.example.tangkelek.activity.admin.DialogMonitoringActivity;
import com.example.tangkelek.activity.admin.DialogUpdateUserActivity;
import com.example.tangkelek.activity.admin.UserAdminFragment;
import com.example.tangkelek.activity.manajer.MonitoringManajerFragment;
import com.example.tangkelek.activity.pengawas.DialogUpdateMonitoringActivity;
import com.example.tangkelek.model.KonveksiModel1;
import com.example.tangkelek.model.MonitoringModel;
import com.example.tangkelek.model.TahapanModel;
import com.example.tangkelek.util.ApiServer;
import com.example.tangkelek.util.PrefManager;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PengawasMonitoringAdapter extends RecyclerView.Adapter<PengawasMonitoringAdapter.MonitoringModelViewHolder> implements DialogUpdateMonitoringActivity.OnMonitoringUpdatedListener {
    private Context context;
    List<TahapanModel> tahapanModelList;
    List<KonveksiModel1> konveksiModel1List;
    List<MonitoringModel> monitoringModelList;
    PrefManager prefManager;

    public PengawasMonitoringAdapter(Context context, List<TahapanModel> tahapanModelList, List<KonveksiModel1> konveksiModel1List, List<MonitoringModel> monitoringModelList) {
        this.context = context;
        this.tahapanModelList = tahapanModelList;
        this.konveksiModel1List = konveksiModel1List;
        this.monitoringModelList = monitoringModelList;
        this.prefManager = new PrefManager(context);
    }

    @NonNull
    @Override
    public PengawasMonitoringAdapter.MonitoringModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_monitoring1, null);
        return new PengawasMonitoringAdapter.MonitoringModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PengawasMonitoringAdapter.MonitoringModelViewHolder holder, int position) {
        TahapanModel tahapanModel = tahapanModelList.get(position);
        KonveksiModel1 konveksiModel1 = konveksiModel1List.get(position);
        MonitoringModel monitoringModel = monitoringModelList.get(position);

        switch (prefManager.getTipe()) {
            case "manajer": {
                if (monitoringModel.getStatus_pengerjaan() != null) {
                    switch (monitoringModel.getStatus_pengerjaan()) {
                        case "Selesai":{
                            holder.txtMonitoring.setText(tahapanModel.getNama_tahap());
                            holder.txtProses.setText("Selesai");
                            holder.txtProses.setPaintFlags(holder.txtProses.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            holder.see.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showAllMonitoring(monitoringModel, konveksiModel1);
                                }
                            });
                            holder.edit.setVisibility(View.GONE);
                            holder.delete.setVisibility(View.GONE);
                            break;
                        }
                        case "Dikerjakan":{
                            holder.txtMonitoring.setText(tahapanModel.getNama_tahap());
                            holder.txtProses.setText("Dikerjakan");
                            holder.see.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showAllMonitoring(monitoringModel, konveksiModel1);
                                }
                            });
                            holder.edit.setVisibility(View.GONE);
                            holder.delete.setVisibility(View.GONE);
                            break;
                        }
                    }
                } else {
                    holder.txtMonitoring.setText(tahapanModel.getNama_tahap());
                    holder.txtProses.setVisibility(View.GONE);
                    holder.see.setVisibility(View.GONE);
                    holder.view2.setVisibility(View.GONE);
                    holder.delete.setVisibility(View.GONE);
                    holder.edit.setVisibility(View.GONE);
                }
            }
            case "pengawas":{
                if (monitoringModel.getStatus_pengerjaan() != null) {
                    switch (monitoringModel.getStatus_pengerjaan()) {
                        case "Selesai":{
                            holder.txtMonitoring.setText(tahapanModel.getNama_tahap());
                            holder.txtProses.setText("Selesai");
                            holder.txtProses.setPaintFlags(holder.txtProses.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            holder.see.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showAllMonitoring(monitoringModel, konveksiModel1);
                                }
                            });

                            holder.edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showUpdateDialog(monitoringModel);
                                }
                            });
                            holder.delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PopupDialog.getInstance(context)
                                            .setStyle(Styles.ANDROID_DEFAULT)
                                            .setHeading("Batalkan Tahapan !!!")
                                            .setDescription("Monitoring akan Dikembalikan ke tahap sebelumnya, lanjutkan ?")
                                            .setCancelable(false)
                                            .showDialog(new OnDialogButtonClickListener() {
                                                @Override
                                                public void onPositiveClicked(Dialog dialog) {
                                                    hapusData(monitoringModel.id);
                                                    UpdateMonitoringList();
                                                    super.onPositiveClicked(dialog);
                                                }

                                                @Override
                                                public void onNegativeClicked(Dialog dialog) {
                                                    super.onNegativeClicked(dialog);
                                                }
                                            });
                                }
                            });
                            break;
                        }
                        case "Dikerjakan":{
                            holder.txtMonitoring.setText(tahapanModel.getNama_tahap());
                            holder.txtProses.setText("Dikerjakan");
                            holder.see.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showAllMonitoring(monitoringModel, konveksiModel1);
                                }
                            });
                            holder.edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showUpdateDialog(monitoringModel);
                                }
                            });
                            holder.delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PopupDialog.getInstance(context)
                                            .setStyle(Styles.ANDROID_DEFAULT)
                                            .setHeading("Batalkan Tahapan !!!")
                                            .setDescription("Monitoring akan Dikembalikan ke tahap sebelumnya, lanjutkan ?")
                                            .setCancelable(false)
                                            .showDialog(new OnDialogButtonClickListener() {
                                                @Override
                                                public void onPositiveClicked(Dialog dialog) {
                                                    hapusData(monitoringModel.id);
                                                    Log.d("response", "id::" + monitoringModel.id);
                                                    UpdateMonitoringList();
                                                    super.onPositiveClicked(dialog);
                                                }

                                                @Override
                                                public void onNegativeClicked(Dialog dialog) {
                                                    super.onNegativeClicked(dialog);
                                                }
                                            });
                                }
                            });
                            break;
                        }
                    }
                } else {
                    holder.txtMonitoring.setText(tahapanModel.getNama_tahap());
                    holder.txtProses.setVisibility(View.GONE);
                    holder.see.setVisibility(View.GONE);
                    holder.view2.setVisibility(View.GONE);
                    holder.delete.setVisibility(View.GONE);
                    holder.edit.setVisibility(View.GONE);
                }
            }
        }
    }

    private void showUpdateDialog(MonitoringModel monitoringModel) {
        DialogUpdateMonitoringActivity dialog = new DialogUpdateMonitoringActivity();
        Bundle args = new Bundle();
        args.putString("id_monitoring", monitoringModel.getId_monitoring());
        args.putString("id_pengerjaan", monitoringModel.getId_pengerjaan());
        args.putString("deskripsi", monitoringModel.getDeskripsi());
        args.putString("gambar_proses", monitoringModel.getGambar_proses());
        args.putString("status_pengerjaan", monitoringModel.getStatus_pengerjaan());
        dialog.setArguments(args);

        AppCompatActivity activity = (AppCompatActivity) context;
        dialog.setOnMonitoringUpdatedListener(this);
        dialog.showNow(activity.getSupportFragmentManager(), "update_user");
    }

    private void showAllMonitoring(MonitoringModel monitoringModel, KonveksiModel1 konveksiModel1) {
        DialogMonitoringActivity dialog = new DialogMonitoringActivity();
        Bundle args = new Bundle();
        dialog.setArguments(args);
        args.putString("status_pengerjaan", monitoringModel.getStatus_pengerjaan());
        args.putString("deskripsi", monitoringModel.getDeskripsi());
        args.putString("tanggal", konveksiModel1.getTanggal());
        args.putString("tanggal_selesai", monitoringModel.getTanggal_selesai());
        args.putString("gambar_proses", monitoringModel.getGambar_proses());

        AppCompatActivity activity = (AppCompatActivity) context;
        dialog.showNow(activity.getSupportFragmentManager(), "monitoring");
    }

    @Override
    public void onMonitoringAdded(String message) {
        showSuccessNotification(message);
    }

    @Override
    public void onMonitoringError(String errorMessage) {
        showErrorNotification(errorMessage);
    }

    private void UpdateMonitoringList() {
        Fragment monitoringManajerFragment = new MonitoringManajerFragment();
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, monitoringManajerFragment);
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
                        UpdateMonitoringList();
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
                        UpdateMonitoringList();
                        super.onDismissClicked(dialog);
                    }
                });
    }

    public void hapusData(String id) {
        if (monitoringModelList.isEmpty()) {
            Toast.makeText(context, "Data Monitoring kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("response", "id::" + id);

        AndroidNetworking.post(ApiServer.site_url_manajer + "deleteMonitoring.php")
                .addBodyParameter("id", id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if ("Tahapan Monitoring Dibatalkan".equals(message)) {
                                PopupDialog.getInstance(context)
                                        .setStyle(Styles.SUCCESS)
                                        .setHeading("BERHASIL !!!")
                                        .setDescription("Tahapan Monitoring Dibatalkan")
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
                                        .setDescription("Gagal Membatalkan")
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
                                    .setDescription("Gagal Membatalkan")
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
                                .setStyle(Styles.SUCCESS)
                                .setHeading("BERHASIL !!!")
                                .setDescription("Tahapan Monitoring Dibatalkan")
                                .setCancelable(false)
                                .showDialog(new OnDialogButtonClickListener() {
                                    @Override
                                    public void onDismissClicked(Dialog dialog) {
                                        super.onDismissClicked(dialog);
                                    }
                                });
                    }
                });
    }

    public int getItemCount() {
        return tahapanModelList.size();
    }

    public class MonitoringModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtMonitoring,txtProses;
        ImageView imgtahap,arrowDown,see,edit,delete;
        View view1, view2;
        private Context context;

        public MonitoringModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMonitoring = itemView.findViewById(R.id.txtMonitoring);
            txtProses = itemView.findViewById(R.id.txtProses);
            view1 = itemView.findViewById(R.id.view1);
            view2 = itemView.findViewById(R.id.view2);
            arrowDown = itemView.findViewById(R.id.arrowDown);
            see = itemView.findViewById(R.id.see);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            imgtahap = itemView.findViewById(R.id.imgtahap);
        }

    }

}