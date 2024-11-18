package com.example.tangkelek.model;

public class KonveksiModel1 {
    public String id_konveksi;
    public String nama_produk;
    public String jenis;
    public String jumlah;
    public String status;
    public String ukuran_s;
    public String ukuran_m;
    public String ukuran_l;
    public String ukuran_xl;
    public String ukuran_xxl;
    public String ukuran_xxxl;
    public String ukuran_5xl;
    public String tanggal;
    public String tanggal_pengambilan;
    public String tgl_selesai;
    public String gambar;


    public KonveksiModel1(String id_konveksi, String nama_produk, String jenis, String jumlah, String status, String tanggal, String tanggal_pengambilan, String gambar, String ukuran_s, String ukuran_m, String ukuran_l, String ukuran_xl, String ukuran_xxl, String ukuran_xxxl, String ukuran_5xl, String tgl_selesai) {
        this.id_konveksi = id_konveksi;
        this.nama_produk = nama_produk;
        this.jenis = jenis;
        this.ukuran_s = ukuran_s;
        this.ukuran_m = ukuran_m;
        this.ukuran_l = ukuran_l;
        this.ukuran_xl = ukuran_xl;
        this.ukuran_xxl = ukuran_xxl;
        this.ukuran_xxxl = ukuran_xxxl;
        this.ukuran_5xl = ukuran_5xl;
        this.jumlah = jumlah;
        this.status = status;
        this.tanggal = tanggal;
        this.tanggal_pengambilan = tanggal_pengambilan;
        this.tgl_selesai = tgl_selesai;
        this.gambar = gambar;
    }

    public String getId_konveksi() {
        return id_konveksi;
    }
    public void setId_konveksi(String id_konveksi) {
        this.id_konveksi = id_konveksi;
    }

    public String getNama_produk() {
        return nama_produk;
    }
    public void setNama_produk(String nama_produk) {
        this.nama_produk = nama_produk;
    }

    public String getJenis() {
        return jenis;
    }
    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getUkuran_s() {
        return ukuran_s;
    }
    public void setUkuran_s(String ukuran_s) {
        this.ukuran_s = ukuran_s;
    }

    public String getUkuran_m() {
        return ukuran_m;
    }
    public void setUkuran_m(String ukuran_m) {
        this.ukuran_m = ukuran_m;
    }

    public String getUkuran_l() {
        return ukuran_l;
    }
    public void setUkuran_l(String ukuran_l) {
        this.ukuran_l = ukuran_l;
    }

    public String getUkuran_xl() {
        return ukuran_xl;
    }
    public void setUkuran_xl(String ukuran_xl) {
        this.ukuran_xl = ukuran_xl;
    }

    public String getUkuran_xxl() {
        return ukuran_xxl;
    }
    public void setUkuran_xxl(String ukuran_xxl) {
        this.ukuran_xxl = ukuran_xxl;
    }

    public String getUkuran_xxxl() {
        return ukuran_xxxl;
    }
    public void setUkuran_xxxl(String ukuran_xxxl) {
        this.ukuran_xxxl = ukuran_xxxl;
    }

    public String getUkuran_5xl() {
        return ukuran_5xl;
    }
    public void setUkuran_5xl(String ukuran_5xl) {
        this.ukuran_5xl = ukuran_5xl;
    }

    public String getJumlah() {
        return jumlah;
    }
    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getTanggal() {
        return tanggal;
    }
    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getTanggal_pengambilan() {
        return tanggal_pengambilan;
    }
    public void setTanggal_pengambilan(String tanggal_pengambilan) {
        this.tanggal_pengambilan = tanggal_pengambilan;
    }

    public String getTgl_selesai() {
        return tgl_selesai;
    }
    public void setTgl_selesai(String tgl_selesai) {
        this.tgl_selesai = tgl_selesai;
    }

    public String getGambar() {
        return gambar;
    }
    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
