package com.example.tangkelek.activity.admin;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.R;
import com.example.tangkelek.util.ApiServer;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;

public class DialogAddProduksiActivity extends AppCompatDialogFragment {
    private EditText editNamaProduk, editJenis, editS, editM, editL, editXL, editXXL, editXXXL, edit5XL, editTanggalPengembalian;
    private ImageView imgProduksi;
    private int tahun,bulan,tanggal;
    private Uri selectedImageUri;
    private static final int REQUEST_IMAGE = 1;
    private static final String URL_ADD_PRODUKSI =
            ApiServer.site_url_admin + "addProduksi.php";

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_add_produksi, null);

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
                .setTitle("Add Produksi")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addProduksi();
                        UpdateProduksiList();
                    }
                });
        return builder.create();
    }

    public interface OnProduksiAddedListener {
        void onProduksiAdded(String message);
        void onProduksiError(String errorMessage);
    }

    private OnProduksiAddedListener onProduksiAddedListener;

    public void setOnProduksiAddedListener(OnProduksiAddedListener listener) {
        this.onProduksiAddedListener = listener;
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

    private void addProduksi(){
        String addName = editNamaProduk.getText().toString();
        String addJenis = editJenis.getText().toString();
        String ukuranS = editS.getText().toString();
        String ukuranM = editM.getText().toString();
        String ukuranL = editL.getText().toString();
        String ukuranXL = editXL.getText().toString();
        String ukuranXXL = editXXL.getText().toString();
        String ukuranXXXL = editXXXL.getText().toString();
        String ukuran5XL = edit5XL.getText().toString();
        String tngglPengembalian = editTanggalPengembalian.getText().toString();

        int jumlahS = Integer.parseInt(ukuranS);
        int jumlahM = Integer.parseInt(ukuranM);
        int jumlahL = Integer.parseInt(ukuranL);
        int jumlahXL = Integer.parseInt(ukuranXL);
        int jumlahXXL = Integer.parseInt(ukuranXXL);
        int jumlahXXXL = Integer.parseInt(ukuranXXXL);
        int jumlah5XL = Integer.parseInt(ukuran5XL);

        int totalJumlah = jumlahS + jumlahM + jumlahL + jumlahXL + jumlahXXL + jumlahXXXL + jumlah5XL;

        String addJumlah = String.valueOf(totalJumlah);
        if (selectedImageUri != null) {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImageUri);
                File file = createFileFromInputStream(inputStream);
                if (file != null) {
                    AndroidNetworking.upload(URL_ADD_PRODUKSI)
                            .addMultipartFile("gambar", file)
                            .addMultipartParameter("nama_produk", addName)
                            .addMultipartParameter("jenis", addJenis)
                            .addMultipartParameter("ukuran_s", ukuranS)
                            .addMultipartParameter("ukuran_m", ukuranM)
                            .addMultipartParameter("ukuran_l", ukuranL)
                            .addMultipartParameter("ukuran_xl", ukuranXL)
                            .addMultipartParameter("ukuran_xxl", ukuranXXL)
                            .addMultipartParameter("ukuran_xxxl", ukuranXXXL)
                            .addMultipartParameter("ukuran_5xl", ukuran5XL)
                            .addMultipartParameter("jumlah", addJumlah)
                            .addMultipartParameter("tanggal_pengembalian", tngglPengembalian)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.d("response", "response::" + response);
                                        if (response.getString("code").equals("1")) {
                                            if (onProduksiAddedListener != null) {
                                                onProduksiAddedListener.onProduksiAdded("Add Produksi Berhasil");
                                            }
                                        } else {
                                            if (onProduksiAddedListener != null) {
                                                onProduksiAddedListener.onProduksiError("Add Produksi Gagal");
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        if (onProduksiAddedListener != null) {
                                            onProduksiAddedListener.onProduksiError("Add Produksi Gagal");
                                        }
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.d("response", "eror::" + anError);
                                    if (onProduksiAddedListener != null) {
                                        onProduksiAddedListener.onProduksiError("Tambah Data Produksi GAGAL");
                                    }
                                }
                            });
                } else {
                    if (onProduksiAddedListener != null) {
                        onProduksiAddedListener.onProduksiError("Failed to create file");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (onProduksiAddedListener != null) {
                    onProduksiAddedListener.onProduksiError("Failed to open input stream");
                }
            }
        } else {
            if (onProduksiAddedListener != null) {
                onProduksiAddedListener.onProduksiError("Select an image first");
            }
        }
    }

    private File createFileFromInputStream(InputStream inputStream) {
        try {
            String fileName = getFileName(selectedImageUri);
            String newFileName = addRandomNumberToFileName(fileName);
            File file = new File(getContext().getCacheDir(), newFileName);
            FileUtils.copyInputStreamToFile(inputStream, file);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            fileName = cursor.getString(nameIndex);
            cursor.close();
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

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void UpdateProduksiList() {
        Fragment produksiAdminFragment = new ProduksiAdminFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, produksiAdminFragment);
        transaction.commit();
    }
}