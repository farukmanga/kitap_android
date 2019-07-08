package com.example.kitap.Class;

public class Kitap {
    int Id;
    String Ad,YayimYili;
    String Resim;
    Float fiyat;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getAd() {
        return Ad;
    }

    public void setAd(String ad) {
        Ad = ad;
    }

    public String getYayimYili() {
        return YayimYili;
    }

    public void setYayimYili(String yayimYili) {
        YayimYili = yayimYili;
    }

    public String getResim() {
        return Resim;
    }

    public void setResim(String resim) {
        Resim = resim;
    }

    public Float getFiyat() {
        return fiyat;
    }

    public void setFiyat(Float fiyat) {
        this.fiyat = fiyat;
    }
}
