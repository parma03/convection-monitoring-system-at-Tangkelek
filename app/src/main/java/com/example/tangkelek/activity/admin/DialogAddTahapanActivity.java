package com.example.tangkelek.activity.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
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

public class DialogAddTahapanActivity extends AppCompatDialogFragment {
    private EditText editNamaTahapan;
    private static final String URL_ADD_Tahapan =
            ApiServer.site_url_admin + "addTahapan.php";

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_add_tahapan, null);

        editNamaTahapan = view.findViewById(R.id.editNamaTahapan);

        builder.setView(view)
                .setTitle("Add Tahapan")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddTahapan();
                        UpdateTahapanList();
                    }
                });
        return builder.create();
    }

    public interface OnTahapanAddedListener {
        void onTahapanAdded(String message);
        void onTahapanError(String errorMessage);
    }

    private OnTahapanAddedListener onTahapanAddedListener;

    public void setOnTahapanAddedListener(OnTahapanAddedListener listener) {
        this.onTahapanAddedListener = listener;
    }

    private void AddTahapan(){
        String addName = editNamaTahapan.getText().toString();
        if (addName.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Nama Tahapan", Toast.LENGTH_SHORT).show();
            return;
        }
        AndroidNetworking.post(URL_ADD_Tahapan)
                .addBodyParameter("nama_tahap", addName)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                if (onTahapanAddedListener != null) {
                                    onTahapanAddedListener.onTahapanAdded("Add Tahapan Berhasil");
                                }
                            } else {
                                if (onTahapanAddedListener != null) {
                                    onTahapanAddedListener.onTahapanError("Add Tahapan Gagal");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (onTahapanAddedListener != null) {
                                onTahapanAddedListener.onTahapanError("Add Tahapan Gagal");
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response", "eror::" + anError);
                        if (onTahapanAddedListener != null) {
                            onTahapanAddedListener.onTahapanError("Tambah Data Tahapan GAGAL");
                        }
                    }
                });
    }

    private void UpdateTahapanList() {
        Fragment tahapanAdminFragment = new TahapanAdminFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, tahapanAdminFragment);
        transaction.commit();
    }
}