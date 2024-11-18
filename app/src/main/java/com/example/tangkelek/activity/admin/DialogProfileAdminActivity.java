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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.LoginActivity;
import com.example.tangkelek.R;
import com.example.tangkelek.util.ApiServer;
import com.example.tangkelek.util.PrefManager;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class DialogProfileAdminActivity extends AppCompatDialogFragment {
    private Context context;
    private EditText editUsername, editPassword;
    private ImageView editImgProfile;
    private static final int REQUEST_IMAGE = 1;
    private Uri selectedImageUri;
    PrefManager prefManager;
    private static final String URL_UPDATE_PROFILE =
            ApiServer.site_url_admin + "updateProfile.php";
    private static final String URL_DELETE_GAMBAR =
            ApiServer.site_url_admin + "deleteFotoProfile.php";

    @SuppressLint("MissingInflatedId")
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_profile_admin, null);

        prefManager = new PrefManager(requireContext());

        editUsername = view.findViewById(R.id.editUsername);
        editPassword = view.findViewById(R.id.editPassword);
        editImgProfile = view.findViewById(R.id.editImgProfile);

        editUsername.setText(prefManager.getUsername());
        editPassword.setText(prefManager.getPassword());

        if (prefManager.getImg() != null && !prefManager.getImg().equals("null")) {
            Picasso.get()
                    .load(ApiServer.site_url_img_profile + prefManager.getImg())
                    .into(editImgProfile);
        } else {
            editImgProfile.setImageResource(R.drawable.baseline_man_24);
        }

        editImgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        builder.setView(view)
                .setTitle("Update Profile")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateProfile();
                        refresh();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void UpdateProfile() {
        if (selectedImageUri != null && !prefManager.getImg().equals("null")) {
            deleteFotoMenu();
        } else if (selectedImageUri != null && prefManager.getImg().equals("null")) {
            updateMenu(selectedImageUri);
        } else {
            updateMenu(null);
        }
    }

    private void deleteFotoMenu() {
        AndroidNetworking.post(URL_DELETE_GAMBAR)
                .addBodyParameter("gambar", prefManager.getImg())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        updateMenu(selectedImageUri);
                    }
                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    private void updateMenu(Uri newImageUri) {
        String updateUsername = editUsername.getText().toString();
        String updatePassword = editPassword.getText().toString();

        if (updateUsername.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Username", Toast.LENGTH_SHORT).show();
            return;
        } else if (updateUsername.length() < 5) {
            Toast.makeText(getActivity(), "Username harus minimal 5 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        if (updatePassword.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Password", Toast.LENGTH_SHORT).show();
            return;
        } else if (updatePassword.length() < 5 || !containsNumber(updatePassword)) {
            Toast.makeText(getActivity(), "Password harus minimal 5 karakter dan mengandung angka", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.upload(URL_UPDATE_PROFILE)
                .addMultipartParameter("id_user", prefManager.getId())
                .addMultipartParameter("username", updateUsername)
                .addMultipartParameter("password", updatePassword)
                .addMultipartFile("gambar", createFileFromUri(selectedImageUri))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
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
            editImgProfile.setImageURI(selectedImageUri);
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

    private boolean containsNumber(String s) {
        // Fungsi untuk memeriksa apakah string mengandung angka
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private void refresh(){
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        prefManager.setLoginStatus(false);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        requireContext().startActivity(intent);
        getActivity().finish();
    }
}