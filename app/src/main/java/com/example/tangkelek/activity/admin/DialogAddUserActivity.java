package com.example.tangkelek.activity.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.R;
import com.example.tangkelek.util.ApiServer;

import org.json.JSONException;
import org.json.JSONObject;

public class DialogAddUserActivity extends AppCompatDialogFragment {
    private EditText editNama, editNohp, editUsername, editPassword;
    private Spinner editSpinnerRole;
    private static final String URL_ADD_USER =
            ApiServer.site_url_admin + "addUser.php";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_add_user, null);

        editNama = view.findViewById(R.id.editNama);
        editNohp = view.findViewById(R.id.editNohp);
        editSpinnerRole = view.findViewById(R.id.editSpinnerRole);
        editUsername = view.findViewById(R.id.editUsername);
        editPassword = view.findViewById(R.id.editPassword);

        builder.setView(view)
                .setTitle("Add User")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddUser();
                        UpdateUserList();
                    }
                });
        return builder.create();
    }

    public interface OnUserAddedListener {
        void onUserAdded(String message);
        void onUserError(String errorMessage);
    }

    private OnUserAddedListener onUserAddedListener;

    public void setOnUserAddedListener(OnUserAddedListener listener) {
        this.onUserAddedListener = listener;
    }

    private void AddUser(){
        String addName = editNama.getText().toString();
        String addNohp = editNohp.getText().toString();
        String addRole = editSpinnerRole.getSelectedItem().toString();
        String addUsername = editUsername.getText().toString();
        String addPassword = editPassword.getText().toString();
        if (addName.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Nama", Toast.LENGTH_SHORT).show();
            return;
        }
        if (addNohp.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom NOHP", Toast.LENGTH_SHORT).show();
            return;
        }
        if (addRole.isEmpty()) {
            Toast.makeText(getActivity(), "Pilih Role User", Toast.LENGTH_SHORT).show();
            return;
        }
        if (addUsername.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (addPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Password", Toast.LENGTH_SHORT).show();
            return;
        }
        AndroidNetworking.post(URL_ADD_USER)
                .addBodyParameter("name", addName)
                .addBodyParameter("nohp", addNohp)
                .addBodyParameter("role", addRole)
                .addBodyParameter("username", addUsername)
                .addBodyParameter("password", addPassword)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                if (onUserAddedListener != null) {
                                    onUserAddedListener.onUserAdded("Add User Berhasil");
                                }
                            } else if (response.getString("code").equals("3")) {
                                if (onUserAddedListener != null) {
                                    onUserAddedListener.onUserError("Username Sudah Terpakai");
                                }
                            } else {
                                if (onUserAddedListener != null) {
                                    onUserAddedListener.onUserError("Add User Gagal");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (onUserAddedListener != null) {
                                onUserAddedListener.onUserError("Add User Gagal");
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response", "eror::" + anError);
                        if (onUserAddedListener != null) {
                            onUserAddedListener.onUserError("Tambah Data User GAGAL");
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void UpdateUserList() {
        Fragment userAdminFragment = new UserAdminFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, userAdminFragment);
        transaction.commit();
    }
}