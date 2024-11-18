package com.example.tangkelek.activity.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.androidnetworking.AndroidNetworking;
import com.example.tangkelek.LoginActivity;
import com.example.tangkelek.R;
import com.example.tangkelek.databinding.ActivityMainAdminBinding;
import com.example.tangkelek.util.ApiServer;
import com.example.tangkelek.util.PrefManager;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.squareup.picasso.Picasso;

public class MainAdminActivity extends AppCompatActivity {
    private ActivityMainAdminBinding binding;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainAdminBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        AndroidNetworking.initialize(this);

        binding.txtName.setText("Welcome "+prefManager.getUsername());
        if (prefManager.getImg() != null && !prefManager.getImg().equals("null")) {
            Picasso.get()
                    .load(ApiServer.site_url_img_profile + prefManager.getImg())
                    .into(binding.imgProfile);
        } else {
            binding.imgProfile.setImageResource(R.drawable.baseline_man_24);
        }

        replaceFragment(new HomeAdminFragment());
        binding.ButtomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.Home:
                    replaceFragment(new HomeAdminFragment());
                    break;
                case R.id.Monitoring:
                    replaceFragment(new MonitoringAdminFragment());
                    break;
                case R.id.Produksi:
                    replaceFragment(new ProduksiAdminFragment());
                    break;
                case R.id.Tahapan:
                    replaceFragment(new TahapanAdminFragment());
                    break;
                case R.id.User:
                    replaceFragment(new UserAdminFragment());
                    break;
            }
            return true;
        });

        binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(binding.imgProfile);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.popup_menu_admin);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_profile) {
                    openSettingsActivity();
                    return true;
                } else if (itemId == R.id.menu_logout) {
                    performLogout();
                    return true;
                } else if (itemId == R.id.menu_laporanpencapaianproduksi) {
                    setTitle("Laporan Pencapaian");
                    performLaporan();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void performLaporan() {
        Intent intent = new Intent(MainAdminActivity.this, PencapainProduksiActivity.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {
        DialogProfileAdminActivity dialog = new DialogProfileAdminActivity();
        if (this != null) {
            dialog.showNow(this.getSupportFragmentManager(), "profile");
        }
    }

    private void performLogout() {
        PopupDialog.getInstance(this)
                .setStyle(Styles.IOS)
                .setHeading("Logout")
                .setDescription("Are you sure you want to logout?"+
                        " This action cannot be undone")
                .setCancelable(false)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onPositiveClicked(Dialog dialog) {
                        PrefManager prefManager = new PrefManager(MainAdminActivity.this);
                        prefManager.setLoginStatus(false);
                        Intent intent = new Intent(MainAdminActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        super.onPositiveClicked(dialog);
                    }

                    @Override
                    public void onNegativeClicked(Dialog dialog) {
                        super.onNegativeClicked(dialog);
                    }
                });
    }
}