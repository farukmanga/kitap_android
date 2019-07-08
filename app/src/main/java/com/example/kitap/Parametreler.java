package com.example.kitap;

import java.util.Date;

/**
 * Created by Manga on 24.01.2018.
 */

public class Parametreler {

    public static Integer hataKodu;
    public static String hataAciklama;
    public static String version;


    public static String token;


    public static int isEmriTipiId,isEmriId;


    public static int getIsEmriTipiId() {
        return isEmriTipiId;
    }

    public static void setIsEmriTipiId(int isEmriTipiId) {
        Parametreler.isEmriTipiId = isEmriTipiId;
    }

    public static int getIsEmriId() {
        return isEmriId;
    }

    public static void setIsEmriId(int isEmriId) {
        Parametreler.isEmriId = isEmriId;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Parametreler.token = token;
    }
}
