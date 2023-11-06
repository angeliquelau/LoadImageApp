package com.example.assignment2partb;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import java.net.HttpURLConnection;
import java.util.concurrent.Callable;

public class SearchImageTask implements Callable<String> {
    private String searchStr;
    private String baseURL;
    private RemoteService rs;
    private Activity uiAct;

    public SearchImageTask(Activity uiAct)
    {
        this.searchStr = null;
        baseURL = "https://pixabay.com/api/";
        rs = RemoteService.getInstance(uiAct);
        this.uiAct = uiAct;
    }

    @Override
    public String call() throws Exception {
        String response = null;
        String endpoint = getSearchEndpoint();
        HttpURLConnection httpConn = rs.openConn(endpoint);
        if(httpConn != null)
        {
            if(rs.checkConnection(httpConn) == true) //if connection is okay
            {
                response = rs.getResponseStr(httpConn);
                httpConn.disconnect();
                try {
                    Thread.sleep(3000);
                }
                catch (Exception e){

                }
            }
        }
        return response;
    }

    private String getSearchEndpoint() {
        Uri.Builder url = Uri.parse(this.baseURL).buildUpon();
        url.appendQueryParameter("key", "30589596-25be3b9051b2cf9d0c5f02584");
        url.appendQueryParameter("q", this.searchStr);
        String urlStr = url.build().toString();
        Log.d("searchImageTask: ", "endpoint: " + urlStr);
        return urlStr;
    }

    public void setSearchStr(String searchStr) { this.searchStr = searchStr; }
}
