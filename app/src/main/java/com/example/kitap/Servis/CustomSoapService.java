package com.example.kitap.Servis;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manga on 23.01.2018.
 */
public class
CustomSoapService {
    private SoapObject request;
    private Integer timeOut;
    private String actionName;
    String name;
    SoapObject result = null;

    public CustomSoapService (String name, String actionName, Integer timeOut ) {
        setRequest(new SoapObject(BusinessPacked.NAMESPACE, name));
        this.timeOut = timeOut * 1000;
        this.actionName = actionName;
        this.name=name;
    }

    public List<Object> SoapPost() {
        List<Object> sonuc = new ArrayList<>();

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(this.request);
        envelope.dotNet = true;
        //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.1.24", 8888));

            HttpTransportSE httpTransport = new HttpTransportSE(BusinessPacked.URL, timeOut);
            SoapObject result = null;

         int intPropertyCount;
        try {
            httpTransport.call(BusinessPacked.SOAP_ACTION + actionName , envelope);

            try {

               // if(name.equals("kitapYazarlari"))
                    if(name.equals("kitapKaydet") || name.equals("kitapSil"))
               result = (SoapObject) envelope.bodyIn;
                else
                    result = (SoapObject) envelope.getResponse();
            } catch (Exception ex) {
                sonuc.add(false);
                sonuc.add("Bir hata oluştu. " + ex.getMessage());
                sonuc.add(null);
                return sonuc;
            }
            sonuc.add(true);
            sonuc.add("İşlem başarıyla gerçekleşti");
            sonuc.add(result);
            return sonuc;
        } catch (Exception ex) {
            sonuc.add(false);
            sonuc.add("Bir hata oluştu. " + ex.getMessage());
            sonuc.add(null);
            return sonuc;
        }

    }

    public SoapObject getRequest() {
        return request;
    }

    public void setRequest(SoapObject request) {
        this.request = request;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
