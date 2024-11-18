package com.example.tangkelek.model;

public class UserModel {
    public String id_user;
    public String nama;
    public String nohp;
    public String username;
    public String password;
    public String role;
    public String gambar;
    public UserModel(String id_user, String nama, String nohp, String username, String password, String role, String gambar) {
        this.id_user = id_user;
        this.nama = nama;
        this.nohp = nohp;
        this.username = username;
        this.password = password;
        this.role = role;
        this.gambar = gambar;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

}
