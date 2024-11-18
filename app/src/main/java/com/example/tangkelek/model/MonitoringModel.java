package com.example.tangkelek.model;

public class MonitoringModel {
    public String id;
    public String id_monitoring;
    public String id_pengerjaan;
    public String deskripsi;
    public String gambar_proses;
    public String tanggal_selesai;
    public String status_pengerjaan;
    public MonitoringModel(String id, String id_monitoring, String id_pengerjaan, String deskripsi, String gambar_proses, String tanggal_selesai, String status_pengerjaan) {
        this.id = id;
        this.id_monitoring = id_monitoring;
        this.id_pengerjaan = id_pengerjaan;
        this.deskripsi = deskripsi;
        this.gambar_proses = gambar_proses;
        this.tanggal_selesai = tanggal_selesai;
        this.status_pengerjaan = status_pengerjaan;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getId_monitoring() {
        return id_monitoring;
    }
    public void setId_monitoring(String id_monitoring) {
        this.id_monitoring = id_monitoring;
    }

    public String getId_pengerjaan() {
        return id_pengerjaan;
    }
    public void setId_pengerjaan(String id_pengerjaan) {
        this.id_pengerjaan = id_pengerjaan;
    }

    public String getDeskripsi() {
        return deskripsi;
    }
    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getGambar_proses() {
        return gambar_proses;
    }
    public void setGambar_proses(String gambar_proses) {
        this.gambar_proses = gambar_proses;
    }

    public String getTanggal_selesai() {
        return tanggal_selesai;
    }
    public void setTanggal_selesai(String tanggal_selesai) {
        this.tanggal_selesai = tanggal_selesai;
    }

    public String getStatus_pengerjaan() {
        return status_pengerjaan;
    }
    public void setStatus_pengerjaan(String status_pengerjaan) {
        this.status_pengerjaan = status_pengerjaan;
    }
}
