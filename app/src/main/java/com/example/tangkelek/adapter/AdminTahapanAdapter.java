package com.example.tangkelek.adapter;

import android.app.Dialog;
import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.R;
import com.example.tangkelek.activity.admin.DialogUpdateTahapanActivity;
import com.example.tangkelek.activity.admin.DialogUpdateUserActivity;
import com.example.tangkelek.activity.admin.TahapanAdminFragment;
import com.example.tangkelek.activity.admin.UserAdminFragment;
import com.example.tangkelek.model.TahapanModel;
import com.example.tangkelek.model.UserModel;
import com.example.tangkelek.util.ApiServer;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdminTahapanAdapter extends RecyclerView.Adapter<AdminTahapanAdapter.TahapanModelViewHolder> implements DialogUpdateTahapanActivity.OnTahapanUpdatedListener {
    private Context context;
    List<TahapanModel> tahapanModelList;

    public AdminTahapanAdapter(Context context, List<TahapanModel> tahapanModelList) {
        this.context = context;
        this.tahapanModelList = tahapanModelList;
    }

    @NonNull
    @Override
    public AdminTahapanAdapter.TahapanModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_tahapan, null);
        return new AdminTahapanAdapter.TahapanModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminTahapanAdapter.TahapanModelViewHolder holder, int position) {
        TahapanModel tahapanModel = tahapanModelList.get(position);
        holder.txtName.setText("Nama: "+tahapanModel.getNama_tahap());
        holder.layoutUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog(tahapanModel);
            }
        });

        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupDialog.getInstance(context)
                        .setStyle(Styles.ANDROID_DEFAULT)
                        .setHeading("Konfigurasi Hapus")
                        .setDescription("Data Tahapan akan dihapus, lanjutkan ?")
                        .setCancelable(false)
                        .showDialog(new OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClicked(Dialog dialog) {
                                hapusData(tahapanModel.id_tahap);
                                tahapanModelList.remove(holder.getAdapterPosition());
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

    private void showUpdateDialog(TahapanModel tahapanModel) {
        DialogUpdateTahapanActivity dialog = new DialogUpdateTahapanActivity();
        Bundle args = new Bundle();
        args.putString("id_tahap", tahapanModel.getId_tahap());
        args.putString("nama_tahap", tahapanModel.getNama_tahap());
        dialog.setArguments(args);

        AppCompatActivity activity = (AppCompatActivity) context;
        dialog.setOnTahapanUpdatedListener(this);
        dialog.showNow(activity.getSupportFragmentManager(), "update_user");
    }

    @Override
    public void onTahapanAdded(String message) {
        showSuccessNotification(message);
    }

    @Override
    public void onTahapanError(String errorMessage) {
        showErrorNotification(errorMessage);
    }

    private void UpdateUserList() {
        Fragment TahapanAdminFragment = new TahapanAdminFragment();
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, TahapanAdminFragment);
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
                        UpdateUserList();
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
                        UpdateUserList();
                        super.onDismissClicked(dialog);
                    }
                });
    }

    public int getItemCount() {
        return tahapanModelList.size();
    }

    public void hapusData(String id_tahap) {
        if (tahapanModelList.isEmpty()) {
            Toast.makeText(context, "Data Tahapan kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_admin + "deleteTahapan.php")
                .addBodyParameter("id_tahap", id_tahap)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if ("Tahapan berhasil dihapus".equals(message)) {
                                PopupDialog.getInstance(context)
                                        .setStyle(Styles.SUCCESS)
                                        .setHeading("BERHASIL !!!")
                                        .setDescription("Hapus Data Tahapan Berhasil")
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
                                        .setDescription("Gagal Hapus Data Tahapan")
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
                                    .setDescription("Gagal Hapus Data Tahapan")
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

    public class TahapanModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        RelativeLayout layoutDelete, layoutUpdate;

        private Context context;

        public TahapanModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            layoutDelete = itemView.findViewById(R.id.layoutDelete);
            layoutUpdate = itemView.findViewById(R.id.layoutEdit);
        }

    }

}