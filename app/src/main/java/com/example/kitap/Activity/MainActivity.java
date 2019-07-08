package com.example.kitap.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.example.kitap.*;
import com.example.kitap.Class.Kitap;
import com.example.kitap.Class.Yazar;
import com.example.kitap.Servis.BusinessPacked;
import com.example.kitap.Servis.CustomSoapService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView Image;
    int hatakodu, kitapId, kitapPos;
    public final static int USING_CAMERA = 11;
    public final static int SELECT_PHOTO = 12345;
    private static final int IMAGE_ACTION_CODE = 102;
    String mImageFileLocation, kontrol;
    private int imageviewCount = 0;
    private int imageId = 0;
    private boolean tabletSize;
    private LinearLayout imageLayout;
    Button Btnyazar, Btnkitapliste, BtnKaydet;
    EditText EtAd, EtYayimYili, EtFiyat;
    TextView yazarlar;
    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    ArrayList<Yazar> yazarList = new ArrayList<>();
    ArrayList<Yazar> kitapYazarList = new ArrayList<>();
    ArrayList<Kitap> kitapList = new ArrayList<>();
    ArrayList<String> yazarlarListesi = new ArrayList<>();
    ArrayList<String> kitaplarListesi = new ArrayList<>();
    String Ad, YayimYili, Fiyat;
    LinearLayout imageLayoutDetay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Image = (ImageView) findViewById(R.id.ivimage);
        Btnyazar = (Button) findViewById(R.id.Btn_listele_yazar);
        BtnKaydet = (Button) findViewById(R.id.Btn_kaydet);
        Btnkitapliste = (Button) findViewById(R.id.Btn_listele);
        EtAd = (EditText) findViewById(R.id.Et_ad);
        EtYayimYili = (EditText) findViewById(R.id.Et_yayim_yili);
        EtFiyat = (EditText) findViewById(R.id.Et_fiyat);
        yazarlar = (TextView) findViewById(R.id.Txt_yazar);
        imageLayout = (LinearLayout) findViewById(R.id.image);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 120);
        }

        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    kontrol = "";
                    selectImage();
            }
        });

        kontrol = "yazarKitapListele";
        WebServiceAsyncTask task = new WebServiceAsyncTask(MainActivity.this);
        task.execute();

        Btnkitapliste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogKitap(MainActivity.this);
            }
        });

        BtnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Ad = EtAd.getText().toString();
                YayimYili = EtYayimYili.getText().toString();
                Fiyat = EtFiyat.getText().toString();
                kontrol = "kitapKaydet";
                WebServiceAsyncTask task = new WebServiceAsyncTask(MainActivity.this);
                task.execute();
                EtAd.setText("");
                EtFiyat.setText("");
                EtYayimYili.setText("");
                imageLayout.removeAllViews();
                yazarlar.setText("");
            }
        });

        Btnyazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle(R.string.dialog_title);
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!mUserItems.contains((position))) {
                                mUserItems.add(position);
                            }
                        } else if (mUserItems.contains(position)) {
                            mUserItems.remove(mUserItems.indexOf(position));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++) {
                            item = item + listItems[mUserItems.get(i)];
                            if (i != mUserItems.size() - 1) {
                                item = item + ",";
                            }
                        }
                        yazarlar.setText(item);
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mUserItems.clear();
                            yazarlar.setText("");
                        }
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    private void selectImage() {

        try {
            final CharSequence[] items = {"Resim Çek", "Kütüphaneden Seç", "İptal"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            builder.setTitle("Resim Ekle");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Resim Çek")) {

                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, USING_CAMERA);
                        }
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            try {
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            startActivityForResult(takePictureIntent, USING_CAMERA);
                        } else {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            }
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_ACTION_CODE);
                        }

                    } else if (items[item].equals("Kütüphaneden Seç")) {
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, SELECT_PHOTO);
                    } else if (items[item].equals("İptal")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
            new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                    .setTitle("HATA")
                    .setIcon(R.drawable.cancel)
                    .setMessage(e.getMessage())
                    .setPositiveButton("Tamam", null)
                    .show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mImageFileLocation = "file:" + image.getAbsolutePath();
        return image;
    }

    // kamera ile işlem yapıp tamam dedikten imagesonra çalışacak kısım
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            ImageView im = null;
            String selectedImagePath = null;
            if (resultCode != RESULT_OK) return;

            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case SELECT_PHOTO://kütüphaneden resim seçme
                        if (resultCode == Activity.RESULT_OK) {
                            Uri uri = data.getData();
                            String[] projection = {MediaStore.Images.Media.DATA};

                            Cursor cursor = MainActivity.this.getContentResolver().query(uri, projection, null, null, null);
                            cursor.moveToFirst();
                            int columnIndeks = cursor.getColumnIndex(projection[0]);
                            String filePath = cursor.getString(columnIndeks);
                            cursor.close();
                            selectedImagePath = filePath;
                            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                            Drawable d = new BitmapDrawable(yourSelectedImage);
                            im = new ImageView(MainActivity.this);
                            im.setImageDrawable(d);
                        }
                        break;

                    case USING_CAMERA:// kamera kullanarak resim seçme

                        Uri imageUri = Uri.parse(mImageFileLocation);
                        File file = new File(imageUri.getPath());

                        MediaScannerConnection.scanFile(MainActivity.this,
                                new String[]{imageUri.getPath()}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                    }
                                });
                        selectedImagePath = ImageHelper.getRealPathFromURI(MainActivity.this, imageUri);
                        Bitmap yourSelectedImage = BitmapFactory.decodeFile(file.toString());

                        im = new ImageView(MainActivity.this);
                        im.setImageBitmap(yourSelectedImage);
                        break;
                }
                if (im != null) {
                    im.setTag(selectedImagePath);
                    setImageLinearLayout(im);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                    .setTitle("HATA")
                    .setIcon(R.drawable.cancel)
                    .setMessage(e.getMessage())
                    .setPositiveButton("Tamam", null)
                    .show();

        }
    }

    private void setImageLinearLayout(ImageView im) throws Exception {
        // resim kısmının bulunduğu linearleyout

        im.setDrawingCacheEnabled(true);
        im.buildDrawingCache();
        imageviewCount++;
        int dp;
        DimensionHelper.init(this);
        if (tabletSize) {
            dp = (int) (100 * DimensionHelper.getDP());
        } else {
            dp = (int) (100 * DimensionHelper.getDP());
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp, dp);
        im.setPadding(2, 2, 2, 2);
        im.setScaleType(ImageView.ScaleType.FIT_XY);
        im.setLayoutParams(params);
        String idImage = String.valueOf(imageId);
        Resources res = getResources();
        im.setId(res.getIdentifier(idImage, "id", MainActivity.this.getPackageName()));
        imageId++;
        if (kontrol.equals("kitapYazarlari"))
            imageLayoutDetay.addView(im);
        else
            imageLayout.addView(im);
        final ImageView finalIm = im;

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImage(finalIm);
            }
        });
    }

    private void showImage(ImageView iv) {
        //kullanıcı resmin üzerine tıkladığında çalışan metod
        try {
            final int idImage = iv.getId();
            ImageView img2 = new ImageView(this);
            img2.setImageDrawable(iv.getDrawable());
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            builder.setTitle("Bu resmi silmek istediğinizden emin misiniz?")
                    .setCancelable(false)
                    .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ImageView delete = (ImageView) findViewById(idImage);
                            imageLayout.removeView(delete);
                        }
                    })
                    .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.setView(img2);
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                    .setTitle("HATA")
                    .setIcon(R.drawable.cancel)
                    .setMessage(e.getMessage())
                    .setPositiveButton("Tamam", null)
                    .show();
        }
    }

    public void showDialogKitap(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_listview);

        Button btndialog = (Button) dialog.findViewById(R.id.btndialog);
        btndialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ListView listView = (ListView) dialog.findViewById(R.id.listview);
        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, R.layout.list_item, R.id.tv, kitaplarListesi);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                kitapId = kitapList.get(position).getId();
                kitapPos = position;
                dialog.dismiss();
                kontrol = "kitapYazarlari";
                WebServiceAsyncTask task = new WebServiceAsyncTask(MainActivity.this);
                task.execute();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
                kitapId = kitapList.get(pos).getId();
                kitapPos = pos;
                dialog.dismiss();
                new android.app.AlertDialog.Builder(MainActivity.this).setIcon(android.R.drawable.ic_delete).setTitle("Sil")
                        .setMessage("Silmek İstediğinize Emin Misiniz?")
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                kontrol = "kitapSil";
                                WebServiceAsyncTask task = new WebServiceAsyncTask(MainActivity.this);
                                task.execute();
                            }
                        }).setNegativeButton("Hayır", null).show();

                return true;
            }
        });
        dialog.show();
    }

    public void showDialogKitapDetay(Activity activity) throws Exception {

        final Dialog dialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_listview_detay);

        Button btndialog = (Button) dialog.findViewById(R.id.btndialog);
        final TextView TxtAd = (TextView) dialog.findViewById(R.id.Txt_ad);
        final TextView TxtYayimYili = (TextView) dialog.findViewById(R.id.Txt_yayim_yili);
        final TextView TxtFiyat = (TextView) dialog.findViewById(R.id.Txt_fiyat);
        final TextView Txtyazar = (TextView) dialog.findViewById(R.id.Txt_yazar);
        final LinearLayout detayLayout = (LinearLayout) dialog.findViewById(R.id.detayLayout);
        imageLayoutDetay = (LinearLayout) dialog.findViewById(R.id.detayImage);

        detayLayout.clearAnimation();
        TxtAd.setText(kitapList.get(kitapPos).getAd());
        TxtYayimYili.setText(kitapList.get(kitapPos).getYayimYili());
        TxtFiyat.setText(kitapList.get(kitapPos).getFiyat().toString());
        String a = "";
        for (int i = 0; i < kitapYazarList.size(); i++) {
            a += kitapYazarList.get(i).getAd() + " " + kitapYazarList.get(i).getSoyad() + ",";
        }
        Txtyazar.setText(a);

        ImageView im;
        byte[] byteImage = ImageHelper.hexStringToByteArray(kitapList.get(kitapPos).getResim());
        Bitmap bitMap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
        Bitmap yourSelectedImage = bitMap;
        Drawable d = new BitmapDrawable(yourSelectedImage);
        im = new ImageView(MainActivity.this);
        im.setImageDrawable(d);
        setImageLinearLayout(im);

        btndialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detayLayout.clearAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public class WebServiceAsyncTask extends AsyncTask<String, String, String> {

        private Context context;
        private ProgressDialog progressDialog;

        public WebServiceAsyncTask(Context context) {
            super();
            this.context = context;
        }

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "Lütfen bekleyin...", "İşlem yürütülüyor...", true);
        }

        SoapObject result = null;

        @Override

        protected String doInBackground(String... params) {
            try {
                if (kontrol.equals("yazarKitapListele")) {
                    CustomSoapService service = new CustomSoapService("yazarListele", "yazarListele", 120);

                    List<Object> servisSonuc = service.SoapPost();
                    Boolean servisSonucKodu = (Boolean) servisSonuc.get(0);
                    String servisSonucAciklama = (String) servisSonuc.get(1);

                    if (!servisSonucKodu) {
                        Parametreler.hataAciklama = servisSonucAciklama;
                        hatakodu = 1;
                        return servisSonucAciklama;
                    }
                    result = (SoapObject) servisSonuc.get(2);
                    yazarList = new ArrayList<>();
                    yazarlarListesi = new ArrayList<>();
                    kitapYazarList = new ArrayList<>();
                    for (int i = 0; i < result.getPropertyCount(); i++) {
                        Yazar yazar = new Yazar();
                        SoapObject soapEmployee = (SoapObject) result.getProperty(i);
                        if (soapEmployee.hasProperty("Id")) {
                            yazar.setId(Integer.parseInt(soapEmployee.getPropertyAsString("Id")));
                        }
                        if (soapEmployee.hasProperty("Ad")) {
                            yazar.setAd(soapEmployee.getPropertyAsString("Ad"));
                        }
                        if (soapEmployee.hasProperty("Soyad")) {
                            yazar.setSoyad(soapEmployee.getPropertyAsString("Soyad"));
                        }
                        yazarList.add(yazar);
                    }

                    service = new CustomSoapService("kitapListele", "kitapListele", 120);

                    servisSonuc = service.SoapPost();
                    servisSonucKodu = (Boolean) servisSonuc.get(0);
                    servisSonucAciklama = (String) servisSonuc.get(1);

                    if (!servisSonucKodu) {
                        Parametreler.hataAciklama = servisSonucAciklama;
                        hatakodu = 1;
                        return servisSonucAciklama;
                    }
                    kitapList = new ArrayList<>();
                    kitaplarListesi = new ArrayList<>();
                    result = (SoapObject) servisSonuc.get(2);
                    for (int i = 0; i < result.getPropertyCount(); i++) {
                        Kitap kitap = new Kitap();
                        SoapObject soapEmployee = (SoapObject) result.getProperty(i);
                        if (soapEmployee.hasProperty("Id")) {
                            kitap.setId(Integer.parseInt(soapEmployee.getPropertyAsString("Id")));
                        }
                        if (soapEmployee.hasProperty("Ad")) {
                            kitap.setAd(soapEmployee.getPropertyAsString("Ad"));
                        }
                        if (soapEmployee.hasProperty("Resim")) {
                            kitap.setResim(soapEmployee.getPropertyAsString("Resim"));
                        }
                        if (soapEmployee.hasProperty("YayimYili")) {
                            kitap.setYayimYili(soapEmployee.getPropertyAsString("YayimYili"));
                        }
                        if (soapEmployee.hasProperty("Fiyat")) {
                            kitap.setFiyat(Float.parseFloat(soapEmployee.getPropertyAsString("Fiyat")));
                        }
                        kitapList.add(kitap);
                    }

                } else if (kontrol.equals("kitapYazarlari")) {
                    CustomSoapService service = new CustomSoapService("kitapYazarlari", "kitapYazarlari", 120);
                    service.getRequest().addProperty("kitapId", kitapId);

                    kitapYazarList = new ArrayList<>();
                    SoapObject result = null;
                    List<Object> servisSonuc = service.SoapPost();
                    Boolean servisSonucKodu = (Boolean) servisSonuc.get(0);
                    String servisSonucAciklama = (String) servisSonuc.get(1);

                    if (!servisSonucKodu) {
                        Parametreler.hataAciklama = servisSonucAciklama;
                        hatakodu = 1;
                        return servisSonucAciklama;
                    }
                    result = (SoapObject) servisSonuc.get(2);
                    for (int i = 0; i < result.getPropertyCount(); i++) {
                        Yazar yazar = new Yazar();
                        SoapObject soapEmployee = (SoapObject) result.getProperty(i);
                        if (soapEmployee.hasProperty("Id")) {
                            yazar.setId(Integer.parseInt(soapEmployee.getPropertyAsString("Id")));
                        }
                        if (soapEmployee.hasProperty("Ad")) {
                            yazar.setAd(soapEmployee.getPropertyAsString("Ad"));
                        }
                        if (soapEmployee.hasProperty("Soyad")) {
                            yazar.setSoyad(soapEmployee.getPropertyAsString("Soyad"));
                        }
                        kitapYazarList.add(yazar);
                    }
                } else if (kontrol.equals("kitapKaydet")) {
                    CustomSoapService service = new CustomSoapService("kitapKaydet", "kitapKaydet", 120);
                    String resim = "";
                    service.getRequest().addProperty("ad", "" + Ad);
                    service.getRequest().addProperty("yayimyili", "" + YayimYili);
                    service.getRequest().addProperty("fiyat", "" + Fiyat);

                    if (imageviewCount != 0) {
                        for (int i = 0; i < imageviewCount + 1; i++) {
                            ImageView imageView = (ImageView) findViewById(i);
                            if (imageView != null) {
                                @SuppressLint("WrongThread") byte[] byteArray = ImageHelper.getBitmapBytes(((BitmapDrawable) imageView.getDrawable()).getBitmap(), Bitmap.CompressFormat.JPEG);
                                resim += ImageHelper.bytesToHex(byteArray);
                            }
                        }
                    }
                    service.getRequest().addPropertyIfValue("resim", resim);
                    String yazarlar = "";
                    for (int i = 0; i < mUserItems.size(); i++) {
                        yazarlar += mUserItems.get(i) + 1 + ",";
                    }
                    service.getRequest().addPropertyIfValue("yazarlar", yazarlar);
                    SoapObject result = null;
                    List<Object> servisSonuc = service.SoapPost();

                    Boolean servisSonucKodu = (Boolean) servisSonuc.get(0);
                    String servisSonucAciklama = (String) servisSonuc.get(1);
                    if (!servisSonucKodu) {
                        Parametreler.hataAciklama = servisSonucAciklama;
                        hatakodu = 1;
                        return servisSonucAciklama;
                    }
                    result = (SoapObject) servisSonuc.get(2);
                } else if (kontrol.equals("kitapSil")) {
                    CustomSoapService service = new CustomSoapService("kitapSil", "kitapSil", 120);
                    service.getRequest().addProperty("kitapId", kitapId);

                    SoapObject result = null;
                    List<Object> servisSonuc = service.SoapPost();
                    Boolean servisSonucKodu = (Boolean) servisSonuc.get(0);
                    String servisSonucAciklama = (String) servisSonuc.get(1);

                    if (!servisSonucKodu) {
                        Parametreler.hataAciklama = servisSonucAciklama;
                        hatakodu = 1;
                        return servisSonucAciklama;
                    }
                    result = (SoapObject) servisSonuc.get(2);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                hatakodu = 1;
                new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom)
                        .setTitle("HATA")
                        .setIcon(R.drawable.cancel)
                        .setMessage(ex.getMessage())
                        .setPositiveButton("Tamam", null)
                        .show();
            }
            return "";
        }

        protected void onPostExecute(String result) {
            progressDialog.cancel();
            if (hatakodu == 0) {
                if (kontrol.equals("yazarKaydet") || kontrol.equals("yazarSil")) {
                    BusinessPacked.alertDialog(MainActivity.this, "İşlem Tamam", "İşlem başarılı şekilde gerçekleştirildi.", "Tamam").show();
                }
                if (kontrol.equals("yazarKitapListele")) {
                    //list array convert
                    for (int i = 0; i < yazarList.size(); i++) {
                        String a = yazarList.get(i).getAd() + " " + yazarList.get(i).getSoyad();
                        yazarlarListesi.add(a);
                    }
                    listItems = new String[yazarlarListesi.size()];
                    for (int i = 0; i < yazarlarListesi.size(); i++) {
                        listItems[i] = yazarlarListesi.get(i);
                    }
                    checkedItems = new boolean[listItems.length];
                    for (int i = 0; i < kitapList.size(); i++) {
                        kitaplarListesi.add(kitapList.get(i).getAd());
                    }
                } else if (kontrol.equals("kitapYazarlari")) {
                    try {
                        showDialogKitapDetay(MainActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (kontrol.equals("kitapSil") || kontrol.equals("kitapKaydet")) {
                    try {
                        kontrol = "yazarKitapListele";
                        WebServiceAsyncTask task = new WebServiceAsyncTask(MainActivity.this);
                        task.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (hatakodu == 2) {
                BusinessPacked.alertDialog(MainActivity.this, "Hata", "Sistem Bağlantı Problemi." + Parametreler.hataAciklama, "Tamam").show();
            } else if (hatakodu == 3) {
                BusinessPacked.alertDialog(MainActivity.this, "Hata", "Sistemden gelen bilgiler eksik.", "Tamam").show();
            } else {
                BusinessPacked.alertDialog(MainActivity.this, "Hata", "HATA-3: " + Parametreler.hataAciklama + " KOD : " + Parametreler.hataKodu, "Tamam").show();
            }
        }
    }
}
