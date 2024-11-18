package com.example.tangkelek.activity.pengawas;

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
import com.example.tangkelek.activity.admin.DialogUpdateTahapanActivity;
import com.example.tangkelek.util.ApiServer;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Random;

public class DialogUpdateMonitoringActivity extends AppCompatDialogFragment {
    private Context context;
    private EditText editTextDeskripsi;
    private Spinner spinnerStatus;
    private ImageView imgProses;
    private String id_monitoring, id_pengerjaan, gambar_proses,deskripsi, status_pengerjaan;
    private Uri selectedImageUri;
    private static final int REQUEST_IMAGE = 1;
    private static final String URL_UPDATE_MONITORING =
            ApiServer.site_url_manajer + "updateMonitoring.php";
    private static final String URL_DELETE_GAMBAR =
            ApiServer.site_url_manajer + "deleteGambarMonitoring.php";


    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_update_monitoring, null);

        editTextDeskripsi = view.findViewById(R.id.editTextDeskripsi);
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        imgProses = view.findViewById(R.id.imgProses);

        Bundle arguments = getArguments();
        if (arguments != null) {
            id_monitoring = arguments.getString("id_monitoring");
            id_pengerjaan = arguments.getString("id_pengerjaan");
            deskripsi = arguments.getString("deskripsi");
            gambar_proses = arguments.getString("gambar_proses");
            status_pengerjaan = arguments.getString("status_pengerjaan");

            editTextDeskripsi.setText(deskripsi);
            if (gambar_proses != null && !gambar_proses.equals("null")) {
                Picasso.get()
                        .load(ApiServer.site_url_gambar_progress + gambar_proses)
                        .into(imgProses);
            } else {
                imgProses.setImageResource(R.drawable.baseline_add_24);
            }

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.status, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStatus.setAdapter(adapter);

            int position = adapter.getPosition(status_pengerjaan);
            spinnerStatus.setSelection(position);
        }

        imgProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

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
                        UpdateMonitoring();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public interface OnMonitoringUpdatedListener {
        void onMonitoringAdded(String message);
        void onMonitoringError(String errorMessage);
    }

    private OnMonitoringUpdatedListener onMonitoringUpdatedListener;

    public void setOnMonitoringUpdatedListener(OnMonitoringUpdatedListener listener) {
        this.onMonitoringUpdatedListener = listener;
    }

    private void UpdateMonitoring() {
        if (selectedImageUri != null && gambar_proses != null && !Objects.equals(status_pengerjaan, "Selesai")) {
            deleteFotoMonitoring();
        } else if (selectedImageUri != null && gambar_proses != null && !status_pengerjaan.equals("Dikerjakan")) {
            updateMenu(selectedImageUri);
        }else {
            updateMenu(null);
        }
    }

    private void deleteFotoMonitoring() {
        AndroidNetworking.post(URL_DELETE_GAMBAR)
                .addBodyParameter("gambar_proses", gambar_proses)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        updateMenu(selectedImageUri);
                    }
                    @Override
                    public void onError(ANError anError) {
                        if (onMonitoringUpdatedListener != null) {
                            onMonitoringUpdatedListener.onMonitoringError("Gagal Update Data Monitoring");
                        }
                    }
                });
    }

    private void updateMenu(Uri newImageUri) {
        String deskripsiBaru = editTextDeskripsi.getText().toString();
        String statusBaru = spinnerStatus.getSelectedItem().toString();

        Log.d("response", "statusBaru::" + statusBaru);

        AndroidNetworking.upload(URL_UPDATE_MONITORING)
                .addMultipartParameter("id_monitoring", id_monitoring)
                .addMultipartParameter("deskripsi", deskripsiBaru)
                .addMultipartParameter("id_pengerjaan", id_pengerjaan)
                .addMultipartParameter("status_pengerjaan", statusBaru)
                .addMultipartFile("gambar_proses", createFileFromUri(selectedImageUri))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                if (onMonitoringUpdatedListener != null) {
                                    onMonitoringUpdatedListener.onMonitoringAdded("Update Data Monitoring Berhasil");
                                }
                            } else {
                                if (onMonitoringUpdatedListener != null) {
                                    onMonitoringUpdatedListener.onMonitoringError("Gagal Update Data Monitoring");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        if (getActivity() != null) {
                            if (onMonitoringUpdatedListener != null) {
                                onMonitoringUpdatedListener.onMonitoringError("Gagal Update Data Monitoring");
                            }
                        }
                    }
                });
    }


    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imgProses.setImageURI(selectedImageUri);
        }
    }

    private File createFileFromUri(Uri uri) {
        try {
            if (uri != null) {
                String fileName = getFileName(uri);
                if (fileName != null) {
                    String newFileName = addRandomNumberToFileName(fileName);
                    File file = new File(context.getCacheDir(), newFileName);
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    FileUtils.copyInputStreamToFile(inputStream, file);
                    return file;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri != null) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                fileName = cursor.getString(nameIndex);
                cursor.close();
            }
        }
        return fileName;
    }

    private String addRandomNumberToFileName(String fileName) {
        if (fileName != null) {
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex != -1) {
                String extension = fileName.substring(dotIndex);
                String nameWithoutExtension = fileName.substring(0, dotIndex);
                Random random = new Random();
                int randomNumber = random.nextInt(10000);
                return nameWithoutExtension + "_" + randomNumber + extension;
            }
        }
        return fileName;
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