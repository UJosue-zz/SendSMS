/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sendsms;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author HP240
 */
public class WebService {
    private static WebService instance;
    public static WebService getInstance(){
        if(instance==null){
            instance = new WebService();
        }
        return instance;
    }
    public String URL_DEVOP = "http://smspruebas-95213.app.xervo.io/";
    public String URL_BASE = "http://paquetes-93539.onmodulus.net/";
    public String URL_SMS = URL_BASE + "/api/v1/sms/send/";

    
    public String get(String url) {
        String source = null;

        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            source = EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return source;
    }

    public String post(String url, NameValuePair... parametros) {
        String source = null;
        HttpClient httpClient = HttpClients.createDefault();
        RequestBuilder requestBuilder = RequestBuilder.post().setUri(url);
        for (NameValuePair parametro : parametros) {
            requestBuilder.addParameter(parametro);
        }
        HttpUriRequest uriRequest = requestBuilder.build();
        try {
            HttpResponse httpResponse = httpClient.execute(uriRequest);
            source = EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return source;
    }
}
