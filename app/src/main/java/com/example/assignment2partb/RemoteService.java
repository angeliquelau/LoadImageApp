package com.example.assignment2partb;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RemoteService {
    public static RemoteService rs = null;
    private Activity uiAct;

    public RemoteService(Activity uiAct)
    {
        this.uiAct = uiAct;
    }

    public void setUiAct(Activity uiAct)
    {
        this.uiAct = uiAct;
    }

    public static RemoteService getInstance(Activity uiAct)
    {
        //create new instance of RemoteService if its null
        if(rs == null)
        {
            rs = new RemoteService(uiAct);
        }
        rs.setUiAct(uiAct); //set ui activity
        return rs;
    }

    public HttpURLConnection openConn(String urlStr)
    {
        Log.d("remoteService: ", "url: " + urlStr);
        HttpURLConnection httpConn = null;
        try
        {
            URL url = new URL(urlStr);
            httpConn = (HttpURLConnection) url.openConnection();
        }
        //must have these two catch
        catch (MalformedURLException e) { //for URL
            e.printStackTrace();
        } catch (IOException e) { //for url.openConnection()
            e.printStackTrace();
        }
        //if httpConn is null, then it means it failed to open connection because of internet connection
        if(httpConn == null)
        {
            uiAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(uiAct, "Please check your Internet connection", Toast.LENGTH_LONG).show();
                }
            });
        }

        return httpConn;
    }

    public boolean checkConnection(HttpURLConnection httpConn)
    {
        boolean connBool = false;
        try
        {
            if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                connBool = true;
            }
        } catch (IOException e) { //for httpConn.getResponseCode()
            e.printStackTrace();
            uiAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(uiAct, "Oh no. There is a problem with API endpoint", Toast.LENGTH_LONG).show();
                }
            });
        }
        return connBool;
    }

    public String getResponseStr(HttpURLConnection httpConn)
    {
        String dataStr = null;
        //transfer bytes (the data) from the server
        try
        {
            InputStream is = httpConn.getInputStream();
            byte[] byteData = IOUtils.toByteArray(is);
            dataStr = new String(byteData, StandardCharsets.UTF_8);
        } catch (IOException e) { //for httpConn.getInputStream()
            e.printStackTrace();
        }

        return dataStr;
    }
}
