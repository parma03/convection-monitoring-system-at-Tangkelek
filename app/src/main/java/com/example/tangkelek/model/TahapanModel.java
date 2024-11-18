package com.example.tangkelek.model;

public class TahapanModel {
    public String id_tahap;
    public String nama_tahap;
    public TahapanModel(String id_tahap, String nama_tahap) {
        this.id_tahap = id_tahap;
        this.nama_tahap = nama_tahap;
    }

    public String getId_tahap() {
        return id_tahap;
    }

    public void setId_tahap(String id_tahap) {
        this.id_tahap = id_tahap;
    }

    public String getNama_tahap() {
        return nama_tahap;
    }
    public void setNama_tahap(String nama_tahap) {
        this.nama_tahap = nama_tahap;
    }


}
