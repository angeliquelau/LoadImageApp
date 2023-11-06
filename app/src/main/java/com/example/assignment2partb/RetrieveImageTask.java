package com.example.assignment2partb;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RetrieveImageTask implements Callable<ImageData> {
    private Activity uiAct;
    private String data;
    private RemoteService rs;
    private ImageData images = new ImageData();

    public RetrieveImageTask(Activity uiAct)
    {
        rs = RemoteService.getInstance(uiAct);
        this.uiAct = uiAct;
        this.data = null;
    }

    @Override
    public ImageData call() throws Exception {
        //ImageData images = new ImageData();
        ImageData endpoint = getEndpoint(this.data);
        if(endpoint == null)
        {
            uiAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(uiAct, "No image found", Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            images = getFromImageURL(endpoint);
            Log.d("retrieveImageTask: ", "images size: " + images.getImageURLs().size());
            try
            {
                Thread.sleep(3000);
            } catch (Exception e) {
            }
        }
        return images;
    }

    private ImageData getEndpoint(String data) {
        //ImageData imageURL = new ImageData();
        try {
            Log.d("RetrieveImageTask: ", "data: " + data);
            JSONObject jBase = new JSONObject(data);
            JSONArray jFound = jBase.getJSONArray("hits");
            Log.d("RetrieveImageTask: ", "jFound length: " + jFound.length());
            if(jFound.length() > 0 && jFound.length() < 15) //if less than 15, just get however many images there are
            {

                for (int i = 0; i < jFound.length(); i++) {
                    Log.d("RetrieveImageTask: ", "jFound: " + jFound.getJSONObject(i).getString("largeImageURL"));
                    JSONObject jFoundItems = jFound.getJSONObject(i);
                    images.setImageURL(jFoundItems);
                }
            }
            else if(jFound.length() >= 15) //if there is more than 15, then get only 15
            {
                for (int i = 0; i < 15; i++) {
                    Log.d("RetrieveImageTask: ", "jFound: " + jFound.getJSONObject(i).getString("largeImageURL") + " i: " + i);
                    JSONObject jFoundItems = jFound.getJSONObject(i);
                    images.setImageURL(jFoundItems);
                }
            }

            Log.d("retrieveImageTask: ", "imageURL size: " + images.getImageURLs().size());
        } catch (JSONException e) { //for json
            e.printStackTrace();
        }
        return images;
    }

    private ImageData getFromImageURL(ImageData imageURL) {
        //ImageData images = new ImageData();
        List<Uri.Builder> urls = new ArrayList<>();
        List<String> urlStrs = new ArrayList<>();
        for (int i = 0; i < imageURL.getImageURLs().size(); i++) {
            urls.add(Uri.parse(imageURL.getImageURLs().get(i).getImageURL()).buildUpon());
            Log.d("retrieveImageTask: ", "imageUrl: " + imageURL.getImageURLs().get(i).getImageURL());
            urlStrs.add(urls.get(i).build().toString());
            Log.d("retrieveImageTask: ", "urlStrs: " + urlStrs.get(i) + " size: " + urlStrs.size());
            HttpURLConnection httpConn = rs.openConn(urlStrs.get(i));
            if(httpConn != null)
            {
                Log.d("retrieveImageTask: ", "Connection: " + rs.checkConnection(httpConn));
                if(rs.checkConnection(httpConn) == true)
                {
                    images.setImageBitmaps(getBitmap(httpConn));
                    httpConn.disconnect();
                }
                Log.d("retrieveImageTask: ", "imageBitmaps size: " + images.getImageBitmaps().size());
            }

        }

        return images;
    }

    public Bitmap getBitmap(HttpURLConnection httpConn) {
        Bitmap data = null;
        try {
            InputStream is = httpConn.getInputStream();
            byte[] bytes = getByteArr(is);
            data = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private byte[] getByteArr(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int readNum;
        byte[] data = new byte[4096];
        while((readNum = is.read(data, 0, data.length)) != -1)
        {
            baos.write(data, 0, readNum);
        }
        return baos.toByteArray();
    }

    public void setData(String data) { this.data = data; }
}
