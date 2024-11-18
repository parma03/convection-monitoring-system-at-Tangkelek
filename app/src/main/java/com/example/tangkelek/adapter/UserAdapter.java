package com.example.tangkelek.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.R;
import com.example.tangkelek.activity.admin.DialogUpdateUserActivity;
import com.example.tangkelek.activity.admin.UserAdminFragment;
import com.example.tangkelek.model.UserModel;
import com.example.tangkelek.util.ApiServer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserModelViewHolder> implements DialogUpdateUserActivity.OnUserUpdatedListener {
    private Context context;
    List<UserModel> userModelList;

    public UserAdapter(Context context, List<UserModel> userModelList) {
        this.context = context;
        this.userModelList = userModelList;
    }

    @NonNull
    @Override
    public UserAdapter.UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_user, null);
        return new UserAdapter.UserModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserModelViewHolder holder, int position) {
        UserModel userModel = userModelList.get(position);
        holder.txtName.setText("Nama: "+userModel.getNama());
        if (userModel.getNohp() == null) {
            holder.txtNohp.setText("NOHP: Belum Diisi");
        } else {
            holder.txtNohp.setText("NOHP: " + userModel.getNohp());
        }
        holder.txtRole.setText("Role: "+userModel.getRole());
        if (userModel.getGambar() != null && !userModel.getGambar().equals("null")) {
            Picasso.get()
                    .load(ApiServer.site_url_img_profile + userModel.getGambar())
                    .into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(R.drawable.baseline_man_24);
        }
        holder.txtUsername.setText("Username: "+userModel.getUsername());
        holder.txtPassword.setText("Password: "+userModel.getPassword());
        holder.layoutUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog(userModel);
            }
        });

        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupDialog.getInstance(context)
                        .setStyle(Styles.ANDROID_DEFAULT)
                        .setHeading("Konfigurasi Hapus")
                        .setDescription("Data User akan dihapus, lanjutkan ?")
                        .setCancelable(false)
                        .showDialog(new OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClicked(Dialog dialog) {
                                hapusData(userModel.id_user);
                                userModelList.remove(holder.getAdapterPosition());
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

    private void showUpdateDialog(UserModel userModel) {
        DialogUpdateUserActivity dialog = new DialogUpdateUserActivity();
        Bundle args = new Bundle();
        args.putString("id_user", userModel.getId_user());
        args.putString("nama", userModel.getNama());
        args.putString("nohp", userModel.getNohp());
        args.putString("username", userModel.getUsername());
        args.putString("password", userModel.getPassword());
        args.putString("role", userModel.getRole());
        args.putString("gambar", userModel.getGambar());
        dialog.setArguments(args);

        AppCompatActivity activity = (AppCompatActivity) context;
        dialog.setOnUserUpdatedListener(this);
        dialog.showNow(activity.getSupportFragmentManager(), "update_user");
    }

    @Override
    public void onUserAdded(String message) {
        showSuccessNotification(message);
    }

    @Override
    public void onUserError(String errorMessage) {
        showErrorNotification(errorMessage);
    }

    private void UpdateUserList() {
        Fragment UserAdminFragment = new UserAdminFragment();
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, UserAdminFragment);
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
        return userModelList.size();
    }

    public void hapusData(String id_user) {
        if (userModelList.isEmpty()) {
            Toast.makeText(context, "Data User kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_admin + "deleteUser.php")
                .addBodyParameter("id_user", id_user)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if ("User berhasil dihapus".equals(message)) {
                                PopupDialog.getInstance(context)
                                        .setStyle(Styles.SUCCESS)
                                        .setHeading("BERHASIL !!!")
                                        .setDescription("Hapus Data User Berhasil")
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
                                        .setDescription("Gagal Hapus Data User")
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
                                    .setDescription("Gagal Hapus Data User")
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

    public class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNohp, txtUsername, txtPassword, txtRole;
        ImageView imgProfile;
        RelativeLayout layoutDelete, layoutUpdate;

        private Context context;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtNohp = itemView.findViewById(R.id.txtNohp);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtPassword = itemView.findViewById(R.id.txtPassword);
            txtRole = itemView.findViewById(R.id.txtRole);
            layoutDelete = itemView.findViewById(R.id.layoutDelete);
            layoutUpdate = itemView.findViewById(R.id.layoutEdit);
        }

    }

}