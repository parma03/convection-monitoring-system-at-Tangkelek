package com.example.tangkelek.activity.admin;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.Random;

public class DialogUpdateProduksiActivity extends AppCompatDialogFragment {
    private Context context;
    private EditText editNamaProduk, editJenis, editS, editM, editL, editXL, editXXL, editXXXL, edit5XL, editTanggalPengembalian;
    private ImageView imgProduksi;
    private int tahun,bulan,tanggal;
    private String id_konveksi, nama_produk, gambar, jenis, ukuran_s, ukuran_m, ukuran_l,ukuran_xl,ukuran_xxl,ukuran_xxxl,ukuran_5xl, tanggal_pengambilan;
    private Uri selectedImageUri;
    private static final int REQUEST_IMAGE = 1;
    private static final String URL_UPDATE_PRODUKSI =
            ApiServer.site_url_admin + "updateProduk.php";
    private static final String URL_DELETE_GAMBAR =
            ApiServer.site_url_admin + "deleteFotoProduksi.php";

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_update_produksi, null);

        editNamaProduk = view.findViewById(R.id.editNamaProduk);
        editJenis = view.findViewById(R.id.editJenis);
        editS = view.findViewById(R.id.editS);
        editM = view.findViewById(R.id.editM);
        editL = view.findViewById(R.id.editL);
        editXL = view.findViewById(R.id.editXL);
        editXXL = view.findViewById(R.id.editXXL);
        editXXXL = view.findViewById(R.id.editXXXL);
        edit5XL = view.findViewById(R.id.edit5XL);
        imgProduksi = view.findViewById(R.id.imgProduksi);
        editTanggalPengembalian = view.findViewById(R.id.editTanggalPengembalian);

        Bundle arguments = getArguments();
        if (arguments != null) {
            id_konveksi = arguments.getString("id_konveksi");
            nama_produk = arguments.getString("nama_produk");
            gambar = arguments.getString("gambar");
            jenis = arguments.getString("jenis");
            ukuran_s = arguments.getString("ukuran_s");
            ukuran_m = arguments.getString("ukuran_m");
            ukuran_l = arguments.getString("ukuran_l");
            ukuran_xl = arguments.getString("ukuran_xl");
            ukuran_xxl = arguments.getString("ukuran_xxl");
            ukuran_xxxl = arguments.getString("ukuran_xxxl");
            ukuran_5xl = arguments.getString("ukuran_5xl");
            tanggal_pengambilan = arguments.getString("tanggal_pengambilan");

            editNamaProduk.setText(nama_produk);
            editJenis.setText(jenis);
            editS.setText(ukuran_s);
            editM.setText(ukuran_m);
            editL.setText(ukuran_l);
            editXL.setText(ukuran_xl);
            editXXL.setText(ukuran_xxl);
            editXXXL.setText(ukuran_xxxl);
            edit5XL.setText(ukuran_5xl);
            Picasso.get()
                    .load(ApiServer.site_url_gambar_produk + gambar)
                    .into(imgProduksi);
            editTanggalPengembalian.setText(tanggal_pengambilan);
        }

        editTanggalPengembalian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                tahun = calendar.get(Calendar.YEAR);
                bulan = calendar.get(Calendar.MONTH);
                tanggal = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog;
                dialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tahun = year;
                        bulan = month;
                        tanggal = dayOfMonth;

                        editTanggalPengembalian.setText(String.format("%04d-%02d-%02d", tahun, bulan + 1, tanggal));
                    }
                }, tahun,bulan,tanggal);
                dialog.show();
            }
        });
        imgProduksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        builder.setView(view)
                .setTitle("Update Produksi")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateProduksi();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public interface OnProduksiUpdatedListener {
        void onProduksiAdded(String message);
        void onProduksiError(String errorMessage);
    }

    private OnProduksiUpdatedListener onProduksiUpdatedListener;

    public void setOnProduksiUpdatedListener(OnProduksiUpdatedListener listener) {
        this.onProduksiUpdatedListener = listener;
    }

    private void UpdateProduksi() {
        if (selectedImageUri != null && gambar != null) {
            deleteFotoProduksi();
        } else {
            updateMenu(null);
        }
    }

    private void deleteFotoProduksi() {
        AndroidNetworking.post(URL_DELETE_GAMBAR)
                .addBodyParameter("gambar", gambar)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        updateMenu(selectedImageUri);
                    }
                    @Override
                    public void onError(ANError anError) {
                        if (onProduksiUpdatedListener != null) {
                            onProduksiUpdatedListener.onProduksiError("Gagal Update Data Produksi");
                        }
                    }
                });
    }

    private void updateMenu(Uri newImageUri) {
        String updatedName = editNamaProduk.getText().toString();
        String updatedJenis = editJenis.getText().toString();
        String updatedS = editS.getText().toString();
        String updatedM = editM.getText().toString();
        String updatedL = editL.getText().toString();
        String updatedXL = editXL.getText().toString();
        String updatedXXL = editXXL.getText().toString();
        String updatedXXXL = editXXXL.getText().toString();
        String updated5XL = edit5XL.getText().toString();
        String updatedtanggalPengembalian = editTanggalPengembalian.getText().toString();

        int jumlahS = Integer.parseInt(updatedS);
        int jumlahM = Integer.parseInt(updatedM);
        int jumlahL = Integer.parseInt(updatedL);
        int jumlahXL = Integer.parseInt(updatedXL);
        int jumlahXXL = Integer.parseInt(updatedXXL);
        int jumlahXXXL = Integer.parseInt(updatedXXXL);
        int jumlah5XL = Integer.parseInt(updated5XL);

        int totalJumlah = jumlahS + jumlahM + jumlahL + jumlahXL + jumlahXXL + jumlahXXXL + jumlah5XL;

        String updatedJumlah = String.valueOf(totalJumlah);

        AndroidNetworking.upload(URL_UPDATE_PRODUKSI)
                .addMultipartParameter("id_konveksi", id_konveksi)
                .addMultipartParameter("nama_produk", updatedName)
                .addMultipartParameter("jenis", updatedJenis)
                .addMultipartParameter("ukuran_s", updatedS)
                .addMultipartParameter("ukuran_m", updatedM)
                .addMultipartParameter("ukuran_l", updatedL)
                .addMultipartParameter("ukuran_xl", updatedXL)
                .addMultipartParameter("ukuran_xxl", updatedXXL)
                .addMultipartParameter("ukuran_xxxl", updatedXXXL)
                .addMultipartParameter("ukuran_5xl", updated5XL)
                .addMultipartParameter("tanggal_pengambalian", updatedtanggalPengembalian)
                .addMultipartParameter("jumlah", updatedJumlah)
                .addMultipartFile("gambar", createFileFromUri(selectedImageUri))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                if (onProduksiUpdatedListener != null) {
                                    onProduksiUpdatedListener.onProduksiAdded("Update Data Produksi Berhasil");
                                }
                            } else {
                                if (onProduksiUpdatedListener != null) {
                                    onProduksiUpdatedListener.onProduksiError("Gagal Update Data Produksi");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        if (getActivity() != null) {
                            if (onProduksiUpdatedListener != null) {
                                onProduksiUpdatedListener.onProduksiError("Gagal Update Data Produksi");
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
            imgProduksi.setImageURI(selectedImageUri);
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