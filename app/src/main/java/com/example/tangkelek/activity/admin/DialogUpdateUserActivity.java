package com.example.tangkelek.activity.admin;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.R;
import com.example.tangkelek.util.ApiServer;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class DialogUpdateUserActivity extends AppCompatDialogFragment {
    private Context context;
    private EditText editNama, editNohp, editUsername, editPassword;
    private String id_user, nama, nohp, username, password, role;
    private Spinner editSpinnerRole;
    private static final String URL_UPDATE_USER =
            ApiServer.site_url_admin + "updateUser.php";

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_update_user, null);

        editNama = view.findViewById(R.id.editNama);
        editSpinnerRole = view.findViewById(R.id.editSpinnerRole);
        editNohp = view.findViewById(R.id.editNohp);
        editUsername = view.findViewById(R.id.editUsername);
        editPassword = view.findViewById(R.id.editPassword);

        Bundle arguments = getArguments();
        if (arguments != null) {
            id_user = arguments.getString("id_user");
            nama = arguments.getString("nama");
            nohp = arguments.getString("nohp");
            username = arguments.getString("username");
            password = arguments.getString("password");
            role = arguments.getString("role");

            editNama.setText(nama);
            editNohp.setText(nohp);
            int statusPosition = getIndexByValue(editSpinnerRole, role);
            editSpinnerRole.setSelection(statusPosition);
            editUsername.setText(username);
            editPassword.setText(password);
        }

        builder.setView(view)
                .setTitle("Update User")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateUser();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public interface OnUserUpdatedListener {
        void onUserAdded(String message);
        void onUserError(String errorMessage);
    }

    private OnUserUpdatedListener onUserUpdatedListener;

    public void setOnUserUpdatedListener(OnUserUpdatedListener listener) {
        this.onUserUpdatedListener = listener;
    }

    private void UpdateUser() {
        String updatedName = editNama.getText().toString();
        String updatedNohp = editNohp.getText().toString();
        String updatedRole = editSpinnerRole.getSelectedItem().toString();
        String updatedUsername = editUsername.getText().toString();
        String updatedPassword = editPassword.getText().toString();

        if (updatedName.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Nama", Toast.LENGTH_SHORT).show();
            return;
        }
        if (updatedNohp.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom NOHP", Toast.LENGTH_SHORT).show();
            return;
        }
        if (updatedUsername.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (updatedPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Password", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(URL_UPDATE_USER)
                .addBodyParameter("id_user", id_user)
                .addBodyParameter("name", updatedName)
                .addBodyParameter("nohp", updatedNohp)
                .addBodyParameter("role", updatedRole)
                .addBodyParameter("username", updatedUsername)
                .addBodyParameter("password", updatedPassword)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                if (onUserUpdatedListener != null) {
                                    onUserUpdatedListener.onUserAdded("Update Data User Berhasil");
                                }
                            } else {
                                if (onUserUpdatedListener != null) {
                                    onUserUpdatedListener.onUserError("Gagal Update Data User");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        if (getActivity() != null) {
                            if (onUserUpdatedListener != null) {
                                onUserUpdatedListener.onUserError("Gagal Update Data User");
                            }
                        }
                    }
                });
    }

    private int getIndexByValue(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                return i;
            }
        }
        return 0;
    }
}