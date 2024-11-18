package com.example.tangkelek.activity.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.LoginActivity;
import com.example.tangkelek.R;
import com.example.tangkelek.activity.pengawas.MainPengawasActivity;
import com.example.tangkelek.util.ApiServer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DialogMonitoringActivity extends AppCompatDialogFragment {
    private String status_pengerjaan, deskripsi, tanggal, tanggal_selesai, gambar_proses;
    private TextView txtStatus, txtDeskripsi, txtDurasi;
    private ImageView imgGambarProses;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_monitoring, null);

        txtStatus = view.findViewById(R.id.txtStatus);
        txtDeskripsi = view.findViewById(R.id.txtDeskripsi);
        txtDurasi = view.findViewById(R.id.txtDurasi);
        imgGambarProses = view.findViewById(R.id.imgGambarProses);

        Bundle arguments = getArguments();
        if (arguments != null) {
            status_pengerjaan = arguments.getString("status_pengerjaan");
            deskripsi = arguments.getString("deskripsi");
            tanggal = arguments.getString("tanggal");
            tanggal_selesai = arguments.getString("tanggal_selesai");
            gambar_proses = arguments.getString("gambar_proses");

            if (gambar_proses != null && !gambar_proses.equals("null")) {
                Picasso.get()
                        .load(ApiServer.site_url_gambar_progress + gambar_proses)
                        .into(imgGambarProses);
            } else {
                imgGambarProses.setImageResource(R.drawable.baseline_add_24);
            }

            txtStatus.setText(status_pengerjaan+"("+tanggal_selesai+")");
            txtDeskripsi.setText(deskripsi);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date dateAwal = null;
            if (tanggal != null) {
                try {
                    dateAwal = dateFormat.parse(tanggal);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                txtDurasi.setText("On Progress");

            }

            Date dateAkhir = null;
            if (tanggal_selesai != null) {
                try {
                    dateAkhir = dateFormat.parse(tanggal_selesai);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                txtDurasi.setText("On Progress");
            }

            if (tanggal_selesai != null) {
                long selisihMillis = dateAkhir.getTime() - dateAwal.getTime();
                long selisihJam = TimeUnit.MILLISECONDS.toHours(selisihMillis) % 24;
                long selisihHari = TimeUnit.MILLISECONDS.toDays(selisihMillis);
                txtDurasi.setText(selisihHari + " Hari " + selisihJam + " Jam");
            } else {
                txtDurasi.setText("On Progress");
            }
        }

        builder.setView(view)
                .setTitle("Informasi Monitoring")
                .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        return builder.create();
    }
}