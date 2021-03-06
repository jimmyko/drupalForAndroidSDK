package com.druCloud.drupalforandroidsdk;

import android.net.Uri;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

/**
 * Created by jimmyko on 10/13/13.
 */
public class DrupalServicesBase {

    private String baseURI = "";
    private String endpoint = "rest";
    protected String resource = "";
    private DrupalAuth auth;

    private List<NameValuePair> pairsToSend = new ArrayList<NameValuePair>();

    public DrupalServicesBase(String baseURI, String endpoint) {
        this.baseURI = baseURI;
        this.endpoint = endpoint;
    }

    public void setAuth(DrupalAuth auth) {
        this.auth = auth;
        auth.initAuth(baseURI, endpoint);
    }

    public void setResource (String resource) {
        this.resource = resource;
    }

    protected String getURI() {
        return this.baseURI + "/" + this.endpoint + "/" + this.resource;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String httpGetRequest (String uri) {
        HttpGet request = new HttpGet(uri);
        return httpSendRequest(request);
    }

    // Only GET request contains query parameters.
    public String httpGetRequest (String uri, BasicNameValuePair[] params)
    {
        for (int i = 0; i < params.length; i++) {
            pairsToSend.add(params[i]);
        }

        Uri.Builder uriBuilder = Uri.parse(uri).buildUpon();
        for (NameValuePair param : pairsToSend) {
            uriBuilder.appendQueryParameter(param.getName(), param.getValue());
        }
        uri = uriBuilder.build().toString();
        HttpGet request = new HttpGet(uri);
        return httpSendRequest(request);
    }

    public String httpPostRequest (String uri, BasicNameValuePair[] params) {
        HttpPost request = new HttpPost(uri);

        for (int i = 0; i < params.length; i++) {
            pairsToSend.add(params[i]);
        }

        // assign parameters to request
        try {
            request.setEntity(new UrlEncodedFormEntity(pairsToSend));
            System.out.println("the request is: " + request.getURI() + EntityUtils.toString(request.getEntity()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return httpSendRequest(request);
    }

    public String httpDeleteRequest (String uri)
    {
        HttpDelete request = new HttpDelete(uri);
        return httpSendRequest(request);
    }

    public String httpPutRequest (String uri, BasicNameValuePair[] params)
    {
        HttpPut request = new HttpPut(uri);

        for (int i = 0; i < params.length; i++) {
            pairsToSend.add(params[i]);
        }

        // assign parameters to request
        try {
            request.setEntity(new UrlEncodedFormEntity(pairsToSend));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return httpSendRequest(request);
    }

    private <T extends HttpRequestBase> String httpSendRequest (T request)
    {
        this.auth.initRequest(request);

        // set header
        request.setHeader("Accept", "application/json");
        request.setHeader("content-type", "application/x-www-form-urlencoded");

        // send the request
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(request);
            // if successful, return the response body
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    String responseBody = EntityUtils.toString(resEntity);
                    // debug
                    System.out.println("The response is:" + responseBody);
                    return responseBody;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}

/* debug code
            //get request headers
            Header[] headers2 = request.getAllHeaders();
            for (Header header : headers2) {
                System.out.println(header.getName()
                        + ": " + header.getValue());
            }

            System.out.println("fuck!! not 200 " + response.getStatusLine().getReasonPhrase() + " " + response.getStatusLine().getStatusCode());
            System.out.println("fuck!! not 200 Called URL" + request.getURI());
 */