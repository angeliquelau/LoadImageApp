package com.example.assignment2partb;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ImageData implements Parcelable {
    private ArrayList<Image> imageURLs;
    private ArrayList<ImageBitmap> imageBitmaps;

    public ImageData()
    {
        imageURLs = new ArrayList<>();
        imageBitmaps = new ArrayList<>();
    }

    protected ImageData(Parcel in) {
        imageURLs = in.createTypedArrayList(Image.CREATOR);
        imageBitmaps = in.createTypedArrayList(ImageBitmap.CREATOR);
    }

    public static final Creator<ImageData> CREATOR = new Creator<ImageData>() {
        @Override
        public ImageData createFromParcel(Parcel in) {
            return new ImageData(in);
        }

        @Override
        public ImageData[] newArray(int size) {
            return new ImageData[size];
        }
    };

    public ArrayList<Image> getImageURLs() {
        return imageURLs;
    }
    public ArrayList<ImageBitmap> getImageBitmaps() { return imageBitmaps; }

    public void setImageURL(JSONObject jFoundItems) throws JSONException {
        imageURLs.add(new Image(jFoundItems.getString("largeImageURL")));
    }

    public void setImageBitmaps(Bitmap data)
    {
        imageBitmaps.add(new ImageBitmap(data));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(imageURLs);
        parcel.writeTypedList(imageBitmaps);
    }
}
