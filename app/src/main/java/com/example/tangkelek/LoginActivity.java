package com.example.tangkelek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.activity.admin.MainAdminActivity;
import com.example.tangkelek.activity.manajer.MainManajerActivity;
import com.example.tangkelek.activity.pengawas.MainPengawasActivity;
import com.example.tangkelek.databinding.ActivityLoginBinding;
import com.example.tangkelek.util.ApiServer;
import com.example.tangkelek.util.PrefManager;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private PrefManager prefManager;
    AlertDialog dialog;
    private static final String URL_CHECK_LOGIN_ADMIN = ApiServer.site_url_admin + "checkLogin.php";
    private static final String URL_CHECK_LOGIN_PENGAWAS = ApiServer.site_url_pengawas + "checkLogin.php";
    private static final String URL_CHECK_LOGIN_MANAJER = ApiServer.site_url_manajer + "checkLogin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AndroidNetworking.initialize(this);
        prefManager = new PrefManager(this);

        dialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Please Wait.").setCancelable(false).build();

        binding.lupaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        dialog.show();
        String username = binding.txtUsername.getText().toString().trim();
        String password = binding.txtPassword.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            showEmptyFieldsDialog();
            dialog.dismiss();
            return;
        }

        AndroidNetworking.post(URL_CHECK_LOGIN_ADMIN)
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.hide();
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                JSONArray array = response.getJSONArray("data");
                                JSONObject object = array.getJSONObject(0);
                                String id = object.getString("id_user");
                                prefManager.setId(id);
                                prefManager.setUsername(object.getString("username"));
                                prefManager.setPassword(object.getString("password"));
                                prefManager.setImg(object.getString("gambar"));
                                prefManager.setLoginStatus(true);
                                prefManager.setTipe("admin");
                                Intent intent = new Intent(LoginActivity.this, MainAdminActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                loginPengawas();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.hide();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.hide();
                    }
                });
    }

    private void loginPengawas() {
        String username = binding.txtUsername.getText().toString().trim();
        String password = binding.txtPassword.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            showEmptyFieldsDialog();
            dialog.dismiss();
            return;
        }
        AndroidNetworking.post(URL_CHECK_LOGIN_PENGAWAS)
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.hide();
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                JSONArray array = response.getJSONArray("data");
                                JSONObject object = array.getJSONObject(0);
                                String id = object.getString("id_pengawas");
                                prefManager.setId(id);
                                prefManager.setNama(object.getString("nama"));
                                prefManager.setUsername(object.getString("username"));
                                prefManager.setPassword(object.getString("password"));
                                prefManager.setNohp(object.getString("nohp"));
                                prefManager.setLoginStatus(true);
                                prefManager.setTipe("pengawas");
                                prefManager.setImg(object.getString("gambar"));
                                Intent intent = new Intent(LoginActivity.this, MainPengawasActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                loginManajer();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.hide();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.hide();
                    }
                });
    }

    private void loginManajer() {
        String username = binding.txtUsername.getText().toString().trim();
        String password = binding.txtPassword.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            showEmptyFieldsDialog();
            dialog.dismiss();
            return;
        }
        AndroidNetworking.post(URL_CHECK_LOGIN_MANAJER)
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.hide();
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                JSONArray array = response.getJSONArray("data");
                                JSONObject object = array.getJSONObject(0);
                                String id = object.getString("id_manajer");
                                prefManager.setId(id);
                                prefManager.setNama(object.getString("nama"));
                                prefManager.setUsername(object.getString("username"));
                                prefManager.setPassword(object.getString("password"));
                                prefManager.setNohp(object.getString("nohp"));
                                prefManager.setLoginStatus(true);
                                prefManager.setTipe("manajer");
                                prefManager.setImg(object.getString("gambar"));
                                Intent intent = new Intent(LoginActivity.this, MainManajerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                loginGagal();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.hide();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.hide();
                    }
                });
    }

    private void loginGagal() {
        PopupDialog.getInstance(LoginActivity.this)
                .setStyle(Styles.FAILED)
                .setHeading("WRONG CREDENTIAL !!!")
                .setDescription("Wrong Login Username or Password.")
                .setCancelable(false)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        super.onDismissClicked(dialog);
                    }
                });
    }

    private void showEmptyFieldsDialog() {
        PopupDialog.getInstance(LoginActivity.this)
                .setStyle(Styles.FAILED)
                .setHeading("ERROR !!!")
                .setDescription("Please fill in all fields.")
                .setCancelable(false)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        super.onDismissClicked(dialog);
                    }
                });
    }
}