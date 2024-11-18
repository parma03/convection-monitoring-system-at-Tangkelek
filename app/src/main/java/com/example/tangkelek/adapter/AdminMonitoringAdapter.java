package com.example.tangkelek.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tangkelek.R;
import com.example.tangkelek.activity.admin.DialogMonitoringActivity;
import com.example.tangkelek.model.KonveksiModel1;
import com.example.tangkelek.model.MonitoringModel;
import com.example.tangkelek.model.TahapanModel;

import java.util.List;

public class AdminMonitoringAdapter extends RecyclerView.Adapter<AdminMonitoringAdapter.MonitoringModelViewHolder> {
    private Context context;
    List<TahapanModel> tahapanModelList;
    List<KonveksiModel1> konveksiModel1List;
    List<MonitoringModel> monitoringModelList;

    public AdminMonitoringAdapter(Context context, List<TahapanModel> tahapanModelList, List<KonveksiModel1> konveksiModel1List, List<MonitoringModel> monitoringModelList) {
        this.context = context;
        this.tahapanModelList = tahapanModelList;
        this.konveksiModel1List = konveksiModel1List;
        this.monitoringModelList = monitoringModelList;
    }

    @NonNull
    @Override
    public AdminMonitoringAdapter.MonitoringModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_monitoring, null);
        return new AdminMonitoringAdapter.MonitoringModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminMonitoringAdapter.MonitoringModelViewHolder holder, int position) {
        TahapanModel tahapanModel = tahapanModelList.get(position);
        KonveksiModel1 konveksiModel1 = konveksiModel1List.get(position);
        MonitoringModel monitoringModel = monitoringModelList.get(position);
        if (monitoringModel.getStatus_pengerjaan() != null) {
            switch (monitoringModel.getStatus_pengerjaan()) {
                case "Selesai":{
                    holder.txtMonitoring.setText(tahapanModel.getNama_tahap());
                    holder.txtProses.setText("Selesai");
                    holder.txtProses.setPaintFlags(holder.txtProses.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.see.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showAllMonitoring(monitoringModel, konveksiModel1);
                        }
                    });
                    break;
                }
                case "Dikerjakan":{
                    holder.txtMonitoring.setText(tahapanModel.getNama_tahap());
                    holder.txtProses.setText("Dikerjakan");
                    holder.see.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showAllMonitoring(monitoringModel, konveksiModel1);
                        }
                    });
                    break;
                }
            }
        } else {
            holder.txtMonitoring.setText(tahapanModel.getNama_tahap());
            holder.txtProses.setVisibility(View.GONE);
            holder.see.setVisibility(View.GONE);
            holder.view2.setVisibility(View.GONE);
        }
    }

    private void showAllMonitoring(MonitoringModel monitoringModel, KonveksiModel1 konveksiModel1) {
        DialogMonitoringActivity dialog = new DialogMonitoringActivity();
        Bundle args = new Bundle();
        dialog.setArguments(args);
        args.putString("status_pengerjaan", monitoringModel.getStatus_pengerjaan());
        args.putString("deskripsi", monitoringModel.getDeskripsi());
        args.putString("tanggal", konveksiModel1.getTanggal());
        args.putString("tanggal_selesai", monitoringModel.getTanggal_selesai());
        args.putString("gambar_proses", monitoringModel.getGambar_proses());

        AppCompatActivity activity = (AppCompatActivity) context;
        dialog.showNow(activity.getSupportFragmentManager(), "monitoring");
    }


    public int getItemCount() {
        return tahapanModelList.size();
    }

    public class MonitoringModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtMonitoring,txtProses;
        ImageView imgtahap,arrowDown,see;
        View view1, view2;
        private Context context;

        public MonitoringModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMonitoring = itemView.findViewById(R.id.txtMonitoring);
            txtProses = itemView.findViewById(R.id.txtProses);
            view1 = itemView.findViewById(R.id.view1);
            view2 = itemView.findViewById(R.id.view2);
            arrowDown = itemView.findViewById(R.id.arrowDown);
            see = itemView.findViewById(R.id.see);
            imgtahap = itemView.findViewById(R.id.imgtahap);
        }

    }

}