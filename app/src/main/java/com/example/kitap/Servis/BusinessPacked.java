package com.example.kitap.Servis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;


/**
 * Created by Manga on 23.01.2018.
 */
public class BusinessPacked {

    public static String NAMESPACE = "http://tempuri.org/";
    public static String URL = "http://192.168.1.48/KitapServis.asmx";
    //public static String URL = "http://192.168.1.2:8080/ekysServices/servisler?wsdl";

    public static String SOAP_ACTION = NAMESPACE ;
    public static String VERSION="v 1.05";

    public static String kullaniciKodu;
    public static String kullaniciSifre;
    public static String hata;
    public static String kurumId;

    public static String ABONELIK_DURUMU_AKTIF = "170";
    public static String EV_SAHIBI_KIRACI_EV_SAHIBI = "144";

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String objectToString (Object object) {
        if (object == null)
            return "";
        else
            return object.toString();
    }


    public static AlertDialog.Builder alertDialog(Context context, String title, String message, String buttonName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(buttonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return alertDialog;

    }

    public static AlertDialog.Builder selectPicker(Context context, final TextView textView, String title, int minValue, int maxValue, int selectedValue) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        final NumberPicker picker = new NumberPicker(context);
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        try {
            picker.setValue(Integer.valueOf(textView.getText().toString()));
        } catch(Exception e) {
            picker.setValue(selectedValue);
        }
        picker.setBackgroundColor(Color.parseColor("#000000"));


        final FrameLayout parent = new FrameLayout(context);
        parent.addView(picker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        alertDialog.setView(parent);

        alertDialog.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(String.valueOf(picker.getValue()));
            }
        });
        alertDialog.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setNeutralButton("Temizle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(null);
            }
        });
        return alertDialog;

    }

    public static AlertDialog.Builder alertDialogTwoButtons(Context context, String title, String message,
                                                            String buttonPositiveName, String buttonNegativeName, DialogInterface.OnClickListener postiveListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(buttonPositiveName, postiveListener);
        alertDialog.setNegativeButton(buttonNegativeName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return alertDialog;

    }
}
