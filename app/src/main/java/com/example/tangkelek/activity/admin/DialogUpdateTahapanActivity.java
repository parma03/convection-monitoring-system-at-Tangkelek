package com.example.tangkelek.activity.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
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

public class DialogUpdateTahapanActivity extends AppCompatDialogFragment {
    private Context context;
    private EditText editNamaTahapan;
    private String id_tahap, nama_tahap;
    private static final String URL_UPDATE_TAHAPAN =
            ApiServer.site_url_admin + "updateTahapan.php";

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_update_tahapan, null);

        editNamaTahapan = view.findViewById(R.id.editNamaTahapan);

        Bundle arguments = getArguments();
        if (arguments != null) {
            id_tahap = arguments.getString("id_tahap");
            nama_tahap = arguments.getString("nama_tahap");

            editNamaTahapan.setText(nama_tahap);
        }

        builder.setView(view)
                .setTitle("Update Tahapan")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateTahapan();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public interface OnTahapanUpdatedListener {
        void onTahapanAdded(String message);
        void onTahapanError(String errorMessage);
    }

    private OnTahapanUpdatedListener onTahapanUpdatedListener;

    public void setOnTahapanUpdatedListener(OnTahapanUpdatedListener listener) {
        this.onTahapanUpdatedListener = listener;
    }

    private void UpdateTahapan() {
        String updatedName = editNamaTahapan.getText().toString();

        if (updatedName.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Nama Tahapan", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(URL_UPDATE_TAHAPAN)
                .addBodyParameter("id_tahap", id_tahap)
                .addBodyParameter("nama_tahap", updatedName)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                if (onTahapanUpdatedListener != null) {
                                    onTahapanUpdatedListener.onTahapanAdded("Update Data Tahapan Berhasil");
                                }
                            } else {
                                if (onTahapanUpdatedListener != null) {
                                    onTahapanUpdatedListener.onTahapanError("Gagal Update Data Tahapan");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        if (getActivity() != null) {
                            if (onTahapanUpdatedListener != null) {
                                onTahapanUpdatedListener.onTahapanError("Gagal Update Data Tahapan");
                            }
                        }
                    }
                });
    }
}